package org.ikasan.endpoint.ftp.consumer;

import javax.resource.ResourceException;

import org.ikasan.spec.endpoint.Consumer;
import org.ikasan.spec.endpoint.EndpointActivator;
import org.ikasan.spec.endpoint.EndpointFactory;
import org.ikasan.spec.endpoint.EndpointManager;

/**
 * @author hasasu
 *
 */
public class FtpConsumerEndpointManager implements EndpointManager<Consumer<?>, FtpConsumerConfiguration>
{
    private FtpConsumerConfiguration configuration;
    private Consumer<?> consumer;
    private final EndpointFactory<Consumer<?>, FtpConsumerConfiguration> endpointFactory;

    /**
     * 
     * @param factory
     */
    public FtpConsumerEndpointManager(final EndpointFactory<Consumer<?>, FtpConsumerConfiguration> factory, final FtpConsumerConfiguration config)
    {
        this.endpointFactory = factory;
        if (this.endpointFactory == null)
        {
            throw new IllegalArgumentException("EndpointFactory cannot be null.");
        }
        this.configuration = config;
    }
    /*
     * (non-Javadoc)
     * @see org.ikasan.spec.endpoint.EndpointManager#setConfiguration(java.lang.Object)
     */
    public void setConfiguration(FtpConsumerConfiguration configuration)
    {
        this.configuration = configuration;
    }

    /*
     * (non-Javadoc)
     * @see org.ikasan.spec.endpoint.EndpointManager#getConfiguration()
     */
    public FtpConsumerConfiguration getConfiguration()
    {
        return this.configuration;
    }

    /*
     * (non-Javadoc)
     * @see org.ikasan.spec.endpoint.EndpointManager#getEndpoint()
     */
    public Consumer<?> getEndpoint()
    {
        return this.consumer;
    }

    /*
     * (non-Javadoc)
     * @see org.ikasan.spec.endpoint.EndpointManager#start()
     */
    public void start() throws ResourceException
    {
        this.configuration.validate();
        this.consumer = this.endpointFactory.createEndpoint(this.configuration);
        if (this.consumer instanceof EndpointActivator)
        {
            ((EndpointActivator) this.consumer).activate();
        }
        // TODO what if factory returned null?
    }

    /*
     * (non-Javadoc)
     * @see org.ikasan.spec.endpoint.EndpointManager#stop()
     */
    public void stop() throws ResourceException
    {
        try
        {
            if (this.consumer instanceof EndpointActivator)
            {
                ((EndpointActivator) this.consumer).deactivate();
            }
        }
        finally
        {
            this.consumer = null;
        }
    }

}
