package org.ikasan.dashboard.ui.search.view;

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
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static com.github.mvysny.kaributesting.v10.LocatorJ._get;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class SearchViewTest
{
    @Autowired
    private ApplicationContext ctx;

    @Before
    public void setup() throws Exception
    {
        Logger rootLogger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.setLevel(Level.WARN);

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
    public void testSearchView() throws IOException
    {
        UI.getCurrent().navigate("");

        try
        {
            SearchView searchView = _get(SearchView.class);
            Assertions.assertNotNull(searchView);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
