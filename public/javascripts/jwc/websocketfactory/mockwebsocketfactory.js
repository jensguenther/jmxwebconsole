steal(
		"jquery/class",
		"jwc/websocketfactory",	function(){

var INSTANCE = null;

WebSocketFactory('MockWebSocketFactory', 
	/* static */
	{
		getInstance : function(){
			if(!INSTANCE){
				INSTANCE = new MockWebSocketFactory();
			}
			return INSTANCE;
		}
	},
	/* prototype */
	{	
		createWebSocket : function(url){
			return {};
		}
	}
);
});