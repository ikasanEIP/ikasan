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
 * <p>Java class for SchematronOutputType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SchematronOutputType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{http://purl.oclc.org/dsdl/svrl}text" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element ref="{http://purl.oclc.org/dsdl/svrl}ns-prefix-in-attribute-values" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;sequence maxOccurs="unbounded"&gt;
 *           &lt;element ref="{http://purl.oclc.org/dsdl/svrl}active-pattern"/&gt;
 *           &lt;sequence maxOccurs="unbounded" minOccurs="0"&gt;
 *             &lt;element ref="{http://purl.oclc.org/dsdl/svrl}fired-rule"/&gt;
 *             &lt;choice maxOccurs="unbounded" minOccurs="0"&gt;
 *               &lt;element ref="{http://purl.oclc.org/dsdl/svrl}failed-assert"/&gt;
 *               &lt;element ref="{http://purl.oclc.org/dsdl/svrl}successful-report"/&gt;
 *             &lt;/choice&gt;
 *           &lt;/sequence&gt;
 *         &lt;/sequence&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="title" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" /&gt;
 *       &lt;attribute name="phase" type="{http://www.w3.org/2001/XMLSchema}NMTOKEN" /&gt;
 *       &lt;attribute name="schemaVersion" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SchematronOutputType", propOrder = {
    "text",
    "nsPrefixInAttributeValues",
    "activePatternAndFiredRuleAndFailedAssert"
})
public class SchematronOutputType implements Serializable
{

    private List<String> text;
    @XmlElement(name = "ns-prefix-in-attribute-values")
    private List<NsPrefixInAttributeValues> nsPrefixInAttributeValues;
    @XmlElements({
        @XmlElement(name = "active-pattern", required = true, type = ActivePattern.class),
        @XmlElement(name = "fired-rule", required = true, type = FiredRule.class),
        @XmlElement(name = "failed-assert", required = true, type = FailedAssert.class),
        @XmlElement(name = "successful-report", required = true, type = SuccessfulReport.class)
    })
    private List<Object> activePatternAndFiredRuleAndFailedAssert;
    @XmlAttribute(name = "title")
    @XmlSchemaType(name = "anySimpleType")
    private String title;
    @XmlAttribute(name = "phase")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NMTOKEN")
    private String phase;
    @XmlAttribute(name = "schemaVersion")
    @XmlSchemaType(name = "anySimpleType")
    private String schemaVersion;

    /**
     * Gets the value of the text property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the text property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getText().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getText() {
        if (text == null) {
            text = new ArrayList<String>();
        }
        return this.text;
    }

    /**
     * Gets the value of the nsPrefixInAttributeValues property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the nsPrefixInAttributeValues property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNsPrefixInAttributeValues().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link NsPrefixInAttributeValues }
     * 
     * 
     */
    public List<NsPrefixInAttributeValues> getNsPrefixInAttributeValues() {
        if (nsPrefixInAttributeValues == null) {
            nsPrefixInAttributeValues = new ArrayList<NsPrefixInAttributeValues>();
        }
        return this.nsPrefixInAttributeValues;
    }

    /**
     * Gets the value of the activePatternAndFiredRuleAndFailedAssert property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the activePatternAndFiredRuleAndFailedAssert property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getActivePatternAndFiredRuleAndFailedAssert().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link org.oclc.purl.dsdl.svrl.ActivePattern }
     * {@link org.oclc.purl.dsdl.svrl.FiredRule }
     * {@link org.oclc.purl.dsdl.svrl.FailedAssert }
     * {@link org.oclc.purl.dsdl.svrl.SuccessfulReport }
     * 
     * 
     */
    public List<Object> getActivePatternAndFiredRuleAndFailedAssert() {
        if (activePatternAndFiredRuleAndFailedAssert == null) {
            activePatternAndFiredRuleAndFailedAssert = new ArrayList<Object>();
        }
        return this.activePatternAndFiredRuleAndFailedAssert;
    }

    /**
     * Gets the value of the title property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the value of the title property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTitle(
        String value) {
        this.title = value;
    }

    /**
     * Gets the value of the phase property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPhase() {
        return phase;
    }

    /**
     * Sets the value of the phase property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPhase(
        String value) {
        this.phase = value;
    }

    /**
     * Gets the value of the schemaVersion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSchemaVersion() {
        return schemaVersion;
    }

    /**
     * Sets the value of the schemaVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSchemaVersion(
        String value) {
        this.schemaVersion = value;
    }

    @Override
    public String toString()
    {
        return "SchematronOutputType{" +
                "text=" + text +
                ", nsPrefixInAttributeValues=" + nsPrefixInAttributeValues +
                ", activePatternAndFiredRuleAndFailedAssert=" + activePatternAndFiredRuleAndFailedAssert +
                ", title='" + title + '\'' +
                ", phase='" + phase + '\'' +
                ", schemaVersion='" + schemaVersion + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        { return true; }
        if (o == null || getClass() != o.getClass())
        { return false; }
        SchematronOutputType that = (SchematronOutputType) o;
        if (activePatternAndFiredRuleAndFailedAssert != null ?
                !activePatternAndFiredRuleAndFailedAssert.equals(that.activePatternAndFiredRuleAndFailedAssert) :
                that.activePatternAndFiredRuleAndFailedAssert != null)
        { return false; }
        if (nsPrefixInAttributeValues != null ?
                !nsPrefixInAttributeValues.equals(that.nsPrefixInAttributeValues) :
                that.nsPrefixInAttributeValues != null)
        { return false; }
        if (phase != null ? !phase.equals(that.phase) : that.phase != null)
        { return false; }
        if (schemaVersion != null ? !schemaVersion.equals(that.schemaVersion) : that.schemaVersion != null)
        { return false; }
        if (text != null ? !text.equals(that.text) : that.text != null)
        { return false; }
        if (title != null ? !title.equals(that.title) : that.title != null)
        { return false; }
        return true;
    }

    @Override
    public int hashCode()
    {
        int result = text != null ? text.hashCode() : 0;
        result = 31 * result + (nsPrefixInAttributeValues != null ? nsPrefixInAttributeValues.hashCode() : 0);
        result = 31 * result + (activePatternAndFiredRuleAndFailedAssert != null ?
                activePatternAndFiredRuleAndFailedAssert.hashCode() :
                0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (phase != null ? phase.hashCode() : 0);
        result = 31 * result + (schemaVersion != null ? schemaVersion.hashCode() : 0);
        return result;
    }

    /**
     * Created by ph-jaxb22-plugin -Xph-list-extension
     * 
     * @param aList
     *     The new list member to set. May be <code>null</code>.
     */
    public void setText(
        final List<String> aList) {
        text = aList;
    }

    /**
     * Created by ph-jaxb22-plugin -Xph-list-extension
     * 
     * @param aList
     *     The new list member to set. May be <code>null</code>.
     */
    public void setNsPrefixInAttributeValues(
        final List<NsPrefixInAttributeValues> aList) {
        nsPrefixInAttributeValues = aList;
    }

    /**
     * Created by ph-jaxb22-plugin -Xph-list-extension
     * 
     * @param aList
     *     The new list member to set. May be <code>null</code>.
     */
    public void setActivePatternAndFiredRuleAndFailedAssert(
        final List<Object> aList) {
        activePatternAndFiredRuleAndFailedAssert = aList;
    }

    /**
     * Created by ph-jaxb22-plugin -Xph-list-extension
     * 
     * @return
     *     <code>true</code> if at least one item is contained, <code>false</code> otherwise.
     */
    public boolean hasTextEntries() {
        return (!getText().isEmpty());
    }

    /**
     * Created by ph-jaxb22-plugin -Xph-list-extension
     * 
     * @return
     *     <code>true</code> if no item is contained, <code>false</code> otherwise.
     */
    public boolean hasNoTextEntries() {
        return getText().isEmpty();
    }

    /**
     * Created by ph-jaxb22-plugin -Xph-list-extension
     * 
     * @return
     *     The number of contained elements. Always &ge; 0.
     */
    public int getTextCount() {
        return getText().size();
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
    public String getTextAtIndex(
        final int index) {
        return getText().get(index);
    }

    /**
     * Created by ph-jaxb22-plugin -Xph-list-extension
     * 
     * @return
     *     <code>true</code> if at least one item is contained, <code>false</code> otherwise.
     */
    public boolean hasNsPrefixInAttributeValuesEntries() {
        return (!getNsPrefixInAttributeValues().isEmpty());
    }

    /**
     * Created by ph-jaxb22-plugin -Xph-list-extension
     * 
     * @return
     *     <code>true</code> if no item is contained, <code>false</code> otherwise.
     */
    public boolean hasNoNsPrefixInAttributeValuesEntries() {
        return getNsPrefixInAttributeValues().isEmpty();
    }

    /**
     * Created by ph-jaxb22-plugin -Xph-list-extension
     * 
     * @return
     *     The number of contained elements. Always &ge; 0.
     */
    public int getNsPrefixInAttributeValuesCount() {
        return getNsPrefixInAttributeValues().size();
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
    public NsPrefixInAttributeValues getNsPrefixInAttributeValuesAtIndex(
        final int index) {
        return getNsPrefixInAttributeValues().get(index);
    }

    /**
     * Created by ph-jaxb22-plugin -Xph-list-extension
     * 
     * @return
     *     <code>true</code> if at least one item is contained, <code>false</code> otherwise.
     */
    public boolean hasActivePatternAndFiredRuleAndFailedAssertEntries() {
        return (!getActivePatternAndFiredRuleAndFailedAssert().isEmpty());
    }

    /**
     * Created by ph-jaxb22-plugin -Xph-list-extension
     * 
     * @return
     *     <code>true</code> if no item is contained, <code>false</code> otherwise.
     */
    public boolean hasNoActivePatternAndFiredRuleAndFailedAssertEntries() {
        return getActivePatternAndFiredRuleAndFailedAssert().isEmpty();
    }

    /**
     * Created by ph-jaxb22-plugin -Xph-list-extension
     * 
     * @return
     *     The number of contained elements. Always &ge; 0.
     */
    public int getActivePatternAndFiredRuleAndFailedAssertCount() {
        return getActivePatternAndFiredRuleAndFailedAssert().size();
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
    public Object getActivePatternAndFiredRuleAndFailedAssertAtIndex(
        final int index) {
        return getActivePatternAndFiredRuleAndFailedAssert().get(index);
    }

}
