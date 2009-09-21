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
package org.ikasan.connector.basefiletransfer.outbound.command;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.resource.ResourceException;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;
import org.ikasan.connector.base.command.ExecutionContext;
import org.ikasan.connector.base.command.ExecutionOutput;
import org.ikasan.connector.basefiletransfer.net.ClientDirectoryFilter;
import org.ikasan.connector.basefiletransfer.net.ClientFilenameFilter;
import org.ikasan.connector.basefiletransfer.net.ClientListEntry;
import org.ikasan.connector.basefiletransfer.net.ClientPolarisedFilter;
import org.ikasan.connector.basefiletransfer.net.ClientSymLinkFilter;
import org.ikasan.connector.basefiletransfer.outbound.persistence.BaseFileTransferDao;

/**
 * Discovers new files from a remote browsable directory
 * 
 * @author Ikasan Development Team
 */
public class FileDiscoveryCommand extends AbstractBaseFileTransferTransactionalResourceCommand
{
    /** The logger instance. */
    private static Logger logger = Logger.getLogger(FileDiscoveryCommand.class);

    /** source directory */
    private String sourceDirectory;

    /** dao class for base file transfer classes */
    private BaseFileTransferDao persistence;

    /** regexp for pattern matching filenames */
    private String filenamePattern;

    /** min age for matched files in seconds */
    private long minAge;

    /** Whether we filter duplicates */
    private boolean filterDuplicates;
    
    /** Whether we use File name in the duplicates filter */
    private boolean filterOnFilename;

    /** Whether we use Last Modified date as a duplicates filter */
    private boolean filterOnLastModifiedDate;

    /** No args constructor as required by Hibernate */
    public FileDiscoveryCommand()
    {
        // Empty Constructor
    }

    /**
     * Constructor
     * 
     * @param sourceDirectory The directory we're picking up the file from
     * @param filenamePattern The pattern to search for
     * @param persistence The DAO persistence class
     * @param minAge The minimum age of the file
     * @param filterDuplicates Whether we filter duplicates
     * @param filterOnFilename Whether we filter on file name
     * @param filterOnLastModifiedDate Whether we filter on the last modified date
     */
    public FileDiscoveryCommand(String sourceDirectory, String filenamePattern, BaseFileTransferDao persistence,
            long minAge, boolean filterDuplicates, boolean filterOnFilename, boolean filterOnLastModifiedDate)
    {
        super();
        this.sourceDirectory = sourceDirectory;
        this.persistence = persistence;
        this.filenamePattern = filenamePattern;
        this.minAge = minAge;
        this.filterDuplicates = filterDuplicates;
        this.filterOnFilename = filterOnFilename;
        this.filterOnLastModifiedDate = filterOnLastModifiedDate;
    }

    @Override
    protected void doCommit()
    {
        logger.debug("In commit for :" + getClass().getName()); //$NON-NLS-1$
    }

    @Override
    protected ExecutionOutput performExecute() throws ResourceException
    {
        logger.debug("execute called on command: [" + getClass().getName() + "]"); //$NON-NLS-1$ //$NON-NLS-2$
        List<ClientListEntry> list = getList();
        logger.debug("got resulting list:" + list); //$NON-NLS-1$

        return new ExecutionOutput(list);
    }

    @Override
    protected void doRollback()
    {
        logger.debug("In rollback for get()"); //$NON-NLS-1$
    }

    /**
     * retrieves a list of new files
     * 
     * @return List<ClientListEntry>
     * @throws ResourceException Exception thrown by Connector
     */
    private List<ClientListEntry> getList() throws ResourceException
    {

        Date now = new Date();
        List<ClientListEntry> result = new ArrayList<ClientListEntry>();
        // Get the client Connection
        getClient().ensureConnection();
        // CD to the src dir
        changeDirectory(sourceDirectory);
        // Getting a complete listing of current directory (sourceDirectory)
        List<ClientListEntry> allFiles = listDirectory(sourceDirectory);
        logFileList(allFiles, "Unfiltered file list"); //$NON-NLS-1$

        // Filter the files
        logger.debug("Filtering entries based on default filter list..."); //$NON-NLS-1$
        List<ClientListEntry> filteredFiles = filterDefaults(allFiles);

        // Process filtered files
        if (filteredFiles.size() > 0)
        {
            String clientId;
            for (ClientListEntry entry : filteredFiles)
            {
                clientId = (String) this.executionContext.get(ExecutionContext.CLIENT_ID);
                entry.setClientId(clientId);

                // skip this entry if its last accessed date indicates that it
                // is younger that the
                // configured minimum age
                Date lastModifiedDate = entry.getDtLastModified();
                long ageInMillis = (now.getTime()) - (lastModifiedDate.getTime());
                long ageInSec = ageInMillis / 1000;
                logger.debug("file [" + entry.getLongFilename() + "] ageInSec [" + ageInSec + "] vs minAge [" + minAge //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        + "]"); //$NON-NLS-1$

                if (ageInSec < minAge)
                {
                    // vetoing File because it is not old enough
                    logger.debug("vetoing file because it is not old enough [" + entry + "]"); //$NON-NLS-1$//$NON-NLS-2$

                    continue;
                }

                // Filter out duplicates based on parameters passed in earlier
                if (filterDuplicates && filterOptionsSet())
                {
                    if (!persistence.isDuplicate(entry, filterOnFilename, filterOnLastModifiedDate))
                    {
                        result.add(entry);
                    }
                }
                else
                {
                    result.add(entry);
                }
            }
        }
        else
        {
            logger.debug("No files to get, nothing matched the filename pattern."); //$NON-NLS-1$
        }
        return result;
    }

    /**
     * Applies the default filtering rules in the following order:
     * 
     * <ol>
     * <li>Exclude directories</li>
     * <li>Exclude symbolic links</li>
     * <li>Exclude the files that do not match the provided pattern</li>
     * </ol>
     * 
     * <p>
     * Note: Any filtering/comparisons relating to time (i.e. minimumAgeLimit)
     * should be done against database and not List entries.
     * </p>
     * 
     * @param list The <code>List&lt;ClientListEntry&gt;</code> objects to
     *            filter.
     * @return The filtered list containing only the files that have passed
     *         through the filtering process.
     */
    private List<ClientListEntry> filterDefaults(List<ClientListEntry> list)
    {
        ClientPolarisedFilter pf = null;
        List<ClientListEntry> noDirsList = null;
        List<ClientListEntry> noLinksDirsList = null;
        List<ClientListEntry> fileList = null;
        // Ignoring directories
        if (list != null && list.size() > 0)
        {
            pf = new ClientPolarisedFilter(new ClientDirectoryFilter(), false);
            noDirsList = pf.applyFilter(list);
        }
        // Ignoring links
        if (noDirsList != null && noDirsList.size() > 0)
        {
            pf = new ClientPolarisedFilter(new ClientSymLinkFilter(), false);
            noLinksDirsList = pf.applyFilter(noDirsList);
        }

        // Ignoring filenames that don't match
        if (noLinksDirsList != null && noLinksDirsList.size() > 0)
        {
            pf = new ClientPolarisedFilter(new ClientFilenameFilter(filenamePattern), true);
            fileList = pf.applyFilter(noLinksDirsList);
        }

        if (fileList != null && fileList.size() > 0)
        {
            return fileList;
        }
        return new ArrayList<ClientListEntry>(0);
    }

    /**
     * Return whether at least one filtering option is set
     * 
     * @return true if at least one filtering option is set 
     */
    private boolean filterOptionsSet()
    {
        if (filterOnFilename || filterOnLastModifiedDate)
        {
            return true;
        }
        logger.warn("No filtering options were set, so skipping filter after all.");
        return false;
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return new ToStringBuilder(this).append("filenamePattern", this.filenamePattern) //$NON-NLS-1$
            .append("client", getClient()) //$NON-NLS-1$
            .append("sourceDirectory", this.sourceDirectory) //$NON-NLS-1$
            .append("fileSeparator", this.fileSeparator) //$NON-NLS-1$
            .append("persistence", this.persistence) //$NON-NLS-1$
            .append("state", this.getState()) //$NON-NLS-1$
            .toString();
    }

}
