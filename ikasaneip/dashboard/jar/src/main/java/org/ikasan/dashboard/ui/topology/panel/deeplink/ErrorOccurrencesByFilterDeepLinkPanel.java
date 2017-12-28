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
package org.ikasan.dashboard.ui.topology.panel.deeplink;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ikasan.dashboard.ui.framework.group.VisibilityGroup;
import org.ikasan.dashboard.ui.framework.panel.NavigationPanel;
import org.ikasan.dashboard.ui.framework.util.DashboardSessionValueConstants;
import org.ikasan.dashboard.ui.framework.window.LoginDialog;
import org.ikasan.dashboard.ui.topology.component.ErrorOccurrenceTab;
import org.ikasan.dashboard.ui.topology.util.FilterMap;
import org.ikasan.security.service.AuthenticationService;
import org.ikasan.security.service.UserService;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.spec.configuration.PlatformConfigurationService;
import org.ikasan.spec.error.reporting.ErrorReportingManagementService;
import org.ikasan.spec.error.reporting.ErrorReportingService;
import org.ikasan.topology.model.Filter;
import org.ikasan.topology.service.TopologyService;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;

/**
 * 
 * @author Ikasan Development Team
 *
 */
@Theme("dashboard")
@SuppressWarnings("serial")
@PreserveOnRefresh
public class ErrorOccurrencesByFilterDeepLinkPanel extends UI
{
	private Logger logger = LoggerFactory.getLogger(ErrorOccurrencesByFilterDeepLinkPanel.class);
	
	private NavigationPanel navigationPanel;
	private AuthenticationService authenticationService;
	private VisibilityGroup visibilityGroup;
	private UserService userService;
	private ErrorReportingService errorReportingService;
	private ErrorReportingManagementService errorReportingManagementService;
	private PlatformConfigurationService platformConfigurationService;
	private TopologyService topologyService;
	
	/**
	 * @param errorOccurrence
	 * @param errorReportingManagementService
	 */
	public ErrorOccurrencesByFilterDeepLinkPanel(ErrorReportingManagementService errorReportingManagementService,
			ErrorReportingService errorReportingService, NavigationPanel navigationPanel,
			AuthenticationService authenticationService, VisibilityGroup visibilityGroup, UserService userService,
			PlatformConfigurationService platformConfigurationService, TopologyService topologyService)
	{
		this.navigationPanel = navigationPanel;
		this.authenticationService = authenticationService;
		this.visibilityGroup = visibilityGroup;
		this.userService = userService;
		this.errorReportingService = errorReportingService;
		this.errorReportingManagementService = errorReportingManagementService;
		this.platformConfigurationService = platformConfigurationService;
		this.topologyService = topologyService;
	}

	/* (non-Javadoc)
	 * @see com.vaadin.ui.UI#initCloud(com.vaadin.server.VaadinRequest)
	 */
	@Override
	protected void init(VaadinRequest request)
	{		
		String filterName = request.getParameter("filter");	

		Filter filter = this.topologyService.getFilterByName(filterName);
		
		FilterMap filters = new FilterMap();
		filters.addFilter(filter);
		
		VaadinService.getCurrentRequest().getWrappedSession()
        	.setAttribute(DashboardSessionValueConstants.FILTERS, filters);
		
		if((IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
 	        	.getAttribute(DashboardSessionValueConstants.USER) == null)
		{
			final LoginDialog dialog = new LoginDialog(this.authenticationService, visibilityGroup,
					this.navigationPanel, userService);
			
			UI.getCurrent().addWindow(dialog);
			
			dialog.addCloseListener(new Window.CloseListener() 
			{
	            // inline close-listener
	            public void windowClose(CloseEvent e) 
	            {
	            	if((IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
	         	        	.getAttribute(DashboardSessionValueConstants.USER) == null)
	        		{
	            		UI.getCurrent().getNavigator().navigateTo("landingView"); 
	            		
	            		Notification.show("You cannot access this page!", Type.ERROR_MESSAGE);
	        		}
	            }
	        });
		}
		
		
		ErrorOccurrenceTab tab = new ErrorOccurrenceTab(errorReportingService, null, errorReportingManagementService,
				platformConfigurationService);
		tab.createLayout();
		tab.applyFilter();
		tab.search();
		
		GridLayout layout = new GridLayout();
		layout.setMargin(true);
		layout.setSizeFull();
		layout.addComponent(tab);
		
		this.setContent(layout);
	}	
	
}
