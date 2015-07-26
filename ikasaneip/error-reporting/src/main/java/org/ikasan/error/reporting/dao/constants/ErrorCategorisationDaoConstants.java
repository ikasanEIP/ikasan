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
package org.ikasan.error.reporting.dao.constants;

/**
 * @author Ikasan Development Team
 *
 */
public interface ErrorCategorisationDaoConstants
{
    /** Static strings representing parameter values to set on the query */
    public static final String MODULE_NAMES = "moduleNames";
    public static final String FLOW_NAMES = "flowNames";
    public static final String FLOW_ELEMENT_NAMES = "flowElementNames";
    public static final String START_DATE = "startDate";
    public static final String END_DATE = "endDate";
    public static final String ERROR_CATEGORY = "errorCategory";
    public static final String ACTION = "action";
    public static final String EXCEPTION_CLASS = "exceptionClass";
    
    public static final String ORDER_BY_ASC = "asc";
    public static final String ORDER_BY_DESC = "desc";

    
    public static final String CATEGORISED_ERROR_OCCURRENCE_QUERY = "select eo from ErrorOccurrence eo, ErrorCategorisationLink ec where";
//    +
//    		" where eo.moduleName = ec.moduleName " +
//    		" and eo.flowName = ec.flowName " +
//    		" and eo.flowElementName = ec.flowElementName";
    
    public static final String NARROW_BY_MODULE_NAMES =	" eo.moduleName = ec.moduleName and eo.moduleName in :" + MODULE_NAMES;
    public static final String NARROW_BY_MODULE_NAMES_EMPTY_OR_NULL =	" (eo.moduleName = ec.moduleName  or ec.moduleName = :" + MODULE_NAMES + ")";
    public static final String NARROW_BY_FLOW_NAMES =	" and eo.flowName = ec.flowName and eo.flowName in :" + FLOW_NAMES;
    public static final String NARROW_BY_FLOW_NAMES_EMPTY_OR_NULL =	" and (eo.flowName = ec.flowName or ec.flowName = :" + FLOW_NAMES + ")";
    public static final String NARROW_BY_FLOW_ELEMENT_NAMES =	"  and eo.flowElementName = ec.flowElementName and eo.flowElementName in :" + FLOW_ELEMENT_NAMES;
    public static final String NARROW_BY_FLOW_ELEMENT_NAMES_EMPTY_OR_NULL =	" and (eo.flowElementName = ec.flowElementName or ec.flowElementName = :" + FLOW_ELEMENT_NAMES + ")";
    public static final String NARROW_BY_ACTION =	" and ec.action = :" + ACTION;
    public static final String NARROW_BY_EXCEPTION_CLASS =	" and ec.exceptionClass = :" + EXCEPTION_CLASS;
    public static final String NARROW_BY_ERROR_CATEGORY = " and ec.errorCategorisation.errorCategory = :" + ERROR_CATEGORY;
    public static final String NARROW_BY_START_DATE = " and eo.timestamp >= :" + START_DATE;
    public static final String NARROW_BY_END_DATE = " and eo.timestamp <= :" + END_DATE;
    
    public static final String ORDER_BY = " order by eo.timestamp desc";
}
