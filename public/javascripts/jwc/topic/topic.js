steal('jquery/class', function(){

	const topics = {};
	
	function getTopic(topicId){
		var topic = topics[topicId];
		if(! topic){
			topic = $.Callbacks('unique');
			topics[topicId] = topic;
		}
		
		return topic;
	}
	
	$.Class('Topic', 
		/* static */
		{
			/**
			 * Subscribes the given function to be called whenever an event on the given topic is fired.
			 * 
			 * @param {String} topicId the topic to subscribe to
			 * @param {Function} callback the callback to call
			 */
			subscribe : function(topicId, callback){
				getTopic(topicId).add(callback);
			},
			
			/**
			 * Unsubscribes the given function from the given topic.
			 * 
			 * @param {String} topicId the topic to unsubscribe from
			 * @param {Function} callback the callback to call
			 */
			unsubscribe : function(topicId, callback){
				getTopic(topicId).remove(callback);
			},
			
			/**
			 * Publishes the given data to the given topic.
			 * 
			 * @param {String} topicId the topic to publish to
			 * @param {data} the data to fire as event
			 */
			publish : function(topicId, data){
				getTopic(topicId).fire(data);
			}
		},
		/* prototype */
		{	
		}
	);
});