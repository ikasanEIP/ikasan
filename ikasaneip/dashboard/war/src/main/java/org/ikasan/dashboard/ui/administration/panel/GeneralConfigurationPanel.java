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
package org.ikasan.dashboard.ui.administration.panel;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.vaadin.server.VaadinService;
import org.apache.log4j.Logger;
import org.ikasan.configurationService.model.ConfigurationParameterIntegerImpl;
import org.ikasan.configurationService.model.ConfigurationParameterLongImpl;
import org.ikasan.configurationService.model.ConfigurationParameterMapImpl;
import org.ikasan.configurationService.model.PlatformConfiguration;
import org.ikasan.configurationService.model.PlatformConfigurationConfiguredResource;
import org.ikasan.dashboard.ui.framework.constants.SecurityConstants;
import org.ikasan.dashboard.ui.framework.util.DashboardSessionValueConstants;
import org.ikasan.dashboard.ui.framework.validator.NonZeroLengthStringValidator;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.spec.configuration.Configuration;
import org.ikasan.spec.configuration.ConfigurationManagement;
import org.ikasan.spec.configuration.ConfigurationParameter;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.vaadin.teemu.VaadinIcons;

import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.converter.StringToIntegerConverter;
import com.vaadin.data.util.converter.StringToLongConverter;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

/**
 * @author CMI2 Development Team
 * 
 */
public class GeneralConfigurationPanel extends Panel implements View
{
	private static final long serialVersionUID = 6005593259860222561L;

	private Logger logger = Logger.getLogger(GeneralConfigurationPanel.class);
	
	private ConfigurationManagement<ConfiguredResource, Configuration> configurationManagement;
	private PlatformConfigurationConfiguredResource platformConfigurationConfiguredResource;
	private Configuration platformConfiguration;
	

	/**
	 * Constructor
	 *
	 * @param configurationManagement
     */
	public GeneralConfigurationPanel(ConfigurationManagement<ConfiguredResource, Configuration> configurationManagement)
	{
		super();
		this.configurationManagement = configurationManagement;
		if (this.configurationManagement == null)
		{
			throw new IllegalArgumentException("configurationService cannot be null!");
		}
		
		
		init();
	}


	protected void init()
	{
		
	}

	protected Panel createConfigurationPanel()
	{
		Panel paramPanel = new Panel();
		paramPanel.addStyleName(ValoTheme.PANEL_BORDERLESS);
		paramPanel.setWidth("100%");

		GridLayout paramLayout = new GridLayout(2, 3);
		paramLayout.setSpacing(true);
		paramLayout.setSizeFull();
		paramLayout.setMargin(true);
		paramLayout.setColumnExpandRatio(0, .25f);
		paramLayout.setColumnExpandRatio(1, .75f);

		Label label = new Label("Replay Target Servers (comma separated)");
		label.addStyleName(ValoTheme.LABEL_LARGE);
		label.addStyleName(ValoTheme.LABEL_BOLD);
		label.setSizeUndefined();
		paramLayout.addComponent(label, 0, 0);
		paramLayout.setComponentAlignment(label, Alignment.MIDDLE_RIGHT);

		TextField dashboardBaseUrl = new TextField();
		dashboardBaseUrl.setWidth(300, Unit.PIXELS);

		paramLayout.addComponent(dashboardBaseUrl, 1, 0);
		paramLayout.setComponentAlignment(dashboardBaseUrl, Alignment.MIDDLE_LEFT);

		label = new Label("Search result set sizes");
		label.addStyleName(ValoTheme.LABEL_LARGE);
		label.addStyleName(ValoTheme.LABEL_BOLD);
		label.setSizeUndefined();
		paramLayout.addComponent(label, 0, 1);
		paramLayout.setComponentAlignment(label, Alignment.MIDDLE_RIGHT);

		TextField webServiceUsername = new TextField();
		webServiceUsername.setWidth(300, Unit.PIXELS);
		webServiceUsername.setWidth(300, Unit.PIXELS);

		paramLayout.addComponent(webServiceUsername, 1, 1);
		paramLayout.setComponentAlignment(webServiceUsername, Alignment.MIDDLE_LEFT);

		label = new Label("Notification interval minutes");
		label.addStyleName(ValoTheme.LABEL_LARGE);
		label.addStyleName(ValoTheme.LABEL_BOLD);
		label.setSizeUndefined();
		paramLayout.addComponent(label, 0, 2);
		paramLayout.setComponentAlignment(label, Alignment.MIDDLE_RIGHT);

		TextField webServiceUserPassword = new TextField();
		webServiceUserPassword.setWidth(300, Unit.PIXELS);

		paramLayout.addComponent(webServiceUserPassword, 1, 2);
		paramLayout.setComponentAlignment(webServiceUserPassword, Alignment.MIDDLE_LEFT);

		paramPanel.setContent(paramLayout);

		return paramPanel;
	}


	/*
     * (non-Javadoc)
     *
     * @see
     * com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener
     * .ViewChangeEvent)
     */
	@Override
	public void enter(ViewChangeEvent event)
	{
		refresh();
	}

	private void refresh()
	{
		this.platformConfigurationConfiguredResource = new PlatformConfigurationConfiguredResource();

		this.platformConfiguration = this.configurationManagement.getConfiguration(this.platformConfigurationConfiguredResource);

		// create the configuration if it does not already exist!
		if(platformConfiguration == null)
		{
			this.platformConfigurationConfiguredResource.setConfiguration(new PlatformConfiguration());
			platformConfiguration = this.configurationManagement.createConfiguration(platformConfigurationConfiguredResource);
		}

		final List<ConfigurationParameter> parameters = (List<ConfigurationParameter>)platformConfiguration.getParameters();

		GridLayout layout = new GridLayout();
		layout.setWidth("100%");
		layout.setSpacing(true);
		layout.setMargin(true);


		Label configLabel = new Label("Solr Configuration");
		configLabel.addStyleName(ValoTheme.LABEL_HUGE);
		configLabel.setSizeUndefined();

		layout.addComponent(configLabel);
		layout.addComponent(this.createConfigurationPanel());

		this.setContent(layout);
	}
}
