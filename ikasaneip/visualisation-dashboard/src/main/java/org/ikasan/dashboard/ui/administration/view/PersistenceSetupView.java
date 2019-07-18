package org.ikasan.dashboard.ui.administration.view;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import org.ikasan.dashboard.ui.administration.component.PersistenceStatusDialog;
import org.ikasan.dashboard.ui.layout.PersistenceSetupLayout;
import org.ikasan.security.model.User;
import org.ikasan.security.service.AuthenticationService;
import org.ikasan.security.service.UserService;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.setup.persistence.service.PersistenceService;
import org.ikasan.setup.persistence.service.PersistenceServiceException;
import org.ikasan.spec.configuration.Configuration;
import org.ikasan.spec.configuration.ConfigurationManagement;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.PrintWriter;
import java.io.StringWriter;

@Route(value = "persistenceSetup", layout = PersistenceSetupLayout.class)
@UIScope
@Component
public class PersistenceSetupView extends VerticalLayout implements BeforeEnterObserver
{
    private Logger logger = LoggerFactory.getLogger(PersistenceSetupView.class);

    @Resource
    private PersistenceService persistenceService;

    @Resource
    private UserService userService;

    @Resource
    private AuthenticationService authenticationService;

    @Resource
    private ConfigurationManagement<ConfiguredResource, Configuration> configurationManagement;


    private static final String INSTALL = "INSTALL";
    private static final String UPGRADE = "UPGRADE";
    private static final String FILE_TRANSFER = "FILE_TRANSFER";

    private ComboBox persistanceStoreTypeCombo = new ComboBox("Select action");


    private Button fullInstallStatusButton = new Button();
    private Button upgradeInstallStatusButton = new Button();
    private Button fileTransferStatusButton = new Button();
    private IkasanAuthentication ikasanAuthentication;

//    /**
//     * Constructor
//     *
//     * @param persistenceService
//     * @param userService
//     * @param authenticationService
//     * @param configurationManagement
//     */
//    public PersistanceSetupPanel(org.ikasan.setup.persistence.service.PersistenceService persistenceService,
//                                 UserService userService, AuthenticationService authenticationService,
//                                 ConfigurationManagement<ConfiguredResource, Configuration> configurationManagement)
//    {
//        super();
//        this.persistenceService = persistenceService;
//        this.userService = userService;
//        this.authenticationService = authenticationService;
//        this.configurationManagement = configurationManagement;
//        init();
//    }


    public PersistenceSetupView()
    {
        super();
    }

    protected void init()
    {
        initPersistanceStoreTypeCombo();

        this.createOptionsView();
    }

    protected void createOptionsView()
    {
        FormLayout layout = new FormLayout();
        layout.setSizeFull();
//        layout.setWidth("100%");
//        layout.setHeight("100%");



        Text ikasanWelcomeLabel2 = new Text("Welcome to Ikasan setup. There are a number of options available to you.");

        Span ikasanWelcomeLabel3 = new Span("Full Installation. You are installing Ikasan for the first time. " +
            "If this is the case please select the full install option.");



        fullInstallStatusButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent ->
        {
            try
            {
                String status = persistenceService.getBaselineStatus();

                PersistenceStatusDialog dialog = new PersistenceStatusDialog(status);
                dialog.open();
            }
            catch(Exception ex)
            {
                ex.printStackTrace();

                Notification.show("Error trying to determine if changes are required to the Ikasan database!");

                return;
            }
        });

        Span ikasanWelcomeLabel4 = new Span("Upgrade. You are upgrading from a previous version of Ikasan. " +
            "If this is the case please select the upgrade option.");

        upgradeInstallStatusButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent ->
        {
            try
            {
                String status = persistenceService.getPostBaselineStatus();

                PersistenceStatusDialog dialog = new PersistenceStatusDialog(status);
                dialog.open();
            }
            catch(Exception ex)
            {
                ex.printStackTrace();

                Notification.show("Error trying to determine if changes are required to the Ikasan database!");

                return;
            }
        });

        Span ikasanWelcomeLabel5 = new Span("Provision File Transfer. You wish to provision Ikasan to provide file transfer funtionality. " +
            "If this is the case please select the install file transfer option.");


        fileTransferStatusButton.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent ->
        {
            try
            {
                String status = persistenceService.getFileTransferStatus();

                PersistenceStatusDialog dialog = new PersistenceStatusDialog(status);
                dialog.open();
            }
            catch(Exception ex)
            {
                ex.printStackTrace();

                Notification.show("Error trying to determine if changes are required to the Ikasan database!");

                return;
            }
        });


        this.updateIcons();

        add(ikasanWelcomeLabel2);
        FlexLayout h1 = new FlexLayout();
        h1.add(ikasanWelcomeLabel3, fullInstallStatusButton);
        layout.add(h1, new Div());
        h1 = new FlexLayout();
        h1.add(ikasanWelcomeLabel4, upgradeInstallStatusButton);
        layout.add(h1, new Div());
        h1 = new FlexLayout();
        h1.add(ikasanWelcomeLabel5, fileTransferStatusButton);
        layout.add(h1, new Div());

        layout.add(persistanceStoreTypeCombo);
        persistanceStoreTypeCombo.setHeight("30px");
        persistanceStoreTypeCombo.setWidth("50%");

        Button button = new Button("Create");
        button.setHeight("30px");

        button.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent ->
        {
            User user = null;

            try
            {
                user = userService.loadUserByUsername("admin");
            }
            catch(Exception e)
            {
                // ignore this as the database may not have been set up yet.
            }


            if(persistanceStoreTypeCombo.getValue() == null)
            {
                Notification.show("Please select an action.");
            }
            else if(persistanceStoreTypeCombo.getValue().equals("Full Installation"))
            {
                if(user != null)
                {
                    if(!baselinePersistenceChangesRequired())
                    {
                        Notification.show("Your database is already upgraded to the latest version! No changes required.");

                        return;
                    }

                    createFull();
                }
                else
                {
                    createFull();
                }
            }
            else if(persistanceStoreTypeCombo.getValue().equals("Upgrade"))
            {
                if(!postBaselinePersistenceChangesRequired())
                {
                    Notification.show("Your database is already upgraded to the latest version! No changes required.");

                    return;
                }

                upgrade();
            }
            else if(persistanceStoreTypeCombo.getValue().equals("Provision File Transfer"))
            {
                if(!fileTransferPersistenceChangesRequired())
                {
                    Notification.show("Your database is already provisioned to support file transfer! No changes required.");

                    return;
                }

                installFileTransfer();
            }

            ikasanAuthentication = null;

        });

        layout.add(button);

        this.add(layout);
    }

    protected void createFull()
    {
        if(ikasanAuthentication != null)
        {
//            final AdminPasswordDialog adminPasswordDialog
//                = new AdminPasswordDialog();
//
//            UI.getCurrent().addWindow(adminPasswordDialog);
//
//            adminPasswordDialog.addCloseListener(new Window.CloseListener()
//            {
//                // inline close-listener
//                public void windowClose(Window.CloseEvent e)
//                {
//                    String password = adminPasswordDialog.getPassword();
//
//                    String persistenceProvider = (String)PersistanceSetupPanel
//                        .this.persistanceStoreTypeCombo.getValue();
//
//                    if(persistenceProvider == null)
//                    {
//                        Notification.show("Please select a database type!");
//                        return;
//                    }
//
//                    try
//                    {
//                        persistenceService.createBaselinePersistence();
//
//                        userService.changeUsersPassword("admin", password, password);
//
//                        PlatformConfigurationConfiguredResource platformConfigurationConfiguredResource = new PlatformConfigurationConfiguredResource();
//
//                        PlatformConfiguration platformConfiguration = new PlatformConfiguration();
//                        platformConfiguration.setWebServiceUserAccount("admin");
//                        platformConfiguration.setWebServiceUserPassword(password);
//
//                        platformConfigurationConfiguredResource.setConfiguration(platformConfiguration);
//
//                        Configuration configuration = configurationManagement.createConfiguration(platformConfigurationConfiguredResource);
//
//                        configurationManagement.saveConfiguration(configuration);
//
//                        updateIcons();
//                    }
//                    catch(Exception ex)
//                    {
//                        StringWriter sw = new StringWriter();
//                        PrintWriter pw = new PrintWriter(sw);
//                        ex.printStackTrace(pw);
//
//                        Notification.show("Error trying to create Ikasan database!");
//
//                        return;
//                    }
//
//                    Notification.show("Database successfully created!");
//                }
//            });
        }
        else
        {
            try
            {
//                PlatformConfiguration platformConfiguration = new PlatformConfiguration();
//                platformConfiguration.setWebServiceUserAccount("admin");
//                platformConfiguration.setWebServiceUserPassword(password);
                persistenceService.createBaselinePersistence();

                userService.changeUsersPassword("admin", "admin", "admin");

                this.updateIcons();
            }
            catch(Exception ex)
            {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                ex.printStackTrace(pw);

                Notification.show("Error trying to create Ikasan database!");

                return;
            }

            Notification.show("Database successfully created!");
        }
    }

    protected void upgrade()
    {
        try
        {
            persistenceService.createPostBaselinePersistence();

            this.updateIcons();
        }
        catch(Exception ex)
        {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);

            Notification.show("Error trying to upgrade the Ikasan database!");

            return;
        }

        Notification.show("Database successfully upgraded!");
    }

    protected void installFileTransfer()
    {
        try
        {
            persistenceService.createFileTransferPersistence();

            this.updateIcons();
        }
        catch(Exception ex)
        {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);

            Notification.show("Error trying to provision the file transfer tables in the Ikasan database!");

            return;
        }

        Notification.show("Database successfully provisioned to support transactional file transfer!");
    }

    protected void initPersistanceStoreTypeCombo()
    {
        persistanceStoreTypeCombo = new ComboBox("Please Select","Full Installation", "Upgrade", "Provision File Transfer");
    }


    public void updateIcons()
    {
        if(this.baselinePersistenceChangesRequired())
        {
            fullInstallStatusButton.setIcon(VaadinIcon.UPLOAD.create());
        }
        else
        {
            fullInstallStatusButton.setIcon(VaadinIcon.CHECK.create());
        }

        if(this.postBaselinePersistenceChangesRequired())
        {
            upgradeInstallStatusButton.setIcon(VaadinIcon.UPLOAD.create());
        }
        else
        {
            upgradeInstallStatusButton.setIcon(VaadinIcon.CHECK.create());
        }

        if(this.fileTransferPersistenceChangesRequired())
        {
            fileTransferStatusButton.setIcon(VaadinIcon.UPLOAD.create());
        }
        else
        {
            fileTransferStatusButton.setIcon(VaadinIcon.CHECK.create());
        }
    }

    private boolean baselinePersistenceChangesRequired()
    {
        try
        {
            return persistenceService.baselinePersistenceChangesRequired();
        }
        catch (PersistenceServiceException e)
        {
            logger.error("Unable to determine if baseline changes required!", e);
        }

        return false;
    }

    private boolean postBaselinePersistenceChangesRequired()
    {
        try
        {
            return persistenceService.postBaselinePersistenceChangesRequired();
        }
        catch (PersistenceServiceException e)
        {
            logger.error("Unable to determine if post baseline changes required!", e);
        }

        return false;
    }

    private boolean fileTransferPersistenceChangesRequired()
    {
        try
        {
            return persistenceService.fileTransferPersistenceChangesRequired();
        }
        catch (PersistenceServiceException e)
        {
            logger.error("Unable to determine if file transfer changes required!", e);
        }

        return false;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent)
    {
        this.init();
    }
}
