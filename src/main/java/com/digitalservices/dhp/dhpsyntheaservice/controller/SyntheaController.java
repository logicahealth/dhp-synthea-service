package com.digitalservices.dhp.dhpsyntheaservice.controller;

import com.digitalservices.dhp.dhpsyntheaservice.client.EhrClient;
import com.digitalservices.dhp.dhpsyntheaservice.data.Process;
import com.digitalservices.dhp.dhpsyntheaservice.data.ProcessRepository;
import com.digitalservices.dhp.dhpsyntheaservice.domain.FileMetaData;
import com.digitalservices.dhp.dhpsyntheaservice.domain.ProcessType;
import com.digitalservices.dhp.dhpsyntheaservice.domain.SyntheaResponse;
import com.digitalservices.dhp.dhpsyntheaservice.domain.VistaOhcResponse;
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

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;


@RestController
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

    /**
     * This method kicks off the Synthea Quartz job.
     *
     * @param population the number of patient files synthea should create
     * @return
     */
    @RequestMapping(value = "/synthea-run", method = RequestMethod.GET)
    public ResponseEntity<SyntheaResponse> syntheaRun(@RequestParam String population) {
        //String userDir = handleCookie(request, response);
        ResponseEntity<SyntheaResponse> responseEntity;
        SyntheaResponse syntheaResponse = new SyntheaResponse(false);
        Iterable<Process> processes = processRepository.findAll();
        if (!processes.iterator().hasNext()) {
            try {
                JobDataMap jobDataMap = new JobDataMap();
                jobDataMap.put("population", population);
                scheduler.triggerJob(syntheaJobDetail.getKey(), jobDataMap);
                syntheaResponse = new SyntheaResponse(true);
            } catch (SchedulerException e) {
                e.printStackTrace();
                responseEntity = new ResponseEntity<SyntheaResponse>(HttpStatus.INTERNAL_SERVER_ERROR);
                return responseEntity;

            }
        }
        responseEntity = new ResponseEntity<SyntheaResponse>(syntheaResponse, HttpStatus.OK);
        return responseEntity;
    }

    private String handleCookie(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(true);
        String sessionId = session.getId();
        Cookie cookie = findCookie(request.getCookies());
        if (cookie == null) {
            cookie = new Cookie(SyntheaController.USER_DIR_COOKIE, session.getId());
        } else {
            sessionId = cookie.getValue();
        }

        response.addCookie(cookie);
        return sessionId;
    }

    /**
     * @return
     */
    @RequestMapping(value = "/synthea-progress", method = RequestMethod.GET)
    public Process syntheaProgress() {
        Process process = new Process(ProcessType.SYNTHEA);
        Iterable<Process> processes = processRepository.findAll();
        if (processes.iterator().hasNext()) {
            return processes.iterator().next();
        } else {
            return new Process();
        }
    }

    /**
     * @param request
     * @return
     */
    @RequestMapping(value = "/patient-files", method = RequestMethod.GET)
    public List<FileMetaData> getPatientFiles(HttpServletRequest request) {
        return fileManager.getAllPatientFiles(assembleUrl(request));
    }

    /**
     * @param fileName
     * @return
     */
    @RequestMapping(value = "/vista-export", method = RequestMethod.POST)
    public ResponseEntity<VistaOhcResponse> vistaExport(@RequestParam(value = "fileName") String
                                                                fileName) {
        VistaOhcResponse response = new VistaOhcResponse();
        if (fileName != null) {
            try {
                response = ehrClient.sendOneToVista(fileName);
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * @param icn
     * @return
     */
    @RequestMapping(value = "/ohc-export", method = RequestMethod.POST)
    public ResponseEntity<VistaOhcResponse> ohcExport(@RequestParam(value = "icn") String icn) {
        VistaOhcResponse response = new VistaOhcResponse();
        if (icn != null) {
            try {
                response = ehrClient.ohcClient(icn);
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * @param fileName
     * @return
     */
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

    protected String assembleUrl(HttpServletRequest request) {
        String baseURl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        baseURl = baseURl + "/synthea/patient?fileName=";
        return baseURl;
    }

    private Cookie findCookie(Cookie[] cookies) {
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
