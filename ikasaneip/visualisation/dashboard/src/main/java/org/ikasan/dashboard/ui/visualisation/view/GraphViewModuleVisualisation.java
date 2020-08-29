package org.ikasan.dashboard.ui.visualisation.view;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.shared.Registration;
import org.ikasan.dashboard.broadcast.FlowState;
import org.ikasan.dashboard.broadcast.FlowStateBroadcaster;
import org.ikasan.dashboard.cache.FlowStateCache;
import org.ikasan.dashboard.ui.visualisation.adapter.service.ModuleVisjsAdapter;
import org.ikasan.dashboard.ui.visualisation.component.ControlPanel;
import org.ikasan.dashboard.ui.visualisation.component.FlowComboBox;
import org.ikasan.dashboard.ui.visualisation.component.ModuleVisualisation;
import org.ikasan.dashboard.ui.visualisation.component.StatusPanel;
import org.ikasan.dashboard.ui.visualisation.event.GraphViewChangeEvent;
import org.ikasan.dashboard.ui.visualisation.event.GraphViewChangeListener;
import org.ikasan.dashboard.ui.visualisation.model.flow.Flow;
import org.ikasan.dashboard.ui.visualisation.model.flow.Module;
import org.ikasan.spec.metadata.ConfigurationMetaData;
import org.ikasan.spec.metadata.ConfigurationMetaDataService;
import org.ikasan.spec.metadata.ModuleMetaData;
import org.ikasan.spec.module.client.ConfigurationService;
import org.ikasan.spec.module.client.MetaDataService;
import org.ikasan.spec.module.client.ModuleControlService;
import org.ikasan.spec.module.client.TriggerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GraphViewModuleVisualisation extends VerticalLayout {
    Logger logger = LoggerFactory.getLogger(GraphViewModuleVisualisation.class);

    private ModuleControlService moduleControlRestService;

    private ConfigurationService configurationRestService;

    private TriggerService triggerRestService;

    private ConfigurationMetaDataService configurationMetadataService;

    private ModuleVisualisation moduleVisualisation;
    private H2 moduleLabel = new H2();
    private HorizontalLayout moduleViewHeaderLayout = new HorizontalLayout();
    private FlowComboBox flowComboBox;
    private ControlPanel controlPanel;

    private Registration broadcasterRegistration;

    private StatusPanel statusPanel;

    private Module currentModule;
    private Flow currentFlow;

    private List<GraphViewChangeListener> graphViewChangeListeners;

    private MetaDataService metaDataApplicationRestService;

    /**
     * Constructor
     */
    public GraphViewModuleVisualisation(ModuleControlService moduleControlRestService
        , ConfigurationService configurationRestService
        , TriggerService triggerRestService
        , ConfigurationMetaDataService configurationMetadataService
        , MetaDataService metaDataApplicationRestService) {

        this.graphViewChangeListeners = new ArrayList<>();

        this.moduleControlRestService = moduleControlRestService;
        if(this.moduleControlRestService == null){
            throw new IllegalArgumentException("moduleControlRestService cannot be null!");
        }
        this.configurationRestService = configurationRestService;
        if(this.configurationRestService == null){
            throw new IllegalArgumentException("configurationRestService cannot be null!");
        }
        this.triggerRestService = triggerRestService;
        if(this.triggerRestService == null){
            throw new IllegalArgumentException("triggerRestService cannot be null!");
        }
        this.configurationMetadataService = configurationMetadataService;
        if(this.configurationMetadataService == null){
            throw new IllegalArgumentException("configurationMetadataService cannot be null!");
        }
        this.metaDataApplicationRestService = metaDataApplicationRestService;
        if(this.metaDataApplicationRestService == null){
            throw new IllegalArgumentException("metaDataApplicationRestService cannot be null!");
        }

        this.init();
    }

    private void init() {
        this.controlPanel = new ControlPanel(this.moduleControlRestService);
        this.setMargin(false);
        this.setSizeFull();
        this.createModuleViewHeader();
    }

    protected void createModuleViewHeader() {
        this.createFlowCombo();

        HorizontalLayout moduleNameLayout = new HorizontalLayout();
        moduleNameLayout.setMargin(false);
        moduleNameLayout.setSpacing(false);
        moduleNameLayout.add(moduleLabel);

        HorizontalLayout comboBoxLayout = new HorizontalLayout();
        comboBoxLayout.setMargin(false);
        comboBoxLayout.setSpacing(false);
        comboBoxLayout.add(flowComboBox);

        moduleViewHeaderLayout.setWidth("100%");
        moduleViewHeaderLayout.setMargin(false);

        moduleVisualisation = new ModuleVisualisation(this.moduleControlRestService,
            this.configurationRestService, this.triggerRestService, this.metaDataApplicationRestService);

        statusPanel = new StatusPanel(this.moduleControlRestService, this.moduleVisualisation);

        moduleViewHeaderLayout.setFlexGrow(1, moduleNameLayout);
        moduleViewHeaderLayout.setFlexGrow(1, statusPanel);
        moduleViewHeaderLayout.setFlexGrow(5, comboBoxLayout);
        moduleViewHeaderLayout.setFlexGrow(3, controlPanel);

        moduleViewHeaderLayout.add(moduleNameLayout, statusPanel, comboBoxLayout, controlPanel);
        moduleViewHeaderLayout.setVerticalComponentAlignment(Alignment.BASELINE, moduleNameLayout, statusPanel, comboBoxLayout, controlPanel);

        this.add(moduleViewHeaderLayout);

        this.graphViewChangeListeners.add(statusPanel);
        this.graphViewChangeListeners.add(controlPanel);
    }

    private void createFlowCombo() {
        flowComboBox = new FlowComboBox();
        flowComboBox.setItemLabelGenerator(Flow::getName);
        flowComboBox.setHeight("40px");
        flowComboBox.setWidth("600px");

        flowComboBox.setRenderer(new ComponentRenderer<>(item ->
        {
            HorizontalLayout container = new HorizontalLayout();

            Icon icon = new Icon(VaadinIcon.CIRCLE);

            FlowState flowState = FlowStateCache.instance().get(currentModule, item);

            if (flowState != null) {
                icon.setColor(flowState.getState().getStateColour());
            }

            icon.setSize("15px");
            icon.setVisible(true);
            VerticalLayout verticalLayout = new VerticalLayout();
            verticalLayout.setWidth("20px");
            verticalLayout.add(icon);
            verticalLayout.setHorizontalComponentAlignment(Alignment.END, icon);

            Label namelabel = new Label(item.getName());
            namelabel.setWidth("500px");

            container.setVerticalComponentAlignment(Alignment.CENTER, namelabel);
            container.add(namelabel, verticalLayout);

            return container;
        }));

        this.flowComboBox.addValueChangeListener((HasValue.ValueChangeListener<AbstractField.ComponentValueChangeEvent<ComboBox<Flow>, Flow>>) comboBoxFlowComponentValueChangeEvent ->
        {
            if (comboBoxFlowComponentValueChangeEvent.getValue() != null) {
                logger.info("Switching to flow {}", comboBoxFlowComponentValueChangeEvent.getValue().getName());
                this.moduleVisualisation.setCurrentFlow(comboBoxFlowComponentValueChangeEvent.getValue());
                this.moduleVisualisation.redraw();

                this.currentFlow = comboBoxFlowComponentValueChangeEvent.getValue();

                this.fireModuleFlowChangeEvent();
                logger.info("Finished switching to flow {}", comboBoxFlowComponentValueChangeEvent.getValue().getName());
            }
        });
    }

    /**
     * Create module graph
     *
     * @param moduleMetaData
     */
    protected void createModuleVisualisation(ModuleMetaData moduleMetaData) {
        List<String> configurationIds = moduleMetaData.getFlows().stream()
            .map(flowMetaData -> flowMetaData.getFlowElements()).flatMap(List::stream)
            .map(flowElementMetaData -> flowElementMetaData.getConfigurationId())
            .filter(id -> id != null)
            .distinct()
            .collect(Collectors.toList());

        List<ConfigurationMetaData> configurationMetaData
            = this.configurationMetadataService.findByIdList(configurationIds);

        ModuleVisjsAdapter adapter = new ModuleVisjsAdapter();
        Module module = adapter.adapt(moduleMetaData, configurationMetaData);

        if (this.moduleVisualisation != null) {
            this.remove(moduleVisualisation);
        }

        this.currentModule = module;
        this.currentFlow = module.getFlows().get(0);

        this.fireModuleFlowChangeEvent();

        this.moduleVisualisation = new ModuleVisualisation(this.moduleControlRestService,
            this.configurationRestService, this.triggerRestService, metaDataApplicationRestService);
        moduleVisualisation.addModule(module);
        moduleVisualisation.setCurrentFlow(module.getFlows().get(0));
        moduleVisualisation.redraw();
        this.flowComboBox.setCurrentModule(module);

        this.statusPanel.setModuleVisualisation(this.moduleVisualisation);
        this.add(moduleVisualisation);
    }

    protected void fireModuleFlowChangeEvent() {
        GraphViewChangeEvent graphViewChangeEvent = new GraphViewChangeEvent(this.currentModule, this.currentFlow);

        for (GraphViewChangeListener graphViewChangeListener : this.graphViewChangeListeners) {
            graphViewChangeListener.onChange(graphViewChangeEvent);
        }
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        UI ui = attachEvent.getUI();

        broadcasterRegistration = FlowStateBroadcaster.register(flowState ->
        {
            ui.access(() ->
            {
                // do something interesting here.
                logger.info("Received flow state: " + flowState);
            });
        });

    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        broadcasterRegistration.remove();
        broadcasterRegistration = null;
    }

    public Module getModule() {
        return this.moduleVisualisation.getModule();
    }

    public Flow getCurrentFlow() {
        return this.moduleVisualisation.getCurrentFlow();
    }
}

