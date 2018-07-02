/* Created by Perspecta http://www.perspecta.com */
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
