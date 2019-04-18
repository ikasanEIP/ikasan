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

 import com.vaadin.navigator.View;
 import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
 import com.vaadin.ui.Alignment;
 import com.vaadin.ui.Image;
 import com.vaadin.ui.Panel;
 import com.vaadin.ui.VerticalLayout;
 import com.vaadin.ui.themes.ValoTheme;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;

/**
 * @author Ikasan Development Team
 *
 */
public class LandingViewPanel extends Panel implements View
{
    private static final long serialVersionUID = 6005593259860222561L;

    private Logger logger = LoggerFactory.getLogger(LandingViewPanel.class);

    private VerticalLayout chartLayout = new VerticalLayout();


    private Image bannerImage;
    private Image titleImage;
    
    /**
     * Constructor
     *
     */
    public LandingViewPanel(Image titleImage, Image bannerImage)
    {
        super();

        this.bannerImage = bannerImage;
        this.titleImage = titleImage;

        init();
    }

    protected void init()
    {       
    	addStyleName(ValoTheme.PANEL_BORDERLESS);
    	
    	VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setWidth("90%");
        verticalLayout.setHeight("90%");
        verticalLayout.setMargin(true);

        titleImage.setWidth("50%");
        bannerImage.setHeight("100%");

        
        this.chartLayout.setSizeFull();
        this.chartLayout.setSpacing(true);
        this.chartLayout.addComponent(this.titleImage);
        this.chartLayout.setExpandRatio(this.titleImage, 20f);
        this.chartLayout.setComponentAlignment(this.titleImage, Alignment.MIDDLE_CENTER);
        this.chartLayout.addComponent(this.bannerImage);
        this.chartLayout.setExpandRatio(this.bannerImage, 80f);
        this.chartLayout.setComponentAlignment(this.bannerImage, Alignment.MIDDLE_CENTER);

        
        verticalLayout.addComponent(this.chartLayout);
        verticalLayout.setComponentAlignment(this.chartLayout, Alignment.MIDDLE_CENTER);
        
        this.setSizeFull();
        this.setContent(verticalLayout);
    }
    
    
    

    /* (non-Javadoc)
     * @see com.vaadin.navigator.View#enter(com.vaadin.navigator.ViewChangeListener.ViewChangeEvent)
     */
    @Override
    public void enter(ViewChangeEvent event)
    {   

    }
    

}
