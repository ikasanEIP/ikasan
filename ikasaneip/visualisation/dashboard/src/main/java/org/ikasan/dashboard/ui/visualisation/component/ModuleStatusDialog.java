package org.ikasan.dashboard.ui.visualisation.component;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.html.Image;
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
import org.ikasan.dashboard.ui.general.component.ComponentSecurityVisibility;
import org.ikasan.dashboard.ui.general.component.FlowControlManagementDialog;
import org.ikasan.dashboard.ui.util.SecurityConstants;
import org.ikasan.dashboard.ui.visualisation.event.GraphViewChangeEvent;
import org.ikasan.dashboard.ui.visualisation.model.flow.Flow;
import org.ikasan.dashboard.ui.visualisation.model.flow.Module;
import org.ikasan.rest.client.ModuleControlRestServiceImpl;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.spec.module.StartupType;
import org.ikasan.spec.module.client.ModuleControlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class ModuleStatusDialog extends Dialog
{
    private Logger logger = LoggerFactory.getLogger(ModuleStatusDialog.class);

    private Grid<Flow> flowGrid = new Grid<>();
    private Module currentModule;
    private ModuleControlService moduleControlRestService;
    private ControlPanel controlPanel;
    private ModuleVisualisation moduleVisualisation;

    private Registration cacheStateBroadcasterRegistration;

    public ModuleStatusDialog(Module currentModule, ModuleControlService moduleControlRestService,
                              ModuleVisualisation moduleVisualisation)
    {
        this.currentModule = currentModule;
        this.moduleControlRestService = moduleControlRestService;
        this.controlPanel = new ControlPanel(moduleControlRestService);
        this.moduleVisualisation = moduleVisualisation;

        H3 moduleLabel = new H3(String.format(getTranslation("label.module", UI.getCurrent().getLocale(), null), currentModule.getName()));

        HorizontalLayout topLayout = new HorizontalLayout();
        topLayout.setWidth("100%");
        topLayout.add(moduleLabel);
        topLayout.setVerticalComponentAlignment(FlexComponent.Alignment.START, moduleLabel);

        topLayout.setFlexGrow(2, moduleLabel);
        topLayout.setFlexGrow(5, controlPanel);


        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setWidth("100%");
        verticalLayout.setHeight("60vh");
        verticalLayout.add(topLayout, createFlowGrid());

        this.flowGrid.setItems(currentModule.getFlows());

        this.add(verticalLayout);
        this.setWidth("1100px");
    }

    protected Grid createFlowGrid()
    {
        IkasanAuthentication authentication = (IkasanAuthentication) SecurityContextHolder.getContext().getAuthentication();

        // Create a modulesGrid bound to the list
        flowGrid.removeAllColumns();
        flowGrid.setVisible(true);
        flowGrid.setSizeFull();
        flowGrid.setSelectionMode(Grid.SelectionMode.MULTI);

        flowGrid.addColumn(Flow::getName).setHeader("Name").setFlexGrow(10);
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
        })).setHeader("Status").setKey("status").setFlexGrow(4);
        flowGrid.addColumn(new ComponentRenderer<>((Flow node) ->
        {
            VerticalLayout wrapper = new VerticalLayout();
            HorizontalLayout layout = new HorizontalLayout();

            if(node.getStartupType().equals(StartupType.AUTOMATIC)) {
                Image autoImage  = new Image("/frontend/images/flow-automatic.png", "");
                autoImage.setHeight("40px");

                Button button = new Button(autoImage);
                button.setHeight("40px");
                button.setWidth("40px");

                button.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent -> {
                    FlowControlManagementDialog flowControlManagementDialog = new FlowControlManagementDialog(this.currentModule,
                        node, this.moduleControlRestService, moduleVisualisation);

                    flowControlManagementDialog.open();

                    flowControlManagementDialog.addOpenedChangeListener((ComponentEventListener<OpenedChangeEvent<Dialog>>) dialogOpenedChangeEvent -> {
                        if(!dialogOpenedChangeEvent.isOpened()){
                            this.flowGrid.getDataProvider().refreshItem(node);
                        }
                    });
                });

                layout.add(button);
                layout.setVerticalComponentAlignment(FlexComponent.Alignment.BASELINE, button);
                wrapper.add(layout);
                wrapper.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, layout);
                return wrapper;
            }
            else if(node.getStartupType().equals(StartupType.DISABLED)) {
                Image disabledImage  = new Image("/frontend/images/flow-disabled.png", "");
                disabledImage.setHeight("40px");

                Button button = new Button(disabledImage);
                button.setHeight("40px");
                button.setWidth("40px");

                button.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent -> {
                    FlowControlManagementDialog flowControlManagementDialog = new FlowControlManagementDialog(this.currentModule,
                        node, this.moduleControlRestService, moduleVisualisation);

                    flowControlManagementDialog.open();

                    flowControlManagementDialog.addOpenedChangeListener((ComponentEventListener<OpenedChangeEvent<Dialog>>) dialogOpenedChangeEvent -> {
                        if(!dialogOpenedChangeEvent.isOpened()){
                            this.flowGrid.getDataProvider().refreshItem(node);
                        }
                    });
                });

                layout.add(button);
                layout.setVerticalComponentAlignment(FlexComponent.Alignment.BASELINE, button);
                wrapper.add(layout);
                wrapper.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, layout);
                return wrapper;
            }
            else if(node.getStartupType().equals(StartupType.MANUAL)) {
                Image manualImage  = new Image("/frontend/images/flow-manual.png", "");
                manualImage.setHeight("40px");

                Button button = new Button(manualImage);
                button.setHeight("40px");
                button.setWidth("40px");

                button.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent -> {
                    FlowControlManagementDialog flowControlManagementDialog = new FlowControlManagementDialog(this.currentModule,
                        node, this.moduleControlRestService, moduleVisualisation);

                    flowControlManagementDialog.open();

                    flowControlManagementDialog.addOpenedChangeListener((ComponentEventListener<OpenedChangeEvent<Dialog>>) dialogOpenedChangeEvent -> {
                        if(!dialogOpenedChangeEvent.isOpened()){
                            this.flowGrid.getDataProvider().refreshItem(node);
                        }
                    });
                });

                layout.add(button);
                layout.setVerticalComponentAlignment(FlexComponent.Alignment.BASELINE, button);
                wrapper.add(layout);
                wrapper.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, layout);
                return wrapper;
            }

            return null;
        })).setHeader("Flow Startup Type").setKey("flowStartupType").setFlexGrow(1);
        flowGrid.addColumn(new ComponentRenderer<>((Flow node) ->
        {
            ControlPanel controlPanel = new ControlPanel(this.moduleControlRestService);
            controlPanel.onChange(new GraphViewChangeEvent(this.currentModule, node));

            UI.getCurrent().access(() -> ComponentSecurityVisibility.applySecurity(authentication, controlPanel, SecurityConstants.ALL_AUTHORITY
                , SecurityConstants.MODULE_CONTROL_WRITE
                , SecurityConstants.MODULE_CONTROL_ADMIN));

            return controlPanel;
        })).setHeader("Flow Control").setKey("flowControl").setFlexGrow(10);

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
            logger.debug("Received flow state: " + flowState);
            this.currentModule.getFlows()
                .stream()
                .filter(flow -> flowState.getFlowName().equals(flow.getName()))
                .findFirst().ifPresent(flow -> ui.access(() -> this.flowGrid.getDataProvider().refreshItem(flow)));
        });
    }

    @Override
    protected void onDetach(DetachEvent detachEvent)
    {
        this.cacheStateBroadcasterRegistration.remove();
        this.cacheStateBroadcasterRegistration = null;
    }
}
