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
 * Base abstract class used to assist in the layout of ikasan flow and module diagrams.
 */
public abstract class LayoutManagerBase
{
    private Logger logger = LoggerFactory.getLogger(LayoutManagerBase.class);

    protected NetworkDiagram networkDiagram;
    protected List<Edge> edgeList;
    protected List<Node> nodeList;
    protected List<Destination> destinations;
    protected Logo logo;

    // The below set of values are used internally
    // in order to calculate the placement of the
    // various components.
    protected int xExtent = 0;
    protected int yExtent = 0;
    protected int xExtentFinal = 0;
    protected int xStart = 0;
    protected int yStart = 0;

    // These values are constant and used to determine the relative location
    // of the various components. Defaults are provided however these can
    // overwritten by setter methods.
    protected int xSpacing = 200;
    protected int ySpacing = 150;


    public LayoutManagerBase(NetworkDiagram networkDiagram, Logo logo)
    {
        this.networkDiagram = networkDiagram;
        this.edgeList = new ArrayList<>();
        this.nodeList = new ArrayList<>();
        this.destinations = new ArrayList<>();
        this.logo = logo;
    }


    /**
     * This method recursively works its way through a flow with all of its
     * various transitions and lays out the flow in a left to right, top
     * to bottom fashion.
     *
     * @param transition
     * @param x
     * @param y
     */
    protected void manageTransition(Node transition, int x, int y)
    {
        nodeList.add(transition);
        logger.debug("Adding component [{}] at [x={}] and [y={}]. ", transition.getLabel(), x, y);


        if (transition instanceof SingleTransition && ((SingleTransition) transition).getTransition() != null)
        {
            transition.setX(x + xSpacing);
            transition.setY(y);

            addEdge(transition.getId(), ((SingleTransition) transition).getTransition().getId(), ((SingleTransition) transition).getTransitionLabel());

            manageTransition(((SingleTransition) transition).getTransition(), x + xSpacing, y);
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

                if(i > 0 && yExtent >= y)
                {
                    y = yExtent + ySpacing;
                }

                manageTransition(((MultiTransition) transition).getTransitions().get(key), x + xSpacing, y);

                i++;
            }
        }
        else if(transition instanceof Node)
        {
            transition.setX(x + xSpacing);
            transition.setY(y);

            if(transition instanceof MessageChannel)
            {
                this.destinations.add((Destination)transition);
            }
        }

        if(x > xExtent)
        {
            xExtent = x;
        }

        if(x > xExtentFinal)
        {
            xExtentFinal = x;
        }

        if(y > yExtent)
        {
            yExtent = y;
        }
    }

    protected void addEdge(String fromId, String toId, String label)
    {
        logger.debug("Adding edge [{}] --> [{}] with label [{}]", fromId, toId, label);
        Edge edge = new Edge(fromId, toId);
        edge.setLabel(label);
        this.edgeList.add(edge);
    }

    public int getxSpacing()
    {
        return xSpacing;
    }

    public void setxSpacing(int xSpacing)
    {
        this.xSpacing = xSpacing;
    }

    public int getySpacing()
    {
        return ySpacing;
    }

    public void setySpacing(int ySpacing)
    {
        this.ySpacing = ySpacing;
    }
}
