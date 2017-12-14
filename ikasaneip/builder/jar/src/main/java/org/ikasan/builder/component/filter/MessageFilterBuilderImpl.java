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
package org.ikasan.builder.component.filter;

import org.ikasan.filter.DefaultMessageFilter;
import org.ikasan.filter.duplicate.IsDuplicateFilterRule;
import org.ikasan.filter.duplicate.model.DefaultFilterEntry;
import org.ikasan.filter.duplicate.model.FilterEntry;
import org.ikasan.filter.duplicate.model.FilterEntryConverter;
import org.ikasan.filter.duplicate.model.FilterEntryConverterException;
import org.ikasan.filter.duplicate.service.DuplicateFilterService;
import org.ikasan.spec.component.filter.Filter;
import org.ikasan.spec.component.filter.FilterRule;

/**
 * Ikasan provided message filter default implementation.
 *
 * @author Ikasan Development Team
 */
public class MessageFilterBuilderImpl implements MessageFilterBuilder
{
    DuplicateFilterService duplicateFilterService;

    String configuredResourceId;

    Object filterPojoConfiguration;

    FilterEntryConverter filterEntryConverter;

    // default time to live 30 days
    int filterTimeToLive = 30;

    // default object hashing filter entry converter
    FilterEntryConverter objectHashingFilterEntryConverter = new ObjectHashingFilterEntryConverter();

    /**
     * Constructor
     * @param duplicateFilterService
     */
    public MessageFilterBuilderImpl(DuplicateFilterService duplicateFilterService)
    {
        this.duplicateFilterService = duplicateFilterService;
        if(duplicateFilterService == null)
        {
            throw new IllegalArgumentException("duplicateFilterService cannot be 'null'");
        }
    }

    /**
     * Build component.
     * @return
     */
    public Filter build()
    {
        validateBuilderConfiguration();

        FilterRule duplicateFilterRule = new IsDuplicateFilterRule(this.duplicateFilterService, this.filterEntryConverter);
        DefaultMessageFilter filter = new DefaultMessageFilter(duplicateFilterRule);

        // only set the configuration if specified
        if(this.filterPojoConfiguration != null)
        {
            filter.setConfiguration(this.filterPojoConfiguration);
        }

        filter.setConfiguredResourceId(this.configuredResourceId);
        return filter;
    }

    protected void validateBuilderConfiguration()
    {
        if(this.filterEntryConverter == null)
        {
            throw new IllegalArgumentException("filterEntryConverter is a required property for the defaultMessageFilter and cannot be 'null'");
        }
    }

    @Override
    public MessageFilterBuilder setConfiguredResourceId(String configuredResourceId)
    {
        this.configuredResourceId = configuredResourceId;
        return this;
    }

    @Override
    public MessageFilterBuilder setConfiguration(Object filterPojoConfiguration)
    {
        this.filterPojoConfiguration = filterPojoConfiguration;
        return this;
    }

    @Override
    public MessageFilterBuilder setFilterEntryConverter(FilterEntryConverter filterEntryConverter)
    {
        this.filterEntryConverter = filterEntryConverter;
        return this;
    }

    @Override
    /**
     * Filter entry time to live in days
     * @param timeToLive
     */
    public MessageFilterBuilder setFilterEntryTimeToLive(int filterTimeToLive)
    {
        this.filterTimeToLive = filterTimeToLive;
        return this;
    }

    @Override
    /**
     * Use the default object hashing filter entry converter.
     */
    public MessageFilterBuilder setObjectHashingFilterEntryConverter()
    {
        this.filterEntryConverter = objectHashingFilterEntryConverter;
        return this;
    }

    /**
     * Default implementation of a filter entry converter.
     * It is recommended this be overridden, but is here to provide a convenience.
     *
     * @param <T>
     */
    class ObjectHashingFilterEntryConverter<T> implements FilterEntryConverter<T>
    {
        @Override
        public FilterEntry convert(T message) throws FilterEntryConverterException
        {
            Integer criteria = Integer.valueOf(message.hashCode());
            return new DefaultFilterEntry(criteria, configuredResourceId, filterTimeToLive);
        }
    }
}

