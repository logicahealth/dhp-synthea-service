package com.digitalservices.dhp.dhpsyntheaservice.util;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import com.digitalservices.dhp.dhpsyntheaservice.domain.PatientFile;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Enumerations;
import org.hl7.fhir.dstu3.model.Patient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class FileManager {
    @Value("${synthea.root.output.fhir}")
    private String syntheaPath;

    public File getFileByName(String fileName) {
        File file = new File(syntheaPath + "/" + fileName);
        return file;
    }

    public String getStringFromFile(String fileName) throws IOException {
        String contents = new String(Files.readAllBytes(Paths.get(syntheaPath + "/" + fileName)));

        return contents;
    }

    public Bundle getPatient(String fileName) {
        File file = getFileByName(fileName);
        return fileToBundle(file);
    }

    public List<PatientFile> getAllPatientFiles(String baseURl) {
        List<PatientFile> patientFiles = new ArrayList<>();

        System.out.println("path=========" + syntheaPath);
        Path path = FileSystems.getDefault().getPath(syntheaPath);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path, "*.{json}")) {
            for (Path entry : stream) {
                File file = entry.toFile();
                Patient patient = bundleFileToPatient(file);
                if (patient.isEmpty()) {
                    continue;
                }
                PatientFile patientFile = new PatientFile();
                patientFile.setFileName(file.getName());
                String family = patient.getName().get(0).getFamily().toString();
                String given = patient.getName().get(0).getGiven().get(0).toString();
                patientFile.setPatientName(given + " " + family);

                patientFile.setUrl(baseURl + file.getName());
                patientFiles.add(patientFile);
            }
        } catch (DirectoryIteratorException ex) {
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return patientFiles;

    }

    private Bundle fileToBundle(File file) {
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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
                if (patient.hasDeceased()){
                    return new Patient();
                }
                return patient;

            }
        }


        return new Patient();
    }
}
