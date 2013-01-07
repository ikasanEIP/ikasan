/*
 * $Id$
 * $URL$
 * 
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * Copyright (c) 2003-2008 Mizuho International plc. and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the 
 * Free Software Foundation Europe e.V. Talstrasse 110, 40217 Dusseldorf, Germany 
 * or see the FSF site: http://www.fsfeurope.org/.
 * ====================================================================
 */
package org.ikasan.connector.base.socket;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Arrays;

import org.apache.log4j.Logger;

/**
 * Socket based class with commonly used send and receive socket operations.
 * 
 * @author Ikasan Development Team
 */
public class TCPSocket extends java.net.Socket
{
    /** Logger */
    private static Logger logger = Logger.getLogger(TCPSocket.class);

    /** Buffer to be used for socket input */
    private BufferedInputStream bins = null;

    /** Buffer to be used for socket output */
    private BufferedOutputStream bons = null;

    /** Whether or not the TCP socket is connected */
    private boolean tcpSocketConnected = false;

    /** Whether or not to log the TCP socket traffic */
    private boolean logSocketTraffic = false;

    /** timestamp in millis of last data successfully recv'd on the socket */
    private Long recvActivity = new Long(0);

    /** timestamp in millis of last data successfully sent on the socket */
    private Long sendActivity = new Long(0);

    // The necessary connection information. All timeout values are in
    // milliseconds.
    /** server to connect to */
    private String server;

    /** port for connection */
    private int port;

    /** connection response timeout */
    private int connectionTimeout;

    /** socket data response timeout */
    private int responseTimeout;

    /** default connection response timeout in millis */
    private static final int DEFAULT_CONNECTION_TIMEOUT = 5000;

    /** default data response timeout in millis */
    private static final int DEFAULT_RESPONSE_TIMEOUT = 5000;

    /** maximum data response timeout in millis */
    private static final int MAXIMUM_RESPONSE_TIMEOUT = 5000;

    /**
     * Default constructor
     */
    public TCPSocket()
    {
        super();
        this.responseTimeout = DEFAULT_RESPONSE_TIMEOUT;
        this.connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;
    }

    /**
     * Utility constructor that takes as parameters the target host and port and
     * uses the underlying defaults for response and connection timeouts.
     * 
     * @param server The target host
     * @param port The target port
     */
    public TCPSocket(final String server, final int port)
    {
        this();
        this.server = server;
        this.port = port;
    }

    /**
     * Utility constructor that takes as parameters the target host and port as
     * well as the response and connection timeouts.
     * 
     * @param server The target host
     * @param port The target port
     * @param connectionTimeout The connection timeout value in milliseconds
     * @param responseTimeout The response timeout value in milliseconds
     */
    public TCPSocket(final String server, final int port, final int connectionTimeout, final int responseTimeout)
    {
        this();
        this.server = server;
        this.port = port;
        this.connectionTimeout = connectionTimeout;
        this.responseTimeout = responseTimeout;
    }

    /* Methods relating to connecting the socket */
    /**
     * Established the <code>Socket</code> level connection to target server.
     * 
     * @throws SocketTimeoutException - Exception if the Socket times out
     * @throws IOException - Generic Input/Output Exception
     */
    public void connect() throws SocketTimeoutException, IOException
    {
        // Sanity check
        // if (super.isConnected())
        if (this.isTCPSocketConnected())
        {
            logger.info("Socket already connected to [" + super.getInetAddress() //$NON-NLS-1$
                    + "]; Ignoring connection request."); //$NON-NLS-1$
            return;
        }
        // Create socket address. Will throw IllegalArgumentException if the
        // port parameter is outside the range of valid port values, or if the
        // hostname parameter is null. TODO: Should we wrap these exceptions?
        SocketAddress sa = new InetSocketAddress(this.server, this.port);
        // Log useful debugging info and try to connect
        if (logger.isDebugEnabled())
        {
            StringBuilder sb = new StringBuilder(128);
            sb.append("Target server = ["); //$NON-NLS-1$
            sb.append(sa);
            sb.append("]; Connection timeout = ["); //$NON-NLS-1$
            sb.append(this.connectionTimeout);
            sb.append("] milliseconds."); //$NON-NLS-1$
            logger.debug(sb.toString());
        }
        super.connect(sa, this.connectionTimeout);
        this.bins = new BufferedInputStream(super.getInputStream());
        this.bons = new BufferedOutputStream(super.getOutputStream());
        // Note: SOTimeout can only be set once before any blocking operation
        super.setSoTimeout(this.responseTimeout);
        this.tcpSocketConnected = true;
    }

    /* Methods relating to writing data to the socket */
    /**
     * Sends a <code>byte[]</code> to the underlying socket. If the
     * <code>startMarker</code> or <code>endMarker</code> fields are not null
     * they will be used to frame the input accordingly.
     * 
     * @param data The data to write to the socket
     * @param startMarker - Start point for writing data
     * @param endMarker - End point for writing data
     * @throws IOException If the underlying <code>write</code> or
     *             <code>flush</code> operations fails.
     */
    public void send(byte[] data, byte startMarker, byte endMarker) throws IOException
    {
        byte[] framedData = frameOutboundData(data, startMarker, endMarker);
        if (logSocketTraffic) logger.info("Sending [" + Arrays.toString(framedData) + "]..."); //$NON-NLS-1$//$NON-NLS-2$
        try
        {
            this.bons.write(framedData);
            this.bons.flush();
            this.sendActivity = java.util.Calendar.getInstance().getTimeInMillis();
        }
        catch (IOException e)
        {
            this.tcpSocketConnected = false;
            throw e;
        }
        logger.info("Send Successful! Bytes written to socket = [" + framedData.length + "]"); //$NON-NLS-1$//$NON-NLS-2$
    }

    /**
     * Sends a <code>String</code> to the underlying socket. If the
     * <code>startMarker</code> or <code>endMarker</code> fields are not null
     * they will be used to frame the input accordingly.
     * 
     * @param data The data to write to the socket
     * @param startMarker - Start point to write data
     * @param endMarker - End point to write data
     * @throws IOException If the underlying <code>write</code> or
     *             <code>flush</code> operations fails.
     */
    public void send(String data, byte startMarker, byte endMarker) throws IOException
    {
        byte[] framedData = frameOutboundData(this.stringToByteArray(data), startMarker, endMarker);
        if (logSocketTraffic) logger.info("Sending [" + Arrays.toString(framedData) + "]..."); //$NON-NLS-1$ //$NON-NLS-2$
        try
        {
            this.bons.write(framedData);
            this.bons.flush();
            this.sendActivity = java.util.Calendar.getInstance().getTimeInMillis();
        }
        catch (IOException e)
        {
            this.tcpSocketConnected = false;
            throw e;
        }
        logger.info("Send Successful! Bytes written to socket = [" + framedData.length + "]"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Method used to frame a <code>byte[]</code> by pre-pending it with a start
     * marker (default is STX byte <code>0x2</code>) and appending it with an
     * end marker (default is ETX byte <code>0x3</code>).
     * 
     * @param data The data array to framed.
     * @param startMarker - Start point to write data
     * @param endMarker - End point to write data
     * @return A framed copy of the input <code>byte[]</code>.
     */
    private byte[] frameOutboundData(byte[] data, Byte startMarker, Byte endMarker)
    {
        if (startMarker != null && endMarker != null)
        {
            byte[] framedData = new byte[data.length + 2];
            System.arraycopy(data, 0, framedData, 1, data.length);
            framedData[0] = startMarker;
            framedData[framedData.length - 1] = endMarker;
            return framedData;
        }
        else if (startMarker != null && endMarker == null)
        {
            byte[] framedData = new byte[data.length + 1];
            System.arraycopy(data, 0, framedData, 1, data.length);
            framedData[0] = startMarker;
            return framedData;
        }
        else if (startMarker == null && endMarker != null)
        {
            byte[] framedData = new byte[data.length + 1];
            System.arraycopy(data, 0, framedData, 0, data.length);
            framedData[framedData.length - 1] = endMarker;
            return framedData;
        }
        // No start or end marker defined, return as is
        else
            return data;
    }

    /* Methods relating to reading data from the socket */
    /**
     * Method used to read the socket (byte by byte) for a message based on the
     * start and end markers as well as a socket timeout.
     * 
     * @param startMarker The <code>byte</code> marker indicating the start of a
     *            message.
     * @param endMarker The <code>byte</code> marker indicating the end of a
     *            message.
     * @return A <code>byte[]</code> representation of a single message
     *         received.
     * @throws SocketTimeoutException To indicate a read timeout.
     * @throws SocketException To indicate an error in the underlying protocol.
     * @throws IOException To indicate a connection exception.
     */
    public byte[] recv(final byte startMarker, final byte endMarker) throws SocketTimeoutException, SocketException, IOException
    {
        // Clean the buffers used to hold message or bad data
        ByteArrayOutputStream badBuffer = new ByteArrayOutputStream();
        ByteArrayOutputStream msgBuffer = new ByteArrayOutputStream();
        // Read the socket (byte by byte) for a message based on the start and
        // end markers by consuming all the data before a start marker is
        // encountered into a buffer of unexpected(bad) data and then by reading
        // all the way up until an endMarker is encountered in a message buffer
        byte[] singleByte = new byte[1];
        // ---- TODO: Put this into a method called purgeInvalidBytes() ----
        try
        {
            int bytesRead;
            do
            {
                if ((bytesRead = this.bins.read(singleByte)) < 0) throw new SocketException("Connection reset by peer; EOF received!"); //$NON-NLS-1$
                if (bytesRead > 0 && singleByte[0] != startMarker) badBuffer.write(singleByte);
                // don't let the bad buffer blow memory
                if (badBuffer.size() > 1024)
                {
                    logger.warn("Unexpected data [" + badBuffer.toString() + "] " //$NON-NLS-1$//$NON-NLS-2$
                            + "received on socket. Ignoring it and resetting buffer."); //$NON-NLS-1$
                    badBuffer = new ByteArrayOutputStream();
                }
            }
            while (singleByte[0] != startMarker);
        }
        catch (SocketTimeoutException e)
        {
            if (badBuffer.size() > 0)
            {
                // We have collected unexpected data, log a warning
                logger.warn("Unexpected data read from socket [" //$NON-NLS-1$
                        + super.getInetAddress() + "] before timeout and will be ignored. Unexpected data [" //$NON-NLS-1$
                        + badBuffer.toString() + "]."); //$NON-NLS-1$
            }
            else
            {
                logger.debug("No data on socket before timeout."); //$NON-NLS-1$
            }
            throw e;
        }
        catch (SocketException e)
        {
            this.tcpSocketConnected = false;
            throw e;
        }
        catch (IOException e)
        {
            this.tcpSocketConnected = false;
            throw e;
        }
        // ---- TODO: Put above into a method called purgeInvalidBytes() ----
        // ---- However, this may not be a great idea as this is only a
        // marker-to-marker solution ----
        // If any unexpected data was found, ignore it but issue a warning
        if (badBuffer.size() > 0)
        {
            logger.warn("Unexpected data read from socket [" + super.getInetAddress() + ':' + super.getPort() + "] which will be ignored. Unexpected data ["
                    + badBuffer.toString() + "].");
        }
        // If we haven't timed out and startMarker was found, read the expected
        // data
        try
        {
            int bytesRead = 0;
            int timeouts = ((this.getSoTimeout() == 0) && (this.getSoTimeout() >= MAXIMUM_RESPONSE_TIMEOUT)) ? 0 : (MAXIMUM_RESPONSE_TIMEOUT / this
                .getSoTimeout());
            do
            {
                try
                {
                    if ((bytesRead = this.bins.read(singleByte)) < 0)
                    {
                        StringBuilder sb = new StringBuilder(256);
                        sb.append("Connection reset by peer while receiving data; EOF received! "); //$NON-NLS-1$
                        if (msgBuffer.size() > 0)
                        {
                            sb.append("Partial data received before interruption will be ignored"); //$NON-NLS-1$
                            if (logSocketTraffic) sb.append(" [").append(Arrays.toString(msgBuffer.toByteArray())).append(']'); //$NON-NLS-1$
                        }
                        throw new SocketException(sb.toString());
                    }
                    if (bytesRead > 0 && singleByte[0] != endMarker) msgBuffer.write(singleByte);
                }
                // Timeout on partial data read - if the timeout is very small,
                // this could be due to
                // network traffic. So need an intelligent way of ignoring a
                // number of these.
                catch (SocketTimeoutException e)
                {
                    if (timeouts == 0) throw e;
                    timeouts--;
                    logger.warn("Valid data received before timeout; Continue read! Continue attempts left [" + timeouts + ']', e);
                }
            }
            while (singleByte[0] != endMarker && timeouts > 0);
            // By this point, the msgBuffer contains a complete message without
            // any markers which we can get into a byte array and return.
            if (logSocketTraffic) logger.info("Received [" + Arrays.toString(msgBuffer.toByteArray()) + "]"); //$NON-NLS-1$//$NON-NLS-2$
            // keep track of the last time we successfully read data off the
            // line
            this.recvActivity = java.util.Calendar.getInstance().getTimeInMillis();
            return msgBuffer.toByteArray();
        }
        catch (SocketTimeoutException e)
        {
            // Socket timed-out without receiving a legitimate endMarker - hence
            // not valid message.
            logger.warn("Socket timeout while receiving message. Partial message received and " //$NON-NLS-1$
                    + "ignored" + (logSocketTraffic ? (" [" + Arrays.toString(msgBuffer.toByteArray()) //$NON-NLS-1$//$NON-NLS-2$
                    + "].") : "!")); //$NON-NLS-1$//$NON-NLS-2$
            throw e;
        }
        catch (SocketException e)
        {
            this.tcpSocketConnected = false;
            throw e;
        }
        catch (IOException e)
        {
            this.tcpSocketConnected = false;
            throw e;
        }
    }

    /**
     * Return whether or not the TCP socket is connected
     * 
     * @return true if connected
     */
    public boolean isTCPSocketConnected()
    {
        boolean connected = this.tcpSocketConnected && !this.isClosed();
        return connected;
    }

    /* Utility methods */
    /**
     * This method is currently more a place-holder for more elaborate
     * conversion of <code>String</code>s to <code>byte[]</code>
     * 
     * TODO ** Verify/Correct commented out code below
     * 
     * @param string - The data to convert into a byte[]
     * 
     * @return <code>byte[]</code> representation of the <code>String</code>
     *         parameter
     */
    private byte[] stringToByteArray(String string)
    {
        // Charset charset = Charset.forName("UTF-8");
        // CharsetEncoder encoder = charset.newEncoder();
        // CharBuffer charBuffer = CharBuffer.wrap(string, 0, 3);
        // ByteBuffer buffer = ByteBuffer.allocateDirect(3);
        // CoderResult encodingResult = encoder.encode(charBuffer, buffer, true
        // /*no more input*/);
        // return buffer.array();
        return string.getBytes();
    }

    /* Additional getters/setters for the class fields */
    /**
     * @return the connection timeout in milliseconds
     */
    public int getConnectionTimeout()
    {
        logger.debug("Getting connectionTimeout [" + this.connectionTimeout + "]"); //$NON-NLS-1$//$NON-NLS-2$
        return this.connectionTimeout;
    }

    /**
     * @param timeout The connection timeout to use in milliseconds
     */
    public void setConnectionTimeout(final int timeout)
    {
        logger.debug("Setting connectionTimeout to [" + timeout + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        this.connectionTimeout = timeout;
    }

    /**
     * @param port the port to set
     */
    public void setPort(final int port)
    {
        logger.debug("Setting port to [" + port + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        this.port = port;
    }

    /**
     * @return the response timeout in milliseconds
     */
    public int getResponseTimeout()
    {
        logger.debug("Getting responseTimeout [" + this.responseTimeout + "]"); //$NON-NLS-1$//$NON-NLS-2$
        return this.responseTimeout;
    }

    /**
     * Set the response timeout
     * 
     * @param timeout - The response timeout to set
     */
    public void setResponseTimeout(final int timeout)
    {
        logger.debug("Setting responseTimeout to [" + timeout + "]"); //$NON-NLS-1$//$NON-NLS-2$
        this.responseTimeout = timeout;
    }

    /**
     * @return the server
     */
    public String getServer()
    {
        logger.debug("Getting server [" + this.server + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        return this.server;
    }

    /**
     * @param server the server to set
     */
    public void setServer(final String server)
    {
        logger.debug("Setting server to [" + server + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        this.server = server;
    }

    /**
     * @return the logSocketTraffic
     */
    public boolean isLogSocketTraffic()
    {
        logger.debug("Getting logSocketTraffic [" + this.logSocketTraffic + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        return this.logSocketTraffic;
    }

    /**
     * @param logSocketTraffic the logSocketTraffic to set
     */
    public void setLogSocketTraffic(boolean logSocketTraffic)
    {
        logger.debug("Setting logSocketTraffic to [" + logSocketTraffic + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        this.logSocketTraffic = logSocketTraffic;
    }

    /**
     * Returns the last successful data socket recv time in millis
     * 
     * @return recvActivity
     */
    public Long getRecvActivity()
    {
        logger.debug("Getting recvActivity to [" + recvActivity + "]");
        return this.recvActivity;
    }

    /**
     * Returns the last successful data socket send in millis
     * 
     * @return sendActivity
     */
    public Long getSendActivity()
    {
        logger.debug("Getting sendActivity to [" + sendActivity + "]");
        return this.sendActivity;
    }

    /**
     * Method that returns a <code>String</code> representation of this socket's
     * connection properties. These are:
     * <ul>
     * <li>Server</li>
     * <li>Port</li>
     * <li>Connection timeout</li>
     * <li>Response timeout</li>
     * </ul>
     * 
     * @return String representation of the connecton properties
     */
    public String connectionPropertiesToString()
    {
        StringBuilder sb = new StringBuilder(256);
        sb.append("Server = [").append(this.server).append(']'); //$NON-NLS-1$
        sb.append("; Port = [").append(this.port).append(']'); //$NON-NLS-1$
        sb.append("; Connection timeout = [").append(this.connectionTimeout).append(']'); //$NON-NLS-1$
        sb.append("; Response timeout = [").append(this.responseTimeout).append(']'); //$NON-NLS-1$
        return sb.toString();
    }
}
