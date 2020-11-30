package org.ikasan.dashboard.ui.administration.view;

import com.vaadin.flow.component.UI;
import org.ikasan.dashboard.ui.UITest;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;

import static com.github.mvysny.kaributesting.v10.LocatorJ._get;


public class UserManagementViewTest extends UITest
{
    @Override
    public void setup_expectations() {

    }

    @Test
    public void testUserManagementView() throws IOException
    {
        UI.getCurrent().navigate("userManagement");

        UserManagementView userManagementView = _get(UserManagementView.class);
        Assertions.assertNotNull(userManagementView);
    }
}
