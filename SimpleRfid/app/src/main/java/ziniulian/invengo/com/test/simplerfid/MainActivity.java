package ziniulian.invengo.com.test.simplerfid;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
	private static final String TAG = "--------";
	private DemoXC2002FHC demo = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// 按钮事件
		Button b = (Button)findViewById(R.id.btn);
		b.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				demo.qryTag();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i(TAG, "唤醒");

		if (demo == null) {
			demo = new DemoXC2002FHC();

			demo.setOnTagListener(new OnTagListener() {
				@Override
				public void onTag(byte[] r) {
					if (r != null) {
						Log.i(TAG, "有标签 --- s");
						for (byte i : r) {
							Log.i(TAG, Integer.toHexString(i));
						}
						Log.i(TAG, "有标签 --- e");
					} else {
						Log.i(TAG, "没标签");
					}
				}
			});
		}

		demo.open();

//		// 检查机器中的所有串口
//		SerialPortFinder spf = new SerialPortFinder();
//		String[] sa = spf.getAllDevices();
//		for (String s : sa) {
//			Log.i(TAG, s);
//		}

//		// CRC 校验测试：
//		Log.i(TAG, "CRC start");
//		Log.i(TAG, Integer.toHexString( Crc.check(new Byte[] {0xFFFFFFaa, 0x55, 0x06, 0x30}, 0, 4) ));
//		Log.i(TAG, "CRC end");
	}

	@Override
	protected void onStop() {
		Log.i(TAG, "休眠");
		demo.close();
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		Log.i(TAG, "关闭");
		demo.close();
		super.onDestroy();
	}
}
