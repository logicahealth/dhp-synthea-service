package com.digitalservices.dhp.dhpsyntheaservice.domain;

import java.util.Objects;

public class VistaOhcResponse {
    private String ICN;
    private boolean vistaSuccess;
    private String error;

    public String getICN() {
        return ICN;
    }

    public void setICN(String ICN) {
        this.ICN = ICN;
    }

    public boolean isVistaSuccess() {
        return vistaSuccess;
    }

    public void setVistaSuccess(boolean vistaSuccess) {
        this.vistaSuccess = vistaSuccess;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "VistaOhcResponse{" +
                "ICN='" + ICN + '\'' +
                ", vistaSuccess=" + vistaSuccess +
                ", error='" + error + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VistaOhcResponse that = (VistaOhcResponse) o;
        return vistaSuccess == that.vistaSuccess &&
                Objects.equals(ICN, that.ICN) &&
                Objects.equals(error, that.error);
    }

    @Override
    public int hashCode() {

        return Objects.hash(ICN, vistaSuccess, error);
    }
}
