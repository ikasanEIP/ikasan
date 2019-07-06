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

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Label;
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
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.spring.annotation.UIScope;
import org.ikasan.dashboard.ui.administration.component.UserDirectoryDialog;
import org.ikasan.dashboard.ui.layout.IkasanAppLayout;
import org.ikasan.security.model.AuthenticationMethod;
import org.ikasan.security.service.LdapService;
import org.ikasan.security.service.SecurityService;
import org.ikasan.security.service.authentication.AuthenticationProviderFactory;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.vaadin.teemu.VaadinIcons;

import javax.annotation.Resource;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;


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

    private AuthenticationProviderFactory<AuthenticationMethod> authenticationProviderFactory;
    private LdapService ldapService;
    private VerticalLayout mainLayout = new VerticalLayout();
    private Grid<AuthenticationMethod> directoryTable;
	private Button newDirectoryButton;

//	/**
//	 * Constructor
//	 *
//	 * @param securityService
//	 * @param authenticationProviderFactory
//	 * @param ldapService
//     */
//    public UserDirectoriesPanel(SecurityService securityService,
//                                AuthenticationProviderFactory<AuthenticationMethod> authenticationProviderFactory,
//                                LdapService ldapService)
//    {
//        super();
//        this.securityService = securityService;
//        if(this.securityService == null)
//        {
//        	throw new IllegalArgumentException("securityService cannot be null!");
//        }
//        this.authenticationProviderFactory = authenticationProviderFactory;
//        if(this.authenticationProviderFactory == null)
//        {
//        	throw new IllegalArgumentException("authenticationProviderFactory cannot be null!");
//        }
//        this.ldapService = ldapService;
//        if(this.ldapService == null)
//        {
//        	throw new IllegalArgumentException("ldapService cannot be null!");
//        }
//        init();
//    }

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
    	H2 userDirectories = new H2("User Directories");

        HorizontalLayout hl = new HorizontalLayout();
        hl.setWidth("100%");

        Icon icon = VaadinIcon.QUESTION_CIRCLE_O.create();
        icon.getStyle().set("marginRight", "10px");

        hl.add(icon);


        Text parapraphTwo = new Text(
            "The table below shows the user directories currently configured for Ikasan. The order of the directory is the order in which it will be searched for users and groups." +
            " It is recommended that each user exists in a single directory.");

        hl.add(parapraphTwo);

        this.mainLayout.setSizeFull();
//        this.mainLayout.setSpacing(true);

        add(userDirectories);
        add(hl);
//        this.mainLayout.add(parapraphTwo);

        newDirectoryButton = new Button("Add Directory");
        newDirectoryButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent ->
        {
            UserDirectoryDialog userDirectoryDialog = new UserDirectoryDialog(securityService, new AuthenticationMethod());
            userDirectoryDialog.open();
        });

        add(newDirectoryButton);

        this.setSizeFull();

        this.directoryTable = new Grid<>();
		this.directoryTable.setSizeFull();
		this.directoryTable.setClassName("my-grid");

        this.directoryTable.addColumn(AuthenticationMethod::getName).setHeader("Directory Name");
        this.directoryTable.addColumn(AuthenticationMethod::getMethod).setHeader("Type");
        this.directoryTable.addColumn(AuthenticationMethod::getOrder).setHeader("Order");
        this.directoryTable.addColumn(new ComponentRenderer<>(authenticationMethod ->
        {
            Button disable = new Button("Disable");
            Button edit = new Button("Edit");
            Button delete = new Button("Delete");
            Button test = new Button("Test");
            Button synchronise = new Button("Synchronise");

            HorizontalLayout layout = new HorizontalLayout();
            layout.add(disable, edit, delete, test, synchronise);

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

        })).setHeader("State");

		add(this.directoryTable);

//        this.add(mainLayout);
    }

//	/* (non-Javadoc)
//	 * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
//	 */
//	@Override
//	public void enter(ViewChangeListener.ViewChangeEvent event)
//	{
//		this.populateAll();
//	}

	protected void populateAll()
	{
		final IkasanAuthentication authentication = (IkasanAuthentication) VaadinService.getCurrentRequest().getWrappedSession()
	        	.getAttribute("USER");

		List<AuthenticationMethod> authenticationMethods = this.securityService.getAuthenticationMethods();

		this.directoryTable.setItems(new ArrayList());

		for(final AuthenticationMethod authenticationMethod: authenticationMethods)
		{
			this.populateDirectoryTable(authenticationMethod);
		}
	}

	protected void populateDirectoryTable(final AuthenticationMethod authenticationMethod)
	{
		Button test = new Button("Test");
//		test.setStyleName(BaseTheme.BUTTON_LINK);
		test.addClickListener(new ComponentEventListener<ClickEvent<Button>>()
        {
            @Override
            public void onComponentEvent(ClickEvent<Button> buttonClickEvent)
            {
                try
                {
                    authenticationProviderFactory.testAuthenticationConnection(authenticationMethod);
                }
                catch(RuntimeException e)
                {
                    logger.error("An error occurred testing an LDAP connection", e);

                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    e.printStackTrace(pw);

                    Notification.show("Error occurred while testing connection!");

                    return;
                }
                catch(Exception e)
                {
                    logger.error("An error occurred testing an LDAP connection", e);

                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    e.printStackTrace(pw);

                    Notification.show("Error occurred while testing connection!");

                    return;
                }

                Notification.show("Connection Successful!");
            }
        });

		final Button enableDisableButton = new Button();

		if(authenticationMethod.isEnabled())
		{
			enableDisableButton.setText("Disable");
		}
		else
		{
			enableDisableButton.setText("Enable");
		}
//		enableDisableButton.setStyleName(BaseTheme.BUTTON_LINK);
		enableDisableButton.addClickListener(new ComponentEventListener<ClickEvent<Button>>()
        {
            @Override
            public void onComponentEvent(ClickEvent<Button> buttonClickEvent)
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

                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    e.printStackTrace(pw);

                    Notification.show("Error trying to enable/disable the authentication method!");

                    return;
                }

                if(authenticationMethod.isEnabled())
                {
                    enableDisableButton.setText("Disable");
                    Notification.show("Enabled!");
                }
                else
                {
                    enableDisableButton.setText("Enable");
                    Notification.show("Disabled!");
                }
            }
        });


		Button delete = new Button("Delete");
//		delete.setStyleName(BaseTheme.BUTTON_LINK);
		delete.addClickListener(new ComponentEventListener<ClickEvent<Button>>()
        {
            @Override
            public void onComponentEvent(ClickEvent<Button> buttonClickEvent)
            {
                try
                {
                    securityService.deleteAuthenticationMethod(authenticationMethod);

                    List<AuthenticationMethod> authenticationMethods = securityService.getAuthenticationMethods();

                    directoryTable.setItems(new ArrayList());

                    long order = 1;

                    for(final AuthenticationMethod authenticationMethod: authenticationMethods)
                    {
                        authenticationMethod.setOrder(order++);
                        securityService.saveOrUpdateAuthenticationMethod(authenticationMethod);
                    }

                    populateAll();
                }
                catch(RuntimeException e)
                {
                    logger.error("An error occurred deleting an authentication method", e);

                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    e.printStackTrace(pw);

                    Notification.show("Error trying to delete the authentication method!");

                    return;
                }

                Notification.show("Deleted!");
            }
        });
//
//		Button edit = new Button("Edit");
//		edit.setStyleName(BaseTheme.BUTTON_LINK);
//		edit.addClickListener(new Button.ClickListener()
//        {
//            public void buttonClick(ClickEvent event)
//            {
//        		UserDirectoryManagementPanel authMethodPanel = new UserDirectoryManagementPanel(authenticationMethod,
//    					securityService, authenticationProviderFactory, ldapService);
//
//        		Window window = new Window("Configure User Directory");
//        		window.setModal(true);
//        		window.setHeight("90%");
//        		window.setWidth("90%");
//
//        		window.setContent(authMethodPanel);
//
//        		window.addCloseListener(new Window.CloseListener()
//        		{
//                    // inline close-listener
//                    public void windowClose(CloseEvent e)
//                    {
//                        populateAll();
//                    }
//                });
//
//        		UI.getCurrent().addWindow(window);
//            }
//        });
//
//		Button synchronise = new Button("Synchronise");
////		synchronise.setStyleName(BaseTheme.BUTTON_LINK);
//
//		synchronise.addClickListener(new Button.ClickListener()
//        {
//            public void buttonClick(ClickEvent event)
//            {
//            	try
//            	{
//            		ldapService.synchronize(authenticationMethod);
//
//            		authenticationMethod.setLastSynchronised(new Date());
//	            	securityService.saveOrUpdateAuthenticationMethod(authenticationMethod);
//
//	            	populateAll();
//            	}
//            	catch(UnexpectedRollbackException e)
//            	{
//            		StringWriter sw = new StringWriter();
//                    PrintWriter pw = new PrintWriter(sw);
//                    e.printStackTrace(pw);
//
//                    logger.error("Most specific cause: " + e.getMostSpecificCause());
//                    e.getMostSpecificCause().printStackTrace();
//                    logger.error("Most specific cause: " + e.getRootCause());
//                    e.getRootCause().printStackTrace();
//
//                    Notification.show("Error occurred while synchronizing!", sw.toString()
//                        , Notification.Type.ERROR_MESSAGE);
//
//                    return;
//            	}
//            	catch(RuntimeException e)
//            	{
//            		logger.error("An error occurred synchronising an LDAP repository", e);
//
//            		StringWriter sw = new StringWriter();
//                    PrintWriter pw = new PrintWriter(sw);
//                    e.printStackTrace(pw);
//
//                    Notification.show("Error occurred while synchronizing!", sw.toString()
//                        , Notification.Type.ERROR_MESSAGE);
//
//                    return;
//            	}
//            	catch(Exception e)
//            	{
//            		logger.error("An error occurred synchronising an LDAP repository", e);
//
//            		StringWriter sw = new StringWriter();
//                    PrintWriter pw = new PrintWriter(sw);
//                    e.printStackTrace(pw);
//
//                    Notification.show("Error occurred while synchronizing!", sw.toString()
//                        , Notification.Type.ERROR_MESSAGE);
//
//                    return;
//            	}
//
//                Notification.show("Synchronized!");
//            }
//        });
//
//		VerticalLayout operationsLayout = new VerticalLayout(9, 2);
//		operationsLayout.setWidth("250px");
//		operationsLayout.add(enableDisableButton);
//		operationsLayout.add(new Label(" "));
//		operationsLayout.add(edit, 2, 0);
//		operationsLayout.add(new Label(" "));
//		operationsLayout.add(delete, 4, 0);
//		operationsLayout.add(new Label(" "));
//		operationsLayout.add(test);
//		operationsLayout.add(new Label(" "));
//		operationsLayout.add(synchronise);
//
//		TextArea synchronisedTextArea = new TextArea();
////		synchronisedTextArea.setRows(3);
////		synchronisedTextArea.setWordwrap(true);
//
//		if(authenticationMethod.getLastSynchronised() != null)
//		{
//			synchronisedTextArea.setValue("This directory was last synchronised at " + authenticationMethod.getLastSynchronised());
//		}
//		else
//		{
//			synchronisedTextArea.setValue("This directory has not been synchronised");
//		}
//
//		synchronisedTextArea.setSizeFull();
//		synchronisedTextArea.setReadOnly(true);
//
//		operationsLayout.add(synchronisedTextArea);
//
//		HorizontalLayout orderLayout = new HorizontalLayout();
//		orderLayout.setWidth("50%");
//
//		Button upArrow = new Button(VaadinIcon.ARROW_UP.create());
//		if(authenticationMethod.getOrder() != 1)
//		{
////			upArrow.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
////			upArrow.addStyleName(ValoTheme.BUTTON_BORDERLESS);BUTTON_BORDERLESS
//			upArrow.addClickListener(new ComponentEventListener<ClickEvent<Button>>()
//            {
//                @Override
//                public void onComponentEvent(ClickEvent<Button> buttonClickEvent)
//                {
//                    if(authenticationMethod.getOrder() != 1)
//                    {
//                        AuthenticationMethod upAuthMethod = securityService.getAuthenticationMethodByOrder(authenticationMethod.getOrder() -1);
//
//                        upAuthMethod.setOrder(authenticationMethod.getOrder());
//                        authenticationMethod.setOrder(authenticationMethod.getOrder() - 1);
//
//                        securityService.saveOrUpdateAuthenticationMethod(upAuthMethod);
//                        securityService.saveOrUpdateAuthenticationMethod(authenticationMethod);
//
//                        populateAll();
//                    }
//                }
//            });
//
//			orderLayout.add(upArrow);
//		}
//
//
//		long numberOfAuthMethods = securityService.getNumberOfAuthenticationMethods();
//
//		Button downArrow = new Button(VaadinIcons.ARROW_DOWN);
//		if(authenticationMethod.getOrder() != numberOfAuthMethods)
//		{
//			downArrow.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
//			downArrow.addStyleName(ValoTheme.BUTTON_BORDERLESS);
//			downArrow.addClickListener(new Button.ClickListener()
//	        {
//	            public void buttonClick(ClickEvent event)
//	            {
//	            	long numberOfAuthMethods = securityService.getNumberOfAuthenticationMethods();
//
//	            	if(authenticationMethod.getOrder() != numberOfAuthMethods)
//	        		{
//	        			AuthenticationMethod downAuthMethod = securityService.getAuthenticationMethodByOrder(authenticationMethod.getOrder()  + 1);
//
//	        			downAuthMethod.setOrder(authenticationMethod.getOrder());
//	        			authenticationMethod.setOrder(authenticationMethod.getOrder() + 1);
//
//	        			securityService.saveOrUpdateAuthenticationMethod(downAuthMethod);
//	        			securityService.saveOrUpdateAuthenticationMethod(authenticationMethod);
//
//	        			populateAll();
//	        		}
//	            }
//	        });
//
//			orderLayout.addComponent(downArrow);
//		}
//
//		final IkasanAuthentication authentication = (IkasanAuthentication)VaadinService.getCurrentRequest().getWrappedSession()
//				.getAttribute(DashboardSessionValueConstants.USER);
//
//
//		if(authentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY) ||
//				authentication.hasGrantedAuthority(SecurityConstants.USER_DIRECTORY_ADMIN))
//		{
//			test.setVisible(true);
//			enableDisableButton.setVisible(true);
//			delete.setVisible(true);
//			edit.setVisible(true);
//			synchronise.setVisible(true);
//			upArrow.setVisible(true);
//			downArrow.setVisible(true);
//			newDirectoryButton.setVisible(true);
//		}
//		else if(authentication.hasGrantedAuthority(SecurityConstants.USER_DIRECTORY_WRITE))
//		{
//			test.setVisible(true);
//			enableDisableButton.setVisible(true);
//			synchronise.setVisible(true);
//			upArrow.setVisible(true);
//			downArrow.setVisible(true);
//
//			delete.setVisible(false);
//			edit.setVisible(false);
//			newDirectoryButton.setVisible(false);
//		}
//		else
//		{
//			test.setVisible(false);
//			enableDisableButton.setVisible(false);
//			delete.setVisible(false);
//			edit.setVisible(false);
//			synchronise.setVisible(false);
//			upArrow.setVisible(false);
//			downArrow.setVisible(false);
//			newDirectoryButton.setVisible(false);
//		}
//
//		this.directoryTable.addItem(new Object[]{authenticationMethod.getName(), "Microsoft Active Directory"
//				, orderLayout, operationsLayout}, authenticationMethod);
	}

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent)
    {
        logger.info("before enter");
        List<AuthenticationMethod> authenticationMethods = this.securityService.getAuthenticationMethods();

        this.directoryTable.setItems(authenticationMethods);
    }
}
