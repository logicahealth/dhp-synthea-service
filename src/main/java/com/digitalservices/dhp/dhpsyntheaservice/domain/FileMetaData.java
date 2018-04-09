package com.digitalservices.dhp.dhpsyntheaservice.domain;

import java.util.Objects;

/**
 * Represents the metadata for display and retrieval.
 */
public class FileMetaData {

    private String patientName;
    private String fileName;

    public FileMetaData() {
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }


    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        return "FileMetaData{" +
                "patientName='" + patientName + '\'' +
                ", fileName='" + fileName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileMetaData that = (FileMetaData) o;
        return Objects.equals(patientName, that.patientName) &&
                Objects.equals(fileName, that.fileName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(patientName, fileName);
    }
}
