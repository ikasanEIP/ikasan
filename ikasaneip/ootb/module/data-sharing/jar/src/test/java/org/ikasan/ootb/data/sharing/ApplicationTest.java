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
package org.ikasan.ootb.data.sharing;

import org.ikasan.ootb.data.sharing.module.Application;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.module.Module;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

/**
 * Integration test for the Application class with a focus on dynamic flow creation and lifecycle management.
 *
 * This class uses Spring Boot's testing framework to initialize and test the application context.
 * The primary purpose of the test is to validate the creation, starting, stopping, and state transitions
 * of various flows within a module. It ensures that the flows operate correctly in a dynamically created setup
 * and adhere to the expected lifecycle state transitions.
 *
 * Annotations:
 * - {@link RunWith}: Configures the class to use the SpringJUnit4ClassRunner for test execution.
 * - {@link SpringBootTest}: Sets up the application context for testing with specified properties.
 * - {@link ContextConfiguration}: Specifies additional configuration classes for the test context.
 *
 * Key Test Method:
 * - {@link test_create_module_start_and_stop_flow}: Validates the dynamic creation of flows, their ability to start,
 *   stop, and transition between different states.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {Application.class},
    properties = {"spring.main.allow-bean-definition-overriding=true"},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationTest {
    @Autowired
    private Module<Flow> moduleUnderTest;

    /**
     * A very simple test to confirm dynamic flows are created.
     */
    @Test
    @DirtiesContext
    public void test_create_module_start_and_stop_flow() throws Exception {
        Flow flow = moduleUnderTest.getFlow("DASHBOARD_1 Ikasan ESB Dashboard Wiretap Entity Consumer Flow");
        flow.start();
        assertEquals(Flow.RUNNING, flow.getState());

        flow.stop();
        assertEquals(Flow.STOPPED, flow.getState());

        flow = moduleUnderTest.getFlow("DASHBOARD_1 Ikasan ESB Dashboard Wiretap Entity Producer Flow");
        flow.start();
        assertEquals(Flow.RUNNING, flow.getState());

        flow.stop();
        assertEquals(Flow.STOPPED, flow.getState());

        flow = moduleUnderTest.getFlow("DASHBOARD_2 Ikasan ESB Dashboard Wiretap Entity Consumer Flow");
        flow.start();
        assertEquals(Flow.RUNNING, flow.getState());

        flow.stop();
        assertEquals(Flow.STOPPED, flow.getState());

        flow = moduleUnderTest.getFlow("DASHBOARD_2 Ikasan ESB Dashboard Wiretap Entity Producer Flow");
        flow.start();
        assertEquals(Flow.RUNNING, flow.getState());

        flow.stop();
        assertEquals(Flow.STOPPED, flow.getState());
    }
}
