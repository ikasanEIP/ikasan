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
package org.ikasan.deployment.model;

import java.io.Serializable;

/**
 * Data model representing the deployment information around a module
 * in a high availability runtime environment.
 * This module depicts the logical cluster name within which the module is deployed, 
 * the module name and the server name the module is deployed to.
 * The URL provides the fully qualified URL for managing this module over
 * the HTTP RESTful interface.
 * 
 * The Active flag specifies whether this entry is the actively running instance (true)
 * or just a deployed, but not running instance (false).
 * 
 * @author Ikasan Development Team
 */
public class DeploymentInfo implements Serializable
{
    /** default serial id */
    private static final long serialVersionUID = 1L;

    /** name of the logical group within which a module/server would be deployed */
    private String clusterName;
    
    /** name of the module */
    private String moduleName;
    
    /** name of the server within which this module is deployed */
    private String serverName;
    
    /** is this deployment actively running */
    private boolean active;
    
    /** url of accessing the deployment over restful API */
    private String url;
    
    /** entry create date time */
    private long createDateTime;
    
    /** entry update date time */
    private long updateDateTime;

    /**
     * Constructor
     * @param moduleName
     * @param serverName
     */
    public DeploymentInfo(String clusterName, String moduleName, String serverName)
    {
        this.moduleName = moduleName;
        if(moduleName == null)
        {
            throw new IllegalArgumentException("moduleName cannot be 'null'");
        }

        this.clusterName = clusterName;
        if(clusterName == null)
        {
            throw new IllegalArgumentException("clusterName cannot be 'null'");
        }

        this.serverName = serverName;
        if(serverName == null)
        {
            throw new IllegalArgumentException("serverName cannot be 'null'");
        }
        
        this.createDateTime = System.currentTimeMillis();
    }
    
    /**
     * Default constructor required by ORM
     */
    protected DeploymentInfo()
    {
        // nothing to do here
    }
    
    public String getClusterName()
    {
        return clusterName;
    }

    protected void setClusterName(String clusterName)
    {
        this.clusterName = clusterName;
    }

    public String getModuleName()
    {
        return moduleName;
    }

    protected void setModuleName(String moduleName)
    {
        this.moduleName = moduleName;
    }

    public String getServerName()
    {
        return serverName;
    }

    protected void setServerName(String serverName)
    {
        this.serverName = serverName;
    }

    public boolean isActive()
    {
        return active;
    }

    public void setActive(boolean active)
    {
        this.active = active;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public long getCreateDateTime()
    {
        return createDateTime;
    }

    protected void setCreateDateTime(long createDateTime)
    {
        this.createDateTime = createDateTime;
    }

    public long getUpdateDateTime()
    {
        return updateDateTime;
    }

    public void setUpdateDateTime(long updateDateTime)
    {
        this.updateDateTime = updateDateTime;
    }
    
    @Override
    public boolean equals(Object object)
    {
        if(object == this)
        {
            return true;
        }

        if( !(object instanceof DeploymentInfo) )
        {
            return false;
        }

        DeploymentInfo deploymentInfo = (DeploymentInfo)object;
        return (deploymentInfo.clusterName.equals(this.clusterName) && 
                deploymentInfo.moduleName.equals(this.moduleName) && 
                deploymentInfo.serverName.equals(this.serverName)) ;
    }
    
    @Override
    public int hashCode() 
    {
        int result = 17;
        result = 31 * result + clusterName.hashCode();
        result = 31 * result + moduleName.hashCode();
        result = 31 * result + serverName.hashCode();
        return result;
    }

}
