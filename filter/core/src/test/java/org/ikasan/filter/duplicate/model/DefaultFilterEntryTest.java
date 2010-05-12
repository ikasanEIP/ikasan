/*
 * $Id$
 * $URL$
 * 
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * Copyright (c) 2003-2010 Mizuho International plc. and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the 
 * Free Software Foundation Europe e.V. Talstrasse 110, 40217 Dusseldorf, Germany 
 * or see the FSF site: http://www.fsfeurope.org/.
 * ====================================================================
 */
package org.ikasan.filter.duplicate.model;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for {@link DefaultFilterEntry} focusing on 
 * testing eqauls-hashCode contract
 * 
 * @author Summer
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
