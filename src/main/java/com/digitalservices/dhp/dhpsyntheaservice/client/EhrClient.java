package com.digitalservices.dhp.dhpsyntheaservice.client;

import com.digitalservices.dhp.dhpsyntheaservice.domain.VistaOhcResponse;
import com.digitalservices.dhp.dhpsyntheaservice.domain.VistaResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

@Component
public class EhrClient {


    @Value("${synthea.root.output.fhir}")
    private String syntheaOutput;


    @Value("${vista.url}")
    private String vistaUrl;

    @Value("${ohc.url}")
    private String ohcUrl;

    //@Autowired RestTemplate resetTemplate;

    public VistaOhcResponse sendAllToVista() throws Exception {
        VistaOhcResponse voResponse = new VistaOhcResponse();
        Path path = FileSystems.getDefault().getPath(syntheaOutput);

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path, "*.{json}")) {
            for (Path entry : stream) {
                if (entry.toFile().getName().contains("hospital")) {
                    continue;
                }
                voResponse = sendToVista(entry);
            }
        } catch (DirectoryIteratorException ex) {
            // I/O error encountered during the iteration, the cause is an IOException
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
            ResponseEntity<String> ohcResponse = ohcClient(response.getBody().getIcn());
            if (ohcResponse.getBody().startsWith("Successful")) {
                voResponse.setOhcSuccess(true);
            } else {
                voResponse.setError(ohcResponse.getBody());
            }

        } else {
            voResponse.setVistaSuccess(false);
            //voResponse.setError(response.getBody().);
        }
        return voResponse;

    }

    private ResponseEntity<VistaResponse> vistaClient(Path path) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        File file = path.toFile();
        Map<String, String> params = new HashMap<>();
        String contents = new String(Files.readAllBytes(path));
        params.put("id", file.getName().replace(" ", "_"));
        System.out.println("sending to " + vistaUrl);
        ResponseEntity<VistaResponse> response = restTemplate.postForEntity(vistaUrl, contents, VistaResponse.class, params);
        System.out.println(response.getBody().getIcn());
        System.out.println(response.getStatusCode());
        return response;

    }

    private ResponseEntity<String> ohcClient(String id) {
        RestTemplate restTemplate = new RestTemplate();
        String url = ohcUrl + "?id=" + id;
        System.out.println("sending to " + url);
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        System.out.println(response.getStatusCode());
        System.out.println(response.getBody());
        return response;
    }

}
