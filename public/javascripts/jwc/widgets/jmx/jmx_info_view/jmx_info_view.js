steal( 
		'jquery/controller',
		'jquery/view/ejs',
		'jwc/topic')
	.then(
		'jwc/widgets/jmx/jmx_attribute_chart_view',
		'jwc/models/jmx/models/jmx_info_view_model.js',
		'./views/default.ejs',
		'./views/error.ejs',
		'./views/mBeanAttributeList.ejs', function($){

const BASE_PATH = '/public/javascripts/jwc/widgets/jmx/jmx_info_view';
const CHART_TYPE_MBEAN_ATTRIBUTE = 'chartTypeMBeanAttribute';

/**
 * Creates a jwc.models.jmxbrowser.InfoViewModel suitable for the given object.
 */
function createInfoViewModelFor(model){
	const clazzFullName = (model.Class)? model.Class.fullName : model;
	
	const viewModel = {
			data : model,
			template : null,
			chartType : null
	};
	
	switch(clazzFullName){
	case 'jwc.models.api.v1.MBeanAttributeModel' :
		viewModel.template = BASE_PATH+'/views/mBeanAttribute.ejs';
		viewModel.chartType = CHART_TYPE_MBEAN_ATTRIBUTE; 
		break;
	case 'jwc.models.api.v1.MBeanAttributeListModel' :
		viewModel.template = BASE_PATH+'/views/mBeanAttributeList.ejs';
		break;
	default :
		viewModel.data = clazzFullName,
		viewModel.template = BASE_PATH+'/views/error.ejs';
	}
	
	return new jwc.models.jmx.JMXInfoViewModel(viewModel);
}

/**
 * @class jwc.widgets.jmx.JMXInfoView
 * 
 * An Widget display information from various JMX types.
 * 
 */
$.Controller('jwc.widgets.jmx.JMXInfoView',
	/** @Static */
	{
		defaults : {
			model : {key: 'key', value: 'value'}
		}
	},
	/** @Prototype */
	{
		_updateViewProxy : null,
		
		init : function(){
			this._updateViewProxy = this.proxy(this._updateView);
			
			this._updateViewModel();
			this._initView();
			
			this._initEventHandling();
			
			this._startAutoRefresh();
		},
		
		/**
		 * @hide
		 * INTERNAL FUNCTION
		 * 
		 * Initializes internal event handling.
		 */
		_initEventHandling : function(){
			// internal event handling -----------------------------------------------------------------------
			function handleDefault(model){
				this.update({model : {key : "id", value : model.id }});
			}
			const handleDefaultProxy = this.proxy(handleDefault);
			
			function handleModel(model){
				this.update({model : model});
			}
			const handleModelProxy = this.proxy(handleModel);
						
			Topic.subscribe('jmxConnection.selected.jmxTree', handleDefaultProxy);
			Topic.subscribe('domain.selected.jmxTree', handleDefaultProxy);
			Topic.subscribe('folder.selected.jmxTree', handleDefaultProxy);
			Topic.subscribe('mBean.selected.jmxTree', handleDefaultProxy);
			Topic.subscribe('mBeanAttributeModel.selected.jmxTree', handleModelProxy);
			Topic.subscribe('mBeanAttributeListModel.selected.jmxTree', handleModelProxy);
		},
		
		/**
		 * Updates this widget based on the options.
		 */
		update : function( options ){
			this._stopAutoRefresh();
			this._super(options);
			
			this._updateViewModel();
			this._initView();
			
			this._startAutoRefresh();
		},
		
		_modelAutoRefreshOrig : null,
		
		/**
		 * @hide
		 * INTERNAL FUNCTION
		 */
		_startAutoRefresh : function(){
			if(this.options.model.autoRefresh != null){
				this._modelAutoRefreshOrig = this.options.model.autoRefresh;
				this.options.model.update({autoRefresh:true});
				this.options.model.bind('updated', this._updateViewProxy);
			}
		},
		
		/**
		 * @hide
		 * INTERNAL FUNCTION
		 */
		_stopAutoRefresh : function(){
			if(this.options.model.autoRefresh != null){
				this.options.model.unbind('updated', this._updateViewProxy);
				this.options.model.update({autoRefresh:this._modelAutoRefreshOrig});
				this._modelAutoRefreshOrig = null;
			}
		},
		
		/**
		 * @hide
		 * INTERNAL FUNCTION
		 */
		_updateViewModel : function(){
			this.options.viewModel = createInfoViewModelFor(this.options.model);
		},
		
		/**
		 * @hide
		 * INTERNAL FUNCTION
		 */
		_initView : function(){
			this.element.html( this.options.viewModel.template, { data : this.options.viewModel.data });
			
			switch(this.options.viewModel.chartType){
			case CHART_TYPE_MBEAN_ATTRIBUTE :
				
				Highcharts.setOptions({
					global: {
						useUTC: false
					}
				});
				
				$('.chart').jwc_widgets_jmx_jmx_attribute_chart_view({
					models : [this.options.model]
				});
				break;
			}
		},
		
		/**
		 * @hide
		 * INTERNAL FUNCTION
		 */
		_updateView : function(e){
			
			switch(this.options.viewModel.chartType){
			case CHART_TYPE_MBEAN_ATTRIBUTE :
				// ignore
				break;
			default :
				this.element.html( this.options.viewModel.template, { data : this.options.viewModel.data });
			}
		}
		
	});
});