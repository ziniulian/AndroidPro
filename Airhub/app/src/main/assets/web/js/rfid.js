rfid = {
    tid: 0,
    scan: function () {
        if (!this.tid) {
            this.tid = setInterval(this.hdScan, 100);
        }
    },
    stop: function () {
        if (this.tid) {
            clearInterval(this.tid);
            this.tid = 0;
            this.hdScan();
        }
    },
    hdScan: function () {
        var s = rfdo.catchScanning();
    },

/*******************************************************/

	dbGetDevInfo: function (id) {
		return rfdo.dbGetDevInfo(id);
	},

	dbGetBorrowByDev: function (id) {
		return rfdo.dbGetBorrowByDev(id);
	},

    dbSynDevIn: function (dat) {
        rfdo.dbSynDevIn(dat);
    },

    dbSynDevUp: function (dat) {
        rfdo.dbSynDevUp(dat);
    },

    dbSynOutIn: function (dat) {
        rfdo.dbSynOutIn(dat);
    },

    dbSynOutUp: function (dat) {
        rfdo.dbSynOutUp(dat);
    },

    getSynTim: function () {
        return rfdo.getSynTim();
    },

    setSynTim: function (t) {
        rfdo.setSynTim(t);
    }

};
