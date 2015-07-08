var page = require('webpage').create();
var fs = require('fs'); // File System Module
//var args = system.args;
//var output = './temp_htmls/test1.html'; // path for saving the local file
var system = require('system');
var args = system.args;

var output = args[1];

page.addCookie({
	'aep_usuc_f' : 'site=glo&region=RU&b_locale=en_US&c_tp=RUB'
});

// .. do testing stuff ..
page.open(args[2], function () { // open the file
	setTimeout(function () {
		fs.write(output, page.content, 'w'); // Write the page to the local file using page.content
		phantom.exit(); // exit PhantomJs
	}, 2000);
});
