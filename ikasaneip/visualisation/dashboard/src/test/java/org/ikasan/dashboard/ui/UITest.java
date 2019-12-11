package org.ikasan.dashboard.ui;

import com.github.mvysny.kaributesting.v10.MockVaadin;
import com.github.mvysny.kaributesting.v10.Routes;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.RouteData;
import com.vaadin.flow.router.Router;
import com.vaadin.flow.router.RouterLayout;
import org.ikasan.dashboard.ui.home.view.HomeView;
import org.ikasan.dashboard.ui.layout.IkasanAppLayout;
import org.ikasan.dashboard.ui.visualisation.view.GraphView;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

public class UITest
{
    private static Routes routes;

    @BeforeAll
    public static void createRoutes()
    {
        // initialize routes only once, to avoid view auto-detection before every test and to speed up the tests
        routes = new Routes().autoDiscoverViews("org.ikasan.dashboard.ui");
    }

    @BeforeEach
    public void setupVaadin()
    {
        MockVaadin.setup(routes);
    }

    @Test
    public void testAvailableRoutes()
    {

        Router router = UI.getCurrent().getRouter();
        Map<Class<? extends RouterLayout>, List<RouteData>> routesByParent = router.getRoutesByParent();
        List<RouteData> myRoutes = routesByParent.get(IkasanAppLayout.class);

        Assertions.assertEquals(8, myRoutes.size(), "Number of Routes is 1!");
        Assertions.assertEquals("", myRoutes.get(0).getUrl(), "Default route is an empty string!");
        Assertions.assertEquals(HomeView.class, myRoutes.get(0).getNavigationTarget(), "HomeView is the default view!");
    }
}
