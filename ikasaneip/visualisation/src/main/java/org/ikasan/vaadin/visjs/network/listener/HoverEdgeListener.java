package org.ikasan.vaadin.visjs.network.listener;

import com.vaadin.flow.component.ComponentEventListener;

import org.ikasan.vaadin.visjs.network.event.HoverEdgeEvent;

/**
 * Fired if the option interaction:{hover:true} is enabled and the mouse hovers over an edge.
 *
 * @see <a href="http://visjs.org/docs/network/#Events">http://visjs.org/docs/network/#Events</a>
 *
 * @author watho
 *
 */
public interface HoverEdgeListener extends ComponentEventListener<HoverEdgeEvent> {
}
