package org.ikasan.dashboard.ui.general.component;

import com.github.mvysny.kaributesting.v10.GridKt;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.SortDirection;
import io.github.ciesielskis.AceEditor;
import org.ikasan.dashboard.ui.UITest;
import org.ikasan.dashboard.ui.search.component.SearchForm;
import org.ikasan.dashboard.ui.search.component.SolrSearchFilteringGrid;
import org.ikasan.error.reporting.service.SolrErrorReportingServiceImpl;
import org.ikasan.replay.service.SolrReplayAuditServiceImpl;
import org.ikasan.rest.client.ReplayRestServiceImpl;
import org.ikasan.rest.client.ResubmissionRestServiceImpl;
import org.ikasan.solr.model.IkasanSolrDocument;
import org.ikasan.solr.model.IkasanSolrDocumentSearchResults;
import org.ikasan.solr.service.SolrGeneralServiceImpl;
import org.ikasan.spec.hospital.service.HospitalAuditService;
import org.ikasan.spec.metadata.ModuleMetaDataService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static com.github.mvysny.kaributesting.v10.ButtonKt._click;
import static com.github.mvysny.kaributesting.v10.LocatorJ._get;
import static org.mockito.ArgumentMatchers.eq;

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

    @Test
    public void test_search_and_filter_user_all()
    {
        Mockito.when(this.solrSearchService.search(Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull(),
            Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyList(),
            Mockito.anyBoolean(), Mockito.isNull(), Mockito.isNull()))
            .thenReturn(this.getSolrResults(50, "wiretap"));

        Mockito.when(this.solrSearchService.search(Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.anyString(),
            Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyList(),
            Mockito.anyBoolean(), Mockito.isNull(), Mockito.isNull()))
            .thenReturn(this.getSolrResults(1, "wiretap"));

        SearchResults searchResults = _get(SearchResults.class);
        Assertions.assertNotNull(searchResults);

        SolrSearchFilteringGrid solrSearchFilteringGrid = (SolrSearchFilteringGrid) ReflectionTestUtils
            .getField(searchResults, "searchResultsGrid");

        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));
        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));

        Assert.assertEquals(50, solrSearchFilteringGrid.getResultSize());

        TextField eventFilter = _get(TextField.class, spec -> spec.withId("event"));
        eventFilter.setValue("event1");

        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));

        Assert.assertEquals(1, solrSearchFilteringGrid.getResultSize());
    }

    @Test
    public void test_search_and_grid_rendered_with_wiretap_image()
    {
        Mockito.when(this.solrSearchService.search(Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull(),
            Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyList(),
            Mockito.anyBoolean(), Mockito.isNull(), Mockito.isNull()))
            .thenReturn(this.getSolrResults(1, "wiretap"));

        SearchResults searchResults = _get(SearchResults.class);
        Assertions.assertNotNull(searchResults);

        SolrSearchFilteringGrid solrSearchFilteringGrid = (SolrSearchFilteringGrid) ReflectionTestUtils
            .getField(searchResults, "searchResultsGrid");

        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));
        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));

        IkasanSolrDocument row = GridKt._get(solrSearchFilteringGrid, 0);
        Grid.Column<IkasanSolrDocument> column = GridKt._getColumnByKey(solrSearchFilteringGrid, "entityImage");
        String formatted = GridKt._getFormatted(column, row);

        Assert.assertEquals("HorizontalLayout[#frontend/images/wiretap-service.png, width:100% justify-content:center]", formatted);
    }

    @Test
    public void test_search_and_wiretap_row_double_clicked()
    {
        Mockito.when(this.solrSearchService.search(Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull(),
            Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyList(),
            Mockito.anyBoolean(), Mockito.isNull(), Mockito.isNull()))
            .thenReturn(this.getSolrResults(1, "wiretap"));

        SearchResults searchResults = _get(SearchResults.class);
        Assertions.assertNotNull(searchResults);

        SolrSearchFilteringGrid solrSearchFilteringGrid = (SolrSearchFilteringGrid) ReflectionTestUtils
            .getField(searchResults, "searchResultsGrid");

        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));
        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));

        GridKt._doubleClickItem(solrSearchFilteringGrid, 0);

        WiretapDialog wiretapDialog = _get(WiretapDialog.class);
        Assert.assertNotNull(wiretapDialog);

        IkasanSolrDocument row = GridKt._get(solrSearchFilteringGrid, 0);

        Assertions.assertEquals(row.getModuleName(), ((TextField)ReflectionTestUtils
            .getField(wiretapDialog, "moduleNameTf")).getValue());
        Assertions.assertEquals(row.getFlowName(), ((TextField)ReflectionTestUtils
            .getField(wiretapDialog, "flowNameTf")).getValue());
        Assertions.assertEquals(row.getComponentName(), ((TextField)ReflectionTestUtils
            .getField(wiretapDialog, "componentNameTf")).getValue());
        Assertions.assertEquals(Anchor.class, ((TextField)ReflectionTestUtils
            .getField(wiretapDialog, "eventIdTf")).getPrefixComponent().getClass());
    }

    @Test
    public void test_search_and_grid_rendered_with_error_image()
    {
        Mockito.when(this.solrSearchService.search(Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull(),
            Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyList(),
            Mockito.anyBoolean(), Mockito.isNull(), Mockito.isNull()))
            .thenReturn(this.getSolrResults(1, "error"));

        SearchResults searchResults = _get(SearchResults.class);
        Assertions.assertNotNull(searchResults);

        SolrSearchFilteringGrid solrSearchFilteringGrid = (SolrSearchFilteringGrid) ReflectionTestUtils
            .getField(searchResults, "searchResultsGrid");

        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));
        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));

        IkasanSolrDocument row = GridKt._get(solrSearchFilteringGrid, 0);
        Grid.Column<IkasanSolrDocument> column = GridKt._getColumnByKey(solrSearchFilteringGrid, "entityImage");
        String formatted = GridKt._getFormatted(column, row);

        Assert.assertEquals("HorizontalLayout[#frontend/images/error-service.png, width:100% justify-content:center]", formatted);
    }

    @Test
    public void test_search_and_error_row_double_clicked()
    {
        Mockito.when(this.solrSearchService.search(Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull(),
            Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyList(),
            Mockito.anyBoolean(), Mockito.isNull(), Mockito.isNull()))
            .thenReturn(this.getSolrResults(1, "error"));

        SearchResults searchResults = _get(SearchResults.class);
        Assertions.assertNotNull(searchResults);

        SolrSearchFilteringGrid solrSearchFilteringGrid = (SolrSearchFilteringGrid) ReflectionTestUtils
            .getField(searchResults, "searchResultsGrid");

        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));
        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));

        GridKt._doubleClickItem(solrSearchFilteringGrid, 0);

        ErrorDialog errorDialog = _get(ErrorDialog.class);
        Assert.assertNotNull(errorDialog);

        IkasanSolrDocument row = GridKt._get(solrSearchFilteringGrid, 0);

        Assertions.assertEquals(row.getModuleName(), ((TextField)ReflectionTestUtils
            .getField(errorDialog, "moduleNameTf")).getValue());
        Assertions.assertEquals(row.getFlowName(), ((TextField)ReflectionTestUtils
            .getField(errorDialog, "flowNameTf")).getValue());
        Assertions.assertEquals(row.getComponentName(), ((TextField)ReflectionTestUtils
            .getField(errorDialog, "componentNameTf")).getValue());
        Assertions.assertEquals(row.getEventId(), ((TextField)ReflectionTestUtils
            .getField(errorDialog, "eventIdTf")).getValue());
        Assertions.assertEquals(row.getErrorAction(), ((TextField)ReflectionTestUtils
            .getField(errorDialog, "errorActionTf")).getValue());
        Assertions.assertEquals(row.getErrorUri(), ((TextField)ReflectionTestUtils
            .getField(errorDialog, "errorUriTf")).getValue());
        Assertions.assertEquals(row.getExceptionClass(), ((TextField)ReflectionTestUtils
            .getField(errorDialog, "errorClassTf")).getValue());
    }

    @Test
    public void test_search_and_grid_rendered_with_exclusion_image()
    {
        Mockito.when(this.solrSearchService.search(Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull(),
            Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyList(),
            Mockito.anyBoolean(), Mockito.isNull(), Mockito.isNull()))
            .thenReturn(this.getSolrResults(1, "exclusion"));

        SearchResults searchResults = _get(SearchResults.class);
        Assertions.assertNotNull(searchResults);

        SolrSearchFilteringGrid solrSearchFilteringGrid = (SolrSearchFilteringGrid) ReflectionTestUtils
            .getField(searchResults, "searchResultsGrid");

        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));
        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));

        IkasanSolrDocument row = GridKt._get(solrSearchFilteringGrid, 0);
        Grid.Column<IkasanSolrDocument> column = GridKt._getColumnByKey(solrSearchFilteringGrid, "entityImage");
        String formatted = GridKt._getFormatted(column, row);

        Assert.assertEquals("HorizontalLayout[#frontend/images/hospital-service.png, width:100% justify-content:center]", formatted);
    }

    @Test
    public void test_search_and_exclusion_row_double_clicked()
    {
        Mockito.when(this.solrSearchService.search(Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull(),
            Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyList(),
            Mockito.anyBoolean(), Mockito.isNull(), Mockito.isNull()))
            .thenReturn(this.getSolrResults(1, "exclusion"));

        Mockito.when(this.solrSearchService.findByErrorUri(eq("error"), eq("id0")))
            .thenReturn(this.getSolrResults(1, "error").getResultList().get(0));

        SearchResults searchResults = _get(SearchResults.class);
        Assertions.assertNotNull(searchResults);

        SolrSearchFilteringGrid solrSearchFilteringGrid = (SolrSearchFilteringGrid) ReflectionTestUtils
            .getField(searchResults, "searchResultsGrid");

        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));
        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));

        GridKt._doubleClickItem(solrSearchFilteringGrid, 0);

        HospitalDialog hospitalDialog = _get(HospitalDialog.class);
        Assert.assertNotNull(hospitalDialog);

        IkasanSolrDocument row = GridKt._get(solrSearchFilteringGrid, 0);

        Assertions.assertEquals(row.getModuleName(), ((TextField)ReflectionTestUtils
            .getField(hospitalDialog, "moduleNameTf")).getValue());
        Assertions.assertEquals(row.getFlowName(), ((TextField)ReflectionTestUtils
            .getField(hospitalDialog, "flowNameTf")).getValue());
        Assertions.assertEquals(row.getEventId(), ((TextField)ReflectionTestUtils
            .getField(hospitalDialog, "eventIdTf")).getValue());
        Assertions.assertEquals(row.getErrorAction(), ((TextField)ReflectionTestUtils
            .getField(hospitalDialog, "errorActionTf")).getValue());
        Assertions.assertEquals("id0", ((TextField)ReflectionTestUtils
            .getField(hospitalDialog, "errorUriTf")).getValue());
    }

    @Test
    public void test_search_and_grid_rendered_with_replay_image()
    {
        Mockito.when(this.solrSearchService.search(Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull(),
            Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyList(),
            Mockito.anyBoolean(), Mockito.isNull(), Mockito.isNull()))
            .thenReturn(this.getSolrResults(1, "replay"));

        SearchResults searchResults = _get(SearchResults.class);
        Assertions.assertNotNull(searchResults);

        SolrSearchFilteringGrid solrSearchFilteringGrid = (SolrSearchFilteringGrid) ReflectionTestUtils
            .getField(searchResults, "searchResultsGrid");

        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));
        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));

        IkasanSolrDocument row = GridKt._get(solrSearchFilteringGrid, 0);
        Grid.Column<IkasanSolrDocument> column = GridKt._getColumnByKey(solrSearchFilteringGrid, "entityImage");
        String formatted = GridKt._getFormatted(column, row);

        Assert.assertEquals("HorizontalLayout[#frontend/images/replay-service.png, width:100% justify-content:center]", formatted);
    }

    @Test
    public void test_search_and_replay_row_double_clicked()
    {
        Mockito.when(this.solrSearchService.search(Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull(),
            Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyList(),
            Mockito.anyBoolean(), Mockito.isNull(), Mockito.isNull()))
            .thenReturn(this.getSolrResults(1, "replay"));

        SearchResults searchResults = _get(SearchResults.class);
        Assertions.assertNotNull(searchResults);

        SolrSearchFilteringGrid solrSearchFilteringGrid = (SolrSearchFilteringGrid) ReflectionTestUtils
            .getField(searchResults, "searchResultsGrid");

        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));
        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));

        GridKt._doubleClickItem(solrSearchFilteringGrid, 0);

        ReplayDialog replayDialog = _get(ReplayDialog.class);
        Assert.assertNotNull(replayDialog);

        IkasanSolrDocument row = GridKt._get(solrSearchFilteringGrid, 0);

        Assertions.assertEquals(row.getModuleName(), ((TextField)ReflectionTestUtils
            .getField(replayDialog, "moduleNameTf")).getValue());
        Assertions.assertEquals(row.getFlowName(), ((TextField)ReflectionTestUtils
            .getField(replayDialog, "flowNameTf")).getValue());
        Assertions.assertEquals(row.getEventId(), ((TextField)ReflectionTestUtils
            .getField(replayDialog, "eventIdTf")).getValue());
    }

    @Test
    public void test_search_replay_and_select_all()
    {
        Mockito.when(this.solrSearchService.search(Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull(),
            Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyList(),
            Mockito.anyBoolean(), Mockito.isNull(), Mockito.isNull()))
            .thenReturn(this.getSolrResults(10, "replay"));

        SearchForm searchForm = _get(SearchForm.class);
        Assertions.assertNotNull(searchForm);

        ReflectionTestUtils.setField(searchForm, "hospitalChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "replayChecked", Boolean.TRUE);
        ReflectionTestUtils.setField(searchForm, "wiretapChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "errorChecked", Boolean.FALSE);

        SearchResults searchResults = _get(SearchResults.class);
        Assertions.assertNotNull(searchResults);

        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));
        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));

        Button selectAllButton = _get(Button.class, spec -> spec.withId("selectAllButton"));

        _click(selectAllButton);

        Assertions.assertEquals(true, ReflectionTestUtils
            .getField(searchResults, "selected"));

        _click(selectAllButton);

        Assertions.assertEquals(false, ReflectionTestUtils
            .getField(searchResults, "selected"));
    }

    @Test
    public void test_search_exclusion_and_select_all()
    {
        Mockito.when(this.solrSearchService.search(Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull(),
            Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyList(),
            Mockito.anyBoolean(), Mockito.isNull(), Mockito.isNull()))
            .thenReturn(this.getSolrResults(10, "exclusion"));

        SearchForm searchForm = _get(SearchForm.class);
        Assertions.assertNotNull(searchForm);

        ReflectionTestUtils.setField(searchForm, "hospitalChecked", Boolean.TRUE);
        ReflectionTestUtils.setField(searchForm, "replayChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "wiretapChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "errorChecked", Boolean.FALSE);

        SearchResults searchResults = _get(SearchResults.class);
        Assertions.assertNotNull(searchResults);

        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));
        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));

        Button selectAllButton = _get(Button.class, spec -> spec.withId("selectAllButton"));

        _click(selectAllButton);

        Assertions.assertEquals(true, ReflectionTestUtils
            .getField(searchResults, "selected"));

        _click(selectAllButton);

        Assertions.assertEquals(false, ReflectionTestUtils
            .getField(searchResults, "selected"));
    }

    @Test
    public void test_search_wiretap_and_select_all()
    {
        Mockito.when(this.solrSearchService.search(Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull(),
            Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyList(),
            Mockito.anyBoolean(), Mockito.isNull(), Mockito.isNull()))
            .thenReturn(this.getSolrResults(10, "wiretap"));

        SearchForm searchForm = _get(SearchForm.class);
        Assertions.assertNotNull(searchForm);

        ReflectionTestUtils.setField(searchForm, "hospitalChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "replayChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "wiretapChecked", Boolean.TRUE);
        ReflectionTestUtils.setField(searchForm, "errorChecked", Boolean.FALSE);

        SearchResults searchResults = _get(SearchResults.class);
        Assertions.assertNotNull(searchResults);

        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));
        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));

        Assertions.assertEquals(0, ((HorizontalLayout)ReflectionTestUtils
            .getField(searchResults, "buttonLayout")).getComponentCount());
    }

    @Test
    public void test_search_error_and_select_all()
    {
        Mockito.when(this.solrSearchService.search(Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull(),
            Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyList(),
            Mockito.anyBoolean(), Mockito.isNull(), Mockito.isNull()))
            .thenReturn(this.getSolrResults(10, "error"));

        SearchForm searchForm = _get(SearchForm.class);
        Assertions.assertNotNull(searchForm);

        ReflectionTestUtils.setField(searchForm, "hospitalChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "replayChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "wiretapChecked", Boolean.FALSE);
        ReflectionTestUtils.setField(searchForm, "errorChecked", Boolean.TRUE);

        SearchResults searchResults = _get(SearchResults.class);
        Assertions.assertNotNull(searchResults);

        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));
        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));

        Assertions.assertEquals(0, ((HorizontalLayout)ReflectionTestUtils
            .getField(searchResults, "buttonLayout")).getComponentCount());
    }

    protected IkasanSolrDocumentSearchResults getSolrResults(int size, String type) {

        ArrayList<IkasanSolrDocument> ikasanSolrDocuments = new ArrayList<>();

        IntStream.range(0, size).forEach(i -> {
            IkasanSolrDocument document = new IkasanSolrDocument();
            document.setId("id" +i);
            document.setComponentName("component"+i);
            document.setErrorAction("exclusion"+i);
            document.setType(type);
            document.setErrorDetail("error"+i);
            document.setErrorUri("uri"+i);
            document.setErrorMessage("message"+i);
            document.setFlowName("flow"+i);
            document.setModuleName("module"+i);
            document.setPayloadRaw("payload".getBytes());
            document.setExceptionClass("exception.class");
            document.setEvent("event"+i);
            document.setEventId("eventId"+i);

            ikasanSolrDocuments.add(document);
        });

        return new IkasanSolrDocumentSearchResults(ikasanSolrDocuments
            , ikasanSolrDocuments.size(), 1);
    }
}
