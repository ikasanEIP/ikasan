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
package org.ikasan.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ikasan.filter.configuration.EntityAgeFilterConfiguration;
import org.ikasan.filter.duplicate.model.EntityAgeFilterEntryConverter;
import org.ikasan.filter.duplicate.model.FilterEntry;
import org.ikasan.filter.duplicate.service.EntityAgeFilterService;
import org.ikasan.spec.component.filter.Filter;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.management.ManagedResource;
import org.ikasan.spec.management.ManagedResourceRecoveryManager;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class EntityAgeFilter<T> implements Filter<T>, ConfiguredResource<EntityAgeFilterConfiguration>, ManagedResource
{
    /** Logger for this class */
    private static Logger logger = LoggerFactory.getLogger(EntityAgeFilter.class);

    /** unique identifier for this configured resource */
    private String configuredResourceId;

    /** The {@link FilterConfiguration} generic configuration for a filter */
    private EntityAgeFilterConfiguration filterConfiguration = new EntityAgeFilterConfiguration();

    private EntityAgeFilterService entityAgeFilterService;

    private EntityAgeFilterEntryConverter converter;

    private String clientId;

    /**
     * Constructor
     */
    public EntityAgeFilter(EntityAgeFilterService entityAgeFilterService,
                           String clientId)
    {
        this.entityAgeFilterService = entityAgeFilterService;
        if(this.entityAgeFilterService == null)
        {
            throw  new IllegalArgumentException("entityAgeFilterService cannot be null!");
        }
        this.clientId = clientId;
        if(this.clientId == null || this.clientId.isEmpty())
        {
            throw  new IllegalArgumentException("clientId cannot be null or empty!");
        }
    }

    /*
     * (non-Javadoc)
     * @see org.ikasan.filter.MessageFilter#filter(java.lang.String)
     */
    public T filter(T message)
    {
        FilterEntry entry = this.converter.convert((String)message);
        if(this.entityAgeFilterService.isOlderEntity(entry))
        {
            return null;
        }
        else
        {
            return message;
        }
    }

    @Override
    public String getConfiguredResourceId()
    {
        return this.configuredResourceId;
    }

    @Override
    public void setConfiguredResourceId(String configuredResourceId)
    {
        this.configuredResourceId = configuredResourceId;
    }

    @Override
    public EntityAgeFilterConfiguration getConfiguration()
    {
        return this.filterConfiguration;
    }

    @Override
    public void setConfiguration(EntityAgeFilterConfiguration filterConfiguration)
    {
        if(filterConfiguration == null)
        {
            this.filterConfiguration = new EntityAgeFilterConfiguration();
        }
        else
        {
            this.filterConfiguration = filterConfiguration;
        }
    }

    @Override
    public void startManagedResource()
    {
        this.entityAgeFilterService.initialise(this.clientId);

        this.converter = new EntityAgeFilterEntryConverter(this.filterConfiguration.getEntityIdentifierXpath(),
                this.filterConfiguration.getEntityLastUpdatedXpath(), this.filterConfiguration.getLastUpdatedDatePattern(),
                this.clientId, this.filterConfiguration.getDaysToKeep());
    }

    @Override
    public void stopManagedResource()
    {
        this.entityAgeFilterService.destroy();
    }

    @Override
    public void setManagedResourceRecoveryManager(ManagedResourceRecoveryManager managedResourceRecoveryManager)
    {

    }

    @Override
    public boolean isCriticalOnStartup()
    {
        return true;
    }

    @Override
    public void setCriticalOnStartup(boolean criticalOnStartup)
    {

    }
}
