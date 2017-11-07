function init() {
	ol.rid = ol.getUrlReq().rid;
	if (!ol.rid) {
		location.href = ol.constant.url.list;
	} else {
		detaila.href = ol.constant.url.detail + ol.rid;
		scana.href = ol.constant.url.scan + ol.rid;
	}
	ol.user = rfdo.getUser();
	// ol.info();
}

ol.flush = function (o) {
};
