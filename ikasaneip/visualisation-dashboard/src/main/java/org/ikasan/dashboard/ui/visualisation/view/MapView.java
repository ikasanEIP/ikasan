package org.ikasan.dashboard.ui.visualisation.view;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.polymertemplate.Id;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import org.ikasan.dashboard.ui.layout.IkasanAppLayout;
import org.ikasan.dashboard.ui.visualisation.component.D3MapView;

@Route(value = "map", layout = IkasanAppLayout.class)
@UIScope
public class MapView extends HorizontalLayout
{
    D3MapView mapView;

    public MapView()
    {
        this.setSizeFull();

        mapView = new D3MapView();
        mapView.setHeightFull();

        this.add(mapView);
    }
}
