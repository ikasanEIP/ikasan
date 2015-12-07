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
package org.ikasan.dashboard.ui.mappingconfiguration.component;

import org.tepi.filtertable.FilterTable;

import com.vaadin.ui.CustomTable;
import com.vaadin.ui.Grid.CellReference;
import com.vaadin.ui.Table;
import com.vaadin.ui.Grid;

/**
 * @author Ikasan Development Team
 *
 */
public class IkasanSmallCellStyleGenerator implements FilterTable.CellStyleGenerator, Table.CellStyleGenerator, Grid.CellStyleGenerator
{

    private static final long serialVersionUID = -7878494388136309726L;

    /* (non-Javadoc)
     * @see com.vaadin.ui.Table.CellStyleGenerator#getStyle(com.vaadin.ui.Table, java.lang.Object, java.lang.Object)
     */
    @Override
    public String getStyle(Table source, Object itemId, Object propertyId)
    {
        return "ikasan-small";
    }

	/* (non-Javadoc)
	 * @see com.vaadin.ui.CustomTable.CellStyleGenerator#getStyle(com.vaadin.ui.CustomTable, java.lang.Object, java.lang.Object)
	 */
	@Override
	public String getStyle(CustomTable source, Object itemId, Object propertyId)
	{
		return "ikasan-small";
	}

	/* (non-Javadoc)
	 * @see com.vaadin.ui.Grid.CellStyleGenerator#getStyle(com.vaadin.ui.Grid.CellReference)
	 */
	@Override
	public String getStyle(CellReference cellReference)
	{
		return "ikasan-small";
	}
}
