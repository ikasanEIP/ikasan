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
public class IkasanModuleLayoutManager
{
    Logger logger = LoggerFactory.getLogger(IkasanModuleLayoutManager.class);

    private Module module;
    private NetworkDiagram networkDiagram;
    private List<Edge> edgeList;
    private List<Node> nodeList;
    private List<MessageChannel> channels;
    private Logo logo;
    int xExtent = 0;
    int yExtent = 0;
    int xExtentFinal = 0;

    int xSpacing = 200;
    int ySpacing = 150;

    int flowSpacing = 300;
    int xStart = 0;
    int yStart = 0;

    public IkasanModuleLayoutManager(Module module, NetworkDiagram networkDiagram, Logo logo)
    {
        this.module = module;
        this.networkDiagram = networkDiagram;
        this.edgeList = new ArrayList<>();
        this.nodeList = new ArrayList<>();
        this.channels = new ArrayList<>();
        this.logo = logo;
    }

    public void layout()
    {
        int x = xStart;
        int y = yStart;

        for(Flow flow: module.getFlows())
        {
            flow.getConsumer().setX(x);
            flow.getConsumer().setY(y);

            logger.info("Add consumer " + flow.getConsumer());

            nodeList.add(flow.getConsumer());

            addEdge(flow.getConsumer().getId(), flow.getConsumer().getTransition().getId(), flow.getConsumer().getTransitionLabel());

            manageTransition(flow.getConsumer().getTransition(), x, y, networkDiagram);

            this.networkDiagram.drawFlow(x - 100, y - 100, xExtent + 200 - x , yExtent + 200 - y , flow.getName());

            x = xStart;
            xExtent = x;
            y = yExtent + flowSpacing;
        }

        this.networkDiagram.setNodes(this.nodeList);
        this.networkDiagram.setEdges(this.edgeList);

        this.channels.forEach(messageChannel -> messageChannel.setX(xExtentFinal + 300));

        this.networkDiagram.drawModule(xStart - 200, yStart - 200, xExtentFinal + 600, yExtent + 400, module.getName());

    }

    private void manageTransition(Node transition, int x, int y, NetworkDiagram networkDiagram)
    {
        nodeList.add(transition);
        logger.info("Add node " + transition.getId() + " x=" + x + " y=" + y + " yExtent=" + yExtent);


        if (transition instanceof SingleTransition && ((SingleTransition) transition).getTransition() != null)
        {
            transition.setX(x + xSpacing);
            transition.setY(y);

            addEdge(transition.getId(), ((SingleTransition) transition).getTransition().getId(), ((SingleTransition) transition).getTransitionLabel());

            manageTransition(((SingleTransition) transition).getTransition(), x + xSpacing, y, networkDiagram);
        }
        else if (transition instanceof MultiTransition)
        {

            transition.setX(x + xSpacing);
            transition.setY(y);

            int i=0;

            for (String key: ((MultiTransition) transition).getTransitions().keySet())
            {
                if(key.equals(((MultiTransition) transition).getTransitions().get(key)))
                {
                   key = "";
                }

                addEdge(transition.getId(), ((MultiTransition) transition).getTransitions().get(key).getId(), key);

                if(i++ > 0)
                {
                    y = y + (ySpacing);
                }

                if(yExtent >= y)
                {
                     y = yExtent + ySpacing;
                }

                manageTransition(((MultiTransition) transition).getTransitions().get(key), x + xSpacing, y, networkDiagram);
            }

            if(y > yExtent)
            {
                yExtent = y;
            }
        }
        else if(transition instanceof Node)
        {
            transition.setX(x + xSpacing);
            transition.setY(y);

            if(transition instanceof MessageChannel)
            {
                this.channels.add((MessageChannel)transition);
            }
        }

        if(x > xExtent)
        {
            xExtent = x;
        }

        if(x >  xExtentFinal)
        {
            xExtentFinal = x;
        }

        if(y > yExtent)
        {
            yExtent = y;
        }
    }

    private void addEdge(String fromId, String toId, String label)
    {
        logger.info("Add edge " + fromId + "-->" + toId);
        Edge edge = new Edge(fromId, toId);
        edge.setLabel(label);
        this.edgeList.add(edge);
    }
}
