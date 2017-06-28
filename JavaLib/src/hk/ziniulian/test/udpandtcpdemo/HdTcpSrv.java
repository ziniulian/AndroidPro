package hk.ziniulian.test.udpandtcpdemo;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

/**
 * 先 UDP 广播，再 TCP 连接测试 的 TCP 处理器
 * Created by LZR on 2017/6/27.
 */

public class HdTcpSrv extends Thread {
	private Socket soc = null;
	private char[] buf = new char[1024];

	public HdTcpSrv (Socket s) {
		this.soc = s;
	}

	// 数据处理
	private void hdDat (String dat) {
		// 测试输出
		System.out.print ("TCP 客户端信息：");
		System.out.println (dat);
	}

	@Override
	public void run() {
		InputStream is=null;
		InputStreamReader isr=null;
		OutputStream os=null;

		try {
//			System.out.println("111 : " + this.soc.getSoTimeout());
			// 设置超时
			this.soc.setSoTimeout(1000);

			// 获取输入流
			is = this.soc.getInputStream();
			isr = new InputStreamReader(is);

//			System.out.println("222");
			// 并读取客户端信息
			StringBuilder dat = new StringBuilder();
			int n = isr.read(this.buf);
			while(n != -1){	//循环读取客户端的信息
				dat.append(this.buf, 0, n);
				n = isr.read();
			}
			this.soc.shutdownInput();	//关闭输入流
//			System.out.println("333");

			// 数据处理
			this.hdDat(dat.toString());

			//获取输出流，响应客户端的请求
			os = this.soc.getOutputStream();
			os.write(1);
			os.flush();	//调用flush()方法将缓冲输出
//			System.out.println("444");
		} catch (Exception e) {
//			e.printStackTrace();
		} finally {
			//关闭资源
			try {
//				System.out.println("555");
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
//				System.out.println("666");
			} catch (Exception e) {
//				e.printStackTrace();
			}
		}
	}
}
