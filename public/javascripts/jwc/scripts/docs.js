//js jwc/scripts/doc.js

load('steal/rhino/rhino.js');
steal("documentjs").then(function(){
	DocumentJS('jwc/jwc.html', {
		markdown : ['jwc']
	});
});