package org.ikasan.dashboard.ui;

import java.util.List;

import org.ikasan.dashboard.ui.replay.panel.ReplayStatusPanel;
import org.ikasan.replay.model.ReplayAuditEvent;
import org.ikasan.replay.model.ReplayEvent;
import org.ikasan.spec.configuration.PlatformConfigurationService;
import org.ikasan.spec.replay.ReplayService;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.ui.UI;

/**
 * 
 * @author Ikasan Development Team
 *
 */
@Theme("dashboard")
@SuppressWarnings("serial")
@Push(value=PushMode.AUTOMATIC, transport=Transport.LONG_POLLING)
@PreserveOnRefresh
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
		ReplayService<ReplayEvent, ReplayAuditEvent> replayService = (ReplayService<ReplayEvent, ReplayAuditEvent>)VaadinService.getCurrentRequest()
				.getWrappedSession().getAttribute("replayService");
		PlatformConfigurationService platformConfigurationService = (PlatformConfigurationService)VaadinService.getCurrentRequest()
				.getWrappedSession().getAttribute("platformConfigurationService");
		
		ReplayStatusPanel panel = new ReplayStatusPanel(replayEvents, replayService, platformConfigurationService);
		
		this.setContent(panel);
	}

}
