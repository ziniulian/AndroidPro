function init() {
	appnam.innerHTML = rfid.getAppNam();
	if (dat.getUrlReq().read) {
		rfid.read();
	}
}

rfid.hdRead = function (tag) {
	var t = encodeURIComponent(JSON.stringify(tag));
	var u;
	switch (tag.pro.src) {
		case "T":
		case "Q":
		case "!":
			u = "infoT.html?tag=";
			break;
		case "J":
			u = "infoJ.html?tag=";
			break;
		case "D":
			u = "infoD.html?tag=";
			break;
		case "K":
			u = "infoK.html?tag=";
			break;
		default:
			u = "infoUn.html?tag=";
			break;
	}
	location.href = u + t;
};

dat = {

	crtAjax: function () {
		var xmlHttp = null;
		try{
			xmlHttp = new XMLHttpRequest();
		} catch (MSIEx) {
			var activeX = [ "MSXML2.XMLHTTP.3.0", "MSXML2.XMLHTTP", "Microsoft.XMLHTTP" ];
			for (var i=0; i < activeX.length; i++) {
				try {
					xmlHttp = new ActiveXObject( activeX[i] );
				} catch (e) {}
			}
		}
		return xmlHttp;
	},

	getAjax: function () {
		if (!this.ajx) {
			this.ajx = this.crtAjax();
			if (!this.ajx) {
				return false;
			}
		}
		return true;
	},

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
	},

	reading: function () {
		content.innerHTML = "正在读取电子标签......";
	},

	readNull: function () {
		content.innerHTML = "读取电子标签超时......";
	},

	// 退出页面
	back: function () {
		location.href = "home.html";
	}

};
