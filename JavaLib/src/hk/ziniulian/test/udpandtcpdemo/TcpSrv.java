package hk.ziniulian.test.udpandtcpdemo;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 先 UDP 广播，再 TCP 连接测试 的 TCP 服务端
 * Created by LZR on 2017/6/27.
 */

public class TcpSrv implements Runnable {
	private boolean isgo = false;
	private ServerSocket srv = null;	// TCP 服务

	// 获取服务的监听端口
	public int getPort () {
		if (this.srv != null) {
			return this.srv.getLocalPort();
		}
		return 0;
	}

	// 开启现场
	public Thread start () throws IOException {
		if (!this.isgo) {
			this.srv = new ServerSocket(0);	// 由系统自动分配服务端口
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
			try {
				this.srv.close();
			} catch (Exception e) {
//				e.printStackTrace();
			}
		}
	}

	@Override
	public void run() {
		try {
//			System.out.println(this.srv.getLocalPort() + " 端口，已开启 TCP 服务 ...");
			while (this.isgo) {
				Socket soc = this.srv.accept();
				System.out.println("TCP 当前客户端的IP：" + this.srv.getInetAddress().getHostAddress());
				HdTcpSrv hdSrv = new HdTcpSrv(soc);
				hdSrv.start();
			}
		} catch (Exception e) {
//			e.printStackTrace();
		} finally {
			if (this.srv != null) {
				// 关闭资源
				try {
					this.srv.close();
				} catch (Exception e) {
//					e.printStackTrace();
				}
//				System.out.println(this.srv.getLocalPort() + " 端口，TCP 服务已关闭！");
				this.srv = null;
			}
			this.isgo = false;
		}
	}

	public static void main (String[] args) {
		try {
			TcpSrv ts = new TcpSrv();
			ts.start();
			Thread.sleep(2000);
			ts.stop();
			Thread.sleep(1000);
			ts.start();
			Thread.sleep(2000);
			ts.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
