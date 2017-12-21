function init() {
	ol.rid = ol.getUrlReq().rid;
	if (!ol.rid) {
		location.href = ol.constant.url.list;
	} else {
		infoa.href = ol.constant.url.inf + ol.rid;
		detaila.href = ol.constant.url.detail + ol.rid;
	}
	ol.user = rfdo.getUser();
	// ol.user = "lzr";	// 测试用
	ol.goods();

	// setTimeout (rfid.hdScan, 1000);	//测试用
}

rfid.hdScan = function () {
	var s = rfdo.catchScanning();
	// var s = "{\"TX02010300001\":1,\"TX02010300003\":2}";	// 测试用
	var o = JSON.parse(s);
	var d;
	for (s in o) {
		if (!ol.sdat[s]) {
			ol.check(s, o[s]);
		} else {
			ol.show(ol.sdat[s], o[s]);
		}
	}
};

ol = {
	busy: false,
	ajx: null,
	rid: null,
	user: null,
	sdat: {},
	gd: {},
	ga: [],

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

	// 获取商品信息
	goods: function () {
		if (!ol.busy && ol.getAjax()) {
			ol.busy = true;
			ol.ajx.onreadystatechange = ol.hdgoods;
			ol.ajx.open("POST", srvurl, true);
			ol.ajx.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
			var msg = ol.constant.srv.goods + ol.rid;
			ol.ajx.send(msg);
		}
	},

	hdgoods: function (evt) {
		a = evt.target;
		if (a.readyState === 4) {
			if (a.status === 200) {
				var o = JSON.parse(a.responseText);
				if (o.ok) {
					ol.ga = o.dat;
					var i, g;
					for (i = 0; i < ol.ga.length; i ++) {
						g = ol.ga[i];
						ol.gd[g.fid] = g;
						g.dom = document.createElement("div");
						g.devs = [];
						devs.appendChild(g.dom);
					}
				}

				ol.gd["+"] = {
					dom: document.createElement("div"),
					devs: []
				};
				devs.appendChild(ol.gd["+"].dom);
				ol.gd["-"] = {
					dom: document.createElement("div"),
					devs: []
				};
				devs.appendChild(ol.gd["-"].dom);
			}
			ol.busy = false;
			if (ol.ga.length) {
				ol.dat();
			}
		}
	},

	dat: function () {
		if (!this.busy && this.getAjax()) {
			this.busy = true;
			this.ajx.onreadystatechange = this.hddat;
			this.ajx.open("POST", srvurl, true);
			this.ajx.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
			var msg = ol.constant.srv.devs + this.rid;
			this.ajx.send(msg);
		}
	},

	hddat: function (evt) {
		a = evt.target;
		if (a.readyState === 4) {
			if (a.status === 200) {
				var o = JSON.parse(a.responseText);
				if (o.ok) {
					var i, d;
					for (i = 0; i < o.dat.length; i ++) {
						d = o.dat[i];
						ol.crtObj(d.devid, d.nam, d.mod, d.sn, true, d.fid);
					}
				}
			}
			ol.busy = false;
		}
	},

	// 创建一个设备对象
	crtObj: function (devid, nam, mod, sn, has, fid) {
		var o = {
			id: devid,
			has: has,
			fid: ol.gd[fid],
			tim: 0,
			visi: false,
			dom: document.createElement("div"),
			markDom: document.createElement("div"),
			timDom: document.createElement("div")
		};
		if (fid === "+") {
			o.markDom.className = "mark sttmp";
		} else if (fid === "-") {
			o.markDom.className = "mark stno";
		} else {
			if (o.fid.ct > o.fid.num) {
				o.markDom.className = "mark stplan";
			} else if (o.fid.ct < o.fid.num) {
				o.markDom.className = "mark stcover";
			} else {
				o.markDom.className = "mark stmatch";
			}
		}
		ol.crtDom(o, nam, mod, sn);
		o.fid.devs.push(o);
		ol.sdat[devid] = o;
		return o;
	},

	// 创建DOM元素
	crtDom: function (o, nam, mod, sn) {
		if (o.has) {
			o.dom.className = "cover";
		}
		var a = document.createElement("a");
			// if (status !== 0) {
			// 	a.href = ol.constant.url.dev + devid;
			// }
			var t = document.createElement("div");
			t.className = "txt mfs";
			var ts = document.createElement("div");
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
			o.timDom.className = "tim sfs";
			t.appendChild(o.timDom);
			a.appendChild(t);
		var op = document.createElement("div");
			op.className = "op sfs";
			op.innerHTML = "删除";
			op.onclick = function (e) {
				ol.del(o);
			};
		o.dom.appendChild(o.markDom);
		o.dom.appendChild(a);
		o.dom.appendChild(op);
	},

	// 显示对象
	show: function (o, tim) {
		if (!o.visi) {
			o.fid.dom.appendChild(o.dom);
			o.visi = true;
			if (o.fid.fid && !o.has) {
				var i;
				o.fid.ct ++;
				if (o.fid.ct === o.fid.num) {
					for (i = 0; i < o.fid.devs.length; i ++) {
						o.fid.devs[i].markDom.className = "mark stmatch";
					}
				} else if (o.fid.ct === (o.fid.num + 1)) {
					for (i = 0; i < o.fid.devs.length; i ++) {
						o.fid.devs[i].markDom.className = "mark stplan";
					}
				}
			}
		}
		o.tim += tim;
		o.timDom.innerHTML = o.tim;
	},

	// 隐藏对象
	del: function (o) {
		if (o.visi) {
			o.tim = 0;
			o.visi = false;
			o.fid.dom.removeChild(o.dom);
			if (o.fid.fid && !o.has) {
				var i;
				o.fid.ct --;
				if (o.fid.ct === o.fid.num) {
					for (i = 0; i < o.fid.devs.length; i ++) {
						o.fid.devs[i].markDom.className = "mark stmatch";
					}
				} else if (o.fid.ct === (o.fid.num - 1)) {
					for (i = 0; i < o.fid.devs.length; i ++) {
						o.fid.devs[i].markDom.className = "mark stcover";
					}
				}
			}
		}
	},

	check: function (id, tim) {
		var a = this.crtAjax();
		if (a) {
			a.tim = tim;
			a.onreadystatechange = this.hdcheck;
			a.open("POST", srvurl, true);
			a.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
			var msg = ol.constant.srv.dev + id;
			a.send(msg);
		}
	},

	hdcheck: function (evt) {
		var a = evt.target;
		var tim = a.tim;
		if (a.readyState === 4) {
			if (a.status === 200) {
				var o = JSON.parse(a.responseText);
				var d = ol.sdat[o.devid];
				var id = o.devid;
				if (o.ok) {
					for (var i = 0; i < ol.ga.length; i ++) {
						if (id.indexOf(ol.ga[i].fid) === 0) {
							ol.show(ol.crtObj(id, o.nam, o.mod, o.sn, false, ol.ga[i].fid), tim);
							return;
						}
					}
					ol.show(ol.crtObj(id, o.nam, o.mod, o.sn, false, "+"), tim);
				} else {
					switch (o.devid.substr(0, 2)) {
						case "JS":
						case "TX":
						case "DH":
						case "QX":
							ol.show(ol.crtObj(id, "（未知标签）", "-", id, false, "-"), tim);
							break;
					}
				}
			}
		}
	},

	add: function () {
		if (!ol.busy && ol.getAjax()) {
			var i, j, f, d;
			var r = "";
			for (i = 0; i < ol.ga.length; i ++) {
				f = ol.ga[i];
				if (f.ct > f.num) {
					dialog.className = "dialog";
					return;
				} else {
					for (j = 0; j < f.devs.length; j ++) {
						d = f.devs[j];
						if (d.visi && !d.has) {
							r += d.id + "," + f.fid + ";";
						}
					}
				}
			}
			if (r) {
				ol.busy = true;
				ol.ajx.onreadystatechange = ol.hddadd;
				ol.ajx.open("POST", srvurl, true);
				ol.ajx.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
				var msg = ol.constant.srv.crt + ol.rid + "&devid=" + r.substr(0, (r.length - 1));
				ol.ajx.send(msg);
			} else {
				location.href = ol.constant.url.detail + ol.rid;
			}
		}
	},

	hddadd: function (evt) {
		a = evt.target;
		if (a.readyState === 4) {
			if (a.status === 200) {
				var o = JSON.parse(a.responseText);
				if (o.ok) {
					location.href = ol.constant.url.detail + ol.rid;
				}
			}
		}
	}

};
