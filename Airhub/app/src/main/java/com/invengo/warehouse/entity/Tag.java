package com.invengo.warehouse.entity;

/**
 * 标签
 * Created by LZR on 2017/8/12.
 */

public class Tag {
	private int tim = 1;	// 标签扫描到的次数

	public int getTim() {
		return tim;
	}

	public void addOne () {
		tim ++;
	}
}
