steal(
		'jquery/controller',
		'jwc/models/api/v1/models/mbean_attribute_model.js',
		'jwc/widgets/chart_view',
function($){


/**
 * Returns a new data array based on the series.data array which contains data points only for the time range
 * of now() - timeRange.
 * 
 * @param {Number} timeRange the time range of the data array in seconds
 * @param {HighCharts.Series} series the series object to get the new data array for
 * 
 * @returns a data array to be suitable to be set to a series object
 */
function getTimeRangeConstrainedDataArray(timeRange, series){
	const dataArray = [];
	// oldestTimeAllowed in millis
	const oldestTimeAllowed = (new Date().getTime()) - (timeRange * 1000);
	// skip the oldest points
	for(var i in series.data){
		var point = series.data[i];
		if(oldestTimeAllowed <= point.x){
			point = [point.x, point.y];
			dataArray.push(point);
		}
	}
	
	return dataArray;
}

// ----------------------------------------------------------------------------------------------------------
// widget
// ----------------------------------------------------------------------------------------------------------

/**
 * Chart Widget to display multiple jwc.models.api.v1.MBeanAttributeModel values.
 */
jwc.widgets.ChartView('jwc.widgets.jmx.JMXAttributeChartView',
/** @Static */
{
	defaults : {
		models : [],
		// how much to look into the past in seconds
		timeRange: 20,
		chartConfig : {
			chart: {
				defaultSeriesType: 'spline',
				marginRight: 10
			},
			title: {
				text: null
			},
			xAxis: {
				type: 'datetime',
				tickPixelInterval: 150
			},
			yAxis: {
				title: {
					text: 'Value'
				},
				plotLines: [{
					value: 0,
					width: 1,
					color: '#808080'
				}]
			},
			tooltip: {
				formatter: function() {
						return '<b>'+ this.series.name +'</b><br/>'+
						Highcharts.dateFormat('%H:%M:%S', this.x) +'<br/>'+
						Highcharts.numberFormat(this.y, 2);
				}
			},
			legend: {
				enabled: true
			},
			exporting: {
				enabled: false
			},
			series: []
		}
	}
},
/** @Prototype */
{
	
	/**
	 * @hide
	 * PROXY FUNCTION
	 */
	_handleModelValueEventProxy : null,
	
	/**
	 * Constructor.
	 */
	init : function(){
		this._super();
		
		this._handleModelValueEventProxy = this.proxy(this._handleModelValueEvent);

		// initializing drop from jmx_tree_view
		this._initJMXTreeViewDrop();
		
		// add all models 
		for(index in this.options.models){
			this._addMBeanAttributeModelSeries(this.options.models[index], true);
		}
	},
	
	/**
	 * @hide
	 * Initializes drop from a JMXTreeView instance. This is somewhat cumbersome as we normally would have
	 * drop functionality by utilizing JavaScriptMVC's $.Drop. But for some strange reason I currently can't
	 * get $.Drag running with jstree continously, sadly. 
	 */
	_initJMXTreeViewDrop : function(){
		// class is defined in the JMXTreeView, denotes valied drop targets
		this.element.addClass('jmxModelDropTarget');
		this.element[0].drop_check = function(model){
			return model instanceof jwc.models.api.v1.MBeanAttributeModel;
		};
		this.element[0].drop_finish = this.proxy(function(model){
			if(model instanceof jwc.models.api.v1.MBeanAttributeModel){
				this.addMBeanAttributeModel(model);
			}
		});
	},
	
	/**
	 * Destructor
	 */
	destroy : function(){
		// remove all models 
		for(index in this.options.models){
			this.removeMBeanAttributeModel(this.options.models[index]);
		}
	},
	
	/**
	 * Adds another jwc.models.api.v1.MBeanAttributeModel to this chart.
	 */
	addMBeanAttributeModel : function(mBeanAttributeModel){
		const currentModels = this.options.models;

		if($.inArray(mBeanAttributeModel, currentModels) != -1){
			// already known model, return
			return;
		}
		
		// store model
		currentModels.push(mBeanAttributeModel);
		// add series to the chart
		this._addMBeanAttributeModelSeries(mBeanAttributeModel);
	},
	
	/**
	 * @hide
	 * INTERNAL FUNCTION
	 * 
	 * Adds another series for the given jwc.models.api.v1.MBeanAttributeModel and initializes model 
	 * listening.
	 */
	_addMBeanAttributeModelSeries : function(mBeanAttributeModel){
		this.chart.addSeries({
			id : mBeanAttributeModel.id,
			name : mBeanAttributeModel.name,
			data : [[(new Date()).getTime(), mBeanAttributeModel.value]]
		});
		// bind to changes
		mBeanAttributeModel.bind('value', this._handleModelValueEventProxy);
		// start autorefresh
		mBeanAttributeModel.update({autoRefresh:true});
	},
	
	/**
	 * Removes a jwc.models.api.v1.MBeanAttributeModel from this chart.
	 */
	removeMBeanAttributeModel : function(mBeanAttributeModel){
		const currentModels = this.options.models;

		const index = $.inArray(mBeanAttributeModel, currentModels);
		if(index == -1){
			// not found;
			return;
		}

		// stop listening to updates
		mBeanAttributeModel.unbind('value', this._handleModelValueEventProxy);
		// remove series
		this.chart.get(mBeanAttributeModel.id).remove();
		// remove model
		currentModels.splice(index, 1);
	},
	
	/**
	 * @hide
	 * INTERNAL FUNCTION
	 * 
	 * Gets called whenever a jwc.models.api.v1.MBeanAttributeModel's value was updated.
	 * 
	 * @param {JQuery Model Event} e
	 */
	_handleModelValueEvent : function(e){
		const mBeanAttributeModel = e.target;
		// the series' id is the mBeanAttributeModel.id, was stored at _createSeriesFor
		const series = this.chart.get(mBeanAttributeModel.id);
		// the point to add to the end of the series
		const point = [(new Date()).getTime(), mBeanAttributeModel.value];

		// time-constrain the series.data array
		const dataArray = getTimeRangeConstrainedDataArray(this.options.timeRange, series);
		// add the new point
		dataArray.push(point);
		// update & redraw
		series.setData(dataArray, true);
	}
});
});