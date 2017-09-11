function init() {
	var o = dat.getUrlReq();
	var s;
	switch (o.typ - 0) {
		case 1:
			s = "小单明细：<br>";
			break;
		case 2:
			s = "被套明细：<br>";
			break;
		case 3:
			s = "枕套明细：<br>";
			break;
		default:
			return;
	}
	var a = JSON.parse(rfid.findDetails(o.tim, o.typ));
	if (a.length) {
		for (var i = 0; i < a.length; i ++) {
			s += a[i] + "<br>";
		}
	} else {
		s += "(暂无记录)";
	}
	boso.innerHTML = s;
}

dat = {
	getUrlReq: function () {
		var url = location.search;
		var theRequest = {};
		if (url.indexOf("?") != -1) {
			url = url.substr(1).split("&");
			for(var i = 0; i < url.length; i ++) {
				var str = url[i].split("=");
				theRequest[str[0]] = decodeURIComponent(str[1]);
			}
		}
		return theRequest;
	}
};
