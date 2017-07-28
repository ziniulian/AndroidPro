package com.invengo.train.tag.xc2910;

import com.invengo.train.tag.TagD;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by LZR on 2017/6/30.
 */

public class Dtag extends TagD implements InfViewTag {
	@Override
	public List<Map<String, String>> getViewDat() {
		List<Map<String, String>> r = new ArrayList<>();
		Map<String, String> m;

		m = new HashMap<>();
		m.put("k", "属性：");
		m.put("v", getPro() + " [" + getProNam() + "]");
		r.add(m);

		m = new HashMap<>();
		m.put("k", "型号：");
		m.put("v", getMod() + " [" + getModNam() + "]");
		r.add(m);

		m = new HashMap<>();
		m.put("k", "车辆编号：");
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
		m.put("k", "车次字母区：");
		m.put("v", getTnoLet());
		r.add(m);

		m = new HashMap<>();
		m.put("k", "车次数字区：");
		m.put("v", getTnoNum());
		r.add(m);

		m = new HashMap<>();
		m.put("k", "端位码：");
		m.put("v", getPot());
		r.add(m);

		return r;
	}
}
