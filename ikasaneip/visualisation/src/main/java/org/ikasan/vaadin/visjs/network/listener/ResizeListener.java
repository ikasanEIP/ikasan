package org.ikasan.vaadin.visjs.network.listener;

import com.vaadin.flow.component.ComponentEventListener;

import org.ikasan.vaadin.visjs.network.event.ResizeEvent;

/**
 * Fired when the size of the canvas has been resized, either by a redraw call when the container
 * div has changed in size, a setSize() call with new values or a setOptions() with new width and/or
 * height values.
 *
 * @see <a href="http://visjs.org/docs/network/#Events">http://visjs.org/docs/network/#Events</a>
 *
 * @author watho
 *
 */
public interface ResizeListener extends ComponentEventListener<ResizeEvent> {
}
