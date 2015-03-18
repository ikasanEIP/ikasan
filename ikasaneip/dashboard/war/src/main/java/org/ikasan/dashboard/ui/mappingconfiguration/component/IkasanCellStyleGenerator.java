/*
 * $Id: IkasanCellStyleGenerator.java 40648 2014-11-07 11:12:53Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationUI/war/src/main/java/org/ikasan/mapping/configuration/ui/component/IkasanCellStyleGenerator.java $
 *
 * ====================================================================
 *
 * Copyright (c) 2000-2011 by Mizuho International plc.
 * All Rights Reserved.
 *
 * ====================================================================
 *
 */
package org.ikasan.dashboard.ui.mappingconfiguration.component;

import com.vaadin.ui.Table;

/**
 * @author CMI2 Development Team
 *
 */
public class IkasanCellStyleGenerator implements Table.CellStyleGenerator
{

    private static final long serialVersionUID = -7878494388136309726L;

    /* (non-Javadoc)
     * @see com.vaadin.ui.Table.CellStyleGenerator#getStyle(com.vaadin.ui.Table, java.lang.Object, java.lang.Object)
     */
    @Override
    public String getStyle(Table source, Object itemId, Object propertyId)
    {
        return "ikasan";
    }
}
