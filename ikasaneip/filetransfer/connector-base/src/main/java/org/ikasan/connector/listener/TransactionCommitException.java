/*
 * $Id: TransactionCommitException.java 43330 2015-02-12 17:55:01Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/Ikasan-0.8.4.x/connector-base/src/main/java/org/ikasan/connector/listener/TransactionCommitException.java $
 *
 * ====================================================================
 *
 * Copyright (c) 2000-2011 by Mizuho International plc.
 * All Rights Reserved.
 *
 * ====================================================================
 *
 */
package org.ikasan.connector.listener;

/**
 * @author CMI2 Development Team
 *
 */
public class TransactionCommitException extends Exception
{
    /**
     * 
     */
    private static final long serialVersionUID = 748687679508953233L;

    /**
     * 
     */
    public TransactionCommitException()
    {
    }

    /**
     * @param message
     */
    public TransactionCommitException(String message)
    {
        super(message);
    }

    /**
     * @param cause
     */
    public TransactionCommitException(Throwable cause)
    {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public TransactionCommitException(String message, Throwable cause)
    {
        super(message, cause);
        // TODO Auto-generated constructor stub
    }
}
