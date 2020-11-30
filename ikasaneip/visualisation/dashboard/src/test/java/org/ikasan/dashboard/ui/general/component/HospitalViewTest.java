package org.ikasan.dashboard.ui.general.component;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.textfield.TextField;
import org.ikasan.dashboard.ui.UITest;
import org.ikasan.solr.model.IkasanSolrDocument;
import org.ikasan.solr.service.SolrGeneralServiceImpl;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;

import static com.github.mvysny.kaributesting.v10.LocatorJ._get;

public class HospitalViewTest extends UITest {

    @MockBean
    private SolrGeneralServiceImpl solrSearchService;


    @Override
    public void setup_expectations() {

    }

    @Test
    public void test_hospital_view_success() throws IOException
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
        document.setEvent("event payload");
        document.setEventId("eventId");

        Mockito.when(this.solrSearchService.findById("exclusion", "12345"))
            .thenReturn(document);

         Mockito.when(this.solrSearchService.findByErrorUri("error", "id"))
            .thenReturn(document);

        UI.getCurrent().navigate("exclusion/12345");

        try
        {
            HospitalView hospitalView = _get(HospitalView.class);
            Assertions.assertNotNull(hospitalView);

            TextField errorUriTf = (TextField)ReflectionTestUtils.getField(hospitalView, "errorUriTf");
            Assertions.assertEquals("id", errorUriTf.getValue(), "Error URI text field equals");

            TextField errorActionTf = (TextField)ReflectionTestUtils.getField(hospitalView, "errorActionTf");
            Assertions.assertEquals("exclusion", errorActionTf.getValue(), "Error Action text field equals");

            TextField eventIdTf = (TextField)ReflectionTestUtils.getField(hospitalView, "eventIdTf");
            Assertions.assertEquals("eventId", eventIdTf.getValue(), "Event Id text field equals");

            TextField flowNameTf = (TextField)ReflectionTestUtils.getField(hospitalView, "flowNameTf");
            Assertions.assertEquals("flow", flowNameTf.getValue(), "Flow name text field equals");

            TextField moduleNameTf = (TextField)ReflectionTestUtils.getField(hospitalView, "moduleNameTf");
            Assertions.assertEquals("module", moduleNameTf.getValue(), "Module name text field equals");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Test
    public void test_hospital_view_redirect_not_found() throws IOException
    {
        UI.getCurrent().navigate("exclusion/12345");

        try
        {
            PageNotFoundView pageNotFoundView = _get(PageNotFoundView.class);
            Assertions.assertNotNull(pageNotFoundView);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
