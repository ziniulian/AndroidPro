function init() {
    ol.rid = ol.getUrlReq().rid;
    if (!ol.rid) {
        location.href = "inList.html";
    } else {
        infoa.href = "inInfo.html?rid=" + ol.rid;
        scana.href = "inScan.html?rid=" + ol.rid;
    }
    ol.flush();
}

ol = {
    busy: false,
    ajx: null,
    rid: null,

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
            var msg = "srv=getDevsBySlave&rid=" + this.rid;
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
                        devs.appendChild(ol.crtDom(o.dat[i].devid));
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
            m.className = "mark";
        var t = document.createElement("div");
            t.className = "txt mfs";
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
        if (!ol.busy && ol.getAjax()) {
            ol.busy = evt.target.parentNode;
            ol.ajx.onreadystatechange = ol.hddel;
            ol.ajx.open("POST", srvurl, true);
            ol.ajx.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
            var msg = "srv=delOneSlave&rid=" + ol.rid + "&devid=" + ol.busy.getAttribute("devid");
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
