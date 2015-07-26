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
import org.ikasan.dashboard.ui.framework.component.DashboardTable;
import org.vaadin.teemu.VaadinIcons;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Responsive;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 * @author Ikasan Development Team
 *
 */
public class LandingViewPanel extends Panel implements View
{
    private static final long serialVersionUID = 6005593259860222561L;

    private Logger logger = Logger.getLogger(LandingViewPanel.class);

    private CssLayout dashboardPanels;
    
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
//    	GridLayout layout = new GridLayout(1, 1);
//    	layout.setSpacing(true);
//    	layout.setSizeFull();
    	
    	addStyleName(ValoTheme.PANEL_BORDERLESS);
    	
    	VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setWidth("100%");
        verticalLayout.setHeight("100%");
        verticalLayout.setMargin(true);
        verticalLayout.addStyleName("dashboard-view");

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

//        verticalLayout.addComponent(ikasanWelcomeLabel1);
//        verticalLayout.addComponent(ikasanWelcomeLabel2);
//        verticalLayout.addComponent(ikasanWelcomeLabel3);
//        verticalLayout.addComponent(ikasanWelcomeLabel4);
        
        Responsive.makeResponsive(verticalLayout);
        
        Component content = buildContent();
        verticalLayout.addComponent(content);
        
        
        
//        layout.addComponent(verticalLayout);
//        
//        VerticalLayout wrapper = new VerticalLayout();
//        wrapper.setSizeFull();
//        wrapper.addComponent(layout);
       
        verticalLayout.setExpandRatio(content, 1);
        
        this.setSizeFull();
        this.setContent(verticalLayout);
    }
    
    private Component createContentWrapper(final Component content) {
        final CssLayout slot = new CssLayout();
        slot.setWidth("100%");
        slot.addStyleName("dashboard-panel-slot");

        CssLayout card = new CssLayout();
        card.setWidth("100%");
        card.addStyleName(ValoTheme.LAYOUT_CARD);

        HorizontalLayout toolbar = new HorizontalLayout();
        toolbar.addStyleName("dashboard-panel-toolbar");
        toolbar.setWidth("100%");

        Label caption = new Label(content.getCaption());
        caption.addStyleName(ValoTheme.LABEL_H4);
        caption.addStyleName(ValoTheme.LABEL_COLORED);
        caption.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        content.setCaption(null);

        MenuBar tools = new MenuBar();
        tools.addStyleName(ValoTheme.MENUBAR_BORDERLESS);
        MenuItem max = tools.addItem("", VaadinIcons.EXPAND, new Command() {

            @Override
            public void menuSelected(final MenuItem selectedItem) {
                if (!slot.getStyleName().contains("max")) {
                    selectedItem.setIcon(FontAwesome.COMPRESS);
                    toggleMaximized(slot, true);
                } else {
                    slot.removeStyleName("max");
                    selectedItem.setIcon(FontAwesome.EXPAND);
                    toggleMaximized(slot, false);
                }
            }
        });
        max.setStyleName("icon-only");
        MenuItem root = tools.addItem("", VaadinIcons.COG, null);
        root.addItem("Configure", new Command() {
            @Override
            public void menuSelected(final MenuItem selectedItem) {
                Notification.show("Not implemented in this demo");
            }
        });
        root.addSeparator();
        root.addItem("Close", new Command() {
            @Override
            public void menuSelected(final MenuItem selectedItem) {
                Notification.show("Not implemented in this demo");
            }
        });

        toolbar.addComponents(caption, tools);
        toolbar.setExpandRatio(caption, 1);
        toolbar.setComponentAlignment(caption, Alignment.MIDDLE_LEFT);

        card.addComponents(toolbar, content);
        slot.addComponent(card);
        return slot;
    }
    
    
    private Component buildContent() 
    {
        dashboardPanels = new CssLayout();
        dashboardPanels.addStyleName("dashboard-panels");
        Responsive.makeResponsive(dashboardPanels);

        dashboardPanels.addComponent(buildDashboard());
        dashboardPanels.addComponent(buildDashboard());
        dashboardPanels.addComponent(buildDashboard());
        dashboardPanels.addComponent(buildDashboard());

        return dashboardPanels;
    }
    
    private Component buildDashboard() 
    {
        Component contentWrapper = createContentWrapper(new DashboardTable());
        contentWrapper.addStyleName("top10-revenue");
        return contentWrapper;
    }
    
    private void toggleMaximized(final Component panel, final boolean maximized) 
    {
        if (maximized) 
        {
            panel.setVisible(true);
            panel.addStyleName("max");
        } 
        else 
        {
            panel.removeStyleName("max");
        }
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
