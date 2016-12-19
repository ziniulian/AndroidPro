package com.jiangzi.ziniulian.android.rfid;

import android.webkit.JavascriptInterface;

/**
 * Created by ziniulian on 2016/12/15.
 */

public interface InfRfidJsObj {
	// 读取一个标签
	@JavascriptInterface
	public void readTag();

	// 写入一个标签
	@JavascriptInterface
	public void writeTag(String msg);

	// 停止动作
	@JavascriptInterface
	public void stop();
}
