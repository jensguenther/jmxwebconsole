steal(
		'jquery/model',
		'jwc/websocketfactory', 
		function(){

const URL_BASE = '/api/v1/jmxconnection/{connId}/mbean/{mBeanId}/attributes';
const WS_URL_BASE_AUTOREFRESH = 'ws://'+document.location.hostname+':'+document.location.port+'/api/v1/jmxconnection';

/**
 * @class jwc.models.api.v1.MBeanAttributeModel
 * @parent index
 * @inherits jQuery.Model
 */
$.Model('jwc.models.api.v1.MBeanAttributeModel',
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
			id : null,
			connId : null,
			mBeanId : null,
			name : null,
			value : null,
			autoRefresh : false
		},
		findAll : 'GET '+URL_BASE,
		findOne : 'GET '+URL_BASE+'/{name}',
		create : function(attrs, success, error){
			throw 'create not allowed on MBeanAttributeModel';
		},
		update : function(id, attrs, success, error){
			success.call(this);
		},
		destroy: function(attrs, success, error){
			throw 'delete not allowed on MBeanAttributeModel';
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
		 * Special setter for value which always fires an event in contrast to the default event handling
		 * where the event would be only fired when the value was actually changed.
		 */
		setValue : function(value){
			this.value = value;
			$(this).triggerHandler('value', [value]);
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
					this.update($.parseJSON(event.data)[0]); // we take the first element as the webservice will always return an array
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
			return WS_URL_BASE_AUTOREFRESH + '/' + this.connId + '/mbean/' + this.mBeanId + '/attributes/'+this.name;
		}
	});

});
