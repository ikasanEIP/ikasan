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
package org.ikasan.framework.component.transformation;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;

import org.ikasan.common.Payload;
import org.ikasan.common.component.DefaultPayload;
import org.ikasan.framework.component.Event;
import org.ikasan.framework.component.transformation.configuration.XsltConfiguration;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@link XsltTransformer}
 * 
 * @author Ikasan Development Team
 * 
 */
public class XsltTransformerTest
{
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
    private XsltTransformer transformerToTest;

    // Test bits and pieces
    /** Dummy xml content expected from transformation */
    // Ick! So many better ways to create this, but is it worth it?
    private static final String DUMMY_CONTENT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><dummy />";

    /** Event passed to Transformer component being tested*/
    private Event testEvent;

    /** Default component configuration object */
    private XsltConfiguration defaultConfig;

    /**
     * Setup common objects used to prepare each test case
     */
    @Before public void setup()
    {
        // Setup test objects
        Payload payload = new DefaultPayload("paylodToTransform", DUMMY_CONTENT.getBytes());
        List<Payload> payloads = new ArrayList<Payload>();
        payloads.add(payload);
        this.testEvent = new Event("testEvent", 4, new Date(), payloads);

        this.defaultConfig = new XsltConfiguration();
        this.defaultConfig.setStylesheetLocation("classpath:anyStylesheet.xsl");
    }

    /**
     * Creating a n XsltTransformer will fail if injected {@link TransformerFactory} is null.
     */
    @Test(expected=IllegalArgumentException.class)
    public void construction_fails_null_transformerFactory()
    {
        new XsltTransformer(null);
    }

    /**
     * Test successful transformation using configured stylesheet where use of trasletes
     * is switched on. No extra parameters are set on transformer.
     * 
     * @throws TransformerException Thrown if error transforming event content.
     */
    @Test
    public void transformation_successful_with_default_setup() throws TransformerException
    {
        // Setup expectations
        this.mockery.checking(new Expectations()
        {
            {
                // Must setup a custom ErrorListener to control error handling for processing stylesheet
                one(mockTransformerFactory).setErrorListener((ErrorListener) with(an(ExceptionThrowingErrorListener.class)));

                // Because using translets was switched on, create Templates from factory
                one(mockTransformerFactory).newTemplates((Source) with(a(Source.class))); will(returnValue(mockTemplates));

                // Create a new Transformer instance
                one(mockTemplates).newTransformer(); will(returnValue(mockTransformer));

                // Transformer instance needs its own ErrorListener to control error handling for tranformations
                one(mockTransformer).setErrorListener((ErrorListener) with(an(ExceptionThrowingErrorListener.class)));

                // The transformation itself
                one(mockTransformer).transform((Source) with(a(Source.class)), (Result) with(a(Result.class)));
            }
        });

        // Run the test:
        this.transformerToTest = new XsltTransformer(this.mockTransformerFactory);
        this.runTest();

        // Make assertions
        this.mockery.assertIsSatisfied();
    }

    /**
     * Test successful transformation using configured stylesheet where use of trasletes
     * is switched off. No extra parameters are set on transformer.
     * 
     * @throws TransformerException Thrown if error transforming event content.
     */
    @Test
    public void transformation_successful_without_using_translets() throws TransformerException
    {
        // Changing the configuration to test other branch of code
        this.defaultConfig.setUseTranslates(false);
        this.defaultConfig.setStylesheetLocation("file://anyStylesheet.xsl");

        // Setup expectations
        this.mockery.checking(new Expectations()
        {
            {
                // Must setup a custom ErrorListener to control error handling for processing stylesheet
                one(mockTransformerFactory).setErrorListener((ErrorListener) with(an(ExceptionThrowingErrorListener.class)));

                // Because using translets was switched off, create Transformer directly from factory
                one(mockTransformerFactory).newTransformer((Source) with(a(Source.class))); will(returnValue(mockTransformer));

                // Transformer instance needs its own ErrorListener to control error handling for tranformations
                one(mockTransformer).setErrorListener((ErrorListener) with(an(ExceptionThrowingErrorListener.class)));

                // The transformation itself
                one(mockTransformer).transform((Source) with(a(Source.class)), (Result) with(a(Result.class)));
            }
        });

        // Run the test:
        this.transformerToTest = new XsltTransformer(this.mockTransformerFactory);
        this.runTest();

        // Make assertions
        this.mockery.assertIsSatisfied();
    }

    /**
     * Test successful transformation using the default configuration but with
     * extra transformation parameters to be set on {@link Transformer}
     * 
     * @throws TransformerException Thrown if error transforming event content.
     */
    @Test
    public void transformation_successful_with_extra_tranformationParams_configured() throws TransformerException
    {
        // Setup test objects
        final Map<String, String> transformationParams = new HashMap<String, String>();
        transformationParams.put("name", "value");

        // Setup expectations
        this.mockery.checking(new Expectations()
        {
            {
                // Must setup a custom ErrorListener to control error handling for processing stylesheet
                one(mockTransformerFactory).setErrorListener((ErrorListener) with(an(ExceptionThrowingErrorListener.class)));

                // Because using translets was switched on, create Templates from factory
                one(mockTransformerFactory).newTemplates((Source) with(a(Source.class))); will(returnValue(mockTemplates));

                // Create a new Transformer instance
                one(mockTemplates).newTransformer(); will(returnValue(mockTransformer));

                // Setting the provided parameters on transformer
                one(mockTransformer).setParameter("name", "value");

                // Transformer instance needs its own ErrorListener to control error handling for tranformations
                one(mockTransformer).setErrorListener((ErrorListener) with(an(ExceptionThrowingErrorListener.class)));

                // The transformation itself
                one(mockTransformer).transform((Source) with(a(Source.class)), (Result) with(a(Result.class)));
            }
        });



        // Run the test:
        this.transformerToTest = new XsltTransformer(this.mockTransformerFactory);
        // Setup the transformer to be tested with extra transformation parameters
        this.transformerToTest.setTransformationParameters(transformationParams);
        this.runTest();

        // Make assertions
        this.mockery.assertIsSatisfied();
    }

    /**
     * Sometimes the default URIResolver provided by implementation cannot be used
     * to resolve resources from classpath. Therefore, a custom URIResolver implementation
     * must be configured on created {@link Transformer} instance
     * 
     * @throws TransformerException Thrown if error transforming event content.
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
                one(mockTransformerFactory).setErrorListener((ErrorListener) with(an(ExceptionThrowingErrorListener.class)));

                // Because using translets was switched on, create Templates from factory
                one(mockTransformerFactory).newTemplates((Source) with(a(Source.class))); will(returnValue(mockTemplates));

                // Create a new Transformer instance
                one(mockTemplates).newTransformer(); will(returnValue(mockTransformer));

                // Setting the custom URI resolver on transformer
                one(mockTransformer).setURIResolver(mockURIResolver);

                // Transformer instance needs its own ErrorListener to control error handling for tranformations
                one(mockTransformer).setErrorListener((ErrorListener) with(an(ExceptionThrowingErrorListener.class)));

                // The transformation itself
                one(mockTransformer).transform((Source) with(a(Source.class)), (Result) with(a(Result.class)));
            }
        });

        // Run the test:
        this.transformerToTest = new XsltTransformer(this.mockTransformerFactory);
        // Setup the transformer being tested with a custom uri resolver
        this.transformerToTest.setURIResolver(mockURIResolver);
        this.runTest();

        // Make assertions
        this.mockery.assertIsSatisfied();
    }

    /**
     * Transformation fail
     * 
     * @throws TransformerException Thrown if error transforming event content.
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
                one(mockTransformerFactory).setErrorListener((ErrorListener) with(an(ExceptionThrowingErrorListener.class)));

                // Because using translets was switched on, create Templates from factory
                one(mockTransformerFactory).newTemplates((Source) with(a(Source.class))); will(returnValue(mockTemplates));

                // Create a new Transformer instance
                one(mockTemplates).newTransformer(); will(returnValue(mockTransformer));

                // Transformer instance needs its own ErrorListener to control error handling for tranformations
                one(mockTransformer).setErrorListener((ErrorListener) with(an(ExceptionThrowingErrorListener.class)));

                // The transformation itself
                one(mockTransformer).transform((Source) with(a(Source.class)), (Result) with(a(Result.class)));will(throwException(expectedException));
            }
        });

        // Run the test:
        this.transformerToTest = new XsltTransformer(this.mockTransformerFactory);
        this.runTest();

        // Make assertions
        this.mockery.assertIsSatisfied();
    }

    /**
     * If the configured stylesheet is not found, creating a {@link Transformer} instance
     * will fail
     * 
     * @throws TransformerException Thrown if error transforming event content.
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
                one(mockTransformerFactory).setErrorListener((ErrorListener) with(an(ExceptionThrowingErrorListener.class)));

                // Create a Templates
                one(mockTransformerFactory).newTemplates((Source)with(a(Source.class)));will(throwException(expectedException));

                //Creating templates failed, so transformer tries to create Transformer from factory
                // but fails since stylesheet location is a malformed uril: classpath is not a supported scheme!
                one(mockTransformerFactory).newTransformer((Source)with(a(Source.class)));will(throwException(expectedException));
            }
        });

        // Run the test:
        this.transformerToTest = new XsltTransformer(this.mockTransformerFactory);
        // As this transformer is a ConfiguredResource, set the configuration object
        this.transformerToTest.setConfiguration(this.defaultConfig);
        // As this transformer is a ManagedResource, start it
        try
        {
            this.transformerToTest.startManagedResource();
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
        this.transformerToTest.onEvent(this.testEvent);
        // Now stop it
        this.transformerToTest.stopManagedResource();

        // Make assertions
        this.mockery.assertIsSatisfied();
    }

    /**
     * If the configured stylesheet is not found, creating a {@link Transformer} instance
     * will fail
     * 
     * @throws TransformerException Thrown if error transforming event content.
     */
    @Test(expected=TransformationException.class)
    public void transformation_fail_at_transform_time_to_create_transformer_xsl_not_found() throws TransformerException
    {
        // Setup test objects
        this.defaultConfig.setUseTranslates(false);
        this.defaultConfig.setStylesheetLocation("classpath:doesnotexist.xsl");

        final TransformerConfigurationException expectedException = new TransformerConfigurationException();

        // Setup expectations
        this.mockery.checking(new Expectations()
        {
            {
                // Must setup a custom ErrorListener to control error handling for processing stylesheet
                one(mockTransformerFactory).setErrorListener((ErrorListener) with(an(ExceptionThrowingErrorListener.class)));

                // Create a new Transformer instance (using translets is switched off)
                one(mockTransformerFactory).newTransformer((Source) with(a(Source.class))); will(throwException(expectedException));
            }
        });

        // Run the test:
        this.transformerToTest = new XsltTransformer(this.mockTransformerFactory);
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
        this.transformerToTest.setConfiguration(this.defaultConfig);
        // As this transformer is a ManagedResource, start it
        this.transformerToTest.startManagedResource();
        // Push data through
        this.transformerToTest.onEvent(this.testEvent);
        // Now stop it
        this.transformerToTest.stopManagedResource();
    }
}
