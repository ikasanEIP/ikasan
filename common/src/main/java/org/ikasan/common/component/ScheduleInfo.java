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
package org.ikasan.common.component;

import java.io.Serializable;

/**
 * ScheduleInfo class.
 * @deprecated - This has been deprecated TODO replace with?
 * 
 * @author Ikasan Development Team
 */
@Deprecated
public class ScheduleInfo
    implements Serializable
{
    /** Serialize ID */
    private static final long serialVersionUID = 1L;

    /** scheduled by a rollback/retry operation */
    public static final ScheduleInfo UNDEFINED_TASK = 
        new ScheduleInfo(new Integer(0), "Undefined schedule info.");
    
    /** scheduled by a rollback/retry operation */
    public static final ScheduleInfo ROLLBACK_RETRY_TASK =
        new ScheduleInfo(new Integer(1), "Scheduled within a rollback/retry.");
    
    /** scheduled by normal onTimeout operation */
    public static final ScheduleInfo ONTIMEOUT_TASK =
        new ScheduleInfo(new Integer(2), "Scheduled within an onTimeout.");
    
    /** schedule cause code */
    private Integer id;
    
    /** schedule description */
    private String description;
    
    /** count */
    private int count;

    /**
     * Default constructor
     * Creates a new instance of <code>ScheduleInfo</code>.
     * 
     * @param id 
     * @param description 
     */
    public ScheduleInfo(final Integer id, final String description)
    {
        this.id = id;
        this.description = description;
    }
}
