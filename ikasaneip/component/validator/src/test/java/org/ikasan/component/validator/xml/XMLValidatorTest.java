package org.ikasan.component.validator.xml;

import org.ikasan.component.validator.ValidationException;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by elliga on 18/11/2015.
 */
public class XMLValidatorTest {

    private XMLValidator validator;

    private DocumentBuilderFactory documentBuilderFactory;

    private DocumentBuilder documentBuilder;

    private XMLValidatorConfiguration configuration;

    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    @Before
    public void setup() {

        documentBuilderFactory = mockery.mock(DocumentBuilderFactory.class);
        documentBuilder = mockery.mock(DocumentBuilder.class);
        configuration = mockery.mock(XMLValidatorConfiguration.class);

        validator = new XMLValidator(documentBuilderFactory);
        validator.setConfiguration(configuration);
    }

    @Test
    public void testSaxException() throws ParserConfigurationException, IOException, SAXException {

        // expectations
        mockery.checking(new Expectations() {
            {
                oneOf(configuration).isSkipValidation();
                will(returnValue(false));
                oneOf(documentBuilderFactory).newDocumentBuilder();
                will(returnValue(documentBuilder));
                oneOf(documentBuilder).setErrorHandler(with(any(ErrorHandler.class)));
                oneOf(configuration).isThrowExceptionOnValidationFailure();
                will(returnValue(true));
                oneOf(documentBuilder).parse(with(any(InputStream.class)));
                will(throwException(new SAXException("SAX Error Message")));
            }
        });

        try {
            validator.convert("<xml>payload</xml>");
        } catch (ValidationException e) {

            String expectedErrorMessage = "XML validation error: SAX Error Message\n" +
                    "\n" +
                    "XML:\n" +
                    "<xml>payload</xml>";

            Assert.assertEquals(expectedErrorMessage, e.getMessage());
        }

        mockery.assertIsSatisfied();

    }

}