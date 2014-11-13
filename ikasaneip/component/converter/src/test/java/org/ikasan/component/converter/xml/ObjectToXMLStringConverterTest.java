/*
 * $Id: ObjectToXMLStringConverterTest.java 25470 2013-08-28 22:19:04Z jeffmitchell $
 * $URL: http://svc-vcs:18080/svn/MSUSA/middleware/branches/ion-marketDataSrc-trunk-moved/jar/src/test/java/com/mizuho/middleware/marketdatasrc/component/converter/ObjectToXMLStringConverterTest.java $
 *
 * ====================================================================
 * (C) Copyright Mizuho Securities USA
 * ====================================================================
 *
 */
package org.ikasan.component.converter.xml;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.DifferenceConstants;
import org.custommonkey.xmlunit.DifferenceListener;
import org.ikasan.spec.component.transformation.Converter;
import org.ikasan.spec.component.transformation.TransformationException;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * Functional unit test cases for <code>ObjectToXMLStringConverter</code>.
 * 
 * @author jeffmitchell
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

    /**
     * Failed constructor test
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failed_constructor_due_to_null_context()
    {
        new ObjectToXMLStringConverter(null);
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
        Converter<Object,Object> objectToXML = new ObjectToXMLStringConverter(JAXBContext.newInstance(Example.class));
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
        Converter<Object,Object> objectToXML = new ObjectToXMLStringConverter(JAXBContext.newInstance(Example.class));
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
        Converter<Object,Object> objectToXML = new ObjectToXMLStringConverter(JAXBContext.newInstance(Example.class));
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
        Converter<Object,Object> objectToXML = new ObjectToXMLStringConverter(JAXBContext.newInstance(Example.class));
        ((ConfiguredResource)objectToXML).setConfiguration(xmlConfiguration);
        
        String xml = (String)objectToXML.convert(example);

        // compare
        Diff diff = new Diff(expectedXML, xml);
        assertTrue(diff.toString(), diff.similar());
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
    
    public class ExampleEventFactory
    {
        final String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><example xsi:schemaLocation=\"http://mizuho.com/domain example.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><one>1</one><two>2</two></example>";

        final String sparseXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><example xsi:schemaLocation=\"http://mizuho.com/domain example.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"></example>";

        public String getXmlEvent()
        {
            return this.xml;
        }
        
        public String getSparseXmlEvent()
        {
            return this.sparseXml;
        }
        
        public Example getObjectEvent()
        {
            return new Example();
        }
    }
}
