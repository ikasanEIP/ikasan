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
package org.ikasan.component.converter.xml;

import org.ikasan.component.converter.xml.jaxb.Example;

public class ExampleEventFactory
{
    final String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><example xsi:schemaLocation=\"http://ikasan.org/domain example.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><one>1</one><two>2</two></example>";

    final String sparseXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><example xsi:schemaLocation=\"http://ikasan.org/domain example.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"></example>";

    final String xmlOutOfOrder = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><example xsi:schemaLocation=\"http://ikasan.org/domain example.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><two>2</two><one>1</one></example>";

    final String xmlInvalidJaxb = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><example xsi:schemaLocation=\"http://ikasan.org/domain example.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><one>1</one><two>2</two><three>3</three></example>";

    final String xmlAdapted = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><example xsi:schemaLocation=\"http://ikasan.org/domain example.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><one>one</one><two>two</two></example>";

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

    public String getXmlInvalidJaxb()
    {
        return xmlInvalidJaxb;
    }

    public String getXmlAdapted()
    {
        return xmlAdapted;
    }
}