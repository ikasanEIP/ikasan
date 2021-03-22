package org.ikasan.component.factory.spring.converter;

import liquibase.pro.packaged.S;
import org.ikasan.component.converter.xml.XsltConverter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes=XsltConverterFactory.class)
public class XsltConverterFactoryTest
{
    @Resource
    private XsltConverterFactory<String,String> xsltConverterFactory;

    private String inboundPayloadContent =
        new String("<sourceRoot><sourceElement1>element1Value</sourceElement1><sourceElement2>element2Value"
            + "</sourceElement2></sourceRoot>");

    private String expectedOutput = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><targetRoot>"
        + "<xsi:attribute xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
        + "name=\"xsi:noNamespaceSchemaLocation\">http://www.ikasan.org/xsd/ikasan.xsd"
        + "</xsi:attribute><targetElement1>element1Value</targetElement1><targetElement2>"
        + "element2Value</targetElement2></targetRoot>";
    @Test
    public void testConverter() {
        XsltConverter<String, String> xsltConverter = xsltConverterFactory
            .create("nameSuffix", "xslt.converter", null);
        xsltConverter.startManagedResource();
        String converted = xsltConverter.convert(inboundPayloadContent);
        assertEquals(expectedOutput, converted);
    }
}