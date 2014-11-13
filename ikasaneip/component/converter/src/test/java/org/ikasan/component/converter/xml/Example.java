/*
 * $Id: Example.java 25470 2013-08-28 22:19:04Z jeffmitchell $
 * $URL: http://svc-vcs:18080/svn/MSUSA/middleware/branches/ion-marketDataSrc-trunk-moved/jar/src/test/java/com/mizuho/middleware/marketdatasrc/component/converter/Example.java $
 *
 * ====================================================================
 * (C) Copyright Mizuho Securities USA
 * ====================================================================
 *
 */
package org.ikasan.component.converter.xml;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Example class for testing JAXB.
 * 
 * @author jeffmitchell
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "example", propOrder = {
        "one",
        "two"
})

@XmlRootElement
public class Example implements Serializable
{
    /** default serial id */
    private static final long serialVersionUID = 1L;

    @XmlElement(required=true)
    protected String one;
    @XmlElement
    protected String two;
    public String getOne()
    {
        return one;
    }
    public void setOne(String one)
    {
        this.one = one;
    }
    public String getTwo()
    {
        return two;
    }
    public void setTwo(String two)
    {
        this.two = two;
    }

}
