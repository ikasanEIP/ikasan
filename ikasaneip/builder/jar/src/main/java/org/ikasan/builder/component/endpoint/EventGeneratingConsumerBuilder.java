package org.ikasan.builder.component.endpoint;

import org.ikasan.builder.component.Builder;
import org.ikasan.component.endpoint.consumer.api.spec.EndpointEventProvider;
import org.ikasan.spec.component.endpoint.Consumer;
import org.ikasan.spec.event.ManagedEventIdentifierService;

/**
 * Contract for a default event generating consumer builder.
 *
 * @author Ikasan Development Team.
 */
public interface EventGeneratingConsumerBuilder extends Builder<Consumer>
{
    /**
     * Set the endpoint event provider for this event consumer.
     * @param eventProvider
     * @return
     */
    EventGeneratingConsumerBuilder setEndpointEventProvider(EndpointEventProvider eventProvider);

    /**
     * Set the ManagedEventIdentifierService for this endpoint event provider.
     * @param managedEventIdentifierService
     * @return
     */
    EventGeneratingConsumerBuilder setManagedEventIdentifierService(ManagedEventIdentifierService managedEventIdentifierService);
}
