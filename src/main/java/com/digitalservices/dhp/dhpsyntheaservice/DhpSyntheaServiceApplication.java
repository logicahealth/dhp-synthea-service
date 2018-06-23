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
package com.digitalservices.dhp.dhpsyntheaservice;

import com.digitalservices.dhp.dhpsyntheaservice.jobs.SyntheaJob;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@SpringBootApplication
public class DhpSyntheaServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DhpSyntheaServiceApplication.class, args);
    }

    @Bean
    public JobDetail SyntheaJobDetail() {
        return JobBuilder.newJob(SyntheaJob.class).withIdentity("SyntheaJob").usingJobData("population", "1")
                .storeDurably().build();
    }

    @Bean
    public Trigger sampleJobTrigger() {
        Instant instant = LocalDateTime.now().plusYears(10).atZone(ZoneId.systemDefault()).toInstant();
        return TriggerBuilder.newTrigger().forJob(SyntheaJobDetail())
                .withIdentity("SyntheaJob").startAt(Date.from(instant)).build();
    }
}
