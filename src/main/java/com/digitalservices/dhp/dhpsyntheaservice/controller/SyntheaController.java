package com.digitalservices.dhp.dhpsyntheaservice.controller;

import com.digitalservices.dhp.dhpsyntheaservice.client.EhrClient;
import com.digitalservices.dhp.dhpsyntheaservice.data.Process;
import com.digitalservices.dhp.dhpsyntheaservice.data.ProcessRepository;
import com.digitalservices.dhp.dhpsyntheaservice.domain.FileMetaData;
import com.digitalservices.dhp.dhpsyntheaservice.domain.SyntheaResponse;
import com.digitalservices.dhp.dhpsyntheaservice.domain.VistaOhcResponse;
import com.digitalservices.dhp.dhpsyntheaservice.util.FileManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    private static final Logger LOG = LogManager.getLogger(EhrClient.class);
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
     * Kicks off the Synthea Quartz job.  The job then creates patients and saves the files to the files
     * system.
     *
     * @param population the number of patient files synthea should create
     * @return A Json object that tells the caller that the process has started
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
                LOG.error("Error running quartz job", e);
                responseEntity = new ResponseEntity<SyntheaResponse>(HttpStatus.INTERNAL_SERVER_ERROR);
                return responseEntity;

            }
        }
        responseEntity = new ResponseEntity<SyntheaResponse>(syntheaResponse, HttpStatus.OK);
        return responseEntity;
    }


    /**
     * Called by the caller to check on the progress of the synthea job that was created.
     *
     * @return a Json object that tells the caller if the job is running
     */
    @RequestMapping(value = "/synthea-progress", method = RequestMethod.GET)
    public Process syntheaProgress() {
        Process process = new Process();
        Iterable<Process> processes = processRepository.findAll();
        if (processes.iterator().hasNext()) {
            return processes.iterator().next();
        } else {
            return new Process();
        }
    }

    /**
     * Returns information about the files created by Synthea. So that he individual files can be
     * retrieved by the caller.
     *
     * @param request the HttpServletRequest
     * @return a list of metadata about the files
     */
    @RequestMapping(value = "/patient-files", method = RequestMethod.GET)
    public List<FileMetaData> getPatientFiles(HttpServletRequest request) {
        return fileManager.getAllPatientFiles(assembleUrl(request));
    }

    /**
     * Loads the file from the file system and sends it to Vista.
     *
     * @param fileName the name of the file to send to Vista
     * @return a Json object with success or failure
     */
    @RequestMapping(value = "/vista-export", method = RequestMethod.POST)
    public ResponseEntity<VistaOhcResponse> vistaExport(@RequestParam(value = "fileName") String
                                                                fileName) {
        VistaOhcResponse response = new VistaOhcResponse();
        if (fileName != null) {
            try {
                response = ehrClient.sendOneToVista(fileName);
            } catch (Exception e) {
                LOG.error("Error calling Vista", e);
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    /**
     * Retrieves the patient FHIR bundle and returns it to the caller
     *
     * @param fileName the file name of the bundle
     * @return the FHIR bundle
     */
    @RequestMapping(value = "/patient", method = RequestMethod.GET)
    public ResponseEntity<String> getPatient(@RequestParam("fileName") String fileName) {
        String file = null;
        try {
            file = fileManager.getStringFromFile(fileName);
        } catch (IOException e) {
            LOG.error("Error getting file from file system.", e);
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<String>(file, HttpStatus.OK);

    }

    protected String assembleUrl(HttpServletRequest request) {
        String baseURl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        baseURl = baseURl + "/synthea/patient?fileName=";
        return baseURl;
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
