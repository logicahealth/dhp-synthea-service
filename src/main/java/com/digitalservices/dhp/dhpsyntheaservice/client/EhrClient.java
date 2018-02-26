package com.digitalservices.dhp.dhpsyntheaservice.client;

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
    private String fhirDir;


    @Value("${vista.url}")
    private String vistaUrl;

    @Value("${ohc.url}")
    private String ohcUrl;

    //@Autowired RestTemplate resetTemplate;

    public VistaResponse sendAllToVista() throws Exception {

        Path path = FileSystems.getDefault().getPath(fhirDir);
        ResponseEntity<VistaResponse> response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path, "*.{json}")) {
            for (Path entry : stream) {
                if (entry.toFile().getName().contains("hospital")){
                    continue;
                }
                response = vistaClient(entry);
                if(response.getStatusCode().equals(HttpStatus.OK)){
                    System.out.println(response.getBody().getId());
                    ohcClient(response.getBody().getIcn());
                }
            }
        } catch (DirectoryIteratorException ex) {
            // I/O error encountered during the iteration, the cause is an IOException
            throw ex.getCause();
        }
        return response.getBody();
    }
    public VistaResponse sendOneToVista(String fileName) throws Exception {
        Path path = FileSystems.getDefault().getPath(fhirDir + "/" + fileName);
        ResponseEntity<VistaResponse> response = vistaClient(path);
        if(response.getStatusCode().equals(HttpStatus.OK)){

            ohcClient(response.getBody().getIcn());
        }
        return response.getBody();

    }
    private ResponseEntity<VistaResponse> vistaClient(Path path) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        File file = path.toFile();
        Map<String, String> params = new HashMap<>();
        String contents = new String(Files.readAllBytes(path));
        params.put("id", file.getName().replace(" ", "_"));
        ResponseEntity<VistaResponse> response = restTemplate.postForEntity(vistaUrl, contents, VistaResponse.class, params);

        return response;

    }

    private void ohcClient(String id){
        RestTemplate restTemplate = new RestTemplate();
        String url = ohcUrl + "?id=" + id;
        ResponseEntity<String> response = restTemplate.getForEntity(url,  String.class);
        System.out.println(response.getStatusCode());
        System.out.println(response.getBody());
    }

}
