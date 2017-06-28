package hk.ziniulian.test.tcpdemo;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by LZR on 2017/6/26.
 */

public class TcpSrv {
	public static void main(String[] args) {
		try {
			//1.创建一个服务器端Socket，即ServerSocket，指定绑定的端口，并监听此端口
			int pot = 0;	// 0 为系统自动分配的端口
			if (args.length > 0) {
				pot = Integer.parseInt(args[0]);
			}
			ServerSocket serverSocket = new ServerSocket(pot);
			Socket socket;

			//记录客户端的数量
			int count=0;
			System.out.println("***服务器即将启动，等待客户端的连接*** 端口号：" + serverSocket.getLocalPort());

			//循环监听等待客户端的连接
			while(true){
				//调用accept()方法开始监听，等待客户端的连接
				socket = serverSocket.accept();

				//创建一个新的线程
				TcpThread serverThread = new TcpThread(socket);

				//启动线程
				serverThread.start();

				count++;//统计客户端的数量
				System.out.println("客户端的数量：" + count);
				InetAddress address = socket.getInetAddress();
				System.out.println("当前客户端的IP：" + address.getHostAddress());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
