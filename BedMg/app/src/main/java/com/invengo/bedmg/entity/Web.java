package com.invengo.bedmg.entity;

import android.webkit.JavascriptInterface;

import com.invengo.bedmg.action.MainActivity;
import com.invengo.bedmg.dao.DbCsv;
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
	private DbCsv db = new DbCsv();

	// 初始化
	public void init (MainActivity m) {
		ma = m;

		// 读写器设置
		rfd.setBank("epc");
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
		db.open();

//// 数据库测试
//save("20170306121212,d00001,3,5,8\n20060306121212,d00003,13,0,81\n");
//Log.i("----- 1 ------", qry(null));
//Log.i("----- 2 ------", qry("d00001"));
//Log.i("----- 3 ------", findNum(null));
//Log.i("----- 4 ------", findNum("d"));
//Log.i("----- 5 ------", findNum("3"));

	}

	public void close() {
		rfd.close();
		db.close();
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

/*----------------------------------------*/

	// 获取单位
	@JavascriptInterface
	public String getUnit() {
		return "XXXXX";
	}

	// 获取车次
	@JavascriptInterface
	public String getTrip() {
		return "XXXXX";
	}

	// 保存记录
	@JavascriptInterface
	public void save(String msg) {
		db.insert("total", msg, false);
	}

	// 通过车号查询结果
	@JavascriptInterface
	public String qry (String num, String tim) {
		return db.qry(num, tim);
	}

	// 查询车号
	@JavascriptInterface
	public String findNum (String num) {
		return db.findNum(num);
	}

}
