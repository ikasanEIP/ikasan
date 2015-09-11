/* 
 * $Id$
 * $URL$
 *
 * =============================================================================
 * Ikasan Enterprise Integration Platform
 * 
 * Distributed under the Modified BSD License.
 * Copyright notice: The copyright for this software and a full listing 
 * of individual contributors are as shown in the packaged copyright.txt 
 * file. 
 * 
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 *
 *  - Neither the name of the ORGANIZATION nor the names of its contributors may
 *    be used to endorse or promote products derived from this software without 
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE 
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE 
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * =============================================================================
 */
package org.ikasan.component.converter.xml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.component.transformation.TransformationException;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.InputSource;

/**
 * Test class for {@link org.ikasan.component.converter.xml.XsltConverter}
 * 
 * @author Ikasan Development Team
 * 
 */
@SuppressWarnings("unqualified-field-access")
public class XsltConverterTest
{
    private final static String CLASSPATH_URL_PREFIX = "classpath:";

    /** Mockery for objects */
    private final Mockery mockery = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** Transformer factory */
    private final TransformerFactory mockTransformerFactory = this.mockery.mock(TransformerFactory.class, "transformerFactory");

    /** Templates */
    private final Templates mockTemplates = this.mockery.mock(Templates.class, "templates");

    /** Transformer */
    private final Transformer mockTransformer = this.mockery.mock(Transformer.class, "transformer");

    /** The test object */
    private XsltConverter uut;


    /** Dummy xml content expected from transformation */
    private static final String DUMMY_CONTENT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><dummy />";


    /** Default component configuration object */
    private XsltConverterConfiguration defaultConfig;

    private Object testEvent;

    /**
     * Setup common objects used to prepare each test case
     */
    @Before public void setup()
    {
        // Setup test objects

        this.testEvent = new String();

        this.defaultConfig = new XsltConverterConfiguration();
        this.defaultConfig.setStylesheetLocation("classpath:anyStylesheet.xsl");
    }

    /**
     * Creating a XsltConverter will fail if injected {@link javax.xml.transform.TransformerFactory} is null.
     */
    @SuppressWarnings("unused")
    @Test(expected=IllegalArgumentException.class)
    public void construction_fails_null_transformerFactory()
    {
        new XsltConverter(null);
    }

    /**
     * Test successful transformation using configured stylesheet where use of translets
     * is switched on. No extra parameters are set on transformer.
     * 
     * @throws javax.xml.transform.TransformerException Thrown if error transforming event content.
     */
    @Test
    public void transformation_successful_with_default_setup() throws TransformerException
    {
        // Setup expectations
        this.mockery.checking(new Expectations()
        {
            {
                // Must setup a custom ErrorListener to control error handling for processing stylesheet
                one(mockTransformerFactory).setErrorListener(with(any(ExceptionThrowingErrorListener.class)));

                // Because using translets was switched on, create Templates from factory
                one(mockTransformerFactory).newTemplates(with(any(Source.class))); will(returnValue(mockTemplates));

                // Create a new Transformer instance
                one(mockTemplates).newTransformer(); will(returnValue(mockTransformer));

                // Transformer instance needs its own ErrorListener to control error handling for transformations
                one(mockTransformer).setErrorListener(with(any(ExceptionThrowingErrorListener.class)));

                // The transformation itself
                one(mockTransformer).transform(with(any(Source.class)), with(any(Result.class)));
            }
        });

        // Run the test:
        this.uut = new XsltConverter(this.mockTransformerFactory);
        this.runTest();

        // Make assertions
        this.mockery.assertIsSatisfied();
    }

    /**
     * Test successful transformation using configured stylesheet where use of translets
     * is switched off. No extra parameters are set on transformer.
     * 
     * @throws javax.xml.transform.TransformerException Thrown if error transforming event content.
     */
    @Test
    public void transformation_successful_without_using_translets() throws TransformerException
    {
        // Changing the configuration to test other branch of code
        this.defaultConfig.setUseTranslets(false);
        this.defaultConfig.setStylesheetLocation("file://anyStylesheet.xsl");

        // Setup expectations
        this.mockery.checking(new Expectations()
        {
            {
                // Must setup a custom ErrorListener to control error handling for processing stylesheet
                one(mockTransformerFactory).setErrorListener(with(any(ExceptionThrowingErrorListener.class)));

                // Because using translets was switched off, create Transformer directly from factory
                one(mockTransformerFactory).newTransformer(with(any(Source.class))); will(returnValue(mockTransformer));

                // Transformer instance needs its own ErrorListener to control error handling for transformations
                one(mockTransformer).setErrorListener(with(any(ExceptionThrowingErrorListener.class)));

                // The transformation itself
                one(mockTransformer).transform(with(any(Source.class)), with(any(Result.class)));
            }
        });

        // Run the test:
        this.uut = new XsltConverter(this.mockTransformerFactory);
        this.runTest();

        // Make assertions
        this.mockery.assertIsSatisfied();
    }

    /**
     * Test successful transformation using the default configuration but with
     * extra transformation parameters to be set on {@link javax.xml.transform.Transformer}
     * 
     * @throws javax.xml.transform.TransformerException Thrown if error transforming event content.
     */
    @Test
    public void transformation_successful_with_extra_tranformationParams_configured() throws TransformerException
    {
        // Setup test objects
        final Map<String, String> transformationParams = new HashMap<String, String>();
        transformationParams.put("name", "value");

        // Setup expectations
        this.mockery.checking(new Expectations() {
            {
                // Must setup a custom ErrorListener to control error handling for processing stylesheet
                one(mockTransformerFactory).setErrorListener(with(any(ExceptionThrowingErrorListener.class)));

                // Because using translets was switched on, create Templates from factory
                one(mockTransformerFactory).newTemplates(with(any(Source.class)));
                will(returnValue(mockTemplates));

                // Create a new Transformer instance
                one(mockTemplates).newTransformer();
                will(returnValue(mockTransformer));

                // Setting the provided parameters on transformer
                one(mockTransformer).setParameter("name", "value");

                // Transformer instance needs its own ErrorListener to control error handling for tranformations
                one(mockTransformer).setErrorListener(with(any(ExceptionThrowingErrorListener.class)));

                // The transformation itself
                one(mockTransformer).transform(with(any(Source.class)), with(any(Result.class)));
            }
        });

        // Run the test:
        this.uut = new XsltConverter(this.mockTransformerFactory);
        // Setup the transformer to be tested with extra transformation parameters
        this.uut.setTransformationParameters(transformationParams);
        this.runTest();

        // Make assertions
        this.mockery.assertIsSatisfied();
    }

    /**
     * Sometimes the default URIResolver provided by implementation cannot be used
     * to resolve resources from classpath. Therefore, a custom URIResolver implementation
     * must be configured on created {@link javax.xml.transform.Transformer} instance
     * 
     * @throws javax.xml.transform.TransformerException Thrown if error transforming event content.
     */
    @Test
    public void transformation_successful_with_custom_uriResolver() throws TransformerException
    {
        // Setup test objects
        final URIResolver mockURIResolver = this.mockery.mock(URIResolver.class, "customURIResolver");

        // Setup expectations
        this.mockery.checking(new Expectations()
        {
            {
                // Must setup a custom ErrorListener to control error handling for processing stylesheet
                one(mockTransformerFactory).setErrorListener(with(any(ExceptionThrowingErrorListener.class)));

                // Because using translets was switched on, create Templates from factory
                one(mockTransformerFactory).newTemplates(with(any(Source.class))); will(returnValue(mockTemplates));

                // Create a new Transformer instance
                one(mockTemplates).newTransformer(); will(returnValue(mockTransformer));

                // Setting the custom URI resolver on transformer
                one(mockTransformer).setURIResolver(mockURIResolver);

                // Transformer instance needs its own ErrorListener to control error handling for tranformations
                one(mockTransformer).setErrorListener(with(any(ExceptionThrowingErrorListener.class)));

                // The transformation itself
                one(mockTransformer).transform(with(any(Source.class)), with(any(Result.class)));
            }
        });

        // Run the test:
        this.uut = new XsltConverter(this.mockTransformerFactory);
        // Setup the transformer being tested with a custom uri resolver
        this.uut.setURIResolver(mockURIResolver);
        this.runTest();

        // Make assertions
        this.mockery.assertIsSatisfied();
    }

    /**
     * Transformation fail
     * 
     * @throws javax.xml.transform.TransformerException Thrown if error transforming event content.
     */
    @Test(expected=TransformationException.class)
    public void transformation_fail() throws TransformerException
    {
        // Setup test objects
        final TransformerException expectedException = new TransformerException("Transformation fail for some reason or another.");

        // Setup expectations
        this.mockery.checking(new Expectations()
        {
            {
                // Must setup a custom ErrorListener to control error handling for processing stylesheet
                one(mockTransformerFactory).setErrorListener(with(any(ExceptionThrowingErrorListener.class)));

                // Because using translets was switched on, create Templates from factory
                one(mockTransformerFactory).newTemplates(with(any(Source.class))); will(returnValue(mockTemplates));

                // Create a new Transformer instance
                one(mockTemplates).newTransformer(); will(returnValue(mockTransformer));

                // Transformer instance needs its own ErrorListener to control error handling for tranformations
                one(mockTransformer).setErrorListener(with(any(ExceptionThrowingErrorListener.class)));

                // The transformation itself
                one(mockTransformer).transform(with(any(Source.class)), with(any(Result.class)));will(throwException(expectedException));
            }
        });

        // Run the test:
        this.uut = new XsltConverter(this.mockTransformerFactory);
        this.runTest();

        // Make assertions
        this.mockery.assertIsSatisfied();
    }

    /**
     * If the configured stylesheet is not found, creating a {@link javax.xml.transform.Transformer} instance
     * will fail
     * 
     * @throws javax.xml.transform.TransformerException Thrown if error transforming event content.
     */
    @Test(expected=TransformationException.class)
    public void transformation_fail_at_start_time_to_create_transformer_xsl_not_found() throws TransformerException
    {
        // Setup test objects
        this.defaultConfig.setStylesheetLocation("classpath:doesnotexist.xsl");

        final TransformerConfigurationException expectedException = new TransformerConfigurationException();

        // Setup expectations
        this.mockery.checking(new Expectations()
        {
            {
                // Must setup a custom ErrorListener to control error handling for processing stylesheet
                one(mockTransformerFactory).setErrorListener(with(any(ExceptionThrowingErrorListener.class)));

                // Create a Templates
                one(mockTransformerFactory).newTemplates(with(any(Source.class)));will(throwException(expectedException));

                //Creating templates failed, so transformer tries to create Transformer from factory
                // but fails since stylesheet location is a malformed uril: classpath is not a supported scheme!
                one(mockTransformerFactory).newTransformer(with(any(Source.class)));will(throwException(expectedException));
            }
        });

        // Run the test:
        this.uut = new XsltConverter(this.mockTransformerFactory);
        // As this transformer is a ConfiguredResource, set the configuration object
        this.uut.setConfiguration(this.defaultConfig);
        // As this transformer is a ManagedResource, start it
        try
        {
            this.uut.startManagedResource();
        }
        catch (RuntimeException e)
        {
            /*
             * This is mimicing how Flow handles failures of starting a managed resource;
             * it is swallowed. Pushing data through to the component will fail throwing an
             * exception that is handled by exception handler.
             */
        }
        // Push data through
        this.uut.convert(this.testEvent);
        // Now stop it
        this.uut.stopManagedResource();

        // Make assertions
        this.mockery.assertIsSatisfied();
    }

    /**
     * If the configured stylesheet is not found, creating a {@link javax.xml.transform.Transformer} instance
     * will fail
     * 
     * @throws javax.xml.transform.TransformerException Thrown if error transforming event content.
     */
    @Test(expected=TransformationException.class)
    public void transformation_fail_at_transform_time_to_create_transformer_xsl_not_found() throws TransformerException
    {
        // Setup test objects
        this.defaultConfig.setUseTranslets(false);
        this.defaultConfig.setStylesheetLocation("classpath:doesnotexist.xsl");

        final TransformerConfigurationException expectedException = new TransformerConfigurationException();

        // Setup expectations
        this.mockery.checking(new Expectations() {
            {
                // Must setup a custom ErrorListener to control error handling for processing stylesheet
                one(mockTransformerFactory).setErrorListener(with(any(ExceptionThrowingErrorListener.class)));

                // Create a new Transformer instance (using translets is switched off)
                one(mockTransformerFactory).newTransformer(with(any(Source.class)));
                will(throwException(expectedException));
            }
        });

        // Run the test:
        this.uut = new XsltConverter(this.mockTransformerFactory);
        this.runTest();

        // Make assertions
        this.mockery.assertIsSatisfied();
    }

    /**
     * Test successful actual stylesheet transformation for Xml to XML.
     * 
     * @throws javax.xml.transform.TransformerException Thrown if error transforming event content.
     */
    @Test
    public void transform_testXmlToXml_xsl() throws TransformerException
    {
        // setup test
        XsltConverter<String,String> uut = new XsltConverter<String,String>(org.apache.xalan.xsltc.trax.TransformerFactoryImpl.newInstance());
        XsltConverterConfiguration XsltConverterConfiguration = new XsltConverterConfiguration();
        XsltConverterConfiguration.setStylesheetLocation(CLASSPATH_URL_PREFIX + "testXmlToXml.xsl");
        uut.setConfiguration(XsltConverterConfiguration);

        final byte[] inboundPayloadContent = 
            new String("<sourceRoot><sourceElement1>element1Value</sourceElement1><sourceElement2>element2Value</sourceElement2></sourceRoot>").getBytes();
        String outboundPayloadContent =
            new String("<?xml version=\"1.0\" encoding=\"UTF-8\"?><targetRoot><targetElement1>element1Value</targetElement1><targetElement2>element2Value</targetElement2></targetRoot>");

        // setup expectations

        // run test
        uut.startManagedResource();
        String result = uut.convert(new String(inboundPayloadContent));

        // Make assertions
        Assert.assertEquals(outboundPayloadContent, result);

    }

    /**
     * Test successful actual stylesheet transformation for Xml to XML.
     * 
     * @throws javax.xml.transform.TransformerException Thrown if error transforming event content.
     */
    @Test
    public void transform_testXmlToText_xsl() throws TransformerException
    {
        // setup test
        XsltConverter<String,String> uut = new XsltConverter<String,String>(org.apache.xalan.xsltc.trax.TransformerFactoryImpl.newInstance());
        XsltConverterConfiguration XsltConverterConfiguration = new XsltConverterConfiguration();
        XsltConverterConfiguration.setStylesheetLocation(CLASSPATH_URL_PREFIX + "testXmlToText.xsl");
        uut.setConfiguration(XsltConverterConfiguration);

        String inboundPayloadContent =
            new String("<sourceRoot><sourceElement1>element1Value</sourceElement1><sourceElement2>element2Value</sourceElement2></sourceRoot>");
        String outboundPayloadContent = new String("element1Value|element2Value");

        // run test
        uut.startManagedResource();
        String result = uut.convert(inboundPayloadContent);

        // Make assertions
        Assert.assertEquals(outboundPayloadContent, result);
    }

    /**
     * Test successful actual stylesheet transformation for Xml to XML.
     * 
     * @throws javax.xml.transform.TransformerException Thrown if error transforming event content.
     */
    @Test
    public void nativeTransformer_testXmlToXml_xsl() throws TransformerException
    {
        TransformerFactory transformerFactory = org.apache.xalan.xsltc.trax.TransformerFactoryImpl.newInstance();

        // load stylesheet from the classpath
        String xslPath = CLASSPATH_URL_PREFIX + "testXmlToXml.xsl";
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream xslInputStream = classLoader.getResourceAsStream(this.stripClasspathScheme(xslPath));
        StreamSource xsltSource = new StreamSource(xslInputStream);
        
        // create transformer 
        Transformer transformer = transformerFactory.newTransformer(xsltSource);

        // setup input
        String inputXml = "<sourceRoot><sourceElement1>element1Value</sourceElement1><sourceElement2>element2Value</sourceElement2></sourceRoot>";
        String expectedOutput = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><targetRoot><targetElement1>element1Value</targetElement1><targetElement2>element2Value</targetElement2></targetRoot>";
        InputSource inputSource = new InputSource(new ByteArrayInputStream(inputXml.getBytes()));
        Source source = new SAXSource(inputSource);

        // setup output
        ByteArrayOutputStream outputDataStream = new ByteArrayOutputStream();

        // transform
        transformer.transform(source, new StreamResult(outputDataStream));

        // check result
        byte[] outputBytes = outputDataStream.toByteArray();
        
        Assert.assertEquals(expectedOutput, new String(outputBytes));
    }

    /**
     * Test successful actual stylesheet transformation for Xml to XML.
     * 
     * @throws javax.xml.transform.TransformerException Thrown if error transforming event content.
     */
    @Test
    public void nativeTransformer_testXmlToText_xsl() throws TransformerException
    {
        TransformerFactory transformerFactory = org.apache.xalan.xsltc.trax.TransformerFactoryImpl.newInstance();

        // load stylesheet from the classpath
        String xslPath = CLASSPATH_URL_PREFIX + "testXmlToText.xsl";
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream xslInputStream = classLoader.getResourceAsStream(this.stripClasspathScheme(xslPath));
        StreamSource xsltSource = new StreamSource(xslInputStream);
        
        // create transformer 
        Transformer transformer = transformerFactory.newTransformer(xsltSource);

        // setup input
        String inputXml = "<sourceRoot><sourceElement1>element1Value</sourceElement1><sourceElement2>element2Value</sourceElement2></sourceRoot>";
        String expectedOutput = "element1Value|element2Value";
        InputSource inputSource = new InputSource(new ByteArrayInputStream(inputXml.getBytes()));
        Source source = new SAXSource(inputSource);

        // setup output
        ByteArrayOutputStream outputDataStream = new ByteArrayOutputStream();

        // transform
        transformer.transform(source, new StreamResult(outputDataStream));

        // check result
        byte[] outputBytes = outputDataStream.toByteArray();
        
        Assert.assertEquals(expectedOutput, new String(outputBytes));
    }

    /**
     * Test successful transformation using the default configuration but with
     * extra transformation parameters to be set on {@link javax.xml.transform.Transformer}
     *
     * @throws javax.xml.transform.TransformerException Thrown if error transforming event content.
     */
    @Test
    public void transformation_successful_with_xsltconverterconfiguration_parameters_injected() throws TransformerException
    {
        final Converter<XsltConverterConfiguration, Map<String, String>> configurationParameterConverter = mockery.mock(Converter.class);
        final Map<String, String> convertedConfig = new HashMap<>();
        convertedConfig.put("name", "value");


        // Setup expectations
        this.mockery.checking(new Expectations()
        {
            {
                // Must setup a custom ErrorListener to control error handling for processing stylesheet
                one(mockTransformerFactory).setErrorListener(with(any(ExceptionThrowingErrorListener.class)));

                // Because using translets was switched on, create Templates from factory
                one(mockTransformerFactory).newTemplates(with(any(Source.class))); will(returnValue(mockTemplates));

                // Create a new Transformer instance
                one(mockTemplates).newTransformer(); will(returnValue(mockTransformer));

                one(configurationParameterConverter).convert(defaultConfig);
                will(returnValue(convertedConfig));

                // Setting the provided parameters on transformer
                one(mockTransformer).setParameter("name", "value");

                // Transformer instance needs its own ErrorListener to control error handling for tranformations
                one(mockTransformer).setErrorListener(with(any(ExceptionThrowingErrorListener.class)));

                // The transformation itself
                one(mockTransformer).transform(with(any(Source.class)), with(any(Result.class)));
            }
        });

        // Run the test:
        this.uut = new XsltConverter(this.mockTransformerFactory);
        // Setup the transformer to be tested with extra transformation parameters
        this.uut.setConfigurationParameterConverter(configurationParameterConverter);
        this.runTest();

        // Make assertions
        this.mockery.assertIsSatisfied();
    }

    /**
      * Utility method refactored for steps required to run a test case
    */
    private void runTest()
    {
        // As this transformer is a ConfiguredResource, set the configuration object
        this.uut.setConfiguration(this.defaultConfig);
        // As this transformer is a ManagedResource, start it
        this.uut.startManagedResource();
        // Push data through
        this.uut.convert(this.testEvent);
        // Now stop it
        this.uut.stopManagedResource();
    }

    private String stripClasspathScheme(final String xslLocation)
    {
        int index = xslLocation.indexOf(CLASSPATH_URL_PREFIX) + CLASSPATH_URL_PREFIX.length();
        return xslLocation.substring(index);
    }
}
