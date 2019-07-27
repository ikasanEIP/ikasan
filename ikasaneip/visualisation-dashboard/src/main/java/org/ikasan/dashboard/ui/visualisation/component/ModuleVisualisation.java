package org.ikasan.dashboard.ui.visualisation.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.function.SerializableSupplier;
import org.ikasan.dashboard.ui.visualisation.layout.IkasanFlowLayoutManager;
import org.ikasan.dashboard.ui.visualisation.layout.IkasanModuleLayoutManager;
import org.ikasan.dashboard.ui.visualisation.model.flow.Flow;
import org.ikasan.dashboard.ui.visualisation.model.flow.Module;
import org.ikasan.vaadin.visjs.network.NetworkDiagram;
import org.ikasan.vaadin.visjs.network.listener.ClickListener;
import org.ikasan.vaadin.visjs.network.listener.DoubleClickListener;
import org.ikasan.vaadin.visjs.network.listener.OnContextListener;
import org.ikasan.vaadin.visjs.network.options.Interaction;
import org.ikasan.vaadin.visjs.network.options.Options;
import org.ikasan.vaadin.visjs.network.options.edges.ArrowHead;
import org.ikasan.vaadin.visjs.network.options.edges.Arrows;
import org.ikasan.vaadin.visjs.network.options.edges.EdgeColor;
import org.ikasan.vaadin.visjs.network.options.edges.Edges;
import org.ikasan.vaadin.visjs.network.options.nodes.Nodes;
import org.ikasan.vaadin.visjs.network.options.physics.Physics;
import org.ikasan.vaadin.visjs.network.util.Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.tabs.PagedTabs;

import java.util.HashMap;
import java.util.Map;

public class ModuleVisualisation extends PagedTabs implements ComponentEventListener<Tabs.SelectedChangeEvent>
{
    Logger logger = LoggerFactory.getLogger(ModuleVisualisation.class);
    Map<Tab, NetworkDiagram> networkDiagramMap;
    Map<Tab, Flow> flowMap;

    public ModuleVisualisation()
    {
        this.setSizeFull();

        super.tabs.addSelectedChangeListener(this);
        this.networkDiagramMap = new HashMap<>();
        this.flowMap = new HashMap<>();
    }

    public void addModule(Module module)
    {
        for(Flow flow: module.getFlows())
        {
            add(flow);
        }
    }

    protected void add(Flow flow)
    {
        NetworkDiagram networkDiagram = this.createNetworkDiagram(flow);
        Tab tab = new Tab(flow.getName());
        this.add(networkDiagram, tab);
        this.networkDiagramMap.put(tab, networkDiagram);
        this.flowMap.put(tab, flow);
    }

    /**
     * Method to update the network diagram with the node and edge lists.
     *
     * @param flow to render.
     */
    protected NetworkDiagram createNetworkDiagram(Flow flow)
    {
        Physics physics = new Physics();
        physics.setEnabled(false);

        NetworkDiagram networkDiagram = new NetworkDiagram
            (Options.builder()
                .withAutoResize(true)
                .withPhysics(physics)
                .withInteraction(Interaction.builder().withDragNodes(false) .build())
                .withEdges(
                    Edges.builder()
                        .withArrows(new Arrows(new ArrowHead()))
                        .withColor(EdgeColor.builder()
                            .withColor("#000000")
                            .build())
                        .withDashes(false)
                        .withFont(Font.builder().withSize(9).build())
                        .build())
                .withNodes(Nodes.builder().withFont(Font.builder().withSize(11).build()).build())
                .build());

        networkDiagram.setSizeFull();

        IkasanFlowLayoutManager layoutManager = new IkasanFlowLayoutManager(flow, networkDiagram, null);
        layoutManager.layout();

        networkDiagram.addDoubleClickListener((DoubleClickListener) doubleClickEvent ->
        {
            logger.info(doubleClickEvent.getParams().toString());
        });

        networkDiagram.addClickListener((ClickListener) clickEvent -> {
            logger.info(clickEvent.getParams().toString());
        });

        networkDiagram.addOnContextListener((OnContextListener) onContextEvent -> {
            logger.info(onContextEvent.getParams().toString());
        });

        return networkDiagram;
    }

    /**
     * Method to update the network diagram with the node and edge lists.
     *
     * @param module to render.
     */
    protected NetworkDiagram createNetworkDiagram(Module module)
    {
        Physics physics = new Physics();
        physics.setEnabled(false);

        NetworkDiagram networkDiagram = new NetworkDiagram
            (Options.builder()
                .withAutoResize(true)
                .withPhysics(physics)
                .withInteraction(Interaction.builder().withDragNodes(false) .build())
                .withEdges(
                    Edges.builder()
                        .withArrows(new Arrows(new ArrowHead()))
                        .withColor(EdgeColor.builder()
                            .withColor("#000000")
                            .build())
                        .withDashes(false)
                        .withFont(Font.builder().withSize(9).build())
                        .build())
                .withNodes(Nodes.builder().withFont(Font.builder().withSize(11).build()).build())
                .build());

        networkDiagram.setSizeFull();

        IkasanModuleLayoutManager layoutManager = new IkasanModuleLayoutManager(module, networkDiagram, null);
        layoutManager.layout();

        networkDiagram.addDoubleClickListener((DoubleClickListener) doubleClickEvent ->
        {
            logger.info(doubleClickEvent.getParams().toString());
        });

        networkDiagram.addClickListener((ClickListener) clickEvent -> {
            logger.info(clickEvent.getParams().toString());
        });

        networkDiagram.addOnContextListener((OnContextListener) onContextEvent -> {
            logger.info(onContextEvent.getParams().toString());
        });

        return networkDiagram;
    }

    @Override
    public void onComponentEvent(Tabs.SelectedChangeEvent selectedChangeEvent)
    {
        if(flowMap.get(this.tabs.getSelectedTab()) != null)
        {
            final NetworkDiagram networkDiagram = this.createNetworkDiagram(flowMap.get(this.tabs.getSelectedTab()));
            this.networkDiagramMap.put(this.tabs.getSelectedTab(), networkDiagram);

            this.tabsToSuppliers.put(this.tabs.getSelectedTab(), (SerializableSupplier<Component>) () -> networkDiagram);
        }
    }
}
