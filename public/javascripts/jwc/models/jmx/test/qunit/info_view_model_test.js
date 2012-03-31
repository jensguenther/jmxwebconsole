steal("funcunit/qunit", "jwc/models/jmxbrowser/fixtures", "jwc/models/jmxbrowser/models/info_view_model.js", function(){
	module("Model: Jwc.Models.Jmxbrowser.InfoViewModel")
	
	test("setKey", function(){
		expect(4);
		stop();
		Jwc.Models.Jmxbrowser.InfoViewModel.findAll({}, function(info_view_models){
			ok(info_view_models)
	        ok(info_view_models.length)
	        ok(info_view_models[0].name)
	        ok(info_view_models[0].description)
			start();
		});
		
	})
	
	test("create", function(){
		expect(3)
		stop();
		new Jwc.Models.Jmxbrowser.InfoViewModel({name: "dry cleaning", description: "take to street corner"}).save(function(info_view_model){
			ok(info_view_model);
	        ok(info_view_model.id);
	        equals(info_view_model.name,"dry cleaning")
	        info_view_model.destroy()
			start();
		})
	})
	test("update" , function(){
		expect(2);
		stop();
		new Jwc.Models.Jmxbrowser.InfoViewModel({name: "cook dinner", description: "chicken"}).
	            save(function(info_view_model){
	            	equals(info_view_model.description,"chicken");
	        		info_view_model.update({description: "steak"},function(info_view_model){
	        			equals(info_view_model.description,"steak");
	        			info_view_model.destroy();
						start();
	        		})
	            })
	
	});
	test("destroy", function(){
		expect(1);
		stop();
		new Jwc.Models.Jmxbrowser.InfoViewModel({name: "mow grass", description: "use riding mower"}).
	            destroy(function(info_view_model){
	            	ok( true ,"Destroy called" )
					start();
	            })
	})
})