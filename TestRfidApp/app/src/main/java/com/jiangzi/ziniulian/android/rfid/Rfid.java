package com.jiangzi.ziniulian.android.rfid;

import android.util.Log;
import android.util.Xml;

import com.atid.lib.dev.ATRfidManager;
import com.atid.lib.dev.ATRfidReader;
import com.atid.lib.dev.event.RfidReaderEventListener;
import com.atid.lib.dev.rfid.type.ActionState;
import com.atid.lib.dev.rfid.type.BankType;
import com.atid.lib.dev.rfid.type.ConnectionState;
import com.atid.lib.dev.rfid.type.ResultCode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by ziniulian on 2017/1/20.
 */

public class Rfid implements Serializable {

	private ATRfidReader reader = null;
	private OnTagChgListener otcLc = null;
	private OnListChgListener olcLc = null;
	private Boolean tagEvt = false;
	private BankType btem = null;
	private int wordUnit = 2;	// 一个字的单位
	private HashMap<String, HashMap<String, Object>> dat = new HashMap<String, HashMap<String, Object>>();
	private ArrayList<HashMap<String, Object>> lvdat = new ArrayList<HashMap<String, Object>>();

	// 初始化
	public boolean init() {
		// 创建读写器
		this.reader = ATRfidManager.getInstance();

		// 测试读写器是否创建成功
		if (this.reader == null) {
			// 加载错误
			return false;
		} else {
			// 添加事件
			this.reader.setEventListener(new RfidLc());
			return true;
		}
	}

	// 唤醒读写器
	public void wakeUp () {
		ATRfidManager.wakeUp();
	}

	// 暂停读写器
	public void sleep () {
		ATRfidManager.sleep();
	}

	// 关闭读写器
	public void destroy () {
		ATRfidManager.onDestroy();
	}

	// 停止
	public void stop () {
		this.btem = null;
		this.reader.stop();
	}

	// 扫码
	public void scan () {
		this.reader.inventory6cTag();
	}

	// 读码
	public void read (BankType bank, int offset, int length) {
		if (this.btem == null) {
			this.btem = bank;
			this.reader.readMemory6c(bank, offset, length);
		}
	}

	// 清空数据
	public void clearDat () {
		this.lvdat.clear();
		this.dat.clear();
	}

	// 获取列表控件数据
	public ArrayList<HashMap<String, Object>> getLvDat () {
		return lvdat;
	}

	// 设置是否为标签事件
	public void setTagEvt (boolean te) {
		this.tagEvt = te;
	}

	// 设置列表更新事件
	public void setListEventListener (OnListChgListener oc) {
		this.olcLc = oc;
	}

	// 设置列表更新事件
	public void setTagEventListener (OnTagChgListener oc) {
		this.otcLc = oc;
	}

	// 解析数据
	public String parseDat (String dat) {
//Log.i("----- dat ------ ", dat);
//int dn = dat.length()/2;
		String digital = "0123456789ABCDEF";
		int n = dat.length();
		byte[] bs = new byte[n/2];
		for (int i=0; i<n; i+=2) {
			/*
			// 该转换方式存在问题，默认只能转换 -128 ～ 127 之间的数字。
			String s = "" + dat.charAt(i) + dat.charAt(i+1);
			bs[i/2] = Byte.parseByte(s, 16);
			*/

			// 新的十六进制字符转换方式：
			bs[i/2] = (byte) (digital.indexOf(dat.charAt(i)) * 16 + digital.indexOf(dat.charAt(i+1)));
		}
//String ds = new String(bs).trim();
//Log.i("----- new dat ------ ", toDat(ds, dn));

		return new String(bs).trim();
	}

	// 转换数据
	public String toDat (String str, int max) {
//Log.i("----- str ------ ", str);
//Log.i("----- max ------ ", Integer.toString(max));
		String r = "";
		String s;
		byte[] bs;
		int n = str.length();
//Log.i("----- n1 ------ ", Integer.toString(n));
		if (n > max) {
			n = max;
			bs = str.substring(0, n).getBytes();
		} else {
			bs = str.getBytes();
		}
		while (bs.length > max) {
			n --;
			bs = str.substring(0, n).getBytes();
		}
//Log.i("----- n2 ------ ", Integer.toString(n));

		for (n=0; n<bs.length; n++) {
			// s = Integer.toString(bs[n], 16);	// 该方法会将大于 127 的数转换为负值
			s = Integer.toHexString(bs[n] & 0xFF).toUpperCase();
			if (s.length() < 2) {
				s = "0" + s;
			}
			r += s;
		}
		for (; n<max; n++) {
			r += "00";
		}

		return r;
	}

	// 解析ID
	private String parseId (String s) {
		return s.substring(4);
	}

	// 创建标签
	private void crtTag (String id, String epc, String tid, String usr, String bck, int tim) {
		HashMap<String, Object> r = null;

		if (id != null) {
			r = this.dat.get(id);
			if (r == null) {
				// 创建标签
				r = new HashMap<>();
				r.put("epc", id);
				r.put("epcNam", parseDat(id));
				r.put("tim", 0);
				this.dat.put(id, r);
				this.lvdat.add(r);
			}

			if (epc != null && !(epc.equals(id))) {
				// 换ID
				this.dat.remove(id);
				r.put("epc", epc);
				r.put("epcNam", parseDat(epc));
				this.dat.put(epc, r);
			}

			if (tid != null) {
				r.put("tid", tid);
				r.put("tidNam", parseDat(tid));
			}

			if (usr != null) {
				r.put("usr", usr);
				r.put("usrNam", parseDat(usr));
			}

			if (bck != null) {
				r.put("bck", bck);
				r.put("bckNam", parseDat(bck));
			}

			if (tim > 0) {
				r.put("tim", (Integer)r.get("tim") + tim);

			}
		}

		if (tagEvt) {
			this.otcLc.onTagChg(r);
		} else {
			this.olcLc.onTagChg(r);
		}
	}

	// 处理RFID事件
	private class RfidLc implements RfidReaderEventListener {

		@Override
		public void onReaderStateChanged(ATRfidReader atRfidReader, ConnectionState connectionState) {
			// Log.i("----------", "state changed");
		}

		@Override
		public void onReaderActionChanged(ATRfidReader atRfidReader, ActionState actionState) {
			// Log.i("----------", "action changed");
		}

		@Override
		public void onReaderReadTag(ATRfidReader atRfidReader, String s, float v, float v1) {
			// Log.i("----------", "read tag ：" + s + " , " + Float.toString(v) + " , " + Float.toString(v1));
			crtTag(parseId(s), null, null, null, null, 1);
		}

		@Override
		public void onReaderResult(ATRfidReader atRfidReader, ResultCode resultCode, ActionState actionState, String s, String s1, float v, float v1) {
//			Log.i("----------", "result ：" + s + " , " + s1 + " , " + Float.toString(v) + " , " + Float.toString(v1));
			switch (btem.name()) {
				case "EPC":
					crtTag(parseId(s), s1, null, null, null, 0);
					break;
				case "TID":
					crtTag(parseId(s), null, s1, null, null, 0);
					break;
				case "User":
					crtTag(parseId(s), null, null, s1, null, 0);
					break;
				case "Reserved":
					crtTag(parseId(s), null, null, null, s1, 0);
					break;
			}
		}
	}

	// RFID标签更新事件
	public interface OnTagChgListener {
		void onTagChg (HashMap<String, Object> tag);
	}

	// RFID列表更新事件
	public interface OnListChgListener {
		void onTagChg (HashMap<String, Object> tag);
	}
}
