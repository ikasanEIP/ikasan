package org.ikasan.dashboard.ui;

import java.util.List;

import org.ikasan.dashboard.ui.topology.panel.ReplayStatusPanel;
import org.ikasan.replay.model.ReplayEvent;
import org.ikasan.spec.configuration.PlatformConfigurationService;
import org.ikasan.spec.replay.ReplayService;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.UI;

/**
 * 
 * @author Ikasan Development Team
 *
 */
@Theme("dashboard")
public class ReplayPopup extends UI
{

	/* (non-Javadoc)
	 * @see com.vaadin.ui.UI#init(com.vaadin.server.VaadinRequest)
	 */
	@Override
	protected void init(VaadinRequest request) 
	{
		List<ReplayEvent> replayEvents = (List<ReplayEvent>)VaadinService.getCurrentRequest().getWrappedSession()
				.getAttribute("replayEvents");
		ReplayService<ReplayEvent> replayService = (ReplayService<ReplayEvent>)VaadinService.getCurrentRequest()
				.getWrappedSession().getAttribute("replayService");
		PlatformConfigurationService platformConfigurationService = (PlatformConfigurationService)VaadinService.getCurrentRequest()
				.getWrappedSession().getAttribute("platformConfigurationService");
		
		ReplayStatusPanel panel = new ReplayStatusPanel(replayEvents, replayService, platformConfigurationService);
		
		this.setContent(panel);
	}

}
