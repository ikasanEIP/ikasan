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
package org.ikasan.testharness.flow.comparator.service;

import java.util.HashMap;
import java.util.Map;

import org.ikasan.testharness.flow.comparator.ExpectationComparator;
import org.ikasan.testharness.flow.comparator.model.FlowElementComparator;
import org.ikasan.testharness.flow.comparator.model.IgnoreComparator;
import org.ikasan.testharness.flow.expectation.model.EndpointComponent;
import org.ikasan.testharness.flow.expectation.model.IgnoreExpectation;
import org.ikasan.testharness.flow.expectation.model.RouterComponent;
import org.ikasan.testharness.flow.expectation.model.SequencerComponent;
import org.ikasan.testharness.flow.expectation.model.TransformerComponent;

/**
 * ComparatorService default implementation provides six default comparators
 * for all component types and event by mapping the expectation class to
 * a comparator instance.
 * 
 * User may define additional comparators by providing the expectation class
 * and associated comparator.
 * 
 * @author Ikasan Development Team
 * 
 */
@SuppressWarnings("serial")
public class ComparatorServiceImpl 
    implements ComparatorService<Class<?>>
{
    // default comparators with their shipped classes
    private Map<Class<?>,ExpectationComparator<?,?>> defaultComparators = new HashMap<Class<?>,ExpectationComparator<?,?>>()
    {{
        put(IgnoreExpectation.class, new IgnoreComparator());
        put(TransformerComponent.class, new FlowElementComparator());
        put(RouterComponent.class, new FlowElementComparator());
        put(SequencerComponent.class, new FlowElementComparator());
        put(EndpointComponent.class, new FlowElementComparator());
    }};

    /**
     * Utility method for finding the comparator for an expectation class.
     * @param cls
     * @return
     */
    public ExpectationComparator<?,?> getComparator(Class<?> cls)
    {
        ExpectationComparator<?,?> expectationComparator = this.defaultComparators.get(cls);
        if(expectationComparator == null)
        {
            throw new RuntimeException("No ExpectationComparators were found for expectation class["
                    + cls.getName() + "]");
        }
        
        return expectationComparator;
    }

}
