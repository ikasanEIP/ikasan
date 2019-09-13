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
package org.ikasan.dashboard.ui.administration.view;

import com.github.appreciated.app.layout.component.appbar.IconButton;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.dialog.GeneratedVaadinDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import org.ikasan.dashboard.ui.administration.component.UserDirectoryDialog;
import org.ikasan.dashboard.ui.component.NotificationHelper;
import org.ikasan.dashboard.ui.general.component.ProgressIndicatorDialog;
import org.ikasan.dashboard.ui.layout.IkasanAppLayout;
import org.ikasan.dashboard.ui.util.SecurityConstants;
import org.ikasan.security.model.AuthenticationMethod;
import org.ikasan.security.service.LdapService;
import org.ikasan.security.service.SecurityService;
import org.ikasan.security.service.authentication.AuthenticationProviderFactory;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


/**
 * 
 * @author Ikasan Development Team
 *
 */
@Route(value = "userDirectories", layout = IkasanAppLayout.class)
@UIScope
@Component
public class UserDirectoriesView extends VerticalLayout implements BeforeEnterObserver
{
	private Logger logger = LoggerFactory.getLogger(UserDirectoriesView.class);

    @Resource
    private SecurityService securityService;

    @Resource
    private LdapService ldapService;

    @Resource
    private AuthenticationProviderFactory<AuthenticationMethod> authenticationProviderFactory;

    private Grid<AuthenticationMethod> directoryTable;
    private Button newDirectoryButton;
    private IkasanAuthentication authentication;


    /**
     * Constructor

     */
    public UserDirectoriesView()
    {
        super();
        init();
    }

    protected void init()
    {
        this.authentication = (IkasanAuthentication)SecurityContextHolder.getContext().getAuthentication();

    	H2 userDirectories = new H2("User Directories");

        HorizontalLayout hl = new HorizontalLayout();
        hl.setWidth("100%");

        Icon icon = VaadinIcon.QUESTION_CIRCLE_O.create();
        icon.getStyle().set("marginRight", "10px");

        hl.add(icon);


        Text instructions = new Text(
            "The table below shows the user directories currently configured for Ikasan. The order of the directory is the order in which it will be searched for users and groups." +
            " It is recommended that each user exists in a single directory.");

        hl.add(instructions);

        add(userDirectories);
        add(hl);

        newDirectoryButton = new Button("Add Directory");
        newDirectoryButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent
            -> openUserDirectoryDialog(new AuthenticationMethod()));

        applyAdminVisibilitySecurity(newDirectoryButton);

        add(newDirectoryButton);

        this.setSizeFull();

        this.directoryTable = new Grid<>();
		this.directoryTable.setSizeFull();
		this.directoryTable.setClassName("my-grid");

        this.directoryTable.addColumn(AuthenticationMethod::getName).setHeader("Directory Name").setFlexGrow(3);
        this.directoryTable.addColumn(AuthenticationMethod::getMethod).setHeader("Type").setFlexGrow(3);
        this.directoryTable.addColumn(new ComponentRenderer<>(authenticationMethod ->
        {
            HorizontalLayout layout = new HorizontalLayout();

            Button upArrow = new Button(VaadinIcon.ARROW_UP.create());
            upArrow.getStyle().set("width", "40px");
            upArrow.getStyle().set("height", "40px");
            upArrow.getStyle().set("font-size", "16pt");
            if(authenticationMethod.getOrder() != 1)
            {
                upArrow.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent ->
                {
                    if(authenticationMethod.getOrder() != 1)
                    {
                        AuthenticationMethod upAuthMethod = securityService.getAuthenticationMethodByOrder(authenticationMethod.getOrder() -1);

                        upAuthMethod.setOrder(authenticationMethod.getOrder());
                        authenticationMethod.setOrder(authenticationMethod.getOrder() - 1);

                        securityService.saveOrUpdateAuthenticationMethod(upAuthMethod);
                        securityService.saveOrUpdateAuthenticationMethod(authenticationMethod);

                        populateAll();
                    }
                });

                layout.add(upArrow);
                applyWriteVisibilitySecurity(upArrow);
            }

            long numberOfAuthMethods = securityService.getNumberOfAuthenticationMethods();

            Button downArrow = new Button(VaadinIcon.ARROW_DOWN.create());
            downArrow.getStyle().set("width", "40px");
            downArrow.getStyle().set("height", "40px");
            downArrow.getStyle().set("font-size", "16pt");
            if(authenticationMethod.getOrder() != numberOfAuthMethods)
            {
                downArrow.addClickListener((ComponentEventListener<ClickEvent<Button>>) divClickEvent ->
                {
                    if(authenticationMethod.getOrder() != numberOfAuthMethods)
                    {
                        AuthenticationMethod downAuthMethod = securityService.getAuthenticationMethodByOrder(authenticationMethod.getOrder()  + 1);

                        downAuthMethod.setOrder(authenticationMethod.getOrder());
                        authenticationMethod.setOrder(authenticationMethod.getOrder() + 1);

                        securityService.saveOrUpdateAuthenticationMethod(downAuthMethod);
                        securityService.saveOrUpdateAuthenticationMethod(authenticationMethod);

                        populateAll();
                    }
                });

                layout.add(downArrow);
                applyWriteVisibilitySecurity(downArrow);
            }

            return layout;

        })).setHeader("Order").setFlexGrow(1);
        this.directoryTable.addColumn(new ComponentRenderer<>(authenticationMethod ->
        {
            HorizontalLayout layout = new HorizontalLayout();
            createUserDirectoryManagementCell(authenticationMethod, layout);

            TextArea synchronisedTextArea = new TextArea();
            synchronisedTextArea.setSizeFull();
            if(authenticationMethod.getLastSynchronised() != null)
            {
                synchronisedTextArea.setValue("This directory was last synchronised at " + authenticationMethod.getLastSynchronised());
            }
            else
            {
                synchronisedTextArea.setValue("This directory has not been synchronised");
            }

            VerticalLayout verticalLayout = new VerticalLayout();
            verticalLayout.setSizeFull();
            verticalLayout.add(layout, synchronisedTextArea);

            return verticalLayout;

        })).setHeader("State").setFlexGrow(6);

		add(this.directoryTable);
    }


    /**
     * Populate the view components.
     */
	protected void populateAll()
	{
		List<AuthenticationMethod> authenticationMethods = this.securityService.getAuthenticationMethods();

        this.directoryTable.setItems(authenticationMethods);
	}

    /**
     * Helper method to create the user durectory management grid cell.
     *
     * @param authenticationMethod the relevant authentication method.
     * @param layout the layout to add the cell elements to.
     */
	protected void createUserDirectoryManagementCell(final AuthenticationMethod authenticationMethod, HorizontalLayout layout)
	{
        Button disable = new Button("Disable");
        Button edit = new Button("Edit");
        Button delete = new Button("Delete");
        Button test = new Button("Test");
        Button synchronise = new Button("Synchronise");

        layout.add(disable, edit, delete, test, synchronise);

        applyWriteVisibilitySecurity(disable, edit, delete, test, synchronise);

		test.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent ->
        {
            try
            {
                authenticationProviderFactory.testAuthenticationConnection(authenticationMethod);
            }
            catch(Exception e)
            {
                logger.error("An error occurred testing an LDAP connection", e);

                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);

                Notification.show("Error occurred while testing connection! " + e, 3000, Notification.Position.MIDDLE);

                return;
            }

            Notification.show("Connection Successful!", 3000, Notification.Position.MIDDLE);
        });


		if(authenticationMethod.isEnabled())
		{
			disable.setText("Disable");
		}
		else
		{
			disable.setText("Enable");
		}

        disable.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent ->
        {
            try
            {
                if(authenticationMethod.isEnabled())
                {
                    authenticationMethod.setEnabled(false);
                }
                else
                {
                    authenticationMethod.setEnabled(true);
                }

                securityService.saveOrUpdateAuthenticationMethod(authenticationMethod);

                populateAll();
            }
            catch(RuntimeException e)
            {
                logger.error("An error occurred saving an authentication method", e);

                Notification.show("Error trying to enable/disable the authentication method!");

                return;
            }

            if(authenticationMethod.isEnabled())
            {
                disable.setText("Disable");
                Notification.show("Enabled!", 3000, Notification.Position.MIDDLE);
            }
            else
            {
                disable.setText("Enable");
                Notification.show("Disabled!", 3000, Notification.Position.MIDDLE);
            }
        });


		delete.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent ->
        {
            try
            {
                securityService.deleteAuthenticationMethod(authenticationMethod);

                List<AuthenticationMethod> authenticationMethods = securityService.getAuthenticationMethods();

                directoryTable.setItems(new ArrayList());

                long order = 1;

                for(final AuthenticationMethod authenticationMethod1 : authenticationMethods)
                {
                    authenticationMethod1.setOrder(order++);
                    securityService.saveOrUpdateAuthenticationMethod(authenticationMethod1);
                }

                populateAll();
            }
            catch(RuntimeException e)
            {
                logger.error("An error occurred deleting an authentication method", e);

                Notification.show("Error trying to delete the authentication method!" , 3000, Notification.Position.MIDDLE);

                return;
            }

            Notification.show("Deleted!");
        });


		edit.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent -> openUserDirectoryDialog(authenticationMethod));


		synchronise.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent ->
        {
            ProgressIndicatorDialog progressIndicatorDialog = new ProgressIndicatorDialog();

            progressIndicatorDialog.open("Synchronising User Directory");

            final UI current = UI.getCurrent();
            Executor executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                try
                {
                    ldapService.synchronize(authenticationMethod);

                    authenticationMethod.setLastSynchronised(new Date());
                    securityService.saveOrUpdateAuthenticationMethod(authenticationMethod);

                    current.access(() ->
                    {
                        populateAll();
                        progressIndicatorDialog.close();
                        NotificationHelper.showUserNotification("User directory synchronisation complete.");
                    });
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                    current.access(() ->
                    {
                        progressIndicatorDialog.close();
                        NotificationHelper.showErrorNotification("Error occurred while synchronizing! " + e.getLocalizedMessage());
                    });

                    return;
                }
            });
        });

	}

    /**
     * Helper to deal with page write authorities.
     *
     * @param components
     */
	protected void applyWriteVisibilitySecurity(com.vaadin.flow.component.Component... components)
    {
        for(com.vaadin.flow.component.Component component: components)
        {
            if(authentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY) ||
                authentication.hasGrantedAuthority(SecurityConstants.USER_DIRECTORY_ADMIN) ||
                authentication.hasGrantedAuthority(SecurityConstants.USER_DIRECTORY_WRITE))
            {
                component.setVisible(true);
            }
            else
            {
                component.setVisible(false);
            }
        }
    }

    /**
     * Helper to deal with page admin authorities.
     *
     * @param components
     */
    protected void applyAdminVisibilitySecurity(com.vaadin.flow.component.Component... components)
    {
        for(com.vaadin.flow.component.Component component: components)
        {
            if(authentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY) ||
                authentication.hasGrantedAuthority(SecurityConstants.USER_DIRECTORY_ADMIN))
            {
                component.setVisible(true);
            }
            else
            {
                component.setVisible(false);
            }
        }
    }

	protected void openUserDirectoryDialog(AuthenticationMethod authenticationMethod)
    {
        UserDirectoryDialog userDirectoryDialog = new UserDirectoryDialog(securityService, authenticationMethod);
        userDirectoryDialog.open();

        userDirectoryDialog.addOpenedChangeListener((ComponentEventListener<GeneratedVaadinDialog.OpenedChangeEvent<Dialog>>) dialogOpenedChangeEvent ->
        {
            if(!dialogOpenedChangeEvent.isOpened())
            {
                populateAll();
            }
        });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent)
    {
        logger.info("before enter");
        this.authentication = (IkasanAuthentication)SecurityContextHolder.getContext().getAuthentication();
        populateAll();
    }
}
