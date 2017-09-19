function init() {
    count1.innerHTML = dat.ts[1].length;
    count2.innerHTML = dat.ts[2].length;
    count3.innerHTML = dat.ts[3].length;
}

rfid.hdScan = function (o) {
    for (var s in o) {
        if (dat.ts[s]) {
            dat.ts[s].tim += o[s].tim;
        } else {
            dat.crtTag(s, o[s]);
        }
    }
};

dat = {
    // 标签集合
    has: false,
    sound: true,
    snSize: 6,    // 序列号格式化长度
    ts: {"1": [], "2": [], "3": []},

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
        return y + "年" + m + "月" + d + "日 " + h + ":" + u;
    },

    // 格式化序列号
    formatSn: function (sn) {
        var r = sn + "";
        var i, n;
        if (r.length < dat.snSize) {
            n = dat.snSize - r.length;
            r = "";
            for (i = 0; i < n; i ++) {
                r += "0";
            }
            r += sn;
        }
        return r;
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
                t += dat.formatSn (dat.ts[typ][i].sn);
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
            detailDom.className = "Lc_nosee";
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
            o.sn = parseInt(s.substring(2, 8), 16);
            dat.ts[o.typ].push(o);
            var d = document.getElementById("count" + o.typ);
            d.innerHTML = dat.ts[o.typ].length;
        }
        dat.ts[s] = o;
		dat.has = true;
		if (dat.sound) {
			rfid.sound();
		}
    },

    // 保存
    save: function () {
		rfid.scanStop();
        var n = num.value;
        if (n) {
            if (n.length === 6) {
                var t = Math.floor(Date.now() / 1000);
                var c = dat.ts[1].length + dat.ts[2].length + dat.ts[3].length;
                var s = t + "," + n + "," + dat.ts[1].length + "," + dat.ts[2].length + "," + dat.ts[3].length + "\n";
                rfid.save(s);
                s = dat.timStr(t * 1000) + "," + n + "," + dat.ts[1].length + "," + dat.ts[2].length + "," + dat.ts[3].length + "," + c + "\n";
                rfid.save2(s);

				s = "";
				for (j = 1; j < 4; j ++) {
					for (i = 0; i < dat.ts[j].length; i ++) {
						s += t + "," + j + "," + dat.ts[j][i].sn + "\n";
					}
				}
				rfid.saveDetails(s);
                note.innerHTML = "保存成功";
                location.href = "qry.html?min=" + t + "&max=" + t;
            } else {
                note.innerHTML = "车号必须是六个数字";
            }
        } else {
            note.innerHTML = "请输入车号";
        }
        return false;
    },

	// 是否保存的提示
	isExit: function () {
		dat.isDetail = true;
		detailDom.innerHTML = "<br>本次清点数据未保存<br><br><br><a class='subbtn' href='javascript: dat.detail();'>返回保存</a><br><br><a class='subbtn' href='home.html'>直接退出</a>";
		detailDom.className = "detailDom midOut mfs";
	},

	// 设置声音
	chgSound: function () {
		if (dat.sound) {
			dat.sound = false;
			ring.className = "ring noring";
		} else {
			dat.sound = true;
			ring.className = "ring";
		}
	},

    // 退出页面
    back: function () {
		rfid.scanStop();
        if (dat.isDetail) {
            dat.detail();
        } else if (dat.has) {
			dat.isExit();
        } else {
            location.href = "home.html";
        }
    }

};
