package com.invengo.bedmg.entity;

import android.webkit.JavascriptInterface;

import com.invengo.bedmg.action.MainActivity;
import com.invengo.bedmg.enums.EmUh;
import com.invengo.bedmg.enums.EmUrl;
import com.invengo.rfid.EmCb;
import com.invengo.rfid.EmPushMod;
import com.invengo.rfid.InfTagListener;
import com.invengo.rfid.xc2910.Rd;

/**
 * Created by LZR on 2017/9/1.
 */

public class Web {
	private Rd rfd = new Rd();
	private MainActivity ma;
	private String user = null;

	// 初始化
	public void init (MainActivity m) {
		ma = m;

		// 读写器设置
//		rfd.setBank("epc");
		rfd.setHex(true);
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
		return rfd.catchScanning(true);
	}

	@JavascriptInterface
	public void wrt (String bankNam, String dat, String tid) {
		rfd.wrt(bankNam, dat, tid);
	}

}
