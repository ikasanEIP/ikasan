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
 import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 import org.ikasan.dashboard.ui.administration.window.GroupSelectWindow;
 import org.ikasan.dashboard.ui.mappingconfiguration.component.IkasanSmallCellStyleGenerator;
 import org.ikasan.security.model.IkasanPrincipalLite;
 import org.ikasan.security.service.SecurityService;
 import org.ikasan.security.service.UserService;
 import org.ikasan.systemevent.service.SystemEventService;
 import org.tepi.filtertable.FilterTable;

 import java.util.List;

 /**
  * @author CMI2 Development Team
  *
  */
 public class GroupSelectPanel extends Panel
 {
     private static final long serialVersionUID = 6005593259860222561L;

     private Logger logger = LoggerFactory.getLogger(GroupSelectPanel.class);

     private UserService userService;
     private SecurityService securityService;
     private SystemEventService systemEventService;

     private FilterTable usersTable;

     private IndexedContainer tableContainer;


     public GroupSelectPanel(UserService userService, SecurityService securityService,
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

         Label policyLabel = new Label("Select Group");
         policyLabel.setStyleName(ValoTheme.LABEL_HUGE);
         gridLayout.addComponent(policyLabel);
         gridLayout.setComponentAlignment(policyLabel, Alignment.MIDDLE_LEFT);
         this.tableContainer = this.buildContainer();

         this.usersTable = new FilterTable();
         this.usersTable.setWidth("100%");
         this.usersTable.setHeight("500px");
         
         this.usersTable.setFilterBarVisible(true);
         this.usersTable.addStyleName(ValoTheme.TABLE_SMALL);
         this.usersTable.addStyleName("ikasan");

         this.usersTable.setColumnExpandRatio("Name", .1f);
         this.usersTable.setColumnExpandRatio("Description", .2f);

         this.usersTable.addStyleName("wordwrap-table");
         this.usersTable.setCellStyleGenerator(new IkasanSmallCellStyleGenerator());

         this.usersTable.setContainerDataSource(tableContainer);

         this.usersTable.addItemClickListener(new ItemClickEvent.ItemClickListener()
         {
             @Override
             public void itemClick(ItemClickEvent itemClickEvent)
             {
                 if(itemClickEvent.isDoubleClick())
                 {
                     IkasanPrincipalLite ikasanPrincipalLite = (IkasanPrincipalLite)itemClickEvent.getItemId();

                     logger.info("ikasanPrincipalLite:" + ikasanPrincipalLite);

                     ((GroupSelectWindow)getParent()).setUser(ikasanPrincipalLite);
                     ((GroupSelectWindow)getParent()).close();
                 }
             }
         });

         logger.info("Loading users");

         List<IkasanPrincipalLite> principals = this.securityService.getAllPrincipalLites();

         logger.info("Finished loading users. Number loaded: " + principals.size());

         this.tableContainer.removeAllItems();

         for(IkasanPrincipalLite principal: principals)
         {
             if(principal.getType() != null && principal.getType().equals("application"))
             {
                 Item item = tableContainer.addItem(principal);
                 this.usersTable.setColumnExpandRatio("Name", .1f);
                 this.usersTable.setColumnExpandRatio("Description", .2f);

                 item.getItemProperty("Name").setValue(principal.getName());
                 item.getItemProperty("Description").setValue(principal.getDescription());
             }
         }

         gridLayout.addComponent(this.usersTable);
         gridLayout.setComponentAlignment(this.usersTable, Alignment.MIDDLE_CENTER);

         policyPanel.setContent(gridLayout);
         layout.addComponent(policyPanel);

         this.setContent(policyPanel);
     }
 }
