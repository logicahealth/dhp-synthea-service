/*
        Licensed to the Apache Software Foundation (ASF) under one
        or more contributor license agreements.  See the NOTICE file
        distributed with this work for additional information
        regarding copyright ownership.  The ASF licenses this file
        to you under the Apache License, Version 2.0 (the
        "License"); you may not use this file except in compliance
        with the License.  You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

        Unless required by applicable law or agreed to in writing,
        software distributed under the License is distributed on an
        "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
        KIND, either express or implied.  See the License for the
        specific language governing permissions and limitations
        under the License.
*/
package com.digitalservices.dhp.dhpsyntheaservice.domain;

import java.util.Objects;

/**
 * The response from Vista ingestion of the FHIR bundle
 */
public class VistaResponse {
    private String id;

    private VitalsStatus vitalsStatus;

    private String status;

    private String loadStatus;

    private String icn;

    private String ien;

    private String dfn;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public VitalsStatus getVitalsStatus() {
        return vitalsStatus;
    }

    public void setVitalsStatus(VitalsStatus vitalsStatus) {
        this.vitalsStatus = vitalsStatus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLoadStatus() {
        return loadStatus;
    }

    public void setLoadStatus(String loadStatus) {
        this.loadStatus = loadStatus;
    }

    public String getIcn() {
        return icn;
    }

    public void setIcn(String icn) {
        this.icn = icn;
    }

    public String getIen() {
        return ien;
    }

    public void setIen(String ien) {
        this.ien = ien;
    }

    public String getDfn() {
        return dfn;
    }

    public void setDfn(String dfn) {
        this.dfn = dfn;
    }

    @Override
    public String toString() {
        return "VistaResponse{" +
                "id='" + id + '\'' +
                ", vitalsStatus=" + vitalsStatus +
                ", status='" + status + '\'' +
                ", loadStatus='" + loadStatus + '\'' +
                ", icn='" + icn + '\'' +
                ", ien='" + ien + '\'' +
                ", dfn='" + dfn + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VistaResponse that = (VistaResponse) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(vitalsStatus, that.vitalsStatus) &&
                Objects.equals(status, that.status) &&
                Objects.equals(loadStatus, that.loadStatus) &&
                Objects.equals(icn, that.icn) &&
                Objects.equals(ien, that.ien) &&
                Objects.equals(dfn, that.dfn);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, vitalsStatus, status, loadStatus, icn, ien, dfn);
    }
}
