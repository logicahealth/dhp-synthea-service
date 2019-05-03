/* Created by Perspecta http://www.perspecta.com */
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
import java.util.List;

/**
 *
 */
@DisallowConcurrentExecution
public class SyntheaJob extends QuartzJobBean {
    private static final Logger LOG = LogManager.getLogger(SyntheaJob.class);

    @Value("${synthea.root}")
    private String synthea;
    @Value("${synthea.root.output}")
    private String syntheaOutput;
    @Value("${synthea.root.output.fhir}")
    private String syntheaOutputFhir;

    private List<String> command;

    @Autowired
    private ProcessRepository processRepository;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        Process processes = new Process();
        processes.setRunning(true);
        // processes.setClient(userDir);
        processRepository.save(processes);
        File file = new File(synthea);
        ProcessBuilder processBuilder = new ProcessBuilder(command).inheritIO().directory(file);
        Path path = Paths.get(syntheaOutputFhir);
        deleteFiles(path);
        try {
            java.lang.Process process = processBuilder.start();

            process.waitFor();
            // moveFiles();
            processRepository.deleteAll();
        } catch (IOException e) {
            LOG.error(e);
            throw new JobExecutionException(e);
        } catch (InterruptedException e) {
            LOG.error(e);
            throw new JobExecutionException(e);
        } finally {
            processRepository.deleteAll();
        }
        LOG.info("building patients with command " + command);
    }

    private void deleteFiles(Path path) {

        LOG.info("deleted directory " + syntheaOutputFhir);
        if (Files.exists(path)) {
            try {
                Files.walk(path).sorted(Comparator.reverseOrder()).filter(Files::isRegularFile).map(Path::toFile).forEach(File::delete);
            } catch (IOException e) {
                LOG.error(e);
            }
        }
    }

    public void setCommand(List<String> command) {
        this.command = command;
    }
    
}
