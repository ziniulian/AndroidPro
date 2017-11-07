function init() {}

rfid.hdScan = function () {
	var s = rfdo.catchScanning();
	var o = JSON.parse(s);
	var d;
	for (s in o) {
		if (!ol.sdat[s]) {
			d = {
				id: s,
				tim: o[s],
				has: false,
				timDom: document.createElement("div")
			};
			ol.sdat[s] = d;
			ol.check(d);
		} else {
			ol.sdat[s].tim += o[s];
			ol.sdat[s].timDom.innerHTML = ol.sdat[s].tim;
		}
	}
};

ol.check = function (d) {
	var strDev = rfid.dbGetDevInfo(d.id);
	var o = JSON.parse(strDev);
	if (o.ok) {
		d.status = 2;
		d.nam = o.nam;
		d.mod = o.mod;
		d.sn = o.sn;
		ol.appendDom(d);
	} else {
		switch (d.id.substr(0, 2)) {
			case "JS":
			case "TX":
			case "DH":
			case "QX":
				d.status = 0;
				d.nam = "（未知标签）";
				d.mod = "--";
				d.sn = d.id;
				ol.appendDom(d);
				break;
			default:
				delete (ol.sdat[d.id]);
				break;
		}
	}
};
