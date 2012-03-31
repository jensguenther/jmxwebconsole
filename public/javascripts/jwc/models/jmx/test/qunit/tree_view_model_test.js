steal("funcunit/qunit", "jwc/models/jmxbrowser/fixtures", "jwc/models/jmxbrowser/models/tree_view_model.js", function(){
	module("Model: Jwc.Models.Jmxbrowser.TreeViewModel")
	
	test("findAll", function(){
		expect(4);
		stop();
		Jwc.Models.Jmxbrowser.TreeViewModel.findAll({}, function(tree_view_models){
			ok(tree_view_models)
	        ok(tree_view_models.length)
	        ok(tree_view_models[0].name)
	        ok(tree_view_models[0].description)
			start();
		});
		
	})
	
	test("create", function(){
		expect(3)
		stop();
		new Jwc.Models.Jmxbrowser.TreeViewModel({name: "dry cleaning", description: "take to street corner"}).save(function(tree_view_model){
			ok(tree_view_model);
	        ok(tree_view_model.id);
	        equals(tree_view_model.name,"dry cleaning")
	        tree_view_model.destroy()
			start();
		})
	})
	test("update" , function(){
		expect(2);
		stop();
		new Jwc.Models.Jmxbrowser.TreeViewModel({name: "cook dinner", description: "chicken"}).
	            save(function(tree_view_model){
	            	equals(tree_view_model.description,"chicken");
	        		tree_view_model.update({description: "steak"},function(tree_view_model){
	        			equals(tree_view_model.description,"steak");
	        			tree_view_model.destroy();
						start();
	        		})
	            })
	
	});
	test("destroy", function(){
		expect(1);
		stop();
		new Jwc.Models.Jmxbrowser.TreeViewModel({name: "mow grass", description: "use riding mower"}).
	            destroy(function(tree_view_model){
	            	ok( true ,"Destroy called" )
					start();
	            })
	})
})