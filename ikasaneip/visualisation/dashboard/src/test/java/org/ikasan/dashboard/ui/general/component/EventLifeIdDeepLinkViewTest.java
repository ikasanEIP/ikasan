package org.ikasan.dashboard.ui.general.component;

import com.vaadin.flow.component.UI;
import org.ikasan.dashboard.ui.UITest;
import org.ikasan.dashboard.ui.search.component.SolrSearchFilteringGrid;
import org.ikasan.dashboard.ui.util.SecurityConstants;
import org.ikasan.solr.model.IkasanSolrDocument;
import org.ikasan.solr.model.IkasanSolrDocumentSearchResults;
import org.ikasan.solr.service.SolrGeneralServiceImpl;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.List;

import static com.github.mvysny.kaributesting.v10.LocatorJ._get;

public class EventLifeIdDeepLinkViewTest extends UITest {

    @MockBean
    private SolrGeneralServiceImpl solrSearchService;


    @Override
    public void setup_expectations() {

    }

    @Test
    public void test_event_life_id_view_success() throws IOException
    {
        IkasanSolrDocument document = new IkasanSolrDocument();
        document.setId("id");
        document.setComponentName("component");
        document.setErrorAction("exclusion");
        document.setType("exclusion");
        document.setErrorDetail("error");
        document.setErrorUri("uri");
        document.setErrorMessage("message");
        document.setFlowName("flow");
        document.setModuleName("module");
        document.setPayloadRaw("payload".getBytes());
        document.setExceptionClass("exception.class");
        document.setEvent("event");
        document.setEventId("eventId");

        IkasanSolrDocumentSearchResults solrDocumentSearchResults = new IkasanSolrDocumentSearchResults(List.of(document, document, document), 3, 1);

        Mockito.when(this.ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
            .thenReturn(true);

        Mockito.when(this.solrSearchService.search(Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull(),
            Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyList(),
            Mockito.anyBoolean(), Mockito.isNull(), Mockito.isNull()))
            .thenReturn(solrDocumentSearchResults);

        SecurityContextHolder.getContext().setAuthentication(this.ikasanAuthentication);

        UI.getCurrent().navigate("eventLifeId/eventId");

        EventLifeIdDeepLinkView eventLifeIdDeepLinkView = _get(EventLifeIdDeepLinkView.class);
        Assertions.assertNotNull(eventLifeIdDeepLinkView);

        SearchResults searchResults = (SearchResults) ReflectionTestUtils
            .getField(eventLifeIdDeepLinkView, "searchResults");

        SolrSearchFilteringGrid solrSearchFilteringGrid = (SolrSearchFilteringGrid) ReflectionTestUtils
            .getField(searchResults, "searchResultsGrid");

        Assertions.assertEquals(3, solrSearchFilteringGrid.getResultSize(), "Search results size equals 3!");
    }

    @Test
    public void test_event_life_id_view_success_no_results() throws IOException
    {
        IkasanSolrDocumentSearchResults solrDocumentSearchResults = new IkasanSolrDocumentSearchResults(List.of(), 0, 0);

        Mockito.when(this.ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
            .thenReturn(true);

        Mockito.when(this.solrSearchService.search(Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull(),
            Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyList(),
            Mockito.anyBoolean(), Mockito.isNull(), Mockito.isNull()))
            .thenReturn(solrDocumentSearchResults);

        SecurityContextHolder.getContext().setAuthentication(this.ikasanAuthentication);

        UI.getCurrent().navigate("eventLifeId/eventId");

        EventLifeIdDeepLinkView eventLifeIdDeepLinkView = _get(EventLifeIdDeepLinkView.class);
        Assertions.assertNotNull(eventLifeIdDeepLinkView);

        SearchResults searchResults = (SearchResults) ReflectionTestUtils
            .getField(eventLifeIdDeepLinkView, "searchResults");

        SolrSearchFilteringGrid solrSearchFilteringGrid = (SolrSearchFilteringGrid) ReflectionTestUtils
            .getField(searchResults, "searchResultsGrid");

        Assertions.assertEquals(0, solrSearchFilteringGrid.getResultSize(), "Search results size equals 0!");
    }
}
