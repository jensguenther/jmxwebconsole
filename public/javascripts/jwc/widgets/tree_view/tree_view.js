steal(
		'jquery/controller',
		'jwc/topic')
	.then('jquery.jstree/jquery.jstree.js', function($){


/**
 * Re-engineers the original event type name.
 * 
 * @param event the event to return the original event type name for
 * @returns the event's event type name
 */
function getEventTypeName(event){
	var eventTypeName = event.type;
	if(event.namespace){
		eventTypeName = eventTypeName + "." + event.namespace;
	}
	
	return eventTypeName;
}

/**
 * @class jwc.widgets.TreeView
 * 
 * This class is basically a JavaScriptMVC Controller decorator around a jstree instance.
 * 
 * Options
 *
 * One can provide options when instanciating an object of this class. That option object is simply the same
 * as if one would have been initialized the jstree directly.
 *   
 * Events
 * 
 * Events generated by jstree will be forwarded to 'XXX.jstree' topics accordingly. So far, following events
 * are propagated:
 * - loaded.jstree
 * - load_node.jstree
 * - select_node.jstree
 * 
 * The delivered message has the following format:
 * 
 * 	{
 * 		e : event, 
 * 		data:  data
 *  }
 * 
 * whereas the individual parameters are the same as if one had gotten these from the original event handling 
 * invocation.
 */
$.Controller('jwc.widgets.TreeView',
/** @Static */
{
	defaults : {}
},
/** @Prototype */
{
	init : function(){
		$.jstree._themes = "/public/stylesheets/plugins/jstree/themes/";
		this.element.jstree(this.options);
		
		this._initEventPublishing();
	},
	
	/**
	 * @hide
	 * Forwards all (well, those being used) xxx.jstree events to a topic named after that event. 
	 */
	_initEventPublishing : function(){
		function publish(e, data){
			Topic.publish(getEventTypeName(e), {e : e, data: data});
		}
		
		$.each('loaded load_node select_node hover_node dehover_node'.split(' '), this.proxy(function(i, v){
			this.delegate(v+'.jstree', publish);
		}));
	},
	
	/**
	 * Binds callback functions to event.
	 *  
	 * @param {String} event the event to listen to 
	 * @param {Function|String} callback the function to call in case of event
	 */
	delegate : function(event, callback){
		this._super(this.element, null, event, callback);
	},
})

});