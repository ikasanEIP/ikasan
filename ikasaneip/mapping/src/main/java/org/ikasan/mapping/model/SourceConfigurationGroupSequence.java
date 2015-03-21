/*
 * $Id: SourceConfigurationGroupSequence.java 40335 2014-10-29 10:42:29Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationService/api/src/main/java/com/mizuho/cmi2/mappingConfiguration/model/SourceConfigurationGroupSequence.java $
 *
 * ====================================================================
 *
 * Copyright (c) 2000-2011 by Mizuho International plc.
 * All Rights Reserved.
 *
 * ====================================================================
 *
 */
package org.ikasan.mapping.model;

import java.math.BigInteger;

/**
 * @author CMI2 Development Team
 *
 */
public class SourceConfigurationGroupSequence
{
    private Long id;
    private Long sequenceNumber;

    /**
     * @return the id
     */
    public Long getId()
    {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id)
    {
        this.id = id;
    }

    /**
     * @return the sequenceNumber
     */
    public Long getSequenceNumber()
    {
        return sequenceNumber;
    }

    /**
     * @param sequenceNumber the sequenceNumber to set
     */
    public void setSequenceNumber(Long sequenceNumber)
    {
        this.sequenceNumber = sequenceNumber;
    }

}
