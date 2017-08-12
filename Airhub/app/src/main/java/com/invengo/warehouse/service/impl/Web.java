package com.invengo.warehouse.service.impl;

import android.util.Log;
import android.webkit.JavascriptInterface;

import com.invengo.rfid.EmCb;
import com.invengo.rfid.EmPushMod;
import com.invengo.rfid.InfTagListener;
import com.invengo.rfid.tag.Base;
import com.invengo.rfid.util.Str;
import com.invengo.rfid.xc2910.Rd;
import com.invengo.warehouse.action.Ma;
import com.invengo.warehouse.entity.Tag;
import com.invengo.warehouse.enums.EmUh;
import com.invengo.warehouse.enums.EmUrl;

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

	// 初始化
	public void init (Ma m) {
		ma = m;

		// 读写器设置
		rfd.setBank("epc");
		rfd.setPm(EmPushMod.Catch);
		rfd.setTagListenter(new InfTagListener() {
			@Override
			public void onReadTag(com.invengo.rfid.tag.Base bt, InfTagListener itl) {}

			@Override
			public void onWrtTag(com.invengo.rfid.tag.Base bt, InfTagListener itl) {}

			@Override
			public void cb(EmCb e, String[] args) {
Log.i("-c-", e.name());
				switch (e) {
					case Scanning:
						ma.sendUrl(EmUrl.Scan);
						break;
					case Stopped:
						ma.sendUrl(EmUrl.Stop);
						break;
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

}
