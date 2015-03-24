/*
 * $Id: IkasanUINavigator.java 40648 2014-11-07 11:12:53Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationUI/war/src/main/java/org/ikasan/mapping/configuration/ui/navigation/IkasanUINavigator.java $
 *
 * ====================================================================
 *
 * Copyright (c) 2000-2011 by Mizuho International plc.
 * All Rights Reserved.
 *
 * ====================================================================
 *
 */
package org.ikasan.dashboard.ui.framework.navigation;

import java.util.List;

import org.ikasan.dashboard.ui.framework.display.IkasanUIView;
import org.ikasan.dashboard.ui.framework.display.ViewComponentContainer;

/**
 * @author CMI2 Development Team
 *
 */
public class IkasanUINavigator
{
	private static final long serialVersionUID = -193864770035097124L;

    private String name;
    private List<IkasanUIView> ikasanViews;
    private ViewComponentContainer container;
   
    /**
     * Constructor 
     * @param name
     * @param ikasanViews
     */
    public IkasanUINavigator(String name, List<IkasanUIView> ikasanViews,
    		ViewComponentContainer container) 
    {
		super();
		this.name = name;
		if(this.name == null)
		{
			throw new IllegalArgumentException("name cannot be null!");
		}
		this.ikasanViews = ikasanViews;
		if(this.ikasanViews == null)
		{
			throw new IllegalArgumentException("ikasanViews cannot be null!");
		}
		this.container = container;
		if(this.container == null)
		{
			throw new IllegalArgumentException("container cannot be null!");
		}
	}

	public ViewComponentContainer getContainer() 
	{
		return container;
	}

	public String getName() 
	{
		return name;
	}

	public List<IkasanUIView> getIkasanViews() 
	{
		return ikasanViews;
	}
}
