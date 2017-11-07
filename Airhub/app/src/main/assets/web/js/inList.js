ol.crtDom = function (rid) {
	var d = document.createElement("div");
	d.setAttribute("rid", rid);
	var m = document.createElement("div");
	m.className = "mark";
	var t = document.createElement("div");
	t.className = "txt mfs";
	t.innerHTML = "<a href=\"" + ol.constant.url.inf + rid + "\">" + rid + "</a>";
	// var o = document.createElement("div");
	// o.className = "op sfs";
	// o.innerHTML = "删除";
	// o.onclick = ol.del;
	d.appendChild(m);
	d.appendChild(t);
	// d.appendChild(o);
	return d;
};
