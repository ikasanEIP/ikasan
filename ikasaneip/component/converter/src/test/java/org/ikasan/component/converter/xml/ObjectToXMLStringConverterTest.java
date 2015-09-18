/*
 * $Id$
 * $URL$
 *
 * ====================================================================
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
 * ====================================================================
 */
package org.ikasan.component.converter.xml;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBException;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.DifferenceConstants;
import org.custommonkey.xmlunit.DifferenceListener;
import org.ikasan.component.converter.xml.jaxb.Example;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.component.transformation.TransformationException;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * Functional unit test cases for <code>ObjectToXMLStringConverter</code>.
 * 
 * @author Ikasan Development Team
 */
public class ObjectToXMLStringConverterTest
{
   /**
    * Mockery for mocking concrete classes
    */
   Mockery mockery = new Mockery()
   {
       {
           setImposteriser(ClassImposteriser.INSTANCE);
       }
   };

    /** mocked marshaller */
    final XmlConfiguration mockedXmlConfiguration = mockery.mock(XmlConfiguration.class, "mockedXmlConfiguration");

    /**
     * Failed constructor test
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failed_constructor_due_to_null_context()
    {
        new ObjectToXMLStringConverter((Class)null);
    }

    /**
     * Successful marshalling from example to XML.
     * @throws java.io.IOException
     * @throws org.xml.sax.SAXException
     * @throws javax.xml.bind.JAXBException
     */
    @Test
    public void test_successful_example_to_xml_with_validation_valid_msgs() throws SAXException, IOException, JAXBException
    {
        XmlConfiguration xmlConfiguration = new XmlConfiguration();
        xmlConfiguration.setSchemaLocation("http://mizuho.com/domain example.xsd");
        xmlConfiguration.setSchema("example.xsd");
        xmlConfiguration.setValidate(true);
        
        ExampleEventFactory eventFactory = new ExampleEventFactory();
        final String expectedXML = eventFactory.getXmlEvent();
        final Example example = eventFactory.getObjectEvent();
        example.setOne("1");
        example.setTwo("2");
        
        /** class on test */
        Converter<Object,Object> objectToXML = new ObjectToXMLStringConverter(Example.class);
        ((ConfiguredResource)objectToXML).setConfiguration(xmlConfiguration);

        for(int i = 0;i <20; i++)
        {
            String xml = (String)objectToXML.convert(example);

            // compare
            Diff diff = new Diff(expectedXML, xml);
            diff.overrideDifferenceListener(new IgnoreNamedElementsDifferenceListener("createDateTime"));
            assertTrue(diff.toString(), diff.similar());
        }

    }

    /**
     * Successful marshalling from example to XML.
     * @throws java.io.IOException
     * @throws org.xml.sax.SAXException
     * @throws javax.xml.bind.JAXBException
     */
    @Test(expected = TransformationException.class)
    public void test_successful_example_to_xml_with_validation_invalid_msg() throws SAXException, IOException, JAXBException
    {
        XmlConfiguration xmlConfiguration = new XmlConfiguration();
        xmlConfiguration.setSchemaLocation("http://mizuho.com/domain example.xsd");
        xmlConfiguration.setSchema("example.xsd");
        xmlConfiguration.setValidate(true);
        
        ExampleEventFactory eventFactory = new ExampleEventFactory();
        final String expectedXML = eventFactory.getXmlEvent();
        final Example example = eventFactory.getObjectEvent();
        
        /** class on test */
        Converter<Object,Object> objectToXML = new ObjectToXMLStringConverter(Example.class);
        ((ConfiguredResource)objectToXML).setConfiguration(xmlConfiguration);
        
        String xml = (String)objectToXML.convert(example);

        // compare
        Diff diff = new Diff(expectedXML, xml);
        assertTrue(diff.toString(), diff.similar());
    }

    /**
     * Successful marshalling from example to XML.
     * @throws java.io.IOException
     * @throws org.xml.sax.SAXException
     * @throws javax.xml.bind.JAXBException
     */
    @Test
    public void test_successful_example_to_xml_with_no_validation_valid_msg() throws SAXException, IOException, JAXBException
    {
        XmlConfiguration xmlConfiguration = new XmlConfiguration();
        xmlConfiguration.setSchemaLocation("http://mizuho.com/domain example.xsd");
        xmlConfiguration.setSchema("example.xsd");
        xmlConfiguration.setValidate(false);
        
        ExampleEventFactory eventFactory = new ExampleEventFactory();
        final String expectedXML = eventFactory.getXmlEvent();
        final Example example = eventFactory.getObjectEvent();
        example.setOne("1");
        example.setTwo("2");
        
        /** class on test */
        List<Class> classes = new ArrayList<Class>();
        classes.add(Example.class);
        Converter<Object,Object> objectToXML = new ObjectToXMLStringConverter(classes);
        ((ConfiguredResource)objectToXML).setConfiguration(xmlConfiguration);
        
        String xml = (String)objectToXML.convert(example);

        // compare
        Diff diff = new Diff(expectedXML, xml);
        assertTrue(diff.toString(), diff.similar());
    }

    /**
     * Successful marshalling from example to XML.
     * @throws java.io.IOException
     * @throws org.xml.sax.SAXException
     * @throws javax.xml.bind.JAXBException
     */
    @Test
    public void test_successful_example_to_xml_with_no_validation_invalid_msg() throws SAXException, IOException, JAXBException
    {
        XmlConfiguration xmlConfiguration = new XmlConfiguration();
        xmlConfiguration.setSchemaLocation("http://mizuho.com/domain example.xsd");
        xmlConfiguration.setSchema("example.xsd");
        xmlConfiguration.setValidate(false);

        ExampleEventFactory eventFactory = new ExampleEventFactory();
        final String expectedXML = eventFactory.getSparseXmlEvent();
        final Example example = eventFactory.getObjectEvent();

        /** class on test */
        List<Class> classes = new ArrayList<Class>();
        classes.add(Example.class);
        Converter<Object,Object> objectToXML = new ObjectToXMLStringConverter(classes);
        ((ConfiguredResource)objectToXML).setConfiguration(xmlConfiguration);

        String xml = (String)objectToXML.convert(example);

        // compare
        Diff diff = new Diff(expectedXML, xml);
        assertTrue(diff.toString(), diff.similar());
    }

    /**
     * Successful marshalling from example to XML.
     * @throws java.io.IOException
     * @throws org.xml.sax.SAXException
     * @throws javax.xml.bind.JAXBException
     */
    @Test
    public void test_configuration_no_root_override() throws SAXException, IOException, JAXBException
    {
        // set test expectations
        mockery.checking(new Expectations() {
            {
                exactly(1).of(mockedXmlConfiguration).getRootName();
                will(returnValue(null));
                exactly(1).of(mockedXmlConfiguration).getRootClassName();
                will(returnValue(null));
                exactly(1).of(mockedXmlConfiguration).getSchema();
                will(returnValue(null));
            }
        });

        /** class on test */
        List<Class> classes = new ArrayList<Class>();
        classes.add(Example.class);
        Converter<Object,Object> objectToXML = new ObjectToXMLStringConverter(classes);
        ((ConfiguredResource)objectToXML).setConfiguration(mockedXmlConfiguration);

        this.mockery.assertIsSatisfied();
    }

    /**
     * Successful marshalling from example to XML.
     * @throws java.io.IOException
     * @throws org.xml.sax.SAXException
     * @throws javax.xml.bind.JAXBException
     */
    @Test
    public void test_configuration_root_override_name_and_class() throws SAXException, IOException, JAXBException
    {
        // set test expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(2).of(mockedXmlConfiguration).getRootName();
                will(returnValue("example"));
                exactly(2).of(mockedXmlConfiguration).getRootClassName();
                will(returnValue("org.ikasan.component.converter.xml.jaxb.Example"));
                exactly(1).of(mockedXmlConfiguration).getSchema();
                will(returnValue(null));
            }
        });

        /** class on test */
        List<Class> classes = new ArrayList<Class>();
        classes.add(Example.class);
        Converter<Object,Object> objectToXML = new ObjectToXMLStringConverter(classes);
        ((ConfiguredResource)objectToXML).setConfiguration(mockedXmlConfiguration);

        this.mockery.assertIsSatisfied();
    }

    /**
     * Successful marshalling from example to XML.
     * @throws java.io.IOException
     * @throws org.xml.sax.SAXException
     * @throws javax.xml.bind.JAXBException
     */
    @Test
    public void test_configuration_root_override_name_and_class_found_on_search() throws SAXException, IOException, JAXBException
    {
        // set test expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(3).of(mockedXmlConfiguration).getRootName();
                will(returnValue("Example"));
                exactly(1).of(mockedXmlConfiguration).getRootClassName();
                will(returnValue(null));
                exactly(1).of(mockedXmlConfiguration).getSchema();
                will(returnValue(null));
            }
        });

        /** class on test */
        List<Class> classes = new ArrayList<Class>();
        classes.add(Example.class);
        Converter<Object,Object> objectToXML = new ObjectToXMLStringConverter(classes);
        ((ConfiguredResource)objectToXML).setConfiguration(mockedXmlConfiguration);

        this.mockery.assertIsSatisfied();
    }

    /**
     * Successful marshalling from example to XML.
     * @throws java.io.IOException
     * @throws org.xml.sax.SAXException
     * @throws javax.xml.bind.JAXBException
     */
    @Test(expected = RuntimeException.class)
    public void test_configuration_root_override_name_and_class_not_found_on_search_fast_fail() throws SAXException, IOException, JAXBException
    {
        // set test expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(3).of(mockedXmlConfiguration).getRootName();
                will(returnValue("example"));
                exactly(2).of(mockedXmlConfiguration).getRootClassName();
                will(returnValue(null));
                exactly(1).of(mockedXmlConfiguration).getSchema();
                will(returnValue(null));
                exactly(1).of(mockedXmlConfiguration).isFastFailOnConfigurationLoad();
                will(returnValue(true));
            }
        });

        /** class on test */
        List<Class> classes = new ArrayList<Class>();
        classes.add(Example.class);
        Converter<Object,Object> objectToXML = new ObjectToXMLStringConverter(classes);
        ((ConfiguredResource)objectToXML).setConfiguration(mockedXmlConfiguration);

        this.mockery.assertIsSatisfied();
    }

    /**
     * Successful marshalling from example to XML.
     * @throws java.io.IOException
     * @throws org.xml.sax.SAXException
     * @throws javax.xml.bind.JAXBException
     */
    @Test
    public void test_configuration_root_override_name_and_class_not_found_on_search_no_fast_fail() throws SAXException, IOException, JAXBException
    {
        // set test expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(3).of(mockedXmlConfiguration).getRootName();
                will(returnValue("example"));
                exactly(1).of(mockedXmlConfiguration).getRootClassName();
                will(returnValue(null));
                exactly(1).of(mockedXmlConfiguration).isFastFailOnConfigurationLoad();
                will(returnValue(false));
            }
        });

        /** class on test */
        List<Class> classes = new ArrayList<Class>();
        classes.add(Example.class);
        Converter<Object,Object> objectToXML = new ObjectToXMLStringConverter(classes);
        ((ConfiguredResource)objectToXML).setConfiguration(mockedXmlConfiguration);

        this.mockery.assertIsSatisfied();
    }

    /**
     * Successful marshalling from example to XML.
     * @throws java.io.IOException
     * @throws org.xml.sax.SAXException
     * @throws javax.xml.bind.JAXBException
     */
    @Test
    public void test_configuration_root_override_class_not_name() throws SAXException, IOException, JAXBException
    {
        // set test expectations
        mockery.checking(new Expectations()
        {
            {
                exactly(1).of(mockedXmlConfiguration).getRootName();
                will(returnValue(null));
                exactly(4).of(mockedXmlConfiguration).getRootClassName();
                will(returnValue("org.ikasan.component.converter.xml.jaxb.Example"));
                exactly(1).of(mockedXmlConfiguration).getSchema();
                will(returnValue(null));
            }
        });

        /** class on test */
        List<Class> classes = new ArrayList<Class>();
        classes.add(Example.class);
        Converter<Object,Object> objectToXML = new ObjectToXMLStringConverter(classes);
        ((ConfiguredResource)objectToXML).setConfiguration(mockedXmlConfiguration);

        this.mockery.assertIsSatisfied();
    }

    /**
     * Difference listener implementation for ignoring certain dynamic element
     * content such as timestamps.
     * 
     * @author jeffmitchell
     * 
     */
    private class IgnoreNamedElementsDifferenceListener implements DifferenceListener
    {
        private Set<String> blackList = new HashSet<String>();

        /**
         * Constructor
         * 
         * @param elementNames
         */
        public IgnoreNamedElementsDifferenceListener(String... elementNames)
        {
            for (String name : elementNames)
            {
                blackList.add(name);
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.custommonkey.xmlunit.DifferenceListener#differenceFound(org.
         * custommonkey.xmlunit.Difference)
         */
        public int differenceFound(Difference difference)
        {
            if (difference.getId() == DifferenceConstants.TEXT_VALUE_ID)
            {
                if (blackList.contains(difference.getControlNodeDetail().getNode().getParentNode().getNodeName()))
                {
                    return DifferenceListener.RETURN_IGNORE_DIFFERENCE_NODES_IDENTICAL;
                }
            }

            return DifferenceListener.RETURN_ACCEPT_DIFFERENCE;
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.custommonkey.xmlunit.DifferenceListener#skippedComparison(org
         * .w3c.dom.Node, org.w3c.dom.Node)
         */
        public void skippedComparison(Node arg0, Node arg1)
        {
            // nothing to do
        }
    }
    

}
