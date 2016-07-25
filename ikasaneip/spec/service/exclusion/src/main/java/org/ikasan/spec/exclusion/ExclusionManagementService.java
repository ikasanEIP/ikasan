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
package org.ikasan.spec.exclusion;

import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * 
 * @author Ikasan Development Team
 *
 */
public interface ExclusionManagementService<ENTITY, IDENTIFIER>
{
	/**
	 * Find all exclusion event entities based on criteria.
	 * 
	 * @param moduleName
	 * @param flowName
	 * @param identifier
	 * @return
	 */
    public ENTITY find(String moduleName, String flowName, IDENTIFIER identifier);

	/**
	 * Count all exclusion event entities based on criteria.
	 *
	 * @param moduleName
	 * @param flowName
	 * @param identifier
	 * @return
	 */
	public long count(List<String> moduleName, List<String> flowName, Date startDate, Date endDate, IDENTIFIER identifier);
    
    /**
     * Find all exclusion event entities based on criteria. Restricted by size
     * 
     * @param moduleName
     * @param flowName
     * @param endDate
     * @param identifier
	 * @param size
     * @return
     */
    public List<ENTITY> find(List<String> moduleName, List<String> flowName, Date startDate, Date endDate, IDENTIFIER identifier, int size);

	/**
	 * Find a list of event entities based on criteria.
	 *
	 * @param moduleName
	 * @param flowName
	 * @param starteDate
	 * @param endDate
	 * @param identifier
	 * @return
	 */
	public List<ENTITY> find(List<String> moduleName, List<String> flowName, Date startDate, Date endDate, IDENTIFIER identifier);


	/**
	 * Find all exclusion event entities.
	 * 
	 * @return
	 */
    public List<ENTITY> findAll();
    
    /**
     * Remove the event
     * @param moduleName
     * @param flowName
     * @param identifier
     * @return
     */
    public void delete(String moduleName, String flowName, IDENTIFIER identifier);
    
    /**
     * Remove the event
     * @param errorUri
     * @return
     */
    public void delete(String errorUri);
    
    /**
    * Find the event based on it's URI
    * @param errorUri
    * @return
    */
   public ENTITY find(String errorUri);
}
