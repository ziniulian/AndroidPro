package com.invengo.train.tag;

/**
 * Created by LZR on 2017/6/29.
 */

public class TagT extends BaseTag {
	private String typ;	// 车种
	private String mod;	// 车型
	private String num;	// 车号
	private String lng;	// 换长
	private String fac;	// 制造厂
	private String tim;	// 制造年月

	@Override
	protected void parseByCod(String cod) {
		typ = cod.substring(1, 2);	// 车种
		mod = cod.substring(2, 7);	// 车型
		num = cod.substring(7, 14);	// 车号
		lng = cod.substring(14, 16);	// 换长
		fac = cod.substring(16, 17);	// 制造厂
		tim = cod.substring(17, 20);	// 制造年月
	}

	public String getTimFmt() {
		return super.getTimFmt(tim);
	}

	public String getFacNam() {
		String r = tpro.getFacs().get(fac);
		if (r == null) {
			r = "...";
		}
		return r;
	}

	public String getTyp() {
		return typ;
	}

	public String getMod() {
		return mod;
	}

	public String getNum() {
		return num;
	}

	public String getLng() {
		return lng;
	}

	public String getFac() {
		return fac;
	}

	public String getTim() {
		return tim;
	}
}
