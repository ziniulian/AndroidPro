var hwo = undefined;

function init() {
	var hw = HelloWorld();
	// var hw = loadWebApp(webapp());
	hwo = new hw({
		doeNam:"boso",
		txtNam:"txt",
		readBtnNam:"readBtn",
		writeBtnNam: "writeBtn",
		stopBtnNam:"stopBtn",
		clrBtnNam:"clrBtn",
		fileNam:"TestLog.txt",
		css:"Lc_btnScd"
	});
}
