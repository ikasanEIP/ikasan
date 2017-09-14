window.org_vaadin_highcharts_AbstractHighChart = function () {

	this.onStateChange = function () {
		// read state
		var domId = this.getState().domId;
		var hcjs = this.getState().hcjs;

		// evaluate highcharts JS which needs to define var "options"
		eval(hcjs);

		// set chart context
		$('#' + domId).highcharts(options)
	};

};
