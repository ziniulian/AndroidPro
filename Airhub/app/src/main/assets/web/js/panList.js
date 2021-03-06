function init() {
	ol.user = rfdo.getUser();
	ol.flush();
}

ol = {
	busy: false,
	ajx: null,
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

	flush: function () {
		if (!this.busy && this.getAjax()) {
			this.busy = true;
			this.ajx.onreadystatechange = this.hdflush;
			this.ajx.open("POST", srvurl, true);
			this.ajx.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
			var msg = ol.constant.srv.allR;
			this.ajx.send(msg);
		}
	},

	hdflush: function (evt) {
		a = evt.target;
		if (a.readyState === 4) {
			if (a.status === 200) {
				var o = JSON.parse(a.responseText);
				if (o.ok) {
					var i, s, d = "";
					ols.innerHTML = "";
					for (i = 0; i < o.dat.length; i ++) {
						s = o.dat[i].rid;
						if (s) {
							ols.appendChild(ol.crtDom(s));
						}
					}
				}
			}
			ol.busy = false;
		}
	},

	// 创建DOM元素
	crtDom: function (rid) {
		var d = document.createElement("div");
		d.setAttribute("rid", rid);
		var m = document.createElement("div");
		m.className = "mark";
		var t = document.createElement("div");
		t.className = "txt mfs";
		t.innerHTML = "<a href=\"" + ol.constant.url.inf + rid + "\">" + rid + "</a>";
		var o = document.createElement("div");
		o.className = "op sfs";
		o.innerHTML = "提交";
		o.onclick = ol.ok;
		d.appendChild(m);
		d.appendChild(t);
		d.appendChild(o);
		return d;
	},

	ok: function (evt) {
		if (!ol.busy && ol.getAjax()) {
			ol.busy = evt.target.parentNode;
			ol.ajx.onreadystatechange = ol.hdok;
			ol.ajx.open("POST", srvurl, true);
			ol.ajx.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
			var msg = ol.constant.srv.okR + ol.busy.getAttribute("rid") + "&user=" + ol.user;
			ol.ajx.send(msg);
		}
	},

	hdok: function (evt) {
		a = evt.target;
		if (a.readyState === 4) {
			if (a.status === 200) {
				var o = JSON.parse(a.responseText);
				if (o.ok) {
					ols.removeChild(ol.busy);
				}
			}
			ol.busy = false;
		}
	}

};
