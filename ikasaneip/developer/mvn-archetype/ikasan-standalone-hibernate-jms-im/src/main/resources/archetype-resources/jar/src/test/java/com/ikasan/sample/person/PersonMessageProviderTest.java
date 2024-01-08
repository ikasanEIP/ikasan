package com.ikasan.sample.person;

import com.ikasan.sample.person.dao.PersonDao;
import com.ikasan.sample.person.model.Person;
import org.jmock.Mockery;
import org.jmock.imposters.ByteBuddyClassImposteriser;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.quartz.JobExecutionContext;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * This test class supports the <code>PersonMessageProvider</code> class.
 *
 * @author Ikasan Development Team
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { com.ikasan.sample.spring.boot.builderpattern.Application.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(locations = {
    "/transaction-pointcut-components-on-test.xml"
})
public class PersonMessageProviderTest
{
    @Resource
    PersonMessageProvider personMessageProvider;

    /**
     * Mockery for mocking concrete classes
     */
    private final Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
        }
    };

    private JobExecutionContext jobExecutionContext = mockery.mock(JobExecutionContext.class);

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
    public void test_invoke()
    {
        List<Person> persons = personMessageProvider.invoke(jobExecutionContext);
        Assert.assertTrue("There should be one person ", persons.size() == 1);
    }

}
