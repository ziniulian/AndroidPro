package test.com.invengo.ziniulian.java;


import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.hwpf.usermodel.Table;
import org.apache.poi.hwpf.usermodel.TableCell;
import org.apache.poi.hwpf.usermodel.TableIterator;
import org.apache.poi.hwpf.usermodel.TableRow;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HdDoc {
	private Map<String, List<String[]>> mdat = null;

	public static void main(String[] args) {
//		System.out.println("Hello World! 你好");

		if (args.length == 0) {
			System.out.println("参数1：doc文档路径；参数2：输出日志路径。");
		} else {
			HdDoc hd = new HdDoc();
			if (args.length == 1) {
				hd.start(args[0]);
				System.out.println("\n----------------\n");
				System.out.println(hd.toString());
			} else {
				hd.start(args[0]).toFile(args[1]);
				System.out.println("完成！");
			}
		}

//		System.out.println(new HdDoc().start("D:\\Doc\\Work\\2017\\HdDoc\\dat\\AEI-S1\\test.docx").toString());
//		new HdDoc().start("D:\\Doc\\Work\\2017\\HdDoc\\dat\\AEI-S1").toFile("D:\\HdDoc.txt");
//		System.out.println(new HdDoc().start("D:\\Doc\\Work\\2017\\HdDoc\\dat\\AEI-S1").toString());
	}

	// 开始执行
	public HdDoc start (String path) {
		File f = new File(path);
		if (mdat != null) {
			mdat.clear();
			mdat = null;
		}
		mdat = new HashMap<>();
		loopDir(f);
		return this;
	}

	// 获取结果
	public Map<String, List<String[]>> getDat () {
		return mdat;
	}

	// 输出结果
	public String toString() {
		if (mdat != null && !mdat.isEmpty()) {
			StringBuilder s = new StringBuilder();
			for (Map.Entry e : mdat.entrySet()) {
				s.append((String)e.getKey());
				s.append('\n');
				List d = (List)e.getValue();
				for (Object o : d) {
					String[] v = (String[]) o;
					s.append(v[0]);
					s.append(':');
					s.append(v[1]);
					s.append('\n');
				}
				s.append('\n');
			}
			return s.toString();
		} else {
			return "";
		}
	}

	// 将结果写入文件
	public void toFile (String path) {
		try {
			FileOutputStream f = new FileOutputStream(path);
			OutputStreamWriter os = new OutputStreamWriter(f, "UTF-8");
			os.write(toString());
			os.flush();
			os.close();
			f.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 循环文件夹
	private void loopDir (File f) {
		if (f.exists()) {
			if (f.isDirectory()) {
				File fs[] = f.listFiles();
				if (fs != null) {
					for (File fa: fs) {
						loopDir(fa);
					}
				}
			} else {
				String fnam = f.getPath();

//				System.out.println(fnam);
				List<String[]> d = checkFile(f);
				if (d != null) {
					System.out.println("OK! " + fnam);
					mdat.put(fnam, d);
				} else {
					System.out.println("Err! " + fnam);
				}
//				System.out.println("============");
			}
		}
	}

	// 文件处理
	private List<String[]> checkFile (File f) {
		List<String[]> r = null;
		try {
			if (f.getName().endsWith(".doc")) {
				FileInputStream stream = new FileInputStream(f);
				r = parsDoc(stream);
			} else if (f.getName().endsWith(".docx")) {
				FileInputStream stream = new FileInputStream(f);
				parsDocx(stream);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return r;
	}

	// 解析 docx 文件
	private List<String[]> parsDocx (FileInputStream stream) {
		try {
			XWPFDocument doc = new XWPFDocument(stream);
//			System.out.println(doc.getTables().size());
			doc.close();
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	// 解析 doc 文件
	private List<String[]> parsDoc (FileInputStream stream) {
		List<String[]> r = null;

		String s;
		try {
			HWPFDocument doc = new HWPFDocument(stream);
			Range range = doc.getRange();
			TableIterator ts = new TableIterator(range);

			if (ts.hasNext()) {
				for (int i = 0; i < doc.getRange().numParagraphs(); i++) {
					s = doc.getRange().getParagraph(i).text().trim();
					if (s.length() > 0) {
						if (s.indexOf("服务记录表") > 0) {
							r = new ArrayList<>();
							Table t = ts.next();
							TableRow row;
							TableCell c;
							String key;
							int stat;	// 1:键,	2:值
							String[] mk = new String[10];

							for (i = 0; i < t.numRows(); i++) {
								row = t.getRow(i);
								if (row.numCells() > 1) {
//									System.out.println(row.numCells());
									stat = 1;
									key = "";
									for (int j = 0; j < row.numCells(); j++) {
										c = row.getCell(j);
										s = c.text().trim().replace(" ", "");
										if (stat == 1) {
											if (s.length() == 0) {
												if (mk[j] != null) {
													key = mk[j];
													switch (key) {
														case "磁钢型号":
															stat = 2;
															break;
													}
												}
											} else {
												if (key.length() == 0) {
													key = s;
												} else {
													key += ":" + s;
												}

												switch (s) {
													case "磁钢距天线":
													case "INI文件参数设置":
														mk[j] = key;
														stat = 1;
														break;
													case "磁钢型号":
														mk[j] = key;
														stat = 2;
														break;
													default:
														mk[j] = null;
														stat = 2;
														break;
												}
											}
										} else {
											r.add(new String[]{key, s});
//											System.out.println(key + " : " + s);
											stat = 1;
											key = "";
										}
									}
								} else {
									break;
								}
							}
						}
						break;
					}
				}
			}

			doc.close();
			stream.close();
		} catch (IOException e) {
//			e.printStackTrace();
		}
		return r;
	}
}
