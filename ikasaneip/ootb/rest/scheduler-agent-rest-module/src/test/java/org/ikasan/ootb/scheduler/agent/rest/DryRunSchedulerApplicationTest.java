package org.ikasan.ootb.scheduler.agent.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.hamcrest.core.IsInstanceOf;
import org.ikasan.ootb.scheduler.agent.rest.dto.DryRunFileListJobParameterDto;
import org.ikasan.ootb.scheduler.agent.rest.dto.DryRunFileListParameterDto;
import org.ikasan.ootb.scheduler.agent.rest.dto.DryRunModeDto;
import org.ikasan.ootb.scheduler.agent.rest.dto.JobDryRunModeDto;
import org.ikasan.spec.scheduled.dryrun.DryRunModeService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.internal.matchers.ThrowableCauseMatcher;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {DryRunSchedulerApplication.class, MockedUserServiceTestConfigWithConverter.class})
@EnableWebMvc
public class DryRunSchedulerApplicationTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @MockBean
    protected DryRunModeService dryRunModeService;

    protected MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext webApplicationContext;

    private ObjectMapper mapper = new ObjectMapper();

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @WithMockUser(authorities = "readonly")
    public void dryRunModeWithReadOnlyUser() throws Exception {
        exceptionRule.expect(new ThrowableCauseMatcher(new IsInstanceOf(AccessDeniedException.class)));
        DryRunModeDto dryRunParameterDto = new DryRunModeDto();
        dryRunParameterDto.setDryRunMode(true);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put("/rest/dryRun/mode")
            .content(mapper.writeValueAsString(dryRunParameterDto))
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder).andReturn();

        verifyNoInteractions(dryRunModeService);
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void shouldAcceptDryRunMode() throws Exception {

        DryRunModeDto dryRunParameterDto = new DryRunModeDto();
        dryRunParameterDto.setDryRunMode(true);

        mockMvc.perform(MockMvcRequestBuilders.put("/rest/dryRun/mode")
                .content(mapper.writeValueAsString(dryRunParameterDto))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(dryRunModeService).setDryRunMode(true);
    }

    @Test
    @WithMockUser(authorities = "readonly")
    public void jobDryRunModeWithReadOnlyUser() throws Exception {
        exceptionRule.expect(new ThrowableCauseMatcher(new IsInstanceOf(AccessDeniedException.class)));
        DryRunModeDto dryRunParameterDto = new DryRunModeDto();
        dryRunParameterDto.setDryRunMode(true);

        mockMvc.perform(MockMvcRequestBuilders.put("/rest/dryRun/jobmode")
            .content(mapper.writeValueAsString(dryRunParameterDto))
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)).andReturn();

        verifyNoInteractions(dryRunModeService);
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void shouldAcceptJobDryRunMode() throws Exception {

        JobDryRunModeDto dryRunParameterDto = new JobDryRunModeDto();
        dryRunParameterDto.setIsDryRun(true);
        dryRunParameterDto.setJobName("jobName");

        mockMvc.perform(MockMvcRequestBuilders.put("/rest/dryRun/jobmode")
            .content(mapper.writeValueAsString(dryRunParameterDto))
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(dryRunModeService).setJobDryRun("jobName", true);

        dryRunParameterDto = new JobDryRunModeDto();
        dryRunParameterDto.setIsDryRun(false);
        dryRunParameterDto.setJobName("jobName");

        mockMvc.perform(MockMvcRequestBuilders.put("/rest/dryRun/jobmode")
            .content(mapper.writeValueAsString(dryRunParameterDto))
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(dryRunModeService).setJobDryRun("jobName", false);
    }

    @Test
    @WithMockUser(authorities = "readonly")
    public void dryRunModeFileListWithReadOnlyUser() throws Exception {
        exceptionRule.expect(new ThrowableCauseMatcher(new IsInstanceOf(AccessDeniedException.class)));
        DryRunFileListParameterDto dto = new DryRunFileListParameterDto();

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put("/rest/dryRun/fileList")
            .content(mapper.writeValueAsString(dto))
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder).andReturn();

        verifyNoInteractions(dryRunModeService);
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void shouldAcceptDryRunFileList() throws Exception {

        DryRunFileListParameterDto dto = new DryRunFileListParameterDto();

        List<DryRunFileListJobParameterDto> jobFileList = List.of(
            createFileListJob("jobName1", "fileName1"),
            createFileListJob("jobName2", "fileName2"),
            createFileListJob("jobName3", "fileName3")
        );

        dto.setFileList(jobFileList);

        mockMvc.perform(MockMvcRequestBuilders.put("/rest/dryRun/fileList")
                .content(mapper.writeValueAsString(dto))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(dryRunModeService).addDryRunFileList(any());
    }

    private DryRunFileListJobParameterDto createFileListJob(String jobName, String fileName) {
        DryRunFileListJobParameterDto dto = new DryRunFileListJobParameterDto();
        dto.setJobName(jobName);
        dto.setFileName(fileName);
        return dto;
    }
}
