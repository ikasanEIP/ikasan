package org.ikasan.vaadin.visjs.network.listener;

import com.vaadin.flow.component.ComponentEventListener;

import org.ikasan.vaadin.visjs.network.event.StartStabilizingEvent;

/**
 * Fired when stabilization starts. This is also the case when you drag a node and the physics
 * simulation restarts to stabilize again. Stabilization does not neccesarily imply 'without
 * showing'.
 *
 * @see <a href="http://visjs.org/docs/network/#Events">http://visjs.org/docs/network/#Events</a>
 *
 * @author watho
 *
 */
public interface StartStabilizingListener extends ComponentEventListener<StartStabilizingEvent> {
}
