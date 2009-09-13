/*
 * $Id$
 * $URL$
 * 
 * =============================================================================
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
 * =============================================================================
 */
package org.ikasan.common;

import java.util.Calendar;
import java.util.Hashtable;
import java.util.Map;

import javax.naming.NameAlreadyBoundException;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.ikasan.common.component.ComponentState;
import org.ikasan.common.component.RapSheetEntry;
import org.ikasan.common.component.Status;
import org.ikasan.common.util.StringMasker;

import org.ikasan.common.component.ScheduleInfo;

/**
 * Administrator class.
 * 
 * @author Ikasan Development Team
 */
public class Administrator
{
    /** Serialise ID */
    private static final long serialVersionUID = 1L;

    /** The logger instance */
    private static Logger logger = Logger.getLogger(Administrator.class);

    /** Administrator context */
    private CommonContext context;

    /** Group name of the administered component */
    private String groupName;

    /** Name of the administered component */
    private String componentName;

    /** URL of the status instance for this component */
    private String componentStatusURL;

    /** Last offence handled by this bean */
    private RapSheetEntry lastRapSheetEntry;

    /** History of the rapSheet */
    private Hashtable<String, RapSheetEntry> rapSheetEntries = new Hashtable<String, RapSheetEntry>();

    /** ScheduleInfo history */
    private Hashtable<ScheduleInfo, Integer> scheduleInfos = new Hashtable<ScheduleInfo, Integer>();

    /** Default constructor Creates a new instance of <code>Administrator</code>. */
    public Administrator()
    {
        this.context = ResourceLoader.getInstance().newContext();
    }

    /**
     * Setter for groupName
     * 
     * @param groupName The name of the group
     */
    public void setGroupName(String groupName)
    {
        this.groupName = groupName;
    }

    /**
     * Getter for groupName
     * 
     * @return groupName
     */
    public String getGroupName()
    {
        return this.groupName;
    }

    /**
     * Setter for componentName
     * 
     * @param componentName The component name to set
     */
    public void setComponentName(String componentName)
    {
        this.componentName = componentName;
    }

    /**
     * Getter for componentName
     * 
     * @return componentName
     */
    public String getComponentName()
    {
        return this.componentName;
    }

    /**
     * Setter for componentStatusURL
     * 
     * @param componentStatusURL The component status URL to set
     */
    public void setComponentStatusURL(String componentStatusURL)
    {
        this.componentStatusURL = componentStatusURL;
    }

    /**
     * Getter for componentStatusURL
     * 
     * @return componentStatusURL
     */
    public String getComponentStatusURL()
    {
        return this.componentStatusURL;
    }

    /**
     * Set the componentStatus of this bean.
     * 
     * @param componentState The component state to set
     * @return Status as set in the context
     */
    public Status setComponentState(ComponentState componentState)
    {
        Status status = new Status(componentState, Calendar.getInstance().getTimeInMillis());
        try
        {
            if (this.componentStatusURL != null)
            {
                this.context.bind(this.componentStatusURL, status);
                logger.debug("Setting ComponentStateURL [" + this.componentStatusURL + "] ["
                        + status.getComponentState().getDescription() + "].");
                return status;
            }
        }
        catch (NameAlreadyBoundException e)
        {
            try
            {
                Status currentStatus = this.getComponentState();
                if (!currentStatus.getComponentState().equals(componentState))
                {
                    logger.info("Changing ComponentStateURL [" + this.componentStatusURL + "] ["
                            + status.getComponentState().getDescription() + "].");
                }
                else
                {
                    logger.debug("Setting ComponentStateURL [" + this.componentStatusURL + "] ["
                            + status.getComponentState().getDescription() + "].");
                }
                this.context.rebind(this.componentStatusURL, status);
                return status;
            }
            catch (NamingException e1)
            {
                logger.error("Failed to set componentStatus", e1); //$NON-NLS-1$
            }
        }
        catch (NamingException e)
        {
            // Don't throw any exceptions on failure to set componentStatus,
            // as we do not want to interfere with real exceptions
            logger.error("Failed to set componentStatus", e); //$NON-NLS-1$
        }
        return new Status(ComponentState.UNKNOWN, Calendar.getInstance().getTimeInMillis());
    }

    /**
     * Updates the status of this component only if the incoming status equals the currently set status of this bean. If
     * another method has since updated this status then this method will leave the status as is.
     * 
     * @param previousStatus The previous state
     * @param componentState The new state to update to
     * @return Status as set in the context
     */
    public Status updateComponentState(Status previousStatus, ComponentState componentState)
    {
        Status currentStatus = this.getComponentState();
        if (previousStatus.equals(currentStatus))
        {
            this.setComponentState(componentState);
            return this.getComponentState();
        }
        return currentStatus;
    }

    /**
     * Get the componentStatus of this bean.
     * 
     * @return Status
     */
    public Status getComponentState()
    {
        try
        {
            if (this.componentStatusURL != null)
            {
                Object obj = this.context.lookup(this.componentStatusURL);
                if (obj instanceof Status)
                {
                    return (Status) obj;
                }
                logger.error("ComponentState lookup returned object [" //$NON-NLS-1$
                        + obj.getClass().getName() + "] rather than 'Status' object " //$NON-NLS-1$
                        + "for URL [" + this.componentStatusURL + "]"); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }
        catch (NamingException e)
        {
            logger.error("Failed to get componentStatus for [" //$NON-NLS-1$
                    + this.componentStatusURL + "]", e); //$NON-NLS-1$
        }
        return new Status(ComponentState.UNKNOWN, Calendar.getInstance().getTimeInMillis());
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Group Name [").append(this.groupName).append(']');
        sb.append(" Component Name [").append(this.componentName).append(']');
        sb.append(" Status URL [").append(this.componentStatusURL).append(']');
        return new String(sb);
    }

    /**
     * Setter for the lastRapSheetEntry.
     * 
     * @param rapSheetEntry The rap sheet entry to set
     */
    public void setLastRapSheetEntry(final RapSheetEntry rapSheetEntry)
    {
        this.lastRapSheetEntry = rapSheetEntry;
        logger.debug("Setting lastRapSheetEntry [" + this.lastRapSheetEntry + "]"); //$NON-NLS-1$//$NON-NLS-2$
    }

    /**
     * Getter for the lastRapSheetEntry.
     * 
     * @return rapSheetEntry
     */
    public RapSheetEntry getLastRapSheetEntry()
    {
        logger.debug("Getting lastRapSheetEntry [" + this.lastRapSheetEntry + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        return this.lastRapSheetEntry;
    }

    /**
     * @return the rapSheetEntries
     */
    public Hashtable<String, RapSheetEntry> getRapSheetEntries()
    {
        logger.debug("Getting rapSheetEntries [" + this.rapSheetEntries + "]"); //$NON-NLS-1$//$NON-NLS-2$
        return this.rapSheetEntries;
    }

    /**
     * @param rapSheetEntries the rapSheetEntries to set
     */
    public void setRapSheetEntries(final Hashtable<String, RapSheetEntry> rapSheetEntries)
    {
        this.rapSheetEntries = rapSheetEntries;
        logger.debug("Setting rapSheetEntries [" + this.rapSheetEntries + "]"); //$NON-NLS-1$//$NON-NLS-2$
    }

    /**
     * Update the current rapSheet list and last rapSheetEntry based on the incoming offence<br>
     * TODO rename method to be offence
     * 
     * @param throwable The offence to add
     * @param messageMaskPattern The pattern to apply
     */
    public void addOffense(Throwable throwable, String messageMaskPattern)
    {
        RapSheetEntry rse = new RapSheetEntry(throwable);
        StringMasker stringMasker = createStringMasker(messageMaskPattern);
        String key = this.getRapSheetEntryKey(rse, stringMasker);
        // Existing entry found
        if (this.rapSheetEntries.containsKey(key))
        {
            rse = this.rapSheetEntries.get(key);
            rse.setTotalCount(rse.getTotalCount() + 1);
            rse.setLastOccurrence(Calendar.getInstance().getTimeInMillis());
            if (rse.equals(this.lastRapSheetEntry))
            {
                rse.setConsecutiveCount(rse.getConsecutiveCount() + 1);
            }
        }
        this.rapSheetEntries.put(key, rse);
        this.lastRapSheetEntry = rse;
    }

    /**
     * Create a string mask pattern
     * 
     * @param messageMaskPattern The pattern to use
     * @return a StringMasker for a given mask pattern (regexp) that masks matches with ####
     */
    private StringMasker createStringMasker(String messageMaskPattern)
    {
        StringMasker stringMasker = null;
        if (messageMaskPattern != null)
        {
            stringMasker = new StringMasker(messageMaskPattern, "####");
        }
        return stringMasker;
    }

    /**
     * Check to see if the thrown offence has previously occurred within the given period.
     * 
     * @param throwable The offence to check
     * @param period in milliseconds within which offence must occur to be considered a duplicate
     * @param messageMaskPattern The mask pattern to apply
     * @return boolean
     */
    public boolean isDuplicateOffense(Throwable throwable, long period, String messageMaskPattern)
    {
        RapSheetEntry rse = new RapSheetEntry(throwable);
        String key = this.getRapSheetEntryKey(rse, createStringMasker(messageMaskPattern));
        if (this.rapSheetEntries.containsKey(key))
        // Existing entry found
        {
            rse = this.getRapSheetEntries().get(key);
            long now = java.util.Calendar.getInstance().getTimeInMillis();
            long lastOccurred = rse.getLastOccurrence();
            long elapsed = now - lastOccurred;
            if (elapsed <= period)
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the Rap Sheet Entry key
     * 
     * @param rse The rap sheet entry to get the key from
     * @param stringMasker - may be null, a masker for masking aspects of the throwable message
     * @return RapSheet entry Key
     */
    private String getRapSheetEntryKey(RapSheetEntry rse, StringMasker stringMasker)
    {
        StringBuffer key = new StringBuffer();
        ExceptionType exceptionType = null;
        Throwable offense = rse.getOffense();
        if (offense instanceof CommonException)
        {
            exceptionType = ((CommonException) offense).getExceptionType();
        }
        key.append(offense.getClass().getName());
        String message = offense.getMessage();
        if (stringMasker != null)
        {
            message = stringMasker.mask(message);
        }
        key.append(message);
        if (exceptionType != null)
        {
            key.append(exceptionType.getClass().getName());
            key.append(exceptionType.getName());
        }
        return new String(key);
    }

    /**
     * Update the administrator
     * 
     * @param scheduleInfo The scheduled info
     */
    public void updateAdministrator(final ScheduleInfo scheduleInfo)
    {
        // get this schedule info count
        Integer count = this.scheduleInfos.get(scheduleInfo);
        if (count == null)
        {
            this.scheduleInfos.put(scheduleInfo, new Integer(1));
        }
        else
        {
            int cnt = count.intValue();
            this.scheduleInfos.put(scheduleInfo, new Integer(cnt++));
        }
    }

    /**
     * Get the schedule informations
     * 
     * @return The schedule informations
     */
    public Map<ScheduleInfo, Integer> getScheduleInfos()
    {
        return this.scheduleInfos;
    }
}
