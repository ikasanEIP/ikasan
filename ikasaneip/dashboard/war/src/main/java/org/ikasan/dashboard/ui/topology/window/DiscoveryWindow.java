package org.ikasan.dashboard.ui.topology.window;

import com.vaadin.server.VaadinService;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.ikasan.dashboard.discovery.DiscoverySchedulerService;
import org.ikasan.dashboard.ui.framework.util.DashboardSessionValueConstants;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.vaadin.teemu.VaadinIcons;

/**
 * Created by stewmi on 25/05/2017.
 */
public class DiscoveryWindow extends Window
{
    private DiscoverySchedulerService discoverySchedulerService;

    public DiscoveryWindow(DiscoverySchedulerService discoverySchedulerService)
    {
        super("Discovery");
        this.discoverySchedulerService = discoverySchedulerService;
        this.discoverySchedulerService = discoverySchedulerService;
        if(this.discoverySchedulerService == null)
        {
            throw new IllegalArgumentException("discoverySchedulerService cannot be null!");
        }

        init();
    }

    public void init()
    {
        this.setWidth("300px");
        this.setHeight("200px");
        this.setModal(true);

        Panel panel = new Panel();
        panel.setSizeFull();
        panel.addStyleName(ValoTheme.PANEL_BORDERLESS);

        GridLayout layout = new GridLayout(2, 3);
        layout.setWidth("100%");
        layout.setSpacing(true);
        layout.setMargin(true);

        HorizontalLayout headingLayout = new HorizontalLayout();
        headingLayout.setWidth("100%");
        headingLayout.setHeight("40px");
        headingLayout.setSpacing(true);

        Label discoveryLabel = new Label("Module Discovery");
        discoveryLabel.setStyleName(ValoTheme.LABEL_HUGE);

        Button refreshButton = new Button();
        refreshButton.setIcon(VaadinIcons.REFRESH);
        refreshButton.setStyleName(ValoTheme.BUTTON_ICON_ONLY);
        refreshButton.setStyleName(ValoTheme.BUTTON_BORDERLESS);

        headingLayout.addComponent(discoveryLabel);
        headingLayout.setComponentAlignment(discoveryLabel, Alignment.MIDDLE_LEFT);
        headingLayout.addComponent(refreshButton);
        headingLayout.setComponentAlignment(refreshButton, Alignment.MIDDLE_LEFT);

        layout.addComponent(headingLayout, 0, 0, 1, 0);

        final Label statusLabel = new Label("Status");
        final Label stateLabel = new Label();

        if(discoverySchedulerService.isRunnung())
        {
            stateLabel.setValue("Running");
        }
        else
        {
            stateLabel.setValue("Stopped");
        }

        layout.addComponent(statusLabel, 0, 1);
        layout.addComponent(stateLabel, 1, 1);

        final Button discoverButton = new Button("Discover");
        discoverButton.setStyleName(ValoTheme.BUTTON_SMALL);

        if(this.discoverySchedulerService.isRunnung())
        {
            discoverButton.setEnabled(false);
        }

        discoverButton.addClickListener(new Button.ClickListener()
        {
            @SuppressWarnings("unchecked")
            public void buttonClick(Button.ClickEvent event)
            {
                final IkasanAuthentication authentication = (IkasanAuthentication) VaadinService.getCurrentRequest().getWrappedSession()
                        .getAttribute(DashboardSessionValueConstants.USER);

                discoverySchedulerService.addJob(authentication);

                discoverButton.setEnabled(false);
            }
        });

        refreshButton.addClickListener(new Button.ClickListener()
        {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent)
            {
                if(discoverySchedulerService.isRunnung())
                {
                    stateLabel.setValue("Running");
                }
                else
                {
                    stateLabel.setValue("Stopped");
                }

                if(discoverySchedulerService.isRunnung())
                {
                    discoverButton.setEnabled(false);
                }
                else
                {
                    discoverButton.setEnabled(true);
                }
            }
        });

        layout.addComponent(discoverButton, 0, 2, 1, 2);
        layout.setComponentAlignment(discoverButton, Alignment.MIDDLE_CENTER);

        panel.setContent(layout);

        this.setContent(panel);
    }
}
