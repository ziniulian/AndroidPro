function init() {
}

rfid.hdScan = function () {
    var s = rfdo.catchScanning();
    var o = JSON.parse(s);
    var d;
    for (s in o) {
        if (!ol.sdat[s]) {
            d = {
                id: s,
                tim: o[s],
                timDom: document.createElement("div")
            };
            d.timDom.innerHTML = o[s];
            d.dom = ol.crtDom(s, d.timDom);
            ol.sdat[s] = d;
            devs.appendChild(d.dom);

            // 位置调整
            devs.scrollTop = d.dom.offsetTop + d.dom.offsetHeight - devs.clientHeight;
        } else {
            ol.sdat[s].tim += o[s];
            ol.sdat[s].timDom.innerHTML = ol.sdat[s].tim;
        }
    }
}

ol = {
    sdat: {},

    // 创建DOM元素
    crtDom: function (devid, td) {
        td.className = "tim sfs";
        var d = document.createElement("div");
            d.setAttribute("devid", devid);
        var m = document.createElement("div");
            m.className = "mark";
        var t = document.createElement("div");
            t.className = "txt mfs";
            t.innerHTML = "<a href=\"offDevInfo.html?devid=" + devid + "\">" + devid + "</a>";
        var o = document.createElement("div");
            o.className = "op sfs";
            o.innerHTML = "删除";
            o.onclick = ol.del;
        d.appendChild(m);
        d.appendChild(t);
        d.appendChild(o);
        d.appendChild(td);
        return d;
    },

    // 删除设备
    del: function (evt) {
        var rid = evt.target.parentNode.getAttribute("devid");
        devs.removeChild(ol.sdat[rid].dom);
        delete (ol.sdat[rid]);
    },

    // 清空
    clear: function (evt) {
        devs.innerHTML = "";
        ol.sdat = {};
    }

};
