package org.ikasan.dashboard.ui.visualisation.view;

import com.vaadin.componentfactory.Tooltip;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.shared.Registration;
import org.ikasan.dashboard.broadcast.FlowStateBroadcaster;
import org.ikasan.dashboard.ui.general.component.ComponentSecurityVisibility;
import org.ikasan.dashboard.ui.general.component.TooltipHelper;
import org.ikasan.dashboard.ui.search.SearchConstants;
import org.ikasan.dashboard.ui.search.listener.SearchListener;
import org.ikasan.dashboard.ui.util.DateTimeUtil;
import org.ikasan.dashboard.ui.util.SecurityConstants;
import org.ikasan.dashboard.ui.visualisation.component.BusinessStreamVisualisation;
import org.ikasan.error.reporting.dao.SolrErrorReportingServiceDao;
import org.ikasan.exclusion.dao.SolrExclusionEventDao;
import org.ikasan.replay.dao.SolrReplayDao;
import org.ikasan.rest.client.ConfigurationRestServiceImpl;
import org.ikasan.rest.client.ModuleControlRestServiceImpl;
import org.ikasan.rest.client.TriggerRestServiceImpl;
import org.ikasan.solr.model.IkasanSolrDocument;
import org.ikasan.solr.model.IkasanSolrDocumentSearchResults;
import org.ikasan.spec.metadata.ConfigurationMetaDataService;
import org.ikasan.spec.metadata.ModuleMetaDataService;
import org.ikasan.spec.solr.SolrGeneralService;
import org.ikasan.wiretap.dao.SolrWiretapDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;

public class GraphViewBusinessStreamVisualisation extends VerticalLayout implements SearchListener
{
    Logger logger = LoggerFactory.getLogger(GraphViewBusinessStreamVisualisation.class);

    private SolrGeneralService<IkasanSolrDocument, IkasanSolrDocumentSearchResults> solrSearchService;

    private ModuleControlRestServiceImpl moduleControlRestService;

    private ModuleMetaDataService moduleMetadataService;

    private ConfigurationRestServiceImpl configurationRestService;

    private TriggerRestServiceImpl triggerRestService;

    private ConfigurationMetaDataService configurationMetadataService;

    private BusinessStreamVisualisation businessStreamVisualisation;

    private HorizontalLayout headerLayout = new HorizontalLayout();

    private H2 moduleLabel = new H2();

    private Registration broadcasterRegistration;

    /**
     * Constructor
     */
    public GraphViewBusinessStreamVisualisation(SolrGeneralService<IkasanSolrDocument, IkasanSolrDocumentSearchResults> solrSearchService
        , ModuleControlRestServiceImpl moduleControlRestService, ModuleMetaDataService moduleMetadataService, ConfigurationRestServiceImpl configurationRestService
        , TriggerRestServiceImpl triggerRestService, ConfigurationMetaDataService configurationMetadataService)
    {
        this.setMargin(true);
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

        init();
    }

    private void init() {
        this.headerLayout = new HorizontalLayout();
        headerLayout.add(this.moduleLabel);
        this.add(this.headerLayout);
    }

    /**
     * Method to perform the search.
     *
     * @param searchTerm the search term
     * @param startDate the start date/time of the search
     * @param endDate the end date/time of the search
     */
    public void search(String searchTerm, long startDate, long endDate)
    {
        ArrayList<String> types = new ArrayList<>();

        if("ALL".equals(SearchConstants.ALL))
        {
            if (ComponentSecurityVisibility.hasAuthorisation(SecurityConstants.SEARCH_REPLAY_WRITE, SecurityConstants.ALL_AUTHORITY))
            {
                types.add(SolrReplayDao.REPLAY);
            }

            if (ComponentSecurityVisibility.hasAuthorisation(SecurityConstants.SEARCH_WRITE, SecurityConstants.SEARCH_ADMIN, SecurityConstants.SEARCH_READ, SecurityConstants.ALL_AUTHORITY))
            {
                if(ComponentSecurityVisibility.hasAuthorisation(SecurityConstants.WIRETAP_READ, SecurityConstants.WIRETAP_WRITE, SecurityConstants.WIRETAP_ADMIN, SecurityConstants.ALL_AUTHORITY))
                {
                    types.add(SolrWiretapDao.WIRETAP);
                }

                if(ComponentSecurityVisibility.hasAuthorisation(SecurityConstants.EXCLUSION_READ, SecurityConstants.EXCLUSION_ADMIN, SecurityConstants.EXCLUSION_WRITE, SecurityConstants.ALL_AUTHORITY))
                {
                    types.add(SolrExclusionEventDao.EXCLUSION);
                }

                if(ComponentSecurityVisibility.hasAuthorisation(SecurityConstants.ERROR_WRITE, SecurityConstants.ERROR_READ, SecurityConstants.ERROR_ADMIN, SecurityConstants.ALL_AUTHORITY))
                {
                    types.add(SolrErrorReportingServiceDao.ERROR);
                }
            }
        }

        this.businessStreamVisualisation.search(types, searchTerm, startDate, endDate);
    }

    /**
     *
     * @param json
     */
    protected void createBusinessStreamGraph(String name, String json) throws IOException {

        if (this.businessStreamVisualisation != null) {
            this.remove(businessStreamVisualisation);
        }

        businessStreamVisualisation = new BusinessStreamVisualisation(this.moduleControlRestService,
            this.configurationRestService, this.triggerRestService, this.moduleMetadataService
            , this.configurationMetadataService, this.solrSearchService);

        businessStreamVisualisation.createBusinessStreamGraphGraph(json);

        this.moduleLabel.setText(name);

        this.add(businessStreamVisualisation);
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
}

