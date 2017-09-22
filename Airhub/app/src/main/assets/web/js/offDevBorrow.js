function init() {
    ol.devid = ol.getUrlReq().devid;
    if (ol.devid) {
        didDom.innerHTML = ol.devid;
        devInf.href = "offDevInfo.html?devid=" + ol.devid;
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
        var d = rfid.dbGetBorrowByDev(ol.devid);
        var o = JSON.parse(d);
        if (o.ok) {
            for (var i = 0; i < o.dat.length; i ++) {
                ol.flushDom(o.dat[i]);
            }
        }
    },

    flushDom: function (o) {
        var d = document.createElement("div");
        d.className = "b sfs";
        var s = "领用时间：" + ol.timStr(o.tim) + "</br>";
        s += "单位名称：" + o.dep + "</br>";
        s += "领用人：" + o.person + "</br>";
        s += "结束日期：" + ol.timStr(o.comtim);
        d.innerHTML = s;
        devs.appendChild(d);
    }

};
