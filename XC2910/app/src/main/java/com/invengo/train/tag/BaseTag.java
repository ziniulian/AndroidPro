package com.invengo.train.tag;

import com.invengo.train.tag.xc2910.Dtag;
import com.invengo.train.tag.xc2910.Jtag;
import com.invengo.train.tag.xc2910.Ktag;
import com.invengo.train.tag.xc2910.Nulltag;
import com.invengo.train.tag.xc2910.Qtag;
import com.invengo.train.tag.xc2910.Ttag;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static com.invengo.tagdata2str.DataConver.Conver;

/**
 * Created by LZR on 2017/6/29.
 */

public abstract class BaseTag {
	private String cod;	// 标签源码
	protected TagPro tpro;	// 标签属性
	private static String able = "";	// 遮罩
	private static Map<String, TagPro> ts = new HashMap<>();	// 类型集合

	public void setCod(String cod) {
		this.cod = cod;
		parseByCod(cod);
	}

	public void setTpro(TagPro tpro) {
		this.tpro = tpro;
	}

	public String getCod() {
		return cod;
	}

	// 获取属性码
	public String getPro() {
		return tpro.getPro();
	}

	// 获取属性名
	public String getProNam () {
		return tpro.getProNam();
	}

	// 获取格式化的制造年月
	protected String getTimFmt (String s) {
		int y = Integer.parseInt(s.substring(0, 2));
		int m = Integer.parseInt(s.substring(2), 16);

		if (y > 50) {
			y += 1900;
		} else {
			y += 2000;
		}

		return String.format("%4d年%02d月", y, m);
	}

	/********************* 接口 **************************/

	// 解析标签源码
	protected abstract void parseByCod (String cod);

	/********************* 静态方法 **************************/

	// 屏蔽属性码
	private static String shield (String c) {
		String r = c.substring(0, 1);
		if (!able.contains(r)) {
			r = null;
		} else {
			// 纠正标签源码与属性码不一致的问题
			switch (r) {
				case "!":
					r = "Q";
					break;
			}
		}
		return r;
	}

	// 解析标签
	public static BaseTag parse (String c) {
		BaseTag tag = null;
		try {
			tag = ts.get(shield(c)).crtTag(c);
		} catch (Exception e) {
//			e.printStackTrace();
			tag = new Nulltag();
			tag.setCod(c.substring(0, 1));
		}
		return tag;
	}

	// 解析标签
	public static BaseTag parse (byte[] src) {
		BaseTag tag = null;
		if (src != null) {
			if (src.length == 19) {
				if (src[0] == 42) {
//				 System.arraycopy(r, 1, bt, 0, r.length -1);
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
				tag = parse(s);
			} else {
				tag = parse("?");
			}
		}
		return tag;
	}

	// 读取 XML 文件，初始化内容
	public static void load (InputStream fs, File ini) {
		try {
			// 遮罩信息
			if (!ini.exists()) {
				if (!ini.getParentFile().exists()) {
					ini.getParentFile().mkdirs();
				}
				FileOutputStream os = new FileOutputStream(ini);
				os.write("T\n".getBytes());
				os.close();
				able = "T";
			} else {
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(ini)));
				able = br.readLine();
				br.close();
			}

			// XML配置信息
			if (ts.isEmpty()) {
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder dbd = dbf.newDocumentBuilder();
				Document doc = dbd.parse(fs);

				loadPro(doc.getElementsByTagName("VehicleProperty"));	// 标签属性
				loadTyp(doc.getElementsByTagName("VehicleType"));	// 车种
				loadFac(doc.getElementsByTagName("MadeFactory"));	// 制造厂
				loadMod(doc.getElementsByTagName("VehicleModel"));	// 车型
				loadSuo(doc.getElementsByTagName("Station"));	// 配属所
				// TODO: 2017/7/28 修程
			}
			fs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 读取标签属性
	private static void loadPro (NodeList na) {
		TagPro tp;
		Node n;
		int i;

		for (i = 0; i < na.getLength(); i++) {
			n = na.item(i);

			tp = new TagPro();
			tp.setPro(n.getAttributes().getNamedItem("PropertyCode").getNodeValue());
			tp.setProNam(n.getAttributes().getNamedItem("PropertyName").getNodeValue());
			switch (tp.getPro()) {
				case "K":
					tp.setCls(Ktag.class);
					break;
				case "D":
					tp.setCls(Dtag.class);
					break;
				case "J":
					tp.setCls(Jtag.class);
					break;
				case "T":
					tp.setCls(Ttag.class);
					break;
				case "!":
				case "Q":
					tp.setCls(Qtag.class);
					break;
			}

			ts.put(tp.getPro(), tp);
		}
	}

	// 读取车种
	private static void loadTyp (NodeList na) {
		Node n;
		String k;
		ProTyp t;
		Map<String, Map<String, ProTyp>> m = new HashMap<>();
		int i;

		for (i = 0; i < na.getLength(); i++) {
			n = na.item(i);
			t = new ProTyp();
			t.setId(n.getAttributes().getNamedItem("TypeCode").getNodeValue());
			t.setNam(n.getAttributes().getNamedItem("TypeName").getNodeValue());

			k = n.getParentNode().getAttributes().getNamedItem("Property").getNodeValue();
			if (!m.containsKey(k)) {
				m.put(k, new HashMap<String, ProTyp>());
			}
			m.get(k).put(t.getId(), t);
		}

		for (Map.Entry<String, Map<String, ProTyp>> e: m.entrySet()) {
			k = e.getKey();
			for (i = 0; i < k.length(); i++) {
				ts.get(Character.toString(k.charAt(i))).setTyps(e.getValue());
			}
		}
	}

	// 读取制造厂
	private static void loadFac (NodeList na) {
		Node n;
		String k;
		ProFac f;
		Map<String, Map<String, ProFac>> m = new HashMap<>();
		int i;

		for (i = 0; i < na.getLength(); i++) {
			n = na.item(i);
			f = new ProFac();
			f.setId(n.getAttributes().getNamedItem("FactoryCode").getNodeValue());
			f.setNam(n.getAttributes().getNamedItem("ShortName").getNodeValue());
			f.setFullNam(n.getAttributes().getNamedItem("FactoryName").getNodeValue());

			k = n.getParentNode().getAttributes().getNamedItem("Property").getNodeValue();
			if (!m.containsKey(k)) {
				m.put(k, new HashMap<String, ProFac>());
			}
			m.get(k).put(f.getId(), f);
		}

		for (Map.Entry<String, Map<String, ProFac>> e: m.entrySet()) {
			k = e.getKey();
			for (i = 0; i < k.length(); i++) {
				ts.get(Character.toString(k.charAt(i))).setFacs(e.getValue());
			}
		}
	}

	// 读取制造厂
	private static void loadMod (NodeList na) {
		Node n;
		String k;
		ProMod d;
		Map<String, Map<String, ProMod>> m = new HashMap<>();
		int i;

		for (i = 0; i < na.getLength(); i++) {
			n = na.item(i);
			d = new ProMod();
			d.setId(n.getAttributes().getNamedItem("ModelCode").getNodeValue());
			d.setNam(n.getAttributes().getNamedItem("ModelName").getNodeValue());

			k = n.getAttributes().getNamedItem("Property").getNodeValue();
			if (!m.containsKey(k)) {
				m.put(k, new HashMap<String, ProMod>());
			}
			m.get(k).put(d.getId(), d);
		}

		for (Map.Entry<String, Map<String, ProMod>> e: m.entrySet()) {
			k = e.getKey();
			for (i = 0; i < k.length(); i++) {
				ts.get(Character.toString(k.charAt(i))).setMods(e.getValue());
			}
		}
	}

	// 读取配属所
	private static void loadSuo (NodeList na) {
		Node n;
		Node p;
		String k;
		String j;
		ProJu u;
		ProSuo s;
		Map<String, Map<String, ProJu>> m = new HashMap<>();
		Map<String, ProJu> mj;
		int i;

		for (i = 0; i < na.getLength(); i++) {
			n = na.item(i);
			s = new ProSuo();
			s.setId(n.getAttributes().getNamedItem("StationCode").getNodeValue());
			s.setNam(n.getAttributes().getNamedItem("StationName").getNodeValue());

			p = n.getParentNode();
			k = p.getAttributes().getNamedItem("Property").getNodeValue();
			j = p.getAttributes().getNamedItem("AreaCode").getNodeValue();
			if (!m.containsKey(k)) {
				m.put(k, new HashMap<String, ProJu>());
			}
			mj = m.get(k);
			if (!mj.containsKey(j)) {
				u = new ProJu();
				u.setId(j);
				u.setNam(p.getAttributes().getNamedItem("AreaName").getNodeValue());
				mj.put(j, u);
			} else {
				u = mj.get(j);
			}
			u.getSuos().put(s.getId(), s);
		}

		for (Map.Entry<String, Map<String, ProJu>> e: m.entrySet()) {
			k = e.getKey();
			for (i = 0; i < k.length(); i++) {
				ts.get(Character.toString(k.charAt(i))).setJus(e.getValue());
			}
		}
	}
}
