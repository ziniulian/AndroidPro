function init() {
    ol.rid = ol.getUrlReq().rid;
    if (!ol.rid) {
        location.href = "inList.html";
    } else {
        infoa.href = "inInfo.html?rid=" + ol.rid;
        detaila.href = "inDetail.html?rid=" + ol.rid;
    }
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
                tim: o[s]
            };
            ol.sdat[s] = d;
            if (ol.rdat[s]) {
                d.has = true;
            } else {
                d.has = false;
            }
            d.dom = ol.crtDom(s, d.has);
            devs.appendChild(d.dom);
            devs.scrollTop = d.dom.offsetTop + d.dom.offsetHeight - devs.clientHeight;
        } else {
            ol.sdat[s].tim += o[s];
        }
    }
};

ol = {
    busy: false,
    ajx: null,
    rid: null,
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
            var msg = "srv=getDevsBySlave&rid=" + this.rid;
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
    crtDom: function (devid, has) {
        var d = document.createElement("div");
            d.setAttribute("devid", devid);
        var m = document.createElement("div");
            m.className = "mark";
        var t = document.createElement("div");
            if (has) {
                t.className = "txt mfs cover";
            } else {
                t.className = "txt mfs";
            }
            t.innerHTML = "<a href=\"devInfo.html?devid=" + devid + "\">" + devid + "</a>";
        var o = document.createElement("div");
            o.className = "op sfs";
            o.innerHTML = "删除";
            o.onclick = ol.del;
        d.appendChild(m);
        d.appendChild(t);
        d.appendChild(o);
        return d;
    },

    del: function (evt) {
        var rid = evt.target.parentNode.getAttribute("devid");
        devs.removeChild(ol.sdat[rid].dom);
        delete (ol.sdat[rid]);
    },

    // 添加
    add: function () {
        if (!this.busy && this.getAjax()) {
            this.busy = true;
            this.ajx.onreadystatechange = this.hdadd;
            this.ajx.open("POST", srvurl, true);
            this.ajx.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
            var msg = "srv=crtSlave&rid=" + this.rid;
            var o, s, d = "";
            for (s in ol.sdat) {
                o = ol.sdat[s];
                if (!o.has) {
                    if (d) {
                        d += ",";
                    }
                    d += s;
                }
            }
            if (d) {
// ddd.innerHTML += d;
                msg += "&devid=" + d;
            }
            this.ajx.send(msg);
        }
    },

    hdadd: function (evt) {
        a = evt.target;
        if (a.readyState === 4) {
            ol.busy = false;
            if (a.status === 200) {
                var o = JSON.parse(a.responseText);
                // if (o.ok) {
                    location.href = "inDetail.html?rid=" + ol.rid;
                // }
            }
        }
    }

};
