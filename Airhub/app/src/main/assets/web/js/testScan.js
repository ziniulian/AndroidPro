function init() {
}

rfid.hdScan = function () {
    var s = rfdo.catchScanning();
    log.innerHTML += s;
}
