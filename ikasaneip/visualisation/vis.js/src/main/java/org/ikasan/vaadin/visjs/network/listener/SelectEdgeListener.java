package org.ikasan.vaadin.visjs.network.listener;

import com.vaadin.flow.component.ComponentEventListener;

import org.ikasan.vaadin.visjs.network.event.SelectEdgeEvent;

/**
 * Fired when a edge has been selected by the user.
 *
 * @see <a href="http://visjs.org/docs/network/#Events">http://visjs.org/docs/network/#Events</a>
 *
 * @author watho
 *
 */
public interface SelectEdgeListener extends ComponentEventListener<SelectEdgeEvent> {
}
