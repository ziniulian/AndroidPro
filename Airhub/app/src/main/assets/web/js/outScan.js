function init() {
    ol.rid = ol.getUrlReq().rid;
    if (!ol.rid) {
        location.href = "outList.html";
    } else {
        infoa.href = "outInfo.html?rid=" + ol.rid;
        detaila.href = "outDetail.html?rid=" + ol.rid;
    }
    ol.dat();
}

rfid.hdScan = function () {
    var s = rfdo.catchScanning();
// ddd.innerHTML += s;
    var o = JSON.parse(s);
// ddd.innerHTML += o;
    var d;
    for (s in o) {
        if (!ol.sdat[s]) {
// ddd.innerHTML += s + ",";
            d = {
                id: s,
                tim: o[s]
            };
            if (ol.rdat[s]) {
                // 已经保存在出库明细中的设备
                d.status = 1;
            } else {
                // 出库明细中没有的设备
                d.status = 2;   // 假设库存表里有记录，若无记录，状态应该为 0
            }
// ddd.innerHTML += d.status + ",";
            d.dom = ol.crtDom(s, d.status);
// ddd.innerHTML += d.dom;
            ol.sdat[s] = d;
            devs.appendChild(d.dom);

            // 位置调整
            devs.scrollTop = d.dom.offsetTop + d.dom.offsetHeight - devs.clientHeight;
        } else {
// ddd.innerHTML += s + ":";
            ol.sdat[s].tim += o[s];
// ddd.innerHTML += ol.sdat[s].tim;
        }
    }
// ddd.innerHTML += "<br>";
}

ol = {
    busy: false,
    ajx: null,
    rid: null,
    rdat: {},
    sdat: {},

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

    // 获取已有的出库明细
    dat: function () {
        if (!this.busy && this.getAjax()) {
            this.busy = true;
            this.ajx.onreadystatechange = this.hddat;
            this.ajx.open("POST", srvurl, true);
            this.ajx.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
            var msg = "srv=getDevsByOut&rid=" + this.rid;
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
                        if (s) {
                            ol.rdat[s] = o.dat[i];
                        }
                    }
                }
            }
            ol.busy = false;
        }
    },

    // 创建DOM元素
    crtDom: function (devid, status) {
        var d = document.createElement("div");
            d.setAttribute("devid", devid);
        var m = document.createElement("div");
            if (status === 1) {
                m.className = "mark stcover";
            } else {
                m.className = "mark";
            }
        var t = document.createElement("div");
            if (status === 1) {
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

    // 删除设备
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
            var msg = "srv=crtBorrow&rid=" + this.rid;
            var o, s, d = "";
            for (s in ol.sdat) {
                o = ol.sdat[s];
                if (o.status === 2) {
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
                    location.href = "outDetail.html?rid=" + ol.rid;
                // }
            }
        }
    }

};
