function init() {
    ol.rid = ol.getUrlReq().rid;
    if (!ol.rid) {
        location.href = "panList.html";
    } else {
        detaila.href = "panDetail.html?rid=" + ol.rid;
        scana.href = "panScan.html?rid=" + ol.rid;
    }
    ol.info();
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

    info: function () {
        if (!this.busy && this.getAjax()) {
            this.busy = true;
            this.ajx.onreadystatechange = this.hdInfo;
            this.ajx.open("POST", srvurl, true);
            this.ajx.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
            var msg = "srv=getPlanInfo&rid=" + this.rid;
            this.ajx.send(msg);
        }
    },

    hdInfo: function (evt) {
        a = evt.target;
        if (a.readyState === 4) {
            ol.busy = false;
            if (a.status === 200) {
                var o = JSON.parse(a.responseText);
                if (o.ok) {
                    var t = new Date(o.tim);
                    var s = t.getFullYear() + "-" + (t.getMonth() + 1) + "-" + t.getDate() + " " + t.getHours() + ":" + t.getMinutes() + ":" + t.getSeconds();
                    ridDom.innerHTML = o.rid;
                    personDom.innerHTML += o.person;
                    timDom.innerHTML += s;
                    if (o.remark) {
                        memoDom.innerHTML = o.remark;
                    }
                }
            }
        }
    },

    ok: function () {
        if (!this.busy && this.getAjax()) {
            this.busy = true;
            this.ajx.onreadystatechange = this.hdok;
            this.ajx.open("POST", srvurl, true);
            this.ajx.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
            var msg = "srv=planOk&rid=" + this.rid;
            this.ajx.send(msg);
        }
    },

    hdok: function (evt) {
        a = evt.target;
        if (a.readyState === 4) {
            ol.busy = false;
            if (a.status === 200) {
                var o = JSON.parse(a.responseText);
                if (o.ok) {
                    location.href = "panList.html";
                }
            }
        }
    }

};
