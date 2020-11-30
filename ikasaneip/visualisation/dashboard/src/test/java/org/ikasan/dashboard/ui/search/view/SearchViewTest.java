package org.ikasan.dashboard.ui.search.view;

import com.vaadin.flow.component.UI;
import org.ikasan.dashboard.ui.UITest;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;

import static com.github.mvysny.kaributesting.v10.LocatorJ._get;


public class SearchViewTest extends UITest
{
    public void setup_expectations() {
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
