package org.ikasan.dashboard.ui.search.popup;

import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.ui.UI;
import org.ikasan.dashboard.ui.search.panel.ReplayStatusPanel;
import org.ikasan.replay.model.BulkReplayResponse;
import org.ikasan.replay.model.HibernateReplayAuditEvent;
import org.ikasan.replay.model.ReplayResponse;
import org.ikasan.spec.configuration.PlatformConfigurationService;
import org.ikasan.spec.replay.ReplayEvent;
import org.ikasan.spec.replay.ReplayService;
import org.ikasan.topology.service.TopologyService;

import java.util.List;

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
	 * @see com.vaadin.ui.UI#initCloud(com.vaadin.server.VaadinRequest)
	 */
	@Override
	protected void init(VaadinRequest request) 
	{
		List<ReplayEvent> replayEvents = (List<ReplayEvent>)VaadinService.getCurrentRequest().getWrappedSession()
				.getAttribute("replayEvents");
		ReplayService<ReplayEvent, HibernateReplayAuditEvent, ReplayResponse, BulkReplayResponse> replayService = (ReplayService<ReplayEvent, HibernateReplayAuditEvent, ReplayResponse, BulkReplayResponse>)VaadinService.getCurrentRequest()
				.getWrappedSession().getAttribute("replayService");
		PlatformConfigurationService platformConfigurationService = (PlatformConfigurationService)VaadinService.getCurrentRequest()
				.getWrappedSession().getAttribute("platformConfigurationService");
		TopologyService topologyService = (TopologyService) VaadinService.getCurrentRequest()
				.getWrappedSession().getAttribute("topologyService");
		
		ReplayStatusPanel panel = new ReplayStatusPanel(replayEvents, replayService, platformConfigurationService,
				topologyService);
		
		this.setContent(panel);
	}

}
