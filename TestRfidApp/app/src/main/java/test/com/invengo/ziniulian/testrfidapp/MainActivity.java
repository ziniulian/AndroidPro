package test.com.invengo.ziniulian.testrfidapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.jiangzi.ziniulian.android.rfid.Rfid;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

	// 公用数据
	public static Rfid rfid = new Rfid();

	private Button btnStop = null;
	private ListView lv = null;
	private SimpleAdapter lvadp = null;
	private Intent intent = new Intent();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		this.lv = (ListView)findViewById(R.id.lv);
		intent.setClass(getApplicationContext(), ReadActivity.class);

		//创建简单适配器SimpleAdapter
		this.lvadp = new SimpleAdapter(this, this.rfid.getLvDat(), R.layout.lvitem,
			new String[] {"epcNam","epc", "tim"},
			new int[] {R.id.lvTitle, R.id.lvEpc, R.id.lvTim});

		//加载SimpleAdapter到ListView中
		this.lv.setAdapter(this.lvadp);

		// 绑定事件
		this.lv.setOnItemClickListener(new LvLc());

		// 绑定事件
		this.rfid.setListEventListener(new ScanLc());

		// 绑定事件
		Button btn;
		btn = (Button)findViewById(R.id.btnClear);
		btn.setOnClickListener(new BtnLc());
		btn = (Button)findViewById(R.id.btnScan);
		btn.setOnClickListener(new BtnLc());
		btn = (Button)findViewById(R.id.btnStop);
		btn.setOnClickListener(new BtnLc());
		this.btnStop = btn;

		// 初始化
		this.rfid.init();
		this.rfid.wakeUp();
	}

	@Override
	protected void onResume() {
		super.onResume();
		this.lvadp.notifyDataSetChanged();
	}

/*
	@Override
	protected void onStop() {

		// 暂停读写器
		if (this.reader != null) {
			ATRfidManager.sleep();
			Log.i("----------", "sleep");
		}

		super.onStop();
	}
*/

	@Override
	protected void onDestroy() {
		// 关闭读写器
		this.rfid.stop();
		this.rfid.sleep();
		this.rfid.destroy();
		super.onDestroy();
	}

	// 按钮事件
	private class BtnLc implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.btnScan:
					btnStop.setVisibility(View.VISIBLE);
					rfid.scan();
					break;
				case R.id.btnStop:
					btnStop.setVisibility(View.GONE);
					rfid.stop();
					break;
				case R.id.btnClear:
					rfid.clearDat();
					lvadp.notifyDataSetChanged();
					break;
			}
		}
	}

	// 列表点击事件
	private class LvLc implements AdapterView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			rfid.setTagEvt(true);
			intent.putExtra("tag", (HashMap<String, Object>)lv.getItemAtPosition(position));
			startActivity(intent);
		}
	}

	// 扫码事件
	private class ScanLc implements Rfid.OnListChgListener {
		@Override
		public void onTagChg(HashMap<String, Object> tag) {
			lvadp.notifyDataSetChanged();
		}
	}

}
