/*
        Licensed to the Apache Software Foundation (ASF) under one
        or more contributor license agreements.  See the NOTICE file
        distributed with this work for additional information
        regarding copyright ownership.  The ASF licenses this file
        to you under the Apache License, Version 2.0 (the
        "License"); you may not use this file except in compliance
        with the License.  You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

        Unless required by applicable law or agreed to in writing,
        software distributed under the License is distributed on an
        "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
        KIND, either express or implied.  See the License for the
        specific language governing permissions and limitations
        under the License.
*/
package com.digitalservices.dhp.dhpsyntheaservice.jobs;

import com.digitalservices.dhp.dhpsyntheaservice.data.Process;
import com.digitalservices.dhp.dhpsyntheaservice.data.ProcessRepository;

import com.digitalservices.dhp.dhpsyntheaservice.util.FileManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
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
import java.nio.file.StandardCopyOption;
import java.util.Comparator;

/**
 *
 */
@DisallowConcurrentExecution
public class SyntheaJob extends QuartzJobBean {
    private static final Logger LOG = LogManager.getLogger(SyntheaJob.class);

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
            Process processes = new Process();
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
            LOG.error(e);
        } finally {
            processRepository.deleteAll();
        }
        LOG.info("building population " + population);
    }

    private void deleteFiles(Path path) {

        LOG.info("deleted directory " + syntheaOutputFhir);
        if (Files.exists(path)) {
            try {
                Files.walk(path).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach
                        (File::delete);
            } catch (IOException e) {
                LOG.error(e);
            }
        }
    }

    private void moveFiles() {
        Path path = Paths.get(syntheaOutputFhir);
        Path newPath = Paths.get(syntheaOutput);
        deleteFiles(newPath);
        try {
            LOG.info("copying from directory " + syntheaOutputFhir);
            LOG.info("copying to  directory " + syntheaOutput);
            Files.move(path, newPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            LOG.error(e);
        }
    }

    public void setPopulation(String population) {
        this.population = population;
    }
}
