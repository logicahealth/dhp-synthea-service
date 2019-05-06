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

package com.digitalservices.dhp.dhpsyntheaservice.client;

import com.digitalservices.dhp.dhpsyntheaservice.domain.VistaOhcResponse;
import com.digitalservices.dhp.dhpsyntheaservice.domain.VistaResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

@Component
public class EhrClient {

    private static final Logger LOG = LogManager.getLogger(EhrClient.class);

    @Value("${synthea.root.output.fhir}")
    private String syntheaOutput;


    @Value("${vista.url}")
    private String vistaUrl;

    public VistaOhcResponse sendAllToVista() throws Exception {
        VistaOhcResponse voResponse = new VistaOhcResponse();
        Path path = FileSystems.getDefault().getPath(syntheaOutput);

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path, "*.{json}")) {
            for (Path entry : stream) {
                if (entry.toFile().getName().contains("Information")) {
                    continue;
                }
                voResponse = sendToVista(entry);
            }
        } catch (DirectoryIteratorException ex) {
            LOG.error("Failed to get files", ex);
            throw ex.getCause();
        }
        return voResponse;
    }

    public VistaOhcResponse sendOneToVista(String fileName) throws Exception {
        Path path = FileSystems.getDefault().getPath(syntheaOutput + "/" + fileName);
        return sendToVista(path);
    }

    public VistaOhcResponse sendToVista(Path path) throws Exception {

        VistaOhcResponse voResponse = new VistaOhcResponse();
        ResponseEntity<VistaResponse> response = vistaClient(path);

        if (!response.getStatusCode().isError() && response.getBody().getLoadStatus().equalsIgnoreCase("loaded")) {
            voResponse.setICN(response.getBody().getIcn());
            voResponse.setVistaSuccess(true);
        } else {
            voResponse.setVistaSuccess(false);

        }
        return voResponse;

    }


    private ResponseEntity<VistaResponse> vistaClient(Path path) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        File file = path.toFile();
        Map<String, String> params = new HashMap<>();
        String contents = new String(Files.readAllBytes(path));
        params.put("id", file.getName().replace(" ", "_"));
        LOG.info("sending to " + vistaUrl);
        ResponseEntity<VistaResponse> response = restTemplate.postForEntity(vistaUrl, contents, VistaResponse.class, params);
        LOG.info(response.getBody().getIcn());
        LOG.info(response.getStatusCode());
        return response;

    }

}
