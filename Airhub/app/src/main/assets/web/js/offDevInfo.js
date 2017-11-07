ol.flush = function () {
	var d = rfid.dbGetDevInfo(ol.devid);
	var o = JSON.parse(d);
	if (o.ok) {
		ol.flushDom(o);
	}
};
