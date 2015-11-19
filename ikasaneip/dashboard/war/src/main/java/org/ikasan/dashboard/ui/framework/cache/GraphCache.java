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
package org.ikasan.dashboard.ui.framework.cache;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.ikasan.dashboard.ui.Broadcaster;
import org.ikasan.error.reporting.model.ErrorOccurrence;
import org.ikasan.spec.error.reporting.ErrorReportingService;

//import com.googlecode.wickedcharts.highcharts.jackson.JsonRenderer;
//import com.googlecode.wickedcharts.highcharts.options.Axis;
//import com.googlecode.wickedcharts.highcharts.options.ChartOptions;
//import com.googlecode.wickedcharts.highcharts.options.Labels;
//import com.googlecode.wickedcharts.highcharts.options.Options;
//import com.googlecode.wickedcharts.highcharts.options.PlotOptions;
//import com.googlecode.wickedcharts.highcharts.options.PlotOptionsChoice;
//import com.googlecode.wickedcharts.highcharts.options.SeriesType;
//import com.googlecode.wickedcharts.highcharts.options.Title;
//import com.googlecode.wickedcharts.highcharts.options.Tooltip;
//import com.googlecode.wickedcharts.highcharts.options.drilldown.DrilldownPoint;
//import com.googlecode.wickedcharts.highcharts.options.series.PointSeries;
//import com.googlecode.wickedcharts.highcharts.options.series.SimpleSeries;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class GraphCache
{
	private Logger logger = Logger.getLogger(GraphCache.class);
	
	private ErrorReportingService errorReportingService;
	private HashMap<ErrorOccurrenceKey, Integer> errorOccurrencesMap;
	private HashMap<String, Integer> moduleErrorOccurrencesMap;
//	private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
//	private CacheRefreshTask task = new CacheRefreshTask();
	
	
	
	/**
	 * @param errorReportingService
	 */
	public GraphCache(ErrorReportingService errorReportingService)
	{
		super();
		this.errorReportingService = errorReportingService;
		
		update();
		
//		executor.scheduleAtFixedRate(task, 0, 60, TimeUnit.SECONDS);
	}


//	private class CacheRefreshTask implements Runnable
//	{
//
//		/* (non-Javadoc)
//		 * @see java.lang.Runnable#run()
//		 */
//		@Override
//		public void run()
//		{
//			update();
//		}
//	}
//	
	protected void update()
	{
		errorOccurrencesMap = new HashMap<ErrorOccurrenceKey, Integer>();
		moduleErrorOccurrencesMap = new HashMap<String, Integer>();
		
		logger.debug("Synchronising topology state cache.");
		List<ErrorOccurrence> errorOccurrences 
			= errorReportingService.find(null, null, null, getMidnightToday(), getTwentyThreeFixtyNineToday());
		
		for(ErrorOccurrence errorOccurrence: errorOccurrences)
		{
			ErrorOccurrenceKey key = new ErrorOccurrenceKey();
			key.moduleName = errorOccurrence.getModuleName();
			key.flowName = errorOccurrence.getFlowName();
			key.componentName = errorOccurrence.getFlowElementName();
			
			Integer value = errorOccurrencesMap.get(key);
			
			if(value == null)
			{
				value = new Integer(1);
				errorOccurrencesMap.put(key, value);
			}
			else
			{
				value++;
			}
			
			value = moduleErrorOccurrencesMap.get(errorOccurrence.getModuleName());
			
			if(value == null)
			{
				value = new Integer(1);
				moduleErrorOccurrencesMap.put(errorOccurrence.getModuleName(), value);
			}
			else
			{
				value++;
				moduleErrorOccurrencesMap.put(errorOccurrence.getModuleName(), value);
			}
		}
		
//		Broadcaster.broadcast(errorOccurrencesMap);
	}
	
	public String getGraphJson()
	{
//    	Options options = new Options();
//
//    	ChartOptions co = new ChartOptions();
////    	co.setHeight(500);
////    	co.setWidth(500);
//    	co.setType(SeriesType.COLUMN);
//    	
//	    options
//	        .setChartOptions(co);
//
//	    options
//	        .setTitle(new Title("Errors Per Module"));
//	    
//	    options
//        	.setSubtitle(new Title("Click on module name to view component errors."));
//
////	    ArrayList<String> axisList = new ArrayList<String>();
//	    
////	    axisList.addAll(this.moduleErrorOccurrencesMap.keySet());    
//	    
//	    options
//	        .setxAxis(new Axis().setCategories(categories));
////	            .setCategories(axisList));
//
//	    options.setTooltip(new Tooltip().setHeaderFormat("<span style=\"font-size:11px\">{series.name}</span><br>")
//	    		.setPointFormat("<span style=\"color:{point.color}\">{point.name}</span>: <b>{point.y:.2f}%</b> errors in total<br/>"));
//	    options
//	        .setyAxis(new Axis()
//	            .setTitle(new Title("Total number of errors")));
//
//	    options.setPlotOptions(new PlotOptionsChoice()
//	    	.setSeries(new PlotOptions().setBorderWidth(0).setDataLabels
//	    			(new DataLabels().setEnabled(true).setsetFormatter
//	    					(new Function().setFunction("{point.y:.1f}%")))));
//	    
//	    PointSeries pointSeries = new PointSeries();
//    	pointSeries.setName("Modules");
//    	
//	    for(String key: this.moduleErrorOccurrencesMap.keySet())
//	    {
//	    	Integer value = this.moduleErrorOccurrencesMap.get(key);
//	    	
//	    	Point point = new Point(key, value);
//	    	
//	    	pointSeries.addPoint(point);
//	    }
//
//
//	    options
//    		.addSeries(pointSeries);
//	    
//	    JsonRenderer renderer = new JsonRenderer();
//	    
//	    return renderer.toJson(options);
		
//		Options options = new Options();
//
//	    options
//	        .setChartOptions(new ChartOptions()
//	            .setType(SeriesType.COLUMN));
//
//	    options
//	        .setTitle(new Title("Errors Per Module"));
//
////	    options
////	        .setxAxis(new Axis()
////	            .setCategories(Arrays
////	                .asList(new String[] { "Jan", "Feb", "Mar", "Apr", "May",
////	                    "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" })).setLabels(new Labels().setRotation(-45)));
//	    
//	    ArrayList<String> axisList = new ArrayList<String>();
//	    
//	    axisList.addAll(this.moduleErrorOccurrencesMap.keySet());    
//	    
//	    options
//	        .setxAxis(new Axis()
//	            .setCategories(axisList).setLabels(new Labels().setRotation(-45)));
//	    
//	    options.setTooltip(new Tooltip().setHeaderFormat("<span style=\"font-size:11px\">{series.name}</span><br>")
//	    		.setPointFormat("<span style=\"color:{point.color}\">{point.name}</span>: <b>{point.y}</b> errors in total<br/>"));
//
//	    options
//	        .setyAxis(new Axis()
//	            .setTitle(new Title("Total number of errors.")));
//	    
//	    options.setPlotOptions(new PlotOptionsChoice()
//    		.setSeries(new PlotOptions().setColorByPoint(true)));
//
////	    options
////	        .setLegend(new Legend()
////	            .setLayout(LegendLayout.VERTICAL)
////	            .setAlign(HorizontalAlignment.RIGHT)
////	            .setVerticalAlign(VerticalAlignment.TOP)
////	            .setX(-10)
////	            .setY(100)
////	            .setBorderWidth(0));
////
////	    options
////	        .addSeries(new SimpleSeries()
////	            .setName("Tokyo")
////	            .setData(
////	                Arrays
////	                    .asList(new Number[] { 7.0, 6.9, 9.5, 14.5, 18.2, 21.5,
////	                        25.2, 26.5, 23.3, 18.3, 13.9, 9.6 })));
////
////	    options
////	        .addSeries(new SimpleSeries()
////	            .setName("New York")
////	            .setData(
////	                Arrays
////	                    .asList(new Number[] { -0.2, 0.8, 5.7, 11.3, 17.0, 22.0,
////	                        24.8, 24.1, 20.1, 14.1, 8.6, 2.5 })));
//	    
//	    PointSeries pointSeries = new PointSeries();
//    	pointSeries.setName("Modules");
//    	
//	    for(String key: this.moduleErrorOccurrencesMap.keySet())
//	    {
//	    	Integer value = this.moduleErrorOccurrencesMap.get(key);
//	    	
////	    	Point point = new Point(key, value);
////	    	point.setColor(Color.BLACK);
////	    	
//	    	
//	    	Options drillDownOptions = new Options();
//
//	    	drillDownOptions
//		        .setChartOptions(new ChartOptions()
//		            .setType(SeriesType.COLUMN));
//
//	    	drillDownOptions
//		        .setTitle(new Title("Errors Per Flow"));
//	    	
//	    	
//	    	ArrayList<String> xAxisList = new ArrayList<String>();
//		    
//	    	
//	    	SimpleSeries flowPointSeries = new SimpleSeries();
//	    	flowPointSeries.setName("Flows");
//	    	
//	    	ArrayList<Number> data = new ArrayList<Number>();
//	    	
//	    	int flowErrors = 0;
//		    for(ErrorOccurrenceKey comKey: this.errorOccurrencesMap.keySet())
//		    {
//		    	if(key.equals(comKey.moduleName))
//		    	{		    		 
//		    		 xAxisList.add(comKey.flowName);	 
//		    		 
//		    		 data.add(this.errorOccurrencesMap.get(key));    		
//		    	}
//		    }
//		    
//		    drillDownOptions
//	        	.setxAxis(new Axis()
//	            	.setCategories(xAxisList)).setLabels(new Labels().setRotation(-45));
//		    
//		    flowPointSeries.setData(data);
//		    drillDownOptions.addSeries(flowPointSeries);
//	    	
//	    	DrilldownPoint drillDownPoint = new DrilldownPoint(options, drillDownOptions);
//	    	drillDownPoint.setName(key);
//	    	drillDownPoint.setY(value);
//	    	
//	    	
//	    	
//	    	pointSeries.addPoint(drillDownPoint);
//	    }
//
//
//	    options
//    		.addSeries(pointSeries);
//	    
//	    JsonRenderer  r = new JsonRenderer();
//	    return r.toJson(options);
		
		return "";
	}
	
	private class ErrorOccurrenceKey
	{
		String moduleName;
		String flowName;
		String componentName;
		
		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result
					+ ((componentName == null) ? 0 : componentName.hashCode());
			result = prime * result
					+ ((flowName == null) ? 0 : flowName.hashCode());
			result = prime * result
					+ ((moduleName == null) ? 0 : moduleName.hashCode());
			return result;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ErrorOccurrenceKey other = (ErrorOccurrenceKey) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (componentName == null)
			{
				if (other.componentName != null)
					return false;
			} else if (!componentName.equals(other.componentName))
				return false;
			if (flowName == null)
			{
				if (other.flowName != null)
					return false;
			} else if (!flowName.equals(other.flowName))
				return false;
			if (moduleName == null)
			{
				if (other.moduleName != null)
					return false;
			} else if (!moduleName.equals(other.moduleName))
				return false;
			return true;
		}
		
		private GraphCache getOuterType()
		{
			return GraphCache.this;
		}
	}
	
	protected Date getMidnightToday()
	{
		Calendar date = new GregorianCalendar();
		// reset hour, minutes, seconds and millis
		date.set(Calendar.DAY_OF_YEAR, -2);
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
}
