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
package org.ikasan.framework.exception.user;

/**
 * User defined exception handler constants for population and retrieval of ExceptionContext map.
 * 
 * TODO Constants as an interface, best practice?
 * TODO declaring new String when String literal will do?
 * 
 * @author Ikasan Development Team
 */
public interface UserExceptionHandlerConstants
{
    /** User defined exception instance */
    public static final String USER_EXCEPTION_DEF = new String("userExceptionDef");

    /** External defined exception instance */
    public static final String EXTERNAL_EXCEPTION_DEF = new String("extExceptionDef");

    /** Object to record user exceptions allowing duplicate management */
    public static final String USER_EXCEPTION_PUBLISHABLE = new String("userExceptionPublishable");

    /** External exception as an XML string for publication */
    public static final String EXTERNAL_EXCEPTION_XML = new String("externalExceptionXml");
}
