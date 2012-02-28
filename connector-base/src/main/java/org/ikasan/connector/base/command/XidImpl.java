/*
 * $Id$
 * $URL$
 * 
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * 
 * Distributed under the Modified BSD License.
 * Copyright notice: The copyright for this software and a full listing 
 * of individual contributors are as shown in the packaged copyright.txt 
 * file. 
 * 
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 *
 *  - Neither the name of the ORGANIZATION nor the names of its contributors may
 *    be used to endorse or promote products derived from this software without 
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE 
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE 
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
