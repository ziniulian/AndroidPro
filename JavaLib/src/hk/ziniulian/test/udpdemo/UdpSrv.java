package hk.ziniulian.test.udpdemo;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * 简单的UDP测试，服务端
 * Created by LZR on 2017/6/26.
 */

public class UdpSrv {
	private int pot = 3000;
	private int size = 1024;	// 缓存大小
	private byte[] buf;	// 缓存区
	private DatagramSocket ds;	// UDP 套接字
	private boolean run;	// 循环执行标记
	private DatagramPacket rdp;	// 需要接收的数据

	public UdpSrv () throws SocketException {
		this.ds = new DatagramSocket(this.pot);
		this.buf = new byte[this.size];
		this.rdp = new DatagramPacket(this.buf, this.size);
	}

	// 开始监听
	public void start() throws IOException {
		this.run = true;
		while (this.run) {
			// 接收请求
			this.ds.receive(this.rdp);

			// 处理请求
			String req = new String(this.rdp.getData(), 0, this.rdp.getLength());
			String res = this.hdDat(req);

			// 测试输出
			System.out.println("客户端信息：" + this.rdp.getAddress().getHostAddress() + ":" + this.rdp.getPort());
			System.out.println(req);

			// 创建数据报
			DatagramPacket dp = new DatagramPacket(res.getBytes(), res.length(), this.rdp.getAddress(), this.rdp.getPort());

			// 响应客户端
			this.ds.send(dp);

			//由于 rdp 在接收了数据之后，其内部消息长度值会变为实际接收的消息的字节数，
			//所以这里要将 rdp 的内部消息长度重置
			this.rdp.setLength(this.size);
		}
		this.ds.close();
	}

	// 处理请求
	private String hdDat (String dat) throws UnknownHostException {
		String r;
		switch (dat) {
			case "quit":
			case "exit":
			case "bye":
				this.run = false;
				r = "bye bye";
				break;
			case "yourIp?":
				r = InetAddress.getLocalHost().getHostAddress() + ":" + this.pot;
				break;
			default:
				r = "Hi~~";
				break;
		}
		return r;
	}

	// 测试程序
	public static void main(String[] args) {
		try {
			UdpSrv us = new UdpSrv();
			System.out.println("服务开始 ...");
			us.start();
			System.out.println("服务终止！");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
