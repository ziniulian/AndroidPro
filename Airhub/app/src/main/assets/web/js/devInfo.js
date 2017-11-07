function init() {
	ol.devid = ol.getUrlReq().devid;
	if (ol.devid) {
		didDom.innerHTML = ol.devid;
		logOut.href = ol.constant.url.out + ol.devid;
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
		var t = new Date(tim);
		return t.getFullYear() + "-" + (t.getMonth() + 1) + "-" + t.getDate() + " " + t.getHours() + ":" + t.getMinutes() + ":" + t.getSeconds();
	},

	flush: function () {
		if (!this.busy && this.getAjax()) {
			this.busy = true;
			this.ajx.onreadystatechange = this.hdflush;
			this.ajx.open("POST", srvurl, true);
			this.ajx.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
			var msg = "srv=getDevInfo&devid=" + this.devid;
			this.ajx.send(msg);
		}
	},

	hdflush: function (evt) {
		a = evt.target;
		if (a.readyState === 4) {
			if (a.status === 200) {
				var o = JSON.parse(a.responseText);
				if (o.ok) {
					ol.flushDom(o);
				}
			}
			ol.busy = false;
		}
	},

	flushDom: function (o) {
		if (o.iscrap) {
			mptDom.innerHTML = "已报废";
		} else if (o.isout) {
			mptDom.innerHTML = "已出库";
		}
		if (o.nam) {
			nam.innerHTML = o.nam;
		}
		if (o.brand) {
			brand.innerHTML = o.brand;
		}
		if (o.mod) {
			mod.innerHTML = o.mod;
		}
		if (o.sn) {
			sn.innerHTML = o.sn;
		}
		if (o.unit) {
			unit.innerHTML = o.unit;
		}
		price.innerHTML = o.price ? o.price : 0;
		if (o.level) {
			level.innerHTML = o.level;
		}
		if (o.location) {
			location.innerHTML = o.location;
		}
		cont.innerHTML = o.cont ? o.cont : 0;
		if (o.tim) {
			tim.innerHTML = ol.timStr(o.tim);
		}
		if (o.intim) {
			intim.innerHTML = ol.timStr(o.intim);
		}
		if (o.fegularcycle) {
			fegularcycle.innerHTML = o.fegularcycle + " 个月";
		}
		if (o.remark) {
			level.innerHTML = o.remark;
		}
	}

};
