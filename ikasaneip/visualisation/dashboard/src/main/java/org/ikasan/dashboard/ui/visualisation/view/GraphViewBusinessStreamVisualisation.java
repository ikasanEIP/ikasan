package org.ikasan.dashboard.ui.visualisation.view;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;
import org.ikasan.dashboard.broadcast.FlowStateBroadcaster;
import org.ikasan.dashboard.ui.visualisation.component.BusinessStreamVisualisation;
import org.ikasan.rest.client.ConfigurationRestServiceImpl;
import org.ikasan.rest.client.ModuleControlRestServiceImpl;
import org.ikasan.rest.client.TriggerRestServiceImpl;
import org.ikasan.solr.model.IkasanSolrDocument;
import org.ikasan.solr.model.IkasanSolrDocumentSearchResults;
import org.ikasan.spec.metadata.ConfigurationMetaDataService;
import org.ikasan.spec.metadata.ModuleMetaDataService;
import org.ikasan.spec.solr.SolrGeneralService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class GraphViewBusinessStreamVisualisation extends VerticalLayout
{
    Logger logger = LoggerFactory.getLogger(GraphViewBusinessStreamVisualisation.class);

    private SolrGeneralService<IkasanSolrDocument, IkasanSolrDocumentSearchResults> solrSearchService;

    private ModuleControlRestServiceImpl moduleControlRestService;

    private ModuleMetaDataService moduleMetadataService;

    private ConfigurationRestServiceImpl configurationRestService;

    private TriggerRestServiceImpl triggerRestService;

    private ConfigurationMetaDataService configurationMetadataService;

    private BusinessStreamVisualisation businessStreamVisualisation;

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
        this.moduleControlRestService = moduleControlRestService;
        this.moduleMetadataService = moduleMetadataService;
        this.configurationRestService = configurationRestService;
        this.triggerRestService = triggerRestService;
        this.configurationMetadataService = configurationMetadataService;
    }

    private void init() {

    }

    /**
     *
     * @param json
     */
    protected void createBusinessStreamGraph(String json) throws IOException {

        if (this.businessStreamVisualisation != null) {
            this.remove(businessStreamVisualisation);
        }

        businessStreamVisualisation = new BusinessStreamVisualisation(this.moduleControlRestService,
            this.configurationRestService, this.triggerRestService, this.moduleMetadataService
            , this.configurationMetadataService, this.solrSearchService);

        businessStreamVisualisation.createBusinessStreamGraphGraph(json);

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

