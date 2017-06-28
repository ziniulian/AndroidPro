package com.invengo.ziniulian.test.xc2910srv.net;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Created by LZR on 2017/6/28.
 */

public class UdpSrv implements Runnable {
	private boolean isgo = false;
	private byte[] tcpPot;	// TCP 的端口信息
	private int pot = 3000;	// 端口号
	private String pw = "invengoTcpPot";	// 验证口令
	private int size = 64;	// 缓存大小
	private DatagramPacket rdp = new DatagramPacket(new byte[this.size], this.size);	// 需要接收的数据
	private DatagramSocket srv = null;	// UDP 服务

	// 设置 TCP 端口
	public void setTcpPot (int tp) {
		this.tcpPot = Integer.toString(tp).getBytes();
	}

	// 获取服务的监听端口
	public int getPort () {
		return this.pot;
	}

	// 开启线程
	public Thread start () throws SocketException {
		if (!this.isgo) {
			this.srv = new DatagramSocket(this.pot);
			this.isgo = true;
			Thread t = new Thread(this);
			t.start();
			return t;
		}
		return null;
	}

	// 关闭线程
	public void stop () {
		if (this.isgo) {
			this.srv.close();
		}
	}

	@Override
	public void run() {
		try {
//Log.i("已开启 UDP 服务 ...", this.pot + "");
			while (this.isgo) {
				this.srv.receive(this.rdp);
				if (this.pw.equals(new String(this.rdp.getData(), 0, this.rdp.getLength()))) {
//Log.i("UDP 客户端信息", this.rdp.getAddress().getHostAddress() + ":" + this.rdp.getPort());
					this.srv.send(new DatagramPacket(this.tcpPot, this.tcpPot.length, this.rdp.getAddress(), this.rdp.getPort()));
					this.rdp.setLength(this.size);
				}
			}
		} catch (Exception e) {
//			e.printStackTrace();
		} finally {
			if (this.srv != null) {
				this.srv.close();
				this.srv = null;
			}
			this.isgo = false;
//Log.i("UDP 服务已关闭！", this.pot + "");
		}
	}
}
