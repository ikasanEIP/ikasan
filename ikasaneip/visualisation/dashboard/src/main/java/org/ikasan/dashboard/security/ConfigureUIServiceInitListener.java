package org.ikasan.dashboard.security;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import org.ikasan.dashboard.ui.security.view.LoginView;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConfigureUIServiceInitListener implements VaadinServiceInitListener
{

    @Value("${dashboard.security.mode:production}")
    private String dashboardSecurityMode;


	@Override
    public void serviceInit(ServiceInitEvent event)
    {
		event.getSource().addUIInitListener(uiEvent ->
        {

			final UI ui = uiEvent.getUI();
			ui.addBeforeEnterListener(this::beforeEnter);
		});
	}

	/**
	 * Reroutes the user if (s)he is not authorized to access the view.
	 *
	 * @param event
	 *            before navigation event with event details
	 */
	private void beforeEnter(BeforeEnterEvent event)
    {
		if (!LoginView.class.equals(event.getNavigationTarget())
		    && !SecurityUtils.isUserLoggedIn() && !dashboardSecurityMode.equals("test"))
		{
			event.rerouteTo(LoginView.class);
		}
	}
}