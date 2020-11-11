package org.ikasan.dashboard.ui.general.component;


import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.material.Material;
import org.ikasan.rest.client.ReplayRestServiceImpl;
import org.ikasan.rest.client.ResubmissionRestServiceImpl;
import org.ikasan.solr.model.IkasanSolrDocument;
import org.ikasan.solr.model.IkasanSolrDocumentSearchResults;
import org.ikasan.spec.hospital.service.HospitalAuditService;
import org.ikasan.spec.metadata.ModuleMetaDataService;
import org.ikasan.spec.persistence.BatchInsert;
import org.ikasan.spec.solr.SolrGeneralService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@HtmlImport("frontend://styles/shared-styles.html")
@HtmlImport("frontend://bower_components/vaadin-lumo-styles/presets/compact.html")
@Viewport("width=device-width, minimum-scale=1.0, initial-scale=1.0, user-scalable=yes")
@Theme(Material.class)
@PreserveOnRefresh
@Route(value = "eventLifeId")
@UIScope
@Component
public class EventLifeIdDeepLinkView extends VerticalLayout implements HasUrlParameter<String>
{
    Logger logger = LoggerFactory.getLogger(EventLifeIdDeepLinkView.class);

    private SearchResults searchResults;

    public EventLifeIdDeepLinkView(ModuleMetaDataService moduleMetadataService,
                                   SolrGeneralService<IkasanSolrDocument, IkasanSolrDocumentSearchResults> solrGeneralService,
                                   HospitalAuditService hospitalAuditService, ResubmissionRestServiceImpl resubmissionRestService,
                                   ReplayRestServiceImpl replayRestService, BatchInsert replayAuditService)
    {
        this.searchResults = new SearchResults(solrGeneralService, hospitalAuditService, resubmissionRestService
            , replayRestService, moduleMetadataService, replayAuditService);
        this.searchResults.setSizeFull();

        Button returnToDashboardButton = new Button("Return to dashboard");
        returnToDashboardButton.addClickListener(new ComponentEventListener<ClickEvent<Button>>() {
            @Override
            public void onComponentEvent(ClickEvent<Button> buttonClickEvent) {
                UI.getCurrent().navigate("");
            }
        });

        this.add(returnToDashboardButton);
        this.add(searchResults);
        this.setSizeFull();
    }


    @Override
    public void setParameter(BeforeEvent beforeEvent, String parameter) {
        logger.info(String.format("Deep link event life identifier [%s]", parameter));

        this.searchResults.search(0, System.currentTimeMillis(), parameter, List.of("wiretap", "error", "exclusion"),
            false, new ArrayList<>(), new ArrayList<>());
    }

}
