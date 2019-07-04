package org.ikasan.vaadin.visjs.network.listener;

import com.vaadin.flow.component.ComponentEventListener;

import org.ikasan.vaadin.visjs.network.event.DoubleClickEvent;

/**
 * Fired when the user double clicks the mouse or double taps on a touchscreen device. Since a
 * double click is in fact 2 clicks, 2 click events are fired, followed by a double click event.
 *
 * @see <a href="http://visjs.org/docs/network/#Events">http://visjs.org/docs/network/#Events</a>
 *
 * @author watho
 *
 */
public interface DoubleClickListener extends ComponentEventListener<DoubleClickEvent> {
}
