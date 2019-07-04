package org.ikasan.vaadin.visjs.network.listener;

import com.vaadin.flow.component.ComponentEventListener;

import org.ikasan.vaadin.visjs.network.event.StabilizedEvent;

/**
 * Fired when the network has stabilized or when the stopSimulation() has been called. The amount of
 * iterations it took could be used to tweak the maximum amount of iterations needed to stabilize
 * the network.
 *
 * @see <a href="http://visjs.org/docs/network/#Events">http://visjs.org/docs/network/#Events</a>
 *
 * @author watho
 *
 */
public interface StabilizedListener extends ComponentEventListener<StabilizedEvent> {
}
