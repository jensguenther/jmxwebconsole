steal(
		'jquery/model',
		'jwc/websocketfactory',
		'jwc/models/api/v1/models/mbean_attribute_model.js', 
		function(){

const WS_URL_BASE_AUTOREFRESH = 'ws://'+document.location.hostname+':'+document.location.port+'/api/v1/jmxconnection';

/**
 * @class jwc.models.api.v1.MBeanAttributeListModel
 * @parent index
 * @inherits jQuery.Model
 */
$.Model('jwc.models.api.v1.MBeanAttributeListModel',
	/** @Static */
	{
		/** 
		 * @hide
		 * The WebSocketFactory to create WebSocket.
		 */
		_webSocketFactory : WebSocketFactory.getInstance(),
		
		/**
		 * Default Model data.
		 */
		defaults : {
			connId : null,
			mBeanId : null,
			attributes : [],
			autoRefresh : false
		},
		create : function(attrs, success, error){
			throw 'create not allowed on MBeanAttributeModel';
		},
		update : function(id, attrs, success, error){
			success.call(this);
		},
		findOne : function(parameters, success, error){
			const options = parameters;
			const successFunc = success;
			
			function convert(mBeanAttributeModels){
				successFunc.call(
						this,
						new jwc.models.api.v1.MBeanAttributeListModel({
							id : 'jmxconnection/'+options.connId+'/mbean/'+options.mBeanId+'/attributes',
							connId : options.connId,
							mBeanId : options.mBeanId,
							attributes : mBeanAttributeModels,
							autoRefresh : options.autoRefresh
						}));
			};
			
			jwc.models.api.v1.MBeanAttributeModel.findAll(
					parameters,
					convert,
					error);
		}
	},
	/** @Prototype */
	{
		/**
		 * @hide
		 * INTERNAL ATTRIBUTE
		 * 
		 * Stores the web socket reference for auto refreshing this instance.
		 */
		_autoRefreshWebSocket : null,
		
		/**
		 * Constructor.
		 */
		init : function(){
			this.validate();
			
			this._switchAutoRefresh();
		},
		
		/**
		 * Validates this instance. 
		 */
		validate : function(){
			if(!this.id){
				throw 'id must be set';
			}
		},
		
		/**
		 * Gets called whenever this instance's attributes has been updated.
		 */
		update : function(attrs, success, error){
			this._super(attrs, success, error);
			
			this._switchAutoRefresh();
		},
		
		/**
		 * @hide
		 * INTERNAL FUNCTION
		 */
		_switchAutoRefresh : function(){
			// 1. autoRefresh false and no socket running
			if(!(this.autoRefresh || this._autoRefreshWebSocket)){
				return;
			}
			// 2. autoRefresh true and socket running
			if(this.autoRefresh && this._autoRefreshWebSocket){
				return;
			}
			// 3. start autoRefresh
			if(this.autoRefresh){
				this._autoRefreshWebSocket = this.Class._webSocketFactory.createWebSocket(this._createWSUrlAutoRefresh());
				this._autoRefreshWebSocket.onmessage = this.proxy(function(event){
					// get the attributes as objects
					const attrObjects = $.parseJSON(event.data);
					const mBeanAttributeModels = [];
					// convert the objects into MBeanAttributeModels
					$.each(attrObjects, function(i, obj){
						mBeanAttributeModels.push(new jwc.models.api.v1.MBeanAttributeModel(obj));
					});
					this.update({attributes : mBeanAttributeModels});
				});
				return;
			}
			// 4. stop autoRefresh
			if(!this.autoRefresh){
				var socket = this._autoRefreshWebSocket;
				// remove message handler
				socket.onmessage = null;
				// conditional close the socket
				if(socket.readyState && socket.readyState == WebSocket.OPEN){
					// workaround for the play hybi10 decoder -- won't recognize the close frame
					socket.send('close');
					socket.close();					
				}
				this._autoRefreshWebSocket = null;
				return;
			}
		},
		
		/**
		 * @hide
		 * INTERNAL FUNCTION
		 * Creates the autorefresh websocket url for this model.
		 */
		_createWSUrlAutoRefresh : function(){
			return WS_URL_BASE_AUTOREFRESH + '/' + this.connId + '/mbean/' + this.mBeanId + '/attributes';
		},
		
		/**
		 * Returns the MBeanAttributeModel out of this.attribute which has the given name.
		 * 
		 * @param {String} attributeName
		 */
		getMBeanAttributeModel : function(attributeName){
			var attributeModel = null;
			for(var index in this.attributes){
				if(this.attributes[index].name == attributeName){
					attributeModel = this.attributes[index];
					break;
				}
			}
			
			return attributeModel;
		}
	});

});
