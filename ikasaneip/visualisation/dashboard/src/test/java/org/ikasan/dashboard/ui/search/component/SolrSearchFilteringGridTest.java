package org.ikasan.dashboard.ui.search.component;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.textfield.TextField;
import org.ikasan.dashboard.ui.UITest;
import org.ikasan.solr.model.IkasanSolrDocument;
import org.ikasan.solr.model.IkasanSolrDocumentSearchResults;
import org.ikasan.solr.service.SolrGeneralServiceImpl;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.IntStream;

import static com.github.mvysny.kaributesting.v10.ButtonKt._click;
import static com.github.mvysny.kaributesting.v10.LocatorJ._get;

public class SolrSearchFilteringGridTest extends UITest {
    @MockBean
    private SolrGeneralServiceImpl solrSearchService;


    public void setup_expectations() {
        Mockito.when(this.solrSearchService.search(Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull(),
            Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyList(),
            Mockito.anyBoolean(), Mockito.isNull(), Mockito.isNull()))
            .thenReturn(this.getSolrResults(50));

        Mockito.when(this.solrSearchService.search(Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.anyString(),
            Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyList(),
            Mockito.anyBoolean(), Mockito.isNull(), Mockito.isNull()))
            .thenReturn(this.getSolrResults(1));
    }

    @Test
    public void test_filtered_data_provider_not_null_after_search() throws IOException
    {
        SolrSearchFilteringGrid solrSearchFilteringGrid = _get(SolrSearchFilteringGrid.class);
        Assertions.assertNotNull(solrSearchFilteringGrid);

        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));

        Assert.assertNotNull(ReflectionTestUtils.getField(solrSearchFilteringGrid, "filteredDataProvider"));
    }

    @Test
    public void test_filtered_data_provider_null_before_search() throws IOException
    {
        UI.getCurrent().navigate("");

        SolrSearchFilteringGrid solrSearchFilteringGrid = _get(SolrSearchFilteringGrid.class);
        Assertions.assertNotNull(solrSearchFilteringGrid);

        Assert.assertNull(ReflectionTestUtils.getField(solrSearchFilteringGrid, "filteredDataProvider"));

    }

    @Test
    public void test_filtered_data_provider_null_before_search_add_filter() throws IOException
    {
        UI.getCurrent().navigate("");

        SolrSearchFilteringGrid solrSearchFilteringGrid = _get(SolrSearchFilteringGrid.class);
        Assertions.assertNotNull(solrSearchFilteringGrid);

        TextField eventFilter = _get(TextField.class, spec -> spec.withId("event"));
        eventFilter.setValue("event");

        Assert.assertNull(ReflectionTestUtils.getField(solrSearchFilteringGrid, "filteredDataProvider"));
    }

    @Test
    public void test_search_and_filter() throws IOException
    {
        SolrSearchFilteringGrid solrSearchFilteringGrid = _get(SolrSearchFilteringGrid.class);
        Assertions.assertNotNull(solrSearchFilteringGrid);

        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));
        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));

        Assert.assertNotNull(ReflectionTestUtils.getField(solrSearchFilteringGrid, "filteredDataProvider"));

        Assert.assertEquals(50, solrSearchFilteringGrid.getResultSize());

        TextField eventFilter = _get(TextField.class, spec -> spec.withId("event"));
        eventFilter.setValue("event1");

        _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));

        Assert.assertEquals(1, solrSearchFilteringGrid.getResultSize());
    }

    private IkasanSolrDocumentSearchResults getSolrResults(int size) {

        ArrayList<IkasanSolrDocument> ikasanSolrDocuments = new ArrayList<>();

        IntStream.range(0, size).forEach(i -> {
            IkasanSolrDocument document = new IkasanSolrDocument();
            document.setId("id" +i);
            document.setComponentName("component"+i);
            document.setErrorAction("exclusion"+i);
            document.setType("exclusion"+i);
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
