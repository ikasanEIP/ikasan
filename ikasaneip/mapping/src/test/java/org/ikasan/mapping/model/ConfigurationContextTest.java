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

import org.junit.Ignore;
import org.junit.Test;


/**
 * @author Ikasan Development Team
 *
 */
@Ignore
public class ConfigurationContextTest
{

    /**
     * Equals contract: x.equals(x)
     */
    @Test public void equals_is_reflexive()
    {
        MappingConfiguration configurationContext = new MappingConfiguration();
        Assert.assertTrue(configurationContext.equals(configurationContext));
        Assert.assertTrue(configurationContext.hashCode() == configurationContext.hashCode());
    }

    /**
     * Equals contract: x.equals(y) iff y.equals(x)
     */
    @Test public void equals_is_symmetric()
    {
//        MappingConfiguration configurationContextA = new MappingConfiguration();
//        configurationContextA.setConfigurationTypeId(new Long(1));
//        configurationContextA.setNumberOfParams(new Long(1));
//        configurationContextA.setSourceContextId(new Long(1));
//        configurationContextA.setTargetContextId(new Long(2));
//        MappingConfiguration configurationContextB = new MappingConfiguration();
//        configurationContextB.setConfigurationTypeId(new Long(1));
//        configurationContextB.setNumberOfParams(new Long(1));
//        configurationContextB.setSourceContextId(new Long(1));
//        configurationContextB.setTargetContextId(new Long(2));
//
//        Assert.assertTrue(configurationContextA.equals(configurationContextB));
//        Assert.assertTrue(configurationContextA.hashCode() == configurationContextB.hashCode());
//        Assert.assertTrue(configurationContextB.equals(configurationContextA));
//        Assert.assertTrue(configurationContextB.hashCode() == configurationContextA.hashCode());
//
//        configurationContextB = new MappingConfiguration();
//        configurationContextB.setConfigurationTypeId(new Long(2));
//        configurationContextB.setNumberOfParams(new Long(2));
//        configurationContextB.setSourceContextId(new Long(3));
//        configurationContextB.setTargetContextId(new Long(4));
//
//
//        Assert.assertFalse(configurationContextA.equals(configurationContextB));
//        Assert.assertFalse(configurationContextA.hashCode() == configurationContextB.hashCode());
//        Assert.assertFalse(configurationContextB.equals(configurationContextA));
//        Assert.assertFalse(configurationContextB.hashCode() == configurationContextA.hashCode());
    }

//    /**
//     * Equals contract: x.equals(y) && y.equals(x), then x.equals(z)
//     */
//    @Test public void equals_is_transitive()
//    {
//        MappingConfiguration configurationContextA = new MappingConfiguration();
//        configurationContextA.setConfigurationTypeId(new Long(1));
//        configurationContextA.setNumberOfParams(new Long(1));
//        configurationContextA.setSourceSystem("sourceSystem");
//        configurationContextA.setTargetSystem("targetSystem");
//        MappingConfiguration configurationContextB = new MappingConfiguration();
//        configurationContextB.setConfigurationTypeId(new Long(1));
//        configurationContextB.setNumberOfParams(new Long(1));
//        configurationContextB.setSourceSystem("sourceSystem");
//        configurationContextB.setTargetSystem("targetSystem");
//        MappingConfiguration configurationContextC = new MappingConfiguration();
//        configurationContextC.setConfigurationTypeId(new Long(1));
//        configurationContextC.setNumberOfParams(new Long(1));
//        configurationContextC.setSourceSystem("sourceSystem");
//        configurationContextC.setTargetSystem("targetSystem");
//
//        Assert.assertTrue(configurationContextA.equals(configurationContextB));
//        Assert.assertTrue(configurationContextA.hashCode() == configurationContextB.hashCode());
//        Assert.assertTrue(configurationContextB.equals(configurationContextC));
//        Assert.assertTrue(configurationContextB.hashCode() == configurationContextC.hashCode());
//        Assert.assertTrue(configurationContextA.equals(configurationContextC));
//        Assert.assertTrue(configurationContextA.hashCode() == configurationContextC.hashCode());
//
//        configurationContextA = new MappingConfiguration();
//        configurationContextA.setConfigurationTypeId(new Long(2));
//        configurationContextA.setNumberOfParams(new Long(2));
//        configurationContextA.setSourceSystem("sourceSystemB");
//        configurationContextA.setTargetSystem("targetSystemB");
//
//        Assert.assertFalse(configurationContextA.equals(configurationContextB));
//        Assert.assertFalse(configurationContextA.hashCode() == configurationContextB.hashCode());
//        Assert.assertTrue(configurationContextB.equals(configurationContextC));
//        Assert.assertTrue(configurationContextB.hashCode() == configurationContextC.hashCode());
//        Assert.assertFalse(configurationContextA.equals(configurationContextC));
//        Assert.assertFalse(configurationContextA.hashCode() == configurationContextC.hashCode());
//    }
}
