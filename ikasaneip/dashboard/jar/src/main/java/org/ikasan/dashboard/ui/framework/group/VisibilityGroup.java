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
package org.ikasan.dashboard.ui.framework.group;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ikasan.dashboard.ui.framework.constants.SecurityConstants;
import org.ikasan.dashboard.ui.framework.util.DashboardSessionValueConstants;
import org.ikasan.security.service.authentication.IkasanAuthentication;

import com.vaadin.server.VaadinService;
import com.vaadin.ui.Component;
import com.vaadin.ui.Table;

/**
 * @author Ikasan Development Team
 *
 */
public class VisibilityGroup
{
    private static Logger logger = LoggerFactory.getLogger(VisibilityGroup.class);

    private HashMap<Component, List<String>> components = new HashMap<Component, List<String>>();
    private ArrayList<Table> refreshableTables = new ArrayList<Table>();


    /**
     * Method to set if the components are visible.
     */
    public void setVisible()
    {
    	final IkasanAuthentication authentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
 	        	.getAttribute(DashboardSessionValueConstants.USER);
    	 
        for(Component component: components.keySet())
        {
            for(String policyName: components.get(component))
            {

                if (authentication != null
                        && (authentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY)
                        || authentication.hasGrantedAuthority(policyName)))
                {
                    component.setVisible(true);
                    break;
                }
                else
                {
                    component.setVisible(false);
                }
            }
        }

        for(Table table: refreshableTables)
        {
            table.refreshRowCache();
        }
    }

    /**
     * Method to set if the components are visible.
     *
     * @param linkedItemType
     * @param linkedItemId
     */
    public void setVisible(String linkedItemType, Long linkedItemId)
    {
    	final IkasanAuthentication authentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
 	        	.getAttribute(DashboardSessionValueConstants.USER);
    	 
        for(Component component: components.keySet())
        {
            for(String policyName: components.get(component))
            {
                if (authentication != null
                        && (authentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY)
                        || authentication.hasGrantedAuthority(policyName))
                        || authentication.canAccessLinkedItem(linkedItemType, linkedItemId))
                {
                    component.setVisible(true);
                    break;
                }
                else
                {
                    component.setVisible(false);
                }
            }
        }

        for(Table table: refreshableTables)
        {
            table.refreshRowCache();
        }
    }

    /**
     * Register a component with this group.
     * @param component
     */
    public void registerComponent(String policyName, Component component)
    {
        if(this.components.containsKey(component))
        {
            this.components.get(component).add(policyName);
        }
        else
        {

        }
    }

    /**
     * Register a table with this group.
     * @param table
     */
    public void registerRefreshableTable(Table table)
    {
        this.refreshableTables.add(table);
    }

    /**
     * @return the components
     */
    public HashMap<Component, List<String>> getComponents()
    {
        return components;
    }

    /**
     * @param components the components to set
     */
    public void setComponents(HashMap<Component, List<String>> components)
    {
        this.components = components;
    }
}
