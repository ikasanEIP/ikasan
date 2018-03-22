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

 import com.vaadin.navigator.View;
 import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
 import com.vaadin.server.VaadinService;
 import com.vaadin.ui.*;
 import com.vaadin.ui.themes.ValoTheme;
 import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 import org.ikasan.dashboard.solr.SolrInitialiser;
 import org.ikasan.dashboard.ui.framework.util.DashboardSessionValueConstants;
 import org.ikasan.security.service.authentication.IkasanAuthentication;
 import org.ikasan.spec.configuration.Configuration;
 import org.ikasan.spec.configuration.ConfigurationManagement;
 import org.ikasan.spec.configuration.ConfiguredResource;
 import org.ikasan.spec.configuration.PlatformConfigurationService;

 /**
  * @author CMI2 Development Team
  *
  */
 public class PlatformConfigurationTabPanel extends Panel implements View
 {
     private static final long serialVersionUID = 6005593259860222561L;

     private Logger logger = LoggerFactory.getLogger(PlatformConfigurationTabPanel.class);

     private PlatformConfigurationService platformConfigurationService;

     private ConfigurationManagement<ConfiguredResource, Configuration> configurationManagement;

     private GeneralConfigurationPanel platformConfigurationGeneralTab;
     private ControlConfigurationPanel controlConfigurationPanel;
     private SolrConfigurationPanel solrConfigurationPanel;
     private RawConfigurationPanel rawConfigurationPanel;
     private SolrInitialiser solrInitialiser;

     private Panel tabsheetPanel;
     private TabSheet tabsheet;

     /**
      * Constructor
      *
      * @param configurationManagement
      * @param platformConfigurationService
      * @param solrInitialiser
      */
     public PlatformConfigurationTabPanel(ConfigurationManagement<ConfiguredResource, Configuration> configurationManagement,
                                          PlatformConfigurationService platformConfigurationService, SolrInitialiser solrInitialiser)
     {
         super();
         this.configurationManagement = configurationManagement;
         if (this.configurationManagement == null)
         {
             throw new IllegalArgumentException("configurationService cannot be null!");
         }
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
         this.tabsheetPanel = new Panel();
         this.tabsheetPanel.addStyleName(ValoTheme.PANEL_BORDERLESS);
         this.tabsheetPanel.setSizeFull();
         super.setSizeFull();
     }

     protected void createTabSheet()
     {
         tabsheet = new TabSheet();
         tabsheet.setSizeFull();

         final IkasanAuthentication authentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
                 .getAttribute(DashboardSessionValueConstants.USER);

         this.platformConfigurationGeneralTab = new GeneralConfigurationPanel
                     (this.platformConfigurationService);

         tabsheet.addTab(this.platformConfigurationGeneralTab, "General");

         this.controlConfigurationPanel = new ControlConfigurationPanel
                 (this.platformConfigurationService);

         tabsheet.addTab(this.controlConfigurationPanel, "Control");

         this.solrConfigurationPanel = new SolrConfigurationPanel
             (this.platformConfigurationService, this.solrInitialiser);

         tabsheet.addTab(this.solrConfigurationPanel, "Solr");

         this.rawConfigurationPanel = new RawConfigurationPanel
                 (this.configurationManagement);

         tabsheet.addTab(this.rawConfigurationPanel, "Raw");

         tabsheet.addSelectedTabChangeListener(new TabSheet.SelectedTabChangeListener()
         {
             @Override
             public void selectedTabChange(TabSheet.SelectedTabChangeEvent selectedTabChangeEvent)
             {
                 platformConfigurationGeneralTab.refresh();
                 controlConfigurationPanel.refresh();
                 solrConfigurationPanel.refresh();
                 rawConfigurationPanel.refresh();
             }
         });

         this.tabsheetPanel.setContent(tabsheet);

         HorizontalLayout layout = new HorizontalLayout();
         layout.setSizeFull();
         layout.setMargin(true);
         layout.addComponent(this.tabsheet);

         Label configLabel = new Label("Platform Configuration");
         configLabel.addStyleName(ValoTheme.LABEL_HUGE);
         configLabel.setSizeUndefined();

         HorizontalLayout labelLayout = new HorizontalLayout();
         labelLayout.setSizeFull();
         labelLayout.setMargin(true);
         labelLayout.addComponent(configLabel);
         labelLayout.setComponentAlignment(configLabel, Alignment.TOP_LEFT);

         VerticalSplitPanel verticalSplitPanel = new VerticalSplitPanel();
         verticalSplitPanel.setSplitPosition(50, Unit.PIXELS);
         verticalSplitPanel.setLocked(true);

         verticalSplitPanel.setFirstComponent(labelLayout);
         verticalSplitPanel.setSecondComponent(layout);

         super.setContent(verticalSplitPanel);
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
         this.rawConfigurationPanel.enter(event);
         this.platformConfigurationGeneralTab.enter(event);
         this.controlConfigurationPanel.enter(event);
         this.solrConfigurationPanel.enter(event);
     }

     private void refresh()
     {
         if(this.tabsheet == null)
         {
             logger.debug("createTabSheet!");
             this.createTabSheet();
         }

     }

     private class TextFieldKeyValuePair
     {

     }
 }
