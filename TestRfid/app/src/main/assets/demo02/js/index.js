var hwo = undefined;

function init() {
	var hw = HelloWorld();
	// var hw = loadWebApp(webapp());
	hwo = new hw.At911n({
		hd_adr: rfidObj
	});

	hwo.view.initView (document.getElementById("boso"), {
		listOut: "listOut",
		tagOut: "tagOut",

		listDoeo: "list",
		listMod: "mod",
		listCtrlBar: "ctrl",
		listStopBar: "listStopBtn",

		ecp: "ecp",
		tid: "tid",
		usr: "usr",
		bck: "bck",
		stopDoeo: "stop",
		writeDoeo: "write",

		chgScd: "chgScd",
		btnCtrlCss: "Lc_btnScd",
		chgCtrlCss: "Lc_btnScd"
	}, {
		scan: "scanBtn",
		clean: "clearBtn",
		listStop: "listStopBtn",

		ecpReadBtn: "ecpReadBtn",
		ecpWriteBtn: "ecpWriteBtn",
		tidReadBtn: "tidReadBtn",
		usrReadBtn: "usrReadBtn",
		usrWriteBtn: "usrWriteBtn",
		bckReadBtn: "bckReadBtn",
		bckWriteBtn: "bckWriteBtn",
		stopBtn: "stopBtn",

		writeTitle: "titl",
		writeOkBtn: "writeOkBtn",
		writeCancleBtn: "writeCancleBtn",

		txt: "txt",
		hx: "hx",
		tim: "tim"
	});
}
