function init() {
	appnam.innerHTML = rfid.getAppNam();
	dat.flashlight(rfid.getFlashlight());
}

dat = {
	// 手电筒
	flashlight: function (b) {
		if (b) {
			fldom.className = "flashlight";
		} else {
			fldom.className = "";
		}
	},

	// 显示对话框
	showDialog: function () {
		dialog.className = "dialog midOut";
	},

	// 关闭对话框
	hidDialog: function () {
		dialog.className = "Lc_nosee";
	},

	// 清空数据
	clear: function () {
		rfid.dbClear();
		dat.hidDialog();
	},

	// 还原
	restore: function () {
		exit.className = "Lc_nosee";
		document.title = "Home";
	},

	// 退出页面
	back: function () {
		document.title = "Exit";
		exit.className = "exit sfs";
		setTimeout(dat.restore, 2000);
	}

};
