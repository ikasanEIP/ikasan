package org.ikasan.component.converter.xml;

import org.ikasan.component.converter.xml.jaxb.Example;

public class ExampleEventFactory
{
    final String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><example xsi:schemaLocation=\"http://mizuho.com/domain example.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><one>1</one><two>2</two></example>";

    final String sparseXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><example xsi:schemaLocation=\"http://mizuho.com/domain example.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"></example>";

    final String xmlOutOfOrder = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><example xsi:schemaLocation=\"http://mizuho.com/domain example.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><two>2</two><one>1</one></example>";

    
    public String getXmlEvent()
    {
        return this.xml;
    }

    public String getXmlEventOutOfOrder()
    {
        return this.xmlOutOfOrder;
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