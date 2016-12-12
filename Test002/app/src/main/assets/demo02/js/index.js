
function init() {
	var hw = HelloWorld();
	// var hw = loadWebApp(webapp());
	var hwo = new hw({
		doeNam:"boso",
		txtNam:"txt",
		hqBtnNam:"hqBtn",
		weatherBtnNam:"weatherBtn",
		clrBtnNam:"clrBtn",
		// hqUrl:"http://hq.sinajs.cn/list=s_sz002161",
		// weatherUrl:"http://www.weather.com.cn/data/sk/101160101.html",
		hqUrl:"https://raw.githubusercontent.com/ziniulian/LZR_JS/dev/test/startServer.bat",
		weatherUrl:"https://raw.githubusercontent.com/ziniulian/LZR_JS/dev/.gitignore",
		fileNam:"TestLog.txt",
		css:"Lc_btnScd"
	});
}
