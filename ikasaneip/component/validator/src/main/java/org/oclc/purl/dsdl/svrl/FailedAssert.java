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

package org.oclc.purl.dsdl.svrl;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{http://purl.oclc.org/dsdl/svrl}diagnostic-reference" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element ref="{http://purl.oclc.org/dsdl/svrl}text"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attGroup ref="{http://purl.oclc.org/dsdl/svrl}attlist.assert-and-report"/&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "diagnosticReference",
    "text"
})
@XmlRootElement(name = "failed-assert")
public class FailedAssert implements Serializable
{

    @XmlElement(name = "diagnostic-reference")
    private List<DiagnosticReference> diagnosticReference;
    @XmlElement(required = true)
    private String text;
    @XmlAttribute(name = "id")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    private String id;
    @XmlAttribute(name = "location", required = true)
    @XmlSchemaType(name = "anySimpleType")
    private String location;
    @XmlAttribute(name = "test", required = true)
    @XmlSchemaType(name = "anySimpleType")
    private String test;
    @XmlAttribute(name = "role")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")
    private String role;
    @XmlAttribute(name = "flag")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")
    private String flag;

    /**
     * Gets the value of the diagnosticReference property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the diagnosticReference property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDiagnosticReference().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DiagnosticReference }
     * 
     * 
     */
    public List<DiagnosticReference> getDiagnosticReference() {
        if (diagnosticReference == null) {
            diagnosticReference = new ArrayList<DiagnosticReference>();
        }
        return this.diagnosticReference;
    }

    /**
     * Gets the value of the text property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the value of the text property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setText(
        String value) {
        this.text = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(
        String value) {
        this.id = value;
    }

    /**
     * Gets the value of the location property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLocation() {
        return location;
    }

    /**
     * Sets the value of the location property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLocation(
        String value) {
        this.location = value;
    }

    /**
     * Gets the value of the test property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTest() {
        return test;
    }

    /**
     * Sets the value of the test property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTest(
        String value) {
        this.test = value;
    }

    /**
     * Gets the value of the role property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRole() {
        return role;
    }

    /**
     * Sets the value of the role property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRole(
        String value) {
        this.role = value;
    }

    /**
     * Gets the value of the flag property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFlag() {
        return flag;
    }

    /**
     * Sets the value of the flag property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFlag(
        String value) {
        this.flag = value;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        { return true; }
        if (o == null || getClass() != o.getClass())
        { return false; }
        FailedAssert that = (FailedAssert) o;
        if (diagnosticReference != null ?
                !diagnosticReference.equals(that.diagnosticReference) :
                that.diagnosticReference != null)
        { return false; }
        if (flag != null ? !flag.equals(that.flag) : that.flag != null)
        { return false; }
        if (id != null ? !id.equals(that.id) : that.id != null)
        { return false; }
        if (location != null ? !location.equals(that.location) : that.location != null)
        { return false; }
        if (role != null ? !role.equals(that.role) : that.role != null)
        { return false; }
        if (test != null ? !test.equals(that.test) : that.test != null)
        { return false; }
        if (text != null ? !text.equals(that.text) : that.text != null)
        { return false; }
        return true;
    }

    @Override
    public int hashCode()
    {
        int result = diagnosticReference != null ? diagnosticReference.hashCode() : 0;
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (location != null ? location.hashCode() : 0);
        result = 31 * result + (test != null ? test.hashCode() : 0);
        result = 31 * result + (role != null ? role.hashCode() : 0);
        result = 31 * result + (flag != null ? flag.hashCode() : 0);
        return result;
    }

    @Override
    public String toString()
    {
        return "FailedAssert{" +
                "diagnosticReference=" + diagnosticReference +
                ", text='" + text + '\'' +
                ", id='" + id + '\'' +
                ", location='" + location + '\'' +
                ", test='" + test + '\'' +
                ", role='" + role + '\'' +
                ", flag='" + flag + '\'' +
                '}';
    }

    /**
     * Created by ph-jaxb22-plugin -Xph-list-extension
     * 
     * @param aList
     *     The new list member to set. May be <code>null</code>.
     */
    public void setDiagnosticReference(
        final List<DiagnosticReference> aList) {
        diagnosticReference = aList;
    }

    /**
     * Created by ph-jaxb22-plugin -Xph-list-extension
     * 
     * @return
     *     <code>true</code> if at least one item is contained, <code>false</code> otherwise.
     */
    public boolean hasDiagnosticReferenceEntries() {
        return (!getDiagnosticReference().isEmpty());
    }

    /**
     * Created by ph-jaxb22-plugin -Xph-list-extension
     * 
     * @return
     *     <code>true</code> if no item is contained, <code>false</code> otherwise.
     */
    public boolean hasNoDiagnosticReferenceEntries() {
        return getDiagnosticReference().isEmpty();
    }

    /**
     * Created by ph-jaxb22-plugin -Xph-list-extension
     * 
     * @return
     *     The number of contained elements. Always &ge; 0.
     */
    public int getDiagnosticReferenceCount() {
        return getDiagnosticReference().size();
    }

    /**
     * Created by ph-jaxb22-plugin -Xph-list-extension
     * 
     * @param index
     *     The index to retrieve
     * @return
     *     The element at the specified index. May be <code>null</code>
     * @throws ArrayIndexOutOfBoundsException
     *     if the index is invalid!
     */
    public DiagnosticReference getDiagnosticReferenceAtIndex(
        final int index) {
        return getDiagnosticReference().get(index);
    }

}
