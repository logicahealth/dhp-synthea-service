package com.digitalservices.dhp.dhpsyntheaservice.domain;

import java.util.Objects;

/**
 * Represents the success/failure of the vitals loaded into Vista
 */
public class VitalsStatus {
    private String loaded;

    private String errors;

    private String status;

    public String getLoaded() {
        return loaded;
    }

    public void setLoaded(String loaded) {
        this.loaded = loaded;
    }

    public String getErrors() {
        return errors;
    }

    public void setErrors(String errors) {
        this.errors = errors;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "VitalsStatus{" +
                "loaded='" + loaded + '\'' +
                ", errors='" + errors + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VitalsStatus that = (VitalsStatus) o;
        return Objects.equals(loaded, that.loaded) &&
                Objects.equals(errors, that.errors) &&
                Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {

        return Objects.hash(loaded, errors, status);
    }
}
