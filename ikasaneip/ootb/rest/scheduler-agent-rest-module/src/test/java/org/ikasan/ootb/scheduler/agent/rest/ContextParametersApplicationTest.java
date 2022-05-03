package org.ikasan.ootb.scheduler.agent.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.apache.commons.io.IOUtils;
import org.hamcrest.core.IsInstanceOf;
import org.ikasan.job.orchestration.model.context.*;
import org.ikasan.job.orchestration.model.job.InternalEventDrivenJobImpl;
import org.ikasan.job.orchestration.model.job.SchedulerJobImpl;
import org.ikasan.ootb.scheduler.agent.rest.cache.ContextParametersCache;
import org.ikasan.spec.scheduled.context.model.*;
import org.ikasan.spec.scheduled.instance.model.ContextParameterInstance;
import org.ikasan.spec.scheduled.job.model.InternalEventDrivenJob;
import org.ikasan.spec.scheduled.job.model.SchedulerJob;
import org.junit.Assert;
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

import java.util.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {ContextParametersApplication.class, MockedUserServiceTestConfigWithConverter.class})
@EnableWebMvc
public class ContextParametersApplicationTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    protected MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext webApplicationContext;

    private ObjectMapper mapper;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        this.mapper = new ObjectMapper();

        final var simpleModule = new SimpleModule()
            .addAbstractTypeMapping(ContextParameterInstance.class, ContextParameterInstanceImpl.class)
            .addAbstractTypeMapping(List.class, ArrayList.class)
            .addAbstractTypeMapping(Map.class, HashMap.class);

        mapper.registerModule(simpleModule);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Test
    @WithMockUser(authorities = "readonly")
    public void saveWithReadOnlyUser() throws Exception {
        exceptionRule.expect(new ThrowableCauseMatcher(new IsInstanceOf(AccessDeniedException.class)));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put("/rest/contextParameters/save")
            .content(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(mapper.readValue(IOUtils.toString(getClass().getResourceAsStream("/data/job-context-parameters-all.json"), "UTF-8"), Map.class)))
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder).andReturn();
    }

    @Test
    @WithMockUser(authorities = "WebServiceAdmin")
    public void save() throws Exception {

        Assert.assertEquals(0, ContextParametersCache.instance().contextNames().size());

        ContextParameterInstanceImpl contextParameterInstance1 = new ContextParameterInstanceImpl();
        contextParameterInstance1.setName("businessDate");
        contextParameterInstance1.setType("date");
        contextParameterInstance1.setValue("19/04/2022");

        Map<String, List<ContextParameterInstance>> contextParameters = new HashMap<>();
        contextParameters.put("test-context", Arrays.asList(contextParameterInstance1));

        mockMvc.perform(MockMvcRequestBuilders.put("/rest/contextParameters/save")
                .content(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(contextParameters))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        Assert.assertEquals(1, ContextParametersCache.instance().contextNames().size());
        Assert.assertEquals(1, ContextParametersCache.instance().getByContextName("test-context").size());
    }


}
