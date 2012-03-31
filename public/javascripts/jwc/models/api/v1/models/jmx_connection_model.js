steal('jquery/model')
	.then('jquery.json/jquery.json-2.3.min.js', function(){

const URL_BASE = '/api/v1/jmxconnection';

/**
 * @class jwc.models.api.v1.JMXConnectionModel
 * @parent index
 * @inherits jQuery.Model
 */
$.Model('jwc.models.api.v1.JMXConnectionModel',
	/** @Static */
	{
		baseUrl : URL_BASE,
		defaults : {
			id : null,
			host : null,
			port: null
		},
		findAll : 'GET '+URL_BASE,
		findOne : 'GET '+URL_BASE+'/{id}',
		create : function(parameters, success, error){
			$.post(URL_BASE, $.toJSON(parameters), success, 'json').error(error);
		},
		destroy: 'DELETE '+URL_BASE+'/{id}'
	},
	/** @Prototype */
	{
		/**
		 * Returns this connection's domains.
		 */
		getDomains : function(success, error){
			$.get(URL_BASE+'/'+this.id+'/domain', null, success).error(error);
		}
	});

});
