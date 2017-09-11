rfid = {
    // 读
    tid: 0,
    scan: function () {
        if (!this.tid) {
            this.tid = setInterval(rfid.doScan, 100);
		note.innerHTML = "正在卧具清点……";
        }
    },
    stop: function () {
        if (this.tid) {
            clearInterval(this.tid);
            this.tid = 0;
            rfid.doScan();
		note.innerHTML = "";
        }
    },
    doScan: function () {
        var s = rfdo.catchScanning();
        var o = JSON.parse(s);
        rfid.hdScan(o);
    },
    hdScan: function (obj) {
        // console.log(obj);
    },
    scanStart: function () {
        rfdo.scan();
    },
    scanStop: function () {
        rfdo.stop();
    },

    // 写
    wrt: function (bankNam, dat, tid) {
        rfdo.wrt(bankNam, dat, tid);
    },
    hdWrt: function (ok) {
        // console.log(ok);
    },

    // ---------------- 其它业务交互 -----------------------
    getUnit: function () {
        return rfdo.getUnit();
    },
    getTrip: function () {
        return rfdo.getTrip();
    },
    save: function (msg) {
        rfdo.save(msg);
    },
    qry: function (num, tim) {
        return rfdo.qry(num, tim);
    },
    findNum: function (num) {
        return rfdo.findNum(num);
    },

	sound: function () {
		rfdo.sound();
	},
	saveDetails: function (msg) {
		rfdo.saveDetails(msg);
	},
	findDetails: function (tim, typ) {
		return rfdo.findDetails(tim, typ);
	},

};
