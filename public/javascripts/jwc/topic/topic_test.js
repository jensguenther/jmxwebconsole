steal("jwc/topic")  
 .then('funcunit/qunit').then(function(){

	module("jwc/topic");

	test('publish_withOneSubscriber', function(){
		expect(1);
		
		var dataObj = {};
		
		var cb = function(data){
			equals(dataObj, data);
		}
		
		Topic.subscribe('topic', cb);
		Topic.publish('topic', dataObj);
		
		Topic.unsubscribe('topic', cb);
	});

	test('publish_withTwoSubscribers', function(){
		expect(2);
		
		var cb1 = function(data){
			ok(true);
		}
		
		var cb2 = function(data){
			ok(true);
		}
		
		Topic.subscribe('topic', cb1);
		Topic.subscribe('topic', cb2);
		Topic.publish('topic', {});
		
		Topic.unsubscribe('topic', cb1);
		Topic.unsubscribe('topic', cb2);
	});
	
	test('subscribe_withTwoTopicsAndPublishToOne', function(){
		expect(1);
		
		var cb1 = function(data){
			ok(true);
		}
		
		var cb2 = function(data){
			ok(true);
		}
		
		Topic.subscribe('topic1', cb1);
		Topic.subscribe('topic2', cb2);
		Topic.publish('topic1', {});
		
		Topic.unsubscribe('topic1', cb1);
		Topic.unsubscribe('topic2', cb2);
	});
	
	test('unsubscribe', function(){
		expect(0);
		
		var cb = function(data){
			ok(true);
		}
		
		Topic.subscribe('topic', cb);
		Topic.unsubscribe('topic', cb);
		Topic.publish('topic', {});
	});
});
