package org.ikasan.component.validator.xml;


import org.ikasan.spec.component.transformation.TransformationException;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.ls.LSInput;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * Tests the XMLValidator
 */
public class ResourceResolverTest
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

    /**
     * class to be tested
     */
    private ResourceResolver uut = new ResourceResolver();

    @Test public void resolveResource_when_systemId_has_no_classpath()
            throws TransformationException, ParserConfigurationException, SAXException, IOException
    {
        String type = "type";
        String namespaceURI = "namespace:";
        String publicId = "publicId";
        String systemId = "systemId";
        String baseURI = null;
        LSInput result = this.uut.resolveResource(type, namespaceURI, publicId, systemId, baseURI);
        Assert.assertNull(result);
    }

    @Test public void resolveResource_when_systemId_has_classpath()
            throws TransformationException, ParserConfigurationException, SAXException, IOException
    {
        String type = "type";
        String namespaceURI = "namespace:";
        String publicId = "publicId";
        String systemId = "classpath:xml/simple-no-namespace.xml";
        String baseURI = null;
        LSInput result = this.uut.resolveResource(type, namespaceURI, publicId, systemId, baseURI);
        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getByteStream());
    }

    @Test(expected = RuntimeException.class)
    public void resolveResource_when_systemId_has_classpath_but_file_does_not_exit()
            throws TransformationException, ParserConfigurationException, SAXException, IOException
    {
        String type = "type";
        String namespaceURI = "namespace:";
        String publicId = "publicId";
        String systemId = "classpath:xml/simple-no-namespace.xml_fake";
        String baseURI = null;
        LSInput result = this.uut.resolveResource(type, namespaceURI, publicId, systemId, baseURI);
        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getByteStream());
    }
}
