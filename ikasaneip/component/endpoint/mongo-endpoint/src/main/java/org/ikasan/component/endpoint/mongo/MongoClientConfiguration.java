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
package org.ikasan.component.endpoint.mongo;

import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mongo configuration options passed at runtime.
 * For full details see Mongo Client Driver http://api.mongodb.org/java/2.12/
 *
 * @author Ikasan Development Team
 */
public class MongoClientConfiguration
{
    /** Logger instance */
    private static Logger logger = LoggerFactory.getLogger(MongoClientConfiguration.class);

    /** Mongo Driver Properties */
    protected List<String> connectionUrls = new ArrayList<String>();

    /** do we authenticate the connection */
    protected Boolean authenticated = Boolean.FALSE;

    /** authentication principal */
    protected String username;

    /** authentication credential */
    protected String password;

    /** database name */
    protected String databaseName;

    /** collection key name and actual name */
    protected Map<String,String> collectionNames = new HashMap<String,String>();

    /** represents preferred replica set members to which a query or command can be sent */
    protected ReadPreference readPreference = ReadPreference.primary();

     /** Controls the acknowledgment of write operations */
    protected WriteConcern writeConcern = WriteConcern.ACKNOWLEDGED;

    /** Sets the localThreshold - overrides default driver options if specified */
    protected Integer localThreshold;

    /** Sets whether JMX beans registered by the driver should always be MBeans, regardless of whether the VM is Java 6 or greater - overrides default driver options if specified */
    protected Boolean alwaysUseMBeans;

    /** Sets the maximum number of connections per host - overrides default driver options if specified */
    protected Integer connectionsPerHost;

    /** Sets the connection timeout - overrides default driver options if specified */
    protected Integer connectionTimeout;

    /** Sets whether cursor finalizers are enabled. - overrides default driver options if specified */
    protected Boolean cursorFinalizerEnabled = Boolean.TRUE; // Need to specify this our a NPE will result in driver 3.0.0 (https://jira.mongodb.org/browse/JAVA-1798)

    /** Sets the description - overrides default driver options if specified */
    protected String description;

    /** Sets the min heart beat frequency - overrides default driver options if specified */
    protected Integer minHeartbeatFrequency;

    /** Sets the heartbeat connect timeout - overrides default driver options if specified */
    protected Integer heartbeatConnectTimeout;

    /** Sets the heartbeat frequency - overrides default driver options if specified */
    protected Integer heartbeatFrequency;

    /** Sets the heartbeat connect socket timeout - overrides default driver options if specified */
    protected Integer heartbeatSocketTimeout;

    /** Sets defaults to be what they are in MongoOptions - overrides default driver options if specified */
    protected Boolean legacyDefaults = Boolean.FALSE;

    /** Sets the maximum idle time for a pooled connection - overrides default driver options if specified */
    protected Integer maxConnectionIdleTime;

    /** Sets the maximum life time for a pooled connection - overrides default driver options if specified */
    protected Integer maxConnectionLifeTime;

    /** Sets the maximum time that a thread will block waiting for a connection - overrides default driver options if specified */
    protected Integer maxWaitTime;

    /** Sets the minimum number of connections per host - overrides default driver options if specified */
    protected Integer minConnectionsPerHost;

    /** Sets the required replica set name for the cluster - overrides default driver options if specified */
    protected String requiredReplicaSetName;

    /** Sets whether socket keep alive is enabled - overrides default driver options if specified */
    protected Boolean socketKeepAlive;

    /** Sets the socket timeout - overrides default driver options if specified */
    protected Integer socketTimeout;

    /** Sets the multiplier for number of threads allowed to block waiting for a connection - overrides default driver options if specified */
    protected Integer threadsAllowedToBlockForConnectionMultiplier;

    
    public List<String> getConnectionUrls() {
        return connectionUrls;
    }

    public void setConnectionUrls(List<String> connectionUrls) {
        this.connectionUrls = connectionUrls;
    }

    public Boolean isAuthenticated() {
        return authenticated;
    }

    public Boolean getAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(Boolean authenticated) {
        this.authenticated = authenticated;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public Map<String,String> getCollectionNames() {
        return collectionNames;
    }

    public void setCollectionNames(Map<String,String> collectionNames) {
        this.collectionNames = collectionNames;
    }

    public ReadPreference getReadPreference() {
        return readPreference;
    }

    public void setReadPreference(ReadPreference readPreference) {
        this.readPreference = readPreference;
    }

    public WriteConcern getWriteConcern() {
        return writeConcern;
    }

    public void setWriteConcern(WriteConcern writeConcern) {
        this.writeConcern = writeConcern;
    }

    public Integer getLocalThreshold() {
        return localThreshold;
    }

    public void setLocalThreshold(Integer localThreshold) {
        this.localThreshold = localThreshold;
    }

    public Boolean getAlwaysUseMBeans() {
        return alwaysUseMBeans;
    }

    public void setAlwaysUseMBeans(Boolean alwaysUseMBeans) {
        this.alwaysUseMBeans = alwaysUseMBeans;
    }

    public Integer getConnectionsPerHost() {
        return connectionsPerHost;
    }

    public void setConnectionsPerHost(Integer connectionsPerHost) {
        this.connectionsPerHost = connectionsPerHost;
    }

    public Integer getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(Integer connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public Boolean getCursorFinalizerEnabled() {
        return cursorFinalizerEnabled;
    }

    public void setCursorFinalizerEnabled(Boolean cursorFinalizerEnabled) {
        this.cursorFinalizerEnabled = cursorFinalizerEnabled;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getMinHeartbeatFrequency() {
        return minHeartbeatFrequency;
    }

    public void setMinHeartbeatFrequency(Integer minHeartbeatFrequency) {
        this.minHeartbeatFrequency = minHeartbeatFrequency;
    }

    public Integer getHeartbeatConnectTimeout() {
        return heartbeatConnectTimeout;
    }

    public void setHeartbeatConnectTimeout(Integer heartbeatConnectTimeout) {
        this.heartbeatConnectTimeout = heartbeatConnectTimeout;
    }

    public Integer getHeartbeatFrequency() {
        return heartbeatFrequency;
    }

    public void setHeartbeatFrequency(Integer heartbeatFrequency) {
        this.heartbeatFrequency = heartbeatFrequency;
    }

    public Integer getHeartbeatSocketTimeout() {
        return heartbeatSocketTimeout;
    }

    public void setHeartbeatSocketTimeout(Integer heartbeatSocketTimeout) {
        this.heartbeatSocketTimeout = heartbeatSocketTimeout;
    }

    public Boolean getLegacyDefaults() {
        return legacyDefaults;
    }

    public void setLegacyDefaults(Boolean legacyDefaults) {
        this.legacyDefaults = legacyDefaults;
    }

    public Integer getMaxConnectionIdleTime() {
        return maxConnectionIdleTime;
    }

    public void setMaxConnectionIdleTime(Integer maxConnectionIdleTime) {
        this.maxConnectionIdleTime = maxConnectionIdleTime;
    }

    public Integer getMaxConnectionLifeTime() {
        return maxConnectionLifeTime;
    }

    public void setMaxConnectionLifeTime(Integer maxConnectionLifeTime) {
        this.maxConnectionLifeTime = maxConnectionLifeTime;
    }

    public Integer getMaxWaitTime() {
        return maxWaitTime;
    }

    public void setMaxWaitTime(Integer maxWaitTime) {
        this.maxWaitTime = maxWaitTime;
    }

    public Integer getMinConnectionsPerHost() {
        return minConnectionsPerHost;
    }

    public void setMinConnectionsPerHost(Integer minConnectionsPerHost) {
        this.minConnectionsPerHost = minConnectionsPerHost;
    }

    public String getRequiredReplicaSetName() {
        return requiredReplicaSetName;
    }

    public void setRequiredReplicaSetName(String requiredReplicaSetName) {
        this.requiredReplicaSetName = requiredReplicaSetName;
    }

    public Boolean getSocketKeepAlive() {
        return socketKeepAlive;
    }

    public void setSocketKeepAlive(Boolean socketKeepAlive) {
        this.socketKeepAlive = socketKeepAlive;
    }

    public Integer getSocketTimeout() {
        return socketTimeout;
    }

    public void setSocketTimeout(Integer socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public Integer getThreadsAllowedToBlockForConnectionMultiplier() {
        return threadsAllowedToBlockForConnectionMultiplier;
    }

    public void setThreadsAllowedToBlockForConnectionMultiplier(Integer threadsAllowedToBlockForConnectionMultiplier) {
        this.threadsAllowedToBlockForConnectionMultiplier = threadsAllowedToBlockForConnectionMultiplier;
    }

    /**
     * Utility method for translating the list of connectionUrls (host:port) to host and port
     * into ServerAddress(es)
     * @return
     */
    protected List<ServerAddress> getServerAddresses()
    {
        List<ServerAddress> serverAddresses = new ArrayList<ServerAddress>();

        for(String connectionUrl:connectionUrls)
        {
            String[] properties = connectionUrl.split(":");
            try
            {
                serverAddresses.add( new ServerAddress(properties[0], Integer.valueOf(properties[1])) );
            }
            catch(NumberFormatException | NullPointerException e)
            {
                logger.warn("connectionUrl is not valid [" + connectionUrl + "]");
            }
        }

        return serverAddresses;
    }

    /**
     * Basic validation of the configuration.
     */
    public void validate()
    {
        boolean atLeastOneHost = false;
        for(String connectionUrl:connectionUrls)
        {
            // we should have at least one valid hostname
            if(connectionUrl != null && connectionUrl.length() > 0)
            {
                atLeastOneHost = true;
                break;
            }
        }

        if(!atLeastOneHost)
        {
            throw new RuntimeException("At least one connectionUrl must be specified.");
        }

        if(databaseName == null)
        {
            throw new RuntimeException("DatabaseName cannot be 'null'");
        }
    }

    @Override
    public String toString()
    {
        return "MongoClientConfiguration [connectionUrls=" + connectionUrls + ", authenticated=" + authenticated
                + ", username=" + username + ", password=" + password + ", databaseName=" + databaseName
                + ", collectionNames=" + collectionNames + ", readPreference=" + readPreference + ", writeConcern="
                + writeConcern + ", localThreshold=" + localThreshold + ", alwaysUseMBeans=" + alwaysUseMBeans
                + ", connectionsPerHost=" + connectionsPerHost + ", connectionTimeout=" + connectionTimeout
                + ", cursorFinalizerEnabled=" + cursorFinalizerEnabled + ", description=" + description
                + ", minHeartbeatFrequency=" + minHeartbeatFrequency + ", heartbeatConnectTimeout="
                + heartbeatConnectTimeout + ", heartbeatFrequency=" + heartbeatFrequency + ", heartbeatSocketTimeout="
                + heartbeatSocketTimeout + ", legacyDefaults=" + legacyDefaults + ", maxConnectionIdleTime="
                + maxConnectionIdleTime + ", maxConnectionLifeTime=" + maxConnectionLifeTime + ", maxWaitTime="
                + maxWaitTime + ", minConnectionsPerHost=" + minConnectionsPerHost + ", requiredReplicaSetName="
                + requiredReplicaSetName + ", socketKeepAlive=" + socketKeepAlive + ", socketTimeout=" + socketTimeout
                + ", threadsAllowedToBlockForConnectionMultiplier=" + threadsAllowedToBlockForConnectionMultiplier
                + "]";
    }
}
