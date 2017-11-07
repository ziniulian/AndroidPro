package com.invengo.warehouse.entity;

import com.invengo.lib.util.StringUtil;
import com.invengo.rfid.util.Str;

/**
 * 标签
 * Created by LZR on 2017/8/12.
 */

public class Tag {
	private int tim = 1;	// 标签扫描到的次数
	private String tid = "";

	public Tag() {}

	public Tag(String d) {
		this.tid = d;
	}

	public int getTim() {
		return tim;
	}

	public void addOne () {
		tim ++;
	}

	public StringBuilder getJson () {	// 测试用
		StringBuilder r = new StringBuilder();
		r.append("{\"tim\":");
		r.append(tim);
		r.append(",\"tid\":\"");
		r.append(tid);
		r.append('\"');
		r.append('}');
		return r;
	}
}
