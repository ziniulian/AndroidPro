package hk.ziniulian.test.udpandtcpdemo;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * 先 UDP 广播，再 TCP 连接测试 的 UDP 服务端
 * Created by LZR on 2017/6/27.
 */

public class UdpSrv implements Runnable {
	private boolean isgo = false;
	private byte[] tcpPot;	// TCP 的端口信息
	private int pot = 3000;	// 端口号
	private String pw = "invengoTcpPot";	// 验证口令
	private int size = 64;	// 缓存大小
	private DatagramPacket rdp = new DatagramPacket(new byte[this.size], this.size);	// 需要接收的数据
	private DatagramSocket srv = null;	// UDP 服务

	public UdpSrv (int tp) {
		this.tcpPot = Integer.toString(tp).getBytes();
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
//			System.out.println(this.pot + " 端口，已开启 UDP 服务 ...");
			while (this.isgo) {
				this.srv.receive(this.rdp);
				if (this.pw.equals(new String(this.rdp.getData(), 0, this.rdp.getLength()))) {
					System.out.println("UDP 客户端信息：" + this.rdp.getAddress().getHostAddress() + ":" + this.rdp.getPort());
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
//			System.out.println(this.pot + " 端口，UDP 服务已关闭！");
		}
	}

	public static void main(String[] args) {
		try {
			TcpSrv ts = new TcpSrv();
			ts.start();
			UdpSrv us = new UdpSrv(ts.getPort());
			us.start();

			System.out.println("服务已开启 ...");
			System.in.read();
			System.out.println("服务已关闭！");

			us.stop();
			ts.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
