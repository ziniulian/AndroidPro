package com.invengo.warehouse.service.impl;

import android.os.Environment;
import android.webkit.JavascriptInterface;

import com.invengo.rfid.EmCb;
import com.invengo.rfid.EmPushMod;
import com.invengo.rfid.InfTagListener;
import com.invengo.rfid.tag.Base;
import com.invengo.rfid.util.Str;
import com.invengo.rfid.xc2910.Rd;
import com.invengo.warehouse.action.Ma;
import com.invengo.warehouse.dao.impl.DbLocal;
import com.invengo.warehouse.entity.Tag;
import com.invengo.warehouse.enums.EmLocalSql;
import com.invengo.warehouse.enums.EmUh;
import com.invengo.warehouse.enums.EmUrl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 接口实现
 * Created by LZR on 2017/8/11.
 */

public class Web {
	private Rd rfd = new Rd();
	private Ma ma;
	private String user = null;
	private String srvUrl = null;
	private DbLocal db = null;

	private String iniPath = "conf.ini";

	// 初始化
	public void init (Ma m) {
		ma = m;
		readConfig();
		db = new DbLocal(m);

		// 读写器设置
		rfd.setBank("epc");		// 测试时取消_Test
		rfd.setPm(EmPushMod.Catch);
		rfd.setTagListenter(new InfTagListener() {
			@Override
			public void onReadTag(com.invengo.rfid.tag.Base bt, InfTagListener itl) {}

			@Override
			public void onWrtTag(com.invengo.rfid.tag.Base bt, InfTagListener itl) {
				ma.sendUrl(EmUrl.WrtOk);
			}

			@Override
			public void cb(EmCb e, String[] args) {
//Log.i("-c-", e.name());
				switch (e) {
					case Scanning:
						ma.sendUrl(EmUrl.Scan);
						break;
					case Stopped:
						ma.sendUrl(EmUrl.Stop);
						break;
					case ErrWrt:
						ma.sendUrl(EmUrl.WrtErr);
					case ErrConnect:
						ma.sendUrl(EmUrl.Err);
						break;
					case Connected:
						ma.sendUh(EmUh.Connected);
						break;
				}
			}
		});
		rfd.init();
	}

	public void open() {
		rfd.open();
	}

	public void close() {
		rfd.close();
	}

	private void readConfig () {
		File f = new File(Environment.getExternalStorageDirectory(), EmLocalSql.sdDir.toString() + iniPath);

		// 创建文件
		if (!f.exists()) {
			if (!f.getParentFile().exists()) {
				f.getParentFile().mkdirs();
			}
			try {
				FileWriter w = new FileWriter(f);
				w.write("http://192.168.191.1:8080/jeesite/srv");
				w.flush();
				w.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// 读取配置
		try {
			FileReader r = new FileReader(f);
			BufferedReader br = new BufferedReader(r);
			srvUrl = br.readLine();
			br.close();
			r.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

/*----------------------------------------*/

	@JavascriptInterface
	public void scan() {
        rfd.scan();
	}

	@JavascriptInterface
	public void stop() {
        rfd.stop();
	}

    @JavascriptInterface
    public void wrt (String bankNam, String dat, String tid) {
        rfd.wrt(bankNam, dat, tid);
    }

	@JavascriptInterface
	public void signin(String un) {
        user = un;
	}

	@JavascriptInterface
	public void signout() {
        user = null;
	}

	@JavascriptInterface
	public String getUser() {
		if (user != null) {
			return user;
		} else {
			return "";
		}
	}

	@JavascriptInterface
	public String getSrvUrl() {
		return srvUrl;
	}

	@JavascriptInterface
	public String catchScanning() {
		StringBuilder r = new StringBuilder();
		Map<String, Tag> m = new HashMap<>();
		List<Base> ts = rfd.getScanning();
		String s;
		int i, n;
		n = ts.size();

		for (i = 0; i < n; i ++) {
			s = ts.get(i).getEpcDat();
//			s = Str.Bytes2Asc(ts.get(i).getEpc());
			if (m.containsKey(s)) {
				m.get(s).addOne();
			} else {
				m.put(s, new Tag());
//				m.put(s, new Tag(ts.get(i).getTidHexstr()));	// 测试用_Test
			}
		}
		rfd.clearScanning();

		r.append('{');
		for (Map.Entry<String, Tag> entry : m.entrySet()) {
			r.append('\"');
			r.append(Str.trimGarbled(entry.getKey()));
//			r.append(entry.getKey());
			r.append('\"');
			r.append(':');
			r.append(entry.getValue().getTim());
//			r.append(entry.getValue().getJson());	// 测试用_Test
			r.append(',');
		}

		n = r.length();
		if (n > 1) {
			r.deleteCharAt(n-1);
		}
		r.append('}');
		return r.toString();

//		return rfd.catchScanning();
	}

	// 本地数据库接口
	@JavascriptInterface
	public String dbGetDevInfo (String id) {
		return db.getDevInfo(id).toString();
	}

	@JavascriptInterface
	public String dbGetBorrowByDev (String id) {
		return db.getBorrowByDev(id).toString();
	}

	@JavascriptInterface
	public void dbSynDevIn(String dat) {
		db.synDevIn(new String[] {dat});
	}

	@JavascriptInterface
	public void dbSynDevUp(String[] dat) {
		db.synDevUp(dat);
	}

	@JavascriptInterface
	public void dbSynOutIn(String dat) {
		db.synOutIn(new String[] {dat});
	}

	@JavascriptInterface
	public void dbSynOutUp(String[] dat) {
		db.synOutUp(dat);
	}

	// 获取同步时间
	@JavascriptInterface
	public long getSynTim() {
		return Long.parseLong(db.getV("syntim"));
	}

	// 设置同步时间
	@JavascriptInterface
	public void setSynTim(long t) {
		db.exe(EmLocalSql.SetV.toString(), new String[] {"syntim", "" + t});
	}

}
