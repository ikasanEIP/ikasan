package org.ikasan.dashboard.ui.control.panel;


import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Panel;
import org.ikasan.dashboard.ui.control.component.ModuleControlLayout;
import org.ikasan.dashboard.ui.framework.cache.TopologyStateCache;
import org.ikasan.topology.service.TopologyService;

/**
 * Created by Ikasan Development Team on 09/11/2017.
 */
public class ModuleControlPanel extends Panel implements View
{
    private ModuleControlLayout moduleControlLayout;

    private TopologyService topologyService;

    private TopologyStateCache topologyCache;

    public ModuleControlPanel(TopologyService topologyService, TopologyStateCache topologyCache)
    {
        super();
        this.topologyService = topologyService;
        if(this.topologyService == null)
        {
            throw new IllegalArgumentException("topology service cannot be null!");
        }
        this.topologyCache = topologyCache;
        if(this.topologyCache == null)
        {
            throw new IllegalArgumentException("topologyCache service cannot be null!");
        }

        init();
    }

    private void init()
    {
        this.setSizeFull();

        this.moduleControlLayout = new ModuleControlLayout(this.topologyService, this.topologyCache);

        this.setContent(this.moduleControlLayout);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent)
    {
        this.moduleControlLayout.loadTable();
    }
}
