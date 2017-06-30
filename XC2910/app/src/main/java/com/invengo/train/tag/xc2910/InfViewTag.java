package com.invengo.train.tag.xc2910;

import java.util.List;
import java.util.Map;

/**
 * XC2910界面显示需要的标签类型
 * Created by LZR on 2017/6/30.
 */

public interface InfViewTag {
	// 获取界面显示的信息
	public List<Map<String, String>> getViewDat ();

	// 获取标签源码
	public String getCod ();
}
