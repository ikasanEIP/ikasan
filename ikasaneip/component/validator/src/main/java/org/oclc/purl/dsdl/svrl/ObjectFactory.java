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

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.oclc.purl.dsdl.svrl package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    public final static QName _SchematronOutput_QNAME = new QName("http://purl.oclc.org/dsdl/svrl", "schematron-output");
    public final static QName _Text_QNAME = new QName("http://purl.oclc.org/dsdl/svrl", "text");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.oclc.purl.dsdl.svrl
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SchematronOutputType }
     * 
     * @return
     *     The created SchematronOutputType object and never <code>null</code>.
     */
    public SchematronOutputType createSchematronOutputType() {
        return new SchematronOutputType();
    }

    /**
     * Create an instance of {@link org.oclc.purl.dsdl.svrl.NsPrefixInAttributeValues }
     * 
     * @return
     *     The created NsPrefixInAttributeValues object and never <code>null</code>.
     */
    public NsPrefixInAttributeValues createNsPrefixInAttributeValues() {
        return new NsPrefixInAttributeValues();
    }

    /**
     * Create an instance of {@link org.oclc.purl.dsdl.svrl.ActivePattern }
     * 
     * @return
     *     The created ActivePattern object and never <code>null</code>.
     */
    public ActivePattern createActivePattern() {
        return new ActivePattern();
    }

    /**
     * Create an instance of {@link org.oclc.purl.dsdl.svrl.FiredRule }
     * 
     * @return
     *     The created FiredRule object and never <code>null</code>.
     */
    public FiredRule createFiredRule() {
        return new FiredRule();
    }

    /**
     * Create an instance of {@link org.oclc.purl.dsdl.svrl.DiagnosticReference }
     * 
     * @return
     *     The created DiagnosticReference object and never <code>null</code>.
     */
    public DiagnosticReference createDiagnosticReference() {
        return new DiagnosticReference();
    }

    /**
     * Create an instance of {@link org.oclc.purl.dsdl.svrl.FailedAssert }
     * 
     * @return
     *     The created FailedAssert object and never <code>null</code>.
     */
    public FailedAssert createFailedAssert() {
        return new FailedAssert();
    }

    /**
     * Create an instance of {@link SuccessfulReport }
     * 
     * @return
     *     The created SuccessfulReport object and never <code>null</code>.
     */
    public SuccessfulReport createSuccessfulReport() {
        return new SuccessfulReport();
    }

    /**
     * Create an instance of {@link javax.xml.bind.JAXBElement }{@code <}{@link SchematronOutputType }{@code >}}
     * 
     * @return
     *     The created JAXBElement and never <code>null</code>.
     */
    @XmlElementDecl(namespace = "http://purl.oclc.org/dsdl/svrl", name = "schematron-output")
    public JAXBElement<SchematronOutputType> createSchematronOutput(final SchematronOutputType value) {
        return new JAXBElement<>(_SchematronOutput_QNAME, SchematronOutputType.class, null, value);
    }

    /**
     * Create an instance of {@link javax.xml.bind.JAXBElement }{@code <}{@link String }{@code >}}
     * 
     * @return
     *     The created JAXBElement and never <code>null</code>.
     */
    @XmlElementDecl(namespace = "http://purl.oclc.org/dsdl/svrl", name = "text")
    public JAXBElement<String> createText(final String value) {
        return new JAXBElement<>(_Text_QNAME, String.class, null, value);
    }

}
