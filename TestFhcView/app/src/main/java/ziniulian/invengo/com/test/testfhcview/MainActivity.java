package ziniulian.invengo.com.test.testfhcview;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.io.IOException;
import java.util.Map;

import ziniulian.invengo.com.test.simplerfid.DemoXC2002FHC;
import ziniulian.invengo.com.test.simplerfid.DicPro;
import ziniulian.invengo.com.test.simplerfid.OnTagListener;

public class MainActivity extends AppCompatActivity {
	private static final String xmlPath = "DataDic.xml";	// XML 文件路径

	protected FrMain frMain = new FrMain();
	protected FrScan frScan = new FrScan();
	protected FrShow frShow = new FrShow();
	protected DemoXC2002FHC demo = null;
	protected String path = "Log.txt";
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
				DicPro.load(this.getAssets().open(xmlPath));
			} catch (IOException e) {
//				e.printStackTrace();
			}

			// 创建读出器对象
			demo = new DemoXC2002FHC();
			demo.setOnTagListener(new OnTagListener() {
				@Override
				public void onTag(Map<String, String> r) {
					if (frScan.isAdded()) {
						Message msg = new Message();
						Bundle dat = new Bundle();
						if (r != null) {
							dat.putString("pro", r.get("property") + " [" + r.get("propertyAlias") + "]");
							dat.putString("typ", r.get("trainType") + " " + r.get("trainModel"));
							dat.putString("num", r.get("trainNumber"));
							dat.putString("fac", r.get("trainFactory") + " [" + r.get("trainFactoryAlias") + "]");
							dat.putString("mdt", r.get("trainDate") + " [" + r.get("trainDateAlias") + "]");
						}
						msg.setData(dat);
						fh.sendMessage(msg);
					}
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
			if (dat.isEmpty()) {
				frScan.clearUi();
				Toast toast = Toast.makeText(getApplicationContext(), "未搜索到标签，请重试 ...", Toast.LENGTH_SHORT);
				toast.show();
			} else {
				frScan.updateUi(
					dat.getString("pro"),
					dat.getString("typ"),
					dat.getString("num"),
					dat.getString("fac"),
					dat.getString("mdt")
				);
			}
		}
	}
}
