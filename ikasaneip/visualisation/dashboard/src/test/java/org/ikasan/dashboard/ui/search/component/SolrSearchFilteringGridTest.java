package org.ikasan.dashboard.ui.search.component;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.github.mvysny.kaributesting.v10.MockVaadin;
import com.github.mvysny.kaributesting.v10.MockedUI;
import com.github.mvysny.kaributesting.v10.Routes;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.function.DeploymentConfiguration;
import com.vaadin.flow.server.*;
import com.vaadin.flow.server.startup.ApplicationRouteRegistry;
import com.vaadin.flow.spring.SpringServlet;
import com.vaadin.flow.spring.SpringVaadinServletService;
import org.ikasan.dashboard.Application;
import org.ikasan.dashboard.ui.util.SecurityConstants;
import org.ikasan.security.model.User;
import org.ikasan.security.service.UserService;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.ikasan.solr.model.IkasanSolrDocument;
import org.ikasan.solr.model.IkasanSolrDocumentSearchResults;
import org.ikasan.solr.service.SolrGeneralServiceImpl;
import org.junit.After;
import org.junit.Assert;
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static com.github.mvysny.kaributesting.v10.ButtonKt._click;
import static com.github.mvysny.kaributesting.v10.LocatorJ._get;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class SolrSearchFilteringGridTest
{
    @Autowired
    private ApplicationContext ctx;

    @MockBean
    private IkasanAuthentication ikasanAuthentication;

    @MockBean
    private SolrGeneralServiceImpl solrSearchService;

    @MockBean
    private UserService userService;

    @MockBean
    private User user;

    @Before
    public void setup() throws Exception
    {
        Logger rootLogger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.setLevel(Level.WARN);

        Mockito.when(this.solrSearchService.search(Mockito.anySet(), Mockito.anySet(), Mockito.anySet(), Mockito.anyString(),
            Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyList(),
            Mockito.anyBoolean(), Mockito.anyString(), Mockito.anyString()))
            .thenReturn(this.getSolrResults());

        Mockito.when(this.solrSearchService.search(Mockito.isNull(), Mockito.isNull(), Mockito.isNull(), Mockito.isNull(),
            Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyList(),
            Mockito.anyBoolean(), Mockito.isNull(), Mockito.isNull()))
            .thenReturn(this.getSolrResults());

        // Setup the mock authentication.
        SecurityContextHolder.getContext().setAuthentication(this.ikasanAuthentication);

        // Mock some of the requisite behaviour.
        Mockito.when(this.ikasanAuthentication.getName())
            .thenReturn("username");
        Mockito.when(this.ikasanAuthentication.hasGrantedAuthority(SecurityConstants.ALL_AUTHORITY))
            .thenReturn(true);
        Mockito.when(this.userService.loadUserByUsername("username"))
            .thenReturn(user);
        Mockito.when(user.isRequiresPasswordChange())
            .thenReturn(false);

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

    @After
    public void tearDown() {
        MockVaadin.tearDown();
    }

    @Test
    public void test_filtered_data_provider_not_null_after_search() throws IOException
    {
        try
        {
            SolrSearchFilteringGrid solrSearchFilteringGrid = _get(SolrSearchFilteringGrid.class);
            Assertions.assertNotNull(solrSearchFilteringGrid);

            _click(_get(Button.class, spec -> spec.withId("searchFormSearchButton")));

            Assert.assertNotNull(ReflectionTestUtils.getField(solrSearchFilteringGrid, "filteredDataProvider"));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Test
    public void test_filtered_data_provider_null_before_search() throws IOException
    {
        UI.getCurrent().navigate("");

        try
        {
            SolrSearchFilteringGrid solrSearchFilteringGrid = _get(SolrSearchFilteringGrid.class);
            Assertions.assertNotNull(solrSearchFilteringGrid);

            Assert.assertNull(ReflectionTestUtils.getField(solrSearchFilteringGrid, "filteredDataProvider"));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Test
    public void test_filtered_data_provider_null_before_search_add_filter() throws IOException
    {
        UI.getCurrent().navigate("");

        try
        {
            SolrSearchFilteringGrid solrSearchFilteringGrid = _get(SolrSearchFilteringGrid.class);
            Assertions.assertNotNull(solrSearchFilteringGrid);

            TextField eventFilter = _get(TextField.class, spec -> spec.withId("event"));
            eventFilter.setValue("event");

            Assert.assertNull(ReflectionTestUtils.getField(solrSearchFilteringGrid, "filteredDataProvider"));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Test
    public void test_search_and_filter() throws IOException
    {
        try
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
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private IkasanSolrDocumentSearchResults getSolrResults() {

        ArrayList<IkasanSolrDocument> ikasanSolrDocuments = new ArrayList<>();

        IntStream.range(0, 50).forEach(i -> {
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
