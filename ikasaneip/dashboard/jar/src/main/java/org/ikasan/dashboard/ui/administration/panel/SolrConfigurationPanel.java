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

 import com.vaadin.data.Validator;
 import com.vaadin.data.Validator.InvalidValueException;
 import com.vaadin.data.util.BeanItem;
 import com.vaadin.data.util.converter.StringToIntegerConverter;
 import com.vaadin.data.util.converter.StringToLongConverter;
 import com.vaadin.navigator.View;
 import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
 import com.vaadin.server.Page;
 import com.vaadin.server.VaadinService;
 import com.vaadin.ui.*;
 import com.vaadin.ui.Button.ClickEvent;
 import com.vaadin.ui.Notification.Type;
 import com.vaadin.ui.themes.ValoTheme;
 import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 import org.ikasan.configurationService.model.*;
 import org.ikasan.dashboard.solr.SolrInitialiser;
 import org.ikasan.dashboard.ui.framework.constants.ConfigurationConstants;
 import org.ikasan.dashboard.ui.framework.constants.SecurityConstants;
 import org.ikasan.dashboard.ui.framework.util.DashboardSessionValueConstants;
 import org.ikasan.dashboard.ui.framework.validator.NonZeroLengthStringValidator;
 import org.ikasan.security.service.authentication.IkasanAuthentication;
 import org.ikasan.spec.configuration.*;
 import org.vaadin.teemu.VaadinIcons;

 import java.text.NumberFormat;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Locale;
 import java.util.Map;

 /**
  * @author CMI2 Development Team
  *
  */
 public class SolrConfigurationPanel extends Panel implements View
 {
     private static final long serialVersionUID = 6005593259860222561L;

     private Logger logger = LoggerFactory.getLogger(SolrConfigurationPanel.class);

     private PlatformConfigurationService platformConfigurationService;
     private SolrInitialiser solrInitialiser;

     /**
      * Constructor
      *
      * @param platformConfigurationService
      * @param solrInitialiser
      */
     public SolrConfigurationPanel(PlatformConfigurationService platformConfigurationService,
                                   SolrInitialiser solrInitialiser)
     {
         super();
         this.platformConfigurationService = platformConfigurationService;
         if (this.platformConfigurationService == null)
         {
             throw new IllegalArgumentException("platformConfigurationService cannot be null!");
         }
         this.solrInitialiser = solrInitialiser;
         if (this.solrInitialiser == null)
         {
             throw new IllegalArgumentException("solrInitialiser cannot be null!");
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

         GridLayout paramLayout = new GridLayout(2, 7);
         paramLayout.setSpacing(true);
         paramLayout.setSizeFull();
         paramLayout.setMargin(true);
         paramLayout.setColumnExpandRatio(0, .25f);
         paramLayout.setColumnExpandRatio(1, .75f);

         Label label = new Label("Is solr enabled?");
         label.addStyleName(ValoTheme.LABEL_LARGE);
         label.addStyleName(ValoTheme.LABEL_BOLD);
         label.setSizeUndefined();
         paramLayout.addComponent(label, 0, 0);
         paramLayout.setComponentAlignment(label, Alignment.MIDDLE_RIGHT);

         CheckBox solrEnabledCheckbox = new CheckBox();

         String soleEnabled = this.platformConfigurationService.getConfigurationValue(ConfigurationConstants.SOLR_ENABLED);

         if(soleEnabled != null && soleEnabled.equals("true"))
         {
             solrEnabledCheckbox.setValue(true);
         }
         else
         {
             solrEnabledCheckbox.setValue(false);
         }


         paramLayout.addComponent(solrEnabledCheckbox, 1, 0);
         paramLayout.setComponentAlignment(solrEnabledCheckbox, Alignment.MIDDLE_LEFT);

         label = new Label("Solr URLs (comma separated)");
         label.addStyleName(ValoTheme.LABEL_LARGE);
         label.addStyleName(ValoTheme.LABEL_BOLD);
         label.setSizeUndefined();
         paramLayout.addComponent(label, 0, 1);
         paramLayout.setComponentAlignment(label, Alignment.MIDDLE_RIGHT);

         TextArea solrUrlsTextField = new TextArea();
         solrUrlsTextField.setWidth(500, Unit.PIXELS);
         solrUrlsTextField.setRows(4);
         solrUrlsTextField.setValue(this.platformConfigurationService.getConfigurationValue(ConfigurationConstants.SOLR_URLS));

         paramLayout.addComponent(solrUrlsTextField, 1, 1);
         paramLayout.setComponentAlignment(solrUrlsTextField, Alignment.MIDDLE_LEFT);

         label = new Label("Days to leave records in solr index");
         label.addStyleName(ValoTheme.LABEL_LARGE);
         label.addStyleName(ValoTheme.LABEL_BOLD);
         label.setSizeUndefined();
         paramLayout.addComponent(label, 0, 2);
         paramLayout.setComponentAlignment(label, Alignment.MIDDLE_RIGHT);

         TextField daysToKeepTextField = new TextField();
         daysToKeepTextField.setValue(this.platformConfigurationService.getConfigurationValue(ConfigurationConstants.SOLR_DAYS_TO_KEEP));

         paramLayout.addComponent(daysToKeepTextField, 1, 2);
         paramLayout.setComponentAlignment(daysToKeepTextField, Alignment.MIDDLE_LEFT);

         label = new Label("Solr Username");
         label.addStyleName(ValoTheme.LABEL_LARGE);
         label.addStyleName(ValoTheme.LABEL_BOLD);
         label.setSizeUndefined();
         paramLayout.addComponent(label, 0, 3);
         paramLayout.setComponentAlignment(label, Alignment.MIDDLE_RIGHT);

         TextField solrUsername = new TextField();
         solrUsername.setValue(this.platformConfigurationService.getSolrUsername());
         solrUsername.setWidth(250, Unit.PIXELS);

         paramLayout.addComponent(solrUsername, 1, 3);
         paramLayout.setComponentAlignment(solrUsername, Alignment.MIDDLE_LEFT);

         label = new Label("Solr Password");
         label.addStyleName(ValoTheme.LABEL_LARGE);
         label.addStyleName(ValoTheme.LABEL_BOLD);
         label.setSizeUndefined();
         paramLayout.addComponent(label, 0, 4);
         paramLayout.setComponentAlignment(label, Alignment.MIDDLE_RIGHT);

         PasswordField solrPassword = new PasswordField();
         solrPassword.setValue(this.platformConfigurationService.getSolrPassword());
         solrPassword.setWidth(250, Unit.PIXELS);

         paramLayout.addComponent(solrPassword, 1, 4);
         paramLayout.setComponentAlignment(solrPassword, Alignment.MIDDLE_LEFT);

         OptionGroup optionGroup = new OptionGroup("Solr Operating Mode");
         optionGroup.addItem("Cloud");
         optionGroup.addItem("Standalone");
         optionGroup.setItemCaption("Cloud", "Cloud");
         optionGroup.setItemCaption("Standalone", "Standalone");

         String operatingMode = platformConfigurationService.getConfigurationValue(ConfigurationConstants.SOLR_OPERATING_MODE);

         if(operatingMode == null || operatingMode.equals("Standalone"))
         {
             optionGroup.setValue("Standalone");
         }
         else
         {
             optionGroup.setValue("Cloud");
         }

         paramLayout.addComponent(optionGroup, 1, 5);

         Button saveButton = new Button("Save");
         saveButton.addStyleName(ValoTheme.BUTTON_SMALL);

         saveButton.addClickListener(new Button.ClickListener()
         {
             @Override
             public void buttonClick(ClickEvent clickEvent)
             {
                 Boolean solrStarted = true;

                 if(solrEnabledCheckbox.getValue() == true)
                 {
                     solrStarted = solrInitialiser.initialiseSolr();

                     solrEnabledCheckbox.setValue(solrStarted);
                 }

                 platformConfigurationService.saveConfigurationValue
                         (ConfigurationConstants.SOLR_ENABLED, solrEnabledCheckbox.getValue().toString());
                 platformConfigurationService.saveConfigurationValue
                         (ConfigurationConstants.SOLR_URLS, solrUrlsTextField.getValue());
                 platformConfigurationService.saveConfigurationValue
                         (ConfigurationConstants.SOLR_DAYS_TO_KEEP, daysToKeepTextField.getValue());
                 platformConfigurationService.saveConfigurationValue
                         (ConfigurationConstants.SOLR_OPERATING_MODE, (String)optionGroup.toString());
                 platformConfigurationService.saveSolrUsername(solrUsername.getValue());
                 platformConfigurationService.saveSolrPassword(solrPassword.getValue());

                 if(!solrStarted)
                 {
                     Notification notification = new Notification(
                             "Saved",
                             "The configuration has been saved successfully! However, Solr does not appear to be running and will not be enabled.",
                             Type.WARNING_MESSAGE);
                     notification.setStyleName(ValoTheme.NOTIFICATION_CLOSABLE);
                     notification.show(Page.getCurrent());
                 }
                 else
                 {
                     Notification notification = new Notification(
                             "Saved",
                             "The configuration has been saved successfully!",
                             Type.HUMANIZED_MESSAGE);
                     notification.setStyleName(ValoTheme.NOTIFICATION_CLOSABLE);
                     notification.show(Page.getCurrent());
                 }
             }
         });

         paramLayout.addComponent(saveButton, 0, 6, 1, 6);
         paramLayout.setComponentAlignment(saveButton, Alignment.MIDDLE_CENTER);

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

     public void refresh()
     {
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
