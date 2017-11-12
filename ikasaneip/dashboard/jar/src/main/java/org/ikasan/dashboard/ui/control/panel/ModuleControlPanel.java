package org.ikasan.dashboard.ui.control.panel;


import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Panel;
import org.ikasan.dashboard.ui.control.component.ModuleControlLayout;
import org.ikasan.topology.service.TopologyService;

/**
 * Created by stewmi on 09/11/2017.
 */
public class ModuleControlPanel extends Panel implements View
{
    private ModuleControlLayout moduleControlLayout;

    private TopologyService topologyService;

    public ModuleControlPanel(TopologyService topologyService)
    {
        super();
        this.topologyService = topologyService;
        init();
    }

    private void init()
    {
        this.setSizeFull();

        this.moduleControlLayout = new ModuleControlLayout(this.topologyService);

        this.setContent(this.moduleControlLayout);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent)
    {
        this.moduleControlLayout.loadTable();
    }
}
