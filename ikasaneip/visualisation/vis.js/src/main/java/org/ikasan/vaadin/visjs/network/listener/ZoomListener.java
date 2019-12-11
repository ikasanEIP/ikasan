package org.ikasan.vaadin.visjs.network.listener;

import com.vaadin.flow.component.ComponentEventListener;

import org.ikasan.vaadin.visjs.network.event.ZoomEvent;

/**
 * Fired when the user zooms in or out. The properties tell you which direction the zoom is in. The
 * scale is a number greater than 0, which is the same that you get with network.getScale(). When
 * fired by clicking the zoom in or zoom out navigation buttons, the pointer property of the object
 * passed will be null.
 *
 * @see <a href="http://visjs.org/docs/network/#Events">http://visjs.org/docs/network/#Events</a>
 *
 * @author watho
 *
 */
public interface ZoomListener extends ComponentEventListener<ZoomEvent> {
}
