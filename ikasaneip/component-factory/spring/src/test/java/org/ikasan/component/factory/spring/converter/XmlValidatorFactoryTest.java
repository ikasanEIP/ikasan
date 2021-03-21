package org.ikasan.component.factory.spring.converter;

import liquibase.pro.packaged.S;
import org.ikasan.component.factory.spring.CustomConverterComponentFactory;
import org.ikasan.component.factory.spring.IkasanComponentFactory;
import org.ikasan.component.factory.spring.MultipleFactoryConverterFactoryOne;
import org.ikasan.component.factory.spring.MultipleFactoryConverterFactoryTwo;
import org.ikasan.component.validator.xml.XMLValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class) @SpringBootTest(classes = { IkasanComponentFactory.class,
    XmlValidatorFactory.class,})
public class XmlValidatorFactoryTest
{
    @Resource
    private XmlValidatorFactory<String,String>xmlValidatorFactory;

    private String xml_with_schema_url =
        "<?xml version=\"1.0\"?><x:books xmlns:x=\"urn:books\" "
            + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" "
            + "xmlns:fo=\"http://www.w3.org/1999/XSL/Format\" "
            + "xsi:schemaLocation=\"urn:books http://www.books4tests.com/xsd/book.xsd\">   <book id=\"bk001\">      "
            + "<author>Writer</author>      <title>The First Book</title>      <genre>Fiction</genre>      "
            + "<price>44.95</price>      <pub_date>2000-10-01</pub_date>      <review>An amazing s"
            + "tory of nothing.</review>   </book>   <book id=\"bk002\">      <author>Poet</author>      "
            + "<title>The Poet's First Poem</title>      <genre>Poem</genre>      <price>24.95</price>      "
            + "<pub_date>2000-10-01</pub_date><review>Least poetic poems.</review>   </book></x:books>";

    @Test
    public void test(){
        XMLValidator<String, String> xmlValidator = xmlValidatorFactory.create("xmlValidator",
            "xml.validator", null);
        xmlValidator.startManagedResource();
        String convertedXml = (String)xmlValidator.convert(xml_with_schema_url);
        assertEquals(xml_with_schema_url, convertedXml);
    }
}