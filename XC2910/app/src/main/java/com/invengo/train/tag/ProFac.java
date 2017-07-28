package com.invengo.train.tag;

/**
 * 制造厂信息
 * Created by LZR on 2017/7/27.
 */

public class ProFac {
	private String id;
	private String nam;
	private String fullNam;

	public ProFac setId(String id) {
		this.id = id;
		return this;
	}

	public ProFac setNam(String nam) {
		this.nam = nam;
		return this;
	}

	public ProFac setFullNam(String fullNam) {
		this.fullNam = fullNam;
		return this;
	}

	public String getId() {
		return id;
	}

	public String getNam() {
		return nam;
	}

	public String getFullNam() {
		return fullNam;
	}
}
