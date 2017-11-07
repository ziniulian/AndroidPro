function init() {
	appnam.innerHTML = rfid.getAppNam();
	var t = JSON.parse(dat.getUrlReq().t);
	dat.id = t.id;
	dat.tag = t.tag;
	dat.ox = t.xiu;
	dat.flush(t.xiu);
}

dat = {
	id: null,
	ox: null,
	tag: null,

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

	set: function () {
		if (dat.tag.xiu.src !== dat.ox) {
			rfid.dbSet(dat.id, dat.tag.xiu.src);
		}
		dat.back();
	},

	back: function () {
		location.href = "check.html";
	}

};
