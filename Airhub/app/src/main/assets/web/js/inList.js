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
	o.innerHTML = "提交";
	o.onclick = ol.ok;
	d.appendChild(m);
	d.appendChild(t);
	d.appendChild(o);
	return d;
};
