package com.digitalservices.dhp.dhpsyntheaservice.jobs;

import com.digitalservices.dhp.dhpsyntheaservice.data.ProcessRepository;
import com.digitalservices.dhp.dhpsyntheaservice.data.Processes;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;


public class SyntheaJob extends QuartzJobBean {
    private String command = "/bin/sh";
    private String arg1 = "run_synthea";
    private String arg2 = "-p";
    @Value("${synthea.root}")
    private String dir;
    @Value("${synthea.root.output}")
    private String outputDir;
    private String population;

    @Autowired
    private ProcessRepository processRepository;
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        deleteFiles();
        File file = new File(dir);
        ProcessBuilder processBuilder = new ProcessBuilder(command, arg1, arg2, population).inheritIO()
                .directory(file);

        try {
            Processes processes = new Processes();
            processes.setRunning(true);
            processRepository.save(processes);
            Process process = processBuilder.start();

            process.waitFor();
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
    private void deleteFiles(){
        Path path = Paths.get(outputDir);
        System.out.println("deleted directory " + outputDir);
        if (Files.exists(path)) {
            try {
                Files.walk(path).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach
                        (File::delete);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void setPopulation(String population) {
        this.population = population;
    }
}
