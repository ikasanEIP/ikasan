/*
 * $Id: SchedulerFactoryTest.java 3629 2011-04-18 10:00:52Z mitcje $
 * $URL: http://open.jira.com/svn/IKASAN/branches/ikasaneip-0.9.x/scheduler/src/test/java/org/ikasan/scheduler/SchedulerFactoryTest.java $
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
package com.ikasan.sample.person.model;

import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * This test class supports the <code>Person</code> class.
 *
 * @author Ikasan Development Team
 */
public class PersonTest
{
    @Test
    public void test_person_default_attributes()
    {
        Person person = new Person();

        Assert.assertTrue("person id should be 0", 0l == person.getId());
        Assert.assertNull("person name should be 'null'", person.getName());
        Assert.assertTrue("person dobDayOfMonth should be 0", 0 == person.getDobDayOfMonth());
        Assert.assertTrue("person dobMonthOfYear should be 0", 0 == person.getDobMonthOfYear());
        Assert.assertTrue("person dobYear should be 0", 0 == person.getDobYear());
    }

    @Test
    public void test_person_mutators()
    {
        Person person = new Person();
        person.setId(1);
        person.setName("ikasan");
        person.setDobYear(2005);
        person.setDobMonthOfYear(7);
        person.setDobDayOfMonth(6);

        Assert.assertTrue("person id should be 1", 1l == person.getId());
        Assert.assertTrue("person name should be 'ikasan'", "ikasan".equals(person.getName()));
        Assert.assertTrue("person dobDayOfMonth should be 6", 6 == person.getDobDayOfMonth());
        Assert.assertTrue("person dobMonthOfYear should be 7", 7 == person.getDobMonthOfYear());
        Assert.assertTrue("person dobYear should be 2005", 2005 == person.getDobYear());
    }

    @Test
    public void test_person_getAge()
    {
        LocalDate dateOneYearAgo = LocalDate.now().minusYears(1);
        Person person = new Person();
        person.setId(1);
        person.setName("ikasan");
        person.setDobYear(dateOneYearAgo.getYear());
        person.setDobMonthOfYear(dateOneYearAgo.getMonthValue());
        person.setDobDayOfMonth(dateOneYearAgo.getDayOfMonth());

        Assert.assertTrue("person should be 1 dobYear old", 1 == person.getAge());
    }

    @Test
    public void test_person_equality()
    {
        Person person1 = new Person();
        person1.setId(1);

        Person person2 = new Person();

        // different people
        person2.setId(2);
        Assert.assertFalse( person1.equals(person2) );

        // same people
        person2.setId(1);
        Assert.assertTrue( person1.equals(person2) );
    }

    @Test
    public void test_person_hashcode()
    {
        Map<Person, String> people = new HashMap<Person,String>();

        Person person1 = new Person();

        // add a peron
        person1.setId(1);
        people.put(person1, "person1");
        Assert.assertTrue( people.size() == 1);

        // add another peron
        person1.setId(2);
        people.put(person1, "person2");
        Assert.assertTrue( people.size() == 2);

        // add repeat peron
        person1.setId(1);
        people.put(person1, "person3");
        Assert.assertTrue( people.size() == 2);
    }
}
