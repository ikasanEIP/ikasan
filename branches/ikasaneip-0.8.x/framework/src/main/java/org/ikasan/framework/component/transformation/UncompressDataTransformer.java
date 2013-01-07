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
package org.ikasan.framework.component.transformation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import org.apache.log4j.Logger;
import org.ikasan.common.Payload;
import org.ikasan.framework.component.Event;

/**
 * A {@link Transformer} implementation that uncompresses  data in GZIP format. Not that
 * this implementation does not support character encoding.
 * 
 * @author Ikasan Development Team
 *
 */
public class UncompressDataTransformer implements Transformer
{
    /** Logger instance */
    private final static Logger logger = Logger.getLogger(UncompressDataTransformer.class);

    /** Constant representing end-of-file is reached. */
    protected static final int END_OF_FILE = -1;

    /* (non-Javadoc)
     * @see org.ikasan.framework.component.transformation.Transformer#onEvent(org.ikasan.framework.component.Event)
     */
    public void onEvent(Event event) throws TransformationException
    {
        ByteArrayOutputStream uncompressedContent = null;
        GZIPInputStream gzipReader = null;
        InputStream compressedContent = null;
        int bytesRead = 0;

        for (Payload payload: event.getPayloads())
        {
            uncompressedContent = null;
            gzipReader = null;
            compressedContent = new ByteArrayInputStream(payload.getContent());
            try
            {
                // Read the compressed file
                gzipReader = new GZIPInputStream(compressedContent);
                uncompressedContent = new ByteArrayOutputStream();
                bytesRead = 0;
                while ((bytesRead = gzipReader.read())!= END_OF_FILE)
                {
                    // Write it to output stream
                    uncompressedContent.write(bytesRead);
                }
                // Set the payload's new transformed content
                payload.setContent(uncompressedContent.toByteArray());
            }
            catch (IOException e)
            {
                throw new TransformationException(e);
            }
            finally
            {
                try
                {
                    if (gzipReader != null)
                    {
                        gzipReader.close();
                    }
                    if (uncompressedContent != null)
                    {
                        uncompressedContent.close();
                    }
                    compressedContent.close(); // Never null!
                }
                catch (IOException e)
                {
                    logger.warn("Could not close streams properly.");
                    throw new TransformationException(e);
                }
            }
        }
    }

}
