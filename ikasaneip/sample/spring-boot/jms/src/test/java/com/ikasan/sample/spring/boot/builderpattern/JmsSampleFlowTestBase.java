/*
 *  ====================================================================
 *  Ikasan Enterprise Integration Platform
 *
 *  Distributed under the Modified BSD License.
 *  Copyright notice: The copyright for this software and a full listing
 *  of individual contributors are as shown in the packaged copyright.txt
 *  file.
 *
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 *   - Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *
 *   - Neither the name of the ORGANIZATION nor the names of its contributors may
 *     be used to endorse or promote products derived from this software without
 *     specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 *  AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 *  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 *  FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 *  DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 *  SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 *  CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 *  OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 *  USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  ====================================================================
 *
 */

package com.ikasan.sample.spring.boot.builderpattern;

import jakarta.jms.JMSException;
import org.ikasan.spec.error.reporting.ErrorReportingService;
import org.ikasan.spec.error.reporting.ErrorReportingServiceFactory;
import org.ikasan.spec.exclusion.ExclusionManagementService;
import org.ikasan.spec.flow.Flow;
import org.ikasan.spec.hospital.service.HospitalService;
import org.ikasan.spec.module.Module;
import org.ikasan.testharness.flow.database.DatabaseHelper;
import org.ikasan.testharness.flow.jms.ActiveMqHelper;
import org.ikasan.testharness.flow.jms.BrowseMessagesOnQueueVerifier;
import org.ikasan.testharness.flow.rule.IkasanFlowTestRule;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jms.core.JmsTemplate;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.with;
import static org.junit.Assert.assertEquals;

/**
 * This test class supports the <code>JmsSampleFlow</code> class.
 *
 * @author Ikasan Development Team
 */
public abstract class JmsSampleFlowTestBase {
    protected final static String MODULE_REST_USERNAME_PROPERTY = "rest.module.username";
    protected final static String MODULE_REST_PASSWORD_PROPERTY = "rest.module.password";

    protected static String SAMPLE_MESSAGE = "Hello world!";

    protected Logger logger = LoggerFactory.getLogger(JmsSampleFlowTestBase.class);

    @Rule
    public TestName name = new TestName();

    @Resource
    protected Module<Flow> moduleUnderTest;

    @Value("${jms.provider.url}")
    protected String brokerUrl;

    @Resource
    protected ErrorReportingServiceFactory errorReportingServiceFactory;

    @Resource
    protected HospitalService hospitalService;

    protected ErrorReportingService errorReportingService;

    @Resource
    protected ExclusionManagementService exclusionManagementService;

    protected IkasanFlowTestRule flowTestRule;

    @Resource
    @Autowired
    @Qualifier("ikasan.xads")
    protected DataSource ikasanxads;

    @LocalServerPort
    protected int randomServerPort;

    protected BrowseMessagesOnQueueVerifier browseMessagesOnQueueVerifier;

    /**
     * On retry the original message is rolled back - this leaves the message on the consumer destination, this can interfere
     * with tests that follow if they are waiting for messages to be *produced* on that destination -
     * contrary to popular belief the AMQBroker is outside the control of Spring
     * so there is no AMQ restart between tests regardless what DirtiesContext is set to.
     *
     * @throws JMSException
     */
    protected void removeAllMessages() throws Exception {
        new ActiveMqHelper().removeAllMessages();
    }

    /**
     * Clears the database by removing all data from specified tables between tests.
     * This method uses an instance of DatabaseHelper to clear the database tables.
     * @throws SQLException If an SQL exception occurs while clearing the database
     */
    protected void clearDatabase() throws SQLException {
        new DatabaseHelper(ikasanxads).clearDatabase();
    }

    /**
     * Resets the Exception Generating Broker associated with the flow test rule. This method
     * retrieves the Exception Generating Broker component from the flowTestRule and calls its reset
     * method to clear any configured exception settings.
     */
    protected void resetExceptionGeneratingBroker() {
        ExceptionGeneratingBroker exceptionGeneratingBroker = (ExceptionGeneratingBroker) flowTestRule
            .getComponent("Exception Generating Broker");
        exceptionGeneratingBroker.reset();
    }

    /**
     * Resets the delay of the DelayGenerationBroker component associated with the flow test rule.
     * This method retrieves the DelayGenerationBroker component from the flowTestRule using the component name
     * "Delay Generating Broker" and calls its reset method to set the broker delay to 0.
     */
    protected void resetDelayGeneratingBroker(){
        DelayGenerationBroker delayGenerationBroker = (DelayGenerationBroker) flowTestRule
            .getComponent("Delay Generating Broker");
        delayGenerationBroker.reset();
    }

    /**
     * Asserts that the number of errors found by the errorReportingService matches the expected number
     * within a specified time frame.
     *
     * @param expectedNumberOfErrors the expected number of errors to be found
     */
    protected void assertErrorsWithWait(int expectedNumberOfErrors) {
        with().pollInterval(50, TimeUnit.MILLISECONDS).and().await().atMost(10, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                List<Object> errors = errorReportingService.find(null, null, null, null, null, 100);
                assertEquals(expectedNumberOfErrors, errors.size());
            });
    }

    /**
     * Restarts the ActiveMQ broker by shutting it down and then starting it again.
     * This method uses an ActiveMqHelper instance to interact with the broker.
     */
    protected void restartBroker() {
        ActiveMqHelper helper = new ActiveMqHelper();
        helper.shutdownBroker();
        helper.startBroker();
    }

    /**
     * Asserts that the number of exclusions found by the exclusionManagementService matches the expected number.
     *
     * @param expectedNumberOfExclusions the expected number of exclusions to be found
     */
    protected void assertExclusionsWithWait(int expectedNumberOfExclusions) {
        with().pollInterval(50, TimeUnit.MILLISECONDS).and().await().atMost(10, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                List<Object> exclusions = exclusionManagementService.find(null, null, null, null, null, 100);
                assertEquals(expectedNumberOfExclusions, exclusions.size());
            });
    }
}
