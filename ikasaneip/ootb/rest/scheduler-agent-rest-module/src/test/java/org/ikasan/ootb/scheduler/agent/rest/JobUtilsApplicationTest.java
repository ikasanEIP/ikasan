package org.ikasan.ootb.scheduler.agent.rest;

import org.hamcrest.core.IsInstanceOf;
import org.ikasan.ootb.scheduled.processtracker.model.SchedulerIkasanProcess;
import org.ikasan.ootb.scheduled.processtracker.service.SchedulerPersistenceService;
import org.ikasan.ootb.scheduler.agent.rest.util.JavaUtilsTestHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { JobUtilsApplication.class, MockedUserServiceTestConfigWithConverter.class })
public class JobUtilsApplicationTest
{
    protected MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext webApplicationContext;

    @MockitoBean
    private SchedulerPersistenceService schedulerPersistenceService;

    @Before
    public void setUp()
    {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @WithMockUser(authorities = "readonly")
    public void killPidWithReadOnlyUser()
    {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/jobUtils/kill/9")
            .accept(MediaType.APPLICATION_JSON_VALUE);

        Exception thrown = assertThrows(Exception.class, () -> mockMvc.perform(requestBuilder).andReturn());
        assertThat(thrown.getCause(), new IsInstanceOf(AccessDeniedException.class));

        Mockito.verifyNoMoreInteractions(schedulerPersistenceService);
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void killPidForbiddenWhenNotManagedByAgent() throws Exception
    {
        // findByPid returns null by default from the mock — PID is not managed by this agent
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/jobUtils/kill/99999")
            .accept(MediaType.APPLICATION_JSON_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(403, result.getResponse().getStatus());
        assertEquals("\"The requested PID is not managed by this agent\"", result.getResponse().getContentAsString());

        Mockito.verify(schedulerPersistenceService).findByPid(99999L);
        Mockito.verifyNoMoreInteractions(schedulerPersistenceService);
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void killPidNotFound() throws Exception
    {
        // PID is managed but the OS process no longer exists
        when(schedulerPersistenceService.findByPid(99999L)).thenReturn(managedProcess(99999L));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/jobUtils/kill/99999")
            .accept(MediaType.APPLICATION_JSON_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(400, result.getResponse().getStatus());
        assertEquals("\"pid not found!\"", result.getResponse().getContentAsString());

        Mockito.verify(schedulerPersistenceService).findByPid(99999L);
        Mockito.verifyNoMoreInteractions(schedulerPersistenceService);
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void killPidNotFoundForcibly() throws Exception
    {
        when(schedulerPersistenceService.findByPid(99999L)).thenReturn(managedProcess(99999L));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/jobUtils/kill/99999?destroy=true")
            .accept(MediaType.APPLICATION_JSON_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(400, result.getResponse().getStatus());
        assertEquals("\"pid not found!\"", result.getResponse().getContentAsString());

        Mockito.verify(schedulerPersistenceService).findByPid(99999L);
        Mockito.verifyNoMoreInteractions(schedulerPersistenceService);
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void killPid() throws Exception
    {
        Process process = JavaUtilsTestHelper.exec(JavaUtilsTestHelper.class);
        when(schedulerPersistenceService.findByPid(process.pid())).thenReturn(managedProcess(process.pid()));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/jobUtils/kill/"+process.pid())
            .accept(MediaType.APPLICATION_JSON_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(200, result.getResponse().getStatus());

        Mockito.verify(schedulerPersistenceService).findByPid(process.pid());
        Mockito.verify(schedulerPersistenceService).persistReturnCodeForKilledProcess(process.pid());
        Mockito.verifyNoMoreInteractions(schedulerPersistenceService);
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void killPidForcibly() throws Exception
    {
        Process process = JavaUtilsTestHelper.exec(JavaUtilsTestHelper.class);
        when(schedulerPersistenceService.findByPid(process.pid())).thenReturn(managedProcess(process.pid()));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/jobUtils/kill/"+process.pid()+"?destroy=true")
            .accept(MediaType.APPLICATION_JSON_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(200, result.getResponse().getStatus());

        Mockito.verify(schedulerPersistenceService).findByPid(process.pid());
        Mockito.verify(schedulerPersistenceService).persistReturnCodeForKilledProcess(process.pid());
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
        when(schedulerPersistenceService.findByPid(parentProcess.pid())).thenReturn(managedProcess(parentProcess.pid()));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/rest/jobUtils/kill/"+parentProcess.pid()+"?destroy=true")
            .accept(MediaType.APPLICATION_JSON_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(200, result.getResponse().getStatus());
        awaitTermination(parentProcess.toHandle(), Duration.ofSeconds(10));
        awaitTermination(childProcessHandle, Duration.ofSeconds(10));

        assertFalse(parentProcess.toHandle().isAlive());
        assertFalse(childProcessHandle.isAlive());

        Mockito.verify(schedulerPersistenceService).findByPid(parentProcess.pid());
        Mockito.verify(schedulerPersistenceService).persistReturnCodeForKilledProcess(parentProcess.pid());
        Mockito.verifyNoMoreInteractions(schedulerPersistenceService);
    }

    private SchedulerIkasanProcess managedProcess(long pid) {
        return new SchedulerIkasanProcess("scheduler", "testJob", pid, "testuser", "/tmp/result", "/tmp/error", 0L);
    }

    private ProcessHandle waitForChildProcess(Process process, Duration timeout) throws InterruptedException
    {
        long timeoutAt = System.nanoTime() + timeout.toNanos();
        Optional<ProcessHandle> childProcess;

        while (System.nanoTime() < timeoutAt) {
            childProcess = process.toHandle().children().findFirst();
            if (childProcess.isPresent()) {
                return childProcess.get();
            }
            Thread.sleep(100);
        }
        throw new AssertionError("Expected child process for pid " + process.pid() + " within timeout");
    }

    private void awaitTermination(ProcessHandle processHandle, Duration timeout) throws InterruptedException
    {
        long timeoutAt = System.nanoTime() + timeout.toNanos();

        while (processHandle.isAlive() && System.nanoTime() < timeoutAt) {
            Thread.sleep(100);
        }
    }
}
