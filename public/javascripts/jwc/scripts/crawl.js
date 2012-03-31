// load('jwc/scripts/crawl.js')

load('steal/rhino/rhino.js')

steal('steal/html/crawl', function(){
  steal.html.crawl("jwc/jwc.html","jwc/out")
});
