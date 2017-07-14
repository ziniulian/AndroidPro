package com.invengo.train.tag.xc2910;

import com.invengo.train.tag.TagNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by LZR on 2017/7/10.
 */

public class Nulltag extends TagNull implements InfViewTag {
	@Override
	public List<Map<String, String>> getViewDat() {
		List<Map<String, String>> r = new ArrayList<>();
		Map<String, String> m;

		m = new HashMap<>();
		m.put("k", getCod());
		m.put("v", " ：未知标签格式");
		r.add(m);

		return r;
	}
}
