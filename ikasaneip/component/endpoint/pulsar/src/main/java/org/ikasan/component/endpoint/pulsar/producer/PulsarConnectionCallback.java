package org.ikasan.component.endpoint.pulsar.producer;

import org.apache.pulsar.client.api.PulsarClientException;

/**
 * Callback interface for Pulsar connection operations.
 * This callback is executed during the transaction commit phase.
 *
 * @author Ikasan Development Team
 */
public interface PulsarConnectionCallback {

    /**
     * Execute the callback operation to send message to Pulsar.
     *
     * @throws PulsarClientException if an error occurs sending to Pulsar
     */
    void execute() throws PulsarClientException;
}
