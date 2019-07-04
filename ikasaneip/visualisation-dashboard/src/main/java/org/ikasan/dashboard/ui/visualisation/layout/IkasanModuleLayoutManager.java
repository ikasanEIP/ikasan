package org.ikasan.dashboard.ui.visualisation.layout;


import org.ikasan.dashboard.ui.visualisation.model.flow.*;
import org.ikasan.vaadin.visjs.network.Edge;
import org.ikasan.vaadin.visjs.network.NetworkDiagram;
import org.ikasan.vaadin.visjs.network.Node;
import org.ikasan.vaadin.visjs.network.options.edges.Smooth;
import org.ikasan.vaadin.visjs.network.util.Color;

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
    private Logo logo;
    int xExtent = 0;
    int yExtent = 0;

    public IkasanModuleLayoutManager(Module module, NetworkDiagram networkDiagram, Logo logo)
    {
        this.module = module;
        this.networkDiagram = networkDiagram;
        this.edgeList = new ArrayList<>();
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

            addEdge(flow.getConsumer().getId(), flow.getConsumer().getTransition().getId());

            manageTransition(flow.getConsumer().getTransition(), x, y, networkDiagram);

//            this.networkDiagram.drawFlow(x - 100, y - 100, xExtent + 200 - x , yExtent + 100, flow.getName());

            System.out.println("Add flow " + xExtent + "-->" + yExtent);

            x = xExtent + 100 + 300;
        }

        System.out.println("Add module " + xExtent + 200 + "-->" + yExtent + 300);

//        this.networkDiagram.drawModule(-100, -100, xExtent + 400, yExtent + 300, module.getName());

        logo.setX(30);
        logo.setY(yExtent + 150);
    }

    private void manageTransition(Node transition, int x, int y, NetworkDiagram networkDiagram)
    {
        if (transition instanceof SingleTransition)
        {
            transition.setX(x + 200);
            transition.setY(y);

            addEdge(transition.getId(), ((SingleTransition) transition).getTransition().getId());

            manageTransition(((SingleTransition) transition).getTransition(), x + 200, y, networkDiagram);
        }
        else if (transition instanceof MultiTransition)
        {
            transition.setX(x + 200);
            transition.setY(y);

            int i=0;

            for (Node next : ((MultiTransition) transition).getTransitions())
            {
                addEdge(transition.getId(), next.getId());

                manageTransition(next, x + 200, y + (100 * i++), networkDiagram);
            }
        }
        else if(transition instanceof Node)
        {
            transition.setX(x + 200);
            transition.setY(y);

        }

        if(x > xExtent)
        {
            xExtent = x;
        }

        if(y > yExtent)
        {
            yExtent = y;
        }
    }

    private void addEdge(String fromId, String toId)
    {
        System.out.println("Add edge " + fromId + "-->" + toId);
		Color black = new Color("#000000");
		black.setColor("#000000");
		black.setBorder("#000000");
        Edge edge = new Edge(fromId, toId);
        edge.setId(fromId + "-->" + toId);
        Smooth smooth = new Smooth();
        smooth.setType(Smooth.Type.horizontal);
        smooth.setRoundness(0.0);
        edge.setSmooth(smooth);
        edge.setHidden(false);

        System.out.println("Add edge " + edge);
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
