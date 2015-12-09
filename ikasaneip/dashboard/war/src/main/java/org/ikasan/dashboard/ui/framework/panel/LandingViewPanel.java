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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.ikasan.dashboard.ui.dashboard.chart.DashboardChart;
import org.ikasan.dashboard.ui.framework.component.EventExclusionsTable;
import org.ikasan.dashboard.ui.framework.component.FlowStateTable;
import org.ikasan.dashboard.ui.framework.util.DashboardSessionValueConstants;
import org.ikasan.dashboard.ui.topology.panel.TopologyViewPanel;
import org.ikasan.dashboard.ui.topology.util.FilterMap;
import org.ikasan.dashboard.ui.topology.util.FilterUtil;
import org.ikasan.error.reporting.model.ErrorOccurrence;
import org.ikasan.error.reporting.model.ErrorOccurrenceNote;
import org.ikasan.error.reporting.model.ModuleErrorCount;
import org.ikasan.error.reporting.model.Note;
import org.ikasan.hospital.model.ExclusionEventAction;
import org.ikasan.hospital.model.ModuleActionedExclusionCount;
import org.ikasan.hospital.service.HospitalManagementService;
import org.ikasan.spec.error.reporting.ErrorReportingManagementService;
import org.ikasan.topology.model.FilterComponent;
import org.ikasan.topology.model.Module;
import org.ikasan.topology.service.TopologyService;

import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.PointClickEvent;
import com.vaadin.addon.charts.PointClickListener;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.Configuration;
import com.vaadin.addon.charts.model.HorizontalAlign;
import com.vaadin.addon.charts.model.Labels;
import com.vaadin.addon.charts.model.LayoutDirection;
import com.vaadin.addon.charts.model.Legend;
import com.vaadin.addon.charts.model.ListSeries;
import com.vaadin.addon.charts.model.PlotOptionsColumn;
import com.vaadin.addon.charts.model.Stacking;
import com.vaadin.addon.charts.model.Tooltip;
import com.vaadin.addon.charts.model.VerticalAlign;
import com.vaadin.addon.charts.model.XAxis;
import com.vaadin.addon.charts.model.YAxis;
import com.vaadin.addon.charts.model.style.SolidColor;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
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
    
    private ErrorReportingManagementService<ErrorOccurrence, Note, ErrorOccurrenceNote, ModuleErrorCount> errorReportingManagementService;
    private TopologyService topologyService;
    private HospitalManagementService<ExclusionEventAction, ModuleActionedExclusionCount> hospitalManagementService;
    
    
    private VerticalLayout chartLayout = new VerticalLayout();
    
    /**
     * Constructor
     * 
     * @param ikasanModuleService
     */
    public LandingViewPanel(EventExclusionsTable eventExclusionsTable, FlowStateTable flowStateTable,
    		ErrorReportingManagementService<ErrorOccurrence, Note, ErrorOccurrenceNote, ModuleErrorCount> errorReportingManagementService,
    		TopologyService topologyService, HospitalManagementService<ExclusionEventAction, ModuleActionedExclusionCount> hospitalManagementService)
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
        this.errorReportingManagementService = errorReportingManagementService;
        if(errorReportingManagementService == null)
		{
			throw new IllegalArgumentException("errorReportingManagementService cannot be null!");
		}
        this.topologyService = topologyService;
        if(topologyService == null)
		{
			throw new IllegalArgumentException("topologyService cannot be null!");
		}
        this.hospitalManagementService = hospitalManagementService;
        if(hospitalManagementService == null)
		{
			throw new IllegalArgumentException("hospitalManagementService cannot be null!");
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
        
        this.chartLayout.setSizeFull();
        
        this.chartLayout.addComponent(buildErrorsChart());
		this.chartLayout.addComponent(buildExclusionsChart());
        
        verticalLayout.addComponent(this.chartLayout);
        
        this.setSizeFull();
        this.setContent(verticalLayout);
    }
    
    
    
    private Component buildErrorsChart() 
    {
    	FilterMap filterMap = (FilterMap)VaadinService.getCurrentRequest().getWrappedSession()
	    		.getAttribute(DashboardSessionValueConstants.FILTERS);
    	
    	List<String> moduleNames = new ArrayList<String>();
    	final HashMap<String, Module> moduleMap = new HashMap<String, Module>();
    	
    	if(filterMap != null)
    	{
    		for(FilterUtil filterUtil: filterMap.getFilters())
    		{
    			for(FilterComponent filterComponent: filterUtil.getFilter().getComponents())
    			{
    				if(!moduleNames.contains(filterComponent.getComponent().getFlow().getModule().getName()))
    				{
    					moduleNames.add(filterComponent.getComponent().getFlow().getModule().getName());
    					moduleMap.put(filterComponent.getComponent().getFlow().getModule().getName(), 
    							filterComponent.getComponent().getFlow().getModule());
    				}
    			}
    		}
    	}
    	else
    	{
    		List<Module> modules = this.topologyService.getAllModules();
    		
    		for(Module module: modules)
    		{
    			moduleNames.add(module.getName());
    			
    			moduleMap.put(module.getName(), module);
    		}
    	}
    	
    	Chart chart = new Chart(ChartType.BAR);
    	chart.setSizeFull();

        Configuration conf = chart.getConfiguration();
        conf.setTitle("ESB Error Report");

        List<ModuleErrorCount> moduleUnactionedErrorsTotal = this.errorReportingManagementService.getModuleErrorCount
        		(moduleNames, false, false, null, this.getMidnightToday());
        
        List<ModuleErrorCount> moduleUnactionedErrorsTodayTotal = this.errorReportingManagementService.getModuleErrorCount
        		(moduleNames, false, false, this.getMidnightToday(), this.getTwentyThreeFixtyNineToday());
        
        HashMap<String, ErrorStats> errorStatsMap = new HashMap<String, ErrorStats>();
        
        for(ModuleErrorCount moduleErrorCount: moduleUnactionedErrorsTotal)
        {
        	logger.debug("Adding errors before today: " + moduleErrorCount);
        	ErrorStats errorStats = new ErrorStats();
        	errorStats.module = moduleMap.get(moduleErrorCount.getModuleName());
        	errorStats.totalErrorsUnactioned = moduleErrorCount.getCount();
        	
        	errorStatsMap.put(errorStats.module.getName(), errorStats);
        }
        
        for(ModuleErrorCount moduleErrorCount: moduleUnactionedErrorsTodayTotal)
        {
        	logger.debug("Adding errors today: " + moduleErrorCount);
        	ErrorStats errorStats = errorStatsMap.get(moduleErrorCount.getModuleName());
        	errorStats.totalErrorsUnactionedToday = moduleErrorCount.getCount();
        	
        	errorStatsMap.put(errorStats.module.getName(), errorStats);
        }
        
        
        
        String[] xAxisNames = new String[errorStatsMap.size()];
        Long[] errorUnactionedBeforeToday = new Long[errorStatsMap.size()];
        Long[] errorUnactionedToday = new Long[errorStatsMap.size()];
        
        int i=0;
        
        for(String key: errorStatsMap.keySet())
        {
        	ErrorStats errorStats = errorStatsMap.get(key);
        	
	        xAxisNames[i] = errorStats.module.getName();
	    	errorUnactionedBeforeToday[i] = errorStats.totalErrorsUnactioned;
	    	errorUnactionedToday[i] = errorStats.totalErrorsUnactionedToday;
	    	
	    	i++;
        }
        
        XAxis x = new XAxis();
        x.setCategories(xAxisNames);
        x.setAllowDecimals(false);
        conf.addxAxis(x);
        
        Labels xlabels = x.getLabels();
        xlabels.setAlign(HorizontalAlign.RIGHT); // Default
        xlabels.setBackgroundColor(SolidColor.PALEGREEN);
        xlabels.setStep(1); 
        xlabels.getStyle().setFontSize("12px");

        YAxis y = new YAxis();
        y.setMin(0);
        y.setTitle("Errors");
        y.setAllowDecimals(false);
        conf.addyAxis(y);

        Legend legend = new Legend();
        legend.setLayout(LayoutDirection.VERTICAL);
        legend.setBackgroundColor("#FFFFFF");
        legend.setHorizontalAlign(HorizontalAlign.CENTER);
        legend.setVerticalAlign(VerticalAlign.BOTTOM);
        legend.getStyle().setFontSize("12px");
        legend.setFloating(false);
        legend.setShadow(true);
        conf.setLegend(legend);

        Tooltip tooltip = new Tooltip();
        tooltip.setFormatter("function() { return '' + this.x + '<br>' + this.series.name + ': ' + this.y + '' + '<br>Click to view errors' ;}");
        conf.setTooltip(tooltip);
        

        PlotOptionsColumn redPlot = new PlotOptionsColumn();
        redPlot.setColor(SolidColor.RED);
        redPlot.setBorderWidth(0);
        redPlot.setStacking(Stacking.NORMAL);

        ListSeries unactionedErrorsSeries = new ListSeries("Errors Unactioned Before Today", errorUnactionedBeforeToday);
        unactionedErrorsSeries.setPlotOptions(redPlot);       
        
        conf.addSeries(unactionedErrorsSeries);
        
        PlotOptionsColumn  orangePlot = new PlotOptionsColumn();
        orangePlot.setColor(SolidColor.ORANGE);
        orangePlot.setBorderWidth(0);
        orangePlot.setStacking(Stacking.NORMAL);

        ListSeries unactionedErrorsTodaySeries = new ListSeries("Errors Unactioned Today", errorUnactionedToday);
        unactionedErrorsTodaySeries.setPlotOptions(orangePlot);
        
        conf.addSeries(unactionedErrorsTodaySeries);

        chart.drawChart(conf);
        
        chart.addPointClickListener(new PointClickListener() 
        {
            @Override
            public void onClick(PointClickEvent event) 
            {
            	Module module = moduleMap.get(event.getCategory());
            	VaadinSession.getCurrent().setAttribute("module", module);
            	VaadinSession.getCurrent().setAttribute("tab", TopologyViewPanel.CATEGORISED_ERROR_TAB);
            	UI.getCurrent().getNavigator().navigateTo("topologyView");
            }
        });
    	
        return chart;
    }
    
    private Component buildExclusionsChart() 
    {
    	FilterMap filterMap = (FilterMap)VaadinService.getCurrentRequest().getWrappedSession()
	    		.getAttribute(DashboardSessionValueConstants.FILTERS);
    	
    	List<String> moduleNames = new ArrayList<String>();
    	final HashMap<String, Module> moduleMap = new HashMap<String, Module>();
    	
    	if(filterMap != null)
    	{
    		for(FilterUtil filterUtil: filterMap.getFilters())
    		{
    			for(FilterComponent filterComponent: filterUtil.getFilter().getComponents())
    			{
    				if(!moduleNames.contains(filterComponent.getComponent().getFlow().getModule().getName()))
    				{
    					moduleNames.add(filterComponent.getComponent().getFlow().getModule().getName());
    					moduleMap.put(filterComponent.getComponent().getFlow().getModule().getName(), 
    							filterComponent.getComponent().getFlow().getModule());
    				}
    			}
    		}
    	}
    	else
    	{
    		List<Module> modules = this.topologyService.getAllModules();
    		
    		for(Module module: modules)
    		{
    			moduleNames.add(module.getName());
    			
    			moduleMap.put(module.getName(), module);
    		}
    	}
    	
    	Chart chart = new Chart(ChartType.BAR);
    	chart.setSizeFull();

        Configuration conf = chart.getConfiguration();
        conf.setTitle("ESB Exclusion Report");

        List<ModuleErrorCount> moduleUnactionedErrorsTotal = this.errorReportingManagementService.getModuleErrorCount
        		(moduleNames, true, false, null, this.getMidnightToday());
        
        List<ModuleErrorCount> moduleUnactionedErrorsTodayTotal = this.errorReportingManagementService.getModuleErrorCount
        		(moduleNames, true, false, this.getMidnightToday(), this.getTwentyThreeFixtyNineToday());
        
        HashMap<String, ErrorStats> errorStatsMap = new HashMap<String, ErrorStats>();
        
        for(ModuleErrorCount moduleErrorCount: moduleUnactionedErrorsTotal)
        {
        	logger.debug("Adding exclusions before today: " + moduleErrorCount);
        	ErrorStats errorStats = new ErrorStats();
        	errorStats.module = moduleMap.get(moduleErrorCount.getModuleName());
        	errorStats.totalErrorsUnactioned = moduleErrorCount.getCount();
        	
        	errorStatsMap.put(errorStats.module.getName(), errorStats);
        }
        
        for(ModuleErrorCount moduleErrorCount: moduleUnactionedErrorsTodayTotal)
        {
        	logger.debug("Adding exclusions today: " + moduleErrorCount);
        	ErrorStats errorStats = errorStatsMap.get(moduleErrorCount.getModuleName());
        	errorStats.totalErrorsUnactionedToday = moduleErrorCount.getCount();
        	
        	errorStatsMap.put(errorStats.module.getName(), errorStats);
        }
               
        String[] xAxisNames = new String[errorStatsMap.size()];
        Long[] errorUnactionedBeforeToday = new Long[errorStatsMap.size()];
        Long[] errorUnactionedToday = new Long[errorStatsMap.size()];
        
        int i=0;
        
        for(String key: errorStatsMap.keySet())
        {
        	ErrorStats errorStats = errorStatsMap.get(key);
        	
	        xAxisNames[i] = errorStats.module.getName();
	    	errorUnactionedBeforeToday[i] = errorStats.totalErrorsUnactioned;
	    	errorUnactionedToday[i] = errorStats.totalErrorsUnactionedToday;
	    	
	    	i++;
        }
        
        XAxis x = new XAxis();
        x.setCategories(xAxisNames);
        x.setAllowDecimals(false);
        conf.addxAxis(x);
        
        Labels xlabels = x.getLabels();
        xlabels.setAlign(HorizontalAlign.RIGHT); // Default
        xlabels.setBackgroundColor(SolidColor.PALEGREEN);
        xlabels.setStep(1); 
        xlabels.getStyle().setFontSize("12px");

        YAxis y = new YAxis();
        y.setMin(0);
        y.setTitle("Exclusions");
        y.setAllowDecimals(false);
        conf.addyAxis(y);

        Legend legend = new Legend();
        legend.setLayout(LayoutDirection.VERTICAL);
        legend.setBackgroundColor("#FFFFFF");
        legend.setHorizontalAlign(HorizontalAlign.CENTER);
        legend.setVerticalAlign(VerticalAlign.BOTTOM);
        legend.getStyle().setFontSize("12px");
        legend.setFloating(false);
        legend.setShadow(true);
        conf.setLegend(legend);

        Tooltip tooltip = new Tooltip();
        tooltip.setFormatter("function() { return '' + this.x + '<br>' + this.series.name + ': ' + this.y + '' + '<br>Click to view errors';}");
        conf.setTooltip(tooltip);

        PlotOptionsColumn redPlot = new PlotOptionsColumn();
        redPlot.setColor(SolidColor.RED);
        redPlot.setBorderWidth(0);
        redPlot.setStacking(Stacking.NORMAL);

        ListSeries unactionedErrorsSeries = new ListSeries("Exclusions Unactioned Before Today", errorUnactionedBeforeToday);
        unactionedErrorsSeries.setPlotOptions(redPlot);       
        
        conf.addSeries(unactionedErrorsSeries);
        
        PlotOptionsColumn  orangePlot = new PlotOptionsColumn();
        orangePlot.setColor(SolidColor.ORANGE);
        orangePlot.setBorderWidth(0);
        orangePlot.setStacking(Stacking.NORMAL);

        ListSeries unactionedErrorsTodaySeries = new ListSeries("Exclusions Unactioned Today", errorUnactionedToday);
        unactionedErrorsTodaySeries.setPlotOptions(orangePlot);
        
        conf.addSeries(unactionedErrorsTodaySeries);

        chart.drawChart(conf);
        
        chart.addPointClickListener(new PointClickListener() 
        {
            @Override
            public void onClick(PointClickEvent event) 
            {
            	Module module = moduleMap.get(event.getCategory());
            	VaadinSession.getCurrent().setAttribute("module", module);
            	VaadinSession.getCurrent().setAttribute("tab", TopologyViewPanel.CATEGORISED_ERROR_TAB);
            	UI.getCurrent().getNavigator().navigateTo("topologyView");
            }
        });
    	
        return chart;
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
    	if( (FilterMap)VaadinService.getCurrentRequest().getWrappedSession()
	    		.getAttribute(DashboardSessionValueConstants.FILTERS) != null)
    	{
    		this.chartLayout.removeAllComponents();
    		this.chartLayout.addComponent(buildErrorsChart());
    		this.chartLayout.addComponent(buildExclusionsChart());
    	}
    }
    
    protected Date getMidnightToday()
	{
		Calendar date = new GregorianCalendar();
		// reset hour, minutes, seconds and millis
		date.set(Calendar.HOUR_OF_DAY, 0);
		date.set(Calendar.MINUTE, 0);
		date.set(Calendar.SECOND, 0);
		date.set(Calendar.MILLISECOND, 0);

		return date.getTime();
	}
	
	protected Date getTwentyThreeFixtyNineToday()
	{
		Calendar date = new GregorianCalendar();
		// reset hour, minutes, seconds and millis
		date.set(Calendar.HOUR_OF_DAY, 23);
		date.set(Calendar.MINUTE, 59);
		date.set(Calendar.SECOND, 0);
		date.set(Calendar.MILLISECOND, 0);

		return date.getTime();
	}
	
	private class ErrorStats
	{
		Module module;
		Long totalErrorsUnactioned = new Long(0);
		Long totalErrorsUnactionedToday = new Long(0);
	}
}
