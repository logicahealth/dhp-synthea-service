package com.digitalservices.dhp.dhpsyntheaservice.data;

import com.digitalservices.dhp.dhpsyntheaservice.domain.ProcessType;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Process {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private boolean isRunning;
    private String client;
    private ProcessType processType;

    public Process() {

    }

    public Process(Long id) {
        this.id = id;
    }

    public Process(ProcessType processType) {
        this.processType = processType;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public ProcessType getProcessType() {
        return processType;
    }

    public void setProcessType(ProcessType processType) {
        this.processType = processType;
    }
}
