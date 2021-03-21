package org.ikasan.component.factory.spring;

public class IkasanComponentFactoryException extends RuntimeException
{
    public IkasanComponentFactoryException(String message){
        super(message);
    }

    public IkasanComponentFactoryException(String message, Exception exc){
        super(message, exc);
    }
}
