/*
 * $Id: RemoveAllItemsAction.java 40648 2014-11-07 11:12:53Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationUI/war/src/main/java/org/ikasan/mapping/configuration/ui/action/RemoveAllItemsAction.java $
 *
 * ====================================================================
 *
 * Copyright (c) 2000-2011 by Mizuho International plc.
 * All Rights Reserved.
 *
 * ====================================================================
 *
 */
package org.ikasan.dashboard.ui.mappingconfiguration.action;

import org.apache.log4j.Logger;
import org.ikasan.dashboard.ui.framework.action.Action;
import org.ikasan.dashboard.ui.mappingconfiguration.component.MappingConfigurationConfigurationValuesTable;

/**
 * @author CMI2 Development Team
 *
 */
public class RemoveAllItemsAction implements Action
{
    private Logger logger = Logger.getLogger(RemoveAllItemsAction.class);

    private MappingConfigurationConfigurationValuesTable mappingConfigurationConfigurationValuesTable;


    /**
     * Constructor
     * 
     * @param mappingConfiguration
     * @param mappingConfigurationConfigurationValuesTable
     */
    public RemoveAllItemsAction(MappingConfigurationConfigurationValuesTable mappingConfigurationConfigurationValuesTable)
    {
        super();
        this.mappingConfigurationConfigurationValuesTable = mappingConfigurationConfigurationValuesTable;
    }

    /* (non-Javadoc)
     * @see com.mapping.configuration.ui.action.Action#exectuteAction()
     */
    @Override
    public void exectuteAction()
    {
        mappingConfigurationConfigurationValuesTable.removeAllItems();
    }

    /* (non-Javadoc)
     * @see com.mapping.configuration.ui.action.Action#ignoreAction()
     */
    @Override
    public void ignoreAction()
    {
        // Nothing to do here.
    }
}
