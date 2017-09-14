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

 import com.vaadin.data.Item;
 import com.vaadin.data.util.IndexedContainer;
 import com.vaadin.event.ItemClickEvent;
 import com.vaadin.ui.*;
 import com.vaadin.ui.themes.ValoTheme;
 import org.apache.log4j.Logger;
 import org.ikasan.dashboard.ui.administration.window.PolicySelectWindow;
 import org.ikasan.dashboard.ui.administration.window.RoleSelectWindow;
 import org.ikasan.dashboard.ui.mappingconfiguration.component.IkasanSmallCellStyleGenerator;
 import org.ikasan.security.model.Policy;
 import org.ikasan.security.model.Role;
 import org.ikasan.security.service.SecurityService;
 import org.ikasan.security.service.UserService;
 import org.ikasan.systemevent.service.SystemEventService;
 import org.tepi.filtertable.FilterTable;

 import java.util.List;

 /**
  * @author CMI2 Development Team
  *
  */
 public class PolicySelectPanel extends Panel
 {
     private static final long serialVersionUID = 6005593259860222561L;

     private Logger logger = Logger.getLogger(PolicySelectPanel.class);

     private UserService userService;
     private SecurityService securityService;
     private SystemEventService systemEventService;

     private FilterTable policiesTable;

     private IndexedContainer tableContainer;


     public PolicySelectPanel(UserService userService, SecurityService securityService,
                              SystemEventService systemEventService)
     {
         super();
         this.userService = userService;
         if (this.userService == null)
         {
             throw new IllegalArgumentException("userService cannot be null!");
         }
         this.securityService = securityService;
         if (this.securityService == null)
         {
             throw new IllegalArgumentException(
                     "securityService cannot be null!");
         }
         this.systemEventService = systemEventService;
         if (this.systemEventService == null)
         {
             throw new IllegalArgumentException(
                     "systemEventService cannot be null!");
         }

         init();
     }

     protected IndexedContainer buildContainer()
     {
         IndexedContainer cont = new IndexedContainer();

         cont.addContainerProperty("Name", String.class,  null);
         cont.addContainerProperty("Description", String.class,  null);

         return cont;
     }

     protected void init()
     {
         this.setWidth("100%");
         this.setHeight("100%");

         VerticalLayout layout = new VerticalLayout();
         layout.setSizeFull();

         Panel policyPanel = new Panel();
         policyPanel.addStyleName(ValoTheme.PANEL_BORDERLESS);
         policyPanel.setHeight("100%");
         policyPanel.setWidth("100%");

         GridLayout gridLayout = new GridLayout();
         gridLayout.setMargin(true);
         gridLayout.setSpacing(true);
         gridLayout.setWidth("100%");

         Label policyLabel = new Label("Select Policy");
         policyLabel.setStyleName(ValoTheme.LABEL_HUGE);
         gridLayout.addComponent(policyLabel);
         gridLayout.setComponentAlignment(policyLabel, Alignment.MIDDLE_LEFT);
         this.tableContainer = this.buildContainer();

         this.policiesTable = new FilterTable();
         this.policiesTable.setWidth("100%");
         this.policiesTable.setHeight("250px");
         
         this.policiesTable.setFilterBarVisible(true);
         this.policiesTable.addStyleName(ValoTheme.TABLE_SMALL);
         this.policiesTable.addStyleName("ikasan");

         this.policiesTable.setColumnExpandRatio("Name", .1f);
         this.policiesTable.setColumnExpandRatio("Description", .2f);

         this.policiesTable.addStyleName("wordwrap-table");
         this.policiesTable.setCellStyleGenerator(new IkasanSmallCellStyleGenerator());

         this.policiesTable.setContainerDataSource(tableContainer);

         this.policiesTable.addItemClickListener(new ItemClickEvent.ItemClickListener()
         {
             @Override
             public void itemClick(ItemClickEvent itemClickEvent)
             {
                 if(itemClickEvent.isDoubleClick())
                 {
                     Policy policy = (Policy)itemClickEvent.getItemId();

                     logger.info("Policy selected:" + policy);

                     ((PolicySelectWindow)getParent()).setPolicy(policy);
                     ((PolicySelectWindow)getParent()).close();
                 }
             }
         });

         logger.info("Loading policies");

         List<Policy> policies = this.securityService.getAllPolicies();

         logger.info("Finished loading policies. Number loaded: " + policies.size());

         this.tableContainer.removeAllItems();

         for(Policy policy: policies)
         {
             Item item = tableContainer.addItem(policy);
             this.policiesTable.setColumnExpandRatio("Name", .1f);
             this.policiesTable.setColumnExpandRatio("Description", .2f);

             item.getItemProperty("Name").setValue(policy.getName());
             item.getItemProperty("Description").setValue(policy.getDescription());
         }

         gridLayout.addComponent(this.policiesTable);
         gridLayout.setComponentAlignment(this.policiesTable, Alignment.MIDDLE_CENTER);

         policyPanel.setContent(gridLayout);
         layout.addComponent(policyPanel);

         this.setContent(policyPanel);
     }
 }
