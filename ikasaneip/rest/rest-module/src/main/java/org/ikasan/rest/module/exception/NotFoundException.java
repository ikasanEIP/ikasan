package org.ikasan.rest.module.exception;

public class NotFoundException extends RuntimeException
{
    public NotFoundException(String message){
        super(message);
    }

    public NotFoundException(String message,Throwable t){
        super(message,t);
    }
}
