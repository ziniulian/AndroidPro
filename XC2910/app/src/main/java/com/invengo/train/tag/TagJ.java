package com.invengo.train.tag;

/**
 * Created by LZR on 2017/6/29.
 */

public class TagJ extends BaseTag {
	private String mod;	// 机车型号
	private String num;	// 机车编号
	private String ju;	// 配属局
	private String suo;	// 配属段（所）
	private String sta;	// 机车状态
	private String kh;	// 客货类别
	private String tnoLet;	// 车次字母区
	private String tnoNum;	// 车次数字区
	private String pot;	// 双机车状态（端位）

	@Override
	protected void parseByCod(String cod) {
		mod = cod.substring(1, 4);	// 机车型号
		num = cod.substring(4, 8);	// 机车编号
		ju = cod.substring(8, 10);	// 配属局
		suo = cod.substring(10, 12);	// 配属段（所）
		sta = cod.substring(12, 13);	// 机车状态
		kh = cod.substring(13, 14);	// 客货类别
		tnoLet = cod.substring(14, 18);	// 车次字母区
		tnoNum = cod.substring(18, 23);	// 车次数字区
		pot = cod.substring(23, 24);	// 双机车状态（端位）
	}

	public String getModNam() {
		String r = tpro.getMods().get(mod).getNam();
		if (r == null) {
			r = "...";
		}
		return r;
	}

	public String getJuNam () {
		String r = tpro.getJus().get(ju).getNam();
		if (r == null) {
			r = "...";
		}
		return r;
	}

	public String getSuoNam () {
		String r = tpro.getJus().get(ju).getSuos().get(suo).getNam();
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

	public String getSta() {
		return sta;
	}

	public String getKh() {
		return kh;
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
