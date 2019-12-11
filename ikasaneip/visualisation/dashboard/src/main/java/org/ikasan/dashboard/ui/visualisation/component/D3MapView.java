package org.ikasan.dashboard.ui.visualisation.component;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JavaScript;

@JavaScript("https://cdnjs.cloudflare.com/ajax/libs/d3/4.2.2/d3.min.js")
@JavaScript("frontend://html/map.js")
@Tag("map")
public class D3MapView extends Component implements HasSize
{

    public D3MapView()
    {
        this.setId("map-canvas");
    }

    private void initConnector()
    {
        this.setSizeFull();
        getUI()
            .orElseThrow(() -> new IllegalStateException(
                "Connector can only be initialized for an attached NetworkDiagram"))
            .getPage()
            .executeJavaScript("window.Vaadin.Flow.mapConnector.initLazy($0)",
                getElement());
    }

    @Override
    protected void onAttach(AttachEvent attachEvent)
    {
        super.onAttach(attachEvent);
        initConnector();
    }
}
