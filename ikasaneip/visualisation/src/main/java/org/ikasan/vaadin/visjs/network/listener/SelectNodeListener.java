package org.ikasan.vaadin.visjs.network.listener;

import com.vaadin.flow.component.ComponentEventListener;

import org.ikasan.vaadin.visjs.network.event.SelectNodeEvent;

/**
 * Fired when a node has been selected by the user.
 *
 * @see <a href="http://visjs.org/docs/network/#Events">http://visjs.org/docs/network/#Events</a>
 *
 * @author watho
 *
 */
public interface SelectNodeListener extends ComponentEventListener<SelectNodeEvent> {
}
