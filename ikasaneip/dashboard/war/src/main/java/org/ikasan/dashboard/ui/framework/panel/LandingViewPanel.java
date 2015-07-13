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
package org.ikasan.dashboard.ui.framework.panel;

import org.apache.log4j.Logger;
import org.ikasan.dashboard.ui.framework.navigation.VerticalMenu;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

/**
 * @author Ikasan Development Team
 *
 */
public class LandingViewPanel extends Panel implements View
{
    private static final long serialVersionUID = 6005593259860222561L;

    private Logger logger = Logger.getLogger(LandingViewPanel.class);

    /**
     * Constructor
     * 
     * @param ikasanModuleService
     */
    public LandingViewPanel()
    {
        super();

        init();
    }

    protected void init()
    {       
    	GridLayout layout = new GridLayout(1, 1);
    	layout.setSpacing(true);
    	layout.setSizeFull();
    	
    	VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setWidth("100%");
        verticalLayout.setHeight("100%");
        verticalLayout.setMargin(true);

        Label ikasanWelcomeLabel1 = new Label("Welcome to Ikasan!");
        ikasanWelcomeLabel1.setStyleName("xlarge");
        ikasanWelcomeLabel1.setWidth("100%");
        ikasanWelcomeLabel1.setHeight("30px");
        
        Label ikasanWelcomeLabel2 = new Label("Welcome to the console for Ikasan EIP, your gateway to many Ikasan EIP services.");
        ikasanWelcomeLabel2.setStyleName("large");
        ikasanWelcomeLabel2.setWidth("60%");
        ikasanWelcomeLabel2.setHeight("30px");
        
        Label ikasanWelcomeLabel3 = new Label("What does the Ikasan EIP console do?");
        ikasanWelcomeLabel3.setStyleName("xlarge");
        ikasanWelcomeLabel3.setWidth("100%");
        ikasanWelcomeLabel3.setHeight("30px");
        
        Label ikasanWelcomeLabel4 = new Label("This browser based console allows end users and " +
        		"administrators to execute Ikasan EIP services. This includes wiretapped event search " +
        		"and user administration, error management and resubmission." +
        		" It also provides access to the Mapping Configuration Service.");
        ikasanWelcomeLabel4.setStyleName("large");
        ikasanWelcomeLabel4.setWidth("60%");
        ikasanWelcomeLabel4.setHeight("100px");
        
             

        verticalLayout.addComponent(ikasanWelcomeLabel1);
        verticalLayout.addComponent(ikasanWelcomeLabel2);
        verticalLayout.addComponent(ikasanWelcomeLabel3);
        verticalLayout.addComponent(ikasanWelcomeLabel4);
        
        layout.addComponent(verticalLayout);
        
        VerticalLayout wrapper = new VerticalLayout();
        wrapper.setSizeFull();
        wrapper.addComponent(layout);
       
        this.setSizeFull();
        this.setContent(wrapper);
    }
    

    /* (non-Javadoc)
     * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
     */
    @Override
    public void enter(ViewChangeEvent event)
    {
        // TODO Auto-generated method stub
    }
}
