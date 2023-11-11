package org.ikasan.component.validator.xml;

import org.ikasan.component.validator.ValidationException;
import org.ikasan.component.validator.ValidationResult;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.imposters.ByteBuddyClassImposteriser;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by elliga on 18/11/2015.
 */
@Disabled
class XMLValidatorTest
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

    /**
     * Mockery for mocking concrete classes
     */
    private Mockery classMockery = new Mockery()
    {
        {
            setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
        }
    };
    //    States test = classMockery.states("test");

    /**
     * SAX Parser factory mockery
     */
    final SAXParserFactory factory = this.classMockery.mock(SAXParserFactory.class, "saxParserFactory");

    /**
     * SAX Parser mockery
     */
    final SAXParser saxParser = this.classMockery.mock(SAXParser.class, "saxParser");

    /**
     * SAX Parser mockery
     */
    final XMLReader xmlReader = this.classMockery.mock(XMLReader.class, "xmlReader");

    @Test
    void test_null_Parser_factory_on_construction()
        throws ParserConfigurationException, IOException, SAXException
    {
        assertThrows(IllegalArgumentException.class, () -> {
            new XMLValidator(null);
        });
    }

    @Test
    void testParseValidate_against_classpath_xml_pass()
        throws ParserConfigurationException, IOException, SAXException
    {
        XMLValidatorConfiguration configuration = new XMLValidatorConfiguration();
        configuration.setSkipValidation(false);
        XMLValidator validator = new XMLValidator(SAXParserFactory.newInstance());
        validator.setConfiguration(configuration);
        validator.startManagedResource();
        assertEquals(validator.convert(this.addSchemaToString(xml)), this.addSchemaToString(xml));
    }

    @Test
    void testParseValidate_against_classpath_xml_pass_return_validation_result()
        throws ParserConfigurationException, IOException, SAXException
    {
        XMLValidatorConfiguration configuration = new XMLValidatorConfiguration();
        configuration.setSkipValidation(false);
        configuration.setReturnValidationResult(true);
        XMLValidator validator = new XMLValidator(SAXParserFactory.newInstance());
        validator.setConfiguration(configuration);
        validator.startManagedResource();
        assertEquals(ValidationResult.class, validator.convert(this.addSchemaToString(xml)).getClass());
    }

    @Test
    void testParseValidate_against_classpath_xml_pass_call_twice_to_make_sure_xml_readr_is_reused()
        throws ParserConfigurationException, IOException, SAXException
    {
        XMLValidatorConfiguration configuration = new XMLValidatorConfiguration();
        configuration.setSkipValidation(false);
        XMLValidator validator = new XMLValidator(SAXParserFactory.newInstance());
        validator.setConfiguration(configuration);
        validator.startManagedResource();
        assertEquals(validator.convert(this.addSchemaToString(xml)), this.addSchemaToString(xml));
        assertEquals(validator.convert(this.addSchemaToString(xml)), this.addSchemaToString(xml));
        assertEquals(1, validator.xmlReaders.size());
    }

    @Test
    void testParseValidate_against_classpath_xml_fail()
        throws ParserConfigurationException, IOException, SAXException
    {
        assertThrows(ValidationException.class, () -> {
            XMLValidatorConfiguration configuration = new XMLValidatorConfiguration();
            configuration.setSkipValidation(false);
            XMLValidator validator = new XMLValidator(SAXParserFactory.newInstance());
            validator.setConfiguration(configuration);
            validator.startManagedResource();
            validator.convert(this.addSchemaToString(xml_bad));
        });
    }

    @Test
    void testParseValidate_skip_validation_return_source()
        throws ParserConfigurationException, IOException, SAXException
    {
        XMLValidatorConfiguration configuration = new XMLValidatorConfiguration();
        configuration.setSkipValidation(true);
        configuration.setReturnValidationResult(false);
        XMLValidator validator = new XMLValidator(SAXParserFactory.newInstance());
        validator.setConfiguration(configuration);
        validator.startManagedResource();
        assertEquals(validator.convert(this.addSchemaToString(xml)), this.addSchemaToString(xml));
    }

    @Test
    void testParseValidate_skip_validation_return_validation_result()
        throws ParserConfigurationException, IOException, SAXException
    {
        XMLValidatorConfiguration configuration = new XMLValidatorConfiguration();
        configuration.setSkipValidation(true);
        configuration.setReturnValidationResult(true);
        XMLValidator validator = new XMLValidator(SAXParserFactory.newInstance());
        validator.setConfiguration(configuration);
        validator.startManagedResource();
        assertEquals(ValidationResult.class, validator.convert(this.addSchemaToString(xml)).getClass());
    }

    @Test
    void test_exception_parser_setup_exception() throws ParserConfigurationException, IOException, SAXException
    {
        assertThrows(RuntimeException.class, () -> {
            this.classMockery.checking(new Expectations()
            {
                {
                    exactly(1).of(factory).setValidating(true);
                    exactly(1).of(factory).setNamespaceAware(true);
                    exactly(1).of(factory).newSAXParser();
                    will(throwException(new ParserConfigurationException("something went wrong!")));
                }
            });
            XMLValidatorConfiguration configuration = new XMLValidatorConfiguration();
            configuration.setSkipValidation(true);
            configuration.setReturnValidationResult(true);
            XMLValidator validator = new XMLValidator(factory);
            validator.setConfiguration(configuration);
            validator.startManagedResource();
        });
    }

    @Test
    void test_exception_parse_exception() throws ParserConfigurationException, IOException, SAXException
    {
        assertThrows(ValidationException.class, () -> {
            this.classMockery.checking(new Expectations()
            {
                {
                    exactly(1).of(factory).setValidating(true);
                    exactly(1).of(factory).setNamespaceAware(true);
                    exactly(1).of(factory).newSAXParser();
                    will(returnValue(saxParser));
                    exactly(1).of(saxParser).setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage",
                        "http://www.w3.org/2001/XMLSchema");
                    exactly(1).of(saxParser).getXMLReader();
                    will(returnValue(xmlReader));
                    exactly(1).of(xmlReader).setErrorHandler(with(any(ErrorHandler.class)));
                    exactly(1).of(xmlReader).setProperty(with(any(String.class)), with(any(Object.class)));
                    exactly(1).of(factory).setValidating(true);
                    exactly(1).of(factory).setNamespaceAware(true);
                    exactly(1).of(factory).newSAXParser();
                    will(returnValue(saxParser));
                    exactly(1).of(saxParser).setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage",
                        "http://www.w3.org/2001/XMLSchema");
                    exactly(1).of(saxParser).getXMLReader();
                    will(returnValue(xmlReader));
                    exactly(1).of(xmlReader).setErrorHandler(with(any(ErrorHandler.class)));
                    exactly(1).of(xmlReader).setProperty(with(any(String.class)), with(any(Object.class)));
                    exactly(1).of(xmlReader).parse(with(any(InputSource.class)));
                    will(throwException(new SAXException("something went wrong!")));
                }
            });
            XMLValidatorConfiguration configuration = new XMLValidatorConfiguration();
            configuration.setSkipValidation(false);
            configuration.setReturnValidationResult(false);
            configuration.setThrowExceptionOnValidationFailure(true);
            XMLValidator validator = new XMLValidator(factory);
            validator.setConfiguration(configuration);
            validator.startManagedResource();
            validator.convert(this.addSchemaToString(xml));
        });
    }

    @Test
    void test_exception_io_exception() throws ParserConfigurationException, IOException, SAXException
    {
        assertThrows(ValidationException.class, () -> {
            this.classMockery.checking(new Expectations()
            {
                {
                    exactly(1).of(factory).setValidating(true);
                    exactly(1).of(factory).setNamespaceAware(true);
                    exactly(1).of(factory).newSAXParser();
                    will(returnValue(saxParser));
                    exactly(1).of(saxParser).setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage",
                        "http://www.w3.org/2001/XMLSchema");
                    exactly(1).of(saxParser).getXMLReader();
                    will(returnValue(xmlReader));
                    exactly(1).of(xmlReader).setErrorHandler(with(any(ErrorHandler.class)));
                    exactly(1).of(xmlReader).setProperty(with(any(String.class)), with(any(Object.class)));
                    exactly(1).of(factory).setValidating(true);
                    exactly(1).of(factory).setNamespaceAware(true);
                    exactly(1).of(factory).newSAXParser();
                    will(returnValue(saxParser));
                    exactly(1).of(saxParser).setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage",
                        "http://www.w3.org/2001/XMLSchema");
                    exactly(1).of(saxParser).getXMLReader();
                    will(returnValue(xmlReader));
                    exactly(1).of(xmlReader).setErrorHandler(with(any(ErrorHandler.class)));
                    exactly(1).of(xmlReader).setProperty(with(any(String.class)), with(any(Object.class)));
                    exactly(1).of(xmlReader).parse(with(any(InputSource.class)));
                    will(throwException(new IOException("something went wrong!")));
                }
            });
            XMLValidatorConfiguration configuration = new XMLValidatorConfiguration();
            configuration.setSkipValidation(false);
            configuration.setReturnValidationResult(false);
            configuration.setThrowExceptionOnValidationFailure(true);
            XMLValidator validator = new XMLValidator(factory);
            validator.setConfiguration(configuration);
            validator.startManagedResource();
            validator.convert(this.addSchemaToString(xml));
        });
    }

    @Test
    void test_exception_io_exception_return_validation_result()
        throws ParserConfigurationException, IOException, SAXException
    {
        this.classMockery.checking(new Expectations()
        {
            {
                exactly(1).of(factory).setValidating(true);
                exactly(1).of(factory).setNamespaceAware(true);
                exactly(1).of(factory).newSAXParser();
                will(returnValue(saxParser));
                exactly(1).of(saxParser).setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage",
                        "http://www.w3.org/2001/XMLSchema");
                exactly(1).of(saxParser).getXMLReader();
                will(returnValue(xmlReader));
                exactly(1).of(xmlReader).setErrorHandler(with(any(ErrorHandler.class)));
                exactly(1).of(xmlReader).setProperty(with(any(String.class)), with(any(Object.class)));
                exactly(1).of(factory).setValidating(true);
                exactly(1).of(factory).setNamespaceAware(true);
                exactly(1).of(factory).newSAXParser();
                will(returnValue(saxParser));
                exactly(1).of(saxParser).setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage",
                        "http://www.w3.org/2001/XMLSchema");
                exactly(1).of(saxParser).getXMLReader();
                will(returnValue(xmlReader));
                exactly(1).of(xmlReader).setErrorHandler(with(any(ErrorHandler.class)));
                exactly(1).of(xmlReader).setProperty(with(any(String.class)), with(any(Object.class)));
                exactly(1).of(xmlReader).parse(with(any(InputSource.class)));
                will(throwException(new IOException("something went wrong!")));
            }
        });
        XMLValidatorConfiguration configuration = new XMLValidatorConfiguration();
        configuration.setSkipValidation(false);
        configuration.setReturnValidationResult(true);
        configuration.setThrowExceptionOnValidationFailure(false);
        XMLValidator validator = new XMLValidator(factory);
        validator.setConfiguration(configuration);
        validator.startManagedResource();
        Object result = validator.convert(this.addSchemaToString(xml));
        assertTrue(result instanceof ValidationResult);
        assertTrue(((ValidationResult) result).getException() instanceof IOException);
        assertTrue(((ValidationResult) result).getResult() == ValidationResult.Result.INVALID);
    }

    @Test
    void testParseValidate_against_url_with_catalog_xml_pass()
        throws ParserConfigurationException, IOException, SAXException
    {
        XMLValidatorConfiguration configuration = new XMLValidatorConfiguration();
        configuration.setSkipValidation(false);
        configuration.setCatalogUrl(XMLValidator.class.getResource("/catalog.xml").toString());
        XMLValidator validator = new XMLValidator(SAXParserFactory.newInstance());
        validator.setConfiguration(configuration);
        validator.startManagedResource();
        assertEquals(validator.convert(xml_with_schema_url), xml_with_schema_url);
    }

    private String addSchemaToString(String xml)
    {
        return xml.replace("<SCHEMA>", new ClasspathSchemaResolver("xsd/book.xsd").getSchemaLocation());
    }
}