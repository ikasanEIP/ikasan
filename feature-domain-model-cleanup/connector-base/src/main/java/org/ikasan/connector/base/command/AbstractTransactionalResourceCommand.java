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

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.resource.ResourceException;
import javax.transaction.xa.Xid;

import org.apache.log4j.Logger;
import org.ikasan.connector.base.command.state.State;
import org.ikasan.connector.base.command.state.StateManager;
import org.ikasan.connector.base.command.state.Transition;
import org.ikasan.connector.base.journal.TransactionJournal;
import org.ikasan.connector.base.journal.TransactionJournalingException;
import org.springframework.beans.factory.BeanFactory;

/**
 * This base implementation of the <code>TransactionalResourceCommand</code>
 * enforces a valid state transition model
 * 
 * @author Ikasan Development Team
 * 
 */
public abstract class AbstractTransactionalResourceCommand implements TransactionalResourceCommand
{
    /** The logger instance. */
    private static Logger logger = Logger.getLogger(AbstractTransactionalResourceCommand.class);

    /**
     * Transient Reference to a bean factory, used to resolve singleton
     * resources To be able to resolve resources, this must be set following
     * explicit creation, and reloading from Hibernate
     */
    private BeanFactory beanFactory;

    /**
     * Reference to the underlying resource or system of which we are providing
     * transactional management
     */
    protected TransactionalResource transactionalResource;

    /** Timestamp format */
    private static final SimpleDateFormat timestampFormat = new SimpleDateFormat("dd/MM/yyyy kk:mm:ss.SSSS");
    
    /** Context object for encapsulating execution parameters */
    protected ExecutionContext executionContext;
    
    /** Journaling service for logging all significant state changes */
    private TransactionJournal transactionJournal;
    
    // State Definitions
    
    /** Represents the command before any methods have been called */
    public static final State INITIALISED_STATE = new State("Initialised");
    
    /** Represents the command whilst execution is in progress */
    public static final State EXECUTION_IN_PROGRESS = new State("ExecutionInProgress");
    
    /**
     * Represents the command immediately after the execute method has
     * successfully executed
     */
    public static final State EXECUTED_STATE = new State("Executed");
    
    /** Represents the command whilst the commit is in progress */
    public static final State COMMIT_IN_PROGRESS = new State("CommitInProgress");
    
    /**
     * Represents the command immediately after the commit method has
     * successfully executed
     */
    public static final State COMPLETED_COMMITTING = new State("CompletedComitting");
    
    /** Represents the command whilst the rollback is in progress */
    public static final State ROLLBACK_IN_PROGRESS = new State("RollbackInProgress");
    
    /**
     * Represents the command immediately after the rollback method has
     * successfully executed
     */
    public static final State ROLLED_BACK_STATE = new State("RolledBack");
    
    // Action Definitions
    
    /** represents the beginning of the execute step */
    private static final String BEGIN_EXECUTE_ACTION = "beginExecuteAction";
    
    /** represents the end of the execute step */
    private static final String COMPLETE_EXECUTE_ACTION = "completeExecuteAction";
    
    /** represents the beginning of the commit step */
    private static final String BEGIN_COMMIT_ACTION = "beginCommitAction";
    
    /** represents the end of the execute step */
    private static final String COMPLETE_COMMIT_ACTION = "completeCommitAction";
    
    /** represents the beginning of the rollback step */
    private static final String BEGIN_ROLLBACK_ACTION = "beginRollbackAction";
    
    /** represents the end of the rollback step */
    private static final String COMPLETE_ROLLBACK_ACTION = "completeRollbackAction";
    
    // State Model
    
    /** Contains all valid state transitions */
    private static final StateManager stateManager = new StateManager();

    /**
     * Reference to the Transaction associated with this command
     */
    private XidImpl xid;

    /**
     * Formatted timestamp created when command is executed
     */
    private String executionTimestamp;
    static
    {
        logger.debug("StateManager initialisation"); //$NON-NLS-1$
        stateManager.addTransition(new Transition(INITIALISED_STATE, BEGIN_EXECUTE_ACTION, EXECUTION_IN_PROGRESS));
        stateManager.addTransition(new Transition(EXECUTION_IN_PROGRESS, COMPLETE_EXECUTE_ACTION, EXECUTED_STATE));
        // TODO is this valid??
        stateManager.addTransition(new Transition(EXECUTION_IN_PROGRESS, BEGIN_ROLLBACK_ACTION, ROLLBACK_IN_PROGRESS));
        stateManager.addTransition(new Transition(EXECUTED_STATE, BEGIN_COMMIT_ACTION, COMMIT_IN_PROGRESS));
        stateManager.addTransition(new Transition(EXECUTED_STATE, BEGIN_ROLLBACK_ACTION, ROLLBACK_IN_PROGRESS));
        stateManager.addTransition(new Transition(COMMIT_IN_PROGRESS, COMPLETE_COMMIT_ACTION, COMPLETED_COMMITTING));
        stateManager.addTransition(new Transition(COMMIT_IN_PROGRESS, BEGIN_ROLLBACK_ACTION, ROLLBACK_IN_PROGRESS));
        stateManager.addTransition(new Transition(COMPLETED_COMMITTING, BEGIN_ROLLBACK_ACTION, ROLLBACK_IN_PROGRESS));
        stateManager.addTransition(new Transition(ROLLBACK_IN_PROGRESS, COMPLETE_ROLLBACK_ACTION, ROLLED_BACK_STATE));
    }
    
    /** 
     * TODO Check whether or not we're still concerned about state
     * 
     * The current state of this command
     */
    private State state;
    
    /** Unique id, assigned when persisted */
    private Long id;

    /** Constructor */
    public AbstractTransactionalResourceCommand()
    {
        this.state = INITIALISED_STATE;
    }

    public void commit() throws ResourceException
    {
        attemptAction(BEGIN_COMMIT_ACTION);
        doCommit();
        attemptAction(COMPLETE_COMMIT_ACTION);
    }

    /**
     * Attempt an action, but blow up if its illegal
     * 
     * @param action
     * @throws ResourceException
     */
    private void attemptAction(String action) throws ResourceException
    {
        if (!stateManager.isValidTransition(state, action))
        {
            reportIllegalTransition(action);
        }
        this.state = stateManager.getEndState(state, action);
        try
        {
            transactionJournal.notifyUpdate(this);
        }
        catch (TransactionJournalingException e)
        {
            throw new ResourceException("Exception notifying transaction journal", e.getCause()); //$NON-NLS-1$
        }
    }

    public ExecutionOutput execute(TransactionalResource resource, final Xid executingXid) throws ResourceException
    {

        logger.debug("execute called with xid [" + executingXid + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        if (transactionJournal == null)
        {
            throw new ResourceException("Command must have a TransactionJournal set prior to execution"); //$NON-NLS-1$
        }

        try
        {
            // transactionJournal.addCommand(this, xid);
            this.setXid(transactionJournal.resolveXid(executingXid));
        }
        catch (TransactionJournalingException e)
        {
            throw new ResourceException("Exception resolving Xid", e); //$NON-NLS-1$
        }

        ExecutionOutput result = null;

        attemptAction(BEGIN_EXECUTE_ACTION);

        setExecutionTimestamp(timestampFormat.format(new Date()));
        result = doExecute(resource);

        attemptAction(COMPLETE_EXECUTE_ACTION);
        return result;
    }

    public void rollback() throws ResourceException
    {
        attemptAction(BEGIN_ROLLBACK_ACTION);
        doRollback();
        attemptAction(COMPLETE_ROLLBACK_ACTION);
    }

    /**
     * Handle the illegal transition
     * 
     * @param action
     */
    private void reportIllegalTransition(String action)
    {
        throw new RuntimeException(
            "Invalid state transition!!, [" + action + "] should not be called whilst in state [" //$NON-NLS-1$//$NON-NLS-2$
                    + state.getName() + "] object is [" + this + "]"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Accessor method for command state
     * 
     * @return state
     */
    public String getState()
    {
        return state.getName();
    }

    /**
     * Execute method to be implemented by subclasses
     * 
     * @param resource
     * @return ExecutionOutput
     * @throws ResourceException
     */
    protected abstract ExecutionOutput doExecute(TransactionalResource resource) throws ResourceException;

    /**
     * Commit method to be implemented by subclasses
     * 
     * @throws ResourceException
     */
    protected abstract void doCommit() throws ResourceException;

    /**
     * Rollback method to be implemented by subclasses
     * 
     * @throws ResourceException
     */
    protected abstract void doRollback() throws ResourceException;

    public void setExecutionContext(ExecutionContext executionContext)
    {
        this.executionContext = executionContext;
    }

    /**
     * Accesser method for id
     * 
     * @return id
     */
    public Long getId()
    {
        return id;
    }

    /**
     * Setter method for id, required by Hibernate
     * 
     * @param id
     */
    @SuppressWarnings("unused")
    private void setId(Long id)
    {
        this.id = id;
    }

    /**
     * Setter method for id, required by Hibernate
     * 
     * @param stateString
     */
    @SuppressWarnings("unused")
    private void setState(String stateString)
    {
        this.state = stateManager.getState(stateString);
    }

    /**
     * Accessor for execution timestamp
     * 
     * @return executionTimestamp formatted
     */
    public String getExecutionTimestamp()
    {
        return executionTimestamp;
    }

    /**
     * Setter method for branchTransactionId, required by Hibernate
     * 
     * @param executionTimestamp
     */
    private void setExecutionTimestamp(String executionTimestamp)
    {
        this.executionTimestamp = executionTimestamp;
    }

    public void setTransactionJournal(TransactionJournal transactionJournal)
    {
        this.transactionJournal = transactionJournal;
    }

    /**
     * Setter for the beanFactory
     * 
     * @param beanFactory
     */
    public void setBeanFactory(BeanFactory beanFactory)
    {
        this.beanFactory = beanFactory;
    }

    /**
     * Accessor for the beanFactory
     * 
     * @return beanFactory
     */
    protected BeanFactory getBeanFactory()
    {
        return beanFactory;
    }

    public void setTransactionalResource(TransactionalResource transactionalResource)
    {
        this.transactionalResource = transactionalResource;
    }

    public XidImpl getXid()
    {
        return xid;
    }

    /**
     * Called by Hibernate
     * 
     * @param xid
     */
    public void setXid(XidImpl xid)
    {
        this.xid = xid;
        xid.addCommand(this);
    }
}
