package com.invengo.xc2910.rfid;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * 通过网络传输数据
 * Created by LZR on 2017/6/28.
 */

public class DatSender implements Runnable {
	private int upot = 3000;	// UDP 服务端端口
	private byte[] pw = "invengoTcpPot".getBytes();	// UDP 验证口令
	private int timOut = 500;	// 接收数据的超时时间（毫秒）
	private int max = 3;	// 重发数据的最多次数
	private int size = 32;	// 缓存大小
	private String ip = null;	// TCP 服务端IP
	private int pot = 0;	// TCP 服务端端口

	// 为支持安卓线程运行追加的属性
	private String tdat = null;	// 线程运行时需要传输的数据
	private OnDatSendListener dsl = null;	// 线程运行时的信息回调

	// 获取广播地址
	public String getBroadcast () throws SocketException {
		for (Enumeration<NetworkInterface> niEnum = NetworkInterface.getNetworkInterfaces(); niEnum.hasMoreElements(); ) {
			NetworkInterface ni = niEnum.nextElement();
			if (!ni.isLoopback() && ni.isUp()) {
				for (InterfaceAddress ia : ni.getInterfaceAddresses()) {
					InetAddress b = ia.getBroadcast();
					if (b != null) {
						return b.getHostAddress();
					}
				}
			}
		}
		return null;
	}

	// 连接 UDP 获取 TCP 信息
	public boolean findTcp (String uip) {
		if (this.ip != null && this.pot != 0) {
			return true;
		}

		DatagramSocket ds = null;
		boolean ok = false;
		try {
			ds = new DatagramSocket();	// UDP 套接字
			ds.setSoTimeout(this.timOut);	// 设置超时

			if (uip == null) {
				uip = this.getBroadcast ();	// 自动获取广播地址
				if (uip == null) {
					return false;
				}
			}
//Log.i("--- UDP ---", uip);

			int tim = 0;	// 重发数据的次数
			DatagramPacket dp = new DatagramPacket(this.pw, this.pw.length, InetAddress.getByName(uip), this.upot);	// 要发送的数据
			DatagramPacket rdp = new DatagramPacket(new byte[this.size], this.size);	// 要接收的数据
			while (!ok && (tim < this.max)) {
				ds.send(dp);	// 发送数据
				try {
					ds.receive(rdp);	// 接收数据
					this.ip = rdp.getAddress().getHostAddress();	// 设置IP
					this.pot = Integer.parseInt(new String(rdp.getData(), 0, rdp.getLength()));	// 设置端口
					ok = true;
//Log.i("--- TCP ---", this.ip + ":" + this.pot);
				}catch(InterruptedIOException e) {
					tim ++;
//Log.i("--- UDP ---", "超时，" + tim);
				}
			}
		} catch (Exception e) {
//			e.printStackTrace();
		} finally {
			if (ds != null) {
				ds.close();
			}
		}
		return ok;
	}

	// 数据传输
	public boolean send(String dat, String uip) {
		boolean r = false;
		if (this.findTcp(uip)) {
			try {
				Socket soc = new Socket(this.ip, this.pot);	// 创建连接
				soc.setSoTimeout(this.timOut * max);	// 设置超时

				// 发送数据
				OutputStream os = soc.getOutputStream();
				PrintWriter pw = new PrintWriter(os);
				pw.write(dat);
				pw.flush();
				soc.shutdownOutput();
//Log.i("--- TCP ---", "已发送");

				// 接收返回
				InputStream is = soc.getInputStream();
				int v = is.read();
//Log.i("--- TCP ---", "已接收，" + v);
				r = true;

				// 关闭资源
				is.close();
				pw.close();
				os.close();
				soc.close();
			} catch (IOException e) {
				this.ip = null;
				this.pot = 0;
//				e.printStackTrace();
			}
		}
		return r;
	}

	// 数据传输
	public boolean send(String dat) {
		return this.send(dat, null);
	}


/****************** 为支持安卓线程运行追加的方法 ********************/

	// 线程级的数据传输
	public Thread tsend (String dat) {
		if (dat != null && this.tdat == null && this.dsl != null) {
			this.tdat = dat;
			Thread t = new Thread(this);
			t.start();
			return t;
		}
		return null;
	}

	// 设置监听
	public void setOnDatSendListener (OnDatSendListener odsl) {
		this.dsl = odsl;
	}

	@Override
	public void run() {
		if (this.tdat != null) {
			this.dsl.onDatSended(this.send(this.tdat));
			this.tdat = null;
		}
	}
}
