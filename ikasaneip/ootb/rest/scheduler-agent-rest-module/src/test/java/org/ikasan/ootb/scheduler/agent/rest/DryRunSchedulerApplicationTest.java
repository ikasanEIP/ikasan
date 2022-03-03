package org.ikasan.ootb.scheduler.agent.rest;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.hamcrest.core.IsInstanceOf;
import org.ikasan.ootb.scheduler.agent.rest.dto.DryRunFileListJobParameterDto;
import org.ikasan.ootb.scheduler.agent.rest.dto.DryRunFileListParameterDto;
import org.ikasan.ootb.scheduler.agent.rest.dto.DryRunParameterDto;
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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {DryRunSchedulerApplication.class, MockedUserServiceTestConfig.class})
@EnableWebMvc
public class DryRunSchedulerApplicationTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

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
        DryRunParameterDto dryRunParameterDto = new DryRunParameterDto();
        dryRunParameterDto.setDryRunMode(true);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put("/rest/dryRun/mode")
            .content(mapper.writeValueAsString(dryRunParameterDto))
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder).andReturn();
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void shouldAcceptDryRunMode() throws Exception {

        DryRunParameterDto dryRunParameterDto = new DryRunParameterDto();
        dryRunParameterDto.setDryRunMode(true);

        mockMvc.perform(MockMvcRequestBuilders.put("/rest/dryRun/mode")
                .content(mapper.writeValueAsString(dryRunParameterDto))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
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
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void shouldAcceptDryRunFileList() throws Exception {

        DryRunFileListParameterDto dto = new DryRunFileListParameterDto();
        dto.setFileList(
            List.of(
                createFileListJob("jobName1", "fileName1"),
                createFileListJob("jobName2", "fileName2"),
                createFileListJob("jobName3", "fileName3")
            )
        );

        mockMvc.perform(MockMvcRequestBuilders.put("/rest/dryRun/fileList")
                .content(mapper.writeValueAsString(dto))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    private DryRunFileListJobParameterDto createFileListJob(String jobName, String fileName) {
        DryRunFileListJobParameterDto dto = new DryRunFileListJobParameterDto();
        dto.setJobName(jobName);
        dto.setFileName(fileName);
        return dto;
    }
}
