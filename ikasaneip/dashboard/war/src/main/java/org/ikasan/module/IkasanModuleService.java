/*
 * $Id: IkasanModuleService.java 40526 2014-11-04 16:19:11Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationUI/war/src/main/java/org/ikasan/module/IkasanModuleService.java $
 *
 * ====================================================================
 *
 * Copyright (c) 2000-2011 by Mizuho International plc.
 * All Rights Reserved.
 *
 * ====================================================================
 *
 */
package org.ikasan.module;

import java.util.List;

import org.ikasan.module.model.Module;


/**
 * @author CMI2 Development Team
 *
 */
public interface IkasanModuleService
{
    /**
     * This method is responsible for returning a list of initiator names for 
     * a given URL and module name.
     * @param targetUrl
     * @param moduleName
     * @return
     */
    public List<String> getAllInitiators(String targetUrl, String moduleName);

    /**
     * This method is responsible for returning a list of flow names for 
     * a given URL and module name.
     * 
     * @param targetUrl
     * @param moduleName
     * @return
     */
    public List<String> getAllFlows(String targetUrl, String moduleName);

    /**
     * This method is responsible for returning a list of component names for 
     * a given URL, module name and flow name.
     * 
     * @param targetUrl
     * @param moduleName
     * @param flowName
     * @return
     */
    public List<String> getAllComponents(String targetUrl, String moduleName, String flowName);

    /**
     * This method is responsible for starting an initiator based on URL, module and
     * initiator name.
     * 
     * @param targetUrl
     * @param moduleName
     * @param initiatorName
     */
    public void startInitiator(String targetUrl, String moduleName, String initiatorName);

    /**
     * This method is responsible for stopping an initiator based on URL, module and
     * initiator name.
     * 
     * @param targetUrl
     * @param moduleName
     * @param initiatorName
     */
    public void stopInitiator(String targetUrl, String moduleName, String initiatorName);

    /**
     * This method is responsible for returning the statistics of an initiator based on URL, module and
     * initiator name.
     *  
     * @param targetUrl
     * @param moduleName
     * @param initiatorName
     * @return
     */
    public String getInitiatorStatus(String targetUrl, String moduleName, String initiatorName);

    /**
     * This method is responsible for resolving all Modules from around the estate.
     * 
     * @param targetUrl
     * @param moduleName
     * @return
     */
    public List<Module> getResolvedModules();
}
