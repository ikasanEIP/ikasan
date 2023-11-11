package org.ikasan.rest.module;

import org.hamcrest.core.IsInstanceOf;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.internal.matchers.ThrowableCauseMatcher;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {DownloadLogFileApplication.class, MockedUserServiceTestConfig.class})
class DownloadLogFileApplicationTest {

    protected MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext webApplicationContext;

    private final String userDir = System.getProperty("user.dir");

    @BeforeEach
    void setUp() throws IOException {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @AfterEach
    void tearDown() throws IOException {
        System.setProperty("user.dir", userDir);
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    void listLogFilesTest() throws Exception {

        System.setProperty("user.dir", String.valueOf(Path.of(System.getProperty("user.dir"), "src", "test", "resources", "data")));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/logs/listLogFiles?maxFileSize=20971520")
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE);

        MvcResult results = mockMvc.perform(requestBuilder)
            .andExpect(status().is(HttpStatus.OK.value()))
            .andReturn();

        assertTrue(results.getResponse().getContentAsString().contains("application.log"));
        assertTrue(results.getResponse().getContentAsString().contains("h2.log"));
    }

    @Test
    @WithMockUser(authorities = "readonly")
    void listLogFilesTestWithReadOnlyUser() throws Exception {
        exceptionRule.expect(new ThrowableCauseMatcher(new IsInstanceOf(AccessDeniedException.class)));

        System.setProperty("user.dir", String.valueOf(Path.of(System.getProperty("user.dir"), "src", "test", "resources", "data")));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/logs/listLogFiles?maxFileSize=20971520")
            .accept(MediaType.APPLICATION_JSON_VALUE);

        mockMvc.perform(requestBuilder)
            .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
            .andReturn();

    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    void listLogFilesTooBigTest() throws Exception {

        System.setProperty("user.dir", String.valueOf(Path.of(System.getProperty("user.dir"), "src", "test", "resources", "data")));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/logs/listLogFiles?maxFileSize=20")
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE);

        MvcResult results = mockMvc.perform(requestBuilder)
            .andExpect(status().is(HttpStatus.OK.value()))
            .andReturn();
        //application.log is over 20 bytes (24bytes), so do not return
        assertTrue(results.getResponse().getContentAsString().contains("h2.log"));
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    void downloadLogFileTest() throws Exception {

        Path path = Path.of(System.getProperty("user.dir"), "src", "test", "resources", "data", "logs");

        File[] filesList = new File(path.toUri()).listFiles();
        String fullPathName = "";
        for (File file : filesList) {
            if (file.getAbsoluteFile().isFile()) {
                if (file.getName().equals("application.log")) {
                    fullPathName = String.valueOf(file.getAbsoluteFile());
                }
            }
        }

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/logs/downloadLogFile?maxFileSize=20971520&fullFilePath="+fullPathName)
            .accept(MediaType.APPLICATION_OCTET_STREAM_VALUE)
            .contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);

        MvcResult results = mockMvc.perform(requestBuilder)
            .andExpect(request().asyncStarted())
            .andDo(MvcResult::getAsyncResult)
            .andExpect(status().is(HttpStatus.OK.value()))
            .andReturn();

        assertEquals("Some application logging", results.getResponse().getContentAsString());
        assertEquals(MediaType.APPLICATION_OCTET_STREAM_VALUE, results.getResponse().getContentType());
        assertEquals("attachment;filename=application.log", results.getResponse().getHeader("Content-Disposition"));
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    void downloadLogFileTooBigTest() throws Exception {

        Path path = Path.of(System.getProperty("user.dir"), "src", "test", "resources", "data", "logs");

        File[] filesList = new File(path.toUri()).listFiles();
        String fullPathName = "";
        for (File file : filesList) {
            if (file.getAbsoluteFile().isFile()) {
                if (file.getName().equals("application.log")) {
                    fullPathName = String.valueOf(file.getAbsoluteFile());
                }
            }
        }

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/logs/downloadLogFile?maxFileSize=20&fullFilePath="+fullPathName)
            .accept(MediaType.APPLICATION_OCTET_STREAM_VALUE)
            .contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);

        MvcResult results = mockMvc.perform(requestBuilder)
            .andExpect(request().asyncStarted())
            .andDo(MvcResult::getAsyncResult)
            .andExpect(status().is(HttpStatus.INTERNAL_SERVER_ERROR.value()))
            .andReturn();

        assertEquals("Not able to download the file for [application.log]. Log file maybe too big to download. Maximum size allowed is 20 bytes.", results.getResponse().getContentAsString());
        assertEquals(MediaType.APPLICATION_OCTET_STREAM_VALUE, results.getResponse().getContentType());
        assertEquals("attachment;filename=application.log", results.getResponse().getHeader("Content-Disposition"));
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    void downloadLogFileNotAllowedTest() throws Exception {

        Path path = Path.of(System.getProperty("user.dir"), "src", "test", "resources", "data", "logs");
        File[] filesList = new File(path.toUri()).listFiles();
        String fullPathName = "";
        for (File file : filesList) {
            if (file.getAbsoluteFile().isFile()) {
                if (file.getName().equals("some-random-file.txt")) {
                    fullPathName = String.valueOf(file.getAbsoluteFile());
                }
            }
        }

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/logs/downloadLogFile?maxFileSize=20971520&fullFilePath="+fullPathName)
            .accept(MediaType.APPLICATION_OCTET_STREAM_VALUE)
            .contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);

        MvcResult results = mockMvc.perform(requestBuilder)
            .andExpect(request().asyncStarted())
            .andDo(MvcResult::getAsyncResult)
            .andExpect(status().is(HttpStatus.INTERNAL_SERVER_ERROR.value()))
            .andReturn();

        assertEquals("Not able to download the file for [some-random-file.txt]. Log file maybe too big to download. Maximum size allowed is 20971520 bytes.", results.getResponse().getContentAsString());
        assertEquals(MediaType.APPLICATION_OCTET_STREAM_VALUE, results.getResponse().getContentType());
        assertEquals("attachment;filename=some-random-file.txt", results.getResponse().getHeader("Content-Disposition"));
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    void downloadLogFileDoesNotExistTest() throws Exception {

        Path path = Path.of(System.getProperty("user.dir"), "src", "test", "resources", "data", "logs");

        File[] filesList = new File(path.toUri()).listFiles();
        String fullPathName = "";
        for (File file : filesList) {
            if (file.getAbsoluteFile().isFile()) {
                if (file.getName().equals("application.log")) {
                    fullPathName = String.valueOf(file.getAbsoluteFile());
                    fullPathName = fullPathName.replace("application.log", "application.log.doesNotExist.log");
                }
            }
        }

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/logs/downloadLogFile?maxFileSize=20971520&fullFilePath="+fullPathName)
            .accept(MediaType.APPLICATION_OCTET_STREAM_VALUE)
            .contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);

        MvcResult results = mockMvc.perform(requestBuilder)
            .andExpect(request().asyncStarted())
            .andDo(MvcResult::getAsyncResult)
            .andExpect(status().is(HttpStatus.INTERNAL_SERVER_ERROR.value()))
            .andReturn();

        assertTrue(results.getResponse().getContentAsString().contains("Something has gone wrong when trying to download the file"));
        assertEquals(MediaType.APPLICATION_OCTET_STREAM_VALUE, results.getResponse().getContentType());
        assertEquals("attachment;filename=error.txt", results.getResponse().getHeader("Content-Disposition"));
    }

    @Test
    @WithMockUser(authorities = "readonly")
    void downloadLogFileTestReadOnly() throws Exception {
        exceptionRule.expect(new ThrowableCauseMatcher(new IsInstanceOf(AccessDeniedException.class)));

        Path path = Path.of(System.getProperty("user.dir"), "src", "test", "resources", "data", "logs");

        File[] filesList = new File(path.toUri()).listFiles();
        String fullPathName = "";
        for (File file : filesList) {
            if (file.getAbsoluteFile().isFile()) {
                if (file.getName().equals("application.log")) {
                    fullPathName = String.valueOf(file.getAbsoluteFile());
                }
            }
        }

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/logs/downloadLogFile?maxFileSize=20971520&fullFilePath="+fullPathName)
            .accept(MediaType.APPLICATION_OCTET_STREAM_VALUE)
            .contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);

        mockMvc.perform(requestBuilder)
            .andExpect(request().asyncStarted())
            .andDo(MvcResult::getAsyncResult)
            .andExpect(status().is(HttpStatus.OK.value()))
            .andReturn();

    }
}
