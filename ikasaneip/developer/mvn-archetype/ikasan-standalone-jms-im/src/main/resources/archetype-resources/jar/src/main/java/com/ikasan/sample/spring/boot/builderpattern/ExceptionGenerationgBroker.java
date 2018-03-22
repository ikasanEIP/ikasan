package com.ikasan.sample.spring.boot.builderpattern;

import org.ikasan.spec.component.endpoint.Broker;
import org.ikasan.spec.component.endpoint.EndpointException;

/**
 * Created by majean on 09/10/2017.
 */
public class ExceptionGenerationgBroker implements Broker
{
    private boolean shouldThrowExclusionException = false;

    private boolean shouldThrowRecoveryException = false;

    private boolean shouldThrowStoppedInErrorException = false;

    @Override public Object invoke(Object o) throws EndpointException
    {
        if(shouldThrowExclusionException){
            throw  new SampleGeneratedException("This exception is thrown to test exclusion.");
        }

        if(shouldThrowRecoveryException){
            throw  new EndpointException("This exception is thrown to test recovery.");
        }

        if(shouldThrowStoppedInErrorException){
            throw  new RuntimeException("This exception is thrown to test stoppedInError.");
        }
        return o;
    }

    public void setShouldThrowExclusionException(boolean shouldThrowExclusionException)
    {
        this.shouldThrowExclusionException = shouldThrowExclusionException;
    }

    public void setShouldThrowRecoveryException(boolean shouldThrowRecoveryException)
    {
        this.shouldThrowRecoveryException = shouldThrowRecoveryException;
    }

    public void setShouldThrowStoppedInErrorException(boolean shouldThrowStoppedInErrorException)
    {
        this.shouldThrowStoppedInErrorException = shouldThrowStoppedInErrorException;
    }
}
