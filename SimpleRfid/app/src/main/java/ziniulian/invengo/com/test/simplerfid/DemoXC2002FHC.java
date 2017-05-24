package ziniulian.invengo.com.test.simplerfid;

import android.util.Log;

import com.atid.lib.system.ModuleControl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import android_serialport_api.SerialPort;

/**
 * XC2002_FHC 型便携式标签读出器 挂接在 AT911N 上的 串口读取测试模块
 * Created by LZR on 2017/5/23.
 */

public class DemoXC2002FHC {
	private static final String TAG = "---- XC2002_FHC ----";

	private static final int br = 9600;	// 波特率 （原接口使用的波特率为 ：115200）
	private static final String drv = "/dev/ttyS4";	// 串口驱动名

	private OnTagListener tl = null;
	private SerialPort sp = null;	// 串口对象
	private ReadThread rt = null;	// 读取线程
	private boolean lock = false;	// 指令锁

	// 开启电源
	private void powerOn () {
		ModuleControl.powerRfidEx(true, 2, 4);
	}

	// 关闭电源
	private void powerOff () {
		ModuleControl.powerRfidEx(false, 2, 4);
	}

	// 读数据线程
	private class ReadThread extends Thread {
		private static final String TAG = "-------- 串口读数据";
		private InputStream is = null;	// 串口的输入流

		ReadThread (InputStream input) {
			this.is = input;
		}

		@Override
		public void run() {
			super.run();
			if (this.is != null) {
				int n = 0;
				int j = 0;
				byte[] r = null;
				while(!isInterrupted()) {
					int size;
					try {
						byte[] buf = new byte[64];
//Log.i(TAG, "挂机 ...");
						size = this.is.read(buf);
						if (size > 0) {
//							Log.i(TAG, "------------ s");
//							Log.i(TAG, Integer.toString(size));
//							for (int i = 0; i < size; i++) {
//								Log.i(TAG, Integer.toHexString(buf[i]));
//							}
//							Log.i(TAG, "------------ e");

							int i = 0;
							if (size > 4 && buf[0] == 0xFFFFFFaa && buf[1] == 0x55 && buf[3] == 0x32) {
								if (buf[2] == 7) {
									// 没有搜索到标签
									hdTag(null);
								} else {
									n = buf[2] - 6;
									r = new byte[n];
									i = 4;
									j = 0;
								}
							}
							if (j < n) {
								for (; i < size; i++) {
									r[j] = buf[i];
									j++;
									if (j == n) {
										j = 0;
										n = 0;
										hdTag(r);
										break;
									}
								}
							}
						}
					} catch (IOException e) {
//						e.printStackTrace();
						Log.e(TAG, "读取失败");
						return;
					}
				}
//Log.i(TAG, "Over");
			}
		}
	}

	// 发送数据
	private void send (byte command) {
//		// 指令字说明：
//		QueryStatus = 0x30,		// 查询状态
//		QueryVersion = 0x31,	// 查询版本 （在目前测试模块中，该指令无效）
//		QueryTag = 0x32			// 查询标签

		// 数据包长度
		int len = 6;

		byte[] d = new byte[len];
		d[0] = 0xFFFFFFaa;
		d[1] = 0x55;	// 0xaa+0x55为帧头
		d[2] = (byte)len;	// 长度
		d[3] = command;	//指令字

		int crc = Crc.check(d, 0, len - 2);	// 总长度, 包括帧头一起校验
//Log.i(TAG, Integer.toHexString(crc));
		d[4] = (byte)(crc >> 8);
		d[5] = (byte)(crc & 0x0FF);	// 最后两位为 CRC 校验码

		try {
			this.sp.getOutputStream().write(d);
		} catch (IOException e) {
//			e.printStackTrace();
			Log.e(TAG, "发送失败");
		}
	}

	// 处理标签
	private void hdTag (byte[] r) {
		if (this.tl != null) {
			this.tl.onTag(r);	// 触发事件
		}
		this.lock = false;
	}

	// 读标签
	public void qryTag() {
		if (!this.lock) {
			this.lock = true;
			this.send((byte)0x32);
		}
	}

	// 设置监听
	public void setOnTagListener (OnTagListener otl) {
		this.tl = otl;
	}

	// 关闭
	public void close () {
		// 关闭串口
		if (this.sp != null) {
			// 关闭读数据线程
			this.rt.interrupt();
			this.rt = null;
			this.sp.close();
			this.sp = null;
		}

		// 关闭电源
		powerOff ();
	}

	// 打开
	public void open () {
		// 开启电源
		powerOn();

		// 开启串口
		if (this.sp == null) {
			try {
				this.sp = new SerialPort(new File(drv), br, 0);
			} catch (Exception e) {
//				e.printStackTrace();
				Log.e(TAG, "串口创建失败");
				return;
			}

			// 启动读数据线程
			this.rt = new ReadThread(this.sp.getInputStream());
			this.rt.start();
		}
	}

}
