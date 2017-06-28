package com.invengo.ziniulian.test.xc2910srv.net;

import android.os.Bundle;
import android.os.Message;

import com.invengo.ziniulian.test.xc2910srv.FlushUiHandle;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by LZR on 2017/6/28.
 */

public class HdTcpSrv extends Thread {
	private Socket soc = null;
	private char[] buf = new char[1024];
	private FlushUiHandle fh;

	public HdTcpSrv (Socket s, FlushUiHandle f) {
		this.soc = s;
		this.fh = f;
	}

	// 数据处理
	private void hdDat (String dat) {
		Message msg = new Message();
		Bundle bd = new Bundle();
		bd.putString("stat", "prt");
		bd.putString("cont", dat);
		msg.setData(bd);
		fh.sendMessage(msg);
	}

	@Override
	public void run() {
		InputStream is=null;
		InputStreamReader isr=null;
		OutputStream os=null;

		try {
			// 设置超时
			this.soc.setSoTimeout(1000);

			// 获取输入流
			is = this.soc.getInputStream();
			isr = new InputStreamReader(is);

			// 并读取客户端信息
			StringBuilder dat = new StringBuilder();
			int n = isr.read(this.buf);
			while(n != -1){
				dat.append(this.buf, 0, n);
				n = isr.read();
			}
			this.soc.shutdownInput();

			// 数据处理
			this.hdDat(dat.toString());

			//获取输出流，响应客户端的请求
			os = this.soc.getOutputStream();
			os.write(1);
			os.flush();
		} catch (Exception e) {
//			e.printStackTrace();
		} finally {
			//关闭资源
			try {
				if(os != null) {
					os.close();
				}
				if(isr != null) {
					isr.close();
				}
				if(is != null) {
					is.close();
				}
				if(this.soc != null) {
					this.soc.close();
				}
			} catch (Exception e) {
//				e.printStackTrace();
			}
		}
	}
}
