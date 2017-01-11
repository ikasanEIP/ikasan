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
 import com.vaadin.data.util.BeanItem;
 import com.vaadin.data.util.IndexedContainer;
 import com.vaadin.event.ItemClickEvent;
 import com.vaadin.navigator.View;
 import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
 import com.vaadin.server.VaadinService;
 import com.vaadin.ui.*;
 import com.vaadin.ui.themes.ValoTheme;
 import org.apache.log4j.Logger;
 import org.ikasan.dashboard.ui.administration.window.NewRoleWindow;
 import org.ikasan.dashboard.ui.administration.window.RoleWindow;
 import org.ikasan.dashboard.ui.administration.window.UserWindow;
 import org.ikasan.dashboard.ui.framework.constants.DashboardConstants;
 import org.ikasan.dashboard.ui.framework.constants.SystemEventConstants;
 import org.ikasan.dashboard.ui.framework.util.DashboardSessionValueConstants;
 import org.ikasan.dashboard.ui.mappingconfiguration.component.IkasanSmallCellStyleGenerator;
 import org.ikasan.security.model.Role;
 import org.ikasan.security.model.UserLite;
 import org.ikasan.security.service.SecurityService;
 import org.ikasan.security.service.UserService;
 import org.ikasan.security.service.authentication.IkasanAuthentication;
 import org.ikasan.systemevent.service.SystemEventService;
 import org.tepi.filtertable.FilterTable;
 import org.vaadin.teemu.VaadinIcons;

 import java.text.SimpleDateFormat;
 import java.util.Date;
 import java.util.List;

 /**
  * @author CMI2 Development Team
  *
  */
 public class RoleManagementPanel extends Panel implements View
 {
     private static final long serialVersionUID = 6005593259860222561L;

     private Logger logger = Logger.getLogger(RoleManagementPanel.class);

     private UserService userService;
     private SecurityService securityService;
     private SystemEventService systemEventService;

     private FilterTable roleTable;

     private IndexedContainer tableContainer;

     public RoleManagementPanel(UserService userService, SecurityService securityService,
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
         cont.addContainerProperty("", Button.class,  null);

         return cont;
     }

     protected void init()
     {
         this.setWidth("100%");
         this.setHeight("100%");


         Panel securityAdministrationPanel = new Panel();
         securityAdministrationPanel.addStyleName(ValoTheme.PANEL_BORDERLESS);
         securityAdministrationPanel.setSizeFull();

         GridLayout gridLayout = new GridLayout(2, 2);
         gridLayout.setMargin(true);
         gridLayout.setSpacing(true);
         gridLayout.setWidth("100%");

         Label mappingConfigurationLabel = new Label("Role Management");
         mappingConfigurationLabel.setStyleName(ValoTheme.LABEL_HUGE);
         gridLayout.addComponent(mappingConfigurationLabel, 0, 0);
         gridLayout.setComponentAlignment(mappingConfigurationLabel, Alignment.MIDDLE_LEFT);

         Button newButton = new Button();
         newButton.setIcon(VaadinIcons.PLUS);
         newButton.setDescription("Create a New Role");
         newButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
         newButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
         newButton.addClickListener(new Button.ClickListener()
         {
             public void buttonClick(Button.ClickEvent event)
             {
                 final NewRoleWindow newRoleWindow = new NewRoleWindow(securityService);
                 UI.getCurrent().addWindow(newRoleWindow);

                 newRoleWindow.addCloseListener(new Window.CloseListener()
                 {
                     // inline close-listener
                     public void windowClose(Window.CloseEvent e)
                     {
                         Role role = newRoleWindow.getRole();

                         if(role != null)
                         {
                             addRoleToTable(role);
                         }
                     }
                 });
             }
         });

         gridLayout.addComponent(newButton, 1, 0);
         gridLayout.setComponentAlignment(newButton, Alignment.MIDDLE_RIGHT);


         this.tableContainer = this.buildContainer();

         this.roleTable = new FilterTable();
         this.roleTable.setWidth("100%");
         this.roleTable.setHeight("900px");

         this.roleTable.setFilterBarVisible(true);
         this.roleTable.addStyleName(ValoTheme.TABLE_SMALL);
         this.roleTable.addStyleName("ikasan");

         this.roleTable.setColumnExpandRatio("Name", .1f);
         this.roleTable.setColumnExpandRatio("Description", .2f);
         this.roleTable.setColumnExpandRatio("", .1f);

         this.roleTable.addStyleName("wordwrap-table");
         this.roleTable.setCellStyleGenerator(new IkasanSmallCellStyleGenerator());

         this.roleTable.setContainerDataSource(tableContainer);

         this.roleTable.addItemClickListener(new ItemClickEvent.ItemClickListener()
         {
             @Override
             public void itemClick(ItemClickEvent itemClickEvent)
             {
                 if(itemClickEvent.isDoubleClick())
                 {
                     RoleWindow window = new RoleWindow(userService, securityService, systemEventService, (Role)itemClickEvent.getItemId());
                     UI.getCurrent().addWindow(window);
                 }
             }
         });

         gridLayout.addComponent(this.roleTable, 0, 1, 1, 1);
         gridLayout.setComponentAlignment(this.roleTable, Alignment.MIDDLE_CENTER);

         securityAdministrationPanel.setContent(gridLayout);

         this.setContent(securityAdministrationPanel);
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
         logger.info("Loading roles");

         List<Role> roles = this.securityService.getAllRoles();

         logger.info("Finished loading roles. Number loaded: " + roles.size());

         this.tableContainer.removeAllItems();

         for(final Role role: roles)
         {
             this.addRoleToTable(role);
         }
     }

     private void addRoleToTable(final Role role)
     {
         Item item = tableContainer.addItem(role);

         logger.info("Adding role: " + role.hashCode());
         logger.info("Adding role: " + role);

         item.getItemProperty("Name").setValue(role.getName());
         item.getItemProperty("Description").setValue(role.getDescription());

         Button deleteButton = new Button();
         deleteButton.setIcon(VaadinIcons.TRASH);
         deleteButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
         deleteButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);


         deleteButton.addClickListener(new Button.ClickListener()
         {
             public void buttonClick(Button.ClickEvent event)
             {
                 try
                 {
                     securityService.deleteRole(role);

                     tableContainer.removeItem(role);

                     logger.info("Deleting role: " + role.hashCode());
                     logger.info("Deleting role: " + role);

                     Notification.show("Role deleted!", Notification.Type.HUMANIZED_MESSAGE);
                 }
                 catch(Exception e)
                 {
                     e.printStackTrace();

                     Notification.show("An error has occurred deleting this role. A role cannot be deleted if it is assigned to " +
                             "any users or groups.", Notification.Type.ERROR_MESSAGE);
                 }
             }
         });

         item.getItemProperty("").setValue(deleteButton);
     }
 }
