package org.ikasan.dashboard.ui.administration.view;

import com.vaadin.flow.component.UI;
import org.ikasan.dashboard.ui.UITest;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;

import static com.github.mvysny.kaributesting.v10.LocatorJ._get;

public class GroupManagementViewTest extends UITest
{
    @Override
    public void setup_expectations() {

    }

    @Test
    public void testGroupManagementView() throws IOException
    {
        UI.getCurrent().navigate("groupManagement");

        GroupManagementView groupManagementView = _get(GroupManagementView.class);
        Assertions.assertNotNull(groupManagementView);
    }
}
