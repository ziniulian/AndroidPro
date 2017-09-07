function init() {
    var o = dat.getUrlReq();
    if (o.tim) {
        dat.tim = o.tim;
    }
    if (o.num) {
        dat.setNum(o.num);
    }
    if (o.typ) {
        dat.setTyp(o.typ - 0);
    }
    unit.innerHTML = rfid.getUnit();
    trip.innerHTML = rfid.getTrip();
    dat.flush();

    // 测试
    // dat.flush([]);
    // dat.flush([["20170101121110", "ASC015", 1,2,3], ["20170101121110", "Bc054A", 33,0,61]]);
}

dat = {
    ts: null,
    num: null,
    tim: null,
    typ: 0,     // 0:全部,1:小单,2:被套,3:枕套

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

    setNum: function (n) {
        if (n !== dat.num) {
            dat.num = n;
            if (n) {
                num.innerHTML = n;
                num.className = "midSub mfs";
            } else {
                num.innerHTML = "点此选择车号";
                num.className = "midSub mfs numall";
            }
        }
    },

    setTyp: function (t) {
        if (t !== dat.typ) {
            var d = document.getElementById("typ" + dat.typ);
            d.className = "midSub typ mfs";
            dat.typ = t;
            d = document.getElementById("typ" + t);
            d.className = "midSub typ mfs typscd";

            var a = dat.ts;
            dat.ts = null;
            dat.flush(a);
        }
    },

    flush: function (a) {
        if (!a) {
            a = JSON.parse(rfid.qry(dat.num, dat.tim));
        }
        if (a !== dat.ts) {
            dat.ts = a;
            if (a.length) {
                rom.innerHTML = "";
                for (var i = 0; i < a.length; i ++) {
                    var o = document.createElement("div");
                    if (i % 2 === 1) {
                        o.className = "lineBg";
                    }

                    var d = document.createElement("div");
                    d.className = "rowSub ktw";
                    d.innerHTML = a[i][0];
                    o.appendChild(d);

                    d = document.createElement("div");
                    d.className = "rowSub knw";
                    d.innerHTML = a[i][1];
                    o.appendChild(d);

                    d = document.createElement("div");
                    d.className = "rowSub kpw";
                    switch (dat.typ) {
                        case 0:
                            d.innerHTML = "小单<br>被套<br>枕套";
                            break;
                        case 1:
                            d.innerHTML = "小单";
                            break;
                        case 2:
                            d.innerHTML = "被套";
                            break;
                        case 3:
                            d.innerHTML = "枕套";
                            break;
                    }
                    o.appendChild(d);

                    d = document.createElement("div");
                    d.className = "rowSub kcw";
                    if (dat.typ) {
                        d.innerHTML = a[i][dat.typ + 1];
                    } else {
                        d.innerHTML = a[i][2] + "<br>" + a[i][3] + "<br>" + a[i][4];
                    }
                    o.appendChild(d);

                    rom.appendChild(o);
                }
            } else {
                rom.innerHTML = "<p>没有符合条件的信息 ...</p>";
            }
        }
    }

};
