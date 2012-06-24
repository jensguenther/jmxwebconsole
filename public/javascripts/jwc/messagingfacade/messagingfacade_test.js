steal(
		'funcunit/qunit',
		'jwc/websocketfactory/mockwebsocketfactory.js',
		'jwc/messagingfacade')  
.then(function(){

	module("jwc/messagingfacade");
	
	// stores the last event a callback function recevied
	var callbackLastEvent = null;
	
	// creates a callback function
	function createCallback(){
		return function callback(evt){
			callbackLastEvent = evt;
			ok(true);
		}
	}

	/**
	 * Cleans the MessagingFacade. 
	 */
	function tearDown(){
		MessagingFacade._webSocketFactory = WebSocketFactory.getInstance();
		MessagingFacade._webSocket = null;
		MessagingFacade.webSocketURL = null;
		MessagingFacade._callbacks.empty();
		
		callbackLastEvent = null;
	}
	
	test('_getWebSocket_when_webSocketIsNullAndURLIsNull_then_errorHasToBeThrown', function(){
		expect(1);
		
		// when
		MessagingFacade._webSocket = null;
		MessagingFacade.webSocketURL = null;
		
		// then
		try{
			MessagingFacade._getWebSocket();
			ok(false);
		} catch(e){
			ok(true);
		}
		
		tearDown();
	});

	test('_getWebSocket_when_webSocketIsNull_then_webSocketHasToBeAquired', function(){
		expect(2);
		
		// when
		MessagingFacade._webSocketFactory = MockWebSocketFactory.getInstance();
		MessagingFacade._webSocket = null;
		MessagingFacade.webSocketURL = 'url';
		
		// then
		try{
			MessagingFacade._getWebSocket();
			ok(MessagingFacade._webSocket);
			equals(MessagingFacade._webSocket.url, MessagingFacade.webSocketURL);
		} catch(e){
			ok(false);
		}
		
		tearDown();
	});

	test('_getWebSocket_when_webSocketIsNotNull_then_thatWebSocketIsUsed', function(){
		expect(1);
		
		// when
		const webSocket = 'webSocket';
		MessagingFacade._webSocketFactory = null;
		MessagingFacade._webSocket = webSocket;
		MessagingFacade.webSocketURL = null;
		
		// then
		try{
			MessagingFacade._getWebSocket();
			equals(MessagingFacade._webSocket, webSocket);
		} catch(e){
			ok(false);
		}
		
		tearDown();
	});
	
	test('addOnMessageCallback_when_addFirstCallback_then_webSocketHasToBeAquired_and_thatWebSocketHasToHaveAOnMessageFunc', function(){
		expect(3);
		
		// when
		MessagingFacade._webSocketFactory = MockWebSocketFactory.getInstance();
		MessagingFacade.webSocketURL = 'url';
		
		ok(MessagingFacade._webSocket === null);
		
		// then		
		MessagingFacade.addOnMessageCallback(createCallback());
		ok(MessagingFacade._webSocket);
		ok(MessagingFacade._webSocket.onmessage);
		
		tearDown();
	});
	
	test('addOnMessageCallback_when_addSecondCallback_then_theSameWebSocketHasToBeUsed', function(){
		expect(2);
		
		// when
		MessagingFacade._webSocketFactory = MockWebSocketFactory.getInstance();
		MessagingFacade.webSocketURL = 'url';

		ok(MessagingFacade._webSocket === null);

		// then
		MessagingFacade.addOnMessageCallback(createCallback());
		const webSocket = MessagingFacade._webSocket;
		// and
		MessagingFacade.addOnMessageCallback(createCallback());
		equals(MessagingFacade._webSocket, webSocket);
		
		tearDown();
	});
	
	test('_webSocket.fireMessage_when_messageWasFired_then_messageHasToHaveArrivedAtTheCallback', function(){
		expect(2);
		
		// when
		const event = {};
		
		MessagingFacade._webSocketFactory = MockWebSocketFactory.getInstance();
		MessagingFacade.webSocketURL = 'url';
		
		MessagingFacade.addOnMessageCallback(createCallback());

		// then
		MessagingFacade._webSocket.onmessage(event);
		equals(callbackLastEvent, event);
		
		tearDown();
	});
	
});
