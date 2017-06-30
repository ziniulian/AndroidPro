package com.invengo.train.tag;

/**
 * Created by LZR on 2017/6/29.
 */

public class TagK extends BaseTag {
	private String typ;	// 车种车型
	private String num;	// 车号
	private String fac;	// 制造厂
	private String tim;	// 制造年月
	private String pot;	// 端位
	private String cap;	// 定员

	@Override
	protected void parseByCod(String cod) {
		typ = cod.substring(1, 7);	// 车种车型
		num = cod.substring(7, 13);	// 车号
		fac = cod.substring(13, 14);	// 制造厂
		tim = cod.substring(14, 17);	// 制造年月
		pot = cod.substring(17, 18);	// 端位
		cap = cod.substring(18, 21);	// 定员
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

	public String getNum() {
		return num;
	}

	public String getFac() {
		return fac;
	}

	public String getTim() {
		return tim;
	}

	public String getPot() {
		return pot;
	}

	public String getCap() {
		return cap;
	}
}
