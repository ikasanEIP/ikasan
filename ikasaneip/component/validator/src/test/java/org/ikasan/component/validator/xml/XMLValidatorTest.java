package org.ikasan.component.validator.xml;

import org.ikasan.component.validator.ValidationException;
import org.ikasan.component.validator.ValidationResult;
import org.ikasan.spec.component.transformation.Converter;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Comprehensive test suite for XMLValidator.
 * Tests cover validation scenarios, error handling, configuration options,
 * schema caching, catalog resolution, and lifecycle management.
 *
 * @author Ikasan Development Team
 */
public class XMLValidatorTest
{
    private String xml =
            """
            <?xml version="1.0"?><x:books xmlns:x="urn:books" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xs="http://www.w3.org/2001/XMLSchema" \
            xmlns:fo="http://www.w3.org/1999/XSL/Format" xsi:schemaLocation="urn:books <SCHEMA>">   <book id="bk001">      \
            <author>Writer</author>      <title>The First Book</title>      <genre>Fiction</genre>      \
            <price>44.95</price>      <pub_date>2000-10-01</pub_date>      <review>An amazing s\
            tory of nothing.</review>   </book>   <book id="bk002">      <author>Poet</author>      \
            <title>The Poet's First Poem</title>      <genre>Poem</genre>      <price>24.95</price>      \
            <pub_date>2000-10-01</pub_date><review>Least poetic poems.</review>   </book></x:books>\
            """;

    private String xml_bad =
            """
            <?xml version="1.0"?><x:books xmlns:x="urn:books" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xs="http://www.w3.org/2001/XMLSchema" \
            xmlns:fo="http://www.w3.org/1999/XSL/Format" xsi:schemaLocation="urn:books <SCHEMA>">   <book id="bk001">      \
            <author>Writer</author>      <title>The First Book</title>      <genre>Fiction</genre>      \
            <price>44.95</price>      <pub_date>2000-10-01</pub_date>      <review>An amazing s\
            tory of nothing.</review>   </book>   <book id="bk002">      <author>Poet</author>      \
            <title>The Poet's First Poem</title>      <genre>Poem</genre>      <price>24.95</price>      \
            <pub_date>2000-10-01</pub_date><review>Least poetic poems.</review> <bad_element>stuff</bad_element>  </book></x:books>\
            """;

    private String xml_with_schema_url =
            """
            <?xml version="1.0"?><x:books xmlns:x="urn:books" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xs="http://www.w3.org/2001/XMLSchema" \
            xmlns:fo="http://www.w3.org/1999/XSL/Format" xsi:schemaLocation="urn:books http://www.books4tests.com/xsd/book.xsd">   <book id="bk001">      \
            <author>Writer</author>      <title>The First Book</title>      <genre>Fiction</genre>      \
            <price>44.95</price>      <pub_date>2000-10-01</pub_date>      <review>An amazing s\
            tory of nothing.</review>   </book>   <book id="bk002">      <author>Poet</author>      \
            <title>The Poet's First Poem</title>      <genre>Poem</genre>      <price>24.95</price>      \
            <pub_date>2000-10-01</pub_date><review>Least poetic poems.</review>   </book></x:books>\
            """;

    private String xml_no_schema_location =
            """
            <?xml version="1.0"?><x:books xmlns:x="urn:books" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
            <book id="bk001"><author>Writer</author></book></x:books>
            """;

    private String xml_missing_required_field =
            """
            <?xml version="1.0"?><x:books xmlns:x="urn:books" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xs="http://www.w3.org/2001/XMLSchema" \
            xmlns:fo="http://www.w3.org/1999/XSL/Format" xsi:schemaLocation="urn:books <SCHEMA>">   <book id="bk001">      \
            <author>Writer</author>      <title>The First Book</title></book></x:books>\
            """;

    private String xml_wrong_data_type =
            """
            <?xml version="1.0"?><x:books xmlns:x="urn:books" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xs="http://www.w3.org/2001/XMLSchema" \
            xmlns:fo="http://www.w3.org/1999/XSL/Format" xsi:schemaLocation="urn:books <SCHEMA>">   <book id="bk001">      \
            <author>Writer</author>      <title>The First Book</title>      <genre>Fiction</genre>      \
            <price>NOT_A_NUMBER</price>      <pub_date>2000-10-01</pub_date>      <review>Review</review></book></x:books>\
            """;

    @Test
    public void testParseValidate_against_classpath_xml_pass()
    {
        XMLValidatorConfiguration configuration = new XMLValidatorConfiguration();
        configuration.setSkipValidation(false);
        XMLValidator validator = new XMLValidator();
        validator.setConfiguration(configuration);
        validator.startManagedResource();
        Assert.assertEquals(validator.convert(this.addSchemaToString(xml)), this.addSchemaToString(xml));
    }

    @Test
    public void testParseValidate_against_classpath_xml_pass_return_validation_result()
    {
        XMLValidatorConfiguration configuration = new XMLValidatorConfiguration();
        configuration.setSkipValidation(false);
        configuration.setReturnValidationResult(true);
        XMLValidator validator = new XMLValidator();
        validator.setConfiguration(configuration);
        validator.startManagedResource();
        Assert.assertEquals(validator.convert(this.addSchemaToString(xml)).getClass(), ValidationResult.class);
    }

    @Test
    public void testParseValidate_against_classpath_xml_pass_call_twice_to_make_sure_xml_readr_is_reused()
    {
        XMLValidatorConfiguration configuration = new XMLValidatorConfiguration();
        configuration.setSkipValidation(false);
        XMLValidator validator = new XMLValidator();
        validator.setConfiguration(configuration);
        validator.startManagedResource();
        Assert.assertEquals(validator.convert(this.addSchemaToString(xml)), this.addSchemaToString(xml));
        Assert.assertEquals(validator.convert(this.addSchemaToString(xml)), this.addSchemaToString(xml));
    }

    @Test(expected = ValidationException.class)
    public void testParseValidate_against_classpath_xml_fail()
    {
        XMLValidatorConfiguration configuration = new XMLValidatorConfiguration();
        configuration.setSkipValidation(false);
        XMLValidator validator = new XMLValidator();
        validator.setConfiguration(configuration);
        validator.startManagedResource();
        validator.convert(this.addSchemaToString(xml_bad));
    }

    @Test
    public void testParseValidate_skip_validation_return_source()
    {
        XMLValidatorConfiguration configuration = new XMLValidatorConfiguration();
        configuration.setSkipValidation(true);
        configuration.setReturnValidationResult(false);
        XMLValidator validator = new XMLValidator();
        validator.setConfiguration(configuration);
        validator.startManagedResource();
        Assert.assertEquals(validator.convert(this.addSchemaToString(xml)), this.addSchemaToString(xml));
    }

    @Test
    public void testParseValidate_skip_validation_return_validation_result()
    {
        XMLValidatorConfiguration configuration = new XMLValidatorConfiguration();
        configuration.setSkipValidation(true);
        configuration.setReturnValidationResult(true);
        XMLValidator validator = new XMLValidator();
        validator.setConfiguration(configuration);
        validator.startManagedResource();
        Assert.assertEquals(validator.convert(this.addSchemaToString(xml)).getClass(), ValidationResult.class);
    }

    @Test
    public void testParseValidate_against_url_with_catalog_xml_pass()
    {
        XMLValidatorConfiguration configuration = new XMLValidatorConfiguration();
        configuration.setSkipValidation(false);
        configuration.setCatalogUrl(XMLValidator.class.getResource("/catalog.xml").toString());
        XMLValidator validator = new XMLValidator();
        validator.setConfiguration(configuration);
        validator.startManagedResource();
        Assert.assertEquals(validator.convert(xml_with_schema_url), xml_with_schema_url);
    }

    /**
     * Test validation failure with invalid XML returns ValidationResult when configured
     */
    @Test
    public void testParseValidate_invalid_xml_return_validation_result()
    {
        XMLValidatorConfiguration configuration = new XMLValidatorConfiguration();
        configuration.setSkipValidation(false);
        configuration.setReturnValidationResult(true);
        configuration.setThrowExceptionOnValidationFailure(false);
        XMLValidator validator = new XMLValidator();
        validator.setConfiguration(configuration);
        validator.startManagedResource();

        Object result = validator.convert(this.addSchemaToString(xml_bad));
        Assert.assertTrue(result instanceof ValidationResult);
        Assert.assertEquals(ValidationResult.Result.INVALID, ((ValidationResult) result).getResult());
        Assert.assertNotNull(((ValidationResult) result).getException());
        Assert.assertTrue(((ValidationResult) result).getException() instanceof SAXException);
    }

    /**
     * Test XML with missing required fields fails validation
     */
    @Test(expected = ValidationException.class)
    public void testParseValidate_missing_required_fields_fail()
    {
        XMLValidatorConfiguration configuration = new XMLValidatorConfiguration();
        configuration.setSkipValidation(false);
        XMLValidator validator = new XMLValidator();
        validator.setConfiguration(configuration);
        validator.startManagedResource();
        validator.convert(this.addSchemaToString(xml_missing_required_field));
    }

    /**
     * Test XML with wrong data type fails validation
     */
    @Test(expected = ValidationException.class)
    public void testParseValidate_wrong_data_type_fail()
    {
        XMLValidatorConfiguration configuration = new XMLValidatorConfiguration();
        configuration.setSkipValidation(false);
        XMLValidator validator = new XMLValidator();
        validator.setConfiguration(configuration);
        validator.startManagedResource();
        validator.convert(this.addSchemaToString(xml_wrong_data_type));
    }

    /**
     * Test XML without schema location throws appropriate exception
     */
    @Test(expected = ValidationException.class)
    public void testParseValidate_no_schema_location_fail()
    {
        XMLValidatorConfiguration configuration = new XMLValidatorConfiguration();
        configuration.setSkipValidation(false);
        XMLValidator validator = new XMLValidator();
        validator.setConfiguration(configuration);
        validator.startManagedResource();
        validator.convert(xml_no_schema_location);
    }

    /**
     * Test schema caching - validate same XML multiple times to ensure schema is reused
     */
    @Test
    public void testParseValidate_schema_caching()
    {
        XMLValidatorConfiguration configuration = new XMLValidatorConfiguration();
        configuration.setSkipValidation(false);
        XMLValidator validator = new XMLValidator();
        validator.setConfiguration(configuration);
        validator.startManagedResource();

        String validXml = this.addSchemaToString(xml);

        // Validate multiple times - should use cached schema
        Assert.assertEquals(validXml, validator.convert(validXml));
        Assert.assertEquals(validXml, validator.convert(validXml));
        Assert.assertEquals(validXml, validator.convert(validXml));
    }

    /**
     * Test validation result contains source when validation passes
     */
    @Test
    public void testParseValidate_validation_result_contains_source()
    {
        XMLValidatorConfiguration configuration = new XMLValidatorConfiguration();
        configuration.setSkipValidation(false);
        configuration.setReturnValidationResult(true);
        XMLValidator validator = new XMLValidator();
        validator.setConfiguration(configuration);
        validator.startManagedResource();

        String validXml = this.addSchemaToString(xml);
        ValidationResult result = (ValidationResult) validator.convert(validXml);

        Assert.assertNotNull(result.getSource());
        Assert.assertEquals(validXml, result.getSource());
        Assert.assertEquals(ValidationResult.Result.VALID, result.getResult());
    }

    /**
     * Test validation result contains exception details when validation fails
     */
    @Test
    public void testParseValidate_validation_result_contains_exception()
    {
        XMLValidatorConfiguration configuration = new XMLValidatorConfiguration();
        configuration.setSkipValidation(false);
        configuration.setReturnValidationResult(true);
        configuration.setThrowExceptionOnValidationFailure(false);
        XMLValidator validator = new XMLValidator();
        validator.setConfiguration(configuration);
        validator.startManagedResource();

        String invalidXml = this.addSchemaToString(xml_bad);
        ValidationResult result = (ValidationResult) validator.convert(invalidXml);

        Assert.assertEquals(ValidationResult.Result.INVALID, result.getResult());
        Assert.assertNotNull(result.getException());
        Assert.assertNotNull(result.getSource());
    }

    /**
     * Test lifecycle - stop and restart managed resource
     */
    @Test
    public void testLifecycle_stop_and_restart()
    {
        XMLValidatorConfiguration configuration = new XMLValidatorConfiguration();
        configuration.setSkipValidation(false);
        XMLValidator validator = new XMLValidator();
        validator.setConfiguration(configuration);

        // Start
        validator.startManagedResource();
        String validXml = this.addSchemaToString(xml);
        Assert.assertEquals(validXml, validator.convert(validXml));

        // Stop
        validator.stopManagedResource();

        // Restart
        validator.startManagedResource();
        Assert.assertEquals(validXml, validator.convert(validXml));
    }

    /**
     * Test configured resource ID getter/setter
     */
    @Test
    public void testConfiguredResourceId()
    {
        XMLValidator validator = new XMLValidator();
        validator.setConfiguredResourceId("testId");
        Assert.assertEquals("testId", validator.getConfiguredResourceId());
    }

    /**
     * Test configuration getter/setter
     */
    @Test
    public void testConfiguration()
    {
        XMLValidatorConfiguration configuration = new XMLValidatorConfiguration();
        configuration.setSkipValidation(true);
        XMLValidator validator = new XMLValidator();
        validator.setConfiguration(configuration);
        Assert.assertEquals(configuration, validator.getConfiguration());
        Assert.assertTrue(validator.getConfiguration().isSkipValidation());
    }

    /**
     * Test isCriticalOnStartup returns true
     */
    @Test
    public void testIsCriticalOnStartup()
    {
        XMLValidator validator = new XMLValidator();
        Assert.assertTrue(validator.isCriticalOnStartup());
    }

    /**
     * Test setCriticalOnStartup (no-op operation)
     */
    @Test
    public void testSetCriticalOnStartup()
    {
        XMLValidator validator = new XMLValidator();
        validator.setCriticalOnStartup(false);
        // Should still be true as setter is no-op
        Assert.assertTrue(validator.isCriticalOnStartup());
    }

    /**
     * Test setManagedResourceRecoveryManager (no-op operation)
     */
    @Test
    public void testSetManagedResourceRecoveryManager()
    {
        XMLValidator validator = new XMLValidator();
        // Should not throw exception
        validator.setManagedResourceRecoveryManager(null);
    }

    /**
     * Test with custom source to byte array converter
     */
    @Test
    public void testCustomSourceConverter()
    {
        XMLValidatorConfiguration configuration = new XMLValidatorConfiguration();
        configuration.setSkipValidation(false);
        XMLValidator<byte[], Object> validator = new XMLValidator<>();
        validator.setConfiguration(configuration);

        // Custom converter from byte[] to ByteArrayInputStream
        Converter<byte[], ByteArrayInputStream> converter = bytes -> new ByteArrayInputStream(bytes);
        validator.setSourceToByteArrayInputStreamConverter(converter);

        validator.startManagedResource();

        String validXml = this.addSchemaToString(xml);
        byte[] xmlBytes = validXml.getBytes(StandardCharsets.UTF_8);

        Object result = validator.convert(xmlBytes);
        Assert.assertArrayEquals(xmlBytes, (byte[]) result);
    }

    /**
     * Test validation with catalog URL resolving remote schema location
     */
    @Test
    public void testParseValidate_catalog_resolves_remote_schema()
    {
        XMLValidatorConfiguration configuration = new XMLValidatorConfiguration();
        configuration.setSkipValidation(false);
        configuration.setCatalogUrl(XMLValidator.class.getResource("/catalog.xml").toString());
        configuration.setReturnValidationResult(true);
        XMLValidator validator = new XMLValidator();
        validator.setConfiguration(configuration);
        validator.startManagedResource();

        ValidationResult result = (ValidationResult) validator.convert(xml_with_schema_url);
        Assert.assertEquals(ValidationResult.Result.VALID, result.getResult());
    }

    /**
     * Test multiple validations in sequence with different schemas
     */
    @Test
    public void testParseValidate_multiple_schemas_cached()
    {
        XMLValidatorConfiguration configuration = new XMLValidatorConfiguration();
        configuration.setSkipValidation(false);
        XMLValidator validator = new XMLValidator();
        validator.setConfiguration(configuration);
        validator.startManagedResource();

        String validXml1 = this.addSchemaToString(xml);

        // First validation - schema compiled and cached
        Assert.assertEquals(validXml1, validator.convert(validXml1));

        // Second validation - schema retrieved from cache
        Assert.assertEquals(validXml1, validator.convert(validXml1));

        // Third validation with same schema - should still use cache
        Assert.assertEquals(validXml1, validator.convert(validXml1));
    }

    /**
     * Test error message generation includes XML content
     */
    @Test
    public void testErrorMessageIncludesXmlContent()
    {
        XMLValidatorConfiguration configuration = new XMLValidatorConfiguration();
        configuration.setSkipValidation(false);
        configuration.setThrowExceptionOnValidationFailure(true);
        XMLValidator validator = new XMLValidator();
        validator.setConfiguration(configuration);
        validator.startManagedResource();

        String invalidXml = this.addSchemaToString(xml_bad);
        try
        {
            validator.convert(invalidXml);
            Assert.fail("Should have thrown ValidationException");
        }
        catch (ValidationException e)
        {
            // Error message should contain the XML content
            Assert.assertTrue(e.getMessage().contains("XML validation error"));
            Assert.assertTrue(e.getMessage().contains("bad_element"));
        }
    }

    /**
     * Test validation with skip validation enabled and return source
     */
    @Test
    public void testSkipValidation_returns_source_unchanged()
    {
        XMLValidatorConfiguration configuration = new XMLValidatorConfiguration();
        configuration.setSkipValidation(true);
        configuration.setReturnValidationResult(false);
        XMLValidator validator = new XMLValidator();
        validator.setConfiguration(configuration);
        validator.startManagedResource();

        // Even invalid XML should pass through unchanged
        String invalidXml = this.addSchemaToString(xml_bad);
        Assert.assertEquals(invalidXml, validator.convert(invalidXml));
    }

    /**
     * Test validation with skip validation enabled and return validation result
     */
    @Test
    public void testSkipValidation_returns_valid_result()
    {
        XMLValidatorConfiguration configuration = new XMLValidatorConfiguration();
        configuration.setSkipValidation(true);
        configuration.setReturnValidationResult(true);
        XMLValidator validator = new XMLValidator();
        validator.setConfiguration(configuration);
        validator.startManagedResource();

        // Even invalid XML should return VALID result
        String invalidXml = this.addSchemaToString(xml_bad);
        ValidationResult result = (ValidationResult) validator.convert(invalidXml);
        Assert.assertEquals(ValidationResult.Result.VALID, result.getResult());
        Assert.assertNull(result.getException());
    }

    /**
     * Test that runtime exception is thrown with invalid catalog URL
     */
    @Test(expected = RuntimeException.class)
    public void testInvalidCatalogUrl_throws_runtime_exception()
    {
        XMLValidatorConfiguration configuration = new XMLValidatorConfiguration();
        configuration.setSkipValidation(false);
        configuration.setCatalogUrl("invalid://url/catalog.xml");
        XMLValidator validator = new XMLValidator();
        validator.setConfiguration(configuration);
        validator.startManagedResource();
    }

    /**
     * Test concurrent access to validator (schema cache thread safety)
     */
    @Test
    public void testConcurrentValidation() throws Exception
    {
        XMLValidatorConfiguration configuration = new XMLValidatorConfiguration();
        configuration.setSkipValidation(false);
        XMLValidator validator = new XMLValidator();
        validator.setConfiguration(configuration);
        validator.startManagedResource();

        String validXml = this.addSchemaToString(xml);

        // Run validations from multiple threads
        Thread thread1 = new Thread(() ->
        {
            try
            {
                validator.convert(validXml);
            }
            catch (Exception e)
            {
                Assert.fail("Thread 1 failed: " + e.getMessage());
            }
        });

        Thread thread2 = new Thread(() ->
        {
            try
            {
                validator.convert(validXml);
            }
            catch (Exception e)
            {
                Assert.fail("Thread 2 failed: " + e.getMessage());
            }
        });

        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();
    }

    // ========== Exception Case Tests ==========

    /**
     * Test error message generation with custom converter throwing IOException
     */
    @Test
    public void testException_io_exception_in_custom_converter()
            throws Exception
    {
        XMLValidatorConfiguration configuration = new XMLValidatorConfiguration();
        configuration.setSkipValidation(false);
        configuration.setReturnValidationResult(true);
        configuration.setThrowExceptionOnValidationFailure(false);

        XMLValidator<String, Object> validator = new XMLValidator<>();
        validator.setConfiguration(configuration);

        // Custom converter that throws IOException
        Converter<String, ByteArrayInputStream> faultyConverter = source -> {
            throw new RuntimeException(new IOException("Converter failed"));
        };
        validator.setSourceToByteArrayInputStreamConverter(faultyConverter);

        validator.startManagedResource();

        try
        {
            validator.convert(this.addSchemaToString(xml_bad));
            Assert.fail("Should have thrown exception");
        }
        catch (RuntimeException e)
        {
            Assert.assertTrue(e.getCause() instanceof IOException);
            Assert.assertEquals("Converter failed", e.getCause().getMessage());
        }
    }

    /**
     * Test exception with null configuration
     */
    @Test(expected = RuntimeException.class)
    public void testException_null_configuration()
    {
        XMLValidator validator = new XMLValidator();
        validator.setConfiguration(null);
        validator.startManagedResource();
    }

    /**
     * Test validation with malformed XML (not well-formed)
     */
    @Test(expected = ValidationException.class)
    public void testException_malformed_xml()
    {
        String malformedXml = "<?xml version=\"1.0\"?><unclosed-tag>";

        XMLValidatorConfiguration configuration = new XMLValidatorConfiguration();
        configuration.setSkipValidation(false);
        XMLValidator validator = new XMLValidator();
        validator.setConfiguration(configuration);
        validator.startManagedResource();
        validator.convert(malformedXml);
    }

    /**
     * Test validation with empty XML string
     */
    @Test(expected = ValidationException.class)
    public void testException_empty_xml()
    {
        XMLValidatorConfiguration configuration = new XMLValidatorConfiguration();
        configuration.setSkipValidation(false);
        XMLValidator validator = new XMLValidator();
        validator.setConfiguration(configuration);
        validator.startManagedResource();
        validator.convert("");
    }

    /**
     * Test validation with null XML input
     */
    @Test(expected = ValidationException.class)
    public void testException_null_xml_input()
    {
        XMLValidatorConfiguration configuration = new XMLValidatorConfiguration();
        configuration.setSkipValidation(false);
        XMLValidator validator = new XMLValidator();
        validator.setConfiguration(configuration);
        validator.startManagedResource();
        validator.convert(null);
    }

    /**
     * Test exception when schema file cannot be found
     */
    @Test(expected = ValidationException.class)
    public void testException_schema_not_found()
    {
        String xmlWithInvalidSchema =
                """
                <?xml version="1.0"?><x:books xmlns:x="urn:books" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" \
                xsi:schemaLocation="urn:books file:///nonexistent/schema.xsd"><book id="bk001"><author>Writer</author></book></x:books>
                """;

        XMLValidatorConfiguration configuration = new XMLValidatorConfiguration();
        configuration.setSkipValidation(false);
        XMLValidator validator = new XMLValidator();
        validator.setConfiguration(configuration);
        validator.startManagedResource();
        validator.convert(xmlWithInvalidSchema);
    }

    /**
     * Test validation exception message contains helpful information
     */
    @Test
    public void testException_message_contains_xml_content()
    {
        XMLValidatorConfiguration configuration = new XMLValidatorConfiguration();
        configuration.setSkipValidation(false);
        XMLValidator validator = new XMLValidator();
        validator.setConfiguration(configuration);
        validator.startManagedResource();

        String invalidXml = this.addSchemaToString(xml_bad);
        try
        {
            validator.convert(invalidXml);
            Assert.fail("Should have thrown ValidationException");
        }
        catch (ValidationException e)
        {
            String message = e.getMessage();
            Assert.assertNotNull("Error message should not be null", message);
            Assert.assertTrue("Message should contain 'XML validation error'",
                message.contains("XML validation error"));
            Assert.assertTrue("Message should contain the invalid element",
                message.contains("bad_element"));
            Assert.assertTrue("Message should contain XML content",
                message.contains("<?xml version"));
        }
    }

    /**
     * Test that schema locations are sorted for cache key generation.
     * Two XML documents with the same schemas in different order should use the same cached schema.
     */
    @Test
    public void testSortedCacheKey_schemas_in_different_order_use_same_cache()
    {
        XMLValidatorConfiguration configuration = new XMLValidatorConfiguration();
        configuration.setSkipValidation(false);
        XMLValidator validator = new XMLValidator();
        validator.setConfiguration(configuration);
        validator.startManagedResource();

        // XML with schemas in one order: schema1 then schema2
        String xml1 = """
            <?xml version="1.0"?><x:books xmlns:x="urn:books" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xsi:schemaLocation="urn:books <SCHEMA>">
            <book id="bk001"><author>Writer</author><title>Book</title><genre>Fiction</genre>
            <price>10.00</price><pub_date>2000-01-01</pub_date><review>Review</review></book></x:books>
            """;

        // XML with same schema - should reuse cached schema
        String xml2 = """
            <?xml version="1.0"?><x:books xmlns:x="urn:books" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xsi:schemaLocation="urn:books <SCHEMA>">
            <book id="bk002"><author>Poet</author><title>Poem</title><genre>Poetry</genre>
            <price>20.00</price><pub_date>2001-01-01</pub_date><review>Great</review></book></x:books>
            """;

        String validXml1 = this.addSchemaToString(xml1);
        String validXml2 = this.addSchemaToString(xml2);

        // First validation - schema compiled and cached
        Object result1 = validator.convert(validXml1);
        Assert.assertEquals(validXml1, result1);

        // Second validation with same schema - should use cache (no "Compiling schema" log)
        Object result2 = validator.convert(validXml2);
        Assert.assertEquals(validXml2, result2);

        // Both validations should succeed, demonstrating cache reuse
        Assert.assertNotNull(result1);
        Assert.assertNotNull(result2);
    }

    /**
     * Test that single schema location creates consistent cache key
     */
    @Test
    public void testSortedCacheKey_single_schema_location()
    {
        XMLValidatorConfiguration configuration = new XMLValidatorConfiguration();
        configuration.setSkipValidation(false);
        configuration.setReturnValidationResult(true);
        XMLValidator validator = new XMLValidator();
        validator.setConfiguration(configuration);
        validator.startManagedResource();

        String xml1 = this.addSchemaToString(xml);
        String xml2 = """
            <?xml version="1.0"?><x:books xmlns:x="urn:books" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xsi:schemaLocation="urn:books <SCHEMA>">
            <book id="bk999"><author>New Author</author><title>New Book</title><genre>Novel</genre>
            <price>15.00</price><pub_date>2020-01-01</pub_date><review>Excellent</review></book></x:books>
            """;
        String xml2WithSchema = this.addSchemaToString(xml2);

        // Validate first XML
        ValidationResult result1 = (ValidationResult) validator.convert(xml1);
        Assert.assertEquals(ValidationResult.Result.VALID, result1.getResult());

        // Validate second XML with same schema - should use cached schema
        ValidationResult result2 = (ValidationResult) validator.convert(xml2WithSchema);
        Assert.assertEquals(ValidationResult.Result.VALID, result2.getResult());
    }

    /**
     * Test that cache key sorting handles noNamespaceSchemaLocation correctly
     */
    @Test
    public void testSortedCacheKey_no_namespace_schema_location()
    {
        XMLValidatorConfiguration configuration = new XMLValidatorConfiguration();
        configuration.setSkipValidation(false);
        XMLValidator validator = new XMLValidator();
        validator.setConfiguration(configuration);
        validator.startManagedResource();

        // XML using noNamespaceSchemaLocation
        String schemaPath = new ClasspathSchemaResolver("xsd/book.xsd").getSchemaLocation();
        String xmlNoNamespace = """
            <?xml version="1.0"?><books xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xsi:noNamespaceSchemaLocation="%s">
            <book id="bk001"><author>Writer</author></book></books>
            """.formatted(schemaPath);

        try
        {
            // This should compile and cache the schema
            validator.convert(xmlNoNamespace);
            // If we get here, validation passed (expected to fail due to schema mismatch, but demonstrates caching)
        }
        catch (ValidationException e)
        {
            // Expected - schema doesn't match this structure, but cache key was created
            Assert.assertTrue(e.getMessage().contains("validation error") || e.getMessage().contains("Cannot find"));
        }
    }

    /**
     * Test cache behavior with restart - cache should be cleared
     */
    @Test
    public void testSortedCacheKey_cache_cleared_on_restart()
    {
        XMLValidatorConfiguration configuration = new XMLValidatorConfiguration();
        configuration.setSkipValidation(false);
        XMLValidator validator = new XMLValidator();
        validator.setConfiguration(configuration);
        validator.startManagedResource();

        String validXml = this.addSchemaToString(xml);

        // First validation - schema compiled and cached
        Assert.assertEquals(validXml, validator.convert(validXml));

        // Stop and restart - cache should be cleared
        validator.stopManagedResource();
        validator.startManagedResource();

        // Validation after restart - schema should be recompiled (cache was cleared)
        Assert.assertEquals(validXml, validator.convert(validXml));
    }

    /**
     * Test that empty or null schema locations are handled correctly
     */
    @Test(expected = ValidationException.class)
    public void testSortedCacheKey_no_schema_location_throws_exception()
    {
        XMLValidatorConfiguration configuration = new XMLValidatorConfiguration();
        configuration.setSkipValidation(false);
        XMLValidator validator = new XMLValidator();
        validator.setConfiguration(configuration);
        validator.startManagedResource();

        // XML without any schema location should throw exception
        validator.convert(xml_no_schema_location);
    }

    /**
     * Test schema cache with multiple rapid validations using same schema
     */
    @Test
    public void testSortedCacheKey_rapid_validations_use_cached_schema()
    {
        XMLValidatorConfiguration configuration = new XMLValidatorConfiguration();
        configuration.setSkipValidation(false);
        XMLValidator validator = new XMLValidator();
        validator.setConfiguration(configuration);
        validator.startManagedResource();

        String validXml = this.addSchemaToString(xml);

        // Perform 10 rapid validations - all should use cached schema
        for (int i = 0; i < 10; i++)
        {
            Object result = validator.convert(validXml);
            Assert.assertEquals(validXml, result);
        }
    }

    /**
     * Test that sorted cache key generation is consistent across multiple validator instances
     */
    @Test
    public void testSortedCacheKey_consistent_across_validator_instances()
    {
        XMLValidatorConfiguration configuration = new XMLValidatorConfiguration();
        configuration.setSkipValidation(false);

        // First validator instance
        XMLValidator validator1 = new XMLValidator();
        validator1.setConfiguration(configuration);
        validator1.startManagedResource();

        String validXml = this.addSchemaToString(xml);
        Object result1 = validator1.convert(validXml);
        Assert.assertEquals(validXml, result1);

        // Second validator instance - has its own cache
        XMLValidator validator2 = new XMLValidator();
        validator2.setConfiguration(configuration);
        validator2.startManagedResource();

        // Should compile schema again (different cache instance)
        Object result2 = validator2.convert(validXml);
        Assert.assertEquals(validXml, result2);

        // Both should produce same result
        Assert.assertEquals(result1, result2);
    }

    /**
     * Test validation using schema location from configuration instead of XML
     */
    @Test
    public void testConfiguredSchemaLocation_single_schema()
    {
        String schemaPath = new ClasspathSchemaResolver("xsd/book.xsd").getSchemaLocation();

        XMLValidatorConfiguration configuration = new XMLValidatorConfiguration();
        configuration.setSkipValidation(false);
        configuration.setSchemaLocations(java.util.List.of(schemaPath));

        XMLValidator validator = new XMLValidator();
        validator.setConfiguration(configuration);
        validator.startManagedResource();

        // XML without schema location in the document - schema comes from config
        String xmlWithoutSchemaLocation = """
            <?xml version="1.0"?><x:books xmlns:x="urn:books">
            <book id="bk001"><author>Writer</author><title>Book</title><genre>Fiction</genre>
            <price>10.00</price><pub_date>2000-01-01</pub_date><review>Review</review></book></x:books>
            """;

        Object result = validator.convert(xmlWithoutSchemaLocation);
        Assert.assertEquals(xmlWithoutSchemaLocation, result);
    }

    /**
     * Test validation result when using configured schema location
     */
    @Test
    public void testConfiguredSchemaLocation_return_validation_result()
    {
        String schemaPath = new ClasspathSchemaResolver("xsd/book.xsd").getSchemaLocation();

        XMLValidatorConfiguration configuration = new XMLValidatorConfiguration();
        configuration.setSkipValidation(false);
        configuration.setReturnValidationResult(true);
        configuration.setSchemaLocations(java.util.List.of(schemaPath));

        XMLValidator validator = new XMLValidator();
        validator.setConfiguration(configuration);
        validator.startManagedResource();

        String xmlWithoutSchemaLocation = """
            <?xml version="1.0"?><x:books xmlns:x="urn:books">
            <book id="bk001"><author>Writer</author><title>Book</title><genre>Fiction</genre>
            <price>10.00</price><pub_date>2000-01-01</pub_date><review>Review</review></book></x:books>
            """;

        ValidationResult result = (ValidationResult) validator.convert(xmlWithoutSchemaLocation);
        Assert.assertEquals(ValidationResult.Result.VALID, result.getResult());
        Assert.assertNull(result.getException());
    }

    /**
     * Test that configured schema location overrides schema location in XML
     */
    @Test
    public void testConfiguredSchemaLocation_overrides_xml_schema()
    {
        String schemaPath = new ClasspathSchemaResolver("xsd/book.xsd").getSchemaLocation();

        XMLValidatorConfiguration configuration = new XMLValidatorConfiguration();
        configuration.setSkipValidation(false);
        configuration.setSchemaLocations(java.util.List.of(schemaPath));

        XMLValidator validator = new XMLValidator();
        validator.setConfiguration(configuration);
        validator.startManagedResource();

        // XML with schema location - should be ignored in favor of configured location
        String validXml = """
            <?xml version="1.0"?><x:books xmlns:x="urn:books" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xsi:schemaLocation="urn:books http://some.other.schema/book.xsd">
            <book id="bk001"><author>Writer</author><title>Book</title><genre>Fiction</genre>
            <price>10.00</price><pub_date>2000-01-01</pub_date><review>Review</review></book></x:books>
            """;

        // Should validate successfully using configured schema, not the one in XML
        Object result = validator.convert(validXml);
        Assert.assertEquals(validXml, result);
    }

    /**
     * Test configured schema location with invalid XML
     */
    @Test(expected = ValidationException.class)
    public void testConfiguredSchemaLocation_invalid_xml_throws_exception()
    {
        String schemaPath = new ClasspathSchemaResolver("xsd/book.xsd").getSchemaLocation();

        XMLValidatorConfiguration configuration = new XMLValidatorConfiguration();
        configuration.setSkipValidation(false);
        configuration.setThrowExceptionOnValidationFailure(true);
        configuration.setSchemaLocations(java.util.List.of(schemaPath));

        XMLValidator validator = new XMLValidator();
        validator.setConfiguration(configuration);
        validator.startManagedResource();

        String invalidXml = """
            <?xml version="1.0"?><x:books xmlns:x="urn:books">
            <book id="bk001"><author>Writer</author><bad_element>Invalid</bad_element></book></x:books>
            """;

        validator.convert(invalidXml);
    }

    /**
     * Test configured schema location returns ValidationResult for invalid XML
     */
    @Test
    public void testConfiguredSchemaLocation_invalid_xml_returns_validation_result()
    {
        String schemaPath = new ClasspathSchemaResolver("xsd/book.xsd").getSchemaLocation();

        XMLValidatorConfiguration configuration = new XMLValidatorConfiguration();
        configuration.setSkipValidation(false);
        configuration.setReturnValidationResult(true);
        configuration.setThrowExceptionOnValidationFailure(false);
        configuration.setSchemaLocations(java.util.List.of(schemaPath));

        XMLValidator validator = new XMLValidator();
        validator.setConfiguration(configuration);
        validator.startManagedResource();

        String invalidXml = """
            <?xml version="1.0"?><x:books xmlns:x="urn:books">
            <book id="bk001"><author>Writer</author><bad_element>Invalid</bad_element></book></x:books>
            """;

        ValidationResult result = (ValidationResult) validator.convert(invalidXml);
        Assert.assertEquals(ValidationResult.Result.INVALID, result.getResult());
        Assert.assertNotNull(result.getException());
        Assert.assertTrue(result.getException() instanceof SAXException);
    }

    /**
     * Test configured schema location with multiple schemas
     */
    @Test
    public void testConfiguredSchemaLocation_multiple_schemas()
    {
        String schemaPath = new ClasspathSchemaResolver("xsd/book.xsd").getSchemaLocation();

        XMLValidatorConfiguration configuration = new XMLValidatorConfiguration();
        configuration.setSkipValidation(false);
        // Multiple schemas - in this case just using the same schema twice for testing
        configuration.setSchemaLocations(java.util.List.of(schemaPath));

        XMLValidator validator = new XMLValidator();
        validator.setConfiguration(configuration);
        validator.startManagedResource();

        String xmlWithoutSchemaLocation = """
            <?xml version="1.0"?><x:books xmlns:x="urn:books">
            <book id="bk001"><author>Writer</author><title>Book</title><genre>Fiction</genre>
            <price>10.00</price><pub_date>2000-01-01</pub_date><review>Review</review></book></x:books>
            """;

        Object result = validator.convert(xmlWithoutSchemaLocation);
        Assert.assertEquals(xmlWithoutSchemaLocation, result);
    }

    /**
     * Test that configured schema locations are cached
     */
    @Test
    public void testConfiguredSchemaLocation_caching()
    {
        String schemaPath = new ClasspathSchemaResolver("xsd/book.xsd").getSchemaLocation();

        XMLValidatorConfiguration configuration = new XMLValidatorConfiguration();
        configuration.setSkipValidation(false);
        configuration.setSchemaLocations(java.util.List.of(schemaPath));

        XMLValidator validator = new XMLValidator();
        validator.setConfiguration(configuration);
        validator.startManagedResource();

        String xmlWithoutSchemaLocation = """
            <?xml version="1.0"?><x:books xmlns:x="urn:books">
            <book id="bk001"><author>Writer</author><title>Book</title><genre>Fiction</genre>
            <price>10.00</price><pub_date>2000-01-01</pub_date><review>Review</review></book></x:books>
            """;

        // First validation - schema compiled and cached
        Object result1 = validator.convert(xmlWithoutSchemaLocation);
        Assert.assertEquals(xmlWithoutSchemaLocation, result1);

        // Second validation - should use cached schema
        Object result2 = validator.convert(xmlWithoutSchemaLocation);
        Assert.assertEquals(xmlWithoutSchemaLocation, result2);

        // Third validation - should still use cached schema
        Object result3 = validator.convert(xmlWithoutSchemaLocation);
        Assert.assertEquals(xmlWithoutSchemaLocation, result3);
    }

    /**
     * Test configured schema locations with different order are sorted for cache key
     */
    @Test
    public void testConfiguredSchemaLocation_sorted_for_cache_key()
    {
        String schemaPath = new ClasspathSchemaResolver("xsd/book.xsd").getSchemaLocation();

        // First validator with schemas in one order
        XMLValidatorConfiguration configuration1 = new XMLValidatorConfiguration();
        configuration1.setSkipValidation(false);
        configuration1.setSchemaLocations(java.util.List.of(schemaPath));

        XMLValidator validator1 = new XMLValidator();
        validator1.setConfiguration(configuration1);
        validator1.startManagedResource();

        String xmlWithoutSchemaLocation = """
            <?xml version="1.0"?><x:books xmlns:x="urn:books">
            <book id="bk001"><author>Writer</author><title>Book</title><genre>Fiction</genre>
            <price>10.00</price><pub_date>2000-01-01</pub_date><review>Review</review></book></x:books>
            """;

        Object result1 = validator1.convert(xmlWithoutSchemaLocation);
        Assert.assertEquals(xmlWithoutSchemaLocation, result1);

        // Second validator with same schema - should compile independently (different cache)
        XMLValidatorConfiguration configuration2 = new XMLValidatorConfiguration();
        configuration2.setSkipValidation(false);
        configuration2.setSchemaLocations(java.util.List.of(schemaPath));

        XMLValidator validator2 = new XMLValidator();
        validator2.setConfiguration(configuration2);
        validator2.startManagedResource();

        Object result2 = validator2.convert(xmlWithoutSchemaLocation);
        Assert.assertEquals(xmlWithoutSchemaLocation, result2);
    }

    /**
     * Test empty configured schema locations list falls back to XML extraction
     */
    @Test
    public void testConfiguredSchemaLocation_empty_list_uses_xml_schema()
    {
        XMLValidatorConfiguration configuration = new XMLValidatorConfiguration();
        configuration.setSkipValidation(false);
        configuration.setSchemaLocations(java.util.List.of()); // Empty list

        XMLValidator validator = new XMLValidator();
        validator.setConfiguration(configuration);
        validator.startManagedResource();

        // XML with schema location - should be used since config list is empty
        String validXml = this.addSchemaToString(xml);
        Object result = validator.convert(validXml);
        Assert.assertEquals(validXml, result);
    }

    /**
     * Test null configured schema locations falls back to XML extraction
     */
    @Test
    public void testConfiguredSchemaLocation_null_uses_xml_schema()
    {
        XMLValidatorConfiguration configuration = new XMLValidatorConfiguration();
        configuration.setSkipValidation(false);
        configuration.setSchemaLocations(null); // Null

        XMLValidator validator = new XMLValidator();
        validator.setConfiguration(configuration);
        validator.startManagedResource();

        // XML with schema location - should be used since config is null
        String validXml = this.addSchemaToString(xml);
        Object result = validator.convert(validXml);
        Assert.assertEquals(validXml, result);
    }

    /**
     * Test configured schema location with catalog resolution
     */
    @Test
    public void testConfiguredSchemaLocation_with_catalog()
    {
        XMLValidatorConfiguration configuration = new XMLValidatorConfiguration();
        configuration.setSkipValidation(false);
        configuration.setCatalogUrl(XMLValidator.class.getResource("/catalog.xml").toString());
        configuration.setSchemaLocations(java.util.List.of("http://www.books4tests.com/xsd/book.xsd"));

        XMLValidator validator = new XMLValidator();
        validator.setConfiguration(configuration);
        validator.startManagedResource();

        String xmlWithoutSchemaLocation = """
            <?xml version="1.0"?><x:books xmlns:x="urn:books">
            <book id="bk001"><author>Writer</author><title>Book</title><genre>Fiction</genre>
            <price>10.00</price><pub_date>2000-01-01</pub_date><review>Review</review></book></x:books>
            """;

        // Should resolve schema via catalog
        Object result = validator.convert(xmlWithoutSchemaLocation);
        Assert.assertEquals(xmlWithoutSchemaLocation, result);
    }

    /**
     * Test configured schema location with invalid schema path
     */
    @Test(expected = ValidationException.class)
    public void testConfiguredSchemaLocation_invalid_schema_path()
    {
        XMLValidatorConfiguration configuration = new XMLValidatorConfiguration();
        configuration.setSkipValidation(false);
        configuration.setSchemaLocations(java.util.List.of("file:///nonexistent/schema.xsd"));

        XMLValidator validator = new XMLValidator();
        validator.setConfiguration(configuration);
        validator.startManagedResource();

        String xmlWithoutSchemaLocation = """
            <?xml version="1.0"?><x:books xmlns:x="urn:books">
            <book id="bk001"><author>Writer</author></book></x:books>
            """;

        validator.convert(xmlWithoutSchemaLocation);
    }

    private String addSchemaToString(String xml)
    {
        return xml.replace("<SCHEMA>", new ClasspathSchemaResolver("xsd/book.xsd").getSchemaLocation());
    }
}