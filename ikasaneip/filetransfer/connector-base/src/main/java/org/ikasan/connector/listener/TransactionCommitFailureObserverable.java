/*
 * $Id: TransactionCommitFailureObserverable.java 43330 2015-02-12 17:55:01Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/Ikasan-0.8.4.x/connector-base/src/main/java/org/ikasan/connector/listener/TransactionCommitFailureObserverable.java $
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
public interface TransactionCommitFailureObserverable
{
    /**
     * 
     * @param listener
     */
    public void addListener(TransactionCommitFailureListener listener);
}
