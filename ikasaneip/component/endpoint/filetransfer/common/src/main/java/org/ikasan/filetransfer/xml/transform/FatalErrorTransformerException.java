/*
 * $Id$
 * $URL$
 * 
 * =============================================================================
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
 * =============================================================================
 */
package org.ikasan.filetransfer.xml.transform;

// Imported trax classes
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;

/**
 * This class extends <code>javax.xml.transform.TransformerException</code>
 * as fatal error exception that occured during the transformation process.
 *
 * @author Ikasan Development Team
 *
 */
public class FatalErrorTransformerException
    extends TransformerException
{
    /**
     * Serial GUID
     */
    private static final long serialVersionUID = 2863683175912012056L;

    /**
     * Creates a new instance of <code>FatalErrorTransformerException</code>
     * with the specified message.
     * @param message 
     *
     */
    public FatalErrorTransformerException(String message)
    {
        super(message);
    }

    /**
     * Creates a new instance of <code>FatalErrorTransformerException</code>
     * with the specified message and source locator.
     * @param message 
     * @param locator 
     *
     */
    public FatalErrorTransformerException(String message,
                                          SourceLocator locator)
    {
        super(message, locator);
    }

    /**
     * Creates a new instance of <code>FatalErrorTransformerException</code>
     * with the specified message, source locator and the existing exception.
     * @param message 
     * @param locator 
     * @param e 
     *
     */
    public FatalErrorTransformerException(String message,
                                          SourceLocator locator, Throwable e)
    {
        super(message, locator, e);
    }

    /**
     * Creates a new instance of <code>FatalErrorTransformerException</code>
     * with the specified message and the existing exception.
     * @param message 
     * @param e 
     *
     */
    public FatalErrorTransformerException(String message, Throwable e)
    {
        super(message, e);
    }

    /**
     * Creates a new instance of <code>FatalErrorTransformerException</code>
     * wrapping the existing exception.
     * @param e 
     *
     */
    public FatalErrorTransformerException(Throwable e)
    {
        super(e);
    }

}
