package com.digitalservices.dhp.dhpsyntheaservice.util;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import com.digitalservices.dhp.dhpsyntheaservice.client.EhrClient;
import com.digitalservices.dhp.dhpsyntheaservice.domain.FileMetaData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Enumerations;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Condition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

@Component
public class FileManager {
    private static final Logger LOG = LogManager.getLogger(FileManager.class);

    @Value("${synthea.root.output}")
    private String syntheaOutput;
    @Value("${synthea.root.output.fhir}")
    private String syntheaOutputFhir;

    public File getFileByName(String fileName) {
        File file = new File(syntheaOutputFhir + "/" + fileName);
        return file;
    }

    public String getStringFromFile(String fileName) throws IOException {
        String contents = new String(Files.readAllBytes(Paths.get(syntheaOutputFhir + "/" + fileName)));
        return contents;
    }

    public List<FileMetaData> getAllPatientFiles(String baseURl) {
        List<FileMetaData> fileMetaDataList = new ArrayList<>();

        Path path = FileSystems.getDefault().getPath(syntheaOutputFhir);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path, "*.{json}")) {
            for (Path entry : stream) {
                File file = entry.toFile();
                Patient patient = bundleFileToPatient(file);
                if (patient.isEmpty()) {
                    continue;
                }
                FileMetaData fileMetaData = new FileMetaData();
                Set<String> conditions = bundleFileToConditions(file);
                fileMetaData.setFileName(file.getName());
                String family = patient.getName().get(0).getFamily();
                String given = patient.getName().get(0).getGiven().get(0).toString();

                fileMetaData.setPatientName(given + " " + family);
                fileMetaData.setProblems(conditions);

                fileMetaDataList.add(fileMetaData);
            }
        } catch (DirectoryIteratorException ex) {
            LOG.error(ex);
        } catch (IOException e) {
            LOG.error(e);
        }
        return fileMetaDataList;

    }

    private Bundle fileToBundle(File file) {
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(file);
        } catch (FileNotFoundException e) {
            LOG.error(e);
            return new Bundle();
        }
        FhirContext fhirContext = FhirContext.forDstu3();
        IParser parser = fhirContext.newJsonParser();
        Bundle bundle = parser.parseResource(Bundle.class, fileReader);
        return bundle;
    }

    private Patient bundleFileToPatient(File file) {

        Bundle bundle = fileToBundle(file);
        List<Bundle.BundleEntryComponent> entries = bundle.getEntry();
        for (Bundle.BundleEntryComponent entry : entries) {
            if (Enumerations.FHIRAllTypes.PATIENT.getDisplay().equals(entry.getResource().fhirType())) {
                Patient patient = (Patient) entry.getResource();
                if (patient.hasDeceased()) {
                    return new Patient();
                }
                return patient;
            }
        }
        return new Patient();
    }

    private Set<String> bundleFileToConditions(File file) {

        Bundle bundle = fileToBundle(file);
        Set<String> returnList =  new HashSet<String>();
        List<Bundle.BundleEntryComponent> entries = bundle.getEntry();
        for (Bundle.BundleEntryComponent entry : entries) {
            if (Enumerations.FHIRAllTypes.CONDITION.getDisplay().equals(entry.getResource().fhirType())) {
                Condition condition = (Condition) entry.getResource();
                if (condition.getCode().getText() == null || condition.getCode().getText().isEmpty()) {
                    continue;
                }
                // Ensure problem isn't in list already
                returnList.add((String) condition.getCode().getText());
            }
        }
        return returnList;
    }

}
