package com.invengo.train.xc2002.entity;

import android.os.Environment;
import android.webkit.JavascriptInterface;

import com.invengo.train.rfid.EmCb;
import com.invengo.train.rfid.InfCallBack;
import com.invengo.train.rfid.tag.BaseTag;
import com.invengo.train.rfid.xc2002.Rd;
import com.invengo.train.xc2002.Ma;
import com.invengo.train.xc2002.dao.DbLocal;
import com.invengo.train.xc2002.enums.EmUh;
import com.invengo.train.xc2002.enums.EmUrl;

import java.io.File;

import static com.invengo.train.rfid.tag.BaseTag.confAble;
import static com.invengo.train.xc2002.Ma.sdDir;

/**
 * 读写器
 * Created by ziniulian on 2017/10/17.
 */

public class Web {
	private static final String confPath = "conf.xml";	// 配置文件路径

	private Rd rfd = new Rd();
	private Ma ma;
	private String appNam;
	private DbLocal db;

	public void init (Ma m) {
		ma = m;

		// 设置监听
		rfd.setCallBackListenter(new InfCallBack() {
			@Override
			public void onReadTag(BaseTag tag) {
				ma.sendUrl(EmUrl.HdRead, tag.toJson());
			}

			@Override
			public void cb(EmCb e, String[] args) {
				switch (e) {
					case ErrInit:
					case ErrConnect:
						ma.sendUrl(EmUrl.Err);
						break;
					case ErrRead:
					case ReadNull:
						ma.sendUrl(EmUrl.ReadNull);
						break;
					case Reading:
						ma.sendUrl(EmUrl.Reading);
						break;
				}
			}
		});

		try {
			// TODO: 内存卡被拔出时触发 ErrInit 事件
			db = new DbLocal(m);
			appNam = confAble(new File(Environment.getExternalStorageDirectory(), sdDir + confPath), ma.getAssets().open(confPath));
			rfd.init(ma);
		} catch (Exception e) {
//			e.printStackTrace();
			rfd.cb(EmCb.ErrInit, e.getMessage());
		}
	}

	public void open() {
		rfd.open();
	}

	public void close() {
		rfd.close();
	}

/*----------------------------------------*/

	@JavascriptInterface
	public void read() {
		rfd.read();
	}

	@JavascriptInterface
	public String getAppNam() {
		return appNam;
	}

	@JavascriptInterface
	public void dbSav(String cod, String xiu) {
		db.sav(new String[] {ma.getTim(), cod, xiu});
	}

	@JavascriptInterface
	public void dbDel(String ids) {
		db.del(new String[] {ids});
	}

	@JavascriptInterface
	public void dbClear() {
		db.del(null);
	}

	@JavascriptInterface
	public void dbSet(long id, String xiu) {
		db.set(new String[] {id + "", xiu});
	}

	@JavascriptInterface
	public String dbGet(long p, long len) {
		return db.get(p + "", len + "");
	}

	@JavascriptInterface
	public long dbCount() {
		return db.count();
	}

	@JavascriptInterface
	public void flashlight() {
		ma.fl.flashlight(false);
	}

	@JavascriptInterface
	public boolean getFlashlight() {
		return ma.fl.isFlb();
	}

	@JavascriptInterface
	public void startTim() {
		ma.sendUh(EmUh.StartTim);
	}

	@JavascriptInterface
	public void flushTim() {
		ma.sendUh(EmUh.Tim);
	}

	@JavascriptInterface
	public void exit() {
		ma.finish();
	}

}
