package org.ikasan.component.converter.xml.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Dummy adapter so we can test the setting and usage correctly
 */
public class DoSomethingXmlAdapter extends XmlAdapter<String, String>
{
    boolean doLowerCase = false;

    public DoSomethingXmlAdapter()
    {
    }

    public DoSomethingXmlAdapter(boolean doLowerCase)
    {
        this.doLowerCase = doLowerCase;
    }

    @Override
    public String unmarshal(String v) throws Exception
    {
        if (doLowerCase)
        {
            return v != null ? v.toLowerCase() : null;
        }
        return v;
    }

    @Override
    public String marshal(String v) throws Exception
    {
        if (doLowerCase)
        {
            return v != null ? v.toLowerCase() : null;
        }
        return v;
    }
}
