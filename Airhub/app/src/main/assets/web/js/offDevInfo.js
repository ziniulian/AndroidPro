function init() {
    ol.devid = ol.getUrlReq().devid;
    if (ol.devid) {
        didDom.innerHTML = ol.devid;
        logOut.href = "offDevBorrow.html?devid=" + ol.devid;
        ol.flush();
    }
}

ol = {
    devid: null,

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
        var d = rfid.dbGetDevInfo(ol.devid);
        var o = JSON.parse(d);
        if (o.ok) {
            ol.flushDom(o);
        }
    },

    flushDom: function (o) {
        if (o.iscrap) {
            prompt.innerHTML = "已报废";
        } else if (o.isout) {
            prompt.innerHTML = "已出库";
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
