steal(
		"funcunit/qunit",
		"jwc/models/api/v1/models/jmx_connection_model.js",
		"jwc/models/api/v1/models/mbean_attribute_model.js", function(){
	
	module("Model: jwc.models.api.v1.MBeanAttributeModel");
	
	function destroyJMXConnection(connId){
		new jwc.models.api.v1.JMXConnectionModel({id: connId}).destroy();
	}
	
	const errorFunc = function(data){
		ok(false,"error: "+data.responseText);
		start();
	}
	
	
	test("findAll", function(){
		expect(5);
		stop();
		// when (need to create a new JMXConnection first)
		new jwc.models.api.v1.JMXConnectionModel({host: "localhost", port: "9999"})
			.save(function(){
				$.when(this)
				.then(function(jmxConnectionModel){
					// and
					jwc.models.api.v1.MBeanAttributeModel.findAll({
						connId : jmxConnectionModel.id,
						mBeanId : 'Sk1JbXBsZW1lbnRhdGlvbjp0eXBlPU1CZWFuU2VydmVyRGVsZWdhdGU=' // JMImplementation:type=MBeanServerDelegate
					}, function(items){
						try { 
							// then
							ok(items.length != 0);
							ok(items[0].connId);
							ok(items[0].mBeanId);
							ok(items[0].name);
							ok(items[0].value);
						} finally {
							// resume other tests
							start();
							// cleanup
							destroyJMXConnection(items[0].connId);
						}
					}, errorFunc);
			    });
			}, errorFunc);
	});
})