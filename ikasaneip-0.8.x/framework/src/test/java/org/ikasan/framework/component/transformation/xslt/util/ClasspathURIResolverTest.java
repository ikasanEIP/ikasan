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
package org.ikasan.framework.component.transformation.xslt.util;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link ClasspathURIResolver}
 * 
 * @author Ikasan Development Team
 *
 */
public class ClasspathURIResolverTest
{
    /** URIResolver to test */
    private URIResolver resolverToTest = new ClasspathURIResolver();

    /**
     * When called with URI string that does not exist on classpath,
     * URIResolver instance returns null.
     * 
     * @throws TransformerException thrown if error resolving URI
     */
    @Test public void uriResolver_returns_null_if_requested_uri_does_not_exist_on_classpath() throws TransformerException
    {
        Source source = this.resolverToTest.resolve("dummy.xsl", "");
        Assert.assertNull("'dummy.xsl' does not exist on classpath, returned source must be null.", source);
    }

    /**
     * When called with URI string that does exist on classpath, URIResolver
     * must return a {@link Source} object of resolved uri.
     * 
     * @throws TransformerException thrown if error resolving URI
     */
    @Test public void uriResolver_resolves_successfully_if_requested_uri_exists_on_classpath() throws TransformerException
    {
        Source source = this.resolverToTest.resolve("uriResolverTest.xsl", "");
        Assert.assertNotNull("'resolverTest.xsl' exist on classpath, returned source must not be null.", source);
    }
}
