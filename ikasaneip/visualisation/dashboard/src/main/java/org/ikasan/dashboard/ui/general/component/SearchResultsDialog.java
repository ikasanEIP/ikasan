package org.ikasan.dashboard.ui.general.component;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import org.ikasan.solr.model.IkasanSolrDocument;
import org.ikasan.solr.model.IkasanSolrDocumentSearchResults;
import org.ikasan.spec.error.reporting.ErrorReportingService;
import org.ikasan.spec.hospital.service.HospitalAuditService;
import org.ikasan.spec.metadata.ModuleMetaDataService;
import org.ikasan.spec.module.client.ReplayService;
import org.ikasan.spec.module.client.ResubmissionService;
import org.ikasan.spec.persistence.BatchInsert;
import org.ikasan.spec.solr.SolrGeneralService;

import java.util.List;

public class SearchResultsDialog extends AbstractCloseableResizableDialog {

    private SearchResults searchResults;

    public SearchResultsDialog(SolrGeneralService<IkasanSolrDocument, IkasanSolrDocumentSearchResults> solrGeneralService
        , HospitalAuditService hospitalAuditService, ResubmissionService resubmissionRestService
        , ReplayService replayRestService, ModuleMetaDataService moduleMetadataService, BatchInsert replayAuditService){
        searchResults = new SearchResults(solrGeneralService, hospitalAuditService,
            resubmissionRestService, replayRestService, moduleMetadataService, replayAuditService);
        searchResults.tooltipBottom();

        searchResults.setSizeFull();

        HorizontalLayout wrapper = new HorizontalLayout();
        wrapper.setWidthFull();
        wrapper.setHeight("95%");
        wrapper.add(searchResults);
        this.content.add(wrapper);


        this.setSizeFull();
    }

    public void search(long startTime, long endTime, String searchTerm, String type, boolean negateQuery, String moduleName, String flowName) {
        this.searchResults.search(startTime, endTime, searchTerm, List.of(type), negateQuery, List.of(moduleName), List.of(flowName));
    }
}
