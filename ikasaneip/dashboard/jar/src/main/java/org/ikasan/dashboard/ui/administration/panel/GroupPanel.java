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

 import com.vaadin.event.ItemClickEvent;
 import com.vaadin.server.VaadinService;
 import com.vaadin.ui.*;
 import com.vaadin.ui.Button.ClickEvent;
 import com.vaadin.ui.themes.ValoTheme;
 import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 import org.ikasan.dashboard.ui.administration.window.RoleSelectWindow;
 import org.ikasan.dashboard.ui.administration.window.UserWindow;
 import org.ikasan.dashboard.ui.framework.constants.SecurityConstants;
 import org.ikasan.dashboard.ui.framework.constants.SystemEventConstants;
 import org.ikasan.dashboard.ui.framework.util.DashboardSessionValueConstants;
 import org.ikasan.dashboard.ui.mappingconfiguration.component.IkasanSmallCellStyleGenerator;
 import org.ikasan.security.model.*;
 import org.ikasan.security.service.SecurityService;
 import org.ikasan.security.service.UserService;
 import org.ikasan.security.service.authentication.IkasanAuthentication;
 import org.ikasan.systemevent.service.SystemEventService;
 import org.vaadin.teemu.VaadinIcons;

 import java.util.List;

 /**
  * @author CMI2 Development Team
  *
  */
 public class GroupPanel extends Panel
 {
     private static final long serialVersionUID = 6005593259860222561L;

     private Logger logger = LoggerFactory.getLogger(GroupPanel.class);

     private TextField groupNameField = new TextField();
     private TextField groupTypeField;
     private TextField groupDescriptionField;

     private Table associatedUsersTable = new Table();
     private Table roleTable = new Table();

     private UserService userService;
     private SecurityService securityService;
     private SystemEventService systemEventService;

     private IkasanPrincipal principal;

     private Button addRoleButton;

     public GroupPanel(UserService userService, SecurityService securityService,
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

     @SuppressWarnings("deprecation")
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

         Label mappingConfigurationLabel = new Label("Group Profile");
         mappingConfigurationLabel.setStyleName(ValoTheme.LABEL_HUGE);
         gridLayout.addComponent(mappingConfigurationLabel, 0, 0, 1, 0);

         Label usernameLabel = new Label("Group name:");

         groupNameField.setWidth("65%");

         groupTypeField = new TextField();
         groupTypeField.setWidth("65%");
         groupTypeField.setNullRepresentation("");
         groupDescriptionField = new TextField();
         groupDescriptionField.setWidth("65%");
         groupDescriptionField.setNullRepresentation("");



         GridLayout formLayout = new GridLayout(2, 3);
         formLayout.setSpacing(true);
         formLayout.setWidth("100%");
         formLayout.setColumnExpandRatio(0, .1f);
         formLayout.setColumnExpandRatio(1, .8f);

         usernameLabel.setSizeUndefined();
         formLayout.addComponent(usernameLabel, 0, 0);
         formLayout.setComponentAlignment(usernameLabel, Alignment.MIDDLE_RIGHT);
         formLayout.addComponent(groupNameField, 1, 0);

         Label firstNameLabel = new Label("Group type:");
         firstNameLabel.setSizeUndefined();
         formLayout.addComponent(firstNameLabel, 0, 1);
         formLayout.setComponentAlignment(firstNameLabel, Alignment.MIDDLE_RIGHT);
         formLayout.addComponent(groupTypeField, 1, 1);

         Label surnameLabel = new Label("Description:");
         surnameLabel.setSizeUndefined();
         formLayout.addComponent(surnameLabel, 0, 2);
         formLayout.setComponentAlignment(surnameLabel, Alignment.MIDDLE_RIGHT);
         formLayout.addComponent(groupDescriptionField, 1, 2);


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

         this.roleTable.addContainerProperty("Ikasan Role", String.class, null);
         this.roleTable.setColumnExpandRatio("Ikasan Role", .3f);
         this.roleTable.addContainerProperty("Description", String.class, null);
         this.roleTable.setColumnExpandRatio("Description", .55f);
         this.roleTable.addContainerProperty("", Button.class, null);
         this.roleTable.setColumnExpandRatio("", .15f);
         this.roleTable.addStyleName("ikasan");
         this.roleTable.addStyleName(ValoTheme.TABLE_SMALL);
         this.roleTable.setCellStyleGenerator(new IkasanSmallCellStyleGenerator());
         this.roleTable.setSizeFull();
         
         this.associatedUsersTable.addContainerProperty("Username", String.class, null);
         this.associatedUsersTable.setColumnExpandRatio("Username", .10f);
         this.associatedUsersTable.addContainerProperty("Firstname", String.class, null);
         this.associatedUsersTable.setColumnExpandRatio("Firstname", .20f);
         this.associatedUsersTable.addContainerProperty("Lastname", String.class, null);
         this.associatedUsersTable.setColumnExpandRatio("Lastname", .20f);
         this.associatedUsersTable.addContainerProperty("Email", String.class, null);
         this.associatedUsersTable.setColumnExpandRatio("Email", .20f);
         this.associatedUsersTable.addContainerProperty("Department", String.class, null);
         this.associatedUsersTable.setColumnExpandRatio("Department", .40f);
         this.associatedUsersTable.addStyleName("ikasan");
         this.associatedUsersTable.addStyleName(ValoTheme.TABLE_SMALL);
         this.associatedUsersTable.setCellStyleGenerator(new IkasanSmallCellStyleGenerator());
         this.associatedUsersTable.setSizeFull();

         this.associatedUsersTable.addItemClickListener(new ItemClickEvent.ItemClickListener()
         {
             @Override
             public void itemClick(ItemClickEvent itemClickEvent)
             {
                 if(itemClickEvent.isDoubleClick())
                 {
                     User user = (User)itemClickEvent.getItemId();
                     UserLite userLite = new UserLite();
                     userLite.setDepartment(user.getDepartment());
                     userLite.setEmail(user.getEmail());
                     userLite.setFirstName(user.getFirstName());
                     userLite.setSurname(user.getSurname());
                     userLite.setUsername(user.getUsername());

                     ((Window)getParent()).close();

                     UserWindow window = new UserWindow(userService, securityService, systemEventService, userLite);
                     UI.getCurrent().addWindow(window);
                 }
             }
         });


         GridLayout tablesLayout = new GridLayout(2, 1);
         tablesLayout.setMargin(false);
         tablesLayout.setSizeFull();
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

                                    systemEventService.logSystemEvent(SystemEventConstants.DASHBOARD_USER_ROLE_CHANGED_CONSTANTS, action, principal.getName());
                                }
                           }
                       });
                   }
               });

         tablesLayout.addComponent(addRoleButton, 1, 0);
         tablesLayout.setComponentAlignment(addRoleButton, Alignment.MIDDLE_RIGHT);

         VerticalSplitPanel roleVpanel = new VerticalSplitPanel(tablesLayout
                 , this.roleTable);
         roleVpanel.setSizeFull();
         roleVpanel.setSplitPosition(40, Unit.PIXELS);
         roleVpanel.setLocked(true);

         tablesLayout = new GridLayout(2, 1);
         tablesLayout.setMargin(false);
         tablesLayout.setSizeFull();
         tablesLayout.setSpacing(true);

         Label ldapGroupsLabel = new Label("Associated Users");
         ldapGroupsLabel.setStyleName(ValoTheme.LABEL_HUGE);
         tablesLayout.addComponent(ldapGroupsLabel, 0, 0);

         VerticalSplitPanel userVpanel = new VerticalSplitPanel(tablesLayout
                 , this.associatedUsersTable);
         userVpanel.setSizeFull();
         userVpanel.setSplitPosition(40, Unit.PIXELS);
         userVpanel.setLocked(true);

         VerticalLayout rolesLayout = new VerticalLayout();
         rolesLayout.setSizeFull();
         rolesLayout.setMargin(true);
         rolesLayout.addComponent(roleVpanel);

         VerticalLayout userLayout = new VerticalLayout();
         userLayout.setSizeFull();
         userLayout.setMargin(true);
         userLayout.addComponent(userVpanel);

         VerticalSplitPanel tablesVpanel = new VerticalSplitPanel(rolesLayout
                 , userLayout);
         tablesVpanel.setSizeFull();
         tablesVpanel.setSplitPosition(50, Unit.PERCENTAGE);
         tablesVpanel.setLocked(true);

         VerticalSplitPanel holderVpanel = new VerticalSplitPanel(gridLayout
                 , tablesVpanel);
         holderVpanel.setSizeFull();
         holderVpanel.setSplitPosition(200, Unit.PIXELS);
         holderVpanel.setLocked(true);
         
         securityAdministrationPanel.setContent(holderVpanel);
         layout.addComponent(securityAdministrationPanel);

         this.setContent(layout);
     }


     public void enter(IkasanPrincipalLite principalLite)
     {
         final IkasanAuthentication authentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
                 .getAttribute(DashboardSessionValueConstants.USER);

         if(authentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY) ||
                 authentication.hasGrantedAuthority(SecurityConstants.GROUP_ADMINISTRATION_ADMIN)
                 || authentication.hasGrantedAuthority(SecurityConstants.GROUP_ADMINISTRATION_WRITE))
         {
             addRoleButton.setVisible(true);
         }
         else
         {
             addRoleButton.setVisible(false);
         }

         this.principal = this.securityService.findPrincipalByName(principalLite.getName());

         groupNameField.setValue(principalLite.getName());
         groupTypeField.setValue(principalLite.getType());
         groupDescriptionField.setValue(principalLite.getDescription());

         roleTable.removeAllItems();

         for (final Role role : this.principal.getRoles())
         {
             addRole(role);
         }

         associatedUsersTable.removeAllItems();

         List<User> users = this.securityService.getUsersAssociatedWithPrincipal(this.principal.getId());

         for(User user: users)
         {
             associatedUsersTable.addItem(new Object[]
                         { user.getUsername(), user.getFirstName(), user.getSurname(),
                            user.getEmail(), user.getDepartment()}, user);
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


                 IkasanAuthentication ikasanAuthentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
                         .getAttribute(DashboardSessionValueConstants.USER);

                 String action = "Role " + role.getName() + " removed by " + ikasanAuthentication.getName();

                 systemEventService.logSystemEvent(SystemEventConstants.DASHBOARD_USER_ROLE_CHANGED_CONSTANTS, action,principal.getName());
             }
         });

         final IkasanAuthentication authentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
                 .getAttribute(DashboardSessionValueConstants.USER);

         if(authentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY) ||
                 authentication.hasGrantedAuthority(SecurityConstants.GROUP_ADMINISTRATION_ADMIN)
                 || authentication.hasGrantedAuthority(SecurityConstants.GROUP_ADMINISTRATION_WRITE))
         {
             deleteButton.setVisible(true);
         }
         else
         {
             deleteButton.setVisible(false);
         }

         roleTable.addItem(new Object[]
                 { role.getName(), role.getDescription(), deleteButton}, role);
     }
 }
