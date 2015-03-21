/*
 * $Id: KeyLocationQueryProcessor.java 32153 2013-08-27 12:46:19Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationService/api/src/main/java/com/mizuho/cmi2/mappingConfiguration/keyQueryProcessor/KeyLocationQueryProcessor.java $
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
public interface KeyLocationQueryProcessor
{
    /**
     * 
     * @param keyLocation
     * @param payload
     * @return
     */
    public String getKeyValueFromPayload(String keyLocation, byte[] payload) throws KeyLocationQueryProcessorException;
}
