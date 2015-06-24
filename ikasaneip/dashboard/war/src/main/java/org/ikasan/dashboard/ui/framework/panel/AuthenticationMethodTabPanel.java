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
package org.ikasan.dashboard.ui.framework.panel;

import java.util.List;

import org.apache.log4j.Logger;
import org.ikasan.dashboard.ui.framework.util.DashboardSessionValueConstants;
import org.ikasan.security.model.AuthenticationMethod;
import org.ikasan.security.service.LdapService;
import org.ikasan.security.service.SecurityService;
import org.ikasan.security.service.authentication.AuthenticationProviderFactory;
import org.ikasan.security.service.authentication.IkasanAuthentication;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.VerticalLayout;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class AuthenticationMethodTabPanel extends Panel implements View
{
	private Logger logger = Logger.getLogger(AuthenticationMethodTabPanel.class);
	
	private SecurityService securityService;
    private AuthenticationProviderFactory<AuthenticationMethod> authenticationProviderFactory;
    private LdapService ldapService;
    private TabSheet tabsheet = new TabSheet();
    private SelectedTabChangeListener listener;
    private VerticalLayout mainLayout = new VerticalLayout();
	
	/**
     * Constructor
     * 
     * @param ikasanModuleService
     */
    public AuthenticationMethodTabPanel(SecurityService securityService,
    		AuthenticationProviderFactory<AuthenticationMethod> authenticationProviderFactory,
    		LdapService ldapService)
    {
        super();
        this.securityService = securityService;
        if(this.securityService == null)
        {
        	throw new IllegalArgumentException("securityService cannot be null!");
        }
        this.authenticationProviderFactory = authenticationProviderFactory;
        if(this.authenticationProviderFactory == null)
        {
        	throw new IllegalArgumentException("authenticationProviderFactory cannot be null!");
        }
        this.ldapService = ldapService;
        if(this.ldapService == null)
        {
        	throw new IllegalArgumentException("ldapService cannot be null!");
        }
        init();        
    }

    protected void init()
    {
    	this.listener = new TabSheet.SelectedTabChangeListener() 
		{
		    public void selectedTabChange(SelectedTabChangeEvent event) 
		    {
		        // Find the tabsheet
		        TabSheet tabsheet = event.getTabSheet();
		        
		        if(tabsheet.getTab(tabsheet.getSelectedTab()).getCaption().equals("+"))
		        {
		        	AuthenticationMethodPanel authMethodPanel = new AuthenticationMethodPanel(new AuthenticationMethod(), 
							securityService, authenticationProviderFactory, ldapService);
		        	
		        	tabsheet.removeTab(tabsheet.getTab(tabsheet.getSelectedTab()));
		        	
		        	tabsheet.addTab(authMethodPanel, "Authentication Configuration");
		        	logger.info("selectedTabChange adding new tab: " + tabsheet.getComponentCount());
		        	tabsheet.setSelectedTab(authMethodPanel);	
		        	
		        	VerticalLayout newTab = new VerticalLayout();
					newTab.setSizeFull();
					tabsheet.addTab(newTab, "+");
					
					logger.info("selectedTabChange adding new + tab: " + tabsheet.getComponentCount());
		        }
		    }
		};
		
		this.tabsheet.addSelectedTabChangeListener(listener);
    	
        this.setWidth("100%");
        this.setHeight("100%");
        
        this.mainLayout.setMargin(true);
        
        this.setContent(this.mainLayout);
    }

	/* (non-Javadoc)
	 * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
	 */
	@Override
	public void enter(ViewChangeEvent event)
	{
		this.tabsheet.removeSelectedTabChangeListener(listener);
		
		for(int i=0; i<this.tabsheet.getComponentCount(); i++)
		{
			Tab tab = this.tabsheet.getTab(i);
			this.tabsheet.removeTab(tab);
		}
		
		this.tabsheet.removeAllComponents();
		
		logger.info("Event method removing all components: " + tabsheet.getComponentCount());
		
		tabsheet = new TabSheet();
		
		Panel securityAdministrationPanel = new Panel("Security Administration");
        securityAdministrationPanel.setStyleName("dashboard");
        securityAdministrationPanel.setWidth("100%");
        securityAdministrationPanel.setHeight("100%");
        
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        layout.addComponent(securityAdministrationPanel);
        
        
		this.tabsheet.setSizeFull();
		
		securityAdministrationPanel.setContent(tabsheet);
		this.mainLayout.removeAllComponents();
		this.mainLayout.addComponent(securityAdministrationPanel);
		
		logger.info("Event method removing all components: " + tabsheet.getComponentCount());
		
		final IkasanAuthentication authentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
	        	.getAttribute(DashboardSessionValueConstants.USER);

		List<AuthenticationMethod> authenticationMethods = this.securityService.getAuthenticationMethods();
		
		for(AuthenticationMethod authenticationMethod: authenticationMethods)
		{
			AuthenticationMethodPanel authMethodPanel = new AuthenticationMethodPanel(authenticationMethod, 
					this.securityService, this.authenticationProviderFactory, this.ldapService);
			
			VerticalLayout tab = new VerticalLayout();
			tab.setSizeFull();
			tab.addComponent(authMethodPanel);
			tabsheet.addTab(tab, "Authentication Configuration");
			
			logger.info("Event method adding new tab: " + tab);
			
			authMethodPanel.enter(event);
		}  
	
		Tab tab = tabsheet.getTab(tabsheet.getComponentCount() - 1);
		
		if(!tab.getCaption().equals("+"))
		{
			logger.info("Event method adding new + tab: " + tab);
			VerticalLayout newTab = new VerticalLayout();
			newTab.setSizeFull();
			newTab.addComponent(new Panel());
			tabsheet.addTab(newTab, "+");
		}
		
		this.tabsheet.addSelectedTabChangeListener(listener);
	}
}
