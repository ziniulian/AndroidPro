package com.invengo.airhub;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.invengo.rfid.Base;
import com.invengo.rfid.EmCb;
import com.invengo.rfid.InfTagListener;
import com.invengo.rfid.xc2910.Rd;

public class MainActivity extends AppCompatActivity {
	private Base bf = new Rd();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD, WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		setContentView(R.layout.activity_main);

		bf.setTagListenter(new InfTagListener() {
			@Override
			public void onReadTag(com.invengo.rfid.tag.Base bt, InfTagListener itl) {
				Log.i("-r-", bt.toJson());
			}

			@Override
			public void onWrtTag(com.invengo.rfid.tag.Base bt, InfTagListener itl) {
				Log.i("-w-", bt.toJson());
			}

			@Override
			public void cb(EmCb e, String[] args) {
				Log.i("-cb-", e.name());
			}
		});
		bf.init();

		Button btn = (Button)findViewById(R.id.btnScan);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				bf.scan();
			}
		});

		btn = (Button)findViewById(R.id.btnStop);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				bf.stop();
			}
		});

		btn = (Button)findViewById(R.id.btnRead);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				bf.read("all");
			}
		});

		btn = (Button)findViewById(R.id.btnWrt);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				String tid = "E2006003204AFEC5";
				String tid = "E20034120137F9000097E384";
//				bf.wrt("use", "01020304050607080910111213141516171819202122232425262728293031323334353637383940414243444546474849505152535455565758596061626364", tid);
				bf.wrt("use", "", tid);
//				bf.wrt("epc", "9910", tid);
			}
		});

	}

	@Override
	protected void onResume() {
		bf.open();
		super.onResume();
	}

	@Override
	protected void onPause() {
		bf.close();
		super.onPause();
	}
}
