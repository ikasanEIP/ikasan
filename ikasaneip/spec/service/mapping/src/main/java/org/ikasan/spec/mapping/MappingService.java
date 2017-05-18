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
package org.ikasan.spec.mapping;

import java.util.List;


/**
 * MappingService contract.
 * 
 * @author Ikasan Development Team
 */
public interface MappingService<CONFIGURATION>
{
    /**
     * For use on One to One mappings.
     *
     * @param clientName
     * @param configurationType
     * @param sourceSystem
     * @param targetSystem
     * @param sourceSystemValue
     * @return
     */
    public String getTargetConfigurationValue(final String clientName, String configurationType, String sourceSystem, String targetSystem,
                                              String sourceSystemValue);

    /**
     * For use on Many to One mappings who do not have source parameter names.
     *
     * @param clientName
     * @param configurationType
     * @param sourceSystem
     * @param targetSystem
     * @param sourceSystemValues
     * @return
     */
    public String getTargetConfigurationValue(final String clientName, final String configurationType, final String sourceSystem,
                                              final String targetSystem, final List<String> sourceSystemValues);

    /**
     * For use on Many to One mappings who do not have source parameter names and will ignore non parameter matches.
     *
     * @param clientName
     * @param configurationTypeName
     * @param sourceContext
     * @param targetContext
     * @param sourceSystemValues
     * @return
     */
    public String getTargetConfigurationValueWithIgnores(String clientName,
        String configurationTypeName, String sourceContext, String targetContext, List<String> sourceSystemValues);

    /**
     * For use on Many to One mappings who have source parameter names and will ignore non parameter matches.
     *
     * @param clientName
     * @param configurationTypeName
     * @param sourceContext
     * @param targetContext
     * @param sourceSystemValues
     * @return
     */
    public String getTargetConfigurationValueWithIgnoresWithOrdinality(String clientName,
        String configurationTypeName, String sourceContext, String targetContext, List<QueryParameter> sourceSystemValues);

    /**
     * For use on Many to Many mappings that do not have the names defined.
     *
     * @param clientName
     * @param configurationType
     * @param sourceContext
     * @param targetContext
     * @param sourceSystemValues
     * @return
     */
    public List<String> getTargetConfigurationValues(String clientName, String configurationType, String sourceContext, String targetContext, List<String> sourceSystemValues);

    /**
     * For use on Many to Many mappings that do have the names defined.
     *
     * @param clientName
     * @param configurationType
     * @param sourceContext
     * @param targetContext
     * @param sourceSystemValues
     * @return
     */
    public List<NamedResult> getTargetConfigurationValuesWithOrdinality(String clientName, String configurationType, String sourceContext, String targetContext, List<QueryParameter> sourceSystemValues);

    /**
     * Set the configuration on the service.
     *
     * @param configuration
     */
    public void setConfiguration(CONFIGURATION configuration);
}
