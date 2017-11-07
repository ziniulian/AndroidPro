function init() {
	dat.minTim = rfid.getSynTim();
	dat.getTim();
}

dat = {
	busy: true,
	cmp: 4,
	minTim: 0,
	maxTim: 0,

	getAjax: function ()/*as:Object*/ {
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

	// 获取服务器时间
	getTim: function () {
		var ajx = this.getAjax();
		if (ajx) {
			ajx.onreadystatechange = this.hdGetTim;
			ajx.open("POST", srvurl, true);
			ajx.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
			var msg = "srv=getTim";
			ajx.send(msg);
		}
	},

	hdGetTim: function (evt) {
		var a = evt.target;
		if (a.readyState === 4) {
			if (a.status === 200) {
				var o = JSON.parse(a.responseText);
				if (o.ok) {
					dat.maxTim = o.timeStamp;
					dat.devIn();
					// dat.devUp();
					// dat.outIn();
					// dat.outUp();
				}
			}
		}
	},

	// 库存新增
	devIn: function () {
		var ajx = this.getAjax();
		if (ajx) {
			ajx.onreadystatechange = this.hdDevIn;
			ajx.open("POST", srvurl, true);
			ajx.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
			var msg = "srv=getXzTable&min=" + this.minTim + "&max=" + this.maxTim;
			ajx.send(msg);
		}
	},

	hdDevIn: function (evt) {
		var a = evt.target;
		if (a.readyState === 4) {
			if (a.status === 200) {
				var s = "库存新增 ： ";
				var o = JSON.parse(a.responseText);
				if (o.ok) {
					var n = o.dat.length;
					for (var i = 0; i < n; i ++) {
						devInDom.innerHTML = s + (i + 1) + " / " + n;
						rfid.dbSynDevIn(o.dat[i]);
					}
				} else {
					devInDom.innerHTML = s + "0 / 0";
				}
			}
			devInDom.innerHTML += " , 完成！";
			dat.cmpNext();

			dat.devUp();
		}
	},

	// 库存修改
	devUp: function () {
		var ajx = this.getAjax();
		if (ajx) {
			ajx.onreadystatechange = this.hdDevUp;
			ajx.open("POST", srvurl, true);
			ajx.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
			var msg = "srv=getXgTable&min=" + this.minTim + "&max=" + this.maxTim;
			ajx.send(msg);
		}
	},

	hdDevUp: function (evt) {
		var a = evt.target;
		if (a.readyState === 4) {
			if (a.status === 200) {
				var s = "库存修改 ： ";
				var o = JSON.parse(a.responseText);
				if (o.ok) {
					var n = o.dat.length;
					for (var i = 0; i < n; i ++) {
						devUpDom.innerHTML = s + (i + 1) + " / " + n;
						rfid.dbSynDevUp(o.dat[i].split(":"));
					}
				} else {
					devUpDom.innerHTML = s + "0 / 0";
				}
			}
			devUpDom.innerHTML += " , 完成！";
			dat.cmpNext();

			dat.outIn();
		}
	},

	// 领用记录新增
	outIn: function () {
		var ajx = this.getAjax();
		if (ajx) {
			ajx.onreadystatechange = this.hdOutIn;
			ajx.open("POST", srvurl, true);
			ajx.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
			var msg = "srv=getXzBorrow&min=" + this.minTim + "&max=" + this.maxTim;
			ajx.send(msg);
		}
	},

	hdOutIn: function (evt) {
		var a = evt.target;
		if (a.readyState === 4) {
			if (a.status === 200) {
				var s = "领用记录新增 ： ";
				var o = JSON.parse(a.responseText);
				if (o.ok) {
					var n = o.dat.length;
					for (var i = 0; i < n; i ++) {
						outInDom.innerHTML = s + (i + 1) + " / " + n;
						rfid.dbSynOutIn(o.dat[i]);
					}
				} else {
					outInDom.innerHTML = s + "0 / 0";
				}
			}
			outInDom.innerHTML += " , 完成！";
			dat.cmpNext();

			dat.outUp();
		}
	},

	// 领用记录修改
	outUp: function () {
		var ajx = this.getAjax();
		if (ajx) {
			ajx.onreadystatechange = this.hdOutUp;
			ajx.open("POST", srvurl, true);
			ajx.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
			var msg = "srv=getXgBorrow&min=" + this.minTim + "&max=" + this.maxTim;
			ajx.send(msg);
		}
	},

	hdOutUp: function (evt) {
		var a = evt.target;
		if (a.readyState === 4) {
			if (a.status === 200) {
				var s = "领用记录修改 ： ";
				var o = JSON.parse(a.responseText);
				if (o.ok) {
					var n = o.dat.length;
					for (var i = 0; i < n; i ++) {
						outUpDom.innerHTML = s + (i + 1) + " / " + n;
						rfid.dbSynOutUp(o.dat[i].split(":"));
					}
				} else {
					outUpDom.innerHTML = s + "0 / 0";
				}
			}
			outUpDom.innerHTML += " , 完成！";
			dat.cmpNext();
		}
	},

	cmpNext: function () {
		dat.cmp --;
		if (dat.cmp === 0) {
			rfid.setSynTim (dat.maxTim);
			dat.busy = false;
		}
	},

	back: function () {
		if (!dat.busy) {
			location.href = "home.html";
		}
	}

};
