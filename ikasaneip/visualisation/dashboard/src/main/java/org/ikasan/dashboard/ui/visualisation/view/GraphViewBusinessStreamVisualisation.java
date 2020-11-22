package org.ikasan.dashboard.ui.visualisation.view;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;
import org.ikasan.dashboard.broadcast.FlowStateBroadcaster;
import org.ikasan.dashboard.ui.search.listener.SearchListener;
import org.ikasan.dashboard.ui.visualisation.component.BusinessStreamStatusPanel;
import org.ikasan.dashboard.ui.visualisation.component.BusinessStreamVisualisation;
import org.ikasan.dashboard.ui.visualisation.event.GraphViewChangeEvent;
import org.ikasan.dashboard.ui.visualisation.event.GraphViewChangeListener;
import org.ikasan.dashboard.ui.visualisation.model.business.stream.BusinessStream;
import org.ikasan.solr.model.IkasanSolrDocument;
import org.ikasan.solr.model.IkasanSolrDocumentSearchResults;
import org.ikasan.spec.error.reporting.ErrorReportingService;
import org.ikasan.spec.hospital.service.HospitalAuditService;
import org.ikasan.spec.metadata.BusinessStreamMetaData;
import org.ikasan.spec.metadata.ConfigurationMetaDataService;
import org.ikasan.spec.metadata.ModuleMetaData;
import org.ikasan.spec.metadata.ModuleMetaDataService;
import org.ikasan.spec.module.client.*;
import org.ikasan.spec.persistence.BatchInsert;
import org.ikasan.spec.solr.SolrGeneralService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GraphViewBusinessStreamVisualisation extends VerticalLayout implements SearchListener
{
    Logger logger = LoggerFactory.getLogger(GraphViewBusinessStreamVisualisation.class);

    private SolrGeneralService<IkasanSolrDocument, IkasanSolrDocumentSearchResults> solrSearchService;

    private ModuleControlService moduleControlRestService;

    private ModuleMetaDataService moduleMetadataService;

    private ConfigurationService configurationRestService;

    private TriggerService triggerRestService;

    private ConfigurationMetaDataService configurationMetadataService;

    private BusinessStreamVisualisation businessStreamVisualisation;

    private HorizontalLayout headerLayout = new HorizontalLayout();

    private H2 businessStreamLabel = new H2();

    private Registration broadcasterRegistration;

    private HospitalAuditService hospitalAuditService;

    private ResubmissionService resubmissionRestService;

    private ReplayService replayRestService;

    private BatchInsert replayAuditService;

    private MetaDataService metaDataApplicationRestService;

    private BatchInsert<ModuleMetaData> moduleMetaDataBatchInsert;

    private BusinessStreamStatusPanel businessStreamStatusPanel;

    private List<GraphViewChangeListener> graphViewChangeListeners;

    /**
     * Constructor
     */
    public GraphViewBusinessStreamVisualisation(SolrGeneralService<IkasanSolrDocument, IkasanSolrDocumentSearchResults> solrSearchService
        , ModuleControlService moduleControlRestService, ModuleMetaDataService moduleMetadataService, ConfigurationService configurationRestService
        , TriggerService triggerRestService, ConfigurationMetaDataService configurationMetadataService, HospitalAuditService hospitalAuditService
        , ResubmissionService resubmissionRestService, ReplayService replayRestService, BatchInsert replayAuditService, MetaDataService metaDataApplicationRestService
        , BatchInsert<ModuleMetaData> moduleMetaDataBatchInsert)
    {
        this.setMargin(false);
        this.setSizeFull();

        this.solrSearchService = solrSearchService;
        if(this.solrSearchService == null){
            throw new IllegalArgumentException("solrSearchService cannot be null!");
        }
        this.moduleControlRestService = moduleControlRestService;
        if(this.moduleControlRestService == null){
            throw new IllegalArgumentException("moduleControlRestService cannot be null!");
        }
        this.moduleMetadataService = moduleMetadataService;
        if(this.moduleMetadataService == null){
            throw new IllegalArgumentException("moduleMetadataService cannot be null!");
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
        this.replayAuditService = replayAuditService;
        if (this.replayAuditService == null) {
            throw new IllegalArgumentException("replayAuditService cannot be null!");
        }
        this.metaDataApplicationRestService = metaDataApplicationRestService;
        if (this.metaDataApplicationRestService == null) {
            throw new IllegalArgumentException("metaDataApplicationRestService cannot be null!");
        }
        this.moduleMetaDataBatchInsert = moduleMetaDataBatchInsert;
        if (this.moduleMetaDataBatchInsert == null) {
            throw new IllegalArgumentException("moduleMetaDataBatchInsert cannot be null!");
        }

        this.graphViewChangeListeners = new ArrayList<>();

        init();
    }

    private void init() {
        this.headerLayout = new HorizontalLayout();
        this.headerLayout.setWidth("100%");
        this.headerLayout.setHeight("50px");
        this.headerLayout.add(this.businessStreamLabel);
        this.headerLayout.setVerticalComponentAlignment(Alignment.CENTER, this.businessStreamLabel);
        this.headerLayout.setSpacing(false);
        this.headerLayout.setMargin(false);

        this.businessStreamStatusPanel = new BusinessStreamStatusPanel(moduleControlRestService, moduleMetadataService);
        this.graphViewChangeListeners.add(businessStreamStatusPanel);

        this.headerLayout.add(businessStreamStatusPanel);

        this.add(this.headerLayout);
    }

    /**
     * Method to perform the search.
     *
     * @param searchTerm the search term
     * @param startDate the start date/time of the search
     * @param endDate the end date/time of the search
     */
    public void search(String searchTerm, List<String> entityTypes, boolean negateQuery, long startDate, long endDate) {
        this.businessStreamVisualisation.search(entityTypes, searchTerm, startDate, endDate);
    }


    protected void createBusinessStreamGraph(String name, BusinessStreamMetaData businessStreamMetaData) throws IOException {

        if (this.businessStreamVisualisation != null) {
            this.remove(businessStreamVisualisation);
        }

        businessStreamVisualisation = new BusinessStreamVisualisation(this.moduleControlRestService,
            this.configurationRestService, this.triggerRestService, this.moduleMetadataService
            , this.configurationMetadataService, this.solrSearchService, this.hospitalAuditService,
            this.resubmissionRestService, this.replayRestService, this.moduleMetadataService, this.replayAuditService,
            this.metaDataApplicationRestService, this.moduleMetaDataBatchInsert);

        businessStreamVisualisation.createBusinessStreamGraphGraph(businessStreamMetaData);

        this.businessStreamLabel.setText(name);
        this.businessStreamStatusPanel.setBusinessStream((org.ikasan.business.stream.metadata.model.BusinessStream)
            businessStreamMetaData.getBusinessStream());
        this.businessStreamStatusPanel.setBusinessStreamVisualisation(businessStreamVisualisation);

        this.add(businessStreamVisualisation);

        this.fireModuleFlowChangeEvent();
    }

    @Override
    protected void onAttach(AttachEvent attachEvent)
    {
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
    protected void onDetach(DetachEvent detachEvent)
    {
        broadcasterRegistration.remove();
        broadcasterRegistration = null;
    }

    protected void fireModuleFlowChangeEvent() {
        GraphViewChangeEvent graphViewChangeEvent = new GraphViewChangeEvent();

        for (GraphViewChangeListener graphViewChangeListener : this.graphViewChangeListeners) {
            graphViewChangeListener.onChange(graphViewChangeEvent);
        }
    }

    public BusinessStream getBusinessStream() {
        if(this.businessStreamVisualisation != null) {
            return this.businessStreamVisualisation.getBusinessStream();
        }

        return null;
    }
}

