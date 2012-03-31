//steal/js jwc/scripts/compress.js

load("steal/rhino/rhino.js");
steal('steal/build').then('steal/build/scripts','steal/build/styles',function(){
	steal.build('jwc/scripts/build.html',{to: 'jwc'});
});
