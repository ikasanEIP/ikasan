package org.ikasan.dashboard.ui.visualisation.model.business.stream;

import org.ikasan.vaadin.visjs.network.Node;
import org.ikasan.vaadin.visjs.network.options.nodes.Nodes;
import org.ikasan.vaadin.visjs.network.util.Shape;

public class IntegratedSystem extends Node
{
    public static final String IMAGE = "frontend/images/computer.png";


    public IntegratedSystem(String id, String name, String image, Integer size, int x, int y)
    {
        super(id, name, Nodes.builder().withShape(Shape.image).withx(x).withy(y).withSize(size).withImage(image));
        super.setEdgeColour("rgba(255, 255, 255, 1)");
        super.setFillColour("rgba(255, 255, 255, 1)");
    }
}
