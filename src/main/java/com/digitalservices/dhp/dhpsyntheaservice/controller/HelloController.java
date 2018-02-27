package com.digitalservices.dhp.dhpsyntheaservice.controller;

import org.javasimon.SimonManager;
import org.javasimon.Split;
import org.javasimon.Stopwatch;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * Simple example web service
 *
 * @author truxall
 */
@Controller
@RequestMapping("/hello")
public class HelloController {
    public static final String HELLO_MESSAGE = "Bazinga!";

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.HEAD}, produces = {"text/plain"})
    @ResponseBody
    public String getHello() {

        Stopwatch stopwatch = SimonManager.getStopwatch("service-hello");
        Split split = stopwatch.start();

        try {
            return HelloController.HELLO_MESSAGE;
        } finally {
            split.stop();
        }
    }
}
