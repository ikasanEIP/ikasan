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
package org.ikasan.console.pointtopointflow;

import java.util.Comparator;

import org.apache.log4j.Logger;
import org.ikasan.console.module.Module;

/**
 * <code>PointToPointFlowComparator</code> is an implementation of
 * {@link Comparator} for <code>PointToPointFlow</code> that compares 
 * two such objects based on their Module children.
 * 
 * Needs to be able to accurately sort:
 * 
 * <pre>
 * FromModule ToModule
 * -------------------
 * 1          2
 * NULL       1
 * 3          NULL
 * 2          3
 * 
 * I'd want it to end up like this:
 * 
 * FromModule ToModule
 * --------------------
 * NULL       1
 * 1          2
 * 2          3
 * 3          NULL
 * </pre>
 * 
 * @author Ikasan Development Team 
 */
public class PointToPointFlowComparator implements Comparator<PointToPointFlow>
{
    /** Magic -1 number, represents the 'NULL' */
    private final static long NULL = -1;

    /** Logger for this class */
    private Logger logger = Logger.getLogger(PointToPointFlowComparator.class);
    
    /**
     * Compare two instances of PointToPointFlow
     * 
     * @param ptpf1 - The first PointToPointFlow to compare 
     * @param ptpf2 - The second PointToPointFlow to compare 
     * @return comparison result
     */
    public int compare(PointToPointFlow ptpf1, PointToPointFlow ptpf2)
    {
    	// Deal with NULL flows
        if (ptpf1 == null && ptpf2 == null)
        {
        	return 0;
        }
    	if (ptpf1 == null)
        {
        	return -1;
        }
        if (ptpf2 == null)
        {
        	return 1;
        }
    	
        // Deal with NULL Profiles (profiles should never be NULL).
    	PointToPointFlowProfile pointToPointFlowProfile1 = ptpf1.getPointToPointFlowProfile();
    	PointToPointFlowProfile pointToPointFlowProfile2 = ptpf2.getPointToPointFlowProfile();
        if (pointToPointFlowProfile1 == null && pointToPointFlowProfile2 == null)
        {
        	logger.error("Profile for PointToPointFlow [" + ptpf1.getId() + "] is NULL");
        	logger.error("Profile for PointToPointFlow [" + ptpf2.getId() + "] is NULL");
        	return 0;
        }
    	if (pointToPointFlowProfile1 == null)
        {
        	logger.error("Profile for PointToPointFlow [" + ptpf1.getId() + "] is NULL");
        	return -1;
        }
        if (pointToPointFlowProfile2 == null)
        {
        	logger.error("Profile for PointToPointFlow [" + ptpf2.getId() + "] is NULL");
        	return 1;        	
        }

        // Retrieve the profile ids
        long pointToPointFlowProfileId1 = pointToPointFlowProfile1.getId();
        long pointToPointFlowProfileId2 = pointToPointFlowProfile2.getId();
        // If the profile ids are different then they're not comparable and get a default of 'equal'
        if (pointToPointFlowProfileId1 != pointToPointFlowProfileId2)
        {
        	 return 0;
        }
    	
    	// Retrieve the modules
        Module fromModule1 = ptpf1.getFromModule();
        Module fromModule2 = ptpf2.getFromModule();
        Module toModule1 = ptpf1.getToModule();
        Module toModule2 = ptpf2.getToModule();
        
        // Retrieve the module ids
        long fromModuleId1 = NULL;
        long fromModuleId2 = NULL;
        long toModuleId1 = NULL;
        long toModuleId2 = NULL;
        if (fromModule1 != null)
        {
            fromModuleId1 = fromModule1.getId();
        }
        if (fromModule2 != null)
        {
            fromModuleId2 = fromModule2.getId();
        }
        if (toModule1 != null)
        {
            toModuleId1 = toModule1.getId();
        }
        if (toModule2 != null)
        {
            toModuleId2 = toModule2.getId();
        }
        
        // PointToPointFlows are equal if their From and To ids are equal
        if ((fromModuleId1 == fromModuleId2) && (toModuleId1 == toModuleId2))
        {
            return 0;
        }

        // PointToPointFlows are considered to be equal if their From ids or their To ids are both NULL
        if ((fromModuleId1 == NULL && fromModuleId2 == NULL) || (toModuleId1 == NULL && toModuleId2 == NULL))
        {
            return 0;
        }

        // PointToPointFlow1 is earlier if its fromModuleId is null
        if ((fromModuleId1 == NULL))
        {
            return -1;
        }

        // PointToPointFlow1 is later if its toModuleId is null
        if ((toModuleId1 == NULL))
        {
            return 1;
        }

        // PointToPointFlow2 is earlier if its fromModuleId is null
        if ((fromModuleId2 == NULL))
        {
            return 1;
        }

        // PointToPointFlow2 is later if its toModuleId is null
        if ((toModuleId2 == NULL))
        {
            return -1;
        }
        
        // PointToPointFlow2 is later if its fromModuleId matches the PointToPointFlow1 toModuleId
        if (fromModuleId2 == toModuleId1)
        {
            return -1;
        }

        // PointToPointFlow1 is later if its fromModuleId matches the PointToPointFlow2 toModuleId        
        if (fromModuleId1 == toModuleId2)
        {
            return 1;
        }

        // Default last case, we can't determine so they're considered equal
        return 0;
    }

}
