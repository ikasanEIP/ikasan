package org.ikasan.component.validator.xml;


import org.ikasan.component.validator.ValidationException;
import org.ikasan.component.validator.ValidationResult;
import org.ikasan.spec.component.transformation.TransformationException;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.States;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.*;
import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Tests the XMLValidator
 */
@Ignore
public class XMLValidatorWhenReturnValidationResultIsTrueTest
{
    /**
     * Mockery for mocking concrete classes
     */
    private Mockery classMockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    States test = classMockery.states("test");

    /** Document builder factory mockery */
    final DocumentBuilderFactory factory = this.classMockery.mock(DocumentBuilderFactory.class, "documentBuilderFactory");

    /** Document builder mockery */
    final DocumentBuilder builder = this.classMockery.mock(DocumentBuilder.class, "documentBuilder");

    /** class to be tested */
    private XMLValidator uut = null;

    @Before
    public void setup()
    {
        this.classMockery.checking(new Expectations()
        {
            {
                ignoring (factory).setAttribute("org.apache.xerces.xni.parser.XMLParserConfiguration", "org.apache.xerces.parsers.XMLGrammarCachingConfiguration");
                when(test.isNot("fully-set-up"));
            }
        });

        // create the class to be tested
        this.uut = new XMLValidator(SAXParserFactory.newInstance());
        XMLValidatorConfiguration configuration = new XMLValidatorConfiguration();
        configuration.setReturnValidationResult(true);
        uut.setConfiguration(configuration);

        test.become("fully-set-up");
    }

    @Test
    public void test_successfulXmlValidation()
            throws TransformationException, ParserConfigurationException,
            SAXException, IOException
    {

        final String payloadContent =
                ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                        + "<rootName/>");
        final InputStream is = new ByteArrayInputStream(payloadContent.getBytes());
        final Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
        this.classMockery.checking(new Expectations()
        {
            {

                exactly(1).of(factory).newDocumentBuilder();
                will(returnValue(builder));
                exactly(1).of(builder).setErrorHandler(with(any(ErrorHandler.class)));
                exactly(1).of(builder).parse(with(any(ByteArrayInputStream.class)));
                will(returnValue(document));
            }
        });

        ValidationResult result = (ValidationResult)this.uut.convert(payloadContent);

        //assert
        Assert.assertTrue("Document should be valid", result.getResult().equals(ValidationResult.Result.VALID));

    }

    @Ignore
    @Test
    public void test_successfulXmlValidation_without_mocking()
            throws TransformationException, ParserConfigurationException,
            SAXException, IOException
    {

        javax.xml.parsers.DocumentBuilderFactory documentBuilderFactory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setValidating(true);
        documentBuilderFactory.setNamespaceAware(true);

        this.uut = new XMLValidator(SAXParserFactory.newInstance());
        XMLValidatorConfiguration configuration = new XMLValidatorConfiguration();
        uut.setConfiguration(configuration);

        final String payloadContent =
                ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                        + "<a/>");

        ValidationResult result = (ValidationResult)this.uut.convert(payloadContent);

        Assert.assertTrue("Document should be valid", result.getResult().equals(ValidationResult.Result.VALID));

    }


    @Test
    public void failed_when_nonXmlPayload()
            throws TransformationException, ParserConfigurationException,
            SAXException, IOException
    {

        final String payloadContent =
                ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                        + "<rootName/>");
        this.classMockery.checking(new Expectations()
        {
            {

                exactly(1).of(factory).newDocumentBuilder();
                will(returnValue(builder));
                exactly(1).of(builder).setErrorHandler(with(any(ErrorHandler.class)));
                exactly(1).of(builder).parse(with(any(ByteArrayInputStream.class)));
                will(throwException(new SAXException("Not a valid XML document!")));
            }
        });

        ValidationResult result = (ValidationResult)this.uut.convert(payloadContent);

        Assert.assertTrue(result.getResult().equals(ValidationResult.Result.INVALID));
        Assert.assertTrue(result.getException() instanceof SAXException );

    }


    @Test(expected = ValidationException.class)
    public void failed_when_nonXmlPayload_and_ThrowExceptionOnValidationFailure_is_true()
            throws TransformationException, ParserConfigurationException,
            SAXException, IOException
    {

        XMLValidatorConfiguration configuration = new XMLValidatorConfiguration();
        configuration.setThrowExceptionOnValidationFailure(true);
        uut.setConfiguration(configuration);

        final String payloadContent =
                ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                        + "<rootName/>");
        this.classMockery.checking(new Expectations() {
            {

                exactly(1).of(factory).newDocumentBuilder();
                will(returnValue(builder));
                exactly(1).of(builder).setErrorHandler(with(any(ErrorHandler.class)));
                exactly(1).of(builder).parse(with(any(ByteArrayInputStream.class)));
                will(throwException(new SAXException("Not a valid XML document!")));
            }
        });

        this.uut.convert(payloadContent);

    }

    @Test
    public void failed_when_nonXmlPayload_throws_IOException()
            throws TransformationException, ParserConfigurationException,
            SAXException, IOException
    {

        final String payloadContent =
                ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                        + "<rootName/>");
        this.classMockery.checking(new Expectations()
        {
            {

                exactly(1).of(factory).newDocumentBuilder();
                will(returnValue(builder));
                exactly(1).of(builder).setErrorHandler(with(any(ErrorHandler.class)));
                exactly(1).of(builder).parse(with(any(ByteArrayInputStream.class)));
                will(throwException(new IOException("schema not found")));
            }
        });

        ValidationResult result = (ValidationResult)this.uut.convert(payloadContent);

        Assert.assertTrue(result.getResult().equals(ValidationResult.Result.INVALID));
        Assert.assertTrue(result.getException() instanceof IOException );

    }


    @Test(expected = ValidationException.class)
    public void failed_when_nonXmlPayload_throws_IOException_and_ThrowExceptionOnValidationFailure_is_true()
            throws TransformationException, ParserConfigurationException,
            SAXException, IOException
    {

        XMLValidatorConfiguration configuration = new XMLValidatorConfiguration();
        configuration.setThrowExceptionOnValidationFailure(true);
        uut.setConfiguration(configuration);

        final String payloadContent =
                ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                        + "<rootName/>");
        this.classMockery.checking(new Expectations() {
            {

                exactly(1).of(factory).newDocumentBuilder();
                will(returnValue(builder));
                exactly(1).of(builder).setErrorHandler(with(any(ErrorHandler.class)));
                exactly(1).of(builder).parse(with(any(ByteArrayInputStream.class)));
                will(throwException(new IOException("schema not found")));
            }
        });

        this.uut.convert(payloadContent);

    }


    @Test
    public void failed_when_nonXmlPayload_throws_ParserConfigurationException()
            throws TransformationException, ParserConfigurationException,
            SAXException, IOException
    {

        final String payloadContent =
                ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                        + "<rootName/>");
        this.classMockery.checking(new Expectations()
        {
            {

                exactly(1).of(factory).newDocumentBuilder();
                will(throwException(new ParserConfigurationException("schema not found")));
            }
        });

        ValidationResult result = (ValidationResult)this.uut.convert(payloadContent);

        Assert.assertTrue(result.getResult().equals(ValidationResult.Result.INVALID));
        Assert.assertTrue(result.getException() instanceof ParserConfigurationException );

    }


    @Test(expected = ValidationException.class)
    public void failed_when_nonXmlPayload_throws_ParserConfigurationException_and_ThrowExceptionOnValidationFailure_is_true()
            throws TransformationException, ParserConfigurationException,
            SAXException, IOException
    {

        XMLValidatorConfiguration configuration = new XMLValidatorConfiguration();
        configuration.setThrowExceptionOnValidationFailure(true);
        uut.setConfiguration(configuration);

        final String payloadContent =
                ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                        + "<rootName/>");
        this.classMockery.checking(new Expectations() {
            {

                exactly(1).of(factory).newDocumentBuilder();
                will(throwException(new ParserConfigurationException("schema not found")));

            }
        });

        this.uut.convert(payloadContent);

    }


}
