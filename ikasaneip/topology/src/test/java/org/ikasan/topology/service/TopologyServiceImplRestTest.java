/*
 * $Id$  
 * $URL$
 * 
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * 
 * Distributed under the Modified BSD License.
 * Copyright notice: The copyright for this software and a full listing 
 * of individual contributors are as shown in the packaged copyright.txt 
 * file. 
 * 
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 *
 *  - Neither the name of the ORGANIZATION nor the names of its contributors may
 *    be used to endorse or promote products derived from this software without 
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE 
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE 
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 */
package org.ikasan.topology.service;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.apache.commons.io.IOUtils;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.topology.dao.TopologyDao;
import org.ikasan.topology.exception.DiscoveryException;
import org.ikasan.topology.model.Component;
import org.ikasan.topology.model.Flow;
import org.ikasan.topology.model.Module;
import org.ikasan.topology.model.Server;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.concurrent.Synchroniser;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.orm.hibernate4.HibernateTemplate;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

/**
 *
 * @author Ikasan Development Team
 *
 */
public class TopologyServiceImplRestTest
{

	private Mockery mockery = new Mockery() {{
		setImposteriser(ClassImposteriser.INSTANCE);
		setThreadingPolicy(new Synchroniser());
	}};
	private TopologyDao topologyDao = mockery.mock(TopologyDao.class);

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(WireMockConfiguration.options().dynamicPort()); // No-args constructor defaults to port 8080



    private TopologyService uut;


	/**
     * Before each test case, inject a mock {@link HibernateTemplate} to dao implementation
     * being tested
     */
    @Before
	public void setup()
    {

	    // Use custom TopologyServiceImpl to avoid duplication in testing
        uut = new TopologyServiceImpl(topologyDao){
            @Override
            public void discover(Server server, Module module, List<Flow> flows) throws DiscoveryException{
                //DO nothing Contents of this methods are test by different unitTest
            }
            @Override
            protected void cleanup(){
                //DO nothing Contents of this methods are test by different unitTest
            }
        };


    }

	@Test
	public void discoveryJustRESTCallsWhenModuleNameHasNoSpace() throws DiscoveryException, IOException, URISyntaxException
    {

        IkasanAuthentication ikasanAuthentication = mockery.mock(IkasanAuthentication.class);

        List<Module> modules =  Arrays.asList(
		 new Module("ModuleTest", "/contextRoot", "I am module 2","version", null, "diagram")
        );
		List<Server> servers = Arrays.asList(new Server("EAI","","http://localhost",wireMockRule.port()));

        stubFor(get(urlEqualTo("/contextRoot/rest/discovery/flows/ModuleTest"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(IOUtils.toString( getClass().getResourceAsStream("/data/discovery.json"), "UTF-8"))));

		mockery.checking(new Expectations() {{

            exactly(1).of(topologyDao).getAllServers();
            will(returnValue(servers));

            exactly(2).of(topologyDao).getAllModules();
            will(returnValue(modules));

            exactly(2).of(ikasanAuthentication).getName();
            will(returnValue(null));

            exactly(2).of(ikasanAuthentication).getCredentials();
            will(returnValue(null));

            exactly(1).of(topologyDao).save(with(any(Server.class)));

        }});

		//do test
		uut.discover(ikasanAuthentication);

		//assert dao calls
		mockery.assertIsSatisfied();

	}

	@Test
	public void discoveryJustRESTCallsWhenModuleNameHasSpace() throws DiscoveryException, IOException, URISyntaxException
    {
        // Use custom TopologyServiceImpl to avoid duplication in testing
        uut = new TopologyServiceImpl(topologyDao){
            @Override
            public void discover(Server server, Module module, List<Flow> flows) throws DiscoveryException{
                //DO nothing Contents of this methods are test by different unitTest
            }
            @Override
            protected void cleanup(){
                //DO nothing Contents of this methods are test by different unitTest
            }
        };

        IkasanAuthentication ikasanAuthentication = mockery.mock(IkasanAuthentication.class);

        List<Module> modules =  Arrays.asList(
		 new Module("Module Test", "/contextRoot", "I am module 2","version", null, "diagram")
        );
		List<Server> servers = Arrays.asList(new Server("EAI","","http://localhost",wireMockRule.port()));

        stubFor(get(urlEqualTo("/contextRoot/rest/discovery/flows/Module%20Test"))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(IOUtils.toString( getClass().getResourceAsStream("/data/discovery.json"), "UTF-8"))));

		mockery.checking(new Expectations() {{

            exactly(1).of(topologyDao).getAllServers();
            will(returnValue(servers));

            exactly(2).of(topologyDao).getAllModules();
            will(returnValue(modules));

            exactly(2).of(ikasanAuthentication).getName();
            will(returnValue(null));

            exactly(2).of(ikasanAuthentication).getCredentials();
            will(returnValue(null));

            exactly(1).of(topologyDao).save(with(any(Server.class)));

        }});

		//do test
		uut.discover(ikasanAuthentication);

		//assert dao calls
		mockery.assertIsSatisfied();

	}

    @Test
    public void discoveryJustRESTCallsWhenRESTReturns500() throws DiscoveryException, IOException, URISyntaxException
    {

        IkasanAuthentication ikasanAuthentication = mockery.mock(IkasanAuthentication.class);

        List<Module> modules =  Arrays.asList(
            new Module("ModuleTest", "/contextRoot", "I am module 2","version", null, "diagram")
        );
        List<Server> servers = Arrays.asList(new Server("EAI","","http://localhost",wireMockRule.port()));

        stubFor(get(urlEqualTo("/contextRoot/rest/discovery/flows/ModuleTest"))
            .willReturn(aResponse()
                .withStatus(500)
                .withHeader("Content-Type", "application/json")
                .withBody("")));

        mockery.checking(new Expectations() {{

            exactly(1).of(topologyDao).getAllServers();
            will(returnValue(servers));

            exactly(2).of(topologyDao).getAllModules();
            will(returnValue(modules));

            exactly(2).of(ikasanAuthentication).getName();
            will(returnValue(null));

            exactly(2).of(ikasanAuthentication).getCredentials();
            will(returnValue(null));

        }});

        //do test
        uut.discover(ikasanAuthentication);

        //assert dao calls
        mockery.assertIsSatisfied();

    }

    @Test
    public void discoveryJustRESTCallsWhenRESTReturns400() throws DiscoveryException, IOException, URISyntaxException
    {

        IkasanAuthentication ikasanAuthentication = mockery.mock(IkasanAuthentication.class);

        List<Module> modules =  Arrays.asList(
            new Module("ModuleTest", "/contextRoot", "I am module 2","version", null, "diagram")
        );
        List<Server> servers = Arrays.asList(new Server("EAI","","http://localhost",wireMockRule.port()));

        stubFor(get(urlEqualTo("/contextRoot/rest/discovery/flows/ModuleTest"))
            .willReturn(aResponse()
                .withStatus(400)
                .withHeader("Content-Type", "application/json")
                .withBody("")));

        mockery.checking(new Expectations() {{

            exactly(1).of(topologyDao).getAllServers();
            will(returnValue(servers));

            exactly(2).of(topologyDao).getAllModules();
            will(returnValue(modules));

            exactly(2).of(ikasanAuthentication).getName();
            will(returnValue(null));

            exactly(2).of(ikasanAuthentication).getCredentials();
            will(returnValue(null));

        }});

        //do test
        uut.discover(ikasanAuthentication);

        //assert dao calls
        mockery.assertIsSatisfied();

    }

    @Test
    public void discoveryJustRESTCallsWhenRESTReturns401() throws DiscoveryException, IOException, URISyntaxException
    {

        IkasanAuthentication ikasanAuthentication = mockery.mock(IkasanAuthentication.class);

        List<Module> modules =  Arrays.asList(
            new Module("ModuleTest", "/contextRoot", "I am module 2","version", null, "diagram")
        );
        List<Server> servers = Arrays.asList(new Server("EAI","","http://localhost",wireMockRule.port()));

        stubFor(get(urlEqualTo("/contextRoot/rest/discovery/flows/ModuleTest"))
            .willReturn(aResponse()
                .withStatus(401)
                .withHeader("Content-Type", "application/json")
                .withBody("")));

        mockery.checking(new Expectations() {{

            exactly(1).of(topologyDao).getAllServers();
            will(returnValue(servers));

            exactly(2).of(topologyDao).getAllModules();
            will(returnValue(modules));

            exactly(2).of(ikasanAuthentication).getName();
            will(returnValue(null));

            exactly(2).of(ikasanAuthentication).getCredentials();
            will(returnValue(null));

        }});

        //do test
        uut.discover(ikasanAuthentication);

        //assert dao calls
        mockery.assertIsSatisfied();

    }
}
