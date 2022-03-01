package org.ikasan.rest.module;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.hamcrest.core.IsInstanceOf;
import org.ikasan.rest.module.sse.MonitoringFileService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.internal.matchers.ThrowableCauseMatcher;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(properties = {"sse.max.stream.threads=1", "sse.thread.wait.time=50"})
@SpringBootTest(classes = {LogFileStreamApplication.class, MonitoringFileService.class, MockedUserServiceTestConfig.class})
public class LogFileStreamApplicationTest {
    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    protected MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext webApplicationContext;

    private final String sampleLogFileStr = "src/test/resources/data/log.sample";

    @Before
    public void setUp() throws IOException {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        FileChannel.open(Paths.get(sampleLogFileStr), StandardOpenOption.WRITE).truncate(0).close();
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void logsWithWrongPath() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/logs?someBogusParam=bogus")
            .accept(MediaType.TEXT_EVENT_STREAM_VALUE);

        mockMvc.perform(requestBuilder)
            .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
            .andReturn();
    }

    @Test
    @WithMockUser(authorities = "readonly")
    public void logsWithReadOnlyUser() throws Exception {
        exceptionRule.expect(new ThrowableCauseMatcher(new IsInstanceOf(AccessDeniedException.class)));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/logs?fullFilePath=logs")
            .accept(MediaType.TEXT_EVENT_STREAM_VALUE);

        mockMvc.perform(requestBuilder)
            .andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
            .andReturn();
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void logsAndCanNotExceedMaxThreads() throws Exception {
        FileUtils.writeLines(Paths.get(sampleLogFileStr).toFile(), List.of("111"), true);
        FileUtils.writeLines(Paths.get(sampleLogFileStr).toFile(), List.of("222"), true);

        // encoded url must be provided!
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/logs?fullFilePath=src%2Ftest%2Fresources%2Fdata%2Flog.sample")
            .accept(MediaType.TEXT_EVENT_STREAM_VALUE);

        mockMvc.perform(requestBuilder)
            .andExpect(status().is(HttpStatus.OK.value()))
            .andExpect(content().string("data:111\n\ndata:222\n\n"))
            .andReturn();

        // make sure we get an error if too many threads i.e. too many requests
        mockMvc.perform(requestBuilder)
            .andExpect(status().is(HttpStatus.TOO_MANY_REQUESTS.value()))
            .andExpect(status().reason("Maximum number of log file streaming threads reached"))
            .andExpect(content().string(""))
            .andReturn();
    }
}
