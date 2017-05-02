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

 import com.vaadin.navigator.View;
 import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
 import com.vaadin.ui.*;
 import com.vaadin.ui.Button.ClickEvent;
 import com.vaadin.ui.themes.ValoTheme;
 import org.ikasan.dashboard.ui.mappingconfiguration.panel.*;
 import org.ikasan.mapping.model.MappingConfiguration;
 import org.ikasan.mapping.service.MappingConfigurationService;
 import org.ikasan.systemevent.service.SystemEventService;

 import java.util.LinkedList;

 /**
  * @author Ikasan Development Team
  *
  */
 public class NewMappingConfigurationWindow extends Window implements View
 {
     private static final long serialVersionUID = -5772122320534411604L;

     private HorizontalLayout viewPort;

     private MappingConfigurationService mappingConfigurationService;
     private SystemEventService systemEventService;
     private NewMappingConfigurationDetailsPanel newMappingConfigurationDetailsPanel;
     private NewMappingConfigurationTypePanel newMappingConfigurationTypePanel;
     private NewMappingConfigurationManyToManyNumParamsPanel newMappingConfigurationManyToManyNumParamsPanel;
     private NewMappingConfigurationManyToManyTargetParamNamesPanel newMappingConfigurationManyToManyTargetParamNamesPanel;
     private NewMappingConfiguratioSummaryPanel newMappingConfiguratioSummaryPanel;
     private NewMappingConfigurationManyToManySourceParamNamesPanel newMappingConfigurationSourceParamNamesPanel;
     private NewMappingConfigurationManyToManyNameParamsPanel newMappingConfigurationManyToManyNameParamsPanel;
     private NewMappingConfigurationManyToOneNumParamsPanel newMappingConfigurationManyToOneNumParamsPanel;
     private NewMappingConfigurationManyToOneSourceParamNamesPanel newMappingConfigurationManyToOneSourceParamNamesPanel;
     private NewMappingConfigurationManyToOneNameParamsPanel newMappingConfigurationManyToOneNameParamsPanel;

     private MappingConfiguration mappingConfiguration;

     private LinkedList<Panel> previousSteps = new LinkedList<Panel>();
     private Panel currentPanel;


     public NewMappingConfigurationWindow(MappingConfigurationService mappingConfigurationService,
                                          SystemEventService systemEventService)
     {
         super("New Mapping Configuration");
         this.mappingConfigurationService = mappingConfigurationService;
         this.systemEventService = systemEventService;

         this.newMappingConfigurationDetailsPanel = new NewMappingConfigurationDetailsPanel(mappingConfigurationService);

         this.mappingConfiguration = new MappingConfiguration();

         this.newMappingConfigurationTypePanel = new NewMappingConfigurationTypePanel();
         this.newMappingConfigurationManyToManyNumParamsPanel = new NewMappingConfigurationManyToManyNumParamsPanel();
         this.newMappingConfigurationManyToManyTargetParamNamesPanel = new NewMappingConfigurationManyToManyTargetParamNamesPanel();
         this.newMappingConfiguratioSummaryPanel = new NewMappingConfiguratioSummaryPanel();
         this.newMappingConfigurationSourceParamNamesPanel = new NewMappingConfigurationManyToManySourceParamNamesPanel();
         this.newMappingConfigurationManyToManyNameParamsPanel = new NewMappingConfigurationManyToManyNameParamsPanel();
         this.newMappingConfigurationManyToOneNumParamsPanel = new NewMappingConfigurationManyToOneNumParamsPanel();
         this.newMappingConfigurationManyToOneSourceParamNamesPanel = new NewMappingConfigurationManyToOneSourceParamNamesPanel();
         this.newMappingConfigurationManyToOneNameParamsPanel = new NewMappingConfigurationManyToOneNameParamsPanel();

         this.viewPort = new HorizontalLayout();
         this.viewPort.setSizeFull();

         init();
     }

     /**
      * Helper method to initialise this object.
      */
     protected void init()
     {
         this.setStyleName("dashboard");
         this.setModal(true);
         this.setWidth(80, Unit.PERCENTAGE);
         this.setHeight(80, Unit.PERCENTAGE);

         Button nextButton = new Button("Next");
         nextButton.setDescription("Next step");
         nextButton.addStyleName(ValoTheme.BUTTON_SMALL);

         final Button previousButton = new Button("Previous");
         previousButton.setDescription("Previous step");
         previousButton.addStyleName(ValoTheme.BUTTON_SMALL);
         previousButton.setVisible(false);

         nextButton.addClickListener(new Button.ClickListener()
         {
             public void buttonClick(ClickEvent event)
             {
                 setNextPanel();
                 previousButton.setVisible(true);
             }
         });

         previousButton.addClickListener(new Button.ClickListener()
         {
             public void buttonClick(ClickEvent event)
             {
                 setPreviousPanel();

                 if(previousSteps.isEmpty())
                 {
                     previousButton.setVisible(false);
                 }
             }
         });

         viewPort.addComponent(this.newMappingConfigurationDetailsPanel);
         this.currentPanel = this.newMappingConfigurationDetailsPanel;

         HorizontalLayout buttonsLayout = new HorizontalLayout();
         buttonsLayout.setSizeFull();
         buttonsLayout.addComponent(previousButton);
         buttonsLayout.addComponent(nextButton);

         VerticalLayout wrapperLayout = new VerticalLayout();
         wrapperLayout.setSizeFull();
         wrapperLayout.addComponent(viewPort);
         wrapperLayout.addComponent(buttonsLayout);

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
                 this.currentPanel = this.newMappingConfiguratioSummaryPanel;
             }
             else if(this.newMappingConfigurationTypePanel.getType() == NewMappingConfigurationTypePanel.TYPE.MANY_TO_ONE)
             {
                 this.currentPanel = this.newMappingConfigurationManyToOneNumParamsPanel;
             }
             else
             {
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
                 this.currentPanel = this.newMappingConfigurationManyToManyNameParamsPanel;
             }
             else
             {
                 this.currentPanel = this.newMappingConfiguratioSummaryPanel;
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
                 this.currentPanel = this.newMappingConfiguratioSummaryPanel;
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
             this.currentPanel = this.newMappingConfiguratioSummaryPanel;
         }
         else if(this.currentPanel.equals(this.newMappingConfigurationManyToManyNameParamsPanel))
         {
             previousSteps.addLast(this.currentPanel);
             this.newMappingConfigurationSourceParamNamesPanel.enter
                     (newMappingConfigurationManyToManyNumParamsPanel.getNumberSourceValues());

             this.currentPanel = this.newMappingConfigurationSourceParamNamesPanel;
         }
         else if(this.currentPanel.equals(this.newMappingConfigurationSourceParamNamesPanel))
         {
             previousSteps.addLast(this.currentPanel);

             this.newMappingConfigurationManyToManyTargetParamNamesPanel.enter
                     (newMappingConfigurationManyToManyNumParamsPanel.getNumberTargetValues());

             this.currentPanel = this.newMappingConfigurationManyToManyTargetParamNamesPanel;
         }
         else if(this.currentPanel.equals(this.newMappingConfigurationManyToManyTargetParamNamesPanel))
         {
             previousSteps.addLast(this.currentPanel);
             this.currentPanel = this.newMappingConfiguratioSummaryPanel;
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
