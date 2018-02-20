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
