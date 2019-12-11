package org.ikasan.vaadin.visjs.network.listener;

import com.vaadin.flow.component.ComponentEventListener;

import org.ikasan.vaadin.visjs.network.event.AfterDrawingEvent;

/**
 * Fired after drawing on the canvas has been completed. Can be used to draw on top of the network.
 *
 * @see <a href="http://visjs.org/docs/network/#Events">http://visjs.org/docs/network/#Events</a>
 *
 * @author watho
 *
 */
public interface AfterDrawingListener extends ComponentEventListener<AfterDrawingEvent> {
}
