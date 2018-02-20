package com.digitalservices.dhp.dhpsyntheaservice.controller;

import com.digitalservices.dhp.dhpsyntheaservice.data.ProcessRepository;
import com.digitalservices.dhp.dhpsyntheaservice.data.Processes;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("synthea")
public class SyntheaController {

    @Autowired
    private ProcessRepository processRepository;

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private JobDetail syntheaJobDetail;

    @RequestMapping("/create")
    public String create(@RequestParam String population) {
        Iterable<Processes> processes = processRepository.findAll();
        if (processes.iterator().hasNext()){
            return "process is already running";
        }
        try {
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put("population", population);
            scheduler.triggerJob(syntheaJobDetail.getKey(), jobDataMap);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return "Greetings from Spring Boot!";
    }

    @RequestMapping("/checkProcess")
    public Processes checkProcess(){
        Iterable<Processes> processes = processRepository.findAll();
        if (processes.iterator().hasNext()){
            return processes.iterator().next();
        }else {
            return new Processes();
        }
    }
}
