package org.ikasan.filter.duplicate.model;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Created by Ikasan Development Team on 09/07/2016.
 */
class EntityAgeFilterEntryConverterTest
{
    String xml = "<message><businessIdentifier>BID</businessIdentifier><lastUpdated>02031973</lastUpdated></message>";

    @Test
    void test_exception_constructor_null_business_identifier_xpath()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            new EntityAgeFilterEntryConverter
                (null, "/message/lastUpdated/text()"
                , "DDMMYYYY", "test-client", 30);
        });
    }

    @Test
    void test_exception_constructor_empty_business_identifier_xpath()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            new EntityAgeFilterEntryConverter
                ("", "/message/lastUpdated/text()"
                , "DDMMYYYY", "test-client", 30);
        });
    }

    @Test
    void test_exception_constructor_null_last_updated_xpath()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            new EntityAgeFilterEntryConverter
                ("/message/businessIdentifier/text()", null
                , "DDMMYYYY", "test-client", 30);
        });
    }

    @Test
    void test_exception_constructor_empty_last_updated_xpath()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            new EntityAgeFilterEntryConverter
                ("/message/businessIdentifier/text()", ""
                , "DDMMYYYY", "test-client", 30);
        });
    }

    @Test
    void test_exception_constructor_null_date_format_xpath()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            new EntityAgeFilterEntryConverter
                ("/message/businessIdentifier/text()", "/message/lastUpdated/text()"
                , null, "test-client", 30);
        });
    }

    @Test
    void test_exception_constructor_empty_date_format_xpath()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            new EntityAgeFilterEntryConverter
                ("/message/businessIdentifier/text()", "/message/lastUpdated/text()"
                , "", "test-client", 30);
        });
    }

    @Test
    void test_exception_constructor_null_client_id_xpath()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            new EntityAgeFilterEntryConverter
                ("/message/businessIdentifier/text()", "/message/lastUpdated/text()"
                , "DDMMYYYY", null, 30);
        });
    }

    @Test
    void test_exception_constructor_empty_client_id_xpath()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            new EntityAgeFilterEntryConverter
                ("/message/businessIdentifier/text()", "/message/lastUpdated/text()"
                , "DDMMYYYY", "", 30);
        });
    }

    // ignoring this test as there seems to be a difference in calculating the milliseconds on the linode server.
    @Test
    @Disabled
    void test_convert()
    {
        EntityAgeFilterEntryConverter converter
                = new EntityAgeFilterEntryConverter
                ("/message/businessIdentifier/text()", "/message/lastUpdated/text()"
                        , "DDMMYYYY", "test-client", 30);

        FilterEntry entry = converter.convert(xml);

        System.out.println(entry);

        System.out.println("Criteria = " + entry.getCriteria());
        assertEquals(entry.getCriteria(), Integer.valueOf(65757));
        assertEquals("test-client", entry.getClientId());
        assertEquals("94694400000", entry.getCriteriaDescription());
    }

    @Test
    void test_exception_bad_xml()
    {
        assertThrows(FilterEntryConverterException.class, () -> {
            EntityAgeFilterEntryConverter converter
                = new EntityAgeFilterEntryConverter
                ("/message/businessIdentifier/text()", "/message/lastUpdated/text()"
                , "DDMMYYYY", "test-client", 30);

            converter.convert("bad xml");
        });
    }

    @Test
    void test_exception_bad_xpath_business_identifier()
    {
        assertThrows(FilterEntryConverterException.class, () -> {
            EntityAgeFilterEntryConverter converter
                = new EntityAgeFilterEntryConverter
                ("bad xpath", "/message/lastUpdated/text()"
                , "DDMMYYYY", "test-client", 30);

            converter.convert(xml);
        });
    }

    @Test
    void test_exception_bad_xpath_updated_date()
    {
        assertThrows(FilterEntryConverterException.class, () -> {
            EntityAgeFilterEntryConverter converter
                = new EntityAgeFilterEntryConverter
                ("/message/businessIdentifier/text()", "bad xpath"
                , "DDMMYYYY", "test-client", 30);

            converter.convert(xml);
        });
    }
}
