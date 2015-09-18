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
import org.ikasan.dashboard.ui.dashboard.chart.DashboardChart;
import org.ikasan.dashboard.ui.framework.cache.GraphCache;
import org.ikasan.dashboard.ui.framework.component.EventExclusionsTable;
import org.ikasan.dashboard.ui.framework.component.FlowStateTable;
import org.vaadin.teemu.VaadinIcons;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Responsive;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
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
    private EventExclusionsTable eventExclusionsTable;
    private DashboardChart dashboardChart;
    private FlowStateTable flowStateTable;
    
    /**
     * Constructor
     * 
     * @param ikasanModuleService
     */
    public LandingViewPanel(EventExclusionsTable eventExclusionsTable, FlowStateTable flowStateTable)
    {
        super();

        this.eventExclusionsTable = eventExclusionsTable;
        if(eventExclusionsTable == null)
		{
			throw new IllegalArgumentException("eventExclusionsTable cannot be null!");
		}
        this.flowStateTable = flowStateTable;
        if(flowStateTable == null)
		{
			throw new IllegalArgumentException("flowStateTable cannot be null!");
		}
        
        init();
    }

    protected void init()
    {       
    	addStyleName(ValoTheme.PANEL_BORDERLESS);
    	
    	VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setWidth("100%");
        verticalLayout.setHeight("100%");
        verticalLayout.setMargin(true);
        verticalLayout.addStyleName("dashboard-view");
        
        Responsive.makeResponsive(verticalLayout);
        
        Component content = buildContent();
        verticalLayout.addComponent(content);
               
        verticalLayout.setExpandRatio(content, 1);
        
        this.setSizeFull();
        this.setContent(verticalLayout);
    }
    
    private Component createContentWrapper(final Component content) 
    {
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
        MenuItem max = tools.addItem("", VaadinIcons.EXPAND, new Command() 
        {

            @Override
            public void menuSelected(final MenuItem selectedItem)
            {
                if (!slot.getStyleName().contains("max")) 
                {
                    selectedItem.setIcon(FontAwesome.COMPRESS);
                    toggleMaximized(slot, true);
                } else 
                {
                    slot.removeStyleName("max");
                    selectedItem.setIcon(FontAwesome.EXPAND);
                    toggleMaximized(slot, false);
                }
            }
        });
        max.setStyleName("icon-only");
        MenuItem root = tools.addItem("", VaadinIcons.COG, null);
        root.addItem("Configure", new Command() 
        {
            @Override
            public void menuSelected(final MenuItem selectedItem) 
            {
                Notification.show("Not implemented in this demo");
            }
        });
        root.addSeparator();
        root.addItem("Close", new Command() 
        {
            @Override
            public void menuSelected(final MenuItem selectedItem) 
            {
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

        dashboardPanels.addComponent(buildDashboard(this.eventExclusionsTable));
//        dashboardPanels.addComponent(buildDashboardChart());
//        dashboardPanels.addComponent(buildAnotherChart());
        dashboardPanels.addComponent(buildDashboard(this.flowStateTable));

        return dashboardPanels;
    }
    
    private Component buildDashboard(Component component) 
    {
        Component contentWrapper = createContentWrapper(component);
        contentWrapper.addStyleName("top10-revenue");
        return contentWrapper;
    }
    
    private Component buildDashboardChart() 
    {
    	this.dashboardChart = new DashboardChart();
    	this.dashboardChart.setHcjs("var options = {        chart: {            type: 'pie'        },        " +
    			"title: {            text: 'Browser market shares. January, 2015 to May, 2015'        },        " +
    			"subtitle: {            text: 'Click the slices to view versions. Source: netmarketshare.com.'        },        " +
    			"plotOptions: {            series: {                dataLabels: {                    enabled: true,                    " +
    			"format: '{point.name}: {point.y:.1f}%'                }            }        },        " +
    			"tooltip: {            headerFormat: '<span style=\"font-size:11px\">{series.name}</span><br>',            " +
    			"pointFormat: '<span style=\"color:{point.color}\">{point.name}</span>: <b>{point.y:.2f}%</b> of total<br/>'        },        " +
    			"series: [{            name: \"Brands\",            colorByPoint: true,            " +
    			"data: [{                name: \"Microsoft Internet Explorer\",                y: 56.33,                " +
    			"drilldown: \"Microsoft Internet Explorer\"            }, " +
    			"{                name: \"Chrome\",                y: 24.03,                drilldown: \"Chrome\"            }," +
    			"{                name: \"Firefox\",                y: 10.38,                drilldown: \"Firefox\"            }, " +
    			"{                name: \"Safari\",                y: 4.77,                drilldown: \"Safari\"            }, " +
    			"{                name: \"Opera\",                y: 0.91,                drilldown: \"Opera\"            }, " +
    			"{                name: \"Proprietary or Undetectable\",                y: 0.2,                drilldown: null            }]        }],        " +
    			"drilldown: {            series: [{                name: \"Microsoft Internet Explorer\",                id: \"Microsoft Internet Explorer\",                data: [                    [\"v11.0\", 24.13],                    [\"v8.0\", 17.2],                    [\"v9.0\", 8.11],                    [\"v10.0\", 5.33],                    [\"v6.0\", 1.06],                    [\"v7.0\", 0.5]                ]            }, " +
    			"{                name: \"Chrome\",                id: \"Chrome\",                data: [                    [\"v40.0\", 5],                    [\"v41.0\", 4.32],                    [\"v42.0\", 3.68],                    [\"v39.0\", 2.96],                    [\"v36.0\", 2.53],                    [\"v43.0\", 1.45],                    [\"v31.0\", 1.24],                    [\"v35.0\", 0.85],                    [\"v38.0\", 0.6],                    [\"v32.0\", 0.55],                    [\"v37.0\", 0.38],                    [\"v33.0\", 0.19],                    [\"v34.0\", 0.14],                    [\"v30.0\", 0.14]                ]            }, " +
    			"{                name: \"Firefox\",                id: \"Firefox\",                data: [                    [\"v35\", 2.76],                    [\"v36\", 2.32],                    [\"v37\", 2.31],                    [\"v34\", 1.27],                    [\"v38\", 1.02],                    [\"v31\", 0.33],                    [\"v33\", 0.22],                    [\"v32\", 0.15]                ]            }, " +
    			"{                name: \"Safari\",                id: \"Safari\",                data: [                    [\"v8.0\", 2.56],                    [\"v7.1\", 0.77],                    [\"v5.1\", 0.42],                    [\"v5.0\", 0.3],                    [\"v6.1\", 0.29],                    [\"v7.0\", 0.26],                    [\"v6.2\", 0.17]                ]            }, " +
    			"{                name: \"Opera\",                id: \"Opera\",                data: [                    [\"v12.x\", 0.34],                    [\"v28\", 0.24],                    [\"v27\", 0.17],                    [\"v29\", 0.16]                ]            }]        }    }");
        
    	
//    	this.dashboardChart.setHcjs("var options = { chart: {            type: 'column'        },        title: {            text: 'Browser market shares. January, 2015 to May, 2015'        }," +
//    	"        subtitle: {            text: 'Click the columns to view versions. Source: <a href=\"http://netmarketshare.com\">netmarketshare.com</a>.'" +
//    	"        },        xAxis: {            type: 'category'        },        yAxis: {            title: {                text: 'Total percent market share'  " +
//    	"          }        },        legend: {            enabled: false        },        plotOptions: {            series: {                borderWidth: 0,    " +
//    	"            dataLabels: {                    enabled: true,                    format: '{point.y:.1f}%'                }            }        },  " +
//    	"      tooltip: {            headerFormat: '<span style=\"font-size:11px\">{series.name}</span><br>',            pointFormat: '<span style=\"color:{point.color}\">{point.name}</span>: <b>{point.y:.2f}%</b> of total<br/>'" +
//    			"        },        series: [{            name: \"Brands\",            colorByPoint: true,            data: [{                name: \"Microsoft Internet Explorer\"," +
//    					"                y: 56.33,                drilldown: \"Microsoft Internet Explorer\"            }, {                name: \"Chrome\",                " +
//    					"y: 24.03,                drilldown: \"Chrome\"            }, {                name: \"Firefox\",                y: 10.38,                drilldown: \"Firefox\"   " +
//    					"         }, {                name: \"Safari\",                y: 4.77,                drilldown: \"Safari\"            }, {   " +
//    					"             name: \"Opera\",                y: 0.91,                drilldown: \"Opera\"            }, {                name: \"Proprietary or Undetectable\",  " +
//    					"              y: 0.2,                drilldown: null            }]        }],        drilldown: {            series: [{                name: \"Microsoft Internet Explorer\",   " +
//    					"             id: \"Microsoft Internet Explorer\",                data: [                    [                        \"v11.0\",   " +
//    					"                     24.13                    ],                    [                        \"v8.0\",                        17.2     " +
//    					"               ],                    [                       \"v9.0\",                        8.11                    ],                    [                        \"v10.0\", " +
//    					"                       5.33                    ],                    [                        \"v6.0\",                        1.06                    ],                    [                        \"v7.0\",      " +
//    					"                  0.5                    ]                ]            }, {                name: \"Chrome\",                id: \"Chrome\",                " +
//    					"data: [                    [                        \"v40.0\",                        5                    ],                    [                        \"v41.0\"," +
//    					"                        4.32                    ],                    [                        \"v42.0\",                        3.68                    ],     " +
//    					"               [                        \"v39.0\",                        2.96                    ],                    [                        \"v36.0\", " +
//    					"                       2.53                    ],                    [                        \"v43.0\",                        1.45                    ],                    [                       " +
//    					" \"v31.0\",                        1.24                    ],                    [                        \"v35.0\",                  " +
//    					"      0.85                    ],                    [                        \"v38.0\",                        0.6                    ],  " +
//    					"                  [                        \"v32.0\",                        0.55                    ],                    [                        \"v37.0\",  " +
//    					"                      0.38                    ],                    [                        \"v33.0\",                        0.19                    ],    " +
//    					"                [                        \"v34.0\",                        0.14                    ],                    [                        \"v30.0\",      " +
//    					"                  0.14                    ]                ]            }, {                name: \"Firefox\",                id: \"Firefox\",                " +
//    					"data: [                    [                        \"v35\",                        2.76                    ],                    [                        \"v36\", " +
//    					"                       2.32                    ],                    [                        \"v37\",                        2.31                    ],  " +
//    					"                  [                        \"v34\",                        1.27                    ],                    [                        \"v38\",  " +
//    					"                      1.02                    ],                    [                        \"v31\",                        0.33                    ],      " +
//    					"              [                        \"v33\",                        0.22                    ],                    [                        \"v32\",   " +
//    					"                     0.15                    ]                ]            }, {                name: \"Safari\",                id: \"Safari\",                data: [                    [                     " +
//    					"   \"v8.0\",                        2.56                    ],                    [                        \"v7.1\",                        0.77                    ],                    [                        \"v5.1\",      " +
//    					"                  0.42                    ],                    [                        \"v5.0\",                        0.3                    ],                    [                        \"v6.1\",                        0.29                    ],  " +
//    					"                  [                        \"v7.0\",                        0.26                    ],                    [                        \"v6.2\",    " +
//    					"                    0.17                    ]                ]            }, {                name: \"Opera\",                id: \"Opera\",                data: [                    [                        \"v12.x\",    " +
//    					"                    0.34                    ],                    [                        \"v28\",                        0.24                    ],                    [                        \"v27\",                        0.17                    ],      " +
//    					"              [                        \"v29\",                        0.16                    ]                ]            }]        }");
    	
    	Component contentWrapper = createContentWrapper(this.dashboardChart);
        contentWrapper.addStyleName("top10-revenue");
        return contentWrapper;
    }
    
    private Component buildAnotherChart()
    {	    
	    DashboardChart anotherChart = new DashboardChart();
	    
	    anotherChart.setHcjs("var options = {        chart: {            type: 'area'        },        title: {            text: 'Historic and Estimated Worldwide Population Growth by Region'        },        subtitle: {            text: 'Source: Wikipedia.org'        },        xAxis: {            categories: ['1750', '1800', '1850', '1900', '1950', '1999', '2050'],            tickmarkPlacement: 'on',            title: {                enabled: false            }        },        yAxis: {            title: {                text: 'Billions'            },            labels: {                formatter: function () {                    return this.value / 1000;                }            }        },        tooltip: {            shared: true,            valueSuffix: ' millions'        },        plotOptions: {            area: {                stacking: 'normal',                lineColor: '#666666',                lineWidth: 1,                marker: {                    lineWidth: 1,                    lineColor: '#666666'                }            }        },        series: [{            name: 'Asia',            data: [502, 635, 809, 947, 1402, 3634, 5268]        }, {            name: 'Africa',            data: [106, 107, 111, 133, 221, 767, 1766]        }, {            name: 'Europe',            data: [163, 203, 276, 408, 547, 729, 628]        }, {            name: 'America',            data: [18, 31, 54, 156, 339, 818, 1201]        }, {            name: 'Oceania',            data: [2, 2, 2, 6, 13, 30, 46]        }]    }");	    
	    Component contentWrapper = createContentWrapper(anotherChart);
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
//    	eventExclusionsTable.populate();
    }
}
