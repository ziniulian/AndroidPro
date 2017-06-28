package com.invengo.ziniulian.test.xc2910srv;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.invengo.ziniulian.test.xc2910srv.net.TcpSrv;
import com.invengo.ziniulian.test.xc2910srv.net.UdpSrv;

// XC2910 上传数据测试用服务
public class MainActivity extends AppCompatActivity {
	private boolean isgo = false;
	private TextView txt;
	private Button btn;
	private TcpSrv ts = new TcpSrv();
	private UdpSrv us = new UdpSrv();
	private FlushUiHandle fh = new FlushUiHandle();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		txt = (TextView) findViewById(R.id.txt);
		btn = (Button) findViewById(R.id.btn);
		fh.setMa (this);
		ts.setFh (fh);

		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isgo) {
					us.stop();
					ts.stop();
					txt.setText("");
					btn.setText("开启服务");
					isgo = false;
				} else {
					try {
						ts.start();
						us.setTcpPot(ts.getPort());
						us.start();
						print("TCP 端口：" + ts.getPort());
						print("UDP 端口：" + us.getPort());
						btn.setText("关闭服务");
						isgo = true;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	public void print (String s) {
		txt.append(s + "\n");
	}
}
