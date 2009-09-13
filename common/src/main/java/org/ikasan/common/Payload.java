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
package org.ikasan.common;

/**
 * Payload providing the generic facade for all data to be moved around as a common object.
 * 
 * @author Ikasan Development Team
 */
public interface Payload extends MetaDataInterface
{
    /** Root name of the payload */
    public static final String PAYLOAD_ROOT_NAME = "payload";

    /** Constant for the payload counter in the JMS mapMessage */
    public static final String PAYLOAD_COUNT_KEY = "payloadCount";

    /**
     * Set the content of the payload
     * 
     * @param content content to set
     */
    public void setContent(final byte[] content);

    /**
     * Get the content of the payload
     * 
     * @return content of payload
     */
    public byte[] getContent();

    /**
     * Test the equality of two payload instances
     * 
     * @param payload payload to test against
     * @return boolean
     */
    public boolean equals(Payload payload);

    /**
     * String representation of the payload
     * 
     * @return String representation of the payload
     */
    public String toString();

    /**
     * String representation of the payload
     * 
     * @param length length of representation
     * @return String representation of the payload
     */
    public String toString(int length);

    /** Base64 encode the payload */
    public void base64EncodePayload();

    /**
     * Returns a completely new instance of the payload with a deep copy of all fields. Note the subtle difference in
     * comparison with spawn() which changes some field values to reflect a newly created instance.
     * 
     * @return a Payload
     * @throws CloneNotSupportedException Exception if clone is not supported by implementer
     */
    public Payload clone() throws CloneNotSupportedException;

    /**
     * Returns a completely new instance of the payload with a deep copy of all fields with the exception of id and
     * timestamp which are populated with new values to reflect that this is a distinctly new instance from the
     * original. Note the subtle difference in comparison with clone() which does not change any fields from their
     * original values.
     * 
     * @return a Payload
     * @throws CloneNotSupportedException Exception if clone is not supported by implementer
     */
    public Payload spawn() throws CloneNotSupportedException;
}
