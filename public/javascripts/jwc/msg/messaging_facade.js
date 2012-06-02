/**
 * A MessagingFacade hides the implementation details of the underlying message system and eases the usage of that. In
 * general by using the MessageFacade one can establish multiple communication channel between the browser and the
 * server. 
 */

steal(
		'jquery/class',
		'jwc/websocketfactory', 
function(){

	/* private */
	
	/**
	 * {$.Callbacks[]} array of JQuery callbacks
	 * 	Stores the callback functions for certain channels.
	 */
	const channelCallbacksArray = {};
	
	/**
	 * Returns a JQuery Callbacks object for the given channelId.
	 * 
	 * @param {String} channelId
	 * 	The channel's id to return the callbacks for. Must be not null nor undefined.
	 * 
	 * @returns
	 * 	The JQuery Callbacks object.
	 */
	function getChannelCallbacks(channelId){
		var channelCallbacks = channelCallbacksArray[channelId];
		if(! channelCallbacks){
			channelCallbacks = $.Callbacks('unique');
			channelCallbacksArray[channelId] = channelCallbacks;
		}
		
		return channelCallbacks;
	}
	

	$.Class('jwc.msg.MessagingFacade',
			/* static */
			{
				/** 
				 * @hide
				 * INTERNAL ATTRIBUTE
				 * 
				 * The WebSocketFactory to create WebSockets.
				 */
				_webSocketFactory : WebSocketFactory.getInstance(),
				
				/**
				 * @hide
				 * INTERNAL ATTRIBUTE
				 * 
				 * The current WebSocket.
				 */
				_webSocket : null,
				
				/**
				 * Sends a message to a channel. The message is send only to the server. Callbacks registered for that
				 * channel (by addOnMessageCallback) will not receive that message.
				 * 
				 * @param {String} channelId
				 * 	The channel's id to send the message to. Must be not null nor undefined.
				 * 
				 * @param {Object} message
				 * 	The message object to send. Must be not null nor undefined. 
				 */
				sendMessage : function(channelId, message){
					// TODO: establish WebSocket if not yet done
					// TODO: send message
					// TODO: tear down WebSocket there was no more listener at all
				},
				
				/**
				 * Registers a callback function on a channel. 
				 * 
				 * @param {String} channelId
				 * 	The channel's id to register the callback function to. Must be not null nor undefined.
				 * 
				 * @param {Function} callback
				 *  The callback function to be called whenever a message is received on the given channel. Must be not
				 *  null nor undefined. The function should have one parameter of type jwc.msg.Message.
				 */
				addOnMessageCallback : function(channelId, callback){
					getChannelCallbacks(channelId).add(callback);
					// TODO: establish WebSocket if not yet done
					// TODO: create WebSocket callback function to fire messages to the channel's listeners
				},
				
				/**
				 * Unregisters a callback function from a channel.
				 * 
				 * @param {String} channelId
				 * 	The channel's id to remove the callback function from.
				 * 
				 * @param {Function} callback
				 * 	The callback function to unregister from the channel.
				 */
				removeOnMessageCallback : function(channelId, callback){
					getChannelCallbacks(channelId).remove(callback);
					// TODO: remove Websocket callback function if no more listeners for that channel were registered
					// TODO: tear down WebSocket if there was no more listener at all
				},
			},
			/* prototype */
			{
				
				_getWebSocket : function(){
					if(_webSocket === null){
						_webSocket = _webSocketFactory.createWebSocket
					}
				}				
			});
});
