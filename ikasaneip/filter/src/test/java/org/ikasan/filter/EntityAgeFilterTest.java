package org.ikasan.filter;

import junit.framework.Assert;
import org.ikasan.filter.configuration.EntityAgeFilterConfiguration;
import org.ikasan.filter.duplicate.dao.FilteredMessageDao;
import org.ikasan.filter.duplicate.model.DefaultFilterEntry;
import org.ikasan.filter.duplicate.model.EntityAgeFilterEntryConverter;
import org.ikasan.filter.duplicate.model.FilterEntry;
import org.ikasan.filter.duplicate.service.DefaultEntityAgeFilterService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
public class EntityAgeFilterTest
{
    String xmlVeryOld = "<message><businessIdentifier>business-id-1</businessIdentifier><lastUpdated>1973-03-02T19:06:44.000Z</lastUpdated></message>";
    String xmlQuiteRecent = "<message><businessIdentifier>business-id-1</businessIdentifier><lastUpdated>2016-03-02T19:06:44.000Z</lastUpdated></message>";

    String xmlVeryOldNewEntity = "<message><businessIdentifier>business-id-100</businessIdentifier><lastUpdated>1973-03-02T19:06:44.000Z</lastUpdated></message>";
    String xmlQuiteRecentNewEntity = "<message><businessIdentifier>business-id-100</businessIdentifier><lastUpdated>2016-03-02T19:06:44.000Z</lastUpdated></message>";

    @Resource private FilteredMessageDao duplicateFilterDao;
    @Resource private DefaultEntityAgeFilterService defaultEntityAgeFilterService;

    @Before
    public void setup()
    {

        FilterEntry aMessage = new DefaultFilterEntry( "business-id-1".hashCode(), "client-d", "318384000000" /** 318384000000 == 02/03/1980 */, 30);
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

    @Test(expected=IllegalArgumentException.class)
    @DirtiesContext
    public void test_exception_constructor_null_service()
    {
        new EntityAgeFilter<String>(null
                , "client-d");
    }

    @Test(expected=IllegalArgumentException.class)
    @DirtiesContext
    public void test_exception_constructor_null_client()
    {
        new EntityAgeFilter<String>(this.defaultEntityAgeFilterService
                , null);
    }

    @Test(expected=IllegalArgumentException.class)
    @DirtiesContext
    public void test_exception_constructor_empty_client()
    {
        new EntityAgeFilter<String>(this.defaultEntityAgeFilterService
                , "");
    }

    @Test
    @DirtiesContext
    public void test_success_newer_message_existing_entity()
    {
        EntityAgeFilter<String> filter = new EntityAgeFilter<String>(this.defaultEntityAgeFilterService
                , "client-d");

        EntityAgeFilterConfiguration configuration  = new EntityAgeFilterConfiguration();
        configuration.setEntityIdentifierXpath("/message/businessIdentifier/text()");
        configuration.setEntityLastUpdatedXpath("/message/lastUpdated/text()");
        configuration.setLastUpdatedDatePattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        filter.setConfiguration(configuration);
        filter.startManagedResource();

        String result = filter.filter(xmlQuiteRecent);

        filter.stopManagedResource();

        Assert.assertNotNull(result);
    }

    @Test
    @DirtiesContext
    public void test_success_older_message_existing_entity()
    {
        EntityAgeFilter<String> filter = new EntityAgeFilter<String>(this.defaultEntityAgeFilterService
                , "client-d");

        EntityAgeFilterConfiguration configuration  = new EntityAgeFilterConfiguration();
        configuration.setEntityIdentifierXpath("/message/businessIdentifier/text()");
        configuration.setEntityLastUpdatedXpath("/message/lastUpdated/text()");
        configuration.setLastUpdatedDatePattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        filter.setConfiguration(configuration);
        filter.startManagedResource();

        String result = filter.filter(xmlVeryOld);

        filter.stopManagedResource();

        Assert.assertNull(result);
    }

    @Test
    @DirtiesContext
    public void test_success_new_entity()
    {
        EntityAgeFilter<String> filter = new EntityAgeFilter<String>(this.defaultEntityAgeFilterService
                , "client-d");

        EntityAgeFilterConfiguration configuration  = new EntityAgeFilterConfiguration();
        configuration.setEntityIdentifierXpath("/message/businessIdentifier/text()");
        configuration.setEntityLastUpdatedXpath("/message/lastUpdated/text()");
        configuration.setLastUpdatedDatePattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        filter.setConfiguration(configuration);
        filter.startManagedResource();

        String result = filter.filter(xmlQuiteRecentNewEntity);

        filter.stopManagedResource();

        Assert.assertNotNull(result);
    }

    @Test
    @DirtiesContext
    public void test_success_new_entity_followed_by_older_version_of_same_entity()
    {
        EntityAgeFilter<String> filter = new EntityAgeFilter<String>(this.defaultEntityAgeFilterService
                , "client-d");

        EntityAgeFilterConfiguration configuration  = new EntityAgeFilterConfiguration();
        configuration.setEntityIdentifierXpath("/message/businessIdentifier/text()");
        configuration.setEntityLastUpdatedXpath("/message/lastUpdated/text()");
        configuration.setLastUpdatedDatePattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        filter.setConfiguration(configuration);
        filter.startManagedResource();

        String result = filter.filter(xmlQuiteRecentNewEntity);
        Assert.assertNotNull(result);

        result = filter.filter(xmlVeryOldNewEntity);
        Assert.assertNull(result);

        filter.stopManagedResource();
    }

    @Test
    @DirtiesContext
    public void test_success_new_entity_followed_by_newer_version_of_same_entity()
    {
        EntityAgeFilter<String> filter = new EntityAgeFilter<String>(this.defaultEntityAgeFilterService
                , "client-d");

        EntityAgeFilterConfiguration configuration  = new EntityAgeFilterConfiguration();
        configuration.setEntityIdentifierXpath("/message/businessIdentifier/text()");
        configuration.setEntityLastUpdatedXpath("/message/lastUpdated/text()");
        configuration.setLastUpdatedDatePattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"          );

        filter.setConfiguration(configuration);
        filter.startManagedResource();

        String result = filter.filter(xmlVeryOldNewEntity);
        Assert.assertNotNull(result);

        result = filter.filter(xmlQuiteRecentNewEntity);
        Assert.assertNotNull(result);

        filter.stopManagedResource();
    }
}
