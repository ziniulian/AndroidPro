package com.invengo.xc2910.rfid;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import static com.invengo.tagdata2str.DataConver.Conver;

/**
 * 标签解析
 * Created by LZR on 2017/5/24.
 */

public class ParseTag {
	public static Map<String, String> parseTag (byte[] src) {
		if (src == null || src.length != 19) {
			return null;
		} else {
			if (src[0]==42) {
				// System.arraycopy(r, 1, bt, 0, r.length -1);
				int i;
				for (i = 1; i < src.length; i++) {
					src[i - 1] = src[i];
				}
				src[i - 1] = 0;
			}

			String s = Conver(src, 0);
			// TC50   400000211A12C
			// !C64K  Q06505812A94C
			// KYW25T 677083E073 066
			// J10489001401AH  TK88188
			// D3010000002101   G12345B
			switch (s.substring(0, 1)) {
				case "T":
					return parseT(s);
				default:
					Log.i(" -- parseTag -- ", s);
					return null;
			}
		}
	}

	// 解析车辆标签
	public static Map<String, String> parseT (String src) {
		String t = "T";
		String s;
		Map<String, String> r = new HashMap<>();
		r.put("tagCode", src);

		// 属性
		// s = src.substring(0, 1);
		s = t;
		r.put("property", s);
		r.put("propertyAlias", parsePro(s));

		//车种
		s = src.substring(1, 2);
		r.put("trainType", s);

		//车型
		s = src.substring(2, 7);
		r.put("trainModel", s);

		//车号
		s = src.substring(7, 14);
		r.put("trainNumber", s);

		//制造厂
		s = src.substring(16, 17);
		r.put("trainFactory", s);
		r.put("trainFactoryAlias", parseFac(t, s));

		//制造年月
		s = src.substring(17, 20);
		r.put("trainDate", s);
		r.put("trainDateAlias", parseDate(s));

		return r;
	}

	// 翻译时间
	public static String parseDate (String s) {
		int y = Integer.parseInt(s.substring(0, 2));
		int m = Integer.parseInt(s.substring(2), 16);

		if (y > 50) {
			y += 1900;
		} else {
			y += 2000;
		}

		return String.format("%4d年%02d月", y, m);
	}

	// 翻译车属性
	public static String parsePro (String pro) {
		return com.invengo.xc2910.rfid.DicPro.getTyp(pro);
	}

	// 翻译制造厂
	public static String parseFac (String pro, String fac) {
		return com.invengo.xc2910.rfid.DicPro.getFac(pro, fac);
	}
}
