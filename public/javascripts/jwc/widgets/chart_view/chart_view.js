steal(
		'jquery/controller')
	.then(
		'highcharts/highcharts.js',
function($){

$.Controller('jwc.widgets.ChartView',
/** @Static */
{
	/**
	 * Options have one attribute chartConfig which defaults to the Highcharts options object. One need to 
	 * specify them when creating the widget like the following:
	 * 
	 * $('#container').jwc_widgets_chart_view({
	 * 	chartConfig : {
	 * 		chart : {
	 * 			defaultSeriesType : 'spline',
	 * 			marginRight: 10
	 *  	},
	 *  	title {
	 *   		...
	 *  	}, ...
	 *  }
	 * });
	 * 
	 * However, the chartConfig.chart.renderTo reference will be overidden to point to this.element[0].
	 */
	defaults : {
		chartConfig : null
	}
},
/** @Prototype */
{
	/**
	 * @hide
	 * The chart instance.
	 */
	chart : null,
	
	/**
	 * Constructor.
	 */
	init : function(){
		this.options.chartConfig.chart.renderTo = this.element[0];
		
		this.chart = new Highcharts.Chart(this.options.chartConfig);
	},
	
	/**
	 * Destructor.
	 */
	destroy : function(){
		this.chart.destroy();
	}
})

});