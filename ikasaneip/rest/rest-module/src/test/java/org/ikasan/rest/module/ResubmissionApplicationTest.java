package org.ikasan.rest.module;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.core.IsInstanceOf;
import org.ikasan.module.SimpleModule;
import org.ikasan.rest.module.dto.ChangeFlowStartupModeDto;
import org.ikasan.rest.module.dto.ChangeFlowStateDto;
import org.ikasan.rest.module.dto.ResubmissionRequestDto;
import org.ikasan.rest.module.model.TestFlow;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.hospital.service.HospitalService;
import org.ikasan.spec.module.StartupType;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.internal.matchers.ThrowableCauseMatcher;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
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
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.security.Principal;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { ResubmissionApplication.class, MockedUserServiceTestConfig.class })
@EnableWebMvc
public class ResubmissionApplicationTest
{
    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    protected MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext webApplicationContext;

    @MockBean
    protected HospitalService hospitalService;

    @Autowired
    protected ResubmissionApplication resubmissionApplication;

    private ObjectMapper mapper = new ObjectMapper();


    @Before
    public void setUp()
    {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @WithMockUser(authorities = "readonly")
    public void searchWithReadOnlyUser() throws Exception
    {
        exceptionRule.expect(new ThrowableCauseMatcher(new IsInstanceOf(AccessDeniedException.class)));
        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/rest/resubmission")
                                                              .content(createResubmissionRequestDto("TEST", "ignore" ))
                                                              .accept(MediaType.APPLICATION_JSON_VALUE)
                                                              .contentType(MediaType.APPLICATION_JSON_VALUE);
        mockMvc.perform(requestBuilder).andReturn();
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void resubmissionPUT_when_actionIgnore() throws Exception
    {


        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/rest/resubmission")
                                                              .content(createResubmissionRequestDto("TEST", "ignore" ))
                                                              .accept(MediaType.APPLICATION_JSON_VALUE)
                                                              .contentType(MediaType.APPLICATION_JSON_VALUE);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        Mockito
            .verify(hospitalService).ignore(Mockito.eq("testModule"), Mockito.eq("testFlow"),Mockito.eq("TEST"),Mockito.any(String.class));
        Mockito.verifyNoMoreInteractions(hospitalService);

        assertEquals(200, result.getResponse().getStatus());

    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void resubmissionPUT_when_actionResubmission() throws Exception
    {


        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/rest/resubmission")
                                                              .content(createResubmissionRequestDto("TEST", "resubmit" ))
                                                              .accept(MediaType.APPLICATION_JSON_VALUE)
                                                              .contentType(MediaType.APPLICATION_JSON_VALUE);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        Mockito
            .verify(hospitalService).resubmit(Mockito.eq("testModule"), Mockito.eq("testFlow"),Mockito.eq("TEST"),Mockito.any(String.class));
        Mockito.verifyNoMoreInteractions(hospitalService);

        assertEquals(200, result.getResponse().getStatus());

    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void resubmissionPUT_when_actionNotKnown() throws Exception
    {


        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/rest/resubmission")
                                                              .content(createResubmissionRequestDto("TEST", "NotKnown" ))
                                                              .accept(MediaType.APPLICATION_JSON_VALUE)
                                                              .contentType(MediaType.APPLICATION_JSON_VALUE);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        Mockito.verifyNoMoreInteractions(hospitalService);

        assertEquals(400, result.getResponse().getStatus());
        assertEquals("{\"errorMessage\":\"Invalid action[NotKnown] allowed values [resubmit|ignore] \"}", result.getResponse().getContentAsString());

    }


    private String createResubmissionRequestDto(String errorUri,String action) throws JsonProcessingException
    {
        ResubmissionRequestDto dto = new ResubmissionRequestDto("testModule", "testFlow", errorUri, action);
        return mapper.writeValueAsString(dto);
    }
}
