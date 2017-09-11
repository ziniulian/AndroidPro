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
    ts: {"1": [], "2": [], "3": []},

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
                var s = t + "," + n + "," + dat.ts[1].length + "," + dat.ts[2].length + "," + dat.ts[3].length + "\n";
                rfid.save(s);
				s = "";
				for (j = 1; j < 4; j ++) {
					for (i = 0; i < dat.ts[j].length; i ++) {
						s += t + "," + j + "," + dat.ts[j][i].sn + "\n";
					}
				}
				rfid.saveDetails(s);
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

	// 是否保存的提示
	isExit: function () {
		dat.isDetail = true;
		detailDom.innerHTML = "<br>有尚未保存的盘点信息，<br>请确认是否需要保存：<br><br><br><a class='subbtn' href='javascript: dat.detail();'>回去保存</a><br><br><a class='subbtn' href='home.html'>不保存</a>";
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
