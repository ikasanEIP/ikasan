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
package org.ikasan.mapping.model;

import junit.framework.Assert;

import org.ikasan.mapping.model.ConfigurationType;
import org.junit.Ignore;
import org.junit.Test;



/**
 * @author Ikasan Development Team
 *
 */
@Ignore
public class ConfigurationTypeTest
{

    /**
     * Equals contract: x.equals(x)
     */
    @Test public void equals_is_reflexive()
    {
        ConfigurationType configurationType = new ConfigurationType();
        Assert.assertTrue(configurationType.equals(configurationType));
        Assert.assertTrue(configurationType.hashCode() == configurationType.hashCode());
    }

    /**
     * Equals contract: x.equals(y) iff y.equals(x)
     */
    @Test public void equals_is_symmetric()
    {
        ConfigurationType configurationTypeA = new ConfigurationType();
        configurationTypeA.setName("name");
        ConfigurationType configurationTypeB = new ConfigurationType();
        configurationTypeB.setName("name");

        Assert.assertTrue(configurationTypeA.equals(configurationTypeB));
        Assert.assertTrue(configurationTypeA.hashCode() == configurationTypeB.hashCode());
        Assert.assertTrue(configurationTypeB.equals(configurationTypeA));
        Assert.assertTrue(configurationTypeB.hashCode() == configurationTypeA.hashCode());

        configurationTypeB = new ConfigurationType();
        configurationTypeB.setName("other_name");


        Assert.assertFalse(configurationTypeA.equals(configurationTypeB));
        Assert.assertFalse(configurationTypeA.hashCode() == configurationTypeB.hashCode());
        Assert.assertFalse(configurationTypeB.equals(configurationTypeA));
        Assert.assertFalse(configurationTypeB.hashCode() == configurationTypeA.hashCode());
    }

    /**
     * Equals contract: x.equals(y) && y.equals(x), then x.equals(z)
     */
    @Test public void equals_is_transitive()
    {
        ConfigurationType configurationTypeA = new ConfigurationType();
        configurationTypeA.setName("name");
        ConfigurationType configurationTypeB = new ConfigurationType();
        configurationTypeB.setName("name");
        ConfigurationType configurationTypeC = new ConfigurationType();
        configurationTypeC.setName("name");

        Assert.assertTrue(configurationTypeA.equals(configurationTypeB));
        Assert.assertTrue(configurationTypeA.hashCode() == configurationTypeB.hashCode());
        Assert.assertTrue(configurationTypeB.equals(configurationTypeC));
        Assert.assertTrue(configurationTypeB.hashCode() == configurationTypeC.hashCode());
        Assert.assertTrue(configurationTypeA.equals(configurationTypeC));
        Assert.assertTrue(configurationTypeA.hashCode() == configurationTypeC.hashCode());

        configurationTypeA = new ConfigurationType();
        configurationTypeA.setName("other_name");

        Assert.assertFalse(configurationTypeA.equals(configurationTypeB));
        Assert.assertFalse(configurationTypeA.hashCode() == configurationTypeB.hashCode());
        Assert.assertTrue(configurationTypeB.equals(configurationTypeC));
        Assert.assertTrue(configurationTypeB.hashCode() == configurationTypeC.hashCode());
        Assert.assertFalse(configurationTypeA.equals(configurationTypeC));
        Assert.assertFalse(configurationTypeA.hashCode() == configurationTypeC.hashCode());
    }
}
