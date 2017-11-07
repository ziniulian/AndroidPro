function init() {
	ol.rid = ol.getUrlReq().rid;
	if (!ol.rid) {
		location.href = ol.constant.url.list;
	} else {
		infoa.href = ol.constant.url.inf + ol.rid;
		detaila.href = ol.constant.url.detail + ol.rid;
	}
	ol.user = rfdo.getUser();
	ol.dat();
}

rfid.hdScan = function () {
	var s = rfdo.catchScanning();
	var o = JSON.parse(s);
	var d;
	for (s in o) {
		if (!ol.sdat[s]) {
			d = {
				id: s,
				tim: o[s],
				timDom: document.createElement("div")
			};
			ol.sdat[s] = d;
			if (ol.rdat[s]) {
				// 已经保存在明细中的设备
				d.has = true;
				d.status = ol.rdat[s].status;
				d.nam = ol.rdat[s].nam;
				d.mod = ol.rdat[s].mod;
				d.sn = ol.rdat[s].sn;
				ol.appendDom(d);
			} else {
				// 明细中没有的设备
				d.has = false;
				ol.check(d);
			}
		} else {
			ol.sdat[s].tim += o[s];
			ol.sdat[s].timDom.innerHTML = ol.sdat[s].tim;
		}
	}
};

ol = {
	busy: false,
	ajx: null,
	rid: null,
	user: null,
	rdat: {},
	sdat: {},

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
					var i, s;
					for (i = 0; i < o.dat.length; i ++) {
						s = o.dat[i].devid;
						ol.rdat[s] = o.dat[i];
					}
				}
			}
			ol.busy = false;
		}
	},

	// 创建DOM元素
	crtDom: function (devid, status, has, nam, mod, sn, timDom) {
		var d = document.createElement("div");
			d.setAttribute("devid", devid);
			if (has && status !== 3) {
				d.className = "cover";
			}
		var m = document.createElement("div");
			switch (status) {
				case 0:
					m.className = "mark stno";
					break;
				case 2:
					m.className = "mark sttmp";
					break;
				case 3:
					m.className = "mark stmatch";
					break;
				// case 1:
				// 	m.className = "mark stcover";
				// 	break;
				default:
					m.className = "mark stcover";
					break;
			}
		var a = document.createElement("a");
			if (status !== 0) {
				a.href = ol.constant.url.dev + devid;
			}
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
			timDom.className = "tim sfs";
			t.appendChild(timDom);
			a.appendChild(t);
		var o = document.createElement("div");
			o.className = "op sfs";
			o.innerHTML = "删除";
			o.onclick = ol.del;
		d.appendChild(m);
		d.appendChild(a);
		d.appendChild(o);
		return d;
	},

	appendDom: function (d) {
		if (!d.dom) {
			d.timDom.innerHTML = d.tim;
			d.dom = this.crtDom(d.id, d.status, d.has, d.nam, d.mod, d.sn, d.timDom);
			devs.appendChild(d.dom);
		}

		// 位置调整
		devs.scrollTop = d.dom.offsetTop + d.dom.offsetHeight - devs.clientHeight;
	},

	del: function (evt) {
		var rid = evt.target.parentNode.getAttribute("devid");
		devs.removeChild(ol.sdat[rid].dom);
		delete (ol.sdat[rid]);
	},

	// 清空
	clear: function (evt) {
		devs.innerHTML = "";
		ol.sdat = {};
	},

	check: function (d) {
		var a = this.crtAjax();
		if (a) {
			a.onreadystatechange = this.hdcheck;
			a.open("POST", srvurl, true);
			a.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
			var msg = ol.constant.srv.dev + d.id;
			a.send(msg);
		}
	},

	hdcheck: function (evt) {
		a = evt.target;
		if (a.readyState === 4) {
			if (a.status === 200) {
				var o = JSON.parse(a.responseText);
				var d = ol.sdat[o.devid];
				if (o.ok) {
					d.status = 2;
					d.nam = o.nam;
					d.mod = o.mod;
					d.sn = o.sn;
					ol.appendDom(d);
				} else {
					switch (o.devid.substr(0, 2)) {
						case "JS":
						case "TX":
						case "DH":
						case "QX":
							d.status = 0;
							d.nam = "（未知标签）";
							d.mod = "--";
							d.sn = o.devid;
							ol.appendDom(d);
							break;
						default:
							delete (ol.sdat[o.devid]);
							break;
					}
				}
			}
		}
	},

	// 添加
	add: function () {
		if (!this.busy && this.getAjax()) {
			this.busy = 0;
			var msg, o, s, d0 = "", d2 = "", d3 = "";
			for (s in ol.sdat) {
				o = ol.sdat[s];
				if (o.has) {
					if (o.status === 3) {
						if (d3) {
							d3 += ",";
						} else {
							this.busy ++;
						}
						d3 += s;
					}
				} else {
					/*if (o.status === 0) {
						if (d0) {
							d0 += ",";
						} else {
							this.busy ++;
						}
						d0 += s;
					} else*/ if (o.status === 2) {
						if (d2) {
							d2 += ",";
						} else {
							this.busy ++;
						}
						d2 += s;
					}
				}
			}

			if (d3) {
				var a3 = this.crtAjax();
				if (a3) {
					a3.onreadystatechange = this.hdd3;
					a3.open("POST", srvurl, true);
					a3.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
					msg = ol.constant.srv.mark + ol.rid + "&devid=" + d3 + "&user=" + ol.user;
					a3.send(msg);
				}
			}
			if (d2) {
				var a2 = this.crtAjax();
				if (a2) {
					a2.onreadystatechange = this.hdd2;
					a2.open("POST", srvurl, true);
					a2.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
					msg = ol.constant.srv.crt + ol.rid + "&devid=" + d2 + "&user=" + ol.user + "&status=2";
					a2.send(msg);
				}
			}/*
			if (d0) {
				var a0 = this.crtAjax();
				if (a0) {
					a0.onreadystatechange = this.hdd0;
					a0.open("POST", srvurl, true);
					a0.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
					msg = ol.constant.srv.crt + ol.rid + "&devid=" + d0 + "&user=" + ol.user + "&status=0";
					a0.send(msg);
				}
			}*/
			if (!this.busy) {
				location.href = ol.constant.url.detail + this.rid;
			}
		}
	},

	hdd3: function (evt) {
		a = evt.target;
		if (a.readyState === 4) {
			if (a.status === 200) {
				var o = JSON.parse(a.responseText);
				if (o.ok) {
					//
				}
				ol.busy --;
				if (!ol.busy) {
					location.href = ol.constant.url.detail + ol.rid;
				}
			}
		}
	},

	hdd2: function (evt) {
		a = evt.target;
		if (a.readyState === 4) {
			if (a.status === 200) {
				var o = JSON.parse(a.responseText);
				if (o.ok) {
					//
				}
				ol.busy --;
				if (!ol.busy) {
					location.href = ol.constant.url.detail + ol.rid;
				}
			}
		}
	},

	hdd0: function (evt) {
		a = evt.target;
		if (a.readyState === 4) {
			if (a.status === 200) {
				var o = JSON.parse(a.responseText);
				if (o.ok) {
					//
				}
				ol.busy --;
				if (!ol.busy) {
					location.href = ol.constant.url.detail + ol.rid;
				}
			}
		}
	}

};
