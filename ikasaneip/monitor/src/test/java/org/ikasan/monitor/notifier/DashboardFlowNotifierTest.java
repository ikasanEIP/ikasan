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
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.ikasan.dashboard.DashboardRestServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.mock.env.MockEnvironment;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.ikasan.spec.dashboard.DashboardRestService.DASHBOARD_BASE_URL_PROPERTY;
import static org.ikasan.spec.dashboard.DashboardRestService.DASHBOARD_EXTRACT_ENABLED_PROPERTY;

/**
 * This test class supports the <code>DashboardNotifier</code> class.
 *
 * @author Ikasan Development Team
 */
public class DashboardFlowNotifierTest
{
    DashboardFlowNotifier uut;

    @RegisterExtension public WireMockExtension wireMockRule = WireMockExtension.newInstance().options(WireMockConfiguration.options().dynamicPort()).build();

    private String FLOW_STATES_CACHE_PATH = "/rest/flowStates/cache";

    private MockEnvironment environment = new MockEnvironment();

    @BeforeEach
    void setup()
    {
        String dashboardBaseUrl = "http://localhost:" + wireMockRule.port() ;
        environment.setProperty(DASHBOARD_EXTRACT_ENABLED_PROPERTY, "true");
        environment.setProperty(DASHBOARD_BASE_URL_PROPERTY, dashboardBaseUrl);
        uut = new DashboardFlowNotifier(new DashboardRestServiceImpl(environment, new HttpComponentsClientHttpRequestFactory(), FLOW_STATES_CACHE_PATH));
    }

    @Test
    void test_running(){

        stubFor(put(urlEqualTo(FLOW_STATES_CACHE_PATH))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("running")));

        uut.invoke("dev","sampleModule","flowName","running");
    }

    @Test
    void test_running_whenFlow_name_has_space(){

        stubFor(put(urlEqualTo(FLOW_STATES_CACHE_PATH))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("running")));

        uut.invoke("dev","sampleModule","flow Name","running");

    }

    @Test
    void test_when_rest_returns_404(){

        stubFor(put(urlEqualTo(FLOW_STATES_CACHE_PATH))
            .willReturn(aResponse()
                .withStatus(404)
                .withHeader("Content-Type", "application/json")
                .withBody("Bad Request")));


        uut.invoke("dev","sampleModule","flowName","running");

        // fails silently
    }

    @Test
    void test_when_rest_returns_500(){

        stubFor(put(urlEqualTo(FLOW_STATES_CACHE_PATH))
            .willReturn(aResponse()
                .withStatus(500)
                .withHeader("Content-Type", "application/json")
                .withBody("Bad Request")));


        uut.invoke("dev","sampleModule","flowName","running");

        // fails silently
    }
}