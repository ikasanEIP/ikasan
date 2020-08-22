package org.ikasan.dashboard.ui.general.component;

import com.vaadin.flow.component.dialog.Dialog;
import org.ikasan.rest.client.ReplayRestServiceImpl;
import org.ikasan.rest.client.ResubmissionRestServiceImpl;
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

public class SearchResultsDialog extends Dialog {

    private SearchResults searchResults;

    public SearchResultsDialog(SolrGeneralService<IkasanSolrDocument, IkasanSolrDocumentSearchResults> solrGeneralService,
                               ErrorReportingService errorReportingService, HospitalAuditService hospitalAuditService,
                               ResubmissionService resubmissionRestService, ReplayService replayRestService,
                               ModuleMetaDataService moduleMetadataService, BatchInsert replayAuditService){
        searchResults = new SearchResults(solrGeneralService, errorReportingService, hospitalAuditService,
            resubmissionRestService, replayRestService, moduleMetadataService, replayAuditService);

        searchResults.setWidth("1600px");
        searchResults.setHeight("800px");
        this.add(searchResults);
        this.setSizeFull();
    }

    public void search(long startTime, long endTime, String searchTerm, String type, boolean negateQuery, String moduleName, String flowName) {
        this.searchResults.search(startTime, endTime, searchTerm, List.of(type), negateQuery, List.of(moduleName), List.of(flowName));
    }
}
