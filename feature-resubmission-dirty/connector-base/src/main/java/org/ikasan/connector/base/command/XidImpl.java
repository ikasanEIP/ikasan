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
package org.ikasan.connector.base.command;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.transaction.xa.Xid;

/**
 * Local Implementation of the <code>Xid</code> interface
 * 
 * Essentially implemented to allow local instantiation of previously persisted
 * Transaction identifiers. These would originally been externally generated
 * implementations
 * 
 * NOTE:  We actually manipulate the setters and getters to store and retrieve 
 * the globalTransactionId and branchQualifier as Strings, for efficient database 
 * interaction.
 * 
 * @author Ikasan Development Team
 */
public class XidImpl implements Xid
{

    /** Identity */
    private Long id;

    /** Global Transaction Id */
    private byte[] globalTransactionId;

    /** Branch Qualifier */
    private byte[] branchQualifier;

    /** Format Id */
    private int formatId;

    /** State */
    private String state = "test";

    /** Client Id */
    private String clientId;

    /** Set of commands */
    private Set<TransactionalResourceCommand> commands = new LinkedHashSet<TransactionalResourceCommand>();

    /** Created Date Time */
    private Date createdDateTime;
    
    /** Last Updated Date Time */
    private Date lastUpdatedDateTime;
    
    /**
     * Constructor
     * 
     * @param globalTransactionId
     * @param branchQualifier
     * @param formatId
     */
    public XidImpl(byte[] globalTransactionId, byte[] branchQualifier, int formatId)
    {
        super();
        this.globalTransactionId = globalTransactionId;
        this.branchQualifier = branchQualifier;
        this.formatId = formatId;
        this.createdDateTime = new Date();
        this.lastUpdatedDateTime = new Date();
    }

    /**
     * Constructor
     * 
     * @param xid
     */
    public XidImpl(Xid xid)
    {
        this(xid.getGlobalTransactionId(), xid.getBranchQualifier(), xid.getFormatId());
    }

    /**
     * No Args Default Constructor as required by Hibernate
     */
    public XidImpl()
    {
        // Constructor
    }

    public byte[] getBranchQualifier()
    {
        return branchQualifier;
    }

    /**
     * Get the branch qualifier as string
     * @return the branch qualifier as string
     */
    protected String getBranchQualifierString()
    {
        return new String(branchQualifier);
    }
    
    public int getFormatId()
    {
        return formatId;
    }

    public byte[] getGlobalTransactionId()
    {
        return globalTransactionId;
    }

    /**
     * Get the global txn id as string
     * @return global txn id as string
     */
    protected String getGlobalTransactionIdString()
    {
        return new String(globalTransactionId);
    }
    
    /**
     * Setter method - required by Hibernate
     * 
     * @param branchQualifierAsString
     */
    protected void setBranchQualifierString(String branchQualifierAsString)
    {
        this.branchQualifier = branchQualifierAsString.getBytes();
    }

    /**
     * Setter method - required by Hibernate
     * 
     * @param globalTransactionIdAsString
     */
    protected void setGlobalTransactionIdString(String globalTransactionIdAsString)
    {
        this.globalTransactionId = globalTransactionIdAsString.getBytes();
    }

    /**
     * Setter method - required by Hibernate
     * 
     * @param formatId
     */
    @SuppressWarnings("unused")
    private void setFormatId(int formatId)
    {
        this.formatId = formatId;
    }

    /**
     * @param transactionalResourceCommand
     */
    public void addCommand(TransactionalResourceCommand transactionalResourceCommand)
    {
        commands.add(transactionalResourceCommand);
    }

    /**
     * Get the state
     * 
     * @return state
     */
    public String getState()
    {
        return state;
    }

    /**
     * Set the state
     * 
     * @param state
     */
    public void setState(String state)
    {
        this.state = state;
    }

    /**
     * Get the id
     * 
     * @return id
     */
    public Long getId()
    {
        return id;
    }

    /**
     * Set the id (used by Hibernate)
     * 
     * @param id
     */
    @SuppressWarnings("unused")
    private void setId(Long id)
    {
        this.id = id;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
    	StringBuffer sb = new StringBuffer("XidImpl [");
    	sb.append("formattedGlobalTransactionId");sb.append( this.getFormattedGlobalTransactionId());sb.append(",");
    	sb.append("formattedBranchQualifier");sb.append(  this.getFormattedBranchQualifier());sb.append(",");
    	sb.append("commands");sb.append(this.commands);sb.append(",");
    	
        sb.append("formatId");sb.append( this.formatId);sb.append(",");
        sb.append("state");sb.append(this.state);sb.append(",");
        sb.append("id");sb.append(this.id);
    	sb.append("]");
    	return sb.toString();
    	
    }

    /**
     * Get the formattedBranchQualifier
     * 
     * @return formattedBranchQualifier
     */
    private String getFormattedBranchQualifier()
    {
        return new String(branchQualifier);
    }

    /**
     * Get the formattedGlobalTransactionId
     * 
     * @return formattedGlobalTransactionId
     */
    private String getFormattedGlobalTransactionId()
    {
        return new String(globalTransactionId);
    }

    /**
     * Get the list of commands
     * 
     * @return the list of commands
     */
    public Set<TransactionalResourceCommand> getCommands()
    {
        return commands;
    }

    /**
     * Set the clientId
     * 
     * @param clientId
     */
    public void setClientId(String clientId)
    {
        this.clientId = clientId;

    }

    /**
     * Get the clientID
     * 
     * @return clientID
     */
    public String getClientId()
    {
        return clientId;
    }

    /**
     * Get the created date time
     * @return the createdDateTime
     */
    public Date getCreatedDateTime()
    {
        return createdDateTime;
    }

    /**
     * Set the created date time
     * @param createdDateTime the createdDateTime to set
     */
    public void setCreatedDateTime(Date createdDateTime)
    {
        this.createdDateTime = createdDateTime;
    }

    /**
     * Get the last updated date time
     * @return the lastUpdatedDateTime
     */
    public Date getLastUpdatedDateTime()
    {
        return lastUpdatedDateTime;
    }

    /**
     * Set the last updated date time
     * @param lastUpdatedDateTime the lastUpdatedDateTime to set
     */
    public void setLastUpdatedDateTime(Date lastUpdatedDateTime)
    {
        this.lastUpdatedDateTime = lastUpdatedDateTime;
    }

}
