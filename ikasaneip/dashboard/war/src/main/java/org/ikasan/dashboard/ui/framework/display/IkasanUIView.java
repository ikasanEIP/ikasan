/*
 * $Id: IkasanUIView.java 40648 2014-11-07 11:12:53Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationUI/war/src/main/java/org/ikasan/mapping/configuration/ui/display/IkasanUIView.java $
 *
 * ====================================================================
 *
 * Copyright (c) 2000-2011 by Mizuho International plc.
 * All Rights Reserved.
 *
 * ====================================================================
 *
 */
package org.ikasan.dashboard.ui.framework.display;

import com.vaadin.navigator.View;

/**
 * @author CMI2 Development Team
 *
 */
public class IkasanUIView
{
    private String path;
    private View view;

    /**
     * Constructor
     * 
     * @param path
     * @param view
     */
    public IkasanUIView(String path, View view)
    {
        super();
        this.path = path;
        this.view = view;
    }

    /**
     * @return the path
     */
    public String getPath()
    {
        return path;
    }

    /**
     * @return the view
     */
    public View getView()
    {
        return view;
    }
}
