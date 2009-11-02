package org.ikasan.tools.errormanager.eai;

import org.ikasan.tools.errormanager.model.BusinessError;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;


public class BusinessErrorConverter {

	 /** The xstream */
    private XStream xstream;

    /**
     * Constructor
     * 
     */
    public BusinessErrorConverter()
    {
        xstream = new XStream(new DomDriver()); 
        xstream.alias("businessError", BusinessError.class);
    }

    public BusinessError toObject(String xml)
    {
        return (BusinessError) xstream.fromXML(xml);
    }

    public String toXml(BusinessError subject)
    {
        return xstream.toXML(subject);
    }
}
