/*
 * $Id: MappingConfigurationValue.java 40648 2014-11-07 11:12:53Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationUI/war/src/main/java/org/ikasan/mapping/configuration/ui/model/MappingConfigurationValue.java $
 *
 * ====================================================================
 *
 * Copyright (c) 2000-2011 by Mizuho International plc.
 * All Rights Reserved.
 *
 * ====================================================================
 *
 */
package org.ikasan.dashboard.ui.mappingconfiguration.model;

import java.util.ArrayList;

import com.mizuho.cmi2.mappingConfiguration.model.SourceConfigurationValue;
import com.mizuho.cmi2.mappingConfiguration.model.TargetConfigurationValue;

/**
 * @author CMI2 Development Team
 *
 */
public class MappingConfigurationValue
{

    private TargetConfigurationValue targetConfigurationValue;
    private ArrayList<SourceConfigurationValue> sourceConfigurationValues = new ArrayList<SourceConfigurationValue>();

    /**
     * Default Constructor
     */
    public MappingConfigurationValue()
    {
        super();
    }

    /**
     * Constructor
     * 
     * @param targetConfigurationValue
     * @param sourceConfigurationValues
     */
    public MappingConfigurationValue(TargetConfigurationValue targetConfigurationValue,
            ArrayList<SourceConfigurationValue> sourceConfigurationValues)
    {
        super();
        this.targetConfigurationValue = targetConfigurationValue;
        this.sourceConfigurationValues = sourceConfigurationValues;
    }

    /**
     * @return the targetConfigurationValue
     */
    public TargetConfigurationValue getTargetConfigurationValue()
    {
        return targetConfigurationValue;
    }

    /**
     * @param targetConfigurationValue the targetConfigurationValue to set
     */
    public void setTargetConfigurationValue(TargetConfigurationValue targetConfigurationValue)
    {
        this.targetConfigurationValue = targetConfigurationValue;
    }

    /**
     * @return the sourceConfigurationValues
     */
    public ArrayList<SourceConfigurationValue> getSourceConfigurationValues()
    {
        return sourceConfigurationValues;
    }

    /**
     * @param sourceConfigurationValues the sourceConfigurationValues to set
     */
    public void setSourceConfigurationValues(ArrayList<SourceConfigurationValue> sourceConfigurationValues)
    {
        this.sourceConfigurationValues = sourceConfigurationValues;
    }

    /**
     * @param sourceConfigurationValues the sourceConfigurationValues to set
     */
    public void addSourceConfigurationValue(SourceConfigurationValue sourceConfigurationValue)
    {
        this.sourceConfigurationValues.add(sourceConfigurationValue);
    }
}
