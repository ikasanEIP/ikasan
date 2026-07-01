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

    private String addSchemaToString(String xml)
    {
        return xml.replace("<SCHEMA>", new ClasspathSchemaResolver("xsd/book.xsd").getSchemaLocation());
    }
}