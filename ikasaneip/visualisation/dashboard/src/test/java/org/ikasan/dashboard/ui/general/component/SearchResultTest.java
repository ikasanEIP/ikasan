package org.ikasan.dashboard.ui.general.component;

import org.ikasan.dashboard.ui.UITest;
import org.ikasan.dashboard.ui.search.component.SolrSearchFilteringGrid;
import org.ikasan.error.reporting.service.SolrErrorReportingServiceImpl;
import org.ikasan.replay.service.SolrReplayAuditServiceImpl;
import org.ikasan.rest.client.ReplayRestServiceImpl;
import org.ikasan.rest.client.ResubmissionRestServiceImpl;
import org.ikasan.solr.service.SolrGeneralServiceImpl;
import org.ikasan.spec.hospital.service.HospitalAuditService;
import org.ikasan.spec.metadata.ModuleMetaDataService;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

public class SearchResultTest extends UITest {

    @MockBean
    private SolrGeneralServiceImpl solrSearchService;

    @MockBean
    private SolrErrorReportingServiceImpl solrErrorReportingService;

    @MockBean
    private HospitalAuditService hospitalAuditService;

    @MockBean
    private ResubmissionRestServiceImpl resubmissionRestService;

    @MockBean
    private ReplayRestServiceImpl replayRestService;

    @MockBean
    private ModuleMetaDataService moduleMetaDataService;

    @MockBean
    private SolrReplayAuditServiceImpl replayAuditService;

    @Override
    public void setup_expectations() {

    }

    @Test
    public void test_no_results_found() {
        SearchResults searchResults = new SearchResults(this.solrSearchService, this.hospitalAuditService
            , resubmissionRestService, replayRestService, moduleMetaDataService, replayAuditService);

        Assertions.assertNotNull(searchResults);

        SecurityContextHolder.getContext().setAuthentication(this.ikasanAuthentication);

        searchResults.search(0, System.currentTimeMillis() + 100000L, "", List.of("error", "exclusion", "wiretap"),
            false, new ArrayList<>(), new ArrayList<>());

        SolrSearchFilteringGrid solrSearchFilteringGrid = (SolrSearchFilteringGrid) ReflectionTestUtils
            .getField(searchResults, "searchResultsGrid");

        Assertions.assertEquals(0, solrSearchFilteringGrid.getResultSize(), "Search results size equals 0!");

    }
}
