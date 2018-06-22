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
