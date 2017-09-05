package com.invengo.bedmg.enums;

/**
 * Created by LZR on 2017/9/1.
 */

public enum EmUrl {
	Scan("javascript: rfid.scan()"),
	Stop("javascript: rfid.stop()"),
	WrtOk("javascript: rfid.hdWrt(true);"),
	WrtErr("javascript: rfid.hdWrt(false);"),
	ScanDemo("file:///android_asset/web/scanDemo.html"),
	Err("file:///android_asset/web/err.html");

	private final String url;
	EmUrl(String u) {
		url = u;
	}
	public String url() {
		return url;
	}
}
