package org.ikasan.vaadin.visjs.network.listener;

import com.vaadin.flow.component.ComponentEventListener;

import org.ikasan.vaadin.visjs.network.event.BeforeDrawingEvent;

/**
 * Fired after the canvas has been cleared, scaled and translated to the viewing position but before
 * all edges and nodes are drawn. Can be used to draw behind the network.
 *
 * @see <a href="http://visjs.org/docs/network/#Events">http://visjs.org/docs/network/#Events</a>
 *
 * @author watho
 *
 */
public interface BeforeDrawingListener extends ComponentEventListener<BeforeDrawingEvent> {
}
