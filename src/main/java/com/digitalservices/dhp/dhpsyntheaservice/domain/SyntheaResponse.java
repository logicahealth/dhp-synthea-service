package com.digitalservices.dhp.dhpsyntheaservice.domain;

import java.util.Objects;

/**
 * Response to the call to start the Synthea quarts job. Set to true when the process is started.
 */
public class SyntheaResponse {
    private boolean processStarted;

    public SyntheaResponse(boolean processStarted) {
        this.processStarted = processStarted;
    }

    public boolean isProcessStarted() {
        return processStarted;
    }

    @Override
    public String toString() {
        return "SyntheaResponse{" +
                "processStarted=" + processStarted +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SyntheaResponse that = (SyntheaResponse) o;
        return processStarted == that.processStarted;
    }

    @Override
    public int hashCode() {

        return Objects.hash(processStarted);
    }
}
