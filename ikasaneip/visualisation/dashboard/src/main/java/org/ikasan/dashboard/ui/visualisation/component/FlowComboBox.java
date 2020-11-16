package org.ikasan.dashboard.ui.visualisation.component;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.shared.Registration;
import org.ikasan.dashboard.broadcast.FlowStateBroadcaster;
import org.ikasan.dashboard.cache.CacheStateBroadcaster;
import org.ikasan.dashboard.ui.visualisation.model.flow.Flow;
import org.ikasan.dashboard.ui.visualisation.model.flow.Module;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlowComboBox extends ComboBox<Flow>
{
    Logger logger = LoggerFactory.getLogger(FlowComboBox.class);

    private Registration flowStateBroadcasterRegistration;
    private Registration cacheStateBroadcasterRegistration;

    private Module currentModule;

    public FlowComboBox()
    {
    }

    @Override
    protected void onAttach(AttachEvent attachEvent)
    {
        UI ui = attachEvent.getUI();
        this.flowStateBroadcasterRegistration = FlowStateBroadcaster.register(flowState ->
        {
            ui.access(() ->
            {
                // do something interesting here.
                logger.debug("Received flow state: " + flowState);

                if(this.currentModule != null)
                {
                    Flow flow = this.getValue();
                    removeAll();
                    setItems(currentModule.getFlows());
                    this.setValue(flow);
                }
            });
        });

        this.cacheStateBroadcasterRegistration = CacheStateBroadcaster.register(flowState ->
        {
            ui.access(() ->
            {
                // do something interesting here.
                logger.debug("Received flow state: " + flowState);

                if(this.currentModule != null)
                {
                    Flow flow = this.getValue();
                    removeAll();
                    setItems(currentModule.getFlows());
                    this.setValue(flow);
                }
            });
        });
    }

    @Override
    protected void onDetach(DetachEvent detachEvent)
    {
        this.flowStateBroadcasterRegistration.remove();
        this.flowStateBroadcasterRegistration = null;
        this.cacheStateBroadcasterRegistration.remove();
        this.cacheStateBroadcasterRegistration = null;
    }

    public void setCurrentModule(Module currentModule)
    {
        this.currentModule = currentModule;
        this.setItems(this.currentModule.getFlows());
        this.setValue(this.currentModule.getFlows().get(0));
    }
}
