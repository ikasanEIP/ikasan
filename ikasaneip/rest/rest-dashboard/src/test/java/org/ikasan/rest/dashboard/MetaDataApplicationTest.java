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
@SpringBootTest(classes = MetaDataApplication.class)
@WebAppConfiguration
public class MetaDataApplicationTest extends  AbstractRestMvcTest
{
    public static final String METADATA_JSON = "/data/metadata.json";

    protected MockMvc mvc;
    @Autowired
    WebApplicationContext webApplicationContext;

    @Before
    public void setUp()
    {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }


    @Test
    public void harvest_metadata_success() throws Exception
    {
        String uri = "/rest/module/metadata";

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.put(uri)
            .contentType(MediaType.APPLICATION_JSON_VALUE).content(super.loadDataFile(METADATA_JSON))).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(HttpStatus.OK.value(), status);
        String content = mvcResult.getResponse().getContentAsString();
        assertEquals(content, "Module metadata successfully captured!");
    }

//    @Test
//    public void test_exception_bad_post_json() throws Exception
//    {
//        String uri = "/rest/harvest/wiretap";
//
//        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.put(uri)
//            .contentType(MediaType.APPLICATION_JSON_VALUE).content("bad json")).andReturn();
//
//        int status = mvcResult.getResponse().getStatus();
//        assertEquals(HttpStatus.BAD_REQUEST.value(), status);
//        String content = mvcResult.getResponse().getErrorMessage();
//        assertEquals(content, "Cannot parse wiretap JSON!");
//    }
}
