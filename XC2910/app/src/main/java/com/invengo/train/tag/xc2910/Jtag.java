package com.invengo.train.tag.xc2910;

import com.invengo.train.tag.TagJ;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by LZR on 2017/6/30.
 */

public class Jtag extends TagJ implements InfViewTag {
	@Override
	public List<Map<String, String>> getViewDat() {
		List<Map<String, String>> r = new ArrayList<>();
		Map<String, String> m;

		m = new HashMap<>();
		m.put("k", "属性：");
		m.put("v", getPro() + " [" + getProNam() + "]");
		r.add(m);

		m = new HashMap<>();
		m.put("k", "机车型号：");
		m.put("v", getMod());
		r.add(m);

		m = new HashMap<>();
		m.put("k", "机车编号：");
		m.put("v", getNum());
		r.add(m);

		m = new HashMap<>();
		m.put("k", "配属局：");
		m.put("v", getJu() + " [" + getJuNam() + "]");
		r.add(m);

		m = new HashMap<>();
		m.put("k", "配属段：");
		m.put("v", getSuo() + " [" + getSuoNam() + "]");
		r.add(m);

		m = new HashMap<>();
		m.put("k", "客货类别：");
		m.put("v", getKh());
		r.add(m);

		m = new HashMap<>();
		m.put("k", "机车状态：");
		m.put("v", getSta());
		r.add(m);

		m = new HashMap<>();
		m.put("k", "车种及标识符：");
		m.put("v", getTnoLet());
		r.add(m);

		m = new HashMap<>();
		m.put("k", "车次数字：");
		m.put("v", getTnoNum());
		r.add(m);

		m = new HashMap<>();
		m.put("k", "双机车状态：");
		m.put("v", getPot());
		r.add(m);

		return r;
	}
}
