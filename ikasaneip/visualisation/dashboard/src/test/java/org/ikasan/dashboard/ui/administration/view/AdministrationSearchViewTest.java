package org.ikasan.dashboard.ui.administration.view;

import com.vaadin.flow.component.UI;
import org.ikasan.dashboard.ui.UITest;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import static com.github.mvysny.kaributesting.v10.LocatorJ._get;

public class AdministrationSearchViewTest extends UITest {

    public void setup_expectations() {
    }



    @Test
    public void test_search_reesolve_view()
    {
        UI.getCurrent().navigate("adminSearchView");

        AdministrationSearchView administrationSearchView = _get(AdministrationSearchView.class);
        Assertions.assertNotNull(administrationSearchView);
    }
}
