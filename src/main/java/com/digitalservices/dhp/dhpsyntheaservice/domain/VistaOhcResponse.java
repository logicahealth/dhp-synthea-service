package com.digitalservices.dhp.dhpsyntheaservice.domain;

public class VistaOhcResponse {
    private String ICN;
    private boolean vistaSuccess;
    private boolean ohcSuccess;
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

    public boolean isOhcSuccess() {
        return ohcSuccess;
    }

    public void setOhcSuccess(boolean ohcSuccess) {
        this.ohcSuccess = ohcSuccess;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
