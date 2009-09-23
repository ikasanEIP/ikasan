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
package org.ikasan.connector.basefiletransfer.net;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.ikasan.connector.basefiletransfer.persistence.FileFilter;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * The <code>ClientListEntry</code> class holds information reflecting a
 * <code>ListEntry</code> as well as other information which facilitates
 * the filtering, comparison and transportation of a received file.
 *
 * @author Ikasan Development Team 
 */
public class ClientListEntry
{

    // The core fields describing the ClientListEntry. Out of these the
    // following are used for comparing and ordering:
    // uri, dtLastAccessed, dtLastModifies and size.
    /** The client id passed in from the Managed Connection */
    private String clientId;
    /** The URI */
    private URI uri;
    /** Last accessed date */
    private Date dtLastAccessed;
    /** Last modified date */
    private Date dtLastModified;
    /** File size */
    private long size;
    /** boolean flag to indicate if it is a directory */
    private boolean isDirectory;
    /** boolean flag to indicate if it is a link */
    private boolean isLink;
    /** file name */
    String name;
    // Additional information as provided some libraries such as jsch.LsEntry
    // Left here for future use. Note that the String date formats are not the
    // same.
    /** Complete 'ls' entry (e.g. "drwxr-xr-x    6 herodotos herodotos      204 Nov 25 17:36 Sites") */
    private String longFilename;
    /** Last accessed time as long */
    private long Atime;                  
    /** Last modified time as long */
    private long Mtime;                  
    /** Last accessed time as a string formatted by jsch (e.g. "1/14/70 12:27 PM") */
    private String AtimeString;
    /** Last modified time as a string formatted by jsch (e.g. "Sat Nov 25 17:36:21 GMT 2006") */
    private String MtimeString;
    /** Used extensively by jsch */
    private int Flags;
    /** Group id (e.g. "501") */
    private String gid;
    /** User  id (e.g. "501") */
    private String uid;                 
    /** Permissions as an int (e.g. "16877") */
    private int permissions;
    /** Permissions as a String (e.g. "drwxr-xr-x") */
    private String permissionsString;   
    /** Additional info (Has not been encountered yet!) */
    private ArrayList<String> extended;

    /**
     * Default Constructor
     */
    public ClientListEntry()
    {
        // Do Nothing
    }

    /**
     * @return the uri
     */
    public URI getUri()
    {
        return uri;
    }

    /**
     * @param uri the uri to set
     */
    public void setUri(URI uri)
    {
        this.uri = uri;
    }

    /**
     * @return the clientId
     */
    public String getClientId()
    {
        return clientId;
    }

    /**
     * @param clientId the clientId to set
     */
    public void setClientId(String clientId)
    {
        this.clientId = clientId;
    }
    
    /**
     * @return the atime
     */
    public long getAtime()
    {
        return Atime;
    }

    /**
     * @param atime the atime to set
     */
    public void setAtime(long atime)
    {
        Atime = atime;
    }

    /**
     * Set the name
     * @param name
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Get the list entry name
     * @return name
     */
    public String getName()
    {
        return this.name;
    }
    /**
     * @return the atimeString
     */
    public String getAtimeString(){
    
        return AtimeString;
    }

    /**
     * @param atimeString the atimeString to set
     */
    public void setAtimeString(String atimeString)
    {
        AtimeString = atimeString;
    }

    /**
     * @return the dtLastAccessed
     */
    public Date getDtLastAccessed()
    {
        return dtLastAccessed;
    }

    /**
     * @param dtLastAccessed the dtLastAccessed to set
     */
    public void setDtLastAccessed(Date dtLastAccessed)
    {
        this.dtLastAccessed = dtLastAccessed;
    }

    /**
     * @return the dtLastModified
     */
    public Date getDtLastModified()
    {
        return dtLastModified;
    }

    /**
     * @param dtLastModified the dtLastModified to set
     */
    public void setDtLastModified(Date dtLastModified)
    {
        this.dtLastModified = dtLastModified;
    }

    /**
     * @return the extended
     */
    public ArrayList<String> getExtended()
    {
        return extended;
    }

    /**
     * @param extended the extended to set
     */
    public void setExtended(ArrayList<String> extended)
    {
        this.extended = extended;
    }

    /**
     * @return the flags
     */
    public int getFlags()
    {
        return Flags;
    }

    /**
     * @param flags the flags to set
     */
    public void setFlags(int flags)
    {
        Flags = flags;
    }

    /**
     * @return the gid
     */
    public String getGid()
    {
        return gid;
    }

    /**
     * @param gid the gid to set
     */
    public void setGid(String gid)
    {
        this.gid = gid;
    }

    /**
     * @return the isDirectory
     */
    public boolean isDirectory()
    {
        return isDirectory;
    }

    /**
     * @param isDir the isDirectory to set
     */
    public void isDirectory(boolean isDir)
    {
        this.isDirectory = isDir;
    }

    /**
     * @return the isLink
     */
    public boolean isLink()
    {
        return isLink;
    }

    /**
     * @param isLnk the isLink to set
     */
    public void isLink(boolean isLnk)
    {
        this.isLink = isLnk;
    }

    /**
     * @return the longFilename
     */
    public String getLongFilename()
    {
        return longFilename;
    }

    /**
     * @param longFilename the longFilename to set
     */
    public void setLongFilename(String longFilename)
    {
        this.longFilename = longFilename;
    }

    /**
     * @return the mtime
     */
    public long getMtime()
    {
        return Mtime;
    }

    /**
     * @param mtime the mtime to set
     */
    public void setMtime(long mtime)
    {
        Mtime = mtime;
    }

    /**
     * @return the mtimeString
     */
    public String getMtimeString()
    {
        return MtimeString;
    }

    /**
     * @param mtimeString the mtimeString to set
     */
    public void setMtimeString(String mtimeString)
    {
        MtimeString = mtimeString;
    }

    /**
     * @return the permissions
     */
    public int getPermissions()
    {
        return permissions;
    }

    /**
     * @param permissions the permissions to set
     */
    public void setPermissions(int permissions)
    {
        this.permissions = permissions;
    }

    /**
     * @return the permissionsString
     */
    public String getPermissionsString()
    {
        return permissionsString;
    }

    /**
     * @param permissionsString the permissionsString to set
     */
    public void setPermissionsString(String permissionsString)
    {
        this.permissionsString = permissionsString;
    }

    /**
     * @return the size
     */
    public long getSize()
    {
        return size;
    }

    /**
     * @param size the size to set
     */
    public void setSize(long size)
    {
        this.size = size;
    }

    /**
     * @return the uid
     */
    public String getUid()
    {
        return uid;
    }

    /**
     * @param uid the uid to set
     */
    public void setUid(String uid)
    {
        this.uid = uid;
    }

    /**
     * Method used to implement the Comparable interface and compare/order
     * <code>ClientListEntries</code> according to the following natural
     * order:
     * 
     * <ol>
     *   <li>Uri</li>
     *   <li>LastModified</li>
     *   <li>LastAccessed</li>
     *   <li>Size</li>
     * </ol>
     * 
     * @param object The <code>ClientListEntry</code> to compare with this
     * one.
     * 
     * @return <code>0</code> if the objects are identical, <code>-1</code> if
     * the object compared to this one is relatively smaller, <code>1</code> if
     * the object compared to this one is relatively bigger.
     * 
     * @throws ClassCastException If the <code>object</code> parameter is not of
     * type <code>ClientListEntry</code>
     */
    public int compareTo(Object object) throws ClassCastException
    {
        ClientListEntry e = (ClientListEntry)object;
        int dClientId = this.clientId.compareTo(e.getClientId());
        int dUri = this.uri.compareTo(e.getUri());
        int dLastModified = this.dtLastModified.compareTo(e.getDtLastModified());
        int dLastAccessed = this.dtLastAccessed.compareTo(e.getDtLastAccessed());
        int dSize = (new Long(this.size)).compareTo(new Long(e.getSize()));

        if (dClientId == 0 && dUri == 0 && dLastModified == 0 && dLastAccessed == 0 && dSize == 0)
            return 0;

        if (dClientId < 0)
            return -1;
        else if (dClientId > 0)
            return 1;
        else if (dUri < 0)
            return -1;
        else if (dUri > 0)
            return 1;
        else if (dLastModified < 0)
            return -1;
        else if (dLastModified > 0)
            return 1;
        else if (dLastAccessed < 0)
            return -1;
        else if (dLastAccessed > 0)
            return 1;
        else if (dSize < 0)
            return -1;
        else
            return 1;
    }
    
    /**
     * Used to create a <code>FileFilter</code> object which can
     * be used for persisting/filtering the entry.
     * 
     * <p>Note: size is currently downcast from long to int. Maybe change the
     * persist object and table definition to handle this.</p>
     * 
     * NOTE:  Order is important due to the Hibernate mapping
     * 
     * @return An <code>FileFilter</code> object holding this 
     * entry's URI as string, lastModified, lastAccessed and size.
     */
    public FileFilter toPersistObject()
    {
        return new FileFilter(
            this.getClientId(),
            this.getName(),
            this.getDtLastModified(),
            this.getDtLastAccessed(),
            (int)this.getSize()); 
    }

    /**
     * @return A formatted representation of this object.
     */
    @Override
    public String toString()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        StringBuilder sb = new StringBuilder(512);
        sb.append("ClientListEntry:\nClientId               = ["); //$NON-NLS-1$
        sb.append(this.clientId);

        sb.append("]\nURI     = ["); //$NON-NLS-1$
        sb.append(this.uri);
        
        sb.append("]\nLast accessed     = ["); //$NON-NLS-1$
        if (this.dtLastAccessed != null)
        {
            sb.append(sdf.format(this.dtLastAccessed));
        }
        else
        {
            sb.append("null"); //$NON-NLS-1$
        }

        sb.append("]\nLast modified     = ["); //$NON-NLS-1$
        if (this.dtLastModified != null)
        {
            sb.append(sdf.format(this.dtLastModified));
        }
        else
        {
            sb.append("null"); //$NON-NLS-1$
        }

        sb.append("]\nSize (bytes)      = ["); //$NON-NLS-1$
        sb.append(this.size);
        sb.append("]\nisDirectory       = ["); //$NON-NLS-1$
        sb.append(this.isDirectory);
        sb.append("]\nisLink            = ["); //$NON-NLS-1$
        sb.append(this.isLink);

        sb.append("]\nlongFilename      = ["); //$NON-NLS-1$
        if (this.longFilename != null)
        {
            sb.append(this.longFilename.toString());
        }
        else
        {
            sb.append("null"); //$NON-NLS-1$
        }
        
        sb.append("]\nAtime             = ["); //$NON-NLS-1$
        sb.append(this.Atime);
        sb.append("]\nAtimeString       = ["); //$NON-NLS-1$
        sb.append(this.AtimeString);
        sb.append("]\nMtime             = ["); //$NON-NLS-1$
        sb.append(this.Mtime);
        sb.append("]\nMtimeString       = ["); //$NON-NLS-1$
        sb.append(this.MtimeString);
        sb.append("]\nFlags             = ["); //$NON-NLS-1$
        sb.append(this.Flags);
        sb.append("]\ngid               = ["); //$NON-NLS-1$
        sb.append(this.gid);
        sb.append("]\nuid               = ["); //$NON-NLS-1$
        sb.append(this.uid);
        sb.append("]\nPersmissions      = ["); //$NON-NLS-1$
        sb.append(this.permissions);
        sb.append("]\nPermissionsString = ["); //$NON-NLS-1$
        sb.append(this.permissionsString);
        sb.append("]\n"); //$NON-NLS-1$
        
        if (this.extended != null)
        {
            for (String i : this.extended)
            {
                sb.append("Extended          = [" + i + "]\n"); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }

        return sb.toString();
    }

    /**
     * @see java.lang.Object#equals(Object)
     */
    @Override
    public boolean equals(Object object)
    {
        if (!(object instanceof ClientListEntry))
        {
            return false;
        }
        ClientListEntry rhs = (ClientListEntry) object;
        return new EqualsBuilder().appendSuper(super.equals(object)).append(
                this.dtLastModified, rhs.dtLastModified).append(this.uri,
                rhs.uri).append(this.clientId, rhs.clientId).append(
                this.size, rhs.size).append(
                this.dtLastAccessed, rhs.dtLastAccessed).isEquals();
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return new HashCodeBuilder(-2145271435, 1455695833).appendSuper(
            super.hashCode()).append(this.MtimeString).append(this.uid).append(
            this.permissionsString).append(this.longFilename).append(
            this.AtimeString).append(this.isLink).append(this.dtLastModified)
            .append(this.isDirectory).append(this.permissions).append(
                this.Mtime).append(this.uri).append(this.gid).append(
                this.extended).append(this.clientId).append(this.size).append(
                this.Atime).append(this.dtLastAccessed).append(this.Flags)
            .toHashCode();
    }
}
