package com.invengo.xc2910.rfid;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * 车属性集合
 * Created by LZR on 2017/5/25.
 */

public class DicPro {
	private static Map<String, DicPro> dat = null;

	private String id;	// 编号
	private String nam;	// 名称
	private Map<String, String> facs = null;	// 对应的制造厂编号

	public static void load (InputStream fs) {
		if (dat == null) {
			try {
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder dbd = dbf.newDocumentBuilder();
				Document doc = dbd.parse(fs);

				// 读出制造厂信息
				Map<String, Map<String, String>> f = new HashMap<>();
				NodeList na = doc.getElementsByTagName("ftyp");
				for (int i = 0; i < na.getLength(); i++) {
					Node n = na.item(i).getFirstChild();
					Map<String, String> fac = new HashMap<>();
					String pfx = n.getNodeValue();
					f.put(pfx, fac);

					NodeList fid = doc.getElementsByTagName(pfx + "_id");
					NodeList fnam = doc.getElementsByTagName(pfx + "_nam");
					for (int j = 0; j < fid.getLength(); j++) {
						fac.put(fid.item(j).getFirstChild().getNodeValue(), fnam.item(j).getFirstChild().getNodeValue());
					}
				}

				// 读出属性信息
				NodeList pid = doc.getElementsByTagName("id");
				NodeList pnam = doc.getElementsByTagName("nam");
				NodeList pfac = doc.getElementsByTagName("facTyp");
				dat = new HashMap<>();
				for (int i = 0; i < pid.getLength(); i++) {
					DicPro dp = new DicPro();
					dp.id = pid.item(i).getFirstChild().getNodeValue();
					dp.nam = pnam.item(i).getFirstChild().getNodeValue();
					dp.facs = f.get(pfac.item(i).getFirstChild().getNodeValue());
					dat.put(dp.id, dp);

//					Log.i("----- id -----", dp.id);
//					Log.i("----- nam -----", dp.nam);
//					for (String s : dp.facs.keySet()) {
//						Log.i (s, dp.facs.get(s));
//					}
//					Log.i("----- end -----", dp.id);
				}
			} catch (Exception e) {
//				e.printStackTrace();
			}
		}
	}

	public static String getTyp (String pro) {
		if (dat != null) {
			return dat.get(pro).nam;
		} else {
			return "...";
		}
	}

	public static String getFac (String pro, String fac) {
		String r = "...";
		if (dat != null) {
			r = dat.get(pro).facs.get(fac);
			if (r == null) {
				r = "...";
			}
		}
		return r;
	}
}
