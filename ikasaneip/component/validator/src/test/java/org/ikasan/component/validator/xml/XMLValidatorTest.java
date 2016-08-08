package org.ikasan.component.validator.xml;

import org.ikasan.component.validator.ValidationException;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.States;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by elliga on 18/11/2015.
 */
public class XMLValidatorTest {

    private String xml = "<?xml version=\"1.0\"?><x:books xmlns:x=\"urn:books\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" " +
            "xmlns:fo=\"http://www.w3.org/1999/XSL/Format\" xsi:schemaLocation=\"urn:books <SCHEMA>\">   <book id=\"bk001\">      " +
            "<author>Writer</author>      <title>The First Book</title>      <genre>Fiction</genre>      " +
            "<price>44.95</price>      <pub_date>2000-10-01</pub_date>      <review>An amazing s" +
            "tory of nothing.</review>   </book>   <book id=\"bk002\">      <author>Poet</author>      " +
            "<title>The Poet's First Poem</title>      <genre>Poem</genre>      <price>24.95</price>      " +
            "<pub_date>2000-10-01</pub_date><review>Least poetic poems.</review>   </book></x:books>";

    private String xml_bad = "<?xml version=\"1.0\"?><x:books xmlns:x=\"urn:books\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" " +
            "xmlns:fo=\"http://www.w3.org/1999/XSL/Format\" xsi:schemaLocation=\"urn:books <SCHEMA>\">   <book id=\"bk001\">      " +
            "<author>Writer</author>      <title>The First Book</title>      <genre>Fiction</genre>      " +
            "<price>44.95</price>      <pub_date>2000-10-01</pub_date>      <review>An amazing s" +
            "tory of nothing.</review>   </book>   <book id=\"bk002\">      <author>Poet</author>      " +
            "<title>The Poet's First Poem</title>      <genre>Poem</genre>      <price>24.95</price>      " +
            "<pub_date>2000-10-01</pub_date><review>Least poetic poems.</review> <bad_element>stuff</bad_element>  </book></x:books>";

    private XMLValidator validator;

    private DocumentBuilderFactory documentBuilderFactory;

    private DocumentBuilder documentBuilder;

    private XMLValidatorConfiguration configuration = new XMLValidatorConfiguration();

    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    States test = mockery.states("test");

    @Before
    public void setup()
    {
        this.configuration.setSkipValidation(false);

        validator = new XMLValidator(SAXParserFactory.newInstance());
        validator.setConfiguration(configuration);

        test.become("fully-set-up");
    }


    @Test
    public void testParseValidate_against_classpath_xml_pass() throws ParserConfigurationException, IOException, SAXException
    {
        validator.startManagedResource();
        validator.convert(this.addSchemaToString(xml));
    }

    @Test (expected = ValidationException.class)
    public void testParseValidate_against_classpath_xml_fail() throws ParserConfigurationException, IOException, SAXException
    {
        validator.startManagedResource();
        validator.convert(this.addSchemaToString(xml_bad));
    }

    private String addSchemaToString(String xml)
    {
        return xml.replace("<SCHEMA>", new ClasspathSchemaResolver("xsd/book.xsd").getSchemaLocation());
    }

}