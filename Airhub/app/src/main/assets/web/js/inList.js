ol.crtDom = function (rid) {
	var d = document.createElement("div");
	d.setAttribute("rid", rid);
	var m = document.createElement("div");
	m.className = "mark";
	var t = document.createElement("div");
	t.className = "txt mfs";
	// t.innerHTML = "<a href=\"" + ol.constant.url.inf + rid + "\">" + rid + "</a>";
	t.innerHTML = "<a href=\"" + ol.constant.url.detail + rid + "\">" + rid + "</a>";
	var o = document.createElement("div");
	o.className = "op sfs";
	// o.innerHTML = "删除";
	// o.onclick = ol.del;
	o.innerHTML = "提交";
	o.onclick = ol.ok;
	d.appendChild(m);
	d.appendChild(t);
	d.appendChild(o);
	return d;
};

ol.hdok = function (evt) {
	a = evt.target;
	if (a.readyState === 4) {
		if (a.status === 200) {
			var o = JSON.parse(a.responseText);
			if (o.ok) {
				ols.removeChild(ol.busy);
			}
		}
		ol.busy = false;
	}
};

ol.ok = function (evt) {
	if (!ol.busy && ol.getAjax()) {
		ol.busy = evt.target.parentNode;
		ol.ajx.onreadystatechange = ol.hdok;
		ol.ajx.open("POST", srvurl, true);
		ol.ajx.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
		var msg = ol.constant.srv.okR + ol.busy.getAttribute("rid") + "&user=" + ol.user;
		ol.ajx.send(msg);
	}
};
