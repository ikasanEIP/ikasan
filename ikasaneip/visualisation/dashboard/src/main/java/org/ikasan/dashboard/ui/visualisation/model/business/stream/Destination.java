package org.ikasan.dashboard.ui.visualisation.model.business.stream;

import org.ikasan.vaadin.visjs.network.Node;
import org.ikasan.vaadin.visjs.network.options.nodes.Nodes;
import org.ikasan.vaadin.visjs.network.util.Shape;

public class Destination extends Node
{
    public static final String IMAGE = "frontend/images/channel.png";

    public Destination(String id, String name, int x, int y)
    {
        super(id, name, Nodes.builder().withShape(Shape.image).withx(x).withy(y).withImage(IMAGE));
        super.setEdgeColour("rgba(255, 255, 255, 1)");
        super.setFillColour("rgba(255, 255, 255, 1)");
    }
}
