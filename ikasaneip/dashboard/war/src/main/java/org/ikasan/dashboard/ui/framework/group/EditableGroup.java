/*
 * $Id: EditableGroup.java 40648 2014-11-07 11:12:53Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationUI/war/src/main/java/org/ikasan/mapping/configuration/ui/group/EditableGroup.java $
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
public class EditableGroup
{
    private ArrayList<Editable> editables = new ArrayList<Editable>();

    /**
     * Set editable on the group.
     * 
     * @param isEditable
     */
    public void setEditable(boolean isEditable)
    {
        for(Editable editable: editables)
        {
            editable.setEditable(isEditable);
        }
    }

    /**
     * @return the editables
     */
    public ArrayList<Editable> getEditables()
    {
        return editables;
    }

    /**
     * @param editables the editables to set
     */
    public void setEditables(ArrayList<Editable> editables)
    {
        this.editables = editables;
    }

    
}
