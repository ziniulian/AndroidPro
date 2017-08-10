package com.invengo.rfid.xc2910;

import com.invengo.rfid.Base;
import com.invengo.rfid.EmCb;
import com.invengo.rfid.tag.T6C;

import invengo.javaapi.core.BaseReader;
import invengo.javaapi.core.IMessage;
import invengo.javaapi.core.IMessageNotification;
import invengo.javaapi.core.MemoryBank;
import invengo.javaapi.handle.IMessageNotificationReceivedHandle;
import invengo.javaapi.protocol.IRP1.IntegrateReaderManager;
import invengo.javaapi.protocol.IRP1.PowerOff;
import invengo.javaapi.protocol.IRP1.RXD_TagData;
import invengo.javaapi.protocol.IRP1.ReadTag;
import invengo.javaapi.protocol.IRP1.Reader;
import invengo.javaapi.protocol.IRP1.WriteEpc;
import invengo.javaapi.protocol.IRP1.WriteUserData_6C;

/**
 * XC2910型标签读写器
 * Created by LZR on 2017/8/9.
 */

public class Rd extends Base implements IMessageNotificationReceivedHandle {
	private Reader rd = null;
	private boolean isConnect = false;
	private boolean isScanning = false;
	private boolean isReading = false;
	private final byte antenna = 1;
	private final byte[] defaulPwd = new byte[] {0, 0, 0, 0};

	// 连接设备
	private Runnable connectRa = new Runnable() {
		@Override
		public void run() {
			isConnect = rd.connect();
			cb(EmCb.HidProgress);
			if (isConnect) {
				cb(EmCb.Connected);
			} else {
				cb(EmCb.ErrConnect);
			}
		}
	};

	// 断开设备
	private Runnable disConnectRa = new Runnable() {
		@Override
		public void run() {
			rd.send(new PowerOff());
			rd.disConnect();
			isConnect = false;
			isScanning = false;
			isReading = false;
			cb(EmCb.DisConnected);
		}
	};

	// 扫描
	private Runnable scanRa = new Runnable() {
		@Override
		public void run() {
			ReadTag rt = new ReadTag (ReadTag.ReadMemoryBank.EPC_TID_UserData_6C);
			rd.send(rt);
			cb(EmCb.Scanning);
		}
	};

	// 停止
	private Runnable stopRa = new Runnable() {
		@Override
		public void run() {
			rd.send(new PowerOff());
			isScanning = false;
			cb(EmCb.Stopped);
		}
	};

	// 读标签
	private Runnable readRa = new Runnable() {
		@Override
		public void run() {
			ReadTag msg = new ReadTag (ReadTag.ReadMemoryBank.EPC_TID_UserData_6C, true);
			if (rd.send(msg)) {
				com.invengo.rfid.tag.Base bt = crtBt(msg.getReceivedMessage().getList_RXD_TagData()[0]);
				isReading = false;
				onReadTag(bt);
			} else {
				isReading = false;
				cb(EmCb.ErrRead);
			}
		}
	};

	// 写标签
	private class WrtRa implements Runnable {
		private com.invengo.rfid.tag.Base bt = new T6C();
		private IMessage msg;
		@Override
		public void run() {
			if (rd.send(msg)) {
				isReading = false;
				onWrtTag(bt);
			} else {
				isReading = false;
				cb(EmCb.ErrWrt);
			}
		}
	}

	// 创建基本标签
	private com.invengo.rfid.tag.Base crtBt (RXD_TagData r) {
		com.invengo.rfid.tag.Base bt = new T6C();
		RXD_TagData.ReceivedInfo ri = r.getReceivedMessage();
		byte[] bs;

		bt.setEpc(ri.getEPC());
		bt.setTid(ri.getTID());
		bt.setUse(ri.getUserData());
		bt.setBck(ri.getReserved());
		return bt;
	}

	@Override
	public void init() {
		if (rd == null) {
			rd = IntegrateReaderManager.getInstance();
			isConnect = false;
			isScanning = false;
			if (rd == null) {
				cb(EmCb.ErrConnect);
			} else {
				rd.onMessageNotificationReceived.add(this);
			}
		}
	}

	@Override
	public void open() {
		if (!isConnect) {
			cb(EmCb.ShowProgress);
			new Thread(connectRa).start();
		}
	}

	@Override
	public void close() {
		if (isConnect) {
			new Thread(disConnectRa).start();
		}
	}

	@Override
	public void read(String bankNam) {
		if (!isScanning && isConnect && !isReading) {
			isReading = true;
			switch (bankNam) {
				case "epc":
				case "tid":
				case "use":
				case "bck":
				case "all":
					new Thread(readRa).start();
					break;
				default:
					isReading = false;
					break;
			}
		}
	}

	@Override
	public void wrt(String bankNam, String dat, String tid) {
		if (!isScanning && isConnect && !isReading) {
			isReading = true;
			WrtRa r = new WrtRa();
			boolean assign = (tid != null && (tid.length() > 0));
			if (assign) {
				r.bt.setTid(tid);
			}
			switch (bankNam) {
				case "epc":
					r.bt.setEpc(dat);
					if (assign) {
						r.msg = new WriteEpc(antenna, defaulPwd, r.bt.getEpc(), r.bt.getTid(), MemoryBank.TIDMemory);
					} else {
						r.msg = new WriteEpc(antenna, defaulPwd, r.bt.getEpc());
					}
					break;
				case "use":
					r.bt.setUse(dat);
					if (assign) {
						r.msg = new WriteUserData_6C(antenna, defaulPwd, (byte)0, r.bt.getUse(), r.bt.getTid(), MemoryBank.TIDMemory);
					} else {
						r.msg = new WriteUserData_6C(antenna, defaulPwd, 0, r.bt.getUse());
					}
					break;
				default:
					isReading = false;
					return;
			}
			new Thread(r).start();
		}
	}

	@Override
	public void scan() {
		if (!isScanning && isConnect && !isReading) {
			isScanning = true;
			new Thread(scanRa).start();
		}
	}

	@Override
	public void stop() {
		if (isScanning && isConnect) {
			new Thread(stopRa).start();
		}
	}

	@Override
	public void messageNotificationReceivedHandle(BaseReader baseReader, IMessageNotification iMessageNotification) {
		if (iMessageNotification instanceof RXD_TagData) {
			onReadTag(crtBt((RXD_TagData)iMessageNotification));
		}
	}

}
