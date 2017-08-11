package com.invengo.warehouse.service;

import android.webkit.JavascriptInterface;

/**
 * Created by LZR on 2017/8/11.
 */

public interface InfWeb {
	// 获取扫描结果
	@JavascriptInterface
	public String catchScanning();

	// 扫描
	@JavascriptInterface
	public void scan();

	// 停止
	@JavascriptInterface
	public void stop();
}
