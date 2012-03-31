steal( 
		'jquery/controller',
		'jquery/view/ejs')
	.then( 
			'jwc/widgets/jmx/jmx_tree_view',
			'jwc/widgets/jmx/jmx_info_view')
	.then( 'jwc/widgets/jmxbrowser/jmx_browser_view/views/init.ejs', function($){


// ----------------------------------------------------------------------------------------------------------
// controller
// ----------------------------------------------------------------------------------------------------------

/**
 * @class Jwc.Widgets.Jmxbrowser.JmxBrowserView
 */
$.Controller('Jwc.Widgets.Jmxbrowser.JmxBrowserView',
/** @Static */
{
	defaults : {}
},
/** @Prototype */
{
	wTreeView : null,
	wInfoView : null,
	
	/**
	 * Constructor
	 */
	init : function(){
		this.element.html("jwc/widgets/jmxbrowser/jmx_browser_view/views/init.ejs",{});
		
		this.wTreeView = $(this.element).find('.wJMXTreeView');
		this.wTreeView.jwc_widgets_jmx_jmx_tree_view();
		
		this.wInfoView = $(this.element).find('.wJMXInfoView');
		this.wInfoView.jwc_widgets_jmx_jmx_info_view();
	},
	
	/**
	 * Adds another api.v1.models.JMXConnectionModel to the tree.
	 */
	addJMXConnection : function(jmxConnection){
		this.wTreeView.controller().addJMXConnection(jmxConnection);
	}
})

});