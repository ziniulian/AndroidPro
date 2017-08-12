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
    }
};
