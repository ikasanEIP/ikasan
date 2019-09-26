package org.ikasan.dashboard.ui.visualisation.component;

import com.github.appreciated.apexcharts.config.tooltip.X;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.function.SerializableSupplier;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
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

public class ModuleVisualisation extends PagedTabs implements BeforeEnterObserver
{
    Logger logger = LoggerFactory.getLogger(ModuleVisualisation.class);
    private Map<Tab, NetworkDiagram> networkDiagramMap;
    private Map<Tab, Flow> flowMap;
    private ControlPanel controlPanel;
    private Map<Tab, Icon> flowIconMap = new HashMap<>();

    public ModuleVisualisation(ControlPanel controlPanel)
    {
        this.setSizeFull();
        this.controlPanel = controlPanel;
        this.controlPanel.registerListener(this.asButtonClickedListener());

        super.tabs.addSelectedChangeListener(this.asTabSelectedListener());
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
        logger.info("Adding flow [{}] to visualisation.", flow.getName());
        NetworkDiagram networkDiagram = this.createNetworkDiagram(flow);

        Icon icon = new Icon(VaadinIcon.CIRCLE_THIN);
        icon.setColor("grey");
        icon.setSize("20px");


        this.controlPanel.setFlowStatus("stopped");

        Tab tab = new Tab(new Label(flow.getName()), icon);
        this.add(networkDiagram, tab);
        this.networkDiagramMap.put(tab, networkDiagram);
        this.flowMap.put(tab, flow);

        this.flowIconMap.put(tab, icon);

        logger.info("Finished adding flow [{}] to visualisation.", flow.getName());
    }

    /**
     * Method to update the network diagram with the node and edge lists.
     *
     * @param flow to render.
     */
    protected NetworkDiagram createNetworkDiagram(Flow flow)
    {
        logger.info("Creating network diagram for flow [{}] to visualisation.", flow.getName());

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

        logger.info("Finished creating network diagram for flow [{}] to visualisation.", flow.getName());
        return networkDiagram;
    }

    /**
     * Method to update the network diagram with the node and edge lists.
     *
     * @param module to render.
     */
    protected NetworkDiagram createNetworkDiagram(Module module)
    {
        logger.info("Creating network diagram for module [{}] to visualisation.", module.getName());
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

        logger.info("Finished creating network diagram for module [{}] to visualisation.", module.getName());

        return networkDiagram;
    }

    public ComponentEventListener<ClickEvent<Button>> asButtonClickedListener()
    {
        return (ComponentEventListener<ClickEvent<Button>>) selectedChangeEvent ->
    {
        if(selectedChangeEvent.getSource().getElement().getAttribute("id").equals(ControlPanel.START))
        {
            controlPanel.setFlowStatus("running");

            if(tabs.getSelectedTab() != null)
            {
                tabs.getSelectedTab().removeAll();

                Flow flow = flowMap.get(tabs.getSelectedTab());

                Icon icon = new Icon(VaadinIcon.CIRCLE);
                icon.setColor("green");
                icon.setSize("20px");

                tabs.getSelectedTab().add(new Label(flow.getName()), icon);
            }
        }
    };
}


    public ComponentEventListener<Tabs.SelectedChangeEvent> asTabSelectedListener()
    {
        return (ComponentEventListener<Tabs.SelectedChangeEvent>) selectedChangeEvent ->
        {
            if(flowMap.get(tabs.getSelectedTab()) != null)
            {
                logger.info("Switching to tab {}", tabs.getSelectedTab().getLabel());
                this.redrawFlow();

                NetworkDiagram networkDiagram = networkDiagramMap.get(tabs.getSelectedTab());
                tabsToSuppliers.put(tabs.getSelectedTab(), (SerializableSupplier<Component>) () -> networkDiagram);
                logger.info("Finished switching to tab {}", tabs.getSelectedTab().getLabel());
            }
        };
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent)
    {
        this.redrawFlow();
    }

    protected void redrawFlow()
    {
        if(flowMap.get(tabs.getSelectedTab()) != null)
        {
            NetworkDiagram networkDiagram = networkDiagramMap.get(tabs.getSelectedTab());

            Flow flow = flowMap.get(tabs.getSelectedTab());
            networkDiagram.drawFlow(flow.getX(), flow.getY(), flow.getW(), flow.getH(), flow.getName());
        }
    }
}
