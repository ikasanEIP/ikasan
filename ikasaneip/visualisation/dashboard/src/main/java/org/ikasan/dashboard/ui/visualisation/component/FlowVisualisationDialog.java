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
import org.ikasan.dashboard.ui.visualisation.event.GraphViewChangeEvent;
import org.ikasan.dashboard.ui.visualisation.model.flow.Module;
import org.ikasan.rest.client.ConfigurationRestServiceImpl;
import org.ikasan.rest.client.ModuleControlRestServiceImpl;
import org.ikasan.rest.client.TriggerRestServiceImpl;
import org.ikasan.solr.model.IkasanSolrDocument;
import org.ikasan.solr.model.IkasanSolrDocumentSearchResults;
import org.ikasan.spec.metadata.ConfigurationMetaData;
import org.ikasan.spec.metadata.ConfigurationMetaDataService;
import org.ikasan.spec.metadata.ModuleMetaData;
import org.ikasan.spec.solr.SolrGeneralService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FlowVisualisationDialog extends Dialog {
    private SolrGeneralService<IkasanSolrDocument, IkasanSolrDocumentSearchResults> solrSearchService;

    private ModuleControlRestServiceImpl moduleControlRestService;
    private ConfigurationRestServiceImpl configurationRestService;
    private TriggerRestServiceImpl triggerRestService;
    private ModuleVisualisation moduleVisualisation;
    private ConfigurationMetaDataService configurationMetadataService;
    private ControlPanel flowControlPanel;

    private Button allButton;
    private Tooltip allButtonTooltip;
    private Button wiretapButton;
    private Tooltip wiretapButtonTooltip;
    private Button hospitalButton;
    private Tooltip hospitalButtonTooltip;
    private Button errorButton;
    private Tooltip errorButtonTooltip;

    private VerticalLayout searchLayout;

    public FlowVisualisationDialog(ModuleControlRestServiceImpl moduleControlRestService
        , ConfigurationRestServiceImpl configurationRestService
        , TriggerRestServiceImpl triggerRestService, ConfigurationMetaDataService configurationMetadataService
        , ModuleMetaData moduleMetaData, String flowName, SolrGeneralService<IkasanSolrDocument, IkasanSolrDocumentSearchResults> solrSearchService)
    {
        this.moduleControlRestService = moduleControlRestService;
        this.configurationRestService = configurationRestService;
        this.triggerRestService = triggerRestService;
        this.configurationMetadataService = configurationMetadataService;
        this.solrSearchService = solrSearchService;

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

        this.searchLayout = this.buildSearchLayout();

        HorizontalLayout bottomLayout = new HorizontalLayout();
        bottomLayout.setFlexGrow(20, this.moduleVisualisation);
        bottomLayout.setFlexGrow(1, this.searchLayout);

        bottomLayout.add(this.moduleVisualisation, this.searchLayout);

        layout.add(bottomLayout);

        this.add(layout);
        this.setWidth("90%");
        this.setHeight("90%");
    }

    private VerticalLayout buildSearchLayout(){
        VerticalLayout serviceLayout = new VerticalLayout();

        Image allButtonImage = new Image("frontend/images/all-services-icon.png", "");
        allButtonImage.setHeight("40px");
        allButton = new Button(allButtonImage);

        addButtonSearchListener(SearchConstants.ALL, allButton);
        allButtonTooltip = TooltipHelper.getTooltipForComponentTopLeft(allButton, getTranslation("tooltip.search-all-event-types"
            , UI.getCurrent().getLocale()));

        serviceLayout.add(createSearchButtonLayout(allButton), allButtonTooltip);

        Image wiretapImage = new Image("frontend/images/wiretap-service.png", "");
        wiretapImage.setHeight("40px");
        wiretapButton = new Button(wiretapImage);
        wiretapButtonTooltip = TooltipHelper.getTooltipForComponentTopLeft(wiretapButton, getTranslation("tooltip.search-wiretap-events", UI.getCurrent().getLocale()));

        addButtonSearchListener(SearchConstants.WIRETAP, wiretapButton);
        serviceLayout.add(createSearchButtonLayout(wiretapButton), wiretapButtonTooltip);

        Image errorImage = new Image("frontend/images/error-service.png", "");
        errorImage.setHeight("40px");
        errorButton = new Button(errorImage);
        errorButtonTooltip = TooltipHelper.getTooltipForComponentTopLeft(errorButton, getTranslation("tooltip.search-error-events", UI.getCurrent().getLocale()));

        addButtonSearchListener(SearchConstants.ERROR, errorButton);
        serviceLayout.add(createSearchButtonLayout(errorButton), errorButtonTooltip);

        Image hospitalImage = new Image("frontend/images/hospital-service.png", "");
        hospitalImage.setHeight("40px");
        hospitalButton = new Button(hospitalImage);
        hospitalButtonTooltip = TooltipHelper.getTooltipForComponentTopLeft(hospitalButton, getTranslation("tooltip.search-hospital-events", UI.getCurrent().getLocale()));

        addButtonSearchListener(SearchConstants.EXCLUSION, hospitalButton);
        serviceLayout.add(createSearchButtonLayout(hospitalButton), hospitalButtonTooltip);

        serviceLayout.setWidth("50px");

        return serviceLayout;
    }

    /**
     * Method to perform the search.
     *
     * @param type the entity type
     * @param startDate the start date/time of the search
     * @param endDate the end date/time of the search
     */
    protected void search(String type, long startDate, long endDate)
    {
        SearchResultsDialog searchResultsDialog = new SearchResultsDialog(this.solrSearchService);
        searchResultsDialog.open();

//        this.currentSearchType = type;
//
//        ArrayList<String> types = new ArrayList<>();
//
//        if(type.equals(ALL))
//        {
//            if (ComponentSecurityVisibility.hasAuthorisation(SecurityConstants.SEARCH_REPLAY_WRITE, SecurityConstants.ALL_AUTHORITY))
//            {
//                types.add(SolrReplayDao.REPLAY);
//            }
//
//            if (ComponentSecurityVisibility.hasAuthorisation(SecurityConstants.SEARCH_WRITE, SecurityConstants.SEARCH_ADMIN, SecurityConstants.SEARCH_READ, SecurityConstants.ALL_AUTHORITY))
//            {
//                if(ComponentSecurityVisibility.hasAuthorisation(SecurityConstants.WIRETAP_READ, SecurityConstants.WIRETAP_WRITE, SecurityConstants.WIRETAP_ADMIN, SecurityConstants.ALL_AUTHORITY))
//                {
//                    types.add(SolrWiretapDao.WIRETAP);
//                }
//
//                if(ComponentSecurityVisibility.hasAuthorisation(SecurityConstants.EXCLUSION_READ, SecurityConstants.EXCLUSION_ADMIN, SecurityConstants.EXCLUSION_WRITE, SecurityConstants.ALL_AUTHORITY))
//                {
//                    types.add(SolrExclusionEventDao.EXCLUSION);
//                }
//
//                if(ComponentSecurityVisibility.hasAuthorisation(SecurityConstants.ERROR_WRITE, SecurityConstants.ERROR_READ, SecurityConstants.ERROR_ADMIN, SecurityConstants.ALL_AUTHORITY))
//                {
//                    types.add(SolrErrorReportingServiceDao.ERROR);
//                }
//            }
//        }
//        else
//        {
//            if(type.equals(REPLAY))
//            {
//                types.add(SolrReplayDao.REPLAY);
//            }
//
//            if(type.equals(WIRETAP))
//            {
//                types.add(SolrWiretapDao.WIRETAP);
//            }
//
//            if(type.equals(EXCLUSION))
//            {
//                types.add(SolrExclusionEventDao.EXCLUSION);
//            }
//
//            if(type.equals(ERROR))
//            {
//                types.add(SolrErrorReportingServiceDao.ERROR);
//            }
//        }
//
//        this.selectionBoxes = new HashMap<>();
//        this.selectionItems = new HashMap<>();
//
//        this.searchResultsGrid.init(startDate, endDate, searchTerm, types);
//
//        if(selected)
//        {
//            toggleSelected();
//        }
//        functionalGroupSetup();
//        this.resultsLabel.setVisible(true);
//        this.addReplayButtonEventListener();
//        this.addHospitalResubmitButtonEventListener();
//        this.addIgnoreButtonEventListener();
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
            search(searchType,
                System.currentTimeMillis(), System.currentTimeMillis());
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
