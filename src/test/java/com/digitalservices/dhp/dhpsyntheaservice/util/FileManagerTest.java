package com.digitalservices.dhp.dhpsyntheaservice.util;

import com.digitalservices.dhp.dhpsyntheaservice.domain.FileMetaData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.*;
@RunWith(SpringRunner.class)
@SpringBootTest
public class FileManagerTest {

    @Autowired
    private FileManager fileManager;

    @Test
    public void getFileByName() {
        String name = "Ardith914 Cummerata161_f7b86eba-46fd-4406-9243-e6b81d38f61f.json";
        File file = fileManager.getFileByName(name);
        assertTrue(file.exists());
    }

    @Test
    public void getStringFromFile() throws IOException {
        String name = "Maricarmen445 Chávez927_fc2034ab-310f-4f07-936d-87aea70892a6.json";
        String contents = fileManager.getStringFromFile(name);
        assertNotNull(contents);
    }

    @Test
    public void getAllPatientFiles() {
        String baseUrl = "http://test.org";
        List<FileMetaData> fileMetaData = fileManager.getAllPatientFiles(baseUrl);
        assertEquals(fileMetaData.size(), 3);
    }
}