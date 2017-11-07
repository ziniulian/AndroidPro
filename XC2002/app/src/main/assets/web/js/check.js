function init() {
	appnam.innerHTML = rfid.getAppNam();
	dat.count = rfid.dbCount();
	content.onscroll = dat.toNext;
	dat.nextPage();

	// 测试
	// dat.flush(
	// 	[
	// 		{
	// 			id:3,
	// 			tim:"20000110071944",
	// 			xiu:"",
	// 			tag:{
	// 				cod:"D2617162813DD<:2155A",
	// 				pro:{src:"D",nam:"动车"},
	// 				mod:{"src":"261",nam:""},
	// 				num:"716",
	// 				ju:{src:"28",nam:"西安局"},
	// 				suo:{src:"13",nam:""},
	// 				tnoLet:"DD",
	// 				tnoNum:"2155",
	// 				pot:"A"
	// 			}
	// 		},
	// 		{
	// 			id:8,
	// 			tim:"20000110071944",
	// 			xiu:"C",
	// 			tag:{
	// 				cod:"D2617162813DD<:2155A",
	// 				pro:{src:"T",nam:"货车8"},
	// 				typ:{"src":"C",nam:"敞车"},
	// 				mod:"28",
	// 				num:"716",
	// 				fac:{src:"A",snam:"齐厂"},
	// 				tim:{src:"075",nam:"2017年5月"}
	// 			}
	// 		},
	// 		{
	// 			id:18,
	// 			tim:"20000110071944",
	// 			xiu:"C",
	// 			tag:{
	// 				cod:"D2617162813DD<:2155A",
	// 				pro:{src:"T",nam:"货车1"},
	// 				typ:{"src":"C",nam:"敞车"},
	// 				mod:"28",
	// 				num:"716",
	// 				fac:{src:"A",snam:"齐厂"},
	// 				tim:{src:"075",nam:"2017年5月"}
	// 			}
	// 		},
	// 		{
	// 			id:28,
	// 			tim:"20000110071944",
	// 			xiu:"C",
	// 			tag:{
	// 				cod:"D2617162813DD<:2155A",
	// 				pro:{src:"T",nam:"货车2"},
	// 				typ:{"src":"C",nam:"敞车"},
	// 				mod:"28",
	// 				num:"716",
	// 				fac:{src:"A",snam:"齐厂"},
	// 				tim:{src:"075",nam:"2017年5月"}
	// 			}
	// 		},
	// 		{
	// 			id:35,
	// 			tim:"20000110071944",
	// 			xiu:"",
	// 			tag:{
	// 				cod:"D2617162813DD<:2155A",
	// 				pro:{src:"D",nam:"动车5"},
	// 				mod:{"src":"261",nam:""},
	// 				num:"716",
	// 				ju:{src:"28",nam:"西安局5"},
	// 				suo:{src:"13",nam:""},
	// 				tnoLet:"DD",
	// 				tnoNum:"2155",
	// 				pot:"A"
	// 			}
	// 		},
	// 		{
	// 			id:34,
	// 			tim:"20000110071944",
	// 			xiu:"",
	// 			tag:{
	// 				cod:"D2617162813DD<:2155A",
	// 				pro:{src:"D",nam:"动车4"},
	// 				mod:{"src":"261",nam:""},
	// 				num:"716",
	// 				ju:{src:"28",nam:"西安局4"},
	// 				suo:{src:"13",nam:""},
	// 				tnoLet:"DD",
	// 				tnoNum:"2155",
	// 				pot:"A"
	// 			}
	// 		},
	// 		{
	// 			id:13,
	// 			tim:"20000110071944",
	// 			xiu:"",
	// 			tag:{
	// 				cod:"D2617162813DD<:2155A",
	// 				pro:{src:"D",nam:"动车1"},
	// 				mod:{"src":"261",nam:""},
	// 				num:"716",
	// 				ju:{src:"28",nam:"西安局1"},
	// 				suo:{src:"13",nam:""},
	// 				tnoLet:"DD",
	// 				tnoNum:"2155",
	// 				pot:"A"
	// 			}
	// 		}
	// 	]
	// );

}

dat = {
	busy: true,
	pagesize: 5,
	total: 0,	// 页面显示的记录数
	count: 0,	// 数据库的记录总数
	ts: {},
	scds: {},

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

	// 下一页
	toNext: function () {
// console.log ( (content.scrollTop + content.clientHeight) + " , " + content.scrollHeight + " , " + content.scrollTop + " , " + content.clientHeight);
		if (content.scrollTop && !dat.busy && (content.scrollTop + content.clientHeight === content.scrollHeight)) {
			dat.nextPage();
		}
	},

	// 下一页
	nextPage: function () {
		if (dat.total < dat.count) {
			dat.busy = true;
			dat.flush(JSON.parse(rfid.dbGet(dat.total, dat.pagesize)));
		}
	},

	flush: function (a) {
		for(var i = 0; i < a.length; i ++) {
			switch (a[i].tag.pro.src) {
				case "T":
				case "Q":
				case "!":
					dat.domT(a[i]);
					break;
				case "J":
					dat.domJ(a[i]);
					break;
				case "D":
					dat.domD(a[i]);
					break;
				case "K":
					dat.domK(a[i]);
					break;
			}
			dat.setDom(a[i]);
			content.appendChild(a[i].dom);
			dat.ts[a[i].id] = a[i];
			dat.total ++;
		}
		dat.busy = false;
	},

	setDom: function (t) {
		var d = t.dom;
		d.appendChild(document.createElement("hr"));
		t.scd = false;
		d.onclick = function (e) {
			dat.scd(t);
		};
	},

	scd: function (t) {
		var d = t.dom;
		if (t.scd) {
			d.className = "";
			delete (dat.scds[t.id]);
			t.scd = false;
		} else {
			d.className = "cscd";
			dat.scds[t.id] = true;
			t.scd = true;
		}
	},

	del: function () {
		var ids = "";
		for (var s in dat.scds) {
			if (ids) {
				ids += ",";
			}
			ids += s;
			content.removeChild(dat.ts[s].dom);
			delete dat.ts[s];
			dat.total --;
			dat.count --;
		}
		if (ids) {
			rfid.dbDel(ids);
			dat.scds = {};
// console.log ( (content.scrollTop + content.clientHeight) + " , " + content.scrollHeight + " , " + content.scrollTop + " , " + content.clientHeight);
			if (!dat.busy && (content.scrollHeight === content.clientHeight) ) {
				dat.nextPage();
			}
		}
	},

	chg: function (t) {
		location.href = "chg.html?t=" + encodeURIComponent(JSON.stringify(t));
	},

	domT: function (t) {
		var d = document.createElement("div");
		d.appendChild(dat.domTim(t.tim));	// 时间
		d.appendChild(dat.domLine("属性", t.tag.pro.src + "[" + t.tag.pro.nam + "]"));
		d.appendChild(dat.domLine("车种车型", t.tag.typ.src + " " + t.tag.mod));
		d.appendChild(dat.domLine("车号", t.tag.num));
		d.appendChild(dat.domLine("制造厂", t.tag.fac.src + "[" + t.tag.fac.snam + "]"));
		d.appendChild(dat.domLine("制造年月", t.tag.tim.src + "[" + t.tag.tim.nam + "]"));
		d.appendChild(dat.domLine("修程", dat.parseXiu(t)));
		t.dom = d;
	},

	domJ: function (t) {
		var d = document.createElement("div");
		d.appendChild(dat.domTim(t.tim));	// 时间
		d.appendChild(dat.domLine("属性", t.tag.pro.src));
		d.appendChild(dat.domLine("车型", t.tag.mod.src + "[" + t.tag.mod.nam + "]"));
		d.appendChild(dat.domLine("车号", t.tag.num));
		d.appendChild(dat.domLine("配属局段", t.tag.ju.src + t.tag.suo.src + "[" + t.tag.ju.nam + t.tag.suo.nam + "]"));
		d.appendChild(dat.domLine("车次", t.tag.tnoLet + t.tag.tnoNum));
		d.appendChild(dat.domLine("端码", t.tag.pot));
		d.appendChild(dat.domLine("客货", t.tag.kh));
		t.dom = d;
	},

	domD: function (t) {
		var d = document.createElement("div");
		d.appendChild(dat.domTim(t.tim));	// 时间
		d.appendChild(dat.domLine("属性", t.tag.pro.src));
		d.appendChild(dat.domLine("车型", t.tag.mod.src + "[" + t.tag.mod.nam + "]"));
		d.appendChild(dat.domLine("车号", t.tag.num));
		d.appendChild(dat.domLine("配属局段", t.tag.ju.src + t.tag.suo.src + "[" + t.tag.ju.nam + t.tag.suo.nam + "]"));
		d.appendChild(dat.domLine("车次", t.tag.tnoLet + t.tag.tnoNum));
		d.appendChild(dat.domLine("端码", t.tag.pot));
		t.dom = d;
	},

	domK: function (t) {
		var d = document.createElement("div");
		d.appendChild(dat.domTim(t.tim));	// 时间
		d.appendChild(dat.domLine("属性", t.tag.pro.src));
		d.appendChild(dat.domLine("车型", t.tag.typ.src + t.tag.mod + "[" + t.tag.typ.nam + "]"));
		d.appendChild(dat.domLine("车号", t.tag.num));
		d.appendChild(dat.domLine("制造厂", t.tag.fac.src + "[" + t.tag.fac.snam + "]"));
		d.appendChild(dat.domLine("制造年月", t.tag.tim.src + "[" + t.tag.tim.nam + "]"));
		d.appendChild(dat.domLine("定员", t.tag.cap));
		t.dom = d;
	},

	domLine: function (n, v) {
		var o = document.createElement("div");
		var d = document.createElement("div");
		d.className = "ckey";
		d.innerHTML = n;
		o.appendChild(d);
		d = document.createElement("div");
		d.className = "csplit";
		d.innerHTML = ":";
		o.appendChild(d);
		d = document.createElement("div");
		d.className = "cvalue";
		d.innerHTML = v;
		o.appendChild(d);
		return o;
	},

	domTim: function (t) {
		var d = document.createElement("div");
		d.className = "ctim";
		d.innerHTML = dat.parseTim(t);
		return d;
	},

	parseTim: function (t) {
		var r = "";
		for (var i = 0; i < t.length; i ++) {
			r += t[i];
			switch (i) {
				case 3:
				case 5:
					r += "/";
					break;
				case 7:
					r += " ";
					break;
				case 9:
				case 11:
					r += ":";
					break;
			}
		}
		return r;
	},

	parseXiu: function (t) {
		var x = t.xiu;
		switch (x) {
			case "C":
				x += "[厂修]";
				break;
			case "D":
				x += "[段修]";
				break;
			case "F":
				x += "[辅修]";
				break;
			case "L":
				x += "[临修]";
				break;
			case "Z":
				x += "[轴修]";
				break;
			case "B":
				x += "[报废]";
				break;
		}
		x += "<a class='cbtn' href='javascript: dat.chg(" + JSON.stringify(t) + ");'>修改</a>";
		return x;
	},

	// 退出页面
	back: function () {
		location.href = "home.html";
	}

};
