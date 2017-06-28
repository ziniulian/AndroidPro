package hk.ziniulian.test.udpdemo;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * 简单的UDP测试，客户端
 * Created by LZR on 2017/6/26.
 */

public class UdpClient {
	private int timOut = 1000;	// 接收数据的超时时间（毫秒）
	private int max = 5;	// 重发数据的最多次数
	private int size = 1024;	// 缓存大小
	private DatagramSocket ds = new DatagramSocket();	// UDP 套接字

	public UdpClient () throws SocketException {
		this.ds.setSoTimeout(this.timOut);	//设置接收数据时阻塞的最长时间
	}

	// 发送数据
	public void send(String ip, int pot, String dat) throws IOException {
		if (ip == null) {
			// 自动获取广播地址
			ip = this.getBroadcast ();
			if (ip == null) {
				throw new IOException("无法自动获取广播地址！");
			}
		}

		int tim = 0;	// 重发数据的次数
		boolean ok = false;	// 是否接收到数据
		byte[] buf = new byte[this.size];	// 缓存区
		InetAddress adr = InetAddress.getByName(ip);	// 服务端地址
		DatagramPacket dp = new DatagramPacket(dat.getBytes(), dat.length(), adr, pot);	// 要发送的数据
		DatagramPacket rdp = new DatagramPacket(buf, this.size);	// 需要接收的数据

		while (!ok && (tim < this.max)) {
			// 发送数据
			this.ds.send(dp);
			try {
				// 接收服务端传回的数据
				ds.receive(rdp);

//				// 如果接收到的数据不是来自目标地址，则抛出异常 （只做局域网单播时可做此校验）
//				if(!rdp.getAddress().equals(adr)){
//					throw new IOException("不是目标服务传回的数据！");
//				}

				//如果接收到数据。则退出循环
				ok = true;
			}catch(InterruptedIOException e){
				//如果接收数据时阻塞超时，重发并减少一次重发的次数
				tim ++;
				System.out.println("超时, " + (this.max - tim) + " 次重试..." );
			}
		}

		if(ok){
			hdDat(new String(rdp.getData(), 0, rdp.getLength()), rdp);
		}else{
			//如果重发MAXNUM次数据后，仍未获得服务器发送回来的数据，则打印如下信息
			System.out.println("无响应！");
		}

		// 关闭套接字
		ds.close();
	}

	// 处理数据
	private void hdDat (String dat, DatagramPacket rdp) {
		// 测试输出
		System.out.println("服务端信息：" + rdp.getAddress().getHostAddress() + ":" + rdp.getPort());
		System.out.println(dat);
	}

	// 获取广播地址
	public String getBroadcast () throws SocketException {
		for (Enumeration<NetworkInterface> niEnum = NetworkInterface.getNetworkInterfaces(); niEnum.hasMoreElements(); ) {
			NetworkInterface ni = niEnum.nextElement();
			if (!ni.isLoopback() && ni.isUp()) {
				for (InterfaceAddress ia : ni.getInterfaceAddresses()) {
					InetAddress b = ia.getBroadcast();
					if (b != null) {
						// System.out.println(b.getHostAddress());
						return b.getHostAddress();
					}
				}
			}
		}
		return null;
	}

	// 测试程序
	public static void main(String[] args) {
		try {
			UdpClient uc = new UdpClient();
			switch (args.length) {
				case 1:
					uc.send(null, 3000, args[0]);
					break;
				case 2:
					uc.send(args[1], 3000, args[0]);
					break;
				default:
					uc.send("localhost", 3000, "Hello World!");
					break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
