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

import org.ikasan.dashboard.ui.framework.action.Action;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class IkasanMessageDialog extends Window
{
    private static final long serialVersionUID = 300587169393808040L;
    private Action action;

    /**
     * Constructor
     * 
     * @param dialogName
     * @param message
     * @param action
     */
    public IkasanMessageDialog(String dialogName, String message, Action action)
    {
        super(dialogName);
        this.action = action;
        init(message);
    }

    /**
     * Helper method to initialise this object.
     * 
     * @param message
     */
    protected void init(String message)
    {
    	super.setWidth("50%");
        super.setModal(true);
        super.setResizable(false);
        super.center();
        
        FormLayout layout = new FormLayout();
        layout.setMargin(true);
        layout.setWidth("100%");
        
        Label messageLabel = new Label(message);
        layout.addComponent(messageLabel);
        layout.setComponentAlignment(messageLabel, Alignment.MIDDLE_CENTER);
        
        Button okButton = new Button("OK");
        okButton.setStyleName(ValoTheme.BUTTON_SMALL);

        okButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                action.exectuteAction();
                close();
            }
        });

        Button cancelButton = new Button("Cancel");
        cancelButton.setStyleName(ValoTheme.BUTTON_SMALL);

        cancelButton.addClickListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                action.ignoreAction();
                close();
            }
        });

        HorizontalLayout wrapper = new HorizontalLayout();
        wrapper.setWidth(100, Unit.PERCENTAGE);
        HorizontalLayout hlayout = new HorizontalLayout();
        wrapper.addComponent(hlayout);
        wrapper.setComponentAlignment(hlayout, Alignment.MIDDLE_CENTER);
        hlayout.addComponent(okButton);
        hlayout.addComponent(cancelButton);

        layout.addComponent(wrapper);
        
        super.setContent(layout);
    }
}
