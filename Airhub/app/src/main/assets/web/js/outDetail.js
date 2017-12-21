function init() {
	ol.rid = ol.getUrlReq().rid;
	if (!ol.rid) {
		location.href = ol.constant.url.list;
	} else {
		infoa.href = ol.constant.url.inf + ol.rid;
		scana.href = ol.constant.url.scan + ol.rid;
	}
	ol.user = rfdo.getUser();
	ol.flush();
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

	flush: function () {
		if (!this.busy && this.getAjax()) {
			this.busy = true;
			this.ajx.onreadystatechange = this.hdflush;
			this.ajx.open("POST", srvurl, true);
			this.ajx.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
			var msg = ol.constant.srv.goods + this.rid;
			this.ajx.send(msg);
		}
	},

	hdflush: function (evt) {
		a = evt.target;
		if (a.readyState === 4) {
			if (a.status === 200) {
				var o = JSON.parse(a.responseText);
				if (o.ok) {
					var i, s;
					for (i = 0; i < o.dat.length; i ++) {
						devs.appendChild(ol.crtDom(o.dat[i].fid, o.dat[i].fnam, o.dat[i].mod, o.dat[i].ct, o.dat[i].num));
					}
				}
			}
			ol.busy = false;
		}
	},

	// 创建DOM元素
	crtDom: function (fid, nam, mod, ct, num) {
		var d = document.createElement("div");
			d.setAttribute("fid", fid);
		var m = document.createElement("div");
			if (ct === 0) {
				m.className = "mark stplan";
			} else if (ct < num) {
				m.className = "mark stcover";
			} else {
				m.className = "mark stmatch";
			}
		var t = document.createElement("div");
			t.className = "txt";
			var ts = document.createElement("div");
			ts.className = "mfs";
			ts.innerHTML = nam;
			t.appendChild(ts);
			ts = document.createElement("div");
			ts.className = "tim sfs";
			ts.innerHTML = "型号：" + mod;
			t.appendChild(ts);
			ts = document.createElement("div");
			ts.className = "tim sfs";
			ts.innerHTML = "个数：" + ct + " / " + num;
			t.appendChild(ts);
		d.appendChild(m);
		d.appendChild(t);
		return d;
	}

};
