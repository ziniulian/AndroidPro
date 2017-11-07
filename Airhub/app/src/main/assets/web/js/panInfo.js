function init() {
	ol.rid = ol.getUrlReq().rid;
	if (!ol.rid) {
		location.href = ol.constant.url.list;
	} else {
		detaila.href = ol.constant.url.detail + ol.rid;
		scana.href = ol.constant.url.scan + ol.rid;
	}
	ol.user = rfdo.getUser();
	ol.info();
}

ol = {
	busy: false,
	ajx: null,
	rid: null,
	user: null,

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

	info: function () {
		if (!this.busy && this.getAjax()) {
			this.busy = true;
			this.ajx.onreadystatechange = this.hdInfo;
			this.ajx.open("POST", srvurl, true);
			this.ajx.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
			var msg = ol.constant.srv.infR + this.rid;
			this.ajx.send(msg);
		}
	},

	hdInfo: function (evt) {
		a = evt.target;
		if (a.readyState === 4) {
			ol.busy = false;
			if (a.status === 200) {
				var o = JSON.parse(a.responseText);
				if (o.ok) {
					ol.flush(o);
				}
			}
		}
	},

	flush: function (o) {
		ridDom.innerHTML = o.rid;
		personDom.innerHTML += o.person;
		timDom.innerHTML += ol.timStr(o.tim);
		if (o.remark) {
			memoDom.innerHTML = o.remark;
		}
	},

	ok: function () {
		if (!this.busy && this.getAjax()) {
			this.busy = true;
			this.ajx.onreadystatechange = this.hdok;
			this.ajx.open("POST", srvurl, true);
			this.ajx.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
			var msg = ol.constant.srv.okR + this.rid + "&user=" + ol.user;
			this.ajx.send(msg);
		}
	},

	hdok: function (evt) {
		a = evt.target;
		if (a.readyState === 4) {
			ol.busy = false;
			if (a.status === 200) {
				var o = JSON.parse(a.responseText);
				if (o.ok) {
					location.href = ol.constant.url.list;
				}
			}
		}
	}

};
