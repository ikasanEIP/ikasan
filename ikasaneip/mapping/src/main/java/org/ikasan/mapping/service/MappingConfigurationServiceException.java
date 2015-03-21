/*
 * $Id: MappingConfigurationServiceException.java 31884 2013-07-31 10:25:31Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationService/api/src/main/java/com/mizuho/cmi2/mappingConfiguration/service/MappingConfigurationServiceException.java $
 *
 * ====================================================================
 *
 * Copyright (c) 2000-2011 by Mizuho International plc.
 * All Rights Reserved.
 *
 * ====================================================================
 *
 */
package org.ikasan.mapping.service;

/**
 * @author CMI2 Development Team
 *
 */
public class MappingConfigurationServiceException extends Exception
{
    private static final long serialVersionUID = 8000714385648036392L;

    /**
     * 
     */
    public MappingConfigurationServiceException()
    {
        super();
    }

    /**
     * @param message
     * @param cause
     */
    public MappingConfigurationServiceException(String message, Throwable cause)
    {
        super(message, cause);
    }

    /**
     * @param message
     */
    public MappingConfigurationServiceException(String message)
    {
        super(message);
    }

    /**
     * @param cause
     */
    public MappingConfigurationServiceException(Throwable cause)
    {
        super(cause);
    }
}
