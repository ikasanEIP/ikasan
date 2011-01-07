/*
 * $Id$
 * $URL$
 * 
 * =============================================================================
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
 * =============================================================================
 */
package org.ikasan.filter.duplicate.model;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for {@link DefaultFilterEntry} focusing on 
 * testing eqauls-hashCode contract
 * 
 * @author Ikasan Development Team
 *
 */
public class DefaultFilterEntryTest
{
    private DefaultFilterEntry firstEntry = new DefaultFilterEntry("firstEntry".hashCode(), "test-1", 1);
    private DefaultFilterEntry secondEntry = new DefaultFilterEntry("secondEntry".hashCode(), "test-2", 1);

    /**
     * Test case: For any two instances of {@link DefaultFilterEntry} A and B, such that<br>
     * <br>
     * &nbsp;&nbsp;&nbsp;&nbsp;<code>A.clientId != B.clientId <b>AND</b> A.criteria != B.criteria</code><br>
     * <br>
     * then <br>
     * <br>
     * &nbsp;&nbsp;&nbsp;&nbsp;<code>A.hashCode() != B.hashCode()</code> 
     */
    @Test public void test_hashCode_two_instances_different_clientid_and_critiera_must_have_different_hash()
    {
        Assert.assertTrue("Any two instances of DefaultFilterEntry with both clientId and criteria fields different must return different hashcode values",
                firstEntry.hashCode() != secondEntry.hashCode());
    }

    /**
     * Test case: For any two instances of {@link DefaultFilterEntry} A and B, such that<br>
     * <br>
     * &nbsp;&nbsp;&nbsp;&nbsp;<code>A.clientId == B.clientId <b>AND</b> A.criteria != B.criteria</code><br>
     * <br>
     * then <br>
     * <br>
     * &nbsp;&nbsp;&nbsp;&nbsp;<code>A.hashCode() != B.hashCode()</code> 
     */
    @Test public void test_hasCode_two_instances_same_clientid_different_criteria_must_have_different_hash()
    {
        DefaultFilterEntry differentCriteria = new DefaultFilterEntry("AnotherEntry".hashCode(), "test-1", 1);
        Assert.assertTrue("Any two instances of DefaultFilterEntry with the same clientId but different criteria fields different must return different hashcode values",
                firstEntry.hashCode() != differentCriteria.hashCode());
    }

    /**
     * Test case: For any two instances of {@link DefaultFilterEntry} A and B, such that<br>
     * <br>
     * &nbsp;&nbsp;&nbsp;&nbsp;<code>A.clientId != B.clientId <b>AND</b> A.criteria == B.criteria</code><br>
     * <br>
     * then <br>
     * <br>
     * &nbsp;&nbsp;&nbsp;&nbsp;<code>A.hashCode() != B.hashCode()</code> 
     */
    @Test public void test_hasCode_two_instances_different_clientid_same_criteria_must_have_different_hash()
    {
        DefaultFilterEntry differentClientId = new DefaultFilterEntry("firstEntry".hashCode(), "test-x", 1);
        Assert.assertTrue("Any two instances of DefaultFilterEntry with different clientId but same criteria fields different must return different hashcode values",
                firstEntry.hashCode() != differentClientId.hashCode());
    }

    /**
     * Test case: For any two instances of {@link DefaultFilterEntry} A and B, such that<br>
     * <br>
     * &nbsp;&nbsp;&nbsp;&nbsp;<code>A.clientId == B.clientId <b>AND</b> A.criteria == B.criteria</code><br>
     * <br>
     * then <br>
     * <br>
     * &nbsp;&nbsp;&nbsp;&nbsp;<code>A.hashCode() == B.hashCode()</code> 
     */
    @Test public void test_hashCode_two_instances_same_clientid_same_critiera_must_have_same_hash()
    {
        DefaultFilterEntry firstEntryDuplicate = new DefaultFilterEntry("firstEntry".hashCode(), "test-1", 1);
        Assert.assertTrue("Any two instances of DefaultFilterEntry with the same clientId and criteria fields must return the same hashcode value.",
                firstEntry.hashCode() == firstEntryDuplicate.hashCode());
    }

    /**
     * Test case: For any two instances of {@link DefaultFilterEntry} A and B, such that<br>
     * <br>
     * &nbsp;&nbsp;&nbsp;&nbsp;<code>A.clientId != B.clientId <b>AND</b> A.criteria != B.criteria</code><br>
     * <br>
     * then <br>
     * <br>
     * &nbsp;&nbsp;&nbsp;&nbsp;<code>A != B</code> 
     */
    @Test public void test_equals_two_instances_different_clientid_and_critiera_must_have_different_hash()
    {
        Assert.assertFalse("Any two instances of DefaultFilterEntry with both clientId and criteria fields different are not equal",
                firstEntry.equals(secondEntry));
    }

    /**
     * Test case: For any two instances of {@link DefaultFilterEntry} A and B, such that<br>
     * <br>
     * &nbsp;&nbsp;&nbsp;&nbsp;<code>A.clientId == B.clientId <b>AND</b> A.criteria != B.criteria</code><br>
     * <br>
     * then <br>
     * <br>
     * &nbsp;&nbsp;&nbsp;&nbsp;<code>A != B</code> 
     */
    @Test public void test_equals_two_instances_same_clientid_different_criteria_must_have_different_hash()
    {
        DefaultFilterEntry differentCriteria = new DefaultFilterEntry("AnotherEntry".hashCode(), "test-1", 1);
        Assert.assertFalse("Any two instances of DefaultFilterEntry with the same clientId but different criteria fields different are not equal",
                firstEntry.equals(differentCriteria));
    }

    /**
     * Test case: For any two instances of {@link DefaultFilterEntry} A and B, such that<br>
     * <br>
     * &nbsp;&nbsp;&nbsp;&nbsp;<code>A.clientId != B.clientId <b>AND</b> A.criteria == B.criteria</code><br>
     * <br>
     * then <br>
     * <br>
     * &nbsp;&nbsp;&nbsp;&nbsp;<code>A != B</code> 
     */
    @Test public void test_equals_two_instances_different_clientid_same_criteria_must_have_different_hash()
    {
        DefaultFilterEntry differentClientId = new DefaultFilterEntry("firstEntry".hashCode(), "test-x", 1);
        Assert.assertFalse("Any two instances of DefaultFilterEntry with different clientId but same criteria fields different are not equal",
                firstEntry.equals(differentClientId));
    }

    /**
     * Test case: For any two instances of {@link DefaultFilterEntry} A and B, such that<br>
     * <br>
     * &nbsp;&nbsp;&nbsp;&nbsp;<code>A.clientId == B.clientId <b>AND</b> A.criteria == B.criteria</code><br>
     * <br>
     * then <br>
     * <br>
     * &nbsp;&nbsp;&nbsp;&nbsp;<code>A == B</code> 
     */
    @Test public void test_equals_two_instances_same_clientid_same_critiera_must_have_same_hash()
    {
        DefaultFilterEntry firstEntryDuplicate = new DefaultFilterEntry("firstEntry".hashCode(), "test-1", 1);
        Assert.assertTrue("Any two instances of DefaultFilterEntry with the same clientId and criteria fields must return the same hashcode value.",
                firstEntry.equals(firstEntryDuplicate));
    }

    /**
     * Test case: The object reference to compare to is an instance of {@link String}. {@link DefaultFilterEntry#equals(Object)}
     * will throw a {@link ClassCastException}
     */
    @Test(expected=ClassCastException.class) public void test_equals_fails()
    {
        DefaultFilterEntry entry = new DefaultFilterEntry("firstEntry".hashCode(), "test", 1);
        entry.equals(new String("I am not an instance of DefaultFilterEntry"));
        Assert.fail("If the object reference with which to compare was not an instance of DefaultFilterEntry, ClassCastExpcetion should've been thrown.");
    }
}
