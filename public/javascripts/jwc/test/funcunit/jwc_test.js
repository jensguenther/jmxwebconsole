steal("funcunit", function(){
	module("jwc test", { 
		setup: function(){
			S.open("//jwc/jwc.html");
		}
	});
	
	test("Copy Test", function(){
		equals(S("h1").text(), "Welcome to JavaScriptMVC 3.2!","welcome text");
	});
})