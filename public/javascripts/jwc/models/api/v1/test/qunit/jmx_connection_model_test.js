steal(
		"funcunit/qunit", 
		"jwc/models/api/v1/models/jmx_connection_model.js", function(){
	
	module("Model: jwc.models.api.v1.JMXConnectionModel");
	
	function destroyJMXConnection(connId){
		new jwc.models.api.v1.JMXConnectionModel({id: connId}).destroy();
	}
	
	var errorFunc = function(data){
		ok(false,"error: "+data.responseText);
		start();
	}
	
	test("create", function(){
		expect(3)
		stop();
		// when
		new jwc.models.api.v1.JMXConnectionModel({host: "localhost", port: "9999"})
			.save(function(jmxConnectionModel){
				try {
					// then
					ok(jmxConnectionModel);
					ok(jmxConnectionModel.id);
					equals(jmxConnectionModel.host,"localhost")
					
					// cleanup
					jmxConnectionModel.destroy()
				} finally {
					start();
				}
			}, errorFunc );
	});
	
	test("destroy", function(){
		expect(1);
		stop();
		// when
		new jwc.models.api.v1.JMXConnectionModel({host: "localhost", port: "9999"})
			.save(function(){
				// and
				this.destroy(function(){
					// then
					ok( true ,"Destroy called" )
					start();
				})
			}, errorFunc );
	});
	
	test("findAll", function(){
		expect(4);
		stop();
		// when
		new jwc.models.api.v1.JMXConnectionModel({host: "localhost", port: "9999"})
			.save(function(){
				$.when(this)
				.then(function(){
					// and
					jwc.models.api.v1.JMXConnectionModel.findAll(null, function(items){
						try {
							// then
							ok(items.length != 0);
							ok(items[0].id);
							ok(items[0].host);
							ok(items[0].port);
						} finally {
							// resume other tests
							start();
							// cleanup
							destroyJMXConnection(items[0].id);
						}
					}, errorFunc);
			    });
			}, errorFunc);
	});
	
	test("findOne", function(){
		expect(2);
		stop();
		// when
		new jwc.models.api.v1.JMXConnectionModel({host: "localhost", port: "9999"})
			.save(function(){
				$.when(this)
				.then(function(jmxConnectionModel){
					// and
					jwc.models.api.v1.JMXConnectionModel.findOne({id : jmxConnectionModel.id},
						function(jmxConnectionModel){
							try {
								// then
								ok(jmxConnectionModel);
								ok(jmxConnectionModel.id);
							} finally {
								// resume other tests
								start();
								// cleanup
								destroyJMXConnection(jmxConnectionModel.id);
							}
						}, errorFunc);
			    });
			}, errorFunc);
	});
	
	test("getDomains", function(){
		expect(2);
		stop();
		// when
		new jwc.models.api.v1.JMXConnectionModel({host: "localhost", port: "9999"})
		.save(function(){
			$.when(this)
			.then(function(jmxConnectionModel){
				// and
				jmxConnectionModel.getDomains(function(domains){
					try {
						// then
						ok(domains);
						ok(domains.length > 0);
					} finally {
						// resume other tests
						start();
						// cleanup
						destroyJMXConnection(jmxConnectionModel.id);
					}
				}, errorFunc);
			 });
		}, errorFunc);
	});
})