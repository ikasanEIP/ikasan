/*
 * $Id: NavigateToAction.java 40648 2014-11-07 11:12:53Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationUI/war/src/main/java/org/ikasan/mapping/configuration/ui/action/NavigateToAction.java $
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

import org.ikasan.dashboard.ui.framework.group.FunctionalGroup;
import org.ikasan.dashboard.ui.framework.util.SaveRequiredMonitor;

import com.vaadin.ui.UI;

/**
 * @author CMI2 Development Team
 *
 */
public class NavigateToAction implements Action
{
    private String navigateTo;
    private SaveRequiredMonitor saveRequireMonitor;
    private FunctionalGroup functionalGroup;

    /**
     * Constructor
     * 
     * @param navigateTo
     * @param saveRequireMonitor
     */
    public NavigateToAction(String navigateTo, SaveRequiredMonitor saveRequireMonitor, FunctionalGroup functionalGroup)
    {
        this.navigateTo = navigateTo;
        this.saveRequireMonitor = saveRequireMonitor;
        this.functionalGroup = functionalGroup;
    }

    /* (non-Javadoc)
     * @see com.mapping.configuration.ui.action.Action#exectuteAction()
     */
    @Override
    public void exectuteAction()
    {
        this.saveRequireMonitor.setSaveRequired(false);
        this.functionalGroup.initialiseButtonState();
        UI.getCurrent().getNavigator().navigateTo(navigateTo);
    }

    /* (non-Javadoc)
     * @see com.mapping.configuration.ui.action.Action#ignoreAction()
     */
    @Override
    public void ignoreAction()
    {
        // Nothing to do
    }
}
