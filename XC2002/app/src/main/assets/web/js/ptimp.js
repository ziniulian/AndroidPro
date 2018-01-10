function init() {
	rfid.flushTim();
	content.innerHTML = "<br />当前系统时间为：<br /><span>" + rfid.getTim() + "</span><br />请注意确认时间是否正确！";
}

dat = {
	// 退出页面
	back: function () {
		location.href = "home.html"
	}
};
