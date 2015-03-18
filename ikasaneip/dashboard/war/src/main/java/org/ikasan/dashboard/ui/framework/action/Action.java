/*
 * $Id: Action.java 40648 2014-11-07 11:12:53Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationUI/war/src/main/java/org/ikasan/mapping/configuration/ui/action/Action.java $
 *
 * ====================================================================
 *
 * Copyright (c) 2000-2011 by Mizuho International plc.
 * All Rights Reserved.
 *
 * ====================================================================
 *
 */
package org.ikasan.dashboard.ui.framework.action;

/**
 * @author CMI2 Development Team
 *
 */
public interface Action
{
    /**
     * This method causes the action to be executed. 
     */
    public void exectuteAction();

    /**
     * This method causes the action to be ignored. 
     */
    public void ignoreAction();
}
