package com.jiangzi.ziniulian.android.rfid;

import android.webkit.JavascriptInterface;

/**
 * Created by ziniulian on 2016/12/15.
 */

public interface InfRfidJsObj {
	// 读取一个标签
	@JavascriptInterface
	public void read(String bankNam, int offset, int len);

	// 写入一个标签
	@JavascriptInterface
	public void write(String bankNam, int offset, String msg);

	// 扫描标签
	@JavascriptInterface
	public void scanning();

	// 获取扫描结果
	@JavascriptInterface
	public String getScanning();

	// 停止动作
	@JavascriptInterface
	public void stop();
}
