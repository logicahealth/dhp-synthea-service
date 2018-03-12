package com.digitalservices.dhp.dhpsyntheaservice.domain;

public class SyntheaResponse {
    private boolean processStarted;

    public SyntheaResponse (boolean processStarted){
        this.processStarted = processStarted;
    }

    public boolean isProcessStarted() {
        return processStarted;
    }

}
