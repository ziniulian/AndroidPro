var hwo = undefined;

function init() {
	var hw = HelloWorld();
	// var hw = loadWebApp(webapp());
	hwo = new hw.At911n({
		// hd_adr: rfidObj
	});

	hwo.view.initView (document.getElementById("boso"), {
		listOut: "listOut",
		tagOut: "tagOut",

		listDoeo: "list",
		listMod: "mod",
		listCtrlBar: "ctrl",
		listStopBar: "listStopBtn",

		btnCtrlCss: "Lc_btnScd"
	}, {
		scan: "scanBtn",
		clean: "clearBtn",
		listStop: "listStopBtn",

		txt: "txt",
		hx: "hx",
		tim: "tim"
	});
}
