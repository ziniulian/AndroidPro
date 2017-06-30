package com.invengo.train.tag.xc2910;

import com.invengo.train.tag.TagT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by LZR on 2017/6/30.
 */

public class Ttag extends TagT implements InfViewTag {
	@Override
	public List<Map<String, String>> getViewDat() {
		List<Map<String, String>> r = new ArrayList<>();
		Map<String, String> m;

//		m = new HashMap<>();
//		m.put("k", "源码：");
//		m.put("v", getCod());
//		r.add(m);

		m = new HashMap<>();
		m.put("k", "属性：");
		m.put("v", getPro() + " [" + getProNam() + "]");
		r.add(m);

		m = new HashMap<>();
		m.put("k", "车种车型：");
		m.put("v", getTyp() + " " + getMod());
		r.add(m);

		m = new HashMap<>();
		m.put("k", "车号：");
		m.put("v", getNum());
		r.add(m);

		m = new HashMap<>();
		m.put("k", "制造厂：");
		m.put("v", getFac() + " [" + getFacNam() + "]");
		r.add(m);

		m = new HashMap<>();
		m.put("k", "制造年月：");
		m.put("v", getTim() + " [" + getTimFmt() + "]");
		r.add(m);

		return r;
	}
}
