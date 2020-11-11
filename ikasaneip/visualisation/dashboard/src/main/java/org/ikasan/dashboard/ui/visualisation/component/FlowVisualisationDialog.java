package org.ikasan.dashboard.ui.visualisation.component;

import com.vaadin.componentfactory.Tooltip;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import org.ikasan.dashboard.ui.general.component.SearchResultsDialog;
import org.ikasan.dashboard.ui.general.component.TooltipHelper;
import org.ikasan.dashboard.ui.search.SearchConstants;
import org.ikasan.dashboard.ui.visualisation.adapter.service.ModuleVisjsAdapter;
import org.ikasan.dashboard.ui.visualisation.component.util.SearchFoundStatus;
import org.ikasan.dashboard.ui.visualisation.event.GraphViewChangeEvent;
import org.ikasan.dashboard.ui.visualisation.model.business.stream.Flow;
import org.ikasan.dashboard.ui.visualisation.model.flow.Module;
import org.ikasan.rest.client.ConfigurationRestServiceImpl;
import org.ikasan.rest.client.TriggerRestServiceImpl;
import org.ikasan.rest.client.*;
import org.ikasan.solr.model.IkasanSolrDocument;
import org.ikasan.solr.model.IkasanSolrDocumentSearchResults;
import org.ikasan.spec.error.reporting.ErrorReportingService;
import org.ikasan.spec.hospital.service.HospitalAuditService;
import org.ikasan.spec.metadata.ConfigurationMetaData;
import org.ikasan.spec.metadata.ConfigurationMetaDataService;
import org.ikasan.spec.metadata.ModuleMetaData;
import org.ikasan.spec.metadata.ModuleMetaDataService;
import org.ikasan.spec.module.client.*;
import org.ikasan.spec.persistence.BatchInsert;
import org.ikasan.spec.solr.SolrGeneralService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FlowVisualisationDialog extends Dialog {
    Logger logger = LoggerFactory.getLogger(FlowVisualisationDialog.class);

    private SolrGeneralService<IkasanSolrDocument, IkasanSolrDocumentSearchResults> solrSearchService;

    private ModuleControlService moduleControlRestService;
    private ConfigurationService configurationRestService;
    private TriggerService triggerRestService;
    private ModuleVisualisation moduleVisualisation;
    private ConfigurationMetaDataService configurationMetadataService;
    private ControlPanel flowControlPanel;

    private Button replayButton;
    private Tooltip replayButtonTooltip;
    private Button wiretapButton;
    private Tooltip wiretapButtonTooltip;
    private Button hospitalButton;
    private Tooltip hospitalButtonTooltip;
    private Button errorButton;
    private Tooltip errorButtonTooltip;

    private SearchFoundStatus searchFoundStatus;

    private VerticalLayout searchLayout;

    private Flow flow;

    private HospitalAuditService hospitalAuditService;

    private ResubmissionService resubmissionRestService;

    private ReplayService replayRestService;

    private ModuleMetaDataService moduleMetadataService;

    private BatchInsert replayAuditService;

    private MetaDataService metaDataApplicationRestService;

    private BatchInsert<ModuleMetaData> moduleMetaDataService;

    public FlowVisualisationDialog(ModuleControlService moduleControlRestService
        , ConfigurationService configurationRestService
        , TriggerService triggerRestService, ConfigurationMetaDataService configurationMetadataService
        , ModuleMetaData moduleMetaData, Flow flow, SolrGeneralService<IkasanSolrDocument
        , IkasanSolrDocumentSearchResults> solrSearchService, SearchFoundStatus searchFoundStatus
        , HospitalAuditService hospitalAuditService
        , ResubmissionService resubmissionRestService, ReplayService replayRestService
        , ModuleMetaDataService moduleMetadataService, BatchInsert replayAuditService
        , MetaDataService metaDataApplicationRestService, BatchInsert<ModuleMetaData> moduleMetaDataService)
    {
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
        this.solrSearchService = solrSearchService;
        if(this.solrSearchService == null){
            throw new IllegalArgumentException("solrSearchService cannot be null!");
        }
        this.searchFoundStatus = searchFoundStatus;
        if(this.searchFoundStatus == null){
            throw new IllegalArgumentException("searchFoundStatus cannot be null!");
        }
        this.flow = flow;
        if(this.flow == null){
            throw new IllegalArgumentException("flow cannot be null!");
        }
        if(moduleMetaData == null){
            throw new IllegalArgumentException("moduleMetaData cannot be null!");
        }
        this.hospitalAuditService = hospitalAuditService;
        if (this.hospitalAuditService == null) {
            throw new IllegalArgumentException("hospitalAuditService cannot be null!");
        }
        this.resubmissionRestService = resubmissionRestService;
        if (this.resubmissionRestService == null) {
            throw new IllegalArgumentException("resubmissionRestService cannot be null!");
        }
        this.replayRestService = replayRestService;
        if (this.replayRestService == null) {
            throw new IllegalArgumentException("replayRestService cannot be null!");
        }
        this.moduleMetadataService = moduleMetadataService;
        if (this.moduleMetadataService == null) {
            throw new IllegalArgumentException("moduleMetadataService cannot be null!");
        }
        this.replayAuditService = replayAuditService;
        if (this.replayAuditService == null) {
            throw new IllegalArgumentException("replayAuditService cannot be null!");
        }
        this.metaDataApplicationRestService = metaDataApplicationRestService;
        if (this.metaDataApplicationRestService == null) {
            throw new IllegalArgumentException("metaDataApplicationRestService cannot be null!");
        }
        this.moduleMetaDataService = moduleMetaDataService;
        if (this.moduleMetaDataService == null) {
            throw new IllegalArgumentException("moduleMetaDataService cannot be null!");
        }


        this.init(moduleMetaData, flow.getFlowName());
    }

    private void init(ModuleMetaData moduleMetaData, String flowName){
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
            this.configurationRestService, this.triggerRestService, metaDataApplicationRestService,
            this.moduleMetaDataService);
        this.moduleVisualisation.addModule(module);

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
            this.add(headerLayout);
        }
        this.moduleVisualisation.setWidth("1400px");
        this.moduleVisualisation.setHeight("80vh");

        this.searchLayout = this.buildSearchLayout();

        HorizontalLayout bottomLayout = new HorizontalLayout();
        bottomLayout.setFlexGrow(20, this.moduleVisualisation);
        bottomLayout.setFlexGrow(1, this.searchLayout);

        bottomLayout.add(this.moduleVisualisation, this.searchLayout);

        this.add(bottomLayout);
        this.setWidth("98vw");
        this.setHeight("98vh");
    }

    private VerticalLayout buildSearchLayout(){
        VerticalLayout serviceLayout = new VerticalLayout();

        Image wiretapImage = new Image("frontend/images/wiretap-service.png", "");
        wiretapImage.setHeight("40px");
        wiretapButton = new Button(wiretapImage);
        wiretapButtonTooltip = TooltipHelper.getTooltipForComponentTopLeft(wiretapButton, getTranslation("tooltip.search-wiretap-events", UI.getCurrent().getLocale()));
        if(!this.searchFoundStatus.getWiretapFound()) {
            this.wiretapButton.setVisible(false);
        }
        else {
            addButtonSearchListener(SearchConstants.WIRETAP, wiretapButton);
            serviceLayout.add(createSearchButtonLayout(wiretapButton), wiretapButtonTooltip);
        }

        Image errorImage = new Image("frontend/images/error-service.png", "");
        errorImage.setHeight("40px");
        errorButton = new Button(errorImage);
        errorButtonTooltip = TooltipHelper.getTooltipForComponentTopLeft(errorButton, getTranslation("tooltip.search-error-events", UI.getCurrent().getLocale()));
        if(!this.searchFoundStatus.getErrorFound()) {
            this.errorButton.setVisible(false);
        }
        else {
            addButtonSearchListener(SearchConstants.ERROR, errorButton);
            serviceLayout.add(createSearchButtonLayout(errorButton), errorButtonTooltip);
        }


        Image hospitalImage = new Image("frontend/images/hospital-service.png", "");
        hospitalImage.setHeight("40px");
        hospitalButton = new Button(hospitalImage);
        hospitalButtonTooltip = TooltipHelper.getTooltipForComponentTopLeft(hospitalButton, getTranslation("tooltip.search-hospital-events", UI.getCurrent().getLocale()));
        if(!this.searchFoundStatus.getExclusionFound()) {
            this.hospitalButton.setVisible(false);
        }
        else {
            addButtonSearchListener(SearchConstants.EXCLUSION, hospitalButton);
            serviceLayout.add(createSearchButtonLayout(hospitalButton), hospitalButtonTooltip);
        }

        Image replayButtonImage = new Image("frontend/images/replay-service.png", "");
        replayButtonImage.setHeight("40px");
        replayButton = new Button(replayButtonImage);
        replayButtonTooltip = TooltipHelper.getTooltipForComponentTopLeft(replayButton, getTranslation("tooltip.search-replay-events"
            , UI.getCurrent().getLocale()));
        if(!this.searchFoundStatus.getReplayFound()) {
            this.replayButton.setVisible(false);
        }
        else {
            addButtonSearchListener(SearchConstants.REPLAY, replayButton);
            serviceLayout.add(createSearchButtonLayout(replayButton), replayButtonTooltip);
        }

        serviceLayout.setWidth("50px");

        return serviceLayout;
    }

    /**
     * Method to perform the search.
     *
     * @param type the entity type
     */
    protected void search(String type)
    {
        SearchResultsDialog searchResultsDialog = new SearchResultsDialog(this.solrSearchService, this.hospitalAuditService,
            this.resubmissionRestService, this.replayRestService, this.moduleMetadataService, this.replayAuditService);
        searchResultsDialog.search(this.searchFoundStatus.getStartTime(), this.searchFoundStatus.getEndTime(), searchFoundStatus.getSearchTerm()
            , type, false, flow.getModuleName(), flow.getFlowName());
        searchResultsDialog.open();
    }

    /**
     * Add the search listener to a button.
     *
     * @param searchType
     * @param button
     */
    private void addButtonSearchListener(String searchType, Button button)
    {
        button.addClickListener((ComponentEventListener<ClickEvent<Button>>) buttonClickEvent ->
        {
            search(searchType);
        });
    }

    /**
     * Create the button layout
     *
     * @param button
     * @return
     */
    private VerticalLayout createSearchButtonLayout(Button button)
    {
        VerticalLayout buttonLayout = new VerticalLayout();
        buttonLayout.setSpacing(false);
        buttonLayout.setMargin(false);
        buttonLayout.setHeight("40px");
        buttonLayout.setWidth("40px");
        button.setHeight("40px");
        button.setWidth("40px");

        buttonLayout.add(button);
        buttonLayout.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, button);

        return buttonLayout;
    }

    private Optional<org.ikasan.dashboard.ui.visualisation.model.flow.Flow> getCurrentFlow
        (List<org.ikasan.dashboard.ui.visualisation.model.flow.Flow> flows
        , String flowName){
        return flows.stream().filter(flow -> flowName.equals(flow.getName())).findFirst();
    }


}
