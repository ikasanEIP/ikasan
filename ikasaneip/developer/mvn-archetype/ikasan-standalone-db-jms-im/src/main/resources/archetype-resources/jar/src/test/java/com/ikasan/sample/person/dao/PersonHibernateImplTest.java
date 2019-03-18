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
package com.ikasan.sample.person.dao;

import com.ikasan.sample.person.model.Person;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * This test class supports the <code>PersonHibernateImpl</code> class.
 *
 * @author Ikasan Development Team
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { com.ikasan.sample.spring.boot.builderpattern.Application.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(locations = {
        "/transaction-pointcut-components-on-test.xml"
})
public class PersonHibernateImplTest
{
    @Resource
    PersonDao personDao;

    @Before
    public void setup()
    {
        Person person = new Person();
        person.setId(1);
        person.setName("ikasan");
        person.setDobDayOfMonth(6);
        person.setDobMonthOfYear(7);
        person.setDobYear(2005);
        personDao.saveOrUpdate(person);
    }

    @After
    public void teardown()
    {
        List<Person> persons = personDao.findAll();
        for(Person person:persons)
        {
            personDao.delete(person);
        }
    }

    @Test
    @DirtiesContext
    public void test_dao_getAll()
    {
        List<Person> people = personDao.findAll();
        Assert.assertTrue("There should be one person ", people.size() == 1);
        Person person = people.get(0);
        Assert.assertTrue("person id should be " + person.getId(), 1l == person.getId());
        Assert.assertTrue("person name should be ikasan", "ikasan".equals(person.getName()));
        Assert.assertTrue("person dayOfMonth should be 6", 6 == person.getDobDayOfMonth());
        Assert.assertTrue("person monthOfYear should be 7", 7 == person.getDobMonthOfYear());
        Assert.assertTrue("person year should be 2005", 2005 == person.getDobYear());
    }

    @Test
    @DirtiesContext
    public void test_dao_getPerson()
    {
        Person person = personDao.findById(1l);
        Assert.assertTrue("person id should be " + person.getId(), 1l == person.getId());
        Assert.assertTrue("person name should be ikasan", "ikasan".equals(person.getName()));
        Assert.assertTrue("person dayOfMonth should be 6", 6 == person.getDobDayOfMonth());
        Assert.assertTrue("person monthOfYear should be 7", 7 == person.getDobMonthOfYear());
        Assert.assertTrue("person year should be 2005", 2005 == person.getDobYear());
    }

    @Test
    @DirtiesContext
    public void test_dao_save()
    {
        Person person = new Person();
        person.setId(2);
        person.setName("New Person");
        personDao.saveOrUpdate(person);

        Person updatedPerson = personDao.findById(2);
        Assert.assertTrue("updated person id should be " + updatedPerson.getId(), 2l == updatedPerson.getId());
        Assert.assertTrue("updated person name should be ikasan", "New Person".equals(updatedPerson.getName()));
        Assert.assertTrue("updated person dayOfMonth should be 0", 0 == updatedPerson.getDobDayOfMonth());
        Assert.assertTrue("updated person monthOfYear should be 0", 0 == updatedPerson.getDobMonthOfYear());
        Assert.assertTrue("updated person year should be 0", 0 == updatedPerson.getDobYear());
    }

    @Test
    @DirtiesContext
    public void test_dao_update()
    {
        Person person = personDao.findById(1l);
        person.setName("Person After Update");
        personDao.saveOrUpdate(person);

        Person updatedPerson = personDao.findById(1);
        Assert.assertTrue("updated person id should be " + updatedPerson.getId(), 1l == updatedPerson.getId());
        Assert.assertTrue("updated person name should be ikasan", "Person After Update".equals(updatedPerson.getName()));
        Assert.assertTrue("updated person dayOfMonth should be 6", 6 == person.getDobDayOfMonth());
        Assert.assertTrue("updated person monthOfYear should be 7", 7 == person.getDobMonthOfYear());
        Assert.assertTrue("updated person year should be 2005", 2005 == person.getDobYear());
    }
}
