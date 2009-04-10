/*
 * $Id: Payload.java 16606 2009-04-09 08:07:33Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/common/src/main/java/org/ikasan/common/Payload.java $
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
