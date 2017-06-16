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
 import org.ikasan.security.model.IkasanPrincipal;
 import org.ikasan.security.model.Policy;
 import org.ikasan.security.model.Role;
 import org.ikasan.security.model.UserLite;
 import org.ikasan.security.service.SecurityService;
 import org.ikasan.security.service.UserService;
 import org.ikasan.security.service.authentication.IkasanAuthentication;
 import org.ikasan.systemevent.service.SystemEventService;
 import org.tepi.filtertable.FilterTable;
 import org.vaadin.teemu.VaadinIcons;

 /**
  * @author CMI2 Development Team
  *
  */
 public class PolicyPanel extends Panel
 {
     private static final long serialVersionUID = 6005593259860222561L;

     private Logger logger = Logger.getLogger(PolicyPanel.class);

     private TextField nameField = new TextField();
     private TextArea descriptionField;

     private FilterTable roleTable = new FilterTable();

     private UserService userService;
     private SecurityService securityService;
     private SystemEventService systemEventService;

     private Policy policy;

     private IndexedContainer rolesContainer;


     public PolicyPanel(UserService userService, SecurityService securityService,
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

     protected IndexedContainer buildRoleContainer()
     {
         IndexedContainer cont = new IndexedContainer();

         cont.addContainerProperty("Ikasan Policy", String.class, null);
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


         nameField.setWidth("65%");

         descriptionField = new TextArea();
         descriptionField.setWidth("65%");
         descriptionField.setRows(5);
         descriptionField.setNullRepresentation("");


         GridLayout formLayout = new GridLayout(2, 5);
         formLayout.setSpacing(true);
         formLayout.setWidth("100%");
         formLayout.setColumnExpandRatio(0, .1f);
         formLayout.setColumnExpandRatio(1, .8f);

         Label mappingConfigurationLabel = new Label("Policy");
         mappingConfigurationLabel.setStyleName(ValoTheme.LABEL_HUGE);

         formLayout.addComponent(mappingConfigurationLabel);

         Label nameLabel = new Label("Name:");

         nameLabel.setSizeUndefined();
         formLayout.addComponent(nameLabel, 0, 1);
         formLayout.setComponentAlignment(nameLabel, Alignment.MIDDLE_RIGHT);
         formLayout.addComponent(nameField, 1, 1);

         Label descriptionLabel = new Label("Description:");
         descriptionLabel.setSizeUndefined();
         formLayout.addComponent(descriptionLabel, 0, 2);
         formLayout.setComponentAlignment(descriptionLabel, Alignment.TOP_RIGHT);
         formLayout.addComponent(descriptionField, 1, 2);

         this.rolesContainer = this.buildRoleContainer();

         this.roleTable.setContainerDataSource(this.rolesContainer);
         this.roleTable.setColumnExpandRatio("Ikasan Policy", .3f);
         this.roleTable.setColumnExpandRatio("Description", .55f);
         this.roleTable.setColumnExpandRatio("", .15f);
         this.roleTable.addStyleName("ikasan");
         this.roleTable.addStyleName(ValoTheme.TABLE_SMALL);
         this.roleTable.setCellStyleGenerator(new IkasanSmallCellStyleGenerator());
         this.roleTable.setSizeFull();
         this.roleTable.setFilterBarVisible(true);

         GridLayout tablesLayout = new GridLayout(2, 1);
         tablesLayout.setMargin(false);
         tablesLayout.setSizeFull();
         tablesLayout.setSpacing(true);

         Label ikasanRolesLabel = new Label("Ikasan Roles This Policy Is Association With");
         ikasanRolesLabel.setStyleName(ValoTheme.LABEL_HUGE);

         tablesLayout.addComponent(ikasanRolesLabel, 0, 0);

         final Button addPolicyButton = new Button("Associate with role");
         addPolicyButton.setStyleName(ValoTheme.BUTTON_SMALL);
         addPolicyButton.addClickListener(new Button.ClickListener()
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
                                Role role = window.getRole();

                                if(role != null)
                                {
                                    policy = securityService.getPolicyById(policy.getId());
                                    role = securityService.getRoleById(role.getId());

                                    if(policy.getRoles().contains(role))
                                    {
                                        Notification.show("The policy is already associated with this role!", Notification.Type.WARNING_MESSAGE);
                                        return;
                                    }

                                    role.getPolicies().add(policy);

                                    securityService.saveRole(role);

                                    IkasanAuthentication ikasanAuthentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
                                            .getAttribute(DashboardSessionValueConstants.USER);

                                    String action = "Policy " + policy.getName() + " added to role "
                                            + role.getName() + " by " + ikasanAuthentication.getName();
                                    systemEventService.logSystemEvent(SystemEventConstants.DASHBOARD_USER_ROLE_CHANGED_CONSTANTS, action, ikasanAuthentication.getName());

                                    addRole(role);
                                }
                           }
                       });
                   }
               });

         tablesLayout.addComponent(addPolicyButton, 1, 0);
         tablesLayout.setComponentAlignment(addPolicyButton, Alignment.MIDDLE_RIGHT);

         VerticalSplitPanel policyVpanel = new VerticalSplitPanel(tablesLayout
                 , this.roleTable);
         policyVpanel.setSizeFull();
         policyVpanel.setSplitPosition(40, Unit.PIXELS);
         policyVpanel.setLocked(true);

         tablesLayout = new GridLayout(2, 1);
         tablesLayout.setMargin(false);
         tablesLayout.setSizeFull();
         tablesLayout.setSpacing(true);


         VerticalLayout formLayoutLayout = new VerticalLayout();
         formLayoutLayout.setSizeFull();
         formLayoutLayout.setMargin(true);
         formLayoutLayout.addComponent(formLayout);

         VerticalLayout policyVpanelLayout = new VerticalLayout();
         policyVpanelLayout.setSizeFull();
         policyVpanelLayout.setMargin(true);
         policyVpanelLayout.addComponent(policyVpanel);

         VerticalSplitPanel topVpanel = new VerticalSplitPanel(formLayoutLayout
                 , policyVpanelLayout);
         topVpanel.setSizeFull();
         topVpanel.setSplitPosition(250, Unit.PIXELS);
         topVpanel.setLocked(true);


         HorizontalLayout holderLayout = new HorizontalLayout();
         holderLayout.setSizeFull();
         holderLayout.setMargin(true);
         holderLayout.addComponent(topVpanel);
         
         securityAdministrationPanel.setContent(holderLayout);
         layout.addComponent(securityAdministrationPanel);


         this.setContent(layout);
     }


     public void enter(Policy policy)
     {
         this.policy = securityService.getPolicyById(policy.getId());

         nameField.setValue(policy.getName());
         descriptionField.setValue(policy.getDescription());

         roleTable.removeAllItems();

         if (this.policy.getRoles() != null)
         {
             for (final Role role : this.policy.getRoles())
             {
                 addRole(role);
             }
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

                 policy.getRoles().remove(role);

                 securityService.savePolicy(policy);


                 IkasanAuthentication ikasanAuthentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
                         .getAttribute(DashboardSessionValueConstants.USER);

                 String action = "Policy " + policy.getName() + " removed from role "
                         + role.getName() + " by " + ikasanAuthentication.getName();

                 systemEventService.logSystemEvent(SystemEventConstants.DASHBOARD_USER_ROLE_CHANGED_CONSTANTS, action, ikasanAuthentication.getName());
             }
         });

         Item item = this.rolesContainer.addItem(role);

         item.getItemProperty("Ikasan Policy").setValue(role.getName());
         item.getItemProperty("Description").setValue(role.getDescription());
         item.getItemProperty("").setValue(deleteButton);
     }

 }
