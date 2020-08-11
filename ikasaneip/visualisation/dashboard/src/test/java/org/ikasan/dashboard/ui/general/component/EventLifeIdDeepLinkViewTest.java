package org.ikasan.dashboard.ui.general.component;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.github.mvysny.kaributesting.v10.MockVaadin;
import com.github.mvysny.kaributesting.v10.MockedUI;
import com.github.mvysny.kaributesting.v10.Routes;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.function.DeploymentConfiguration;
import com.vaadin.flow.server.*;
import com.vaadin.flow.server.startup.ApplicationRouteRegistry;
import com.vaadin.flow.spring.SpringServlet;
import com.vaadin.flow.spring.SpringVaadinServletService;
import org.ikasan.dashboard.Application;
import org.ikasan.dashboard.ui.search.component.SolrSearchFilteringGrid;
import org.ikasan.dashboard.ui.util.SecurityConstants;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.solr.model.IkasanSolrDocument;
import org.ikasan.solr.model.IkasanSolrDocumentSearchResults;
import org.ikasan.solr.service.SolrGeneralServiceImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.List;

import static com.github.mvysny.kaributesting.v10.LocatorJ._get;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class EventLifeIdDeepLinkViewTest {

    @MockBean
    private SolrGeneralServiceImpl solrSearchService;

    @MockBean
    private IkasanAuthentication ikasanAuthentication;

    @Autowired
    private ApplicationContext ctx;

    @Before
    public void setup()
    {
        Logger rootLogger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.setLevel(Level.INFO);

        final SpringServlet servlet = new SpringServlet(ctx, true) {
            @Override
            protected VaadinServletService createServletService(DeploymentConfiguration deploymentConfiguration) throws ServiceException
            {
                final VaadinServletService service = new SpringVaadinServletService(this, deploymentConfiguration, ctx) {
                    @Override
                    protected boolean isAtmosphereAvailable() {
                        return false;
                    }

                    @Override
                    protected RouteRegistry getRouteRegistry() {
                        new Routes().autoDiscoverViews("org.ikasan.dashboard.ui").register(this.getServlet().getServletContext());
                        RouteRegistry registry =  ApplicationRouteRegistry.getInstance(this.getServlet().getServletContext());
                        return registry;
                    }

                    @Override
                    public String getMainDivId(VaadinSession session, VaadinRequest request) {
                        return "ROOT-1";
                    }
                };
                service.init();
                return service;
            }
        };
        MockVaadin.setup(MockedUI::new, servlet);
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

        Mockito.when(this.solrSearchService.search(Mockito.anySet(), Mockito.anySet(), Mockito.anySet(), Mockito.anyString(),
            Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyList(),
            Mockito.anyBoolean(), Mockito.anyString(), Mockito.anyString()))
            .thenReturn(solrDocumentSearchResults);

        SecurityContextHolder.getContext().setAuthentication(this.ikasanAuthentication);

        UI.getCurrent().navigate("eventLifeId/eventId");

        try
        {
            EventLifeIdDeepLinkView eventLifeIdDeepLinkView = _get(EventLifeIdDeepLinkView.class);
            Assertions.assertNotNull(eventLifeIdDeepLinkView);

            SearchResults searchResults = (SearchResults) ReflectionTestUtils
                .getField(eventLifeIdDeepLinkView, "searchResults");

            SolrSearchFilteringGrid solrSearchFilteringGrid = (SolrSearchFilteringGrid) ReflectionTestUtils
                .getField(searchResults, "searchResultsGrid");

            Assertions.assertEquals(3, solrSearchFilteringGrid.getResultSize(), "Search results size equals 3!");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Test
    public void test_event_life_id_view_success_no_results() throws IOException
    {
        IkasanSolrDocumentSearchResults solrDocumentSearchResults = new IkasanSolrDocumentSearchResults(List.of(), 0, 0);

        Mockito.when(this.ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
            .thenReturn(true);

        Mockito.when(this.solrSearchService.search(Mockito.anySet(), Mockito.anySet(), Mockito.anySet(), Mockito.anyString(),
            Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyList(),
            Mockito.anyBoolean(), Mockito.anyString(), Mockito.anyString()))
            .thenReturn(solrDocumentSearchResults);

        SecurityContextHolder.getContext().setAuthentication(this.ikasanAuthentication);

        UI.getCurrent().navigate("eventLifeId/eventId");

        try
        {
            EventLifeIdDeepLinkView eventLifeIdDeepLinkView = _get(EventLifeIdDeepLinkView.class);
            Assertions.assertNotNull(eventLifeIdDeepLinkView);

            SearchResults searchResults = (SearchResults) ReflectionTestUtils
                .getField(eventLifeIdDeepLinkView, "searchResults");

            SolrSearchFilteringGrid solrSearchFilteringGrid = (SolrSearchFilteringGrid) ReflectionTestUtils
                .getField(searchResults, "searchResultsGrid");

            Assertions.assertEquals(0, solrSearchFilteringGrid.getResultSize(), "Search results size equals 0!");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    @After
    public void tearDown() {
        MockVaadin.tearDown();
    }
}
