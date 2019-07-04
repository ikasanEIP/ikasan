package org.ikasan.vaadin.visjs.network.listener;

import com.vaadin.flow.component.ComponentEventListener;

import org.ikasan.vaadin.visjs.network.event.HoldEvent;

/**
 * Fired when the user clicks and holds the mouse or taps and holds on a touchscreen device. A click
 * event is also fired in this case.
 *
 * @see <a href="http://visjs.org/docs/network/#Events">http://visjs.org/docs/network/#Events</a>
 *
 * @author watho
 *
 */
public interface HoldListener extends ComponentEventListener<HoldEvent> {
}
