/*
 * $Id$
 * $URL$
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
package org.ikasan.common.component;

/**
 * Enumeration of specs used within the Payload object.
 * 
 * @author Ikasan Development Team
 */
public enum Spec
{
    /** XML */
    TEXT_XML("text/xml"), //$NON-NLS-1$
    /** HTML */
    TEXT_HTML("text/html"), //$NON-NLS-1$
    /** CSV */
    TEXT_CSV("text/csv"), //$NON-NLS-1$
    /** TXT */
    TEXT_PLAIN("text/plain"), //$NON-NLS-1$
    /** JAR */
    BYTE_JAR("byte/jar"), //$NON-NLS-1$
    /** ZIP */
    BYTE_ZIP("byte/zip"), //$NON-NLS-1$
    /** plain binary - default */
    BYTE_PLAIN("byte/plain"); //$NON-NLS-1$

    /** Serialize ID */
    private static final long serialVersionUID = 1L;

    /** Spec MIME type */
    private final String mime_type;

    /**
     * Creates a new instance of <code>Spec</code> with the specified MIME type.
     * 
     * @param mime_type - The spec MIME type
     */
    private Spec(final String mime_type)
    {
        this.mime_type = mime_type;
    }

    /**
     * Utility method for returning spec as String
     */
    @Override
    public String toString()
    {
        return this.mime_type;
    }

}
