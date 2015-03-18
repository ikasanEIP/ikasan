/*
 * $Id: Flow.java 40526 2014-11-04 16:19:11Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationUI/war/src/main/java/org/ikasan/module/model/Flow.java $
 *
 * ====================================================================
 *
 * Copyright (c) 2000-2011 by Mizuho International plc.
 * All Rights Reserved.
 *
 * ====================================================================
 *
 */
package org.ikasan.module.model;

import java.util.ArrayList;

/**
 * @author CMI2 Development Team
 *
 */
public class Flow
{
    private String flowName;
    private String initiatorName;
    private ArrayList<Component> components;
    private Module module;

    /**
     * @return the flowName
     */
    public String getFlowName()
    {
        return flowName;
    }

    /**
     * @param flowName the flowName to set
     */
    public void setFlowName(String flowName)
    {
        this.flowName = flowName;
    }

    /**
     * @return the components
     */
    public ArrayList<Component> getComponents()
    {
        return components;
    }

    /**
     * @param components the components to set
     */
    public void setComponents(ArrayList<Component> components)
    {
        this.components = components;
    }

    /**
     * @return the initiatorName
     */
    public String getInitiatorName()
    {
        return initiatorName;
    }

    /**
     * @param initiatorName the initiatorName to set
     */
    public void setInitiatorName(String initiatorName)
    {
        this.initiatorName = initiatorName;
    }

    /**
     * @return the module
     */
    public Module getModule()
    {
        return module;
    }

    /**
     * @param module the module to set
     */
    public void setModule(Module module)
    {
        this.module = module;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        final int maxLen = 10;
        return "Flow [flowName=" + flowName + ", initiatorName=" + initiatorName + ", components="
                + (components != null ? components.subList(0, Math.min(components.size(), maxLen)) : null) + "]";
    }
}
