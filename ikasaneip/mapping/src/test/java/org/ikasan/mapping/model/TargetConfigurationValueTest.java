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

import org.ikasan.mapping.model.TargetConfigurationValue;
import org.junit.Ignore;
import org.junit.Test;



/**
 * @author Ikasan Development Team
 *
 */
@Ignore
public class TargetConfigurationValueTest
{
    /**
     * Equals contract: x.equals(x)
     */
    @Test public void equals_is_reflexive()
    {
        TargetConfigurationValue stateModelHistoryA = new TargetConfigurationValue();
        Assert.assertTrue(stateModelHistoryA.equals(stateModelHistoryA));
        Assert.assertTrue(stateModelHistoryA.hashCode() == stateModelHistoryA.hashCode());
    }

    /**
     * Equals contract: x.equals(y) iff y.equals(x)
     */
    @Test public void equals_is_symmetric()
    {
        TargetConfigurationValue stateModelHistoryA = new TargetConfigurationValue();
        stateModelHistoryA.setTargetSystemValue("a");
        TargetConfigurationValue stateModelHistoryB = new TargetConfigurationValue();
        stateModelHistoryB.setTargetSystemValue("a");
        
        Assert.assertTrue(stateModelHistoryA.equals(stateModelHistoryB));
        Assert.assertTrue(stateModelHistoryA.hashCode() == stateModelHistoryB.hashCode());
        Assert.assertTrue(stateModelHistoryB.equals(stateModelHistoryA));
        Assert.assertTrue(stateModelHistoryB.hashCode() == stateModelHistoryA.hashCode());

        stateModelHistoryB = new TargetConfigurationValue();
        stateModelHistoryB.setTargetSystemValue("b");

        Assert.assertFalse(stateModelHistoryA.equals(stateModelHistoryB));
        Assert.assertFalse(stateModelHistoryA.hashCode() == stateModelHistoryB.hashCode());
        Assert.assertFalse(stateModelHistoryB.equals(stateModelHistoryA));
        Assert.assertFalse(stateModelHistoryB.hashCode() == stateModelHistoryA.hashCode());
    }

    /**
     * Equals contract: x.equals(y) && y.equals(x), then x.equals(z)
     */
    @Test public void equals_is_transitive()
    {
        
        TargetConfigurationValue stateModelHistoryA = new TargetConfigurationValue();
        stateModelHistoryA.setTargetSystemValue("a");
        TargetConfigurationValue stateModelHistoryB = new TargetConfigurationValue();
        stateModelHistoryB.setTargetSystemValue("a");
        TargetConfigurationValue stateModelHistoryC = new TargetConfigurationValue();
        stateModelHistoryC.setTargetSystemValue("a");

        Assert.assertTrue(stateModelHistoryA.equals(stateModelHistoryB));
        Assert.assertTrue(stateModelHistoryA.hashCode() == stateModelHistoryB.hashCode());
        Assert.assertTrue(stateModelHistoryB.equals(stateModelHistoryC));
        Assert.assertTrue(stateModelHistoryB.hashCode() == stateModelHistoryC.hashCode());
        Assert.assertTrue(stateModelHistoryA.equals(stateModelHistoryC));
        Assert.assertTrue(stateModelHistoryA.hashCode() == stateModelHistoryC.hashCode());

        stateModelHistoryA = new TargetConfigurationValue();
        stateModelHistoryB.setTargetSystemValue("b");

        Assert.assertFalse(stateModelHistoryA.equals(stateModelHistoryB));
        Assert.assertFalse(stateModelHistoryA.hashCode() == stateModelHistoryB.hashCode());
        Assert.assertFalse(stateModelHistoryB.equals(stateModelHistoryC));
        Assert.assertFalse(stateModelHistoryB.hashCode() == stateModelHistoryC.hashCode());
        Assert.assertFalse(stateModelHistoryA.equals(stateModelHistoryC));
        Assert.assertFalse(stateModelHistoryA.hashCode() == stateModelHistoryC.hashCode());

        stateModelHistoryA = new TargetConfigurationValue();
        stateModelHistoryA.setTargetSystemValue("c");

        Assert.assertFalse(stateModelHistoryA.equals(stateModelHistoryB));
        Assert.assertFalse(stateModelHistoryA.hashCode() == stateModelHistoryB.hashCode());
        Assert.assertFalse(stateModelHistoryB.equals(stateModelHistoryC));
        Assert.assertFalse(stateModelHistoryB.hashCode() == stateModelHistoryC.hashCode());
        Assert.assertFalse(stateModelHistoryA.equals(stateModelHistoryC));
        Assert.assertFalse(stateModelHistoryA.hashCode() == stateModelHistoryC.hashCode());
    }

    /**
     * Equals contract: multiple invocations of x.equals(y) will always be true, unless
     * properties used to determine equality change. 
     */
    @Test public void equals_is_consistent()
    {
        TargetConfigurationValue stateModelHistoryA = new TargetConfigurationValue();
        TargetConfigurationValue stateModelHistoryB = new TargetConfigurationValue();

        Assert.assertTrue(stateModelHistoryA.equals(stateModelHistoryB));
        Assert.assertTrue(stateModelHistoryA.hashCode() == stateModelHistoryB.hashCode());
        Assert.assertTrue(stateModelHistoryB.equals(stateModelHistoryA));
        Assert.assertTrue(stateModelHistoryB.hashCode() == stateModelHistoryA.hashCode());

        stateModelHistoryA.setTargetSystemValue("a");
        stateModelHistoryB.setTargetSystemValue("b");

        Assert.assertFalse(stateModelHistoryA.equals(stateModelHistoryB));
        Assert.assertFalse(stateModelHistoryA.hashCode() == stateModelHistoryB.hashCode());
        Assert.assertFalse(stateModelHistoryB.equals(stateModelHistoryA));
        Assert.assertFalse(stateModelHistoryB.hashCode() == stateModelHistoryA.hashCode());

        // Cannot test changing equality property since their setters are
        // purposefully coded to be private!
    }

    /**
     * Equals contract: a Book instance is never equal to null
     */
    @Test public void nulls_are_not_equal_to_any_history_instance()
    {
        TargetConfigurationValue stateModelHistoryA = new TargetConfigurationValue();
        Assert.assertFalse(stateModelHistoryA.equals(null));
    }

    /**
     * Equals contract: comparing a Book against an object of a different
     * type will always return false
     */
    @Test public void a_history_is_NOT_equal_to_an_object_of_different_type()
    {
        TargetConfigurationValue stateModelHistoryA = new TargetConfigurationValue();
        Assert.assertFalse(stateModelHistoryA.equals("Not a StateModelHistory!"));
    }
}
