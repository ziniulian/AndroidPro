package hk.ziniulian.test.tcpdemo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by LZR on 2017/6/26.
 */

public class TcpClient {
	public static void main(String[] args) {
		try {
			Socket socket;
			String dat;
			//1.创建客户端Socket，指定服务器地址和端口
			switch (args.length) {
				case 2:
					socket = new Socket("localhost", Integer.parseInt(args[0]));
					dat = args[1];
					break;
				case 3:
					socket = new Socket(args[0], Integer.parseInt(args[1]));
					dat = args[2];
					break;
				default:
					return;
			}

			//2.获取输出流，向服务器端发送信息
			OutputStream os = socket.getOutputStream();	//字节输出流
			PrintWriter pw = new PrintWriter(os);	//将输出流包装为打印流
			pw.write(dat);
			pw.flush();
			socket.shutdownOutput();	//关闭输出流

			//3.获取输入流，并读取服务器端的响应信息
			InputStream is = socket.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String info;
			while((info = br.readLine())!=null){
				System.out.println("我是客户端，服务器说："+info);
			}

			//4.关闭资源
			br.close();
			is.close();
			pw.close();
			os.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
