package com.invengo.train.tag;

/**
 * Created by LZR on 2017/6/29.
 */

public class TagD extends BaseTag {
	private String mod;	// 型号
	private String num;	// 车辆编号
	private String ju;	// 配属局
	private String suo;	// 配属段（所）
	private String tnoLet;	// 车次字母区
	private String tnoNum;	// 车次数字区
	private String pot;	// 双机车状态（端位）

	@Override
	protected void parseByCod(String cod) {
		mod = cod.substring(1, 4);	// 型号
		num = cod.substring(4, 10);	// 车辆编号
		ju = cod.substring(10, 12);	// 配属局
		suo = cod.substring(12, 14);	// 配属段（所）
		tnoLet = cod.substring(14, 18);	// 车次字母区
		tnoNum = cod.substring(18, 23);	// 车次数字区
		pot = cod.substring(23, 24);	// 双机车状态（端位）
	}

	public String getJuNam () {
		String r = tpro.getJus().get(ju);
		if (r == null) {
			r = "...";
		}
		return r;
	}

	public String getSuoNam () {
		String r = tpro.getSuos().get(suo);
		if (r == null) {
			r = "...";
		}
		return r;
	}

	public String getMod() {
		return mod;
	}

	public String getNum() {
		return num;
	}

	public String getJu() {
		return ju;
	}

	public String getSuo() {
		return suo;
	}

	public String getTnoLet() {
		return tnoLet;
	}

	public String getTnoNum() {
		return tnoNum;
	}

	public String getPot() {
		return pot;
	}
}
