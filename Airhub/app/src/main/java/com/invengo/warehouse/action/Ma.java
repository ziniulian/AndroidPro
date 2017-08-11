package com.invengo.warehouse.action;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.invengo.airhub.R;
import com.invengo.rfid.Base;
import com.invengo.rfid.EmCb;
import com.invengo.rfid.InfTagListener;
import com.invengo.rfid.xc2910.Rd;

/**
 * Created by LZR on 2017/8/11.
 */

public class Ma extends AppCompatActivity {
	private Base bf = new Rd();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD, WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		setContentView(R.layout.activity_main);

		init();

		Button b = (Button)findViewById(R.id.btnScan);
		b.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				bf.scan();
			}
		});

		b = (Button)findViewById(R.id.btnStop);
		b.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				bf.stop();
			}
		});

		b = (Button)findViewById(R.id.btnRead);
		b.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				bf.read("epc");
			}
		});

		b = (Button)findViewById(R.id.btnWrt);
		b.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				bf.wrt("epc", "呵呵", "E2006003204AFEC5");
			}
		});
	}

	// 初始化
	private void init() {
		bf.setTagListenter(new InfTagListener() {
			@Override
			public void onReadTag(com.invengo.rfid.tag.Base bt, InfTagListener itl) {
				Log.i("-r-", bt.toJson(true));
				Log.i("-r-", bt.toJson(false));
			}

			@Override
			public void onWrtTag(com.invengo.rfid.tag.Base bt, InfTagListener itl) {
				Log.i("-w-", "OK!");
			}

			@Override
			public void cb(EmCb e, String[] args) {
				Log.i("-c-", e.name());
			}
		});
		bf.init();
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
