function init() {
    // dat.crtTag("013366990000", {tim: 5});
    // dat.crtTag("023366990000", {tim: 9});
    // dat.crtTag("036366990000", {tim: 7});
    // dat.crtTag("013366010000", {tim: 13});
    // dat.crtTag("013366020000", {tim: 6});

    count1.innerHTML = dat.ts[1].length;
    count2.innerHTML = dat.ts[2].length;
    count3.innerHTML = dat.ts[3].length;
    unit.innerHTML += rfid.getUnit();
    trip.innerHTML += rfid.getTrip();
}

rfid.hdScan = function (o) {
    for (s in o) {
        if (dat.ts[s]) {
            dat.ts[s].tim += o[s].tim;
        } else {
            dat.crtTag(s, o[s]);
        }
    }
};

dat = {
    // 标签集合
    ts: {"1": [], "2": [], "3": []},

    timStr: function (tim) {
        var t;
        if (tim) {
            t = new Date(tim);
        } else {
            t = new Date();
        }
        var y = t.getFullYear() + "";
        var m = t.getMonth() + 1;
        if (m < 10) {
            m = "0" + m;
        }
        var d = t.getDate();
        if (d < 10) {
            d = "0" + d;
        }
        var h = t.getHours();
        if (h < 10) {
            h = "0" + h;
        }
        var u = t.getMinutes();
        if (u < 10) {
            u = "0" + u;
        }
        var s = t.getSeconds();
        if (s < 10) {
            s = "0" + s;
        }
        // return y + "-" + m + "-" + d + " " + h + ":" + u + ":" + s;
        return y + m + d + h + u + s;
    },

    // 显示明细
    isDetail: false,
    detail: function (typ) {
        if (typ) {
            // 显示明细
            dat.isDetail = true;
            var i, t, s;
            switch (typ) {
                case 1:
                    t = "小单明细：<br>";
                    break;
                case 2:
                    t = "被套明细：<br>";
                    break;
                case 3:
                    t = "枕套明细：<br>";
                    break;
            }
            for (i = 0; i < dat.ts[typ].length; i ++) {
                t += dat.ts[typ][i].sn;
                // t += ",";
                // t += dat.ts[typ][i].tim;
                t += "<br>";
            }
            if (i === 0) {
                t += "(暂无记录)";
            }
            detailDom.innerHTML = t;
            detailDom.className = "detailDom sfs";
        } else {
            // 隐藏明细
            dat.isDetail = false;
            detailDom.innerHTML = "";
            detailDom.className = "detailDom sfs Lc_nosee";
        }
    },

    // 生成标签
    crtTag: function (s, o) {
        // 解析类型
        switch (s.substring(0, 2)) {
            case "01":
                o.typ = 1;
                break;
            case "02":
                o.typ = 2;
                break;
            case "03":
                o.typ = 3;
                break;
            default:
                o.typ = false;
                break;
        }
        if (o.typ) {
            // 解析序列号
            o.sn = s.substring(2, 8);
            dat.ts[o.typ].push(o);
            var d = document.getElementById("count" + o.typ);
            d.innerHTML = dat.ts[o.typ].length;
        }
        dat.ts[s] = o;
    },

    // 保存
    save: function () {
        var n = num.value;
        if (n) {
            if (n.length === 6) {
                var t = dat.timStr();
                var s = t + "," + n + "," + dat.ts[1].length + "," + dat.ts[2].length + "," + dat.ts[3].length + "\n";
                rfid.save(s);
                note.innerHTML = "保存成功";
                location.href = "qry.html?tim=" + t;
            } else {
                note.innerHTML = "车号必须是六个字符";
            }
        } else {
            note.innerHTML = "请输入车号";
        }
        return false;
    },

    // 退出页面
    back: function () {
        if (dat.isDetail) {
            dat.detail();
        } else {
            location.href = "home.html";
        }
    }

};
