package org.ikasan.endpoint.sftp;

/**
 * Exception thrown when SFTP connector have not started correctly.
 *
 * @author Ikasan Development Team
 */
public class SftpResourceNotStartedException extends RuntimeException
{
    public SftpResourceNotStartedException(String message)
    {
        super(message);
    }
}
