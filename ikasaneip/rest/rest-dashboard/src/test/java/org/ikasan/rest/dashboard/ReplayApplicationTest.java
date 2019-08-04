package org.ikasan.rest.dashboard;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ReplayApplication.class)
@WebAppConfiguration
public class ReplayApplicationTest extends  AbstractRestMvcTest
{
    public static final String REPLAY_JSON = "/data/replayEvents.json";

    protected MockMvc mvc;
    @Autowired
    WebApplicationContext webApplicationContext;

    @Before
    public void setUp()
    {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }


    @Test
    public void harvest_replay_success() throws Exception
    {
        String uri = "/rest/harvest/replay";

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.put(uri)
            .contentType(MediaType.APPLICATION_JSON_VALUE).content(super.loadDataFile(REPLAY_JSON))).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(HttpStatus.OK.value(), status);
        String content = mvcResult.getResponse().getContentAsString();
        assertEquals(content, "Harvested replay events successfully captured!");
    }

    @Test
    public void test_exception_bad_post_json() throws Exception
    {
        String uri = "/rest/harvest/replay";

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.put(uri)
            .contentType(MediaType.APPLICATION_JSON_VALUE).content("bad json")).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(HttpStatus.BAD_REQUEST.value(), status);
        String content = mvcResult.getResponse().getErrorMessage();
        assertEquals(content, "Cannot parse replay JSON!");
    }
}
