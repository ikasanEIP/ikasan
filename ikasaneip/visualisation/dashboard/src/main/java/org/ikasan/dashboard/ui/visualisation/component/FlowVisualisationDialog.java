package org.ikasan.dashboard.ui.visualisation.component;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.ikasan.dashboard.ui.visualisation.adapter.service.ModuleVisjsAdapter;
import org.ikasan.dashboard.ui.visualisation.event.GraphViewChangeEvent;
import org.ikasan.dashboard.ui.visualisation.model.flow.Module;
import org.ikasan.rest.client.ConfigurationRestServiceImpl;
import org.ikasan.rest.client.ModuleControlRestServiceImpl;
import org.ikasan.rest.client.TriggerRestServiceImpl;
import org.ikasan.spec.metadata.ConfigurationMetaData;
import org.ikasan.spec.metadata.ConfigurationMetaDataService;
import org.ikasan.spec.metadata.ModuleMetaData;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FlowVisualisationDialog extends Dialog {
    private ModuleControlRestServiceImpl moduleControlRestService;
    private ConfigurationRestServiceImpl configurationRestService;
    private TriggerRestServiceImpl triggerRestService;
    private Module module;
    private ModuleVisualisation moduleVisualisation;
    private ConfigurationMetaDataService configurationMetadataService;
    private ControlPanel flowControlPanel;

    public FlowVisualisationDialog(ModuleControlRestServiceImpl moduleControlRestService
        , ConfigurationRestServiceImpl configurationRestService
        , TriggerRestServiceImpl triggerRestService, ConfigurationMetaDataService configurationMetadataService
        , ModuleMetaData moduleMetaData, String flowName)
    {
        this.moduleControlRestService = moduleControlRestService;
        this.configurationRestService = configurationRestService;
        this.triggerRestService = triggerRestService;
        this.configurationMetadataService = configurationMetadataService;

        List<String> configurationIds = moduleMetaData.getFlows().stream()
            .map(flowMetaData -> flowMetaData.getFlowElements()).flatMap(List::stream)
            .map(flowElementMetaData -> flowElementMetaData.getConfigurationId())
            .filter(id -> id != null)
            .distinct()
            .collect(Collectors.toList());

        List<ConfigurationMetaData> configurationMetaData
            = this.configurationMetadataService.findByIdList(configurationIds);

        ModuleVisjsAdapter moduleVisjsAdapter = new ModuleVisjsAdapter();

        Module module = moduleVisjsAdapter.adapt(moduleMetaData, configurationMetaData);


        this.moduleVisualisation = new ModuleVisualisation(this.moduleControlRestService,
            this.configurationRestService, this.triggerRestService);
        this.moduleVisualisation.addModule(module);

        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();

        Optional<org.ikasan.dashboard.ui.visualisation.model.flow.Flow> flow
            = this.getCurrentFlow(module.getFlows(), flowName);
        if(flow.isPresent()) {
            this.moduleVisualisation.setCurrentFlow(flow.get());
            Image flowImage = new Image("/frontend/images/flow.png", "");
            flowImage.setHeight("70px");

            H3 flowLabel = new H3(flow.get().getName());
            flowLabel.setWidthFull();

            this.flowControlPanel = new ControlPanel(this.moduleControlRestService);
            this.flowControlPanel.onChange(new GraphViewChangeEvent(module, flow.get()));

            HorizontalLayout headerLayout = new HorizontalLayout();
            headerLayout.setWidthFull();
            headerLayout.setSpacing(true);
            VerticalLayout controlPanelLayout = new VerticalLayout();
            controlPanelLayout.setWidthFull();
            controlPanelLayout.add(this.flowControlPanel);
            controlPanelLayout.setHorizontalComponentAlignment(FlexComponent.Alignment.END, this.flowControlPanel);
            headerLayout.setFlexGrow(1, flowImage);
            headerLayout.setFlexGrow(20, flowLabel);
            headerLayout.setFlexGrow(1, controlPanelLayout);

            headerLayout.add(flowImage, flowLabel, controlPanelLayout);
            headerLayout.setMargin(false);
            layout.add(headerLayout);
        }
        this.moduleVisualisation.setWidth("1600px");
        this.moduleVisualisation.setHeight("800px");
        this.moduleVisualisation.redraw();

        layout.add(this.moduleVisualisation);

        this.add(layout);
        this.setWidth("90%");
        this.setHeight("90%");
    }

    private Optional<org.ikasan.dashboard.ui.visualisation.model.flow.Flow> getCurrentFlow
        (List<org.ikasan.dashboard.ui.visualisation.model.flow.Flow> flows
        , String flowName){
        return flows.stream().filter(flow -> flowName.equals(flow.getName())).findFirst();
    }


}
