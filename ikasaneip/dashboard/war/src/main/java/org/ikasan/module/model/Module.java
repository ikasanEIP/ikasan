/*
 * $Id: Module.java 40526 2014-11-04 16:19:11Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationUI/war/src/main/java/org/ikasan/module/model/Module.java $
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
public class Module
{
    private String serverName;
    private String moduleName;
    private ArrayList<Flow> flows;

    /**
     * @return the moduleName
     */
    public String getModuleName()
    {
        return moduleName;
    }

    /**
     * @param moduleName the moduleName to set
     */
    public void setModuleName(String moduleName)
    {
        this.moduleName = moduleName;
    }

    /**
     * @return the flows
     */
    public ArrayList<Flow> getFlows()
    {
        return flows;
    }

    /**
     * @param flows the flows to set
     */
    public void setFlows(ArrayList<Flow> flows)
    {
        this.flows = flows;
    }

    /**
     * @return the serverName
     */
    public String getServerName()
    {
        return serverName;
    }

    /**
     * @param serverName the serverName to set
     */
    public void setServerName(String serverName)
    {
        this.serverName = serverName;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        final int maxLen = 10;
        return "Module [serverName=" + serverName + ", moduleName=" + moduleName + ", flows="
                + (flows != null ? flows.subList(0, Math.min(flows.size(), maxLen)) : null) + "]";
    }
}
