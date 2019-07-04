package org.ikasan.vaadin.visjs.network.listener;

import com.vaadin.flow.component.ComponentEventListener;

import org.ikasan.vaadin.visjs.network.event.InitRedrawEvent;

/**
 * Fired before the redrawing begins. The simulation step has completed at this point. Can be used
 * to move custom elements before starting drawing the new frame.
 *
 * @see <a href="http://visjs.org/docs/network/#Events">http://visjs.org/docs/network/#Events</a>
 *
 * @author watho
 *
 */
public interface InitRedrawListener extends ComponentEventListener<InitRedrawEvent> {
}
