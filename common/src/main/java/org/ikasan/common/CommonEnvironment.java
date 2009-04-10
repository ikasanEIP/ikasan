/*
 * $Id: CommonEnvironment.java 16606 2009-04-09 08:07:33Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/common/src/main/java/org/ikasan/common/CommonEnvironment.java $
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
 * Common environment provides the base interface for all environment access.
 * 
 * @author Ikasan Development Team
 */
public interface CommonEnvironment
{
    /**
     * Expand the Environment Variables given the original string, a start and end marker and a boolean to indicate
     * whether we care about errors or not
     * 
     * @param originalStr The original string
     * @param startMarker The start marker
     * @param endMarker The end marker
     * @param screamOnError Flag whether to 'scream' on error
     * @return Expanded Environment Variables
     */
    public String expandEnvVar(String originalStr, String startMarker, String endMarker, boolean screamOnError);

    /**
     * Expand the Environment Variables given the original string, a start and end marker
     * 
     * @param originalStr The original string
     * @param startMarker The start marker
     * @param endMarker The end marker
     * @return Expanded Environment Variables
     */
    public String expandEnvVar(String originalStr, String startMarker, String endMarker);

    /**
     * Expand the Environment Variables given the original string and a boolean to indicate whether we care about errors
     * or not
     * 
     * @param originalStr The original string
     * @param screamOnError Flag whether to 'scream' on error
     * @return Expanded Environment Variables
     */
    public String expandEnvVar(String originalStr, boolean screamOnError);

    /**
     * Expand the Environment Variables given the original string
     * 
     * @param originalStr The original string
     * @return Expanded Environment Variables
     */
    public String expandEnvVar(String originalStr);
}
