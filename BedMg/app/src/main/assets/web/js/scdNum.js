function init() {
    var a = JSON.parse(rfid.findNum(null));
    if (a.length) {
        var i, d;
        d = document.createElement("a");
        d.href = "qry.html";
        d.innerHTML = "全部车号";
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
