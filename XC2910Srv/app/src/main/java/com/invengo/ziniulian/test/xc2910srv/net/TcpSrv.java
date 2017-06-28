package com.invengo.ziniulian.test.xc2910srv.net;

import com.invengo.ziniulian.test.xc2910srv.FlushUiHandle;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by LZR on 2017/6/28.
 */

public class TcpSrv implements Runnable {
	private boolean isgo = false;
	private ServerSocket srv = null;
	private FlushUiHandle fh;

	public void setFh (FlushUiHandle f) {
		this.fh = f;
	}

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
//Log.i("已开启 TCP 服务 ...", "" + this.srv.getLocalPort());
			while (this.isgo) {
				Socket soc = this.srv.accept();
//Log.i("TCP 当前客户端的IP", this.srv.getInetAddress().getHostAddress());
				HdTcpSrv hdSrv = new HdTcpSrv(soc, fh);
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
//Log.i("TCP 服务已关闭！", "" + this.srv.getLocalPort());
				this.srv = null;
			}
			this.isgo = false;
		}
	}
}
