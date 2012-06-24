/**
 * A MessagingFacade hides the implementation details of the underlying message system and eases the usage of that. In
 * general by using the MessageFacade one can establish multiple communication channel between the browser and the
 * server. 
 */

steal(
		'jquery/class',
		'jwc/websocketfactory', 
function(){
	
	$.Class('MessagingFacade',
			/* static */
			{
				/**
				 * @hide
				 * INTERNAL ATTRIBUTE
				 * 
				 * The callbacks object holding all callbacks.
				 */
				_callbacks : $.Callbacks('unique'),
				
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
				 * The WebSocket endpoint url. Has to be specified before any other action is done.
				 */
				webSocketURL : null,
				
				/**
				 * Sends a message. The message is send only to the server. Callbacks registered will not
				 * receive that message.
				 * 
				 * @param {Object} message
				 * 	The message object to send. Must be not null nor undefined. 
				 */
				sendMessage : function(message){
					// TODO: establish WebSocket if not yet done
					// TODO: send message
				},
				
				/**
				 * @hide
				 * INTERNAL METHOD
				 * 
				 * Will be called whenever a message arrives over the WebSocket.
				 */
				_onmessage : function(evt){
					this._callbacks.fire(evt);
				},
				
				/**
				 * Registers a callback function.
				 * 
				 * @param {Function} callback
				 *  The callback function to be called whenever a message is received. Must be not
				 *  null nor undefined. The function should have one parameter to receive the event.
				 */
				addOnMessageCallback : function(callback){
					this._callbacks.add(callback);
					this._getWebSocket().onmessage = this.proxy(this._onmessage);
				},
				
				/**
				 * Unregisters a callback function.
				 * 
				 * @param {Function} callback
				 * 	The callback function to unregister.
				 */
				removeOnMessageCallback : function(callback){
					this._callbacks.remove(callback);
				},
				
				/**
				 * @hide
				 * INTERNAL FUNCTION
				 * 
				 * Returns the WebSocket. Creates a new one if neccessary. Expects #webSocketURL was set 
				 * before. Otherwise it will throw an Error.
				 * 
				 * @returns
				 * 	The WebSocket.
				 * 
				 * @throws Error
				 * 	if the #webSocketURL was not set before.
				 */
				_getWebSocket : function(){
					if(this._webSocket === null){
						if(!this.webSocketURL){
							throw new Error("webSocketURL must not be null or undefined");
						}
						
						this._webSocket = this._webSocketFactory.createWebSocket(this.webSocketURL);
					}
					
					return this._webSocket;
				}				
			},
			/* prototype */
			{
			});
});
