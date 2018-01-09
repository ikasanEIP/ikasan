package org.ikasan.endpoint.ftp;

/**
 * Exception thrown when SFTP connector have not started correctly.
 *
 * @author Ikasan Development Team
 */
public class FtpResourceNotStartedException extends RuntimeException
{
    public FtpResourceNotStartedException(String message)
    {
        super(message);
    }
}
