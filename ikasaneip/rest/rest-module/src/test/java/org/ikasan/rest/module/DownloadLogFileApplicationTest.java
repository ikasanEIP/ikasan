package org.ikasan.rest.module;

import org.hamcrest.core.IsInstanceOf;
import org.junit.*;
import org.junit.internal.matchers.ThrowableCauseMatcher;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { DownloadLogFileApplication.class, MockedUserServiceTestConfig.class })
public class DownloadLogFileApplicationTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    protected MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext webApplicationContext;

    private final String userDir = System.getProperty("user.dir");;

    @Before
    public void setUp() throws IOException {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @After
    public void tearDown() throws IOException {
        System.setProperty("user.dir", userDir);
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void listLogFilesTest() throws Exception {

        System.setProperty("user.dir", String.valueOf(Path.of(System.getProperty("user.dir"), "src", "test", "resources", "data")));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/logs/listLogFiles?maxFileSize=20971520")
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE);

        MvcResult results = mockMvc.perform(requestBuilder)
            .andExpect(status().is(HttpStatus.OK.value()))
            .andReturn();

        Assert.assertTrue(results.getResponse().getContentAsString().contains("application.log"));
        Assert.assertTrue(results.getResponse().getContentAsString().contains("h2.log"));
    }

    @Test
    @WithMockUser(authorities = "readonly")
    public void listLogFilesTestWithReadOnlyUser() throws Exception {
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
    public void listLogFilesTooBigTest() throws Exception {

        System.setProperty("user.dir", String.valueOf(Path.of(System.getProperty("user.dir"), "src", "test", "resources", "data")));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/logs/listLogFiles?maxFileSize=20")
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE);

        MvcResult results = mockMvc.perform(requestBuilder)
            .andExpect(status().is(HttpStatus.OK.value()))
            .andReturn();
        //application.log is over 20 bytes (24bytes), so do not return
        Assert.assertTrue(results.getResponse().getContentAsString().contains("h2.log"));
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void downloadLogFileTest() throws Exception {

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

        Assert.assertEquals(results.getResponse().getContentAsString(), "Some application logging");
        Assert.assertEquals(results.getResponse().getContentType(), MediaType.APPLICATION_OCTET_STREAM_VALUE);
        Assert.assertEquals(results.getResponse().getHeader("Content-Disposition"), "attachment;filename=application.log");
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void downloadLogFileTooBigTest() throws Exception {

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

        Assert.assertEquals(results.getResponse().getContentAsString(), "Not able to download the file for [application.log]. Log file maybe too big to download. Maximum size allowed is 20 bytes.");
        Assert.assertEquals(results.getResponse().getContentType(), MediaType.APPLICATION_OCTET_STREAM_VALUE);
        Assert.assertEquals(results.getResponse().getHeader("Content-Disposition"), "attachment;filename=application.log");
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void downloadLogFileNotAllowedTest() throws Exception {

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

        Assert.assertEquals(results.getResponse().getContentAsString(), "Not able to download the file for [some-random-file.txt]. Log file maybe too big to download. Maximum size allowed is 20971520 bytes.");
        Assert.assertEquals(results.getResponse().getContentType(), MediaType.APPLICATION_OCTET_STREAM_VALUE);
        Assert.assertEquals(results.getResponse().getHeader("Content-Disposition"), "attachment;filename=some-random-file.txt");
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void downloadLogFileDoesNotExistTest() throws Exception {

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

        Assert.assertTrue(results.getResponse().getContentAsString().contains("Something has gone wrong when trying to download the file"));
        Assert.assertEquals(results.getResponse().getContentType(), MediaType.APPLICATION_OCTET_STREAM_VALUE);
        Assert.assertEquals(results.getResponse().getHeader("Content-Disposition"), "attachment;filename=error.txt");
    }

    @Test
    @WithMockUser(authorities = "readonly")
    public void downloadLogFileTestReadOnly() throws Exception {
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
