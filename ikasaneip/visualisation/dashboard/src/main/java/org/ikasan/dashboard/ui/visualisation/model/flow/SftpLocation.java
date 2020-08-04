package org.ikasan.dashboard.ui.visualisation.model.flow;

import org.ikasan.vaadin.visjs.network.Node;
import org.ikasan.vaadin.visjs.network.options.nodes.Nodes;
import org.ikasan.vaadin.visjs.network.util.Shape;

/**
 * Created by stewmi on 07/11/2018.
 */
public class SftpLocation extends Node implements Destination
{
	public static final String IMAGE = "frontend/images/sftp-location.png";


	public SftpLocation(String id, String name)
	{
        super(id, name, Nodes.builder().withShape(Shape.image).withImage(IMAGE));
	}

}
