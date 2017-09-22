package com.invengo.warehouse.enums;

/**
 * 页面信息
 * Created by LZR on 2017/8/11.
 */

public enum EmUrl {
	Home("file:///android_asset/web/home.html"),
	Signin("file:///android_asset/web/signin.html"),
	User("file:///android_asset/web/user.html"),
	Scan("javascript: rfid.scan();"),
	Stop("javascript: rfid.stop();"),
	Back("javascript: dat.back();"),
	OffScan("file:///android_asset/web/offScan.html"),
	OffDevInfo("file:///android_asset/web/offDevInfo.html"),
	OffDevBorrow("file:///android_asset/web/offDevBorrow.html"),
	DevInfo("file:///android_asset/web/devInfo.html"),
	InList("file:///android_asset/web/inList.html"),
	InDetail("file:///android_asset/web/inDetail.html"),
	InInfo("file:///android_asset/web/inInfo.html"),
	InScan("file:///android_asset/web/inScan.html"),
	OutList("file:///android_asset/web/outList.html"),
	OutDetail("file:///android_asset/web/outDetail.html"),
	OutInfo("file:///android_asset/web/outInfo.html"),
	OutScan("file:///android_asset/web/outScan.html"),
	PanDetail("file:///android_asset/web/panDetail.html"),
	PanInfo("file:///android_asset/web/panInfo.html"),
	PanScan("file:///android_asset/web/panScan.html"),
	PanList("file:///android_asset/web/panList.html"),
	Syn("file:///android_asset/web/syn.html"),
	Err("file:///android_asset/web/err.html");

	private final String url;
	EmUrl(String u) {
		url = u;
	}
	public String url() {
		return url;
	}
}
