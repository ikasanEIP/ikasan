package org.ikasan.ootb.scheduler.agent.rest.dto;

import java.io.Serializable;

public class ErrorDto implements Serializable
{
    private String errorCode;
    private String errorMessage;

    public ErrorDto(){

    }

    public ErrorDto(String errorMessage)
    {
        this.errorMessage = errorMessage;
    }

    public ErrorDto(String errorCode, String errorMessage)
    {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public String getErrorCode()
    {
        return errorCode;
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
