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
package org.ikasan.framework.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Abstract <code>InputStream</code> implementation supported by blocks of byte arrays
 * 
 * Subclasses of this class will incrementally supply an unknown number of content blocks in the form of byte arrays,
 * which will be available seamlessly to the stream
 * 
 * hooks exist for injecting additional content: 1) before the first block (header) 2) between each block 3) after each
 * block 4) after the last block (footer)
 * 
 * @author Ikasan Development Team
 */
public abstract class AggregatedContentInputStream extends InputStream
{
    /** Before start state */
    private static final int STATE_BEFORE_START = 0;

    /** Consuming Header state */
    private static final int STATE_CONSUMING_HEADER = 1;

    /** Before content state */
    private static final int STATE_BEFORE_CONTENT = 2;

    /** Consuming content state */
    private static final int STATE_CONSUMING_CONTENT = 3;

    /** After content state */
    private static final int STATE_AFTER_CONTENT = 4;

    /** Consuming footer state */
    private static final int STATE_CONSUMING_FOOTER = 5;

    /** After footer state */
    private static final int STATE_AFTER_FOOTER = 6;

    /** State, defaults to before start */
    private int state = STATE_BEFORE_START;

    /** The internal InputStream being used to support this class */
    private InputStream inputStream;

    @Override
    public int read() throws IOException
    {
        if (state == STATE_BEFORE_START)
        {
            rolloverInputStream();
        }
        if (inputStream == null)
        {
            return -1;
        }
        int result = inputStream.read();
        if (result == -1)
        {
            rolloverInputStream();
            result = read();
        }
        return result;
    }

    /**
     * Causes a new chunk to be loaded and its contents used to populate the internal InputStream
     * 
     * @throws IOException Exception if the IO Stream fails
     */
    private void rolloverInputStream() throws IOException
    {
        inputStream = null;
        if (state == STATE_BEFORE_START)
        {
            attemptHeader();
        }
        else if (state == STATE_CONSUMING_HEADER)
        {
            attemptContentBlock();
        }
        else if (state == STATE_CONSUMING_CONTENT)
        {
            attemptAfterContent();
        }
        else if (state == STATE_BEFORE_CONTENT)
        {
            inputStream = new ByteArrayInputStream(getNextContent());
            state = STATE_CONSUMING_CONTENT;
        }
        else if (state == STATE_AFTER_CONTENT)
        {
            if (hasMoreContent())
            {
                attemptBeforeContent();
            }
            else
            {
                attemptFooter();
            }
        }
        else if (state == STATE_CONSUMING_FOOTER)
        {
            state = STATE_AFTER_FOOTER;
        }
    }

    /**
     * Attempt the consumption of the footer
     * 
     * @throws IOException Exception if the IO Stream fails
     */
    private void attemptFooter() throws IOException
    {
        state = STATE_CONSUMING_FOOTER;
        byte[] footer = getFooter();
        if ((footer != null) && (footer.length > 0))
        {
            inputStream = new ByteArrayInputStream(getFooter());
        }
        else
        {
            rolloverInputStream();
        }
    }

    /**
     * Attempt consuming the data after the content
     * 
     * @throws IOException Exception if the IO Stream fails
     */
    private void attemptAfterContent() throws IOException
    {
        state = STATE_AFTER_CONTENT;
        byte[] afterContent = getAfterContent();
        if ((afterContent != null) && (afterContent.length > 0))
        {
            inputStream = new ByteArrayInputStream(getAfterContent());
        }
        else
        {
            rolloverInputStream();
        }
    }

    /**
     * Attempt to consume the content block
     * 
     * @throws IOException Exception if the IO Stream fails
     */
    private void attemptContentBlock() throws IOException
    {
        if (hasMoreContent())
        {
            attemptBeforeContent();
        }
        else
        {
            // ie there is no content whatsoever
            state = STATE_AFTER_CONTENT;
            rolloverInputStream();
        }
    }

    /**
     * Attempt consumer the header
     * 
     * @throws IOException Exception if the IO Stream fails
     */
    private void attemptHeader() throws IOException
    {
        state = STATE_CONSUMING_HEADER;
        byte[] header = getHeader();
        if ((header != null) && (header.length > 0))
        {
            inputStream = new ByteArrayInputStream(header);
        }
        else
        {
            rolloverInputStream();
        }
    }

    /**
     * Attempt consumer the data before the content
     * 
     * @throws IOException Exception if the IO Stream fails
     */
    private void attemptBeforeContent() throws IOException
    {
        state = STATE_BEFORE_CONTENT;
        byte[] beforeContent = getBeforeContent();
        if ((beforeContent != null) && (beforeContent.length > 0))
        {
            inputStream = new ByteArrayInputStream(beforeContent);
        }
        else
        {
            rolloverInputStream();
        }
    }

    /**
     * Hook method for subclasses to inject footer content after last block
     * 
     * @return footer
     */
    protected byte[] getFooter()
    {
        return new byte[] {};
    }

    /**
     * Hook method for subclasses to inject content after each block
     * 
     * @return after content
     */
    protected byte[] getAfterContent()
    {
        return new byte[] {};
    }

    /**
     * Hook method for subclasses to inject content before each block
     * 
     * @return before content
     */
    protected byte[] getBeforeContent()
    {
        return new byte[] {};
    }

    /**
     * Hook method for subclasses to header content before first block
     * 
     * @return header
     */
    protected byte[] getHeader()
    {
        return new byte[] {};
    }

    /**
     * Returns the next chunk of content
     * 
     * @return next bit of content
     */
    protected abstract byte[] getNextContent();

    /**
     * Returns true if there is more content to consume
     * 
     * @return true if there is more content to consume
     */
    protected abstract boolean hasMoreContent();
}
