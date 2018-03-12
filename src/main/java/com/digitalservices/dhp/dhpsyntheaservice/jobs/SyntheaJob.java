package com.digitalservices.dhp.dhpsyntheaservice.jobs;

import com.digitalservices.dhp.dhpsyntheaservice.data.ProcessRepository;
import com.digitalservices.dhp.dhpsyntheaservice.data.Process;
import com.digitalservices.dhp.dhpsyntheaservice.domain.ProcessType;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Comparator;

@DisallowConcurrentExecution
public class SyntheaJob extends QuartzJobBean {
    @Value("${synthea.shell}")
    private String command;
    @Value("${synthea.arg1}")
    private String arg1 = "run_synthea";
    @Value("${synthea.arg2}")
    private String arg2 = "-p";
    @Value("${synthea.root}")
    private String synthea;
    @Value("${synthea.root.output}")
    private String syntheaOutput;
    @Value("${synthea.root.output.fhir}")
    private String syntheaOutputFhir;


    private String population;

    @Autowired
    private ProcessRepository processRepository;
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {

        File file = new File(synthea);
        ProcessBuilder processBuilder = new ProcessBuilder(command, arg1, arg2, population).inheritIO()
                .directory(file);
        Path path = Paths.get(syntheaOutputFhir);
        deleteFiles(path);
        try {
            Process processes = new Process(ProcessType.SYNTHEA);
            processes.setRunning(true);
            //processes.setClient(userDir);
            processRepository.save(processes);
            java.lang.Process process = processBuilder.start();

            process.waitFor();
            //moveFiles();
            processRepository.deleteAll();
        } catch (IOException e) {
            throw new JobExecutionException(e);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            processRepository.deleteAll();
        }
        System.out.println("building population " + population);
    }
    private void deleteFiles(Path path){

        System.out.println("deleted directory " + syntheaOutputFhir);
        if (Files.exists(path)) {
            try {
                Files.walk(path).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach
                        (File::delete);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void moveFiles(){
        Path path = Paths.get(syntheaOutputFhir);
        Path newPath = Paths.get(syntheaOutput );
        deleteFiles(newPath);
        try {
            System.out.println("copying from directory " + syntheaOutputFhir);
            System.out.println("copying to  directory " + syntheaOutput );
            Files.move(path, newPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void setPopulation(String population) {
        this.population = population;
    }

   // public void setUserDir(String userDir) {
      //  this.userDir = userDir;
  //  }
}
