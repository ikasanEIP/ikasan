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
package org.ikasan.builder.invoker;

import org.ikasan.flow.visitorPattern.invoker.FilterInvokerConfiguration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This test class supports the <code>FilterInvokerConfigurationBuilder</code> class.
 *
 * @author Ikasan Development Team
 */
class FilterInvokerConfigurationBuilderTest
{
    /**
     * Test successful builder.
     */
    @Test
    void FilterInvokerConfigurationBuilder_test_properties_setters()
    {
        FilterInvokerConfigurationBuilder ficb = new FilterInvokerConfigurationBuilder(new FilterInvokerConfiguration());

        assertEquals(5, TestUtils.getFields(FilterInvokerConfiguration.class).size(), "FilterInvokerConfiguration should have 2 properties");
        assertFalse(ficb.withDynamicConfiguration(false).build().isDynamicConfiguration(), "FilterInvokerConfiguration should be false");
        assertTrue(ficb.withDynamicConfiguration(true).build().isDynamicConfiguration(), "FilterInvokerConfiguration should be true");
        assertFalse(ficb.withApplyFilter(false).build().isApplyFilter(), "FilterInvokerConfiguration should be false");
        assertTrue(ficb.withApplyFilter(true).build().isApplyFilter(), "FilterInvokerConfiguration should be true");
        assertFalse(ficb.withLogFiltered(false).build().isLogFiltered(), "FilterInvokerConfiguration should be false");
        assertTrue(ficb.withLogFiltered(true).build().isLogFiltered(), "FilterInvokerConfiguration should be true");
        assertTrue(ficb.withCaptureMetrics(true).build().getCaptureMetrics(), "FilterInvokerConfiguration should be true");
        assertFalse(ficb.withCaptureMetrics(false).build().getCaptureMetrics(), "FilterInvokerConfiguration should be false");
        assertTrue(ficb.withSnapMetricsEvent(true).build().getSnapEvent(), "FilterInvokerConfiguration should be true");
        assertFalse(ficb.withSnapMetricsEvent(false).build().getSnapEvent(), "FilterInvokerConfiguration should be false");

    }

}
