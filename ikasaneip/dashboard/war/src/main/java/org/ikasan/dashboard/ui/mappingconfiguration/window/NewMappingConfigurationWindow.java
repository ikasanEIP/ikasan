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
package org.ikasan.dashboard.ui.mappingconfiguration.window;

 import com.vaadin.navigator.Navigator;
 import com.vaadin.navigator.View;
 import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
 import com.vaadin.ui.*;
 import com.vaadin.ui.Button.ClickEvent;
 import com.vaadin.ui.themes.ValoTheme;
 import org.ikasan.dashboard.ui.framework.display.IkasanUIView;
 import org.ikasan.dashboard.ui.framework.navigation.IkasanUINavigator;
 import org.ikasan.dashboard.ui.mappingconfiguration.panel.*;
 import org.ikasan.mapping.model.MappingConfiguration;
 import org.ikasan.mapping.model.ParameterName;
 import org.ikasan.mapping.service.MappingManagementService;
 import org.ikasan.systemevent.service.SystemEventService;

 import java.util.ArrayList;
 import java.util.LinkedList;
 import java.util.List;

 /**
  * @author Ikasan Development Team
  *
  */
 public class NewMappingConfigurationWindow extends Window implements View
 {
     private static final long serialVersionUID = -5772122320534411604L;

     private HorizontalLayout viewPort;

     private MappingManagementService mappingConfigurationService;
     private SystemEventService systemEventService;
     private NewMappingConfigurationDetailsPanel newMappingConfigurationDetailsPanel;
     private NewMappingConfigurationTypePanel newMappingConfigurationTypePanel;
     private NewMappingConfigurationManyToManyNumParamsPanel newMappingConfigurationManyToManyNumParamsPanel;
     private NewMappingConfigurationManyToManyTargetParamNamesPanel newMappingConfigurationManyToManyTargetParamNamesPanel;
     private NewMappingConfigurationSummaryPanel existingMappingConfiguratioSummaryPanel;
     private NewMappingConfigurationManyToManySourceParamNamesPanel newMappingConfigurationSourceParamNamesPanel;
     private NewMappingConfigurationManyToManyNameParamsPanel newMappingConfigurationManyToManyNameParamsPanel;
     private NewMappingConfigurationManyToOneNumParamsPanel newMappingConfigurationManyToOneNumParamsPanel;
     private NewMappingConfigurationManyToOneSourceParamNamesPanel newMappingConfigurationManyToOneSourceParamNamesPanel;
     private NewMappingConfigurationManyToOneNameParamsPanel newMappingConfigurationManyToOneNameParamsPanel;
     private ExistingMappingConfigurationPanel newMappingConfigurationPanel;

     private MappingConfiguration mappingConfiguration;

     private LinkedList<Panel> previousSteps = new LinkedList<Panel>();
     private Panel currentPanel;

     private List<ParameterName> parameterNames = null;

     private IkasanUINavigator mappingNavigator;


     public NewMappingConfigurationWindow(MappingManagementService mappingConfigurationService,
                                          SystemEventService systemEventService, ExistingMappingConfigurationPanel existingMappingConfigurationPanel,
                                          IkasanUINavigator mappingNavigator)
     {
         super("New Mapping Configuration");
         this.mappingConfigurationService = mappingConfigurationService;
         this.systemEventService = systemEventService;

         this.mappingConfiguration = new MappingConfiguration();
         this.mappingConfiguration.setNumTargetValues(1);
         this.mappingConfiguration.setNumberOfParams(1);

         this.newMappingConfigurationDetailsPanel = new NewMappingConfigurationDetailsPanel(mappingConfigurationService, mappingConfiguration);
         this.newMappingConfigurationTypePanel = new NewMappingConfigurationTypePanel();
         this.newMappingConfigurationManyToManyNumParamsPanel = new NewMappingConfigurationManyToManyNumParamsPanel(this.mappingConfiguration);
         this.newMappingConfigurationManyToManyTargetParamNamesPanel = new NewMappingConfigurationManyToManyTargetParamNamesPanel(this.mappingConfiguration);
         this.existingMappingConfiguratioSummaryPanel = new NewMappingConfigurationSummaryPanel(this.mappingConfiguration);
         this.newMappingConfigurationSourceParamNamesPanel = new NewMappingConfigurationManyToManySourceParamNamesPanel();
         this.newMappingConfigurationManyToManyNameParamsPanel = new NewMappingConfigurationManyToManyNameParamsPanel();
         this.newMappingConfigurationManyToOneNumParamsPanel = new NewMappingConfigurationManyToOneNumParamsPanel(this.mappingConfiguration);
         this.newMappingConfigurationManyToOneSourceParamNamesPanel = new NewMappingConfigurationManyToOneSourceParamNamesPanel();
         this.newMappingConfigurationManyToOneNameParamsPanel = new NewMappingConfigurationManyToOneNameParamsPanel();

         this.newMappingConfigurationPanel = existingMappingConfigurationPanel;

         this.mappingNavigator = mappingNavigator;

         this.viewPort = new HorizontalLayout();
         this.viewPort.setWidth("100%");
         this.viewPort.setHeight("550px");

         init();
     }

     /**
      * Helper method to initialise this object.
      */
     protected void init()
     {
         this.setStyleName("dashboard");
         this.setModal(true);
         this.setWidth(600, Unit.PIXELS);
         this.setHeight(700, Unit.PIXELS);

         final Button nextButton = new Button("Next");
         nextButton.setDescription("Next step");
         nextButton.addStyleName(ValoTheme.BUTTON_SMALL);

         final Button previousButton = new Button("Previous");
         previousButton.setDescription("Previous step");
         previousButton.addStyleName(ValoTheme.BUTTON_SMALL);
         previousButton.setEnabled(false);

         final Button createButton = new Button("Create");
         createButton.setDescription("Create new mapping configuration");
         createButton.addStyleName(ValoTheme.BUTTON_SMALL);
         createButton.setEnabled(true);

         viewPort.addComponent(this.newMappingConfigurationDetailsPanel);
         this.currentPanel = this.newMappingConfigurationDetailsPanel;

         final HorizontalLayout buttonsLayout = new HorizontalLayout();
         buttonsLayout.setWidth("200px");
         buttonsLayout.setSpacing(true);
         buttonsLayout.setMargin(true);
         buttonsLayout.addComponent(previousButton);
         buttonsLayout.setComponentAlignment(previousButton, Alignment.MIDDLE_LEFT);
         buttonsLayout.addComponent(nextButton);
         buttonsLayout.setComponentAlignment(nextButton, Alignment.MIDDLE_RIGHT);

         VerticalLayout wrapperLayout = new VerticalLayout();
         wrapperLayout.setSizeFull();
         wrapperLayout.addComponent(viewPort);
         wrapperLayout.setComponentAlignment(viewPort, Alignment.TOP_CENTER);
         wrapperLayout.addComponent(buttonsLayout);
         wrapperLayout.setComponentAlignment(buttonsLayout, Alignment.BOTTOM_CENTER);

         nextButton.addClickListener(new Button.ClickListener()
         {
             public void buttonClick(ClickEvent event)
             {
                 setNextPanel();

                 if(!previousSteps.isEmpty())
                 {
                     previousButton.setEnabled(true);
                 }

                 if(currentPanel.equals(existingMappingConfiguratioSummaryPanel))
                 {
                     buttonsLayout.removeAllComponents();

                     buttonsLayout.addComponent(previousButton);
                     buttonsLayout.setComponentAlignment(previousButton, Alignment.MIDDLE_LEFT);
                     buttonsLayout.addComponent(createButton);
                     buttonsLayout.setComponentAlignment(createButton, Alignment.MIDDLE_RIGHT);
                 }
             }
         });

         previousButton.addClickListener(new Button.ClickListener()
         {
             public void buttonClick(ClickEvent event)
             {

                 if(currentPanel.equals(existingMappingConfiguratioSummaryPanel))
                 {
                     buttonsLayout.removeAllComponents();

                     buttonsLayout.addComponent(previousButton);
                     buttonsLayout.setComponentAlignment(previousButton, Alignment.MIDDLE_LEFT);
                     buttonsLayout.addComponent(nextButton);
                     buttonsLayout.setComponentAlignment(nextButton, Alignment.MIDDLE_RIGHT);
                 }

                 setPreviousPanel();

                 if(previousSteps.isEmpty())
                 {
                     previousButton.setEnabled(false);
                 }
             }
         });

         createButton.addClickListener(new Button.ClickListener()
         {
             public void buttonClick(ClickEvent event)
             {
                 try
                 {
                     mappingConfigurationService.addMappingConfiguration(mappingConfiguration, parameterNames);

                     Navigator navigator = new Navigator(UI.getCurrent(), mappingNavigator.getParentContainer());

                     for (IkasanUIView view : mappingNavigator.getIkasanViews())
                     {
                         navigator.addView(view.getPath(), view.getView());
                     }

                     UI.getCurrent().getNavigator().navigateTo("existingMappingConfigurationPanel");

                     newMappingConfigurationPanel.setMappingConfiguration(mappingConfiguration);
                     newMappingConfigurationPanel.populateMappingConfigurationForm();


                     close();
                 }
                 catch(Exception e)
                 {
                     e.printStackTrace();
                     Notification.show("Error creating Mapping Configuration: " + e.getMessage(), Notification.Type.ERROR_MESSAGE);
                 }
             }
         });

         this.setContent(wrapperLayout);
     }

     private void setNextPanel()
     {
         if(this.currentPanel.equals(this.newMappingConfigurationDetailsPanel))
         {
             if(!this.newMappingConfigurationDetailsPanel.isValid())
             {
                 // not ready to move on yet.
                 return;
             }

             previousSteps.addLast(this.currentPanel);
             this.currentPanel = this.newMappingConfigurationTypePanel;
         }
         else if(this.currentPanel.equals(this.newMappingConfigurationTypePanel))
         {
             previousSteps.addLast(this.currentPanel);

             if(this.newMappingConfigurationTypePanel.getType() == NewMappingConfigurationTypePanel.TYPE.ONE_TO_ONE)
             {
                 mappingConfiguration.setIsManyToMany(false);
                 this.currentPanel = this.existingMappingConfiguratioSummaryPanel;
                 mappingConfiguration.setConstrainParameterListSizes(true);

                 this.existingMappingConfiguratioSummaryPanel.enter(null, null);
             }
             else if(this.newMappingConfigurationTypePanel.getType() == NewMappingConfigurationTypePanel.TYPE.MANY_TO_ONE)
             {
                 mappingConfiguration.setIsManyToMany(false);
                 mappingConfiguration.setConstrainParameterListSizes(true);
                 this.currentPanel = this.newMappingConfigurationManyToOneNumParamsPanel;
             }
             else
             {
                 mappingConfiguration.setIsManyToMany(true);
                 this.currentPanel = this.newMappingConfigurationManyToManyNumParamsPanel;
             }
         }
         else if(this.currentPanel.equals(this.newMappingConfigurationManyToManyNumParamsPanel))
         {
             if(!this.newMappingConfigurationManyToManyNumParamsPanel.isValid())
             {
                 // not ready to move on yet.
                 return;
             }

             previousSteps.addLast(this.currentPanel);

             if(this.newMappingConfigurationManyToManyNumParamsPanel.getAnswer() == NewMappingConfigurationManyToManyNumParamsPanel.ANSWER.YES)
             {
                 mappingConfiguration.setConstrainParameterListSizes(true);
                 this.currentPanel = this.newMappingConfigurationManyToManyNameParamsPanel;
             }
             else
             {
                 mappingConfiguration.setConstrainParameterListSizes(false);
                 this.currentPanel = this.existingMappingConfiguratioSummaryPanel;

                 this.existingMappingConfiguratioSummaryPanel.enter(null, null);
             }
         }
         else if(this.currentPanel.equals(this.newMappingConfigurationManyToOneNumParamsPanel))
         {
             if(!this.newMappingConfigurationManyToOneNumParamsPanel.isValid())
             {
                 // not ready to move on yet.
                 return;
             }

             previousSteps.addLast(this.currentPanel);
             this.currentPanel = this.newMappingConfigurationManyToOneNameParamsPanel;
         }
         else if(this.currentPanel.equals(this.newMappingConfigurationManyToOneNameParamsPanel))
         {
             previousSteps.addLast(this.currentPanel);

             if(this.newMappingConfigurationManyToOneNameParamsPanel.getAnswer() == NewMappingConfigurationManyToOneNameParamsPanel.ANSWER.YES)
             {
                 newMappingConfigurationManyToOneSourceParamNamesPanel.enter
                         (this.newMappingConfigurationManyToOneNumParamsPanel.getNumberSourceValues());

                 this.currentPanel = this.newMappingConfigurationManyToOneSourceParamNamesPanel;
             }
             else
             {
                 this.currentPanel = this.existingMappingConfiguratioSummaryPanel;

                 this.existingMappingConfiguratioSummaryPanel.enter(null, null);
             }

         }
         else if(this.currentPanel.equals(this.newMappingConfigurationManyToOneSourceParamNamesPanel))
         {
             if(!this.newMappingConfigurationManyToOneSourceParamNamesPanel.isValid())
             {
                 // not ready to move on yet.
                 return;
             }

             previousSteps.addLast(this.currentPanel);
             this.currentPanel = this.existingMappingConfiguratioSummaryPanel;

             this.existingMappingConfiguratioSummaryPanel.enter(this.newMappingConfigurationManyToOneSourceParamNamesPanel.getParameterNames(), null);

             parameterNames = new ArrayList<ParameterName>();
             if(this.newMappingConfigurationManyToOneSourceParamNamesPanel.getParameterNames() != null)
             {
                 parameterNames.addAll(this.newMappingConfigurationManyToOneSourceParamNamesPanel.getParameterNames());
             }
         }
         else if(this.currentPanel.equals(this.newMappingConfigurationManyToManyNameParamsPanel))
         {
             previousSteps.addLast(this.currentPanel);

             if(this.newMappingConfigurationManyToManyNameParamsPanel.getAnswer() == NewMappingConfigurationManyToManyNameParamsPanel.ANSWER.YES)
             {
                 newMappingConfigurationSourceParamNamesPanel.enter
                         (this.newMappingConfigurationManyToManyNumParamsPanel.getNumberSourceValues());

                 this.currentPanel = this.newMappingConfigurationSourceParamNamesPanel;
             }
             else
             {
                 this.currentPanel = this.existingMappingConfiguratioSummaryPanel;

                 this.existingMappingConfiguratioSummaryPanel.enter(null, null);
             }

         }
         else if(this.currentPanel.equals(this.newMappingConfigurationSourceParamNamesPanel))
         {
             if(!this.newMappingConfigurationSourceParamNamesPanel.isValid())
             {
                 // not ready to move on yet.
                 return;
             }

             previousSteps.addLast(this.currentPanel);

             this.newMappingConfigurationManyToManyTargetParamNamesPanel.enter
                     (newMappingConfigurationManyToManyNumParamsPanel.getNumberTargetValues());

             this.currentPanel = this.newMappingConfigurationManyToManyTargetParamNamesPanel;
         }
         else if(this.currentPanel.equals(this.newMappingConfigurationManyToManyTargetParamNamesPanel))
         {
             if(!this.newMappingConfigurationManyToManyTargetParamNamesPanel.isValid())
             {
                 // not ready to move on yet.
                 return;
             }

             previousSteps.addLast(this.currentPanel);
             this.currentPanel = this.existingMappingConfiguratioSummaryPanel;

             this.existingMappingConfiguratioSummaryPanel.enter(this.newMappingConfigurationSourceParamNamesPanel.getParameterNames(),
                     this.newMappingConfigurationManyToManyTargetParamNamesPanel.getParameterNames());

             parameterNames = new ArrayList<ParameterName>();
             if(this.newMappingConfigurationSourceParamNamesPanel.getParameterNames() != null)
             {
                 parameterNames.addAll(this.newMappingConfigurationSourceParamNamesPanel.getParameterNames());
             }

             if(this.newMappingConfigurationManyToManyTargetParamNamesPanel.getParameterNames() != null)
             {
                 parameterNames.addAll(this.newMappingConfigurationManyToManyTargetParamNamesPanel.getParameterNames());
             }
         }
         this.viewPort.removeAllComponents();
         this.viewPort.addComponent(this.currentPanel);
     }

     private void setPreviousPanel()
     {
         this.currentPanel = this.previousSteps.getLast();
         this.previousSteps.removeLast();

         this.viewPort.removeAllComponents();
         this.viewPort.addComponent(this.currentPanel);
     }

     /* (non-Javadoc)
      * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
      */
     @Override
     public void enter(ViewChangeEvent event)
     {

     }
 }
