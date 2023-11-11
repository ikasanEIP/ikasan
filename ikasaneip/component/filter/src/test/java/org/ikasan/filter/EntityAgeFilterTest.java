package org.ikasan.filter;

import jakarta.annotation.Resource;
import org.ikasan.filter.configuration.EntityAgeFilterConfiguration;
import org.ikasan.filter.duplicate.dao.FilteredMessageDao;
import org.ikasan.filter.duplicate.model.DefaultFilterEntry;
import org.ikasan.filter.duplicate.model.EntityAgeFilterEntryConverter;
import org.ikasan.filter.duplicate.model.FilterEntry;
import org.ikasan.filter.duplicate.service.DefaultEntityAgeFilterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Ikasan Development Team on 10/07/2016.
 */
@SpringJUnitConfig(locations = {
    "/FilteredMessageDaoInMemDBTest-context.xml",
    "/filter-service-conf.xml"
})
class EntityAgeFilterTest
{
    String xmlVeryOld = "<message><businessIdentifier>business-id-1</businessIdentifier><lastUpdated>1973-03-02T19:06:44.000Z</lastUpdated></message>";
    String xmlQuiteRecent = "<message><businessIdentifier>business-id-1</businessIdentifier><lastUpdated>2016-03-02T19:06:44.000Z</lastUpdated></message>";

    String xmlVeryOldNewEntity = "<message><businessIdentifier>business-id-100</businessIdentifier><lastUpdated>1973-03-02T19:06:44.000Z</lastUpdated></message>";
    String xmlQuiteRecentNewEntity = "<message><businessIdentifier>business-id-100</businessIdentifier><lastUpdated>2016-03-02T19:06:44.000Z</lastUpdated></message>";

    String xmlEquals = "<message><businessIdentifier>business-id-1</businessIdentifier><lastUpdated>1980-03-02T00:00:00.000Z</lastUpdated></message>";

    @Resource private FilteredMessageDao duplicateFilterDao;
    @Resource private DefaultEntityAgeFilterService defaultEntityAgeFilterService;

    @BeforeEach
    void setup()
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

    @Test
        @DirtiesContext
    void test_exception_constructor_null_service()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            new EntityAgeFilter<String>(null
            , "client-d");
        });
    }

    @Test
        @DirtiesContext
    void test_exception_constructor_null_client()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            new EntityAgeFilter<String>(this.defaultEntityAgeFilterService
            , null);
        });
    }

    @Test
        @DirtiesContext
    void test_exception_constructor_empty_client()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            new EntityAgeFilter<String>(this.defaultEntityAgeFilterService
            , "");
        });
    }

    @Test
        @DirtiesContext
    void test_success_newer_message_existing_entity()
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

        assertNotNull(result);
    }


    @Test
        @DirtiesContext
    void test_success_older_message_existing_entity()
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

        assertNull(result);
    }

    @Test
        @DirtiesContext
    void test_success_new_entity()
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

        assertNotNull(result);
    }

    @Test
        @DirtiesContext
    void test_success_new_entity_followed_by_older_version_of_same_entity()
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
        assertNotNull(result);

        result = filter.filter(xmlVeryOldNewEntity);
        assertNull(result);

        filter.stopManagedResource();
    }

    @Test
        @DirtiesContext
    void test_success_new_entity_followed_by_newer_version_of_same_entity()
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
        assertNotNull(result);

        result = filter.filter(xmlQuiteRecentNewEntity);
        assertNotNull(result);

        filter.stopManagedResource();
    }
}
