steal('funcunit').then(function(){

module("Jwc.Widgets.Jmxbrowser.InfoView", { 
	setup: function(){
		S.open("//jwc/widgets/jmxbrowser/info_view/info_view.html");
	}
});

test("Text Test", function(){
	equals(S("h1").text(), "Jwc.Widgets.Jmxbrowser.InfoView Demo","demo text");
});


});