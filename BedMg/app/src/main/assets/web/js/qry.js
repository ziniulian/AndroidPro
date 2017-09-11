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
    dat.flush();
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

    timStr: function (tim) {
        var t;
        t = new Date(tim);
        var y = t.getFullYear() + "";
        var m = t.getMonth() + 1;
        // if (m < 10) {
        //     m = "0" + m;
        // }
        var d = t.getDate();
        // if (d < 10) {
        //     d = "0" + d;
        // }
        var h = t.getHours();
        if (h < 10) {
            h = "0" + h;
        }
        var u = t.getMinutes();
        if (u < 10) {
            u = "0" + u;
        }
        // var s = t.getSeconds();
        // if (s < 10) {
        //     s = "0" + s;
        // }
        // return y + "-" + m + "-" + d + " " + h + ":" + u + ":" + s;
        // return y + m + d + h + u + s;
        return y + "年" + m + "月" + d + "日<br>" + h + ":" + u;
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
                var i = a.length - 1;
                var cc = i % 2;
                for (; i >= 0; i --) {
                    var o = document.createElement("div");
                    if (i % 2 !== cc) {
                        o.className = "lineBg";
                    }

                    var d = document.createElement("div");
                    d.className = "rowSub ktw";
                    d.innerHTML = dat.timStr(a[i][0] * 1000);
                    o.appendChild(d);

                    d = document.createElement("div");
                    d.className = "rowSub knw";
                    d.innerHTML = a[i][1];
                    o.appendChild(d);

                    d = document.createElement("div");
                    d.className = "rowSub kpw";
                    var s = "<a href='details.html?tim=" + a[i][0];
                    switch (dat.typ) {
                        case 0:
                            d.innerHTML = s + "&typ=1'>小单</a><br>" + s + "&typ=2'>被套</a><br>" + s + "&typ=3'>枕套";
                            break;
                        case 1:
                            d.innerHTML = s + "&typ=1'>小单</a>";
                            break;
                        case 2:
                            d.innerHTML = s + "&typ=2'>被套</a>";
                            break;
                        case 3:
                            d.innerHTML = s + "&typ=3'>枕套</a>";
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
