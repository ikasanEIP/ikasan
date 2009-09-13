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
package org.ikasan.common.util;

// Imported apache commons classes
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

/**
 * This convenient class wraps apache's commons codec.
 * 
 * @author Ikasan Development Team
 */
public class Codec
{

    /**
     * Don't let anyone instantiate this class.
     */
    private Codec()
    {
        // Do Nothing
    }

    /**
     * Encodes the incoming data using the defined encoding, if encoding is
     * supported. Currently supported encodings are:-
     * <ul>
     *   <li>base64</li>
     *   <li>hex</li>
     *   <li>noenc</li>
     * </ul>
     * 
     * @param data is the byte array data to encode.
     * @param encoding is the encoding algorithm used to encode the data.
     * @return encoded data
     */
    public static byte[] encode(byte[] data, String encoding)
    {
        if (encoding.equalsIgnoreCase("base64"))
        {
            return Base64.encodeBase64(data);
        }
        else if (encoding.equalsIgnoreCase("hex"))
        {
            return new Hex().encode(data);
        }
        else if (encoding.equalsIgnoreCase("noenc"))
        {
            return data;
        }
        else
        {
            throw new IllegalArgumentException("Unsupported encoding algorithm '" + encoding + "'");
        }
    }

    /**
     * Decodes the incoming encoded data using the defined encoding, if
     * encoding is supported. Currently supported encodings are:-
     * <ul>
     * <li>base64</li>
     * <li>hex</li>
     * <li>noenc</li>
     * </ul>
     * 
     * @param data is the byte array data to decode.
     * @param encoding is the encoding algorithm used to decode the data.
     * @return encoded data
     * @throws DecoderException 
     */
    public static byte[] decode(byte[] data, String encoding) throws DecoderException
    {
        if (encoding.equalsIgnoreCase("base64"))
        {
            return Base64.decodeBase64(data);
        }
        else if (encoding.equalsIgnoreCase("hex"))
        {
            return new Hex().decode(data);
        }
        else if (encoding.equalsIgnoreCase("noenc"))
        {
            return data;
        }
        else
        {
            throw new IllegalArgumentException("Unsupported encoding algorithm '" + encoding + "'");
        }
    }

}
