/*
 * $Id: Component.java 40526 2014-11-04 16:19:11Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationUI/war/src/main/java/org/ikasan/module/model/Component.java $
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

/**
 * @author CMI2 Development Team
 *
 */
public class Component
{
    private String componentName;

    /**
     * @return the componentName
     */
    public String getComponentName()
    {
        return componentName;
    }

    /**
     * @param componentName the componentName to set
     */
    public void setComponentName(String componentName)
    {
        this.componentName = componentName;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return "Component [componentName=" + componentName + "]";
    }
}
