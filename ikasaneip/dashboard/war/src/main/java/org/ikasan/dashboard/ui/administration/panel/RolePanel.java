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
 import com.vaadin.server.VaadinService;
 import com.vaadin.ui.*;
 import com.vaadin.ui.Button.ClickEvent;
 import com.vaadin.ui.themes.ValoTheme;
 import org.apache.log4j.Logger;
 import org.ikasan.dashboard.ui.administration.window.PolicySelectWindow;
 import org.ikasan.dashboard.ui.administration.window.RoleSelectWindow;
 import org.ikasan.dashboard.ui.administration.window.UserSelectWindow;
 import org.ikasan.dashboard.ui.administration.window.UserWindow;
 import org.ikasan.dashboard.ui.framework.constants.SystemEventConstants;
 import org.ikasan.dashboard.ui.framework.util.DashboardSessionValueConstants;
 import org.ikasan.dashboard.ui.mappingconfiguration.component.IkasanSmallCellStyleGenerator;
 import org.ikasan.security.model.*;
 import org.ikasan.security.service.SecurityService;
 import org.ikasan.security.service.UserService;
 import org.ikasan.security.service.authentication.IkasanAuthentication;
 import org.ikasan.systemevent.service.SystemEventService;
 import org.springframework.security.access.method.P;
 import org.tepi.filtertable.FilterTable;
 import org.vaadin.teemu.VaadinIcons;

 import java.util.HashMap;
 import java.util.List;

 /**
  * @author CMI2 Development Team
  *
  */
 public class RolePanel extends Panel
 {
     private static final long serialVersionUID = 6005593259860222561L;

     private Logger logger = Logger.getLogger(RolePanel.class);

     private TextField nameField = new TextField();
     private TextField descriptionField;

     private FilterTable associatedUsersTable = new FilterTable();
     private FilterTable policyTable = new FilterTable();
     private FilterTable groupsTable = new FilterTable();

     private UserService userService;
     private SecurityService securityService;
     private SystemEventService systemEventService;

     private Role role;

     private IndexedContainer usersContainer;
     private IndexedContainer policiesContainer;
     private IndexedContainer groupsContainer;


     public RolePanel(UserService userService, SecurityService securityService,
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

     protected IndexedContainer buildPoliciesContainer()
     {
         IndexedContainer cont = new IndexedContainer();

         cont.addContainerProperty("Ikasan Policy", String.class, null);
         cont.addContainerProperty("Description", String.class, null);
         cont.addContainerProperty("", Button.class, null);

         return cont;
     }

     protected IndexedContainer buildUsersContainer()
     {
         IndexedContainer cont = new IndexedContainer();

         cont.addContainerProperty("Username", String.class, null);
         cont.addContainerProperty("Firstname", String.class, null);
         cont.addContainerProperty("Lastname", String.class, null);
         cont.addContainerProperty("Email", String.class, null);
         cont.addContainerProperty("Department", String.class, null);
         cont.addContainerProperty("", Button.class, null);

         return cont;
     }

     protected IndexedContainer buildGroupsContainer()
     {
         IndexedContainer cont = new IndexedContainer();

         cont.addContainerProperty("Name", String.class, null);
         cont.addContainerProperty("Type", String.class, null);
         cont.addContainerProperty("Description", String.class, null);
         cont.addContainerProperty("", Button.class, null);

         return cont;
     }

     protected void init()
     {
         this.setWidth("100%");
         this.setHeight("100%");

         VerticalLayout layout = new VerticalLayout();
         layout.setSizeFull();

         Panel securityAdministrationPanel = new Panel();
         securityAdministrationPanel.addStyleName(ValoTheme.PANEL_BORDERLESS);
         securityAdministrationPanel.setHeight("100%");
         securityAdministrationPanel.setWidth("100%");

         GridLayout gridLayout = new GridLayout(2, 3);
         gridLayout.setMargin(true);
         gridLayout.setSpacing(true);
         gridLayout.setWidth("100%");

         Label mappingConfigurationLabel = new Label("Role");
         mappingConfigurationLabel.setStyleName(ValoTheme.LABEL_HUGE);
         gridLayout.addComponent(mappingConfigurationLabel, 0, 0, 1, 0);


         nameField.setWidth("65%");

         descriptionField = new TextField();
         descriptionField.setWidth("65%");
         descriptionField.setNullRepresentation("");


         GridLayout formLayout = new GridLayout(2, 4);
         formLayout.setSpacing(true);
         formLayout.setWidth("100%");
         formLayout.setColumnExpandRatio(0, .1f);
         formLayout.setColumnExpandRatio(1, .8f);

         Label nameLabel = new Label("Name:");

         nameLabel.setSizeUndefined();
         formLayout.addComponent(nameLabel, 0, 0);
         formLayout.setComponentAlignment(nameLabel, Alignment.MIDDLE_RIGHT);
         formLayout.addComponent(nameField, 1, 0);

         Label descriptionLabel = new Label("Description:");
         descriptionLabel.setSizeUndefined();
         formLayout.addComponent(descriptionLabel, 0, 1);
         formLayout.setComponentAlignment(descriptionLabel, Alignment.MIDDLE_RIGHT);
         formLayout.addComponent(descriptionField, 1, 1);

         gridLayout.addComponent(formLayout, 0, 1, 1, 1);
         gridLayout.setComponentAlignment(formLayout, Alignment.TOP_CENTER);

         this.policiesContainer = this.buildPoliciesContainer();

         this.policyTable.setContainerDataSource(this.policiesContainer);
         this.policyTable.setColumnExpandRatio("Ikasan Policy", .3f);
         this.policyTable.setColumnExpandRatio("Description", .55f);
         this.policyTable.setColumnExpandRatio("", .15f);
         this.policyTable.addStyleName("ikasan");
         this.policyTable.addStyleName(ValoTheme.TABLE_SMALL);
         this.policyTable.setCellStyleGenerator(new IkasanSmallCellStyleGenerator());
         this.policyTable.setWidth("100%");
         this.policyTable.setHeight("200px");
         this.policyTable.setFilterBarVisible(true);

         this.usersContainer = this.buildUsersContainer();

         this.associatedUsersTable.setContainerDataSource(this.usersContainer);
         this.associatedUsersTable.setColumnExpandRatio("Username", .10f);
         this.associatedUsersTable.setColumnExpandRatio("Firstname", .20f);
         this.associatedUsersTable.setColumnExpandRatio("Lastname", .20f);
         this.associatedUsersTable.setColumnExpandRatio("Email", .20f);
         this.associatedUsersTable.setColumnExpandRatio("Department", .40f);
         this.associatedUsersTable.setColumnExpandRatio("", .05f);
         this.associatedUsersTable.addStyleName("ikasan");
         this.associatedUsersTable.addStyleName(ValoTheme.TABLE_SMALL);
         this.associatedUsersTable.setCellStyleGenerator(new IkasanSmallCellStyleGenerator());
         this.associatedUsersTable.setWidth("100%");
         this.associatedUsersTable.setHeight("200px");
         this.associatedUsersTable.setFilterBarVisible(true);

         this.associatedUsersTable.addItemClickListener(new ItemClickEvent.ItemClickListener()
         {
             @Override
             public void itemClick(ItemClickEvent itemClickEvent)
             {
                 if(itemClickEvent.isDoubleClick())
                 {
                     UserLite user = (UserLite)itemClickEvent.getItemId();

                     ((Window)getParent()).close();

                     UserWindow window = new UserWindow(userService, securityService, systemEventService, user);
                     UI.getCurrent().addWindow(window);
                 }
             }
         });

         this.groupsContainer = this.buildGroupsContainer();

         this.groupsTable.setContainerDataSource(this.groupsContainer);
         this.groupsTable.setColumnExpandRatio("Name", .20f);
         this.groupsTable.setColumnExpandRatio("Type", .20f);
         this.groupsTable.setColumnExpandRatio("Description", .40f);
         this.groupsTable.setColumnExpandRatio("", .05f);
         this.groupsTable.addStyleName("ikasan");
         this.groupsTable.addStyleName(ValoTheme.TABLE_SMALL);
         this.groupsTable.setCellStyleGenerator(new IkasanSmallCellStyleGenerator());
         this.groupsTable.setWidth("100%");
         this.groupsTable.setHeight("200px");
         this.groupsTable.setFilterBarVisible(true);


         GridLayout tablesLayout = new GridLayout(2, 6);
         tablesLayout.setMargin(false);
         tablesLayout.setHeight("800px");
         tablesLayout.setWidth("100%");
         tablesLayout.setSpacing(true);

         Label ikasanRolesLabel = new Label("Ikasan Policies");
         ikasanRolesLabel.setStyleName(ValoTheme.LABEL_HUGE);

         tablesLayout.addComponent(ikasanRolesLabel, 0, 0);

         final Button addPolicyButton = new Button("Add policy");
         addPolicyButton.setStyleName(ValoTheme.BUTTON_SMALL);
         addPolicyButton.addClickListener(new Button.ClickListener()
               {
                   @SuppressWarnings("unchecked")
                   public void buttonClick(ClickEvent event) {

                       final PolicySelectWindow window = new PolicySelectWindow(userService, securityService, systemEventService);
                       UI.getCurrent().addWindow(window);

                       window.addCloseListener(new Window.CloseListener()
                       {
                           @Override
                           public void windowClose(Window.CloseEvent closeEvent)
                           {
                                Policy policy = window.getPolicy();

                                if(policy != null)
                                {
                                    policy = securityService.getPolicyById(policy.getId());

                                    role = securityService.getRoleById(role.getId());

                                    role.getPolicies().add(policy);

                                    securityService.saveRole(role);

                                    IkasanAuthentication ikasanAuthentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
                                            .getAttribute(DashboardSessionValueConstants.USER);

                                    String action = "Policy " + policy.getName() + " added to role " + role.getName() + " by " + ikasanAuthentication.getName();

                                    systemEventService.logSystemEvent(SystemEventConstants.DASHBOARD_USER_ROLE_CHANGED_CONSTANTS, action, ikasanAuthentication.getName());

                                    addPolicy(policy);
                                }
                           }
                       });
                   }
               });

         tablesLayout.addComponent(addPolicyButton, 1, 0);
         tablesLayout.setComponentAlignment(addPolicyButton, Alignment.MIDDLE_RIGHT);

         tablesLayout.addComponent(this.policyTable, 0, 1, 1, 1);

         Label ldapGroupsLabel = new Label("Associated Users");
         ldapGroupsLabel.setStyleName(ValoTheme.LABEL_HUGE);
         tablesLayout.addComponent(ldapGroupsLabel, 0, 2);

         final Button addUserButton = new Button("Add user");
         addUserButton.setStyleName(ValoTheme.BUTTON_SMALL);
         addUserButton.addClickListener(new Button.ClickListener()
         {
             @SuppressWarnings("unchecked")
             public void buttonClick(ClickEvent event) {

                 final UserSelectWindow window = new UserSelectWindow(userService, securityService, systemEventService);
                 UI.getCurrent().addWindow(window);

                 window.addCloseListener(new Window.CloseListener()
                 {
                     @Override
                     public void windowClose(Window.CloseEvent closeEvent)
                     {
                         UserLite userLite = window.getUser();

                         if(userLite != null)
                         {
                             role = securityService.getRoleById(role.getId());

                             IkasanPrincipal principal = securityService.findPrincipalByName(userLite.getUsername());
                             principal.getRoles().add(role);

                             securityService.savePrincipal(principal);

                             IkasanAuthentication ikasanAuthentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
                                     .getAttribute(DashboardSessionValueConstants.USER);

                             String action = "Role " + role.getName() + " added to user " + userLite.getUsername() + " by " + ikasanAuthentication.getName();

                             systemEventService.logSystemEvent(SystemEventConstants.DASHBOARD_USER_ROLE_CHANGED_CONSTANTS, action, ikasanAuthentication.getName());

                             addUser(userLite);
                         }
                     }
                 });
             }
         });

         tablesLayout.addComponent(addUserButton, 1, 2);
         tablesLayout.setComponentAlignment(addUserButton, Alignment.MIDDLE_RIGHT);

         tablesLayout.addComponent(this.associatedUsersTable, 0, 3, 1, 3);

         Label groupsLabel = new Label("Associated Groups");
         groupsLabel.setStyleName(ValoTheme.LABEL_HUGE);
         tablesLayout.addComponent(groupsLabel, 0, 4);

         final Button addGroupButton = new Button("Add group");
         addGroupButton.setStyleName(ValoTheme.BUTTON_SMALL);
         addGroupButton.addClickListener(new Button.ClickListener()
         {
             @SuppressWarnings("unchecked")
             public void buttonClick(ClickEvent event) {

                 final UserSelectWindow window = new UserSelectWindow(userService, securityService, systemEventService);
                 UI.getCurrent().addWindow(window);

                 window.addCloseListener(new Window.CloseListener()
                 {
                     @Override
                     public void windowClose(Window.CloseEvent closeEvent)
                     {
                         UserLite userLite = window.getUser();

                         if(userLite != null)
                         {
                             role = securityService.getRoleById(role.getId());

                             IkasanPrincipal principal = securityService.findPrincipalByName(userLite.getUsername());
                             principal.getRoles().add(role);

                             securityService.savePrincipal(principal);

                             IkasanAuthentication ikasanAuthentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
                                     .getAttribute(DashboardSessionValueConstants.USER);

                             String action = "Role " + role.getName() + " added to user " + userLite.getUsername() + " by " + ikasanAuthentication.getName();

                             systemEventService.logSystemEvent(SystemEventConstants.DASHBOARD_USER_ROLE_CHANGED_CONSTANTS, action, ikasanAuthentication.getName());

                             addUser(userLite);
                         }
                     }
                 });
             }
         });

         tablesLayout.addComponent(addGroupButton, 1, 4);
         tablesLayout.setComponentAlignment(addGroupButton, Alignment.MIDDLE_RIGHT);

         tablesLayout.addComponent(this.groupsTable, 0, 5, 1, 5);

         gridLayout.addComponent(tablesLayout, 0, 2, 1, 2);

         Panel roleMemberPanel = new Panel();

         roleMemberPanel.addStyleName(ValoTheme.PANEL_BORDERLESS);
         roleMemberPanel.setHeight("100%");
         roleMemberPanel.setWidth("100%");

         GridLayout roleMemberLayout = new GridLayout();
         roleMemberLayout.setSpacing(true);
         roleMemberLayout.setWidth("100%");
         
         securityAdministrationPanel.setContent(gridLayout);
         layout.addComponent(securityAdministrationPanel);


         this.setContent(layout);
     }


     public void enter(Role role)
     {
         this.role = role;

         nameField.setValue(role.getName());
         descriptionField.setValue(role.getDescription());

         policyTable.removeAllItems();

         if(this.role.getPolicies() != null)
         {
             for (final Policy policy : this.role.getPolicies())
             {
                 addPolicy(policy);
             }
         }
         
         associatedUsersTable.removeAllItems();

         List<IkasanPrincipal> principals = this.securityService.getAllPrincipalsWithRole(role.getName());

         List<UserLite> users = this.userService.getUserLites();
         HashMap<String, UserLite> userMap = new HashMap<String, UserLite>();

         for(UserLite user: users)
         {
             userMap.put(user.getUsername(), user);
         }

         for(IkasanPrincipal principal: principals)
         {
             if(principal.getType().equals("user"))
             {
                 UserLite user = userMap.get(principal.getName());

                 if(user != null)
                 {
                    addUser(user);
                 }
             }
         }

     }

     private void addPolicy(final Policy policy)
     {
         Button deleteButton = new Button();
         deleteButton.setIcon(VaadinIcons.TRASH);
         deleteButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
         deleteButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);


         deleteButton.addClickListener(new Button.ClickListener()
         {
             public void buttonClick(ClickEvent event)
             {
                 policyTable.removeItem(policy);

                 role.getPolicies().remove(policy);

                 securityService.saveRole(role);


                 IkasanAuthentication ikasanAuthentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
                         .getAttribute(DashboardSessionValueConstants.USER);

                 String action = "Policy " + policy.getName() + " removed by " + ikasanAuthentication.getName();

                 systemEventService.logSystemEvent(SystemEventConstants.DASHBOARD_USER_ROLE_CHANGED_CONSTANTS, action, ikasanAuthentication.getName());
             }
         });

         Item item = this.policiesContainer.addItem(policy);

         item.getItemProperty("Ikasan Policy").setValue(policy.getName());
         item.getItemProperty("Description").setValue(policy.getDescription());
         item.getItemProperty("").setValue(deleteButton);
     }

     private void addUser(final UserLite user)
     {
         Button deleteButton = new Button();
         deleteButton.setIcon(VaadinIcons.TRASH);
         deleteButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
         deleteButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);


         deleteButton.addClickListener(new Button.ClickListener()
         {
             public void buttonClick(ClickEvent event)
             {
                 associatedUsersTable.removeItem(user);

                 role = securityService.getRoleById(role.getId());

                 IkasanPrincipal principal = securityService.findPrincipalByName(user.getUsername());
                 principal.getRoles().remove(role);

                 securityService.savePrincipal(principal);

                 IkasanAuthentication ikasanAuthentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
                         .getAttribute(DashboardSessionValueConstants.USER);

                 String action = "Role " + role.getName() + " removed from user " + user.getUsername() + " by " + ikasanAuthentication.getName();

                 systemEventService.logSystemEvent(SystemEventConstants.DASHBOARD_USER_ROLE_CHANGED_CONSTANTS, action, ikasanAuthentication.getName());
             }
         });

         Item item = this.usersContainer.addItem(user);

         item.getItemProperty("Username").setValue(user.getUsername());
         item.getItemProperty("Firstname").setValue(user.getFirstName());
         item.getItemProperty("Lastname").setValue(user.getSurname());
         item.getItemProperty("Email").setValue(user.getEmail());
         item.getItemProperty("Department").setValue(user.getDepartment());
         item.getItemProperty("").setValue(deleteButton);
     }
 }
