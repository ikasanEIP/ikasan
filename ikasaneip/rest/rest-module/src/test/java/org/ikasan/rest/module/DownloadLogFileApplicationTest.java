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

        Assert.assertTrue(results.getResponse().getContentAsString().endsWith("/ikasan/ikasaneip/rest" +
            "/rest-module/src/test/resources/data/logs/application.log]. Log file is too big to download. Maximum size " +
            "allowed is 20 bytes."));
        Assert.assertEquals(MediaType.APPLICATION_OCTET_STREAM_VALUE, results.getResponse().getContentType());
        Assert.assertEquals("attachment;filename=application.log", results.getResponse().getHeader("Content-Disposition"));
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

        Assert.assertTrue(results.getResponse().getContentAsString().endsWith("/ikasan/ikasaneip/rest" +
            "/rest-module/src/test/resources/data/logs/some-random-file.txt]. Log file name must be one of the " +
            "following[application.log, h2.log, h2-server.log]"));
        Assert.assertEquals(MediaType.APPLICATION_OCTET_STREAM_VALUE, results.getResponse().getContentType());
        Assert.assertEquals("attachment;filename=some-random-file.txt", results.getResponse().getHeader("Content-Disposition"));
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

        Assert.assertTrue(results.getResponse().getContentAsString().endsWith("/ikasan/ikasaneip/rest/rest-module" +
            "/src/test/resources/data/logs/application.log.doesNotExist.log]. Log file name must be one of the " +
            "following[application.log, h2.log, h2-server.log]"));
        Assert.assertEquals(MediaType.APPLICATION_OCTET_STREAM_VALUE, results.getResponse().getContentType());
        Assert.assertEquals("attachment;filename=application.log.doesNotExist.log", results.getResponse().getHeader("Content-Disposition"));
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

    // ==================== Additional Comprehensive Tests ====================

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void test_listLogFiles_with_empty_directory() throws Exception {
        // Set to a directory with no logs subdirectory
        System.setProperty("user.dir", String.valueOf(Path.of(System.getProperty("user.dir"), "src", "test", "resources")));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/logs/listLogFiles?maxFileSize=20971520")
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE);

        MvcResult results = mockMvc.perform(requestBuilder)
            .andExpect(status().is(HttpStatus.NO_CONTENT.value()))
            .andReturn();
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void test_listLogFiles_all_files_filtered_by_size() throws Exception {
        System.setProperty("user.dir", String.valueOf(Path.of(System.getProperty("user.dir"), "src", "test", "resources", "data")));

        // Set max size to 1 byte - should filter out all files
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/logs/listLogFiles?maxFileSize=1")
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE);

        MvcResult results = mockMvc.perform(requestBuilder)
            .andExpect(status().is(HttpStatus.NO_CONTENT.value()))
            .andReturn();
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void test_listLogFiles_returns_h2_log() throws Exception {
        System.setProperty("user.dir", String.valueOf(Path.of(System.getProperty("user.dir"), "src", "test", "resources", "data")));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/logs/listLogFiles?maxFileSize=20971520")
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE);

        MvcResult results = mockMvc.perform(requestBuilder)
            .andExpect(status().is(HttpStatus.OK.value()))
            .andReturn();

        String response = results.getResponse().getContentAsString();
        Assert.assertTrue("Should contain h2.log", response.contains("h2.log"));
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void test_listLogFiles_excludes_non_log_files() throws Exception {
        System.setProperty("user.dir", String.valueOf(Path.of(System.getProperty("user.dir"), "src", "test", "resources", "data")));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/logs/listLogFiles?maxFileSize=20971520")
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE);

        MvcResult results = mockMvc.perform(requestBuilder)
            .andExpect(status().is(HttpStatus.OK.value()))
            .andReturn();

        String response = results.getResponse().getContentAsString();
        Assert.assertFalse("Should not contain random text files", response.contains("some-random-file.txt"));
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void test_listLogFiles_exact_size_boundary() throws Exception {
        System.setProperty("user.dir", String.valueOf(Path.of(System.getProperty("user.dir"), "src", "test", "resources", "data")));

        // h2.log is 18 bytes, application.log is 24 bytes
        // Setting max to 18 should exclude both (< not <=)
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/logs/listLogFiles?maxFileSize=18")
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE);

        MvcResult results = mockMvc.perform(requestBuilder)
            .andExpect(status().is(HttpStatus.NO_CONTENT.value()))
            .andReturn();
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void test_listLogFiles_size_just_above_boundary() throws Exception {
        System.setProperty("user.dir", String.valueOf(Path.of(System.getProperty("user.dir"), "src", "test", "resources", "data")));

        // h2.log is 18 bytes, setting to 19 should include it
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/logs/listLogFiles?maxFileSize=19")
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE);

        MvcResult results = mockMvc.perform(requestBuilder)
            .andExpect(status().is(HttpStatus.OK.value()))
            .andReturn();

        String response = results.getResponse().getContentAsString();
        Assert.assertTrue("Should contain h2.log", response.contains("h2.log"));
        Assert.assertFalse("Should not contain application.log", response.contains("application.log"));
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void test_listLogFiles_json_format_valid() throws Exception {
        System.setProperty("user.dir", String.valueOf(Path.of(System.getProperty("user.dir"), "src", "test", "resources", "data")));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/logs/listLogFiles?maxFileSize=20971520")
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE);

        MvcResult results = mockMvc.perform(requestBuilder)
            .andExpect(status().is(HttpStatus.OK.value()))
            .andReturn();

        String response = results.getResponse().getContentAsString();
        // Verify JSON format
        Assert.assertTrue("Should be valid JSON with braces", response.startsWith("\"{"));
        Assert.assertTrue("Should be valid JSON with braces", response.endsWith("}\""));
        Assert.assertTrue("Should contain key-value pairs", response.contains(":"));
    }

    @Test
    @WithMockUser(authorities = "ALL")
    public void test_listLogFiles_with_ALL_authority() throws Exception {
        System.setProperty("user.dir", String.valueOf(Path.of(System.getProperty("user.dir"), "src", "test", "resources", "data")));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/logs/listLogFiles?maxFileSize=20971520")
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE);

        MvcResult results = mockMvc.perform(requestBuilder)
            .andExpect(status().is(HttpStatus.OK.value()))
            .andReturn();

        Assert.assertTrue(results.getResponse().getContentAsString().contains("application.log"));
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void test_downloadLogFile_h2_log() throws Exception {
        Path path = Path.of(System.getProperty("user.dir"), "src", "test", "resources", "data", "logs");

        File[] filesList = new File(path.toUri()).listFiles();
        String fullPathName = "";
        for (File file : filesList) {
            if (file.getAbsoluteFile().isFile()) {
                if (file.getName().equals("h2.log")) {
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

        Assert.assertEquals("Some h2 db logging", results.getResponse().getContentAsString());
        Assert.assertEquals(MediaType.APPLICATION_OCTET_STREAM_VALUE, results.getResponse().getContentType());
        Assert.assertEquals("attachment;filename=h2.log", results.getResponse().getHeader("Content-Disposition"));
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void test_downloadLogFile_with_normalized_path() throws Exception {
        Path path = Path.of(System.getProperty("user.dir"), "src", "test", "resources", "data", "logs");

        File[] filesList = new File(path.toUri()).listFiles();
        String fullPathName = "";
        for (File file : filesList) {
            if (file.getAbsoluteFile().isFile()) {
                if (file.getName().equals("application.log")) {
                    // Add extra path elements that should be normalized
                    fullPathName = String.valueOf(file.getAbsoluteFile()).replace("/logs/", "/logs/../logs/");
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

        Assert.assertEquals("Some application logging", results.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void test_downloadLogFile_exact_size_boundary() throws Exception {
        Path path = Path.of(System.getProperty("user.dir"), "src", "test", "resources", "data", "logs");

        File[] filesList = new File(path.toUri()).listFiles();
        String fullPathName = "";
        long fileSize = 0;
        for (File file : filesList) {
            if (file.getAbsoluteFile().isFile()) {
                if (file.getName().equals("h2.log")) {
                    fullPathName = String.valueOf(file.getAbsoluteFile());
                    fileSize = file.length();
                }
            }
        }

        // Set max size to exact file size - should fail (< not <=)
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/logs/downloadLogFile?maxFileSize="+fileSize+"&fullFilePath="+fullPathName)
            .accept(MediaType.APPLICATION_OCTET_STREAM_VALUE)
            .contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);

        MvcResult results = mockMvc.perform(requestBuilder)
            .andExpect(request().asyncStarted())
            .andDo(MvcResult::getAsyncResult)
            .andExpect(status().is(HttpStatus.INTERNAL_SERVER_ERROR.value()))
            .andReturn();

        Assert.assertTrue(results.getResponse().getContentAsString().contains("too big to download"));
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void test_downloadLogFile_size_just_above_boundary() throws Exception {
        Path path = Path.of(System.getProperty("user.dir"), "src", "test", "resources", "data", "logs");

        File[] filesList = new File(path.toUri()).listFiles();
        String fullPathName = "";
        long fileSize = 0;
        for (File file : filesList) {
            if (file.getAbsoluteFile().isFile()) {
                if (file.getName().equals("h2.log")) {
                    fullPathName = String.valueOf(file.getAbsoluteFile());
                    fileSize = file.length();
                }
            }
        }

        // Set max size to file size + 1 - should succeed
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/logs/downloadLogFile?maxFileSize="+(fileSize+1)+"&fullFilePath="+fullPathName)
            .accept(MediaType.APPLICATION_OCTET_STREAM_VALUE)
            .contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);

        MvcResult results = mockMvc.perform(requestBuilder)
            .andExpect(request().asyncStarted())
            .andDo(MvcResult::getAsyncResult)
            .andExpect(status().is(HttpStatus.OK.value()))
            .andReturn();

        Assert.assertEquals("Some h2 db logging", results.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void test_downloadLogFile_error_response_format() throws Exception {
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

        String errorMessage = results.getResponse().getContentAsString();
        Assert.assertTrue("Error should start with 'Not able to download'", errorMessage.startsWith("Not able to download the file for"));
        Assert.assertTrue("Error should mention allowed files", errorMessage.contains("application.log, h2.log, h2-server.log"));
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void test_downloadLogFile_content_disposition_header() throws Exception {
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

        String contentDisposition = results.getResponse().getHeader("Content-Disposition");
        Assert.assertEquals("attachment;filename=application.log", contentDisposition);
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void test_downloadLogFile_content_type_header() throws Exception {
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

        Assert.assertEquals(MediaType.APPLICATION_OCTET_STREAM_VALUE, results.getResponse().getContentType());
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void test_downloadLogFile_file_does_not_exist_error_message() throws Exception {
        String nonExistentPath = Path.of(System.getProperty("user.dir"), "src", "test", "resources", "data", "logs", "application.log.missing").toAbsolutePath().toString();

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/logs/downloadLogFile?maxFileSize=20971520&fullFilePath="+nonExistentPath)
            .accept(MediaType.APPLICATION_OCTET_STREAM_VALUE)
            .contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);

        MvcResult results = mockMvc.perform(requestBuilder)
            .andExpect(request().asyncStarted())
            .andDo(MvcResult::getAsyncResult)
            .andExpect(status().is(HttpStatus.INTERNAL_SERVER_ERROR.value()))
            .andReturn();

        String errorMessage = results.getResponse().getContentAsString();
        Assert.assertTrue("Should mention file name issue", errorMessage.contains("Log file name must be one of the following"));
    }

    @Test
    @WithMockUser(authorities = "ALL")
    public void test_downloadLogFile_with_ALL_authority() throws Exception {
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

        Assert.assertEquals("Some application logging", results.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void test_downloadLogFile_zero_max_size() throws Exception {
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

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/logs/downloadLogFile?maxFileSize=0&fullFilePath="+fullPathName)
            .accept(MediaType.APPLICATION_OCTET_STREAM_VALUE)
            .contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);

        MvcResult results = mockMvc.perform(requestBuilder)
            .andExpect(request().asyncStarted())
            .andDo(MvcResult::getAsyncResult)
            .andExpect(status().is(HttpStatus.INTERNAL_SERVER_ERROR.value()))
            .andReturn();

        Assert.assertTrue(results.getResponse().getContentAsString().contains("too big to download"));
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void test_listLogFiles_zero_max_size() throws Exception {
        System.setProperty("user.dir", String.valueOf(Path.of(System.getProperty("user.dir"), "src", "test", "resources", "data")));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/logs/listLogFiles?maxFileSize=0")
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE);

        MvcResult results = mockMvc.perform(requestBuilder)
            .andExpect(status().is(HttpStatus.NO_CONTENT.value()))
            .andReturn();
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void test_listLogFiles_very_large_max_size() throws Exception {
        System.setProperty("user.dir", String.valueOf(Path.of(System.getProperty("user.dir"), "src", "test", "resources", "data")));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/logs/listLogFiles?maxFileSize=999999999")
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(MediaType.APPLICATION_JSON_VALUE);

        MvcResult results = mockMvc.perform(requestBuilder)
            .andExpect(status().is(HttpStatus.OK.value()))
            .andReturn();

        Assert.assertTrue(results.getResponse().getContentAsString().contains("application.log"));
        Assert.assertTrue(results.getResponse().getContentAsString().contains("h2.log"));
    }

    // ==================== Path Traversal & Security Tests ====================

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void test_downloadLogFile_path_traversal_with_dot_dot_blocked() throws Exception {
        // Attempt to access file outside working directory using ../
        String maliciousPath = System.getProperty("user.dir") + "/../../../etc/passwd";

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/logs/downloadLogFile?maxFileSize=20971520&fullFilePath=" + maliciousPath)
            .accept(MediaType.APPLICATION_OCTET_STREAM_VALUE)
            .contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);

        MvcResult results = mockMvc.perform(requestBuilder)
            .andExpect(request().asyncStarted())
            .andDo(MvcResult::getAsyncResult)
            .andExpect(status().is(HttpStatus.INTERNAL_SERVER_ERROR.value()))
            .andReturn();

        String errorMessage = results.getResponse().getContentAsString();
        Assert.assertTrue("Should block path traversal",
            errorMessage.contains("You are not permitted to download from this location") ||
            errorMessage.contains("Log file name must be one of the following"));
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void test_downloadLogFile_absolute_path_outside_working_dir_blocked() throws Exception {
        // Attempt to access absolute path outside working directory
        String absolutePath = "/etc/passwd";

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/logs/downloadLogFile?maxFileSize=20971520&fullFilePath=" + absolutePath)
            .accept(MediaType.APPLICATION_OCTET_STREAM_VALUE)
            .contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);

        MvcResult results = mockMvc.perform(requestBuilder)
            .andExpect(request().asyncStarted())
            .andDo(MvcResult::getAsyncResult)
            .andExpect(status().is(HttpStatus.INTERNAL_SERVER_ERROR.value()))
            .andReturn();

        String errorMessage = results.getResponse().getContentAsString();
        Assert.assertTrue("Should block access outside working directory",
            errorMessage.contains("You are not permitted to download from this location"));
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void test_downloadLogFile_path_within_working_directory_allowed() throws Exception {
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

        // Verify the path is within working directory
        Assert.assertTrue("Test setup: file should be in working directory",
            Path.of(fullPathName).startsWith(Path.of(System.getProperty("user.dir"))));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/logs/downloadLogFile?maxFileSize=20971520&fullFilePath=" + fullPathName)
            .accept(MediaType.APPLICATION_OCTET_STREAM_VALUE)
            .contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);

        MvcResult results = mockMvc.perform(requestBuilder)
            .andExpect(request().asyncStarted())
            .andDo(MvcResult::getAsyncResult)
            .andExpect(status().is(HttpStatus.OK.value()))
            .andReturn();

        Assert.assertEquals("Some application logging", results.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void test_downloadLogFile_path_traversal_encoded_blocked() throws Exception {
        // URL encoded path traversal attempt: ..%2F..%2F..%2Fetc%2Fpasswd
        String encodedPath = System.getProperty("user.dir") + "/..%2F..%2F..%2Fetc%2Fpasswd";

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/logs/downloadLogFile?maxFileSize=20971520&fullFilePath=" + encodedPath)
            .accept(MediaType.APPLICATION_OCTET_STREAM_VALUE)
            .contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);

        MvcResult results = mockMvc.perform(requestBuilder)
            .andExpect(request().asyncStarted())
            .andDo(MvcResult::getAsyncResult)
            .andExpect(status().is(HttpStatus.INTERNAL_SERVER_ERROR.value()))
            .andReturn();

        String errorMessage = results.getResponse().getContentAsString();
        Assert.assertTrue("Should block encoded path traversal",
            errorMessage.contains("You are not permitted to download from this location") ||
            errorMessage.contains("Log file name must be one of the following"));
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void test_downloadLogFile_path_with_symbolic_reference() throws Exception {
        // Attempt with path containing . and .. references
        Path validPath = Path.of(System.getProperty("user.dir"), "src", "test", "resources", "data", "logs", "application.log");
        String pathWithDots = validPath.getParent().toString() + "/./subdir/../application.log";

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/logs/downloadLogFile?maxFileSize=20971520&fullFilePath=" + pathWithDots)
            .accept(MediaType.APPLICATION_OCTET_STREAM_VALUE)
            .contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);

        MvcResult results = mockMvc.perform(requestBuilder)
            .andExpect(request().asyncStarted())
            .andDo(MvcResult::getAsyncResult)
            .andExpect(status().is(HttpStatus.OK.value()))
            .andReturn();

        // Should succeed because after normalization, it's within working directory
        Assert.assertEquals("Some application logging", results.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void test_downloadLogFile_temp_directory_access_blocked() throws Exception {
        // Attempt to access /tmp or system temp directory
        String tempPath = "/tmp/application.log";

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/logs/downloadLogFile?maxFileSize=20971520&fullFilePath=" + tempPath)
            .accept(MediaType.APPLICATION_OCTET_STREAM_VALUE)
            .contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);

        MvcResult results = mockMvc.perform(requestBuilder)
            .andExpect(request().asyncStarted())
            .andDo(MvcResult::getAsyncResult)
            .andExpect(status().is(HttpStatus.INTERNAL_SERVER_ERROR.value()))
            .andReturn();

        String errorMessage = results.getResponse().getContentAsString();
        Assert.assertTrue("Should block access to temp directory",
            errorMessage.contains("You are not permitted to download from this location"));
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void test_downloadLogFile_home_directory_access_blocked() throws Exception {
        // Attempt to access home directory
        String homePath = System.getProperty("user.home") + "/application.log";

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/logs/downloadLogFile?maxFileSize=20971520&fullFilePath=" + homePath)
            .accept(MediaType.APPLICATION_OCTET_STREAM_VALUE)
            .contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);

        MvcResult results = mockMvc.perform(requestBuilder)
            .andExpect(request().asyncStarted())
            .andDo(MvcResult::getAsyncResult)
            .andExpect(status().is(HttpStatus.INTERNAL_SERVER_ERROR.value()))
            .andReturn();

        String errorMessage = results.getResponse().getContentAsString();
        Assert.assertTrue("Should block access to home directory",
            errorMessage.contains("You are not permitted to download from this location"));
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void test_downloadLogFile_windows_path_traversal_blocked() throws Exception {
        // Windows-style path traversal
        String windowsPath = System.getProperty("user.dir") + "\\..\\..\\..\\windows\\system32\\config\\sam";

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/logs/downloadLogFile?maxFileSize=20971520&fullFilePath=" + windowsPath)
            .accept(MediaType.APPLICATION_OCTET_STREAM_VALUE)
            .contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);

        MvcResult results = mockMvc.perform(requestBuilder)
            .andExpect(request().asyncStarted())
            .andDo(MvcResult::getAsyncResult)
            .andExpect(status().is(HttpStatus.INTERNAL_SERVER_ERROR.value()))
            .andReturn();

        String errorMessage = results.getResponse().getContentAsString();
        Assert.assertTrue("Should block Windows-style path traversal",
            errorMessage.contains("You are not permitted to download from this location") ||
            errorMessage.contains("Log file name must be one of the following"));
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void test_downloadLogFile_null_byte_injection() throws Exception {
        // Null byte injection attempt
        Path validPath = Path.of(System.getProperty("user.dir"), "src", "test", "resources", "data", "logs");
        String nullBytePath = validPath + "/application.log\0.txt";

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/logs/downloadLogFile?maxFileSize=20971520&fullFilePath=" + nullBytePath)
            .accept(MediaType.APPLICATION_OCTET_STREAM_VALUE)
            .contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);

        MvcResult results = mockMvc.perform(requestBuilder)
            .andExpect(request().asyncStarted())
            .andDo(MvcResult::getAsyncResult)
            .andExpect(status().is(HttpStatus.INTERNAL_SERVER_ERROR.value()))
            .andReturn();

        // Should fail due to invalid filename or file not found
        String errorMessage = results.getResponse().getContentAsString();
        Assert.assertTrue("Should handle null byte injection",
            errorMessage.contains("Something has gone wrong when trying to download the file"));
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void test_downloadLogFile_double_encoded_path_traversal() throws Exception {
        // Double encoded path traversal: %252E%252E%252F (which is ../)
        String doubleEncodedPath = System.getProperty("user.dir") + "/%252E%252E%252F%252E%252E%252Fetc%252Fpasswd";

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/logs/downloadLogFile?maxFileSize=20971520&fullFilePath=" + doubleEncodedPath)
            .accept(MediaType.APPLICATION_OCTET_STREAM_VALUE)
            .contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);

        MvcResult results = mockMvc.perform(requestBuilder)
            .andExpect(request().asyncStarted())
            .andDo(MvcResult::getAsyncResult)
            .andExpect(status().is(HttpStatus.INTERNAL_SERVER_ERROR.value()))
            .andReturn();

        String errorMessage = results.getResponse().getContentAsString();
        Assert.assertTrue("Should block double encoded path traversal",
            errorMessage.contains("You are not permitted to download from this location") ||
            errorMessage.contains("Log file name must be one of the following"));
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void test_downloadLogFile_path_normalization_prevents_escape() throws Exception {
        // Complex path that tries to escape but should be normalized
        String complexPath = System.getProperty("user.dir") + "/src/test/../../../../../../etc/passwd";

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/logs/downloadLogFile?maxFileSize=20971520&fullFilePath=" + complexPath)
            .accept(MediaType.APPLICATION_OCTET_STREAM_VALUE)
            .contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);

        MvcResult results = mockMvc.perform(requestBuilder)
            .andExpect(request().asyncStarted())
            .andDo(MvcResult::getAsyncResult)
            .andExpect(status().is(HttpStatus.INTERNAL_SERVER_ERROR.value()))
            .andReturn();

        String errorMessage = results.getResponse().getContentAsString();
        Assert.assertTrue("Should prevent escape after normalization",
            errorMessage.contains("You are not permitted to download from this location"));
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void test_downloadLogFile_validates_file_is_within_working_dir() throws Exception {
        Path workingDir = Path.of(System.getProperty("user.dir"));
        Path validLogPath = Path.of(System.getProperty("user.dir"), "src", "test", "resources", "data", "logs", "application.log");

        // Verify test assumption
        Assert.assertTrue("Test file should be within working directory",
            validLogPath.toAbsolutePath().normalize().startsWith(workingDir.toAbsolutePath().normalize()));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/logs/downloadLogFile?maxFileSize=20971520&fullFilePath=" + validLogPath)
            .accept(MediaType.APPLICATION_OCTET_STREAM_VALUE)
            .contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);

        MvcResult results = mockMvc.perform(requestBuilder)
            .andExpect(request().asyncStarted())
            .andDo(MvcResult::getAsyncResult)
            .andExpect(status().is(HttpStatus.OK.value()))
            .andReturn();

        Assert.assertEquals("Some application logging", results.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void test_downloadLogFile_security_error_message_format() throws Exception {
        String outsidePath = "/etc/passwd";

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/logs/downloadLogFile?maxFileSize=20971520&fullFilePath=" + outsidePath)
            .accept(MediaType.APPLICATION_OCTET_STREAM_VALUE)
            .contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);

        MvcResult results = mockMvc.perform(requestBuilder)
            .andExpect(request().asyncStarted())
            .andDo(MvcResult::getAsyncResult)
            .andExpect(status().is(HttpStatus.INTERNAL_SERVER_ERROR.value()))
            .andReturn();

        String errorMessage = results.getResponse().getContentAsString();
        Assert.assertTrue("Error message should be clear about permission",
            errorMessage.contains("You are not permitted to download from this location"));
        Assert.assertEquals("Content type should still be octet stream",
            MediaType.APPLICATION_OCTET_STREAM_VALUE, results.getResponse().getContentType());
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void test_downloadLogFile_case_sensitive_filename_validation() throws Exception {
        // Attempt with different case (should fail if case-sensitive)
        Path path = Path.of(System.getProperty("user.dir"), "src", "test", "resources", "data", "logs");
        File[] filesList = new File(path.toUri()).listFiles();
        String fullPathName = "";
        for (File file : filesList) {
            if (file.getAbsoluteFile().isFile() && file.getName().equals("application.log")) {
                // Change the filename case in the path
                fullPathName = file.getAbsolutePath().replace("application.log", "APPLICATION.LOG");
            }
        }

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/logs/downloadLogFile?maxFileSize=20971520&fullFilePath=" + fullPathName)
            .accept(MediaType.APPLICATION_OCTET_STREAM_VALUE)
            .contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);

        MvcResult results = mockMvc.perform(requestBuilder)
            .andExpect(request().asyncStarted())
            .andDo(MvcResult::getAsyncResult)
            .andExpect(status().is(HttpStatus.INTERNAL_SERVER_ERROR.value()))
            .andReturn();

        String errorMessage = results.getResponse().getContentAsString();
        Assert.assertTrue("Should validate filename case-sensitively",
            errorMessage.contains("Log file name must be one of the following"));
    }
}
