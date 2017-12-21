function init() {
	rfid.flushTim();
	content.innerHTML = "产品型号：" + rfid.getAppNam().substr(0, 10) + "<br />公司名称：远望谷<br />软件版本：V0.2.5<br />RFID基带版本：V0.0.0";
}

dat = {
	// 退出页面
	back: function () {
		location.href = "home.html"
	}
};
