/*
 * $Id: KeyLocationQueryProcessorException.java 31879 2013-07-30 15:03:33Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationService/api/src/main/java/com/mizuho/cmi2/mappingConfiguration/keyQueryProcessor/KeyLocationQueryProcessorException.java $
 *
 * ====================================================================
 *
 * Copyright (c) 2000-2011 by Mizuho International plc.
 * All Rights Reserved.
 *
 * ====================================================================
 *
 */
package org.ikasan.mapping.keyQueryProcessor;

/**
 * @author CMI2 Development Team
 *
 */
public class KeyLocationQueryProcessorException extends Exception
{

    /**
     * 
     */
    private static final long serialVersionUID = -8680732395798387051L;

    /**
     * 
     */
    public KeyLocationQueryProcessorException()
    {
        super();
    }

    /**
     * @param arg0
     * @param arg1
     */
    public KeyLocationQueryProcessorException(String message, Throwable throwable)
    {
        super(message, throwable);
    }

    /**
     * @param message
     */
    public KeyLocationQueryProcessorException(String message)
    {
        super(message);
    }

    /**
     * @param throwable
     */
    public KeyLocationQueryProcessorException(Throwable throwable)
    {
        super(throwable);
    }
}
