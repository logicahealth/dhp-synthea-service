package com.digitalservices.dhp.dhpsyntheaservice.controller;

import com.digitalservices.dhp.dhpsyntheaservice.client.EhrClient;
import com.digitalservices.dhp.dhpsyntheaservice.data.ProcessRepository;
import com.digitalservices.dhp.dhpsyntheaservice.data.Processes;
import com.digitalservices.dhp.dhpsyntheaservice.domain.PatientFile;
import com.digitalservices.dhp.dhpsyntheaservice.domain.VistaOhcResponse;
import com.digitalservices.dhp.dhpsyntheaservice.util.FileManager;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@RestController
@CrossOrigin(origins = "*.*")
@RequestMapping("synthea")
public class SyntheaController {

    private static final String USER_DIR_COOKIE = "USER_DIR_COOKIE";

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
    public String create(@RequestParam String population, HttpServletRequest request, HttpServletResponse response) {
        //String userDir = handleCookie(request, response);
        String userDir = request.getSession(true).getId();
        Iterable<Processes> processes = processRepository.findAll();
        if (processes.iterator().hasNext()) {
            return "process is already running";
        }
        try {
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put("population", population);
            jobDataMap.put("userDir", userDir);
            scheduler.triggerJob(syntheaJobDetail.getKey(), jobDataMap);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return "Greetings from Spring Boot!";
    }

    private String handleCookie(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(true);
        String sessionId = session.getId();
        Cookie cookie = findCookie(request.getCookies());
        if (cookie == null){
            cookie = new Cookie(SyntheaController.USER_DIR_COOKIE, session.getId());
        } else {
            sessionId = cookie.getValue();
        }

        response.addCookie(cookie);
        return sessionId;
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
        Cookie[] cookies = request.getCookies();

        return fileManager.getAllPatientFiles(assembleUrl(request), request.getSession().getId());

    }

    @RequestMapping(value = "/processPatientFiles", method = RequestMethod.GET)
    public ResponseEntity<VistaOhcResponse> processPatientFiles(@RequestParam(value = "fileName", required = false) String
                                                                        fileName, HttpSession session) {
        VistaOhcResponse response = new VistaOhcResponse();
        if (fileName != null) {
            try {
                response = ehrClient.sendOneToVista(session.getId(), fileName);
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {

            try {
                ehrClient.sendAllToVista(session.getId());
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @RequestMapping(value = "/patient", method = RequestMethod.GET)
    public ResponseEntity<String> getPatient(@RequestParam("fileName") String fileName, HttpSession session) {
        String file = null;
        try {
            file = fileManager.getStringFromFile(session.getId(), fileName);
        } catch (IOException e) {
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(file, HttpStatus.OK);

    }

    protected String assembleUrl(HttpServletRequest request) {
        System.out.println(request.getRequestURI());
        System.out.println(request.getRequestURL());
        String baseURl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        baseURl = baseURl + "/synthea/patient?fileName=";
        return baseURl;
    }

    private Cookie findCookie(Cookie[] cookies){
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("cookieName")) {
                    return cookie;
                }
            }
        }
        return null;
    }
}
