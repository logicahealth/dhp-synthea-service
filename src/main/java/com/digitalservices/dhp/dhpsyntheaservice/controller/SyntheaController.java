package com.digitalservices.dhp.dhpsyntheaservice.controller;

import com.digitalservices.dhp.dhpsyntheaservice.client.EhrClient;
import com.digitalservices.dhp.dhpsyntheaservice.data.ProcessRepository;
import com.digitalservices.dhp.dhpsyntheaservice.data.Processes;
import com.digitalservices.dhp.dhpsyntheaservice.domain.PatientFile;
import com.digitalservices.dhp.dhpsyntheaservice.util.FileManager;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("synthea")
public class SyntheaController {

    @Autowired
    private ProcessRepository processRepository;

    @Autowired
    private Scheduler scheduler;

    @Autowired
    private JobDetail syntheaJobDetail;

    @Autowired
    private FileManager fileManager;

    @Autowired
    private EhrClient ehrClient;

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String create(@RequestParam String population) {
        Iterable<Processes> processes = processRepository.findAll();
        if (processes.iterator().hasNext()) {
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
    public Processes checkProcess() {
        Iterable<Processes> processes = processRepository.findAll();
        if (processes.iterator().hasNext()) {
            return processes.iterator().next();
        } else {
            return new Processes();
        }
    }

    @RequestMapping(value = "/patientFiles", method = RequestMethod.GET)
    public List<PatientFile> getPatientFiles(HttpServletRequest request) {
        List<PatientFile> patientFiles = new ArrayList<PatientFile>();

        return fileManager.getAllPatientFiles(assembleUrl(request));

    }

    @RequestMapping(value = "/processPatientFiles", method = RequestMethod.GET)
    public ResponseEntity<String> processPatientFiles(@RequestParam(value = "fileName", required = false) String fileName) {

        if (fileName != null) {
            try {
                ehrClient.sendOneToVista(fileName);
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {

            try {
                ehrClient.sendAllToVista();
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @RequestMapping(value = "/patient", method = RequestMethod.GET)
    public ResponseEntity<String> getPatient(@RequestParam("fileName") String fileName) {
        String file = null;
        try {
            file = fileManager.getStringFromFile(fileName);
        } catch (IOException e) {
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(file, HttpStatus.OK);

    }

    protected String assembleUrl(HttpServletRequest request){
        String baseURl = request.getScheme() + "://"+  request.getServerName() + ":"+ request.getServerPort();
        baseURl = baseURl + "/synthea/patient?fileName=";
                return baseURl;
    }


}
