package com.invengo.xc2910;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.invengo.train.tag.BaseTag;
import com.invengo.train.tag.xc2910.InfViewTag;
import com.invengo.xc2910.rfid.DatSender;
import com.invengo.xc2910.rfid.OnDatSendListener;
import com.invengo.xc2910.rfid.OnTagListener;
import com.invengo.xc2910.rfid.XC2910;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
	private static final String xmlPath = "DataDic.xml";	// XML 文件路径

	protected FrMain frMain = new FrMain();
	protected FrScan frScan = new FrScan();
	protected FrShow frShow = new FrShow();
	protected XC2910 demo = null;
	protected String path = "Log.txt";
	protected DatSender ds = new DatSender();
	private FlushUiHandle fh = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		fh = new FlushUiHandle();
		getFragmentManager().beginTransaction().replace(R.id.frdiv, frMain).commit();
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (demo == null) {
			// 加载 车属性集合的 XML 文档
			try {
				BaseTag.load(this.getAssets().open(xmlPath));
			} catch (IOException e) {
//				e.printStackTrace();
			}

			// 创建读出器对象
			demo = new XC2910();
			demo.setOnTagListener(new OnTagListener() {
				@Override
				public void onTag(InfViewTag t) {
					if (frScan.isAdded()) {
						frScan.setTag(t);
						Message msg = new Message();
						Bundle dat = new Bundle();
						dat.putString("stat", "OnTag");
						msg.setData(dat);
						fh.sendMessage(msg);
					}
				}
			});

			// 数据上传回调处理
			ds.setOnDatSendListener(new OnDatSendListener() {
				@Override
				public void onDatSended(boolean b) {
					Message msg = new Message();
					Bundle dat = new Bundle();
					dat.putString("stat", "msg");
					if (b) {
//						deleteFile(path);
						dat.putString("msg", "OK! 发送成功！");
					} else {
						dat.putString("msg", "Err! 发送失败！");
					}
					msg.setData(dat);
					fh.sendMessage(msg);
				}
			});
		}

		demo.open();
	}

	@Override
	protected void onStop() {
		demo.close();
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		demo.close();
		super.onDestroy();
	}

	private class FlushUiHandle extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Bundle dat = msg.getData();
			Toast toast;
			switch (dat.getString("stat")) {
				case "OnTag":
					frScan.updateUi(true);
					break;
				case "msg":
					toast = Toast.makeText(getApplicationContext(), dat.getString("msg"), Toast.LENGTH_SHORT);
					toast.show();
					break;
			}
		}
	}
}
