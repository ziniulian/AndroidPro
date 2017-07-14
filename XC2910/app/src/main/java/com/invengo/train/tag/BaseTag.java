package com.invengo.train.tag;

import com.invengo.train.tag.xc2910.Nulltag;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
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
	public static void load (InputStream fs) {
		try {
			if (ts.isEmpty()) {
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder dbd = dbf.newDocumentBuilder();
				Document doc = dbd.parse(fs);

				// 读出遮罩信息
				able = doc.getElementsByTagName("able").item(0).getFirstChild().getNodeValue();

				NodeList na;
				int i, j;
				// 读出制造厂信息
				Map<String, Map<String, String>> f = new HashMap<>();
				na = doc.getElementsByTagName("ftyp");
				for (i = 0; i < na.getLength(); i++) {
					Node n = na.item(i).getFirstChild();
					Map<String, String> fac = new HashMap<>();
					String pfx = n.getNodeValue();
					f.put(pfx, fac);

					NodeList fid = doc.getElementsByTagName(pfx + "_id");
					NodeList fnam = doc.getElementsByTagName(pfx + "_nam");
					for (j = 0; j < fid.getLength(); j++) {
						fac.put(fid.item(j).getFirstChild().getNodeValue(), fnam.item(j).getFirstChild().getNodeValue());
					}
				}

				// 读出配属局信息
				Map<String, Map<String, String>> ju = new HashMap<>();
				na = doc.getElementsByTagName("jtyp");
				for (i = 0; i < na.getLength(); i++) {
					Node n = na.item(i).getFirstChild();
					Map<String, String> fac = new HashMap<>();
					String pfx = n.getNodeValue();
					ju.put(pfx, fac);

					NodeList fid = doc.getElementsByTagName("k_" + pfx);
					NodeList fnam = doc.getElementsByTagName("v_" + pfx);
					for (j = 0; j < fid.getLength(); j++) {
						fac.put(fid.item(j).getFirstChild().getNodeValue(), fnam.item(j).getFirstChild().getNodeValue());
					}
				}

				// 读出配属所信息
				Map<String, Map<String, String>> s = new HashMap<>();
				na = doc.getElementsByTagName("styp");
				for (i = 0; i < na.getLength(); i++) {
					Node n = na.item(i).getFirstChild();
					Map<String, String> fac = new HashMap<>();
					String pfx = n.getNodeValue();
					s.put(pfx, fac);

					NodeList fid = doc.getElementsByTagName("k_" + pfx);
					NodeList fnam = doc.getElementsByTagName("v_" + pfx);
					for (j = 0; j < fid.getLength(); j++) {
						fac.put(fid.item(j).getFirstChild().getNodeValue(), fnam.item(j).getFirstChild().getNodeValue());
					}
				}

				// 读出属性信息
				NodeList pid = doc.getElementsByTagName("id");
				NodeList pnam = doc.getElementsByTagName("nam");
				NodeList pcls = doc.getElementsByTagName("cls");
				NodeList pfac = doc.getElementsByTagName("facTyp");
				NodeList pju = doc.getElementsByTagName("juTyp");
				NodeList psuo = doc.getElementsByTagName("suoTyp");
				String p = new Object() {
					public String getPn()
					{
						return this.getClass().getPackage().getName();
					}
				}.getPn();
				Node n;
				for (i = 0; i < pid.getLength(); i++) {
					TagPro tp = new TagPro();
					tp.setPro(pid.item(i).getFirstChild().getNodeValue());
					tp.setProNam(pnam.item(i).getFirstChild().getNodeValue());
					tp.setCls(Class.forName(p + "." + pcls.item(i).getFirstChild().getNodeValue()));

					// 制造厂
					n = pfac.item(i).getFirstChild();
					if (n != null) {
						tp.setFacs(f.get(n.getNodeValue()));
					}

					// 配属局
					n = pju.item(i).getFirstChild();
					if (n != null) {
						tp.setJus(ju.get(n.getNodeValue()));
					}

					// 配属所
					n = psuo.item(i).getFirstChild();
					if (n != null) {
						tp.setSuos(s.get(n.getNodeValue()));
					}

					ts.put(tp.getPro(), tp);
				}
			}
			fs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
