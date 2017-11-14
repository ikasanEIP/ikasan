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
package org.ikasan.builder.component.filter;

import org.ikasan.filter.duplicate.model.FilterEntryConverter;
import org.ikasan.filter.duplicate.service.DuplicateFilterService;
import org.ikasan.spec.component.filter.Filter;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertTrue;

/**
 * This test class supports the <code>MessageFilterBuilderImpl</code> class.
 *
 * @author Ikasan Development Team
 */
public class MessageFilterBuilderImplTest {
    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery() {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    @Rule
    public ExpectedException thrown= ExpectedException.none();

    /**
     * Mocks
     */
    final DuplicateFilterService duplicateFilterService = mockery.mock(DuplicateFilterService.class, "mockDuplicateFilterService");
    final FilterEntryConverter filterEntryConverter = mockery.mock(FilterEntryConverter.class, "mockFilterEntryConverter");

    /**
     * Test successful builder creation.
     */
    @Test(expected = IllegalArgumentException.class)
    public void messageFilterBuilder_with_without_mandatory_configuration() {

        new MessageFilterBuilderImpl(duplicateFilterService).build();
    }

    /**
     * Test successful builder creation.
     */
    @Test
    public void messageFilterBuilder_with_minimum_configuration() {

        Filter filter = new MessageFilterBuilderImpl(duplicateFilterService)
                .setConfiguredResourceId("configuredResourceId")
                .setFilterEntryConverter(filterEntryConverter).build();

        assertTrue("instance should be a Filter", filter instanceof Filter);
        assertTrue("Filter configuredResourceId should be 'configuredResourceId'", "configuredResourceId".equals(((ConfiguredResource) filter).getConfiguredResourceId()));

        mockery.assertIsSatisfied();
    }

    /**
     * Test successful builder creation.
     */
    @Test
    public void messageFilterBuilder_with_configuration_instance_override() {

        Filter filter = new MessageFilterBuilderImpl(duplicateFilterService)
                .setConfiguredResourceId("configuredResourceId")
                .setConfiguration(new String("rule not configurable so this will be logged as ignored"))
                .setFilterEntryConverter(filterEntryConverter).build();

        assertTrue("instance should be a Filter", filter instanceof Filter);
        assertTrue("Filter configuredResourceId should be 'configuredResourceId'", "configuredResourceId".equals(((ConfiguredResource<Object>) filter).getConfiguredResourceId()));

        mockery.assertIsSatisfied();
    }

}
