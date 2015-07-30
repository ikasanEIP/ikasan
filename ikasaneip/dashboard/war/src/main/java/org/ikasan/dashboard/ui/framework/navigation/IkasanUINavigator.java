 /*
 * $Id$
 * $URL$
 *
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 *
 * Distributed under the Modified BSD License.
 * Copyright notice: The copyright for this software and a full listing
 * of individual contributors are as shown in the packaged copyright.txt
 * file.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  - Neither the name of the ORGANIZATION nor the names of its contributors may
 *    be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 */
package org.ikasan.dashboard.ui.framework.navigation;

import java.util.List;

import org.ikasan.dashboard.ui.framework.display.IkasanUIView;
import org.ikasan.dashboard.ui.framework.display.ViewComponentContainer;

import com.vaadin.ui.ComponentContainer;

/**
 * @author Ikasan Development Team
 *
 */
public class IkasanUINavigator
{
	private static final long serialVersionUID = -193864770035097124L;

    private String name;
    private List<IkasanUIView> ikasanViews;
    private ViewComponentContainer container;
    private ComponentContainer parentContainer;
   
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

	/**
	 * @return the parentContainer
	 */
	public ComponentContainer getParentContainer()
	{
		return parentContainer;
	}

	/**
	 * @param parentContainer the parentContainer to set
	 */
	public void setParentContainer(ComponentContainer parentContainer)
	{
		this.parentContainer = parentContainer;
	}
}
