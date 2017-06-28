package hk.ziniulian.test.udpandtcpdemo;

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
 * 先 UDP 广播，再 TCP 连接测试 的 客户端
 * Created by LZR on 2017/6/27.
 */

public class Client {
	private int upot = 3000;	// UDP 服务端端口
	private byte[] pw = "invengoTcpPot".getBytes();	// UDP 验证口令
	private int timOut = 500;	// 接收数据的超时时间（毫秒）
	private int max = 3;	// 重发数据的最多次数
	private int size = 32;	// 缓存大小
	private String ip = null;	// TCP 服务端IP
	private int pot = 0;	// TCP 服务端端口

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

	// 重新获取 TCP 信息
	public boolean findTcpAgain (String uip) {
		this.ip = null;
		this.pot = 0;
		return findTcp (uip);
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

			int tim = 0;	// 重发数据的次数
			byte[] buf = new byte[this.size];	// 缓存区
			DatagramPacket dp = new DatagramPacket(this.pw, this.pw.length, InetAddress.getByName(uip), this.upot);	// 要发送的数据
			DatagramPacket rdp = new DatagramPacket(buf, this.size);	// 需要接收的数据
			while (!ok && (tim < this.max)) {
				// 发送数据
				ds.send(dp);
				try {
					ds.receive(rdp);
					this.ip = rdp.getAddress().getHostAddress();
					this.pot = Integer.parseInt(new String(rdp.getData(), 0, rdp.getLength()));
					ok = true;
				}catch(InterruptedIOException e) {
					tim ++;
					 System.out.println("超时, " + (this.max - tim) + " 次重试..." );
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
				soc.setSoTimeout(this.timOut);	// 设置超时

				// 发送数据
				OutputStream os = soc.getOutputStream();
				PrintWriter pw = new PrintWriter(os);
				pw.write(dat);
				pw.flush();
				soc.shutdownOutput();

				// 接收返回
				InputStream is = soc.getInputStream();
				is.read();
//				System.out.println(is.read());
				r = true;

				// 关闭资源
				is.close();
				pw.close();
				os.close();
				soc.close();
			} catch (IOException e) {
//				e.printStackTrace();
			}
		}
		return r;
	}

	// 数据传输
	public boolean send(String dat) {
		return this.send(dat, null);
	}

	// 测试程序
	public static void main(String[] args) {
		try {
			Client c = new Client();
			byte[] b = new byte[64];
			int n;
			int min = 1;
			String s;

			if (args.length > 0) {
				min = Integer.parseInt (args[0]);
			}

			System.out.println("输入内容按回车发送，直接按回车退出：");
			for (n = System.in.read(b); n > min; n = System.in.read(b)) {
				s = new String(b, 0, (n - min));
				if (c.send(s)) {
					System.out.println("发送成功：" + s);
				} else {
					System.out.println("Err！发送失败：" + s);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
