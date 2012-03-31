steal(
		"funcunit/qunit",
		"jquery/dom/fixture",
		"jwc/websocketfactory/mockwebsocketfactory.js",
		"jwc/models/api/v1/models/jmx_connection_model.js",
		"jwc/models/api/v1/models/mbean_attribute_model.js",
		"jwc/models/api/v1/models/mbean_attribute_list_model.js",function(){
	
	module("Model: jwc.models.api.v1.MBeanAttributeListModel");
	
	function destroyJMXConnection(connId){
		new jwc.models.api.v1.JMXConnectionModel({id: connId}).destroy();
	}
	
	const errorFunc = function(data){
		ok(false,"error: "+data.responseText);
		start();
	}
	
	test("fullName", function(){
		expect(2);
		
		// when
		const o = new jwc.models.api.v1.MBeanAttributeListModel();
		
		// then
		ok(o.Class);
		ok(o.Class.fullName == 'jwc.models.api.v1.MBeanAttributeListModel');
	});
	
	test("findOne", function(){
		// setting up fixtures ------------------------------------------------------------------------------
		const fixtureMethod = 'GET /api/v1/jmxconnection/{connId}/mbean/{mBeanId}/attributes';
		$.fixture(fixtureMethod, function(orig){
			// it's expected an array of MBeanAttributeModels will be returned.
			return [[
			    {
			    	connId : orig.data.connId, 
			    	mBeanId : orig.data.mBeanId,
			    	name : 'name1',
			    	value : 'val1'
			    },{
			    	connId : orig.data.connId, 
			    	mBeanId : orig.data.mBeanId,
			    	name : 'name2',
			    	value : 'val2'
				}]];
		})
		
		// test control -------------------------------------------------------------------------------------
		expect(4);
		stop();
		
		// when
		jwc.models.api.v1.MBeanAttributeListModel.findOne({
			connId : 'connId',
			mBeanId : 'mBeanId'
		}, function(item){
			try { 
				// then
				ok(item.connId);
				ok(item.mBeanId);
				ok(item.attributes);
				ok(item.attributes.length == 2);
			} finally {
				// resume other tests
				start();
				// cleanup
				$.fixture(fixtureMethod, null);
			}
		}, errorFunc);
	});
	
	test("constructor", function(){
		expect(5);
		
		// when
		const connId = 'test.constructor.connId';
		const mBeanId = 'test.constructor.mBeanId';
		const attributes = [];
		const autoRefresh = false;
		
		var model = new jwc.models.api.v1.MBeanAttributeListModel({
			connId : connId,
			mBeanId : mBeanId,
			attributes : attributes,
			autoRefresh : autoRefresh
		})
		
		// then
		equals(connId, model.connId);
		equals(mBeanId, model.mBeanId);
		equals(attributes, model.attributes);
		equals(autoRefresh, model.autoRefresh);
		equals(null, model._autoRefreshWebSocket);
	});
	
	test("autoRefresh: autoRefresh is false and no socket running", function(){
		expect(1);
		
		// when
		var model = new jwc.models.api.v1.MBeanAttributeListModel({
			connId : 'test.constructor.connId',
			mBeanId : 'test.constructor.mBeanId',
			attributes : [],
			autoRefresh : false
		})
		
		// and
		model.update({autoRefresh: false});
		
		// then
		equals(null, model._autoRefreshWebSocket);		
	});
	
	test("autoRefresh: autoRefresh is true and no socket running", function(){
		expect(1);
		
		// when
		var model = new jwc.models.api.v1.MBeanAttributeListModel({
			connId : 'test.constructor.connId',
			mBeanId : 'test.constructor.mBeanId',
			attributes : [],
			autoRefresh : false
		})
		
		// and
		model.update({autoRefresh: true});
		
		// then
		ok(model._autoRefreshWebSocket);		
	});
	
	test("autoRefresh: autoRefresh is true and socket is running", function(){
		expect(1);
		
		// when
		var model = new jwc.models.api.v1.MBeanAttributeListModel({
			connId : 'test.constructor.connId',
			mBeanId : 'test.constructor.mBeanId',
			attributes : [],
			autoRefresh : true
		})
		// and
		var socket = model._autoRefreshWebSocket;
		// and
		model.update({autoRefresh: true});
		
		// then
		equals(socket, model._autoRefreshWebSocket);
	});
	
	test("autoRefresh: autoRefresh is false and socket is running", function(){
		expect(1);
		
		// when
		var model = new jwc.models.api.v1.MBeanAttributeListModel({
			connId : 'test.constructor.connId',
			mBeanId : 'test.constructor.mBeanId',
			attributes : [],
			autoRefresh : true
		})
		
		// and
		model.update({autoRefresh: false});
		
		// then
		equals(null, model._autoRefreshWebSocket);		
	});
	
	test("autoRefresh: attribute update", function(){
		expect(1);
		
		// setting MockWebSocketFactory instance
		jwc.models.api.v1.MBeanAttributeListModel._webSocketFactory = MockWebSocketFactory.getInstance();
			
		// when
		var model = new jwc.models.api.v1.MBeanAttributeListModel({
			connId : 'test.constructor.connId',
			mBeanId : 'test.constructor.mBeanId',
			attributes : [],
			autoRefresh : true
		})
		// and
		var attributes = "[{}]";
		model._autoRefreshWebSocket.onmessage.call(model._autoRefreshWebSocket,{data:attributes});
		
		// then
		equals($.parseJSON(attributes), model.attributes);
	});
	
	test("getMBeanAttributeModel", function(){
		expect(2);
		
		// when
		var attribute = new jwc.models.api.v1.MBeanAttributeListModel({name : 'a1_name'});
		// and
		var model = new jwc.models.api.v1.MBeanAttributeListModel({
			connId : 'test.getMBeanAttributeModel.connId',
			mBeanId : 'test.getMBeanAttributeModel.mBeanId',
			attributes : [attribute],
			autoRefresh : true
		})
		
		// then
		equals(attribute, model.getMBeanAttributeModel('a1_name'));
		ok(!model.getMBeanAttributeModel('other'));
	});
	
})