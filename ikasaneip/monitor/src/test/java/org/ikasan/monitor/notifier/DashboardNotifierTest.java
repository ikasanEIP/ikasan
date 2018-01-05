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
package org.ikasan.monitor.notifier;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.apache.http.HttpHeaders;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;

/**
 * This test class supports the <code>DashboardNotifier</code> class.
 *
 * @author Ikasan Development Team
 */
public class DashboardNotifierTest
{
    DashboardNotifier uut;

    @Rule public WireMockRule wireMockRule = new WireMockRule(WireMockConfiguration.options().dynamicPort()); // No-args constructor defaults to port 8080

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setup()
    {
        String dashboardBaseUrl = "http://localhost:" + wireMockRule.port() ;
        uut = new DashboardNotifier();
        uut.setDashboardBaseUrl(dashboardBaseUrl);
    }

    @Test
    public void test_running(){

        stubFor(put(urlEqualTo("/rest/topologyCache/updateCache/sampleModule/flowName"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("sampleModule"))

            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("running")));

        uut.invoke("dev","sampleModule","flowName","running");

    }

    @Test
    public void test_running_whenFlow_name_has_space(){

        stubFor(put(urlEqualTo("/rest/topologyCache/updateCache/sampleModule/flow%20Name"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("sampleModule"))

            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("running")));

        uut.invoke("dev","sampleModule","flow Name","running");

    }

    @Test
    public void test_when_rest_returns_404(){

        stubFor(put(urlEqualTo("/rest/topologyCache/updateCache/sampleModule/flowName"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("sampleModule"))

            .willReturn(aResponse()
                .withStatus(404)
                .withHeader("Content-Type", "application/json")
                .withBody("Bad Request")));

        thrown.expect(RuntimeException.class);
        thrown.expectMessage("An exception occurred trying to notify the dashboard!");


        uut.invoke("dev","sampleModule","flowName","running");

    }

    @Test
    public void test_when_rest_returns_500(){

        stubFor(put(urlEqualTo("/rest/topologyCache/updateCache/sampleModule/flowName"))
            .withHeader(HttpHeaders.USER_AGENT, equalTo("sampleModule"))

            .willReturn(aResponse()
                .withStatus(500)
                .withHeader("Content-Type", "application/json")
                .withBody("Bad Request")));

        thrown.expect(RuntimeException.class);
        thrown.expectMessage("An exception occurred trying to notify the dashboard!");


        uut.invoke("dev","sampleModule","flowName","running");

    }
}