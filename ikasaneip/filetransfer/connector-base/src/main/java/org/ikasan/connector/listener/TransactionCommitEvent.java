/*
 * $Id: TransactionCommitEvent.java 43330 2015-02-12 17:55:01Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/Ikasan-0.8.4.x/connector-base/src/main/java/org/ikasan/connector/listener/TransactionCommitEvent.java $
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
public class TransactionCommitEvent
{
    private TransactionCommitException exception;

    /**
     * @param exception
     */
    public TransactionCommitEvent(TransactionCommitException exception)
    {
        super();
        this.exception = exception;
    }

    /**
     * @return the exception
     */
    public TransactionCommitException getException()
    {
        return exception;
    }
}
