package org.ikasan.dashboard.ui.visualisation.component;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridMultiSelectionModel;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.shared.Registration;
import org.ikasan.dashboard.broadcast.FlowState;
import org.ikasan.dashboard.broadcast.State;
import org.ikasan.dashboard.cache.CacheStateBroadcaster;
import org.ikasan.dashboard.cache.FlowStateCache;
import org.ikasan.dashboard.ui.visualisation.event.GraphViewChangeEvent;
import org.ikasan.dashboard.ui.visualisation.model.flow.Flow;
import org.ikasan.dashboard.ui.visualisation.model.flow.Module;
import org.ikasan.rest.client.ModuleControlRestServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class ModuleStatusDialog extends Dialog
{
    private Logger logger = LoggerFactory.getLogger(ModuleStatusDialog.class);

    private Grid<Flow> flowGrid = new Grid<>();
    private Module currentModule;
    private ModuleControlRestServiceImpl moduleControlRestService;
    private ControlPanel controlPanel;

    private Registration cacheStateBroadcasterRegistration;

    public ModuleStatusDialog(Module currentModule, ModuleControlRestServiceImpl moduleControlRestService)
    {
        this.currentModule = currentModule;
        this.moduleControlRestService = moduleControlRestService;
        this.controlPanel = new ControlPanel(moduleControlRestService);

        H3 moduleLabel = new H3(String.format(getTranslation("label.module", UI.getCurrent().getLocale(), null), currentModule.getName()));

        HorizontalLayout topLayout = new HorizontalLayout();
        topLayout.setWidth("100%");
        topLayout.add(moduleLabel);
//        topLayout.add(controlPanel);
        topLayout.setVerticalComponentAlignment(FlexComponent.Alignment.START, moduleLabel);
//        topLayout.setVerticalComponentAlignment(FlexComponent.Alignment.END, controlPanel);

        topLayout.setFlexGrow(2, moduleLabel);
        topLayout.setFlexGrow(5, controlPanel);


        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setWidth("100%");
        verticalLayout.setHeight("60vh");
        verticalLayout.add(topLayout, createFlowGrid());

        this.flowGrid.setItems(currentModule.getFlows());

        this.add(verticalLayout);
        this.setWidth("1000px");
    }

    protected Grid createFlowGrid()
    {
        // Create a modulesGrid bound to the list
        flowGrid.removeAllColumns();
        flowGrid.setVisible(true);
        flowGrid.setSizeFull();
        flowGrid.setSelectionMode(Grid.SelectionMode.MULTI);

        flowGrid.addColumn(Flow::getName).setHeader("Name").setFlexGrow(5);
        flowGrid.addColumn(new ComponentRenderer<>((Flow node) ->
        {
            FlowState flowState = FlowStateCache.instance().get(this.currentModule, node);

            State state = State.UNKNOWN_STATE;

            if(flowState != null)
            {
                state = flowState.getState();
            }

            HorizontalLayout layout = this.getStatusLabelLayout(state);

            return layout;
        })).setHeader("Status").setKey("status").setFlexGrow(2);
        flowGrid.addColumn(new ComponentRenderer<>((Flow node) ->
        {
            VerticalLayout layout = new VerticalLayout();
            ControlPanel controlPanel = new ControlPanel(this.moduleControlRestService);
            controlPanel.onChange(new GraphViewChangeEvent(this.currentModule, node));

            layout.add(controlPanel);
            layout.setHorizontalComponentAlignment(FlexComponent.Alignment.START, controlPanel);

            return controlPanel;
        })).setFlexGrow(5);

        flowGrid.getColumnByKey("status").setClassNameGenerator(item -> {
            FlowState flowState = FlowStateCache.instance().get(this.currentModule, item);

            State state = State.UNKNOWN_STATE;

            if(flowState != null)
            {
                state = flowState.getState();
            }

            return Optional.ofNullable(state.getFlowState()).orElse("");
        });

        return flowGrid;
    }

    protected HorizontalLayout getStatusLabelLayout(State state)
    {
        Icon icon = new Icon(VaadinIcon.CIRCLE);
        icon.setColor(state.getStateColour());
        icon.setSize("20px");

        HorizontalLayout layout = new HorizontalLayout();
        layout.setSizeFull();

        H6 statusLabel = new H6(getTranslation("label.status-"+state.getFlowState(), UI.getCurrent().getLocale()));
        layout.add(icon, statusLabel);

        layout.setVerticalComponentAlignment(FlexComponent.Alignment.CENTER, icon, statusLabel);

        return layout;
    }

    @Override
    protected void onAttach(AttachEvent attachEvent)
    {
        UI ui = attachEvent.getUI();

        cacheStateBroadcasterRegistration = CacheStateBroadcaster.register(flowState ->
        {
            logger.info("Received flow state: " + flowState);
            this.flowGrid.setItems(currentModule.getFlows());
        });
    }

    @Override
    protected void onDetach(DetachEvent detachEvent)
    {
        this.cacheStateBroadcasterRegistration.remove();
        this.cacheStateBroadcasterRegistration = null;
    }
}
