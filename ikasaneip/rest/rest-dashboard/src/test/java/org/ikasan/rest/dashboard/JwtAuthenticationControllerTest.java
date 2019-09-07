package org.ikasan.rest.dashboard;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {JwtAuthenticationController.class, MockedUserServiceTestConfig.class})
@WebAppConfiguration
public class JwtAuthenticationControllerTest extends AbstractRestMvcTest
{
    public static final String AUTHENTICATE_JSON = "/data/authenticate.json";
    public static final String AUTHENTICATION_URI = "/authenticate";

    protected MockMvc mvc;
    @Autowired
    WebApplicationContext webApplicationContext;

    @MockBean
    protected AuthenticationManager authenticationManager;

    @Before
    public void setUp()
    {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }


    @Test
    public void authenticate_success() throws Exception
    {

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(AUTHENTICATION_URI)
            .contentType(MediaType.APPLICATION_JSON_VALUE).content(super.loadDataFile(AUTHENTICATE_JSON))).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(HttpStatus.OK.value(), status);
        String content = mvcResult.getResponse().getContentAsString();
        assertThat(content, startsWith("{\"token\":\""));
        assertThat(content, endsWith("\"}"));
    }

    @Test
    public void authenticate_bad_credentials() throws Exception
    {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(AUTHENTICATION_URI)
            .contentType(MediaType.APPLICATION_JSON_VALUE).content("{\"username\": \"readonly\",\"password\": \"123}")).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(HttpStatus.BAD_REQUEST.value(), status);
        String content = mvcResult.getResponse().getContentAsString();
        assertEquals( "", content);
    }

    @Test
    public void authenticate_invalid_user() throws Exception
    {
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(AUTHENTICATION_URI)
                                              .contentType(MediaType.APPLICATION_JSON_VALUE).content("{\"username\": \"invalid\",\"password\": \"readonly}")).andReturn();

        int status = mvcResult.getResponse().getStatus();
        assertEquals(HttpStatus.BAD_REQUEST.value(), status);
        String content = mvcResult.getResponse().getContentAsString();
        assertEquals( "", content);
    }
}
