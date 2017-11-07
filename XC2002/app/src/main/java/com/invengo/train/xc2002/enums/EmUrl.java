package com.invengo.train.xc2002.enums;

/**
 * Created by ziniulian on 2017/10/17.
 */

public enum EmUrl {
	Home("file:///android_asset/web/home.html"),
	Read("file:///android_asset/web/read.html"),
	Info("file:///android_asset/web/infoT.html"),
	Check("file:///android_asset/web/check.html"),
	Chg("file:///android_asset/web/chg.html"),
	ReRead("file:///android_asset/web/read.html?read=1"),
	HdRead("javascript: rfid.hdRead(<0>);"),
	ReadNull("javascript: dat.readNull();"),
	Reading("javascript: dat.reading();"),
	FlashLight("javascript: dat.flashlight(<0>);"),
	Back("javascript: dat.back();"),
	Exit("file:///android_asset/web/home.html"),
	Err("file:///android_asset/web/err.html");

	private final String url;
	EmUrl(String u) {
		url = u;
	}
	@Override
	public String toString() {
		return url;
	}
}
