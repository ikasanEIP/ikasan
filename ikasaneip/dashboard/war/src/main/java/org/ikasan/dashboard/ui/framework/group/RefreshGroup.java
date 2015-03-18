/*
 * $Id: RefreshGroup.java 40648 2014-11-07 11:12:53Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationUI/war/src/main/java/org/ikasan/mapping/configuration/ui/group/RefreshGroup.java $
 *
 * ====================================================================
 *
 * Copyright (c) 2000-2011 by Mizuho International plc.
 * All Rights Reserved.
 *
 * ====================================================================
 *
 */
package org.ikasan.dashboard.ui.framework.group;

import java.util.ArrayList;

/**
 * @author CMI2 Development Team
 *
 */
public class RefreshGroup implements Refreshable
{
    private ArrayList<Refreshable> refreshableItems = new ArrayList<Refreshable>();

    /* (non-Javadoc)
     * @see com.mapping.configuration.ui.group.Refreshable#refresh()
     */
    @Override
    public void refresh()
    {
        for(Refreshable refreshable: refreshableItems)
        {
            refreshable.refresh();
        }
    }

    /**
     * @return the refreshableItems
     */
    public ArrayList<Refreshable> getRefreshableItems()
    {
        return refreshableItems;
    }

    /**
     * @param refreshableItems the refreshableItems to set
     */
    public void setRefreshableItems(ArrayList<Refreshable> refreshableItems)
    {
        this.refreshableItems = refreshableItems;
    }
}
