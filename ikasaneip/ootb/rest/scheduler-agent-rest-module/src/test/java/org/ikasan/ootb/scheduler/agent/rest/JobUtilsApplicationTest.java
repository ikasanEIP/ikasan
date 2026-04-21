package org.ikasan.ootb.scheduler.agent.rest;

import org.hamcrest.core.IsInstanceOf;
import org.ikasan.ootb.scheduled.processtracker.service.SchedulerPersistenceService;
import org.ikasan.ootb.scheduler.agent.rest.util.JavaUtilsTestHelper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.internal.matchers.ThrowableCauseMatcher;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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

import java.time.Duration;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { JobUtilsApplication.class, MockedUserServiceTestConfigWithConverter.class })
public class JobUtilsApplicationTest
{
    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    protected MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext webApplicationContext;

    @MockBean
    private SchedulerPersistenceService schedulerPersistenceService;


    @Before
    public void setUp()
    {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @WithMockUser(authorities = "readonly")
    public void killPidWithReadOnlyUser() throws Exception
    {
        exceptionRule.expect(new ThrowableCauseMatcher(new IsInstanceOf(AccessDeniedException.class)));
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/jobUtils/kill/9")
            .accept(MediaType.APPLICATION_JSON_VALUE);
        mockMvc.perform(requestBuilder).andReturn();

        Mockito.verifyNoMoreInteractions(schedulerPersistenceService);
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void killPidNotFound() throws Exception
    {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/jobUtils/kill/99999")
            .accept(MediaType.APPLICATION_JSON_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(400, result.getResponse().getStatus());

        assertEquals("\"pid not found!\"", result.getResponse().getContentAsString());

        Mockito.verifyNoMoreInteractions(schedulerPersistenceService);
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void killPidNotFoundForcibly() throws Exception
    {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/jobUtils/kill/99999?destroy=true")
            .accept(MediaType.APPLICATION_JSON_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(400, result.getResponse().getStatus());

        assertEquals("\"pid not found!\"", result.getResponse().getContentAsString());

        Mockito.verifyNoMoreInteractions(schedulerPersistenceService);
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void killPid() throws Exception
    {
        Process process = JavaUtilsTestHelper.exec(JavaUtilsTestHelper.class);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/jobUtils/kill/"+process.pid())
            .accept(MediaType.APPLICATION_JSON_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(200, result.getResponse().getStatus());

        Mockito.verify(schedulerPersistenceService).removeAll(process.pid());
        Mockito.verifyNoMoreInteractions(schedulerPersistenceService);
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void killPidForcibly() throws Exception
    {
        Process process = JavaUtilsTestHelper.exec(JavaUtilsTestHelper.class);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/jobUtils/kill/"+process.pid()+"?destroy=true")
            .accept(MediaType.APPLICATION_JSON_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(200, result.getResponse().getStatus());

        Mockito.verify(schedulerPersistenceService).removeAll(process.pid());
        Mockito.verifyNoMoreInteractions(schedulerPersistenceService);
    }

    /**
     * This test deliberately creates sub-processes which would be typical of a Job started by the scheduler.
     * As a result, this test will catch the issue experienced previously when sub-processes were not dealt with.
     */
    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void killPidKillsChildProcessesExplicitly() throws Exception
    {
        Process parentProcess = JavaUtilsTestHelper.exec(JavaUtilsTestHelper.class, JavaUtilsTestHelper.SPAWN_CHILD_ARGUMENT);
        ProcessHandle childProcessHandle = waitForChildProcess(parentProcess, Duration.ofSeconds(10));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/jobUtils/kill/"+parentProcess.pid()+"?destroy=true")
            .accept(MediaType.APPLICATION_JSON_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(200, result.getResponse().getStatus());
        awaitTermination(parentProcess.toHandle(), Duration.ofSeconds(10));
        awaitTermination(childProcessHandle, Duration.ofSeconds(10));

        assertFalse(parentProcess.toHandle().isAlive());
        assertFalse(childProcessHandle.isAlive());

        Mockito.verify(schedulerPersistenceService).removeAll(parentProcess.pid());
        Mockito.verifyNoMoreInteractions(schedulerPersistenceService);
    }

    private ProcessHandle waitForChildProcess(Process process, Duration timeout) throws InterruptedException
    {
        long timeoutAt = System.nanoTime() + timeout.toNanos();
        Optional<ProcessHandle> childProcess = Optional.empty();

        while (System.nanoTime() < timeoutAt) {
            childProcess = process.toHandle().children().findFirst();
            if (childProcess.isPresent()) {
                return childProcess.get();
            }

            Thread.sleep(100);
        }

        assertTrue("Expected child process for pid " + process.pid(), childProcess.isPresent());
        return childProcess.orElseThrow(IllegalStateException::new);
    }

    private void awaitTermination(ProcessHandle processHandle, Duration timeout) throws InterruptedException
    {
        long timeoutAt = System.nanoTime() + timeout.toNanos();

        while (processHandle.isAlive() && System.nanoTime() < timeoutAt) {
            Thread.sleep(100);
        }
    }

}
