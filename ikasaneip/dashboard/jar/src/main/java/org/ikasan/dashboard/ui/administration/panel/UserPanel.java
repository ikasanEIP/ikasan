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
 import com.vaadin.server.VaadinService;
 import com.vaadin.ui.*;
 import com.vaadin.ui.Button.ClickEvent;
 import com.vaadin.ui.themes.ValoTheme;
 import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 import org.ikasan.dashboard.ui.administration.window.RoleSelectWindow;
 import org.ikasan.dashboard.ui.framework.constants.SecurityConstants;
 import org.ikasan.dashboard.ui.framework.constants.SystemEventConstants;
 import org.ikasan.dashboard.ui.framework.util.DashboardSessionValueConstants;
 import org.ikasan.dashboard.ui.mappingconfiguration.component.IkasanSmallCellStyleGenerator;
 import org.ikasan.security.model.IkasanPrincipal;
 import org.ikasan.security.model.Role;
 import org.ikasan.security.model.User;
 import org.ikasan.security.model.UserLite;
 import org.ikasan.security.service.SecurityService;
 import org.ikasan.security.service.UserService;
 import org.ikasan.security.service.authentication.IkasanAuthentication;
 import org.ikasan.systemevent.model.SystemEvent;
 import org.ikasan.systemevent.service.SystemEventService;
 import org.tepi.filtertable.FilterTable;
 import org.vaadin.teemu.VaadinIcons;

 import java.text.SimpleDateFormat;
 import java.util.ArrayList;
 import java.util.List;

 /**
  * @author CMI2 Development Team
  *
  */
 public class UserPanel extends Panel
 {
     private static final long serialVersionUID = 6005593259860222561L;

     private Logger logger = LoggerFactory.getLogger(UserPanel.class);

     private UserService userService;
     private SecurityService securityService;
     private TextField usernameField = new TextField();
     private TextField firstName;
     private TextField surname;
     private Table dashboardActivityTable = new Table();
     private FilterTable associatedPrincipalsTable = new FilterTable();
     private TextField department = new TextField();
     private TextField email = new TextField();
     private FilterTable roleTable = new FilterTable();
     private Table permissionChangeTable = new Table();
     private SystemEventService systemEventService;
     private User user;
     private IkasanPrincipal principal;

     private IndexedContainer associatedPrincipalsTableContainer;
     private IndexedContainer roleTableTableContainer;

     private Button addRoleButton;


     public UserPanel(UserService userService, SecurityService securityService,
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

         Label mappingConfigurationLabel = new Label("User Profile");
         mappingConfigurationLabel.setStyleName(ValoTheme.LABEL_HUGE);
         gridLayout.addComponent(mappingConfigurationLabel, 0, 0, 1, 0);

         Label usernameLabel = new Label("Username:");

         usernameField.setWidth("65%");

         firstName = new TextField();
         firstName.setWidth("65%");
         firstName.setNullRepresentation("");
         surname = new TextField();
         surname.setWidth("65%");
         surname.setNullRepresentation("");
         department.setWidth("65%");
         department.setNullRepresentation("");
         email.setWidth("65%");
         email.setNullRepresentation("");


         GridLayout formLayout = new GridLayout(2, 5);
         formLayout.setSpacing(true);
         formLayout.setWidth("100%");
         formLayout.setColumnExpandRatio(0, .1f);
         formLayout.setColumnExpandRatio(1, .8f);

         usernameLabel.setSizeUndefined();
         formLayout.addComponent(usernameLabel, 0, 0);
         formLayout.setComponentAlignment(usernameLabel, Alignment.MIDDLE_RIGHT);
         formLayout.addComponent(usernameField, 1, 0);

         Label firstNameLabel = new Label("First name:");
         firstNameLabel.setSizeUndefined();
         formLayout.addComponent(firstNameLabel, 0, 1);
         formLayout.setComponentAlignment(firstNameLabel, Alignment.MIDDLE_RIGHT);
         formLayout.addComponent(firstName, 1, 1);

         Label surnameLabel = new Label("Surname:");
         surnameLabel.setSizeUndefined();
         formLayout.addComponent(surnameLabel, 0, 2);
         formLayout.setComponentAlignment(surnameLabel, Alignment.MIDDLE_RIGHT);
         formLayout.addComponent(surname, 1, 2);

         Label departmentLabel = new Label("Department:");
         departmentLabel.setSizeUndefined();
         formLayout.addComponent(departmentLabel, 0, 3);
         formLayout.setComponentAlignment(departmentLabel, Alignment.MIDDLE_RIGHT);
         formLayout.addComponent(department, 1, 3);

         Label emailLabel = new Label("Email address:");
         emailLabel.setSizeUndefined();
         formLayout.addComponent(emailLabel, 0, 4);
         formLayout.setComponentAlignment(emailLabel, Alignment.MIDDLE_RIGHT);
         formLayout.addComponent(email, 1, 4);

         gridLayout.addComponent(formLayout, 0, 1, 1, 1);
         gridLayout.setComponentAlignment(formLayout, Alignment.TOP_CENTER);

//         Label rolesAndGroupsHintLabel1 = new Label();
//         rolesAndGroupsHintLabel1.setCaptionAsHtml(true);
//         rolesAndGroupsHintLabel1.setCaption(VaadinIcons.QUESTION_CIRCLE_O.getHtml() +
//                 " The Roles table below displays the Ikasan roles that the user has.");
//         rolesAndGroupsHintLabel1.addStyleName(ValoTheme.LABEL_TINY);
//         rolesAndGroupsHintLabel1.addStyleName(ValoTheme.LABEL_LIGHT);
//         rolesAndGroupsHintLabel1.setWidth(300, Unit.PIXELS);
//         gridLayout.addComponent(rolesAndGroupsHintLabel1, 0, 3, 1, 3);
//
//         Label rolesAndGroupsHintLabel2 = new Label();
//         rolesAndGroupsHintLabel2.setCaptionAsHtml(true);
//         rolesAndGroupsHintLabel2.setCaption(VaadinIcons.QUESTION_CIRCLE_O.getHtml() +
//                 " The Groups table below displays all the LDAP groups that the user is a member of.");
//
//         rolesAndGroupsHintLabel2.addStyleName(ValoTheme.LABEL_TINY);
//         rolesAndGroupsHintLabel2.addStyleName(ValoTheme.LABEL_LIGHT);
//         rolesAndGroupsHintLabel2.setWidth(300, Unit.PIXELS);
//         gridLayout.addComponent(rolesAndGroupsHintLabel2, 0, 4, 1, 4);

         this.associatedPrincipalsTableContainer = new IndexedContainer();
         this.roleTableTableContainer = new IndexedContainer();

         this.roleTableTableContainer.addContainerProperty("Ikasan Role", String.class, null);
         this.roleTable.setColumnExpandRatio("Ikasan Role", .3f);
         this.roleTableTableContainer.addContainerProperty("Description", String.class, null);
         this.roleTable.setColumnExpandRatio("Description", .55f);
         this.roleTableTableContainer.addContainerProperty("", Button.class, null);
         this.roleTable.setColumnExpandRatio("", .15f);
         this.roleTable.addStyleName("ikasan");
         this.roleTable.addStyleName(ValoTheme.TABLE_SMALL);
         this.roleTable.setCellStyleGenerator(new IkasanSmallCellStyleGenerator());
         this.roleTable.setSizeFull();
         this.roleTable.setFilterBarVisible(true);
         this.roleTable.setContainerDataSource(this.roleTableTableContainer);
         
         this.associatedPrincipalsTableContainer.addContainerProperty("LDAP Group", String.class, null);
         this.associatedPrincipalsTable.setColumnExpandRatio("LDAP Group", .40f);
         this.associatedPrincipalsTableContainer.addContainerProperty("Type", String.class, null);
         this.associatedPrincipalsTable.setColumnExpandRatio("Type", .20f);
         this.associatedPrincipalsTableContainer.addContainerProperty("Description", String.class, null);
         this.associatedPrincipalsTable.setColumnExpandRatio("Description", .40f);
         this.associatedPrincipalsTable.addStyleName("ikasan");
         this.associatedPrincipalsTable.addStyleName(ValoTheme.TABLE_SMALL);
         this.associatedPrincipalsTable.setCellStyleGenerator(new IkasanSmallCellStyleGenerator());
         this.associatedPrincipalsTable.setSizeFull();
         this.associatedPrincipalsTable.setFilterBarVisible(true);
         this.associatedPrincipalsTable.setContainerDataSource(this.associatedPrincipalsTableContainer);


         GridLayout tablesLayout = new GridLayout(2, 1);
         tablesLayout.setWidth("100%");
         tablesLayout.setMargin(false);
         tablesLayout.setSpacing(true);

         Label ikasanRolesLabel = new Label("Ikasan Roles");
         ikasanRolesLabel.setStyleName(ValoTheme.LABEL_HUGE);

         tablesLayout.addComponent(ikasanRolesLabel, 0, 0);

         addRoleButton = new Button("Add role");
         addRoleButton.setStyleName(ValoTheme.BUTTON_SMALL);
         addRoleButton.addClickListener(new Button.ClickListener()
               {
                   @SuppressWarnings("unchecked")
                   public void buttonClick(ClickEvent event) {

                       final RoleSelectWindow window = new RoleSelectWindow(userService, securityService, systemEventService);
                       UI.getCurrent().addWindow(window);

                       window.addCloseListener(new Window.CloseListener()
                       {
                           @Override
                           public void windowClose(Window.CloseEvent closeEvent)
                           {
                                final Role role = window.getRole();

                                if(role != null)
                                {
                                    addRole(role);

                                    principal.getRoles().add(role);

                                    securityService.savePrincipal(principal);

                                    IkasanAuthentication ikasanAuthentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
                                            .getAttribute(DashboardSessionValueConstants.USER);

                                    String action = "Role " + role.getName() + " added by " + ikasanAuthentication.getName();

                                    systemEventService.logSystemEvent(SystemEventConstants.DASHBOARD_USER_ROLE_CHANGED_CONSTANTS, action, user.getUsername());

                                    updateRoleChangedEvents();
                                }
                           }
                       });
                   }
               });

         tablesLayout.addComponent(addRoleButton, 1, 0);
         tablesLayout.setComponentAlignment(addRoleButton, Alignment.MIDDLE_RIGHT);

         VerticalSplitPanel rolesVpanel = new VerticalSplitPanel(tablesLayout
                 , this.roleTable);
         rolesVpanel.setSizeFull();
         rolesVpanel.setSplitPosition(40, Unit.PIXELS);
         rolesVpanel.setLocked(true);

         tablesLayout = new GridLayout(2, 1);
         tablesLayout.setMargin(false);
         tablesLayout.setSpacing(true);

         Label ldapGroupsLabel = new Label("Ldap Groups");
         ldapGroupsLabel.setStyleName(ValoTheme.LABEL_HUGE);
         tablesLayout.addComponent(ldapGroupsLabel, 0, 0);

         VerticalSplitPanel ldapVpanel = new VerticalSplitPanel(tablesLayout
                 , this.associatedPrincipalsTable);
         ldapVpanel.setSizeFull();
         ldapVpanel.setSplitPosition(40, Unit.PIXELS);
         ldapVpanel.setLocked(true);

         VerticalLayout rolesLayout = new VerticalLayout();
         rolesLayout.setSizeFull();
         rolesLayout.setMargin(true);
         rolesLayout.addComponent(rolesVpanel);

         VerticalLayout ldapLayout = new VerticalLayout();
         ldapLayout.setSizeFull();
         ldapLayout.setMargin(true);
         ldapLayout.addComponent(ldapVpanel);

         VerticalSplitPanel tablesVpanel = new VerticalSplitPanel(rolesLayout
                 , ldapLayout);
         tablesVpanel.setSizeFull();
         tablesVpanel.setSplitPosition(50, Unit.PERCENTAGE);
         tablesVpanel.setLocked(true);

         VerticalSplitPanel holderVpanel = new VerticalSplitPanel(gridLayout
                 , tablesVpanel);
         holderVpanel.setSizeFull();
         holderVpanel.setSplitPosition(260, Unit.PIXELS);
         holderVpanel.setLocked(true);

         Panel roleMemberPanel = new Panel();

         roleMemberPanel.addStyleName(ValoTheme.PANEL_BORDERLESS);
         roleMemberPanel.setHeight("100%");
         roleMemberPanel.setWidth("100%");

         dashboardActivityTable.addContainerProperty("Action", String.class, null);
         dashboardActivityTable.addContainerProperty("Date/Time", String.class, null);
         dashboardActivityTable.addStyleName("ikasan");
         dashboardActivityTable.addStyleName(ValoTheme.TABLE_SMALL);
         dashboardActivityTable.setCellStyleGenerator(new IkasanSmallCellStyleGenerator());
         dashboardActivityTable.setSizeFull();

         this.permissionChangeTable.addContainerProperty("Action", String.class, null);
         this.permissionChangeTable.addContainerProperty("Date/Time", String.class, null);
         this.permissionChangeTable.addStyleName("ikasan");
         this.permissionChangeTable.addStyleName(ValoTheme.TABLE_SMALL);
         this.permissionChangeTable.setCellStyleGenerator(new IkasanSmallCellStyleGenerator());
         this.permissionChangeTable.setSizeFull();

         Label dashboardActivityLabel = new Label("Dashboard Activity");
         dashboardActivityLabel.setStyleName(ValoTheme.LABEL_HUGE);


         VerticalSplitPanel activityVpanel = new VerticalSplitPanel(dashboardActivityLabel
                 , this.dashboardActivityTable);
         activityVpanel.setSizeFull();
         activityVpanel.setSplitPosition(40, Unit.PIXELS);
         activityVpanel.setLocked(true);

         Label permissionChangeLabel = new Label("User Security Changes");
         permissionChangeLabel.setStyleName(ValoTheme.LABEL_HUGE);


         VerticalSplitPanel securityChangeVpanel = new VerticalSplitPanel(permissionChangeLabel
                 , this.permissionChangeTable);
         securityChangeVpanel.setSizeFull();
         securityChangeVpanel.setSplitPosition(40, Unit.PIXELS);
         securityChangeVpanel.setLocked(true);

         VerticalSplitPanel rightVpanel = new VerticalSplitPanel(activityVpanel
                 , securityChangeVpanel);
         rightVpanel.setSizeFull();
         rightVpanel.setSplitPosition(50, Unit.PERCENTAGE);
         rightVpanel.setLocked(true);

         roleMemberPanel.setContent(rightVpanel);

         securityAdministrationPanel.setContent(holderVpanel);
         layout.addComponent(securityAdministrationPanel);

         VerticalLayout roleMemberPanelLayout = new VerticalLayout();
         roleMemberPanelLayout.setWidth("100%");
         roleMemberPanelLayout.setHeight("100%");
         roleMemberPanelLayout.setMargin(true);
         roleMemberPanelLayout.addComponent(roleMemberPanel);
         roleMemberPanelLayout.setSizeFull();

         HorizontalSplitPanel hsplit = new HorizontalSplitPanel();
         hsplit.setFirstComponent(layout);
         hsplit.setSecondComponent(roleMemberPanelLayout);


         // Set the position of the splitter as percentage
         hsplit.setSplitPosition(65, Unit.PERCENTAGE);
         hsplit.setLocked(true);

         this.setContent(hsplit);
     }


     public void enter(UserLite userLite)
     {
         final IkasanAuthentication authentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
                 .getAttribute(DashboardSessionValueConstants.USER);

         if(authentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY) ||
                 authentication.hasGrantedAuthority(SecurityConstants.USER_ADMINISTRATION_ADMIN)
                 || authentication.hasGrantedAuthority(SecurityConstants.USER_ADMINISTRATION_WRITE))
         {
             addRoleButton.setVisible(true);
         }
         else
         {
             addRoleButton.setVisible(false);
         }


         this.dashboardActivityTable.removeAllItems();

         user = this.userService.loadUserByUsername(userLite.getUsername());

         usernameField.setValue(user.getUsername());
         firstName.setValue(user.getFirstName());
         surname.setValue(user.getSurname());
         department.setValue(user.getDepartment());
         email.setValue(user.getEmail());

         principal = securityService.findPrincipalByName(user.getUsername());

         roleTable.removeAllItems();

         for (final Role role : principal.getRoles())
         {
             addRole(role);
         }

         associatedPrincipalsTable.removeAllItems();

         for(IkasanPrincipal ikasanPrincipal: user.getPrincipals())
         {
             if(!ikasanPrincipal.getType().equals("user"))
             {
                 Item item = this.associatedPrincipalsTableContainer.addItem(ikasanPrincipal);

                 item.getItemProperty("LDAP Group").setValue(ikasanPrincipal.getName());
                 item.getItemProperty("Type").setValue(ikasanPrincipal.getType());
                 item.getItemProperty("Description").setValue(ikasanPrincipal.getDescription());
             }
         }

         ArrayList<String> subjects = new ArrayList<String>();
         subjects.add(SystemEventConstants.DASHBOARD_LOGIN_CONSTANTS);
         subjects.add(SystemEventConstants.DASHBOARD_LOGOUT_CONSTANTS);
         subjects.add(SystemEventConstants.DASHBOARD_SESSION_EXPIRED_CONSTANTS);

         List<SystemEvent> events = this.systemEventService.listSystemEvents(subjects, user.getUsername(), null, null);

         for(SystemEvent systemEvent: events)
         {
             SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");

             String date = dateFormat.format(systemEvent.getTimestamp());

             dashboardActivityTable.addItem(new Object[]
                     { systemEvent.getAction(), date}, systemEvent);
         }

         updateRoleChangedEvents();
     }

     private void updateRoleChangedEvents()
     {
         ArrayList<String> subjects = new ArrayList<String>();
         subjects.add(SystemEventConstants.DASHBOARD_USER_ROLE_CHANGED_CONSTANTS);


         List<SystemEvent> events = this.systemEventService.listSystemEvents(subjects, user.getUsername(), null, null);

         for(SystemEvent systemEvent: events)
         {
             SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");

             String date = dateFormat.format(systemEvent.getTimestamp());

             this.permissionChangeTable.addItem(new Object[]
                     { systemEvent.getAction(), date}, systemEvent);
         }
     }

     private void addRole(final Role role)
     {
         Button deleteButton = new Button();
         deleteButton.setIcon(VaadinIcons.TRASH);
         deleteButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
         deleteButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);


         deleteButton.addClickListener(new Button.ClickListener()
         {
             public void buttonClick(ClickEvent event)
             {
                 roleTable.removeItem(role);

                 principal.getRoles().remove(role);

                 securityService.savePrincipal(principal);

                 dashboardActivityTable.removeItem(principal.getName());

                 IkasanAuthentication ikasanAuthentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
                         .getAttribute(DashboardSessionValueConstants.USER);

                 String action = "Role " + role.getName() + " removed by " + ikasanAuthentication.getName();

                 systemEventService.logSystemEvent(SystemEventConstants.DASHBOARD_USER_ROLE_CHANGED_CONSTANTS, action, user.getUsername());

                 updateRoleChangedEvents();
             }
         });

         final IkasanAuthentication authentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
                 .getAttribute(DashboardSessionValueConstants.USER);

         if(authentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY) ||
                 authentication.hasGrantedAuthority(SecurityConstants.USER_ADMINISTRATION_ADMIN)
                 || authentication.hasGrantedAuthority(SecurityConstants.USER_ADMINISTRATION_WRITE))
         {
             deleteButton.setVisible(true);
         }
         else
         {
             deleteButton.setVisible(false);
         }

         Item item = this.roleTableTableContainer.addItem(role);

         item.getItemProperty("Ikasan Role").setValue(role.getName());
         item.getItemProperty("Description").setValue(role.getDescription());
         item.getItemProperty("").setValue(deleteButton);
     }
 }
