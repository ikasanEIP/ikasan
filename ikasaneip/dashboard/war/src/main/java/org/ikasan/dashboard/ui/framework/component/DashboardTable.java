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
package org.ikasan.dashboard.ui.framework.component;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.vaadin.ui.Table;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
public final class DashboardTable extends Table {


    public DashboardTable() 
    {
        setCaption("Event Exclusions");

        addStyleName(ValoTheme.TABLE_BORDERLESS);
        addStyleName(ValoTheme.TABLE_NO_STRIPES);
        addStyleName(ValoTheme.TABLE_NO_VERTICAL_LINES);
        addStyleName(ValoTheme.TABLE_SMALL);
       
        setSortEnabled(false);
        setColumnAlignment("revenue", Align.RIGHT);
        setRowHeaderMode(RowHeaderMode.INDEX);
//        setColumnHeaderMode(ColumnHeaderMode.HIDDEN);
        setSizeFull();


        addContainerProperty("Module Name", String.class,  null);
		addContainerProperty("Flow Name", String.class,  null);
		addContainerProperty("Component Name", String.class,  null);
		addContainerProperty("Timestamp", String.class,  null);
    			

    	for(int i=0; i<10; i++)
    	{
//    		Date date = new Date(errorOccurrence.getTimestamp());
    		SimpleDateFormat format = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
    	    String timestamp = format.format(new Date(System.currentTimeMillis()));
    	    
    	    addItem(new Object[]{"Module Name", "Flow Name"
    				, "Component Name", timestamp}, "errorOccurrence" + i);
    	}

//        setVisibleColumns("title", "revenue");
//        setColumnHeaders("Title", "Revenue");
//        setColumnExpandRatio("title", 2);
//        setColumnExpandRatio("revenue", 1);
//
//        setSortContainerPropertyId("revenue");
//        setSortAscending(false);
    }

}