package org.ikasan.filter.duplicate.service;

import junit.framework.Assert;
import org.ikasan.filter.duplicate.dao.FilteredMessageDao;
import org.ikasan.filter.duplicate.model.DefaultFilterEntry;
import org.ikasan.filter.duplicate.model.FilterEntry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * Created by Ikasan Development Team on 10/07/2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={
        "/FilteredMessageDaoInMemDBTest-context.xml",
        "/filter-service-conf.xml"
})
public class DefaultEntityAgeFilterServiceTest
{
    @Resource private FilteredMessageDao duplicateFilterDao;
    @Resource private DefaultEntityAgeFilterService defaultEntityAgeFilterService;

    @Before
    public void setup()
    {
        FilterEntry aMessage = new DefaultFilterEntry( "business-id-1".hashCode(), "client-d", "12345", 30);
        duplicateFilterDao.save(aMessage);
        aMessage = new DefaultFilterEntry( "business-id-2".hashCode(), "client-d", "22345", 30);
        duplicateFilterDao.save(aMessage);
        aMessage = new DefaultFilterEntry( "business-id-3".hashCode(), "client-d", "32345", 30);
        duplicateFilterDao.save(aMessage);
        aMessage = new DefaultFilterEntry( "business-id-4".hashCode(), "client-d", "42345", 30);
        duplicateFilterDao.save(aMessage);
        aMessage = new DefaultFilterEntry( "business-id-5".hashCode(), "client-d", "52345", 30);
        duplicateFilterDao.save(aMessage);
    }

    @Test
    @DirtiesContext
    public void test_success_newer_message()
    {
        defaultEntityAgeFilterService.initialise("client-d");

        FilterEntry aMessage = new DefaultFilterEntry( "business-id-1".hashCode(), "client-d", "22345", 30);

        boolean result = defaultEntityAgeFilterService.isOlderEntity(aMessage);

        Assert.assertFalse(result);

        FilterEntry bMessage = duplicateFilterDao.findMessage(aMessage);

        Assert.assertTrue(aMessage.getCriteriaDescription().equals(bMessage.getCriteriaDescription()));
    }

    @Test
    @DirtiesContext
    public void test_success_message_doesnt_exist()
    {
        defaultEntityAgeFilterService.initialise("client-d");

        FilterEntry aMessage = new DefaultFilterEntry( "business-id-11".hashCode(), "client-d", "22345", 30);

        boolean result = defaultEntityAgeFilterService.isOlderEntity(aMessage);

        Assert.assertFalse(result);

        FilterEntry bMessage = duplicateFilterDao.findMessage(aMessage);

        Assert.assertTrue(aMessage.getCriteriaDescription().equals(bMessage.getCriteriaDescription()));
    }

    @Test
    @DirtiesContext
    public void test_success_older_message()
    {
        defaultEntityAgeFilterService.initialise("client-d");

        FilterEntry aMessage = new DefaultFilterEntry( "business-id-1".hashCode(), "client-d", "2345", 30);

        boolean result = defaultEntityAgeFilterService.isOlderEntity(aMessage);

        Assert.assertTrue(result);

        FilterEntry bMessage = duplicateFilterDao.findMessage(aMessage);

        Assert.assertFalse(aMessage.getCriteriaDescription().equals(bMessage.getCriteriaDescription()));
    }

    @Test
    @DirtiesContext
    public void test_success_equals_message_older_equals_flag_default()
    {
        defaultEntityAgeFilterService.initialise("client-d");

        FilterEntry aMessage = new DefaultFilterEntry( "business-id-1".hashCode(), "client-d", "12345", 30);

        boolean result = defaultEntityAgeFilterService.isOlderEntity(aMessage);

        Assert.assertTrue(result);
    }

    @Test
    @DirtiesContext
    public void test_success_equals_message_older_equals_flag_false()
    {
        defaultEntityAgeFilterService.initialise("client-d");
        defaultEntityAgeFilterService.setOlderIfEquals(false);

        FilterEntry aMessage = new DefaultFilterEntry( "business-id-1".hashCode(), "client-d", "12345", 30);

        boolean result = defaultEntityAgeFilterService.isOlderEntity(aMessage);

        Assert.assertFalse(result);
    }

    @Test
    @DirtiesContext
    public void test_success_equals_message_older_equals_flag_true()
    {
        defaultEntityAgeFilterService.initialise("client-d");
        defaultEntityAgeFilterService.setOlderIfEquals(true);

        FilterEntry aMessage = new DefaultFilterEntry( "business-id-1".hashCode(), "client-d", "12345", 30);

        boolean result = defaultEntityAgeFilterService.isOlderEntity(aMessage);

        Assert.assertTrue(result);
    }
}
