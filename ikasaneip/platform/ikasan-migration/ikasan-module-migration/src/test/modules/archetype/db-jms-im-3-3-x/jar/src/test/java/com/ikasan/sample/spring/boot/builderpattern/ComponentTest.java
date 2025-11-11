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
package com.ikasan.sample.spring.boot.builderpattern;

import com.ikasan.sample.person.model.Person;
import org.ikasan.spec.component.filter.Filter;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * This test class supports the <code>ComponentFactory</code> class.
 *
 * @author Ikasan Development Team
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { Application.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(locations = {
        "/transaction-pointcut-components-on-test.xml"
})
public class ComponentTest
{
    @Resource
    ComponentFactory componentFactory;

    final String personXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><person><id>1</id><name>ikasan</name><dobDayOfMonth>6</dobDayOfMonth><dobMonthOfYear>7</dobMonthOfYear><dobYear>2005</dobYear></person>";

    Person person;

    @Before
    public void setup()
    {
        person = new Person();
        person.setId(1);
        person.setName("ikasan");
        person.setDobDayOfMonth(6);
        person.setDobMonthOfYear(7);
        person.setDobYear(2005);
    }

    @Test
    @DirtiesContext
    public void test_objectToXmlStringConverter() throws Exception
    {
        Converter converter = componentFactory.getObjectToXmlStringConverter();
        Assert.assertEquals(personXml, converter.convert(person));
    }

    @Test
    @DirtiesContext
    public void test_xmlToObjectConverter() throws Exception
    {
        Converter<String,Person> converter = componentFactory.getXmlToObjectConverter();
        Person person = converter.convert(personXml);

        Assert.assertEquals(person.getId(), 1);
        Assert.assertEquals(person.getName(), "ikasan");
        Assert.assertEquals(person.getDobDayOfMonth(), 6);
        Assert.assertEquals(person.getDobMonthOfYear(), 7);
        Assert.assertEquals(person.getDobYear(), 2005);
    }

    @Test
    @DirtiesContext
    public void test_filter() throws Exception
    {
        List<Person> persons = new ArrayList<Person>();
        persons.add(person);

        Person elderlyPerson = new Person();
        elderlyPerson.setDobYear(1900);
        elderlyPerson.setDobMonthOfYear(7);
        elderlyPerson.setDobDayOfMonth(6);
        persons.add(elderlyPerson);

        Filter<List<Person>> filter = componentFactory.getFilter();
        Assert.assertTrue("Filter should not have removed a person", filter.filter(persons).size() == 2 );

        // change filter age criteria and rerun
        ((ConfiguredResource<MyFilterConfiguration>)filter).getConfiguration().setAgeRestriction(100);
        Assert.assertTrue("Filter should have removed a person", filter.filter(persons).size() == 1 );

        // change filter age criteria and rerun
        ((ConfiguredResource<MyFilterConfiguration>)filter).getConfiguration().setAgeRestriction(500);
        Assert.assertNull("Filter should have removed all persons", filter.filter(persons) );
    }

}
