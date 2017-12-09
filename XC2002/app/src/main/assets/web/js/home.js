function init() {
	rfid.startTim();
	rfid.flushTim();
	appnam.innerHTML = rfid.getAppNam();
	dat.flashlight(rfid.getFlashlight());
}

dat = {
	// 手电筒
	flashlight: function (b) {
		if (b) {
			fldom.className = "home_block home_block_lightH";
		} else {
			fldom.className = "home_block home_block_light";
		}
	},

	// 还原
	restore: function () {
		exit.className = "Lc_nosee";
		document.title = "Home";
	},

	// 退出页面
	back: function () {
		if (document.title === "Exit") {
			rfid.exit();
		} else {
			document.title = "Exit";
			exit.className = "home_exit mfs";
			setTimeout(dat.restore, 2000);
		}
	}

};
