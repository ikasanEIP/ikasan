/*
 * $Id: AggregatedContentInputStream.java 16808 2009-04-27 07:28:17Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/framework/src/main/java/org/ikasan/framework/util/AggregatedContentInputStream.java $
 * 
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * Copyright (c) 2003-2008 Mizuho International plc. and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the 
 * Free Software Foundation Europe e.V. Talstrasse 110, 40217 Dusseldorf, Germany 
 * or see the FSF site: http://www.fsfeurope.org/.
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
