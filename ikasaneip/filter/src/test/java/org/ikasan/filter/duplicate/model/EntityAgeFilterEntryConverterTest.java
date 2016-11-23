package org.ikasan.filter.duplicate.model;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Created by stewmi on 09/07/2016.
 */
public class EntityAgeFilterEntryConverterTest
{
    String xml = "<message><businessIdentifier>BID</businessIdentifier><lastUpdated>02031973</lastUpdated></message>";

    @Test(expected=IllegalArgumentException.class)
    public void test_exception_constructor_null_business_identifier_xpath()
    {
        new EntityAgeFilterEntryConverter
                (null, "/message/lastUpdated/text()"
                        , "DDMMYYYY", "test-client", 30);
    }

    @Test(expected=IllegalArgumentException.class)
    public void test_exception_constructor_empty_business_identifier_xpath()
    {
        new EntityAgeFilterEntryConverter
                ("", "/message/lastUpdated/text()"
                        , "DDMMYYYY", "test-client", 30);
    }

    @Test(expected=IllegalArgumentException.class)
    public void test_exception_constructor_null_last_updated_xpath()
    {
        new EntityAgeFilterEntryConverter
                ("/message/businessIdentifier/text()", null
                        , "DDMMYYYY", "test-client", 30);
    }

    @Test(expected=IllegalArgumentException.class)
    public void test_exception_constructor_empty_last_updated_xpath()
    {
        new EntityAgeFilterEntryConverter
                ("/message/businessIdentifier/text()", ""
                        , "DDMMYYYY", "test-client", 30);
    }

    @Test(expected=IllegalArgumentException.class)
    public void test_exception_constructor_null_date_format_xpath()
    {
        new EntityAgeFilterEntryConverter
                ("/message/businessIdentifier/text()", "/message/lastUpdated/text()"
                        , null, "test-client", 30);
    }

    @Test(expected=IllegalArgumentException.class)
    public void test_exception_constructor_empty_date_format_xpath()
    {
        new EntityAgeFilterEntryConverter
                ("/message/businessIdentifier/text()", "/message/lastUpdated/text()"
                        , "", "test-client", 30);
    }

    @Test(expected=IllegalArgumentException.class)
    public void test_exception_constructor_null_client_id_xpath()
    {
        new EntityAgeFilterEntryConverter
                ("/message/businessIdentifier/text()", "/message/lastUpdated/text()"
                        , "DDMMYYYY", null, 30);
    }

    @Test(expected=IllegalArgumentException.class)
    public void test_exception_constructor_empty_client_id_xpath()
    {
        new EntityAgeFilterEntryConverter
                ("/message/businessIdentifier/text()", "/message/lastUpdated/text()"
                        , "DDMMYYYY", "", 30);
    }

    @Test
    @Ignore
    // ignoring this test as there seems to be a difference in calculating the milliseconds on the linode server.
    public void test_convert()
    {
        EntityAgeFilterEntryConverter converter
                = new EntityAgeFilterEntryConverter
                ("/message/businessIdentifier/text()", "/message/lastUpdated/text()"
                        , "DDMMYYYY", "test-client", 30);

        FilterEntry entry = converter.convert(xml);

        System.out.println(entry);

        System.out.println("Criteria = " + entry.getCriteria());
        Assert.assertEquals(entry.getCriteria(), new Integer(65757));
        Assert.assertEquals(entry.getClientId(), "test-client");
        Assert.assertEquals(entry.getCriteriaDescription(), "94694400000");
    }

    @Test(expected=FilterEntryConverterException.class)
    public void test_exception_bad_xml()
    {
        EntityAgeFilterEntryConverter converter
                = new EntityAgeFilterEntryConverter
                ("/message/businessIdentifier/text()", "/message/lastUpdated/text()"
                        , "DDMMYYYY", "test-client", 30);

        converter.convert("bad xml");
    }

    @Test(expected=FilterEntryConverterException.class)
    public void test_exception_bad_xpath_business_identifier()
    {
        EntityAgeFilterEntryConverter converter
                = new EntityAgeFilterEntryConverter
                ("bad xpath", "/message/lastUpdated/text()"
                        , "DDMMYYYY", "test-client", 30);

        converter.convert(xml);
    }

    @Test(expected=FilterEntryConverterException.class)
    public void test_exception_bad_xpath_updated_date()
    {
        EntityAgeFilterEntryConverter converter
                = new EntityAgeFilterEntryConverter
                ("/message/businessIdentifier/text()", "bad xpath"
                        , "DDMMYYYY", "test-client", 30);

        converter.convert(xml);
    }
}
