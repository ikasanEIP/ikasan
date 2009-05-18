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
package org.ikasan.common;

/**
 * Interface defining the Ikasan runtime environment configuration.
 * 
 * @author <a href="mailto:info@ikasan.org">Ikasan Development Team</a>
 */
public interface IkasanEnv
{
    /**
     * Gets the Ikasan runtime platform configuration directory.
     * @return String
     */
    public String getIkasanConfDir();

    /**
     * Gets the Ikasan runtime platform configuration directory meta data
     * via which the actual value is associated.
     * @return String
     */
    public String getIkasanConfDirMetaData();

    /**
     * Gets the Ikasan runtime platform secure configuration directory.
     * @return String
     */
    public String getIkasanSecureConfDir();

    /**
     * Gets the Ikasan runtime platform secure configuration directory meta data
     * via which the actual value is associated.
     * @return String
     */
    public String getIkasanSecureConfDirMetaData();

    /**
     * Gets the Ikasan runtime platform security configuration resource.
     * @return String
     */
    public String getIkasanSecurityResource();

    /**
     * Gets the Ikasan runtime platform security configuration resource meta data
     * via which the actual value is associated.
     * @return String
     */
    public String getIkasanSecurityResourceMetaData();

    /**
     * Gets the Ikasan runtime Web Server configuration resource.
     * @return String
     */
    public String getIkasanWebResource();

    /**
     * Gets the Ikasan runtime Web Server configuration resource meta data
     * via which the actual value is associated.
     * @return String
     */
    public String getIkasanWebResourceMetaData();

}
