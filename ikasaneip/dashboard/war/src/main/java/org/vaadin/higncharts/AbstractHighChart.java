package org.vaadin.higncharts;

import com.vaadin.ui.AbstractJavaScriptComponent;

/**
 * Abstract Highcharts chart.
 *
 * Download jquery and highcharts.js (if not already loaded in your webapp) and save them in the resource directory org/vaadin/highcharts.
 * Create a new class in the package org.vaadin.highcharts (e.g. "HighChart") and inherit it from AbstractHighChart.
 * Then add a proper JavaScript annotation to the newly created class in order to load all necessary JavaScript
 * libraries that you need (e.g. jquery.js, highcharts.js, highcharts-more.js, ...).
 * Make sure your project complies with the licenses of those libraries.
 * At the end of this list add "highcharts-connector.js".
 *
 * <p>Example of how to extend <code>AbstractHighChart</code>:</p>
 * <pre>
 * package org.vaadin.highcharts;
 *
 * {@literal @}JavaScript({"jquery-min.js", "highcharts.js", "highcharts-connector.js"})
 * public class HighChart extends AbstractHighChart {
 *	private static final long serialVersionUID = -7326315426217377753L;
 * }
 * </pre>
 *
 * @author Stefan Endrullis
 */
public abstract class AbstractHighChart extends AbstractJavaScriptComponent {
	private static final long serialVersionUID = 7738496276049495017L;

	public static int currChartId = 0;

	public static int nextChartId() {
		return ++currChartId;
	}


	protected int chartId = nextChartId();

	/** Creates the chart object. */
	public AbstractHighChart() {
		setId(getDomId());
		getState().domId = getDomId();
		getState().hcjs = "";
	}

	/** Returns the stage of the chart that is shared with the web browser. */
	@Override
	protected HighChartState getState() {
		return (HighChartState) super.getState();
	}

	/**
	 * Returns the DOM ID of the chart component.
	 *
	 * @return DOM ID of the chart component
	 */
	public String getDomId() {
		return "highchart_" + chartId;
	}

	/**
	 * Sets the Highcharts JavaScript code describing the chart.
	 * Note that this code needs to bind the the JSON definition of the chart to a JS variable called <code>options</code>.
	 *
	 * <p>Example:</p>
	 * <pre>chart.setHcjs("var options = { chart: { title: 'my title' } };")</pre>
	 *
	 * @param hcjs Highcharts JavaScript code describing the chart
	 */
	public void setHcjs(String hcjs) {
		getState().hcjs = hcjs;
  }
}
