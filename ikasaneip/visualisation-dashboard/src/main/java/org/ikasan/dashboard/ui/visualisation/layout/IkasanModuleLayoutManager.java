package org.ikasan.dashboard.ui.visualisation.layout;


import org.ikasan.dashboard.ui.visualisation.model.flow.*;
import org.ikasan.vaadin.visjs.network.Edge;
import org.ikasan.vaadin.visjs.network.NetworkDiagram;
import org.ikasan.vaadin.visjs.network.Node;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by stewmi on 08/11/2018.
 */
public class IkasanModuleLayoutManager
{
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
        int x = 100;
        int y = 100;

        for(Flow flow: module.getFlows())
        {
            flow.getConsumer().setX(x);
            flow.getConsumer().setY(y);

            System.out.println("Add consumer " + flow.getConsumer());

            nodeList.add(flow.getConsumer());

            addEdge(flow.getConsumer().getId(), flow.getConsumer().getTransition().getId());

            manageTransition(flow.getConsumer().getTransition(), x, y, networkDiagram);

            this.networkDiagram.drawFlow(x - 100, y - 100, xExtent + 200 - x , yExtent + 200 - y , flow.getName());

//            System.out.println("Add flow " + xExtent + "-->" + yExtent);

//            x = xExtent + flowSpacing;
            x = 100;
            xExtent = x;
            y = yExtent + flowSpacing;
        }

//        System.out.println("Add module " + xExtent + 400 + "-->" + yExtent + 300);

        this.networkDiagram.setNodes(this.nodeList);
        this.networkDiagram.setEdges(this.edgeList);

        this.channels.forEach(messageChannel -> messageChannel.setX(xExtentFinal + 300));

        this.networkDiagram.drawModule(-100, -100, xExtentFinal + 500, yExtent + 300, module.getName());

//        logo.setX(30);
//        logo.setY(yExtent + 150);
    }

    private void manageTransition(Node transition, int x, int y, NetworkDiagram networkDiagram)
    {
        nodeList.add(transition);
        System.out.println("Add node " + transition.getId() + " x=" + x + " y=" + y + " yExtent=" + yExtent);

//        if(yExtent >= y)
//        {
//            y = yExtent;
//        }

        if (transition instanceof SingleTransition && ((SingleTransition) transition).getTransition() != null)
        {
            transition.setX(x + xSpacing);
            transition.setY(y);

            addEdge(transition.getId(), ((SingleTransition) transition).getTransition().getId());

            manageTransition(((SingleTransition) transition).getTransition(), x + xSpacing, y, networkDiagram);
        }
        else if (transition instanceof MultiTransition)
        {

            transition.setX(x + xSpacing);
            transition.setY(y);

            int i=0;

            for (Node next : ((MultiTransition) transition).getTransitions())
            {
                addEdge(transition.getId(), next.getId());

                if(i++ > 0)
                {
                    y = y + (ySpacing);
                }

                if(yExtent >= y)
                {
                     y = yExtent + ySpacing;
                }

                manageTransition(next, x + xSpacing, y, networkDiagram);
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

    private void addEdge(String fromId, String toId)
    {
        System.out.println("Add edge " + fromId + "-->" + toId);
//		Color black = new Color("#000000");
//		black.setColor("#000000");
//		black.setBorder("#000000");
        Edge edge = new Edge(fromId, toId);
//        edge.setId(fromId + "-->" + toId);
//        Smooth smooth = new Smooth();
//        smooth.setType(Smooth.Type.horizontal);
//        smooth.setRoundness(0.0);
//        edge.setSmooth(smooth);
//        edge.setHidden(false);

        this.edgeList.add(edge);

//        ContextMenu contextMenu = new ContextMenu(this, false);
//
//        contextMenu.addItem("Delete", e -> {
//
//        });
//
//        networkDiagram.addEdgeSelectListener(new Edge.EdgeSelectListener(edge)
//        {
//            @Override
//            public void onFired(SelectEdgeEvent event)
//            {
//                System.out.println(event);
//            }
//        });
//
//        networkDiagram.addEdgeClickListener(new Edge.EdgeClickListener(edge)
//        {
//            @Override
//            public void onFired(ClickEvent event)
//            {
//                System.out.println(event);
//            }
//        });
//
//        networkDiagram.addEdgeConextListener(new Edge.EdgeContextListener(edge)
//        {
//            @Override
//            public void onFired(ContextEvent event)
//            {
//                System.out.println(event);
//                contextMenu.open((int) event.getDOMx(), (int) event.getDOMy());
//            }
//        });
    }

//    private void updateDiagram()
//    {
//        networkDiagram.getUI().getSession().getLockInstance().lock();
//        try
//        {
//            networkDiagram.clear();
//            networkDiagram.updateEdges(edgeList);
//        }
//        finally
//        {
//            networkDiagram.getUI().getSession().getLockInstance().unlock();
//            UI.getCurrent().push();
//        }
//    }

    public List<Edge> getEdgeList()
    {
        return edgeList;
    }
}
