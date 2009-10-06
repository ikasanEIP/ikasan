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
            fromModuleId1 = ptpf1.getFromModule().getId();
        }
        if (fromModule2 != null)
        {
            fromModuleId2 = ptpf2.getFromModule().getId();
        }
        if (toModule1 != null)
        {
            toModuleId1 = ptpf1.getToModule().getId();
        }
        if (toModule2 != null)
        {
            toModuleId2 = ptpf2.getToModule().getId();
        }

        // TODO remove once debugged, Retrieve the module names
        String fromModuleName1 = null;
        String fromModuleName2 = null;
        String toModuleName1 = null;
        String toModuleName2 = null;
        
        if (fromModule1 != null)
        {
            fromModuleName1 = ptpf1.getFromModule().getName();
        }
        if (fromModule2 != null)
        {
            fromModuleName2 = ptpf2.getFromModule().getName();
        }
        if (toModule1 != null)
        {
            toModuleName1 = ptpf1.getToModule().getName();
        }
        if (toModule2 != null)
        {
            toModuleName2 = ptpf2.getToModule().getName();
        }
        
        logger.debug("First Module is [" + fromModuleName1 + "] --> [" + toModuleName1 + "]");
        logger.debug("Second Module is [" + fromModuleName2 + "] --> [" + toModuleName2 + "]");
        
        // PointToPointFlows are equal if their From and To ids are equal
        if ((fromModuleId1 == fromModuleId2) && (toModuleId1 == toModuleId2))
        {
            logger.warn("Never should reach this case in the comparator!");
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
        
        // TODO Maybe we do reach this case if we're comparing 2 that have no numerical relationship 
        // towards each other!  Then does returning 0 work as it'll be compared against all others in the 
        // Set?
        logger.warn("Never should reach this case in the comparator!  Well actually its OK?");
        return 0;
        
    }

}
