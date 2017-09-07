function init() {
    var a = JSON.parse(rfid.findNum(null));
// 测试数据
// var a = [];
// var a = ["aaaaaa", "bbbbbb"];
    if (a.length) {
        var i, d;
        d = document.createElement("a");
        d.href = "qry.html";
        d.innerHTML = "全部";
        boso.appendChild(d);
        for (i = 0; i < a.length; i ++) {
            d = document.createElement("a");
            d.href = "qry.html?num=" + a[i];
            d.innerHTML = a[i];
            boso.appendChild(d);
        }
    } else {
        boso.innerHTML = "<br><span>暂无车号</span>";
    }
}
