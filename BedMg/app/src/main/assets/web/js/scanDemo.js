function init() {
//    dat.crtTag("aaa", { "epc": "aaa", "tim": 5 });  // 测试3
}

rfid.hdScan = function () {
    var s = rfdo.catchScanning();
    var o = JSON.parse(s);
    var d;
    for (s in o) {
        if (dat.ts[s]) {
            dat.ts[s].tim += o[s].tim;
            dat.ts[s].timDom.innerHTML = dat.ts[s].tim;
        } else {
            dat.crtTag(s, o[s]);
        }
    }
};

dat = {
    // 标签集合
    ts: {},

    // 生成标签
    crtTag: function (s, o) {
        var r = {
          dom: document.createElement("div"),
          epcDom: document.createElement("div"),
          timDom: document.createElement("div"),
          delDom: document.createElement("div"),
          tim: o.tim
        };
        var d = document.createElement("div");
        d.className = "out";
        r.epcDom.className = "txt mfs";
        r.timDom.className = "tim sfs";
        r.delDom.className = "op mfs";
        d.appendChild(r.epcDom);
        d.appendChild(r.timDom);
        r.dom.appendChild(d);
        r.dom.appendChild(r.delDom);
        if (o.epc) {
            r.epcDom.innerHTML = o.epc;
        }
        r.timDom.innerHTML = r.tim;
        r.delDom.innerHTML = "<a href=\"javascript:dat.delTag('" + s + "');\">删除</a>";
        tago.appendChild(r.dom);
        dat.ts[s] = r;
    },

    // 删除标签
    delTag: function (key) {
        tago.removeChild(dat.ts[key].dom);
        delete (dat.ts[key]);
    },

    // 清空集合
    clear: function () {
        tago.innerHTML = "";
        dat.ts = {};
    }
};
