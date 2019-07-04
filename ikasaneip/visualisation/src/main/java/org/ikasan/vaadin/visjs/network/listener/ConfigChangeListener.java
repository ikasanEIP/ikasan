package org.ikasan.vaadin.visjs.network.listener;

import com.vaadin.flow.component.ComponentEventListener;

import org.ikasan.vaadin.visjs.network.event.ConfigChangeEvent;

/**
 * Fired when a user changes any option in the configurator. The options object can be used with the
 * setOptions method or stringified using JSON.stringify(). You do not have to manually put the
 * options into the network: this is done automatically. You can use the event to store user options
 * in the database.
 *
 * @see <a href="http://visjs.org/docs/network/#Events">http://visjs.org/docs/network/#Events</a>
 *
 * @author watho
 *
 */
public interface ConfigChangeListener extends ComponentEventListener<ConfigChangeEvent> {
}
