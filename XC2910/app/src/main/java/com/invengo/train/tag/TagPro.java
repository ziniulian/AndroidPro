package com.invengo.train.tag;

import java.util.Map;

/**
 * 标签属性
 * Created by LZR on 2017/6/29.
 */

public class TagPro {
	private String pro;	// 属性码
	private String proNam;	// 属性名
	private Map<String, String> facs = null;	// 制造厂信息
	private Map<String, String> jus = null;	// 配属局信息
	private Map<String, String> suos = null;	// 配属所信息
	private Class cls = null;	// 标签类

	public void setCls(Class cls) {
		this.cls = cls;
	}

	public void setPro(String pro) {
		this.pro = pro;
	}

	public void setProNam(String proNam) {
		this.proNam = proNam;
	}

	public void setFacs(Map<String, String> facs) {
		this.facs = facs;
	}

	public void setJus(Map<String, String> jus) {
		this.jus = jus;
	}

	public void setSuos(Map<String, String> suos) {
		this.suos = suos;
	}

	public String getPro() {
		return pro;
	}

	public String getProNam() {
		return proNam;
	}

	public Map<String, String> getFacs() {
		return facs;
	}

	public Map<String, String> getJus() {
		return jus;
	}

	public Map<String, String> getSuos() {
		return suos;
	}

	// 生成标签
	public BaseTag crtTag (String c) throws Exception {
		BaseTag t = (BaseTag) cls.newInstance();
		t.setTpro(this);
		t.setCod(c);
		return t;
	}
}
