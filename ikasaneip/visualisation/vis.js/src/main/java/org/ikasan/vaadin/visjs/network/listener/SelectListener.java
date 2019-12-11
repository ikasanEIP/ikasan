package org.ikasan.vaadin.visjs.network.listener;

import com.vaadin.flow.component.ComponentEventListener;

import org.ikasan.vaadin.visjs.network.event.SelectEvent;

/**
 * Fired when the selection has changed by user action. This means a node or edge has been selected,
 * added to the selection or deselected. All select events are only triggered on click and hold.
 *
 * @see <a href="http://visjs.org/docs/network/#Events">http://visjs.org/docs/network/#Events</a>
 *
 * @author watho
 *
 */
public interface SelectListener extends ComponentEventListener<SelectEvent> {
}
