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
package org.ikasan.component.validator.xml;

import org.w3c.dom.ls.LSInput;

import java.io.InputStream;
import java.io.Reader;

/**
 * Ikasan basic implmentation of LSInput interface. Required by ResourceResolver implementation.
 */
public class LSInputImpl implements LSInput
{

    private String publicId;

    private String systemId;

    private String baseURI;

    private InputStream inputStream;


    @Override public Reader getCharacterStream()
    {
        return null;
    }

    @Override public void setCharacterStream(Reader characterStream)
    {
    }

    @Override public InputStream getByteStream()
    {
        return inputStream;
    }

    @Override public void setByteStream(InputStream byteStream)
    {
        this.inputStream=byteStream;
    }

    @Override public String getStringData()
    {
        return null;
    }

    @Override public void setStringData(String stringData)
    {
    }

    @Override public String getSystemId()
    {
        return systemId;
    }

    @Override public void setSystemId(String systemId)
    {
        this.systemId = systemId;
    }

    @Override public String getPublicId()
    {
        return publicId;
    }

    @Override public void setPublicId(String publicId)
    {
        this.publicId = publicId;
    }

    @Override public String getBaseURI()
    {
        return baseURI;
    }

    @Override public void setBaseURI(String baseURI)
    {
        this.baseURI=baseURI;
    }

    @Override public String getEncoding()
    {
        return null;
    }

    @Override public void setEncoding(String encoding)
    {
    }

    @Override public boolean getCertifiedText()
    {
        return false;
    }

    @Override public void setCertifiedText(boolean certifiedText)
    {
    }
}
