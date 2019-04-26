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
import org.junit.Assert;
import org.junit.Test;

/**
 * This test class supports the <code>FilterInvokerConfigurationBuilder</code> class.
 *
 * @author Ikasan Development Team
 */
public class FilterInvokerConfigurationBuilderTest
{
    /**
     * Test successful builder.
     */
    @Test
    public void FilterInvokerConfigurationBuilder_test_properties_setters()
    {
        FilterInvokerConfigurationBuilder ficb = new FilterInvokerConfigurationBuilder(new FilterInvokerConfiguration());

        Assert.assertTrue("FilterInvokerConfiguration should have 2 properties", TestUtils.getFields(FilterInvokerConfiguration.class).size() == 3);
        Assert.assertFalse("FilterInvokerConfiguration should be false", ficb.withDynamicConfiguration(false).build().isDynamicConfiguration());
        Assert.assertTrue("FilterInvokerConfiguration should be true", ficb.withDynamicConfiguration(true).build().isDynamicConfiguration());
        Assert.assertFalse("FilterInvokerConfiguration should be false", ficb.withApplyFilter(false).build().isApplyFilter());
        Assert.assertTrue("FilterInvokerConfiguration should be true", ficb.withApplyFilter(true).build().isApplyFilter());
        Assert.assertFalse("FilterInvokerConfiguration should be false", ficb.withLogFiltered(false).build().isLogFiltered());
        Assert.assertTrue("FilterInvokerConfiguration should be true", ficb.withLogFiltered(true).build().isLogFiltered());
    }

}
