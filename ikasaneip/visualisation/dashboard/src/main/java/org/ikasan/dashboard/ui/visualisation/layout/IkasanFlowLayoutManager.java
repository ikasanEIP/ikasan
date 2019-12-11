package org.ikasan.dashboard.ui.visualisation.layout;


import org.ikasan.dashboard.ui.visualisation.model.flow.*;
import org.ikasan.vaadin.visjs.network.Edge;
import org.ikasan.vaadin.visjs.network.NetworkDiagram;
import org.ikasan.vaadin.visjs.network.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by stewmi on 08/11/2018.
 */
public class IkasanFlowLayoutManager extends LayoutManagerBase implements LayoutManager
{
    Logger logger = LoggerFactory.getLogger(IkasanFlowLayoutManager.class);

    private Flow flow;

    public IkasanFlowLayoutManager(Flow flow, NetworkDiagram networkDiagram, Logo logo)
    {
        super(networkDiagram, logo);
        this.flow = flow;
    }

    public void layout()
    {
        int x = xStart;
        int y = yStart;


        flow.getConsumer().getSource().setX(x);
        flow.getConsumer().getSource().setY(y);

        logger.debug("Adding consumer [{}] for flow [{}]. ", flow.getConsumer().getLabel(), flow.getName());

        nodeList.add(flow.getConsumer().getSource());

        addEdge(flow.getConsumer().getSource().getId(), flow.getConsumer().getId(), "");

        manageTransition(flow.getConsumer(), x, y);

        this.networkDiagram.setNodes(this.nodeList);
        this.networkDiagram.setEdges(this.edgeList);

        flow.setBorder(x + 100, y - 100, xExtent - x , yExtent + 200 - y);

        this.networkDiagram.drawFlow(flow.getX(), flow.getY(), flow.getW(), flow.getH(), flow.getName());

        this.channels.forEach(messageChannel -> messageChannel.setX(xExtentFinal + 200));
    }

}
