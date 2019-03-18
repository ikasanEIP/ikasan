package com.ikasan.sample.person.service;

import com.ikasan.sample.person.dao.PersonDao;
import com.ikasan.sample.person.model.Person;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * This test class supports the <code>PersonServiceImpl</code> class.
 *
 * @author Ikasan Development Team
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { com.ikasan.sample.spring.boot.builderpattern.Application.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(locations = {
        "/transaction-pointcut-components-on-test.xml"
})
public class PrivateIndividualServiceImplTest
{
    @Resource
    PersonService personService;

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
    public void test_service_getAll()
    {
        List<Person> people = personService.getPersons();
        Assert.assertTrue("There should be one person ", people.size() == 1);
    }

    @Test
    public void test_service_getPerson_and_update()
    {
        // get and test existing person entry
        Person person = personService.getPerson(1l);
        Assert.assertNotNull("There should be one person ", person );
        Assert.assertTrue("Name should be ikasan ", "ikasan".equals(person.getName()) );

        // update person entry
        person.setName("I have changed my name");
        personService.update(person);

        // get and test updated person entry
        Person updatedPerson = personService.getPerson(1l);
        Assert.assertNotNull("There should be one person ", updatedPerson );
        Assert.assertTrue("Name should be ikasan ", "I have changed my name".equals(updatedPerson.getName()) );
    }
}
