package org.ikasan.replay.model;

/**
 * Created by Ikasan Development Team on 30/10/2017.
 */
public class ReplayResponse
{
    private boolean success;
    private String responseBody;
    private Exception exception;

    public ReplayResponse(boolean success, String responseBody, Exception exception)
    {
        this.success = success;
        this.responseBody = responseBody;
        this.exception = exception;
    }

    public boolean isSuccess()
    {
        return success;
    }

    public String getResponseBody()
    {
        return responseBody;
    }

    public Exception getException()
    {
        return exception;
    }
}
