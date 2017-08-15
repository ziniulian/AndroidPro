package com.invengo.warehouse.enums;

/**
 * 页面信息
 * Created by LZR on 2017/8/11.
 */

public enum EmUrl {
	Scan("javascript: rfid.scan()"),
	Stop("javascript: rfid.stop()"),
	Signin("file:///android_asset/web/signin.html"),
	Home("file:///android_asset/web/home.html"),
	Test("file:///android_asset/web/testScan.html"),
	Err("file:///android_asset/web/err.html");

	private final String url;
	EmUrl(String u) {
		url = u;
	}
	public String url() {
		return url;
	}
}
