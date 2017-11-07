ol.flush = function () {
	var d = rfid.dbGetBorrowByDev(ol.devid);
	var o = JSON.parse(d);
	if (o.ok) {
		for (var i = 0; i < o.dat.length; i ++) {
			ol.flushDom(o.dat[i]);
		}
	}
};
