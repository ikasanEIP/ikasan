package org.ikasan.vaadin.visjs.network.listener;

import com.vaadin.flow.component.ComponentEventListener;

import org.ikasan.vaadin.visjs.network.event.StabilizationIterationsDoneEvent;

/**
 * Fired when the 'hidden' stabilization finishes. This does not necessarily mean the network is
 * stabilized; it could also mean that the amount of iterations defined in the options has been
 * reached.
 *
 * @see <a href="http://visjs.org/docs/network/#Events">http://visjs.org/docs/network/#Events</a>
 *
 * @author watho
 *
 */
public interface StabilizationIterationsDoneListener
    extends ComponentEventListener<StabilizationIterationsDoneEvent> {
}
