steal('funcunit').then(function(){

module("Jwc.Widgets.Jmxbrowser.JmxBrowserView", { 
	setup: function(){
		S.open("//jwc/widgets/jmxbrowser/jmx_browser_view/jmx_browser_view.html");
	}
});

test("Text Test", function(){
	equals(S("h1").text(), "Jwc.Widgets.Jmxbrowser.JmxBrowserView Demo","demo text");
});


});