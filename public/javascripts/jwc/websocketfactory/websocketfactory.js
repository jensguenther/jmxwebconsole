steal('jquery/class', function(){

var INSTANCE = null;

$.Class('WebSocketFactory', 
	/* static */
	{
		getInstance : function(){
			if(!INSTANCE){
				INSTANCE = new WebSocketFactory();
			}
			return INSTANCE;
		}
	},
	/* prototype */
	{	
		createWebSocket : function(url){
			return new WebSocket(url);
		}
	}
);
});