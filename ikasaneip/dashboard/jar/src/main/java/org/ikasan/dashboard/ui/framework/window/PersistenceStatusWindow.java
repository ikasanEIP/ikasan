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
package org.ikasan.dashboard.ui.framework.window;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaadin.aceeditor.AceEditor;
import org.vaadin.aceeditor.AceMode;
import org.vaadin.aceeditor.AceTheme;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Window;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class PersistenceStatusWindow extends Window
{
	private static final long serialVersionUID = -3265893258014286092L;
	
	private Logger logger = LoggerFactory.getLogger(PersistenceStatusWindow.class);
	

	/**
	 * @param configurationManagement
	 */
	public PersistenceStatusWindow(String content)
	{		
		init(content);
	}

	/**
     * Helper method to initialise this object.
     * 
     * @param message
     */
    protected void init(String content)
    {
    	setModal(true);
		setHeight("90%");
		setWidth("90%");
		
		AceEditor errorEditor = new AceEditor();
		errorEditor.setCaption("Persistence Status");
		errorEditor.setValue(content);
		errorEditor.setReadOnly(true);
		errorEditor.setWordWrap(true);
		errorEditor.setMode(AceMode.text);
		errorEditor.setTheme(AceTheme.eclipse);
		errorEditor.setWidth("100%");
		errorEditor.setHeight("100%");
		
		HorizontalLayout layout = new HorizontalLayout();
		layout.setSizeFull();
		layout.addComponent(errorEditor);
		
		this.setContent(layout);
    }

    
}