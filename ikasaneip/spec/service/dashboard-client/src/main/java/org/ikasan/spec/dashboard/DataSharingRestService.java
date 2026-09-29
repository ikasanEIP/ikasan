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
package org.ikasan.spec.dashboard;

import org.ikasan.spec.error.reporting.ErrorOccurrence;
import org.ikasan.spec.exclusion.ExclusionEvent;
import org.ikasan.spec.metadata.model.ConfigurationMetaData;
import org.ikasan.spec.metadata.model.ModuleMetaData;
import org.ikasan.spec.replay.ReplayEvent;
import org.ikasan.spec.search.SearchResults;
import org.ikasan.spec.wiretap.WiretapEvent;

import java.util.List;

/**
 * REST client service interface for ESB data sharing APIs.
 * Provides methods to query wiretap, errors, exclusions, replays, module metadata,
 * and configuration data from the upstream dashboard for data sharing purposes.
 *
 * @author Ikasan Development Team
 */
public interface DataSharingRestService {

    /**
     * Query wiretap data for data sharing
     *
     * @param fromTimestamp start timestamp (milliseconds)
     * @param toTimestamp end timestamp (milliseconds)
     * @param moduleNames optional filter by module names
     * @param limit maximum records to return
     * @param offset pagination offset
     * @return SearchResults containing search results and metadata
     */
    SearchResults<WiretapEvent> queryWiretap(long fromTimestamp, long toTimestamp, List<String> moduleNames, int limit, int offset);

    /**
     * Query error data for data sharing
     *
     * @param fromTimestamp start timestamp (milliseconds)
     * @param toTimestamp end timestamp (milliseconds)
     * @param moduleNames optional filter by module names
     * @param limit maximum records to return
     * @param offset pagination offset
     * @return SearchResults containing search results and metadata
     */
    SearchResults<ErrorOccurrence> queryErrors(long fromTimestamp, long toTimestamp, List<String> moduleNames, int limit, int offset);

    /**
     * Query exclusion data for data sharing
     *
     * @param fromTimestamp start timestamp (milliseconds)
     * @param toTimestamp end timestamp (milliseconds)
     * @param moduleNames optional filter by module names
     * @param limit maximum records to return
     * @param offset pagination offset
     * @return SearchResults containing search results and metadata
     */
    SearchResults<ExclusionEvent> queryExclusions(long fromTimestamp, long toTimestamp, List<String> moduleNames, int limit, int offset);

    /**
     * Query replay data for data sharing
     *
     * @param fromTimestamp start timestamp (milliseconds)
     * @param toTimestamp end timestamp (milliseconds)
     * @param moduleNames optional filter by module names
     * @param limit maximum records to return
     * @param offset pagination offset
     * @return SearchResults containing search results and metadata
     */
    SearchResults<ReplayEvent> queryReplays(long fromTimestamp, long toTimestamp, List<String> moduleNames, int limit, int offset);

    /**
     * Query module metadata for data sharing
     *
     * @param moduleNames filter by module names (required)
     * @param limit maximum records to return
     * @param offset pagination offset
     * @return SearchResults containing search results and metadata
     */
    SearchResults<ModuleMetaData> queryModuleMetadata(List<String> moduleNames, int limit, int offset);

    /**
     * Query configuration data for data sharing
     *
     * @param configurationIdentifiers filter by configuration identifiers (required)
     * @param limit maximum records to return
     * @param offset pagination offset
     * @return SearchResults containing search results and metadata
     */
    SearchResults<ConfigurationMetaData> queryConfiguration(List<String> configurationIdentifiers, int limit, int offset);

    /**
     * Count wiretap data for data sharing
     *
     * @param fromTimestamp start timestamp (milliseconds)
     * @param toTimestamp end timestamp (milliseconds)
     * @param moduleNames optional filter by module names
     * @return count of records
     */
    long countWiretap(long fromTimestamp, long toTimestamp, List<String> moduleNames);

    /**
     * Count error data for data sharing
     *
     * @param fromTimestamp start timestamp (milliseconds)
     * @param toTimestamp end timestamp (milliseconds)
     * @param moduleNames optional filter by module names
     * @return count of records
     */
    long countErrors(long fromTimestamp, long toTimestamp, List<String> moduleNames);

    /**
     * Count exclusion data for data sharing
     *
     * @param fromTimestamp start timestamp (milliseconds)
     * @param toTimestamp end timestamp (milliseconds)
     * @param moduleNames optional filter by module names
     * @return count of records
     */
    long countExclusions(long fromTimestamp, long toTimestamp, List<String> moduleNames);

    /**
     * Count replay data for data sharing
     *
     * @param fromTimestamp start timestamp (milliseconds)
     * @param toTimestamp end timestamp (milliseconds)
     * @param moduleNames optional filter by module names
     * @return count of records
     */
    long countReplays(long fromTimestamp, long toTimestamp, List<String> moduleNames);
}
