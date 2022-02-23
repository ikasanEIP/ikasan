package org.ikasan.ootb.scheduler.agent.rest;

import org.apache.commons.io.FileUtils;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.internal.matchers.ThrowableCauseMatcher;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

import static org.awaitility.Awaitility.with;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { LogFileStreamApplication.class, MockedUserServiceTestConfig.class })
public class LogFileStreamApplicationTest
{
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
    @WithMockUser(authorities = "readonly")
    public void logsWithReadOnlyUser() throws Exception
    {
        exceptionRule.expect(new ThrowableCauseMatcher(new IsInstanceOf(AccessDeniedException.class)));
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/logs?fullFilePath=logs")
            .accept(MediaType.TEXT_EVENT_STREAM_VALUE);
        mockMvc.perform(requestBuilder).andReturn();
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void logs() throws Exception
    {
        FileUtils.writeLines(Paths.get(sampleLogFileStr).toFile(), Arrays.asList("111"), true);

        // encoded url must be provided!
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/logs?fullFilePath=src%2Ftest%2Fresources%2Fdata%2Flog.sample")
            .accept(MediaType.TEXT_EVENT_STREAM_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        FileUtils.writeLines(Paths.get(sampleLogFileStr).toFile(), Arrays.asList("222"), true);

        assertEquals(200, result.getResponse().getStatus());

        with().pollInterval(1, TimeUnit.SECONDS).and().with().pollDelay(1, TimeUnit.SECONDS).await()
            .atMost(20, TimeUnit.SECONDS).untilAsserted(() -> {

                assertEquals("data:111\n\ndata:222\n\n", result.getResponse().getContentAsString());
            });
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void logs_no_data() throws Exception
    {
        // encoded url must be provided!
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/logs?fullFilePath=src%2Ftest%2Fresources%2Fdata%2Flog.sample")
            .accept(MediaType.TEXT_EVENT_STREAM_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(200, result.getResponse().getStatus());

        Thread.sleep(1000);

        assertEquals("", result.getResponse().getContentAsString());
    }


}
