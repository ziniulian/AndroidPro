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
			var msg = ol.constant.srv.devs + this.rid;
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
						devs.appendChild(ol.crtDom(o.dat[i].devid, o.dat[i].status, o.dat[i].nam, o.dat[i].mod, o.dat[i].sn));
					}
				}
			}
			ol.busy = false;
		}
	},

	// 创建DOM元素
	crtDom: function (devid, status, nam, mod, sn) {
		var d = document.createElement("div");
			d.setAttribute("devid", devid);
		var m = document.createElement("div");
			switch (status) {
				case 0:
					m.className = "mark stno";
					break;
				// case 1:
				// 	m.className = "mark stmatch";
				// 	break;
				case 2:
					m.className = "mark sttmp";
					break;
				case 3:
					m.className = "mark stplan";
					break;
				default:
					m.className = "mark stmatch";
					break;
			}
		var a = document.createElement("a");
			a.href = ol.constant.url.dev + devid;
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
			ts.innerHTML = "序列号：" + sn;
			t.appendChild(ts);
			a.appendChild(t);
		var o = document.createElement("div");
			o.className = "op sfs";
			switch (status) {
				case 1:
					o.innerHTML = "取消";
					o.onclick = ol.cacl;
					break;
				case 3:
					break;
				// case 0:
				// case 2:
				// 	o.innerHTML = "删除";
				// 	o.onclick = ol.del;
				// 	break;
				default:
					o.innerHTML = "删除";
					o.onclick = ol.del;
					break;
			}
		d.appendChild(m);
		d.appendChild(a);
		d.appendChild(o);
		return d;
	},

	cacl: function (evt) {
		if (!ol.busy && ol.getAjax()) {
			ol.busy = evt.target.parentNode;
			ol.ajx.onreadystatechange = ol.hdcacl;
			ol.ajx.open("POST", srvurl, true);
			ol.ajx.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
			var msg = ol.constant.srv.clr + ol.rid + "&devid=" + ol.busy.getAttribute("devid");
			ol.ajx.send(msg);
		}
	},

	hdcacl: function (evt) {
		var a = evt.target;
		if (a.readyState === 4) {
			if (a.status === 200) {
				var o = JSON.parse(a.responseText);
				if (o.ok) {
					var p = ol.busy;
					var m = p.firstChild;
					var d = p.lastChild;
					m.className = "mark stplan";
					d.innerHTML = "";
					d.onclick = "";
				}
			}
			ol.busy = false;
		}
	},

	del: function (evt) {
		if (!ol.busy && ol.getAjax()) {
			ol.busy = evt.target.parentNode;
			ol.ajx.onreadystatechange = ol.hddel;
			ol.ajx.open("POST", srvurl, true);
			ol.ajx.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
			var msg = ol.constant.srv.del + ol.rid + "&devid=" + ol.busy.getAttribute("devid");
			ol.ajx.send(msg);
		}
	},

	hddel: function (evt) {
		var a = evt.target;
		if (a.readyState === 4) {
			if (a.status === 200) {
				var o = JSON.parse(a.responseText);
				if (o.ok) {
					devs.removeChild(ol.busy);
				}
			}
			ol.busy = false;
		}
	}

};
