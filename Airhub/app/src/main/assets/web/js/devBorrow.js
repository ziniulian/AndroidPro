function init() {
	ol.devid = ol.getUrlReq().devid;
	if (ol.devid) {
		didDom.innerHTML = ol.devid;
		devInf.href = ol.constant.url.dev + ol.devid;
		ol.flush();
	}
}

ol = {
	busy: false,
	ajx: null,
	devid: null,

	getAjax: function ()/*as:Object*/ {
		if (!this.ajx) {
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

			if (xmlHttp) {
				this.ajx = xmlHttp;
			} else {
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

	timStr: function (tim) {
		if (tim) {
			var t = new Date(tim);
			return t.getFullYear() + "-" + (t.getMonth() + 1) + "-" + t.getDate() + " " + t.getHours() + ":" + t.getMinutes() + ":" + t.getSeconds();
		} else {
			return "";
		}
	},

	flush: function () {
		if (!this.busy && this.getAjax()) {
			this.busy = true;
			this.ajx.onreadystatechange = this.hdflush;
			this.ajx.open("POST", srvurl, true);
			this.ajx.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
			var msg = "srv=getBorrowByDev&devid=" + this.devid;
			this.ajx.send(msg);
		}
	},

	hdflush: function (evt) {
		a = evt.target;
		if (a.readyState === 4) {
			if (a.status === 200) {
				var o = JSON.parse(a.responseText);
				if (o.ok) {
					for (var i = 0; i < o.dat.length; i ++) {
						ol.flushDom(o.dat[i]);
					}
				}
			}
			ol.busy = false;
		}
	},

	flushDom: function (o) {
		var d = document.createElement("div");
		d.className = "pan_info_title sfs";
		var s = "领用时间：" + ol.timStr(o.tim) + "</br>";
		s += "单位名称：" + o.dep + "</br>";
		s += "领用人：" + o.person + "</br>";
		s += "结束日期：" + ol.timStr(o.comtim);
		d.innerHTML = s;
		devs.appendChild(d);
	}

};
