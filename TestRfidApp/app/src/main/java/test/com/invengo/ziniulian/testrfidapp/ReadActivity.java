package test.com.invengo.ziniulian.testrfidapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.atid.lib.dev.rfid.type.BankType;
import com.jiangzi.ziniulian.android.rfid.Rfid;

import java.util.HashMap;
import java.util.Objects;

public class ReadActivity extends AppCompatActivity {

	private Rfid rfid = null;
	private AlertDialog stopLog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_read);

		// 对话框
		AlertDialog.Builder bld = new AlertDialog.Builder(this);
		bld.setMessage("确认停止吗？");
		bld.setTitle("提示");
		bld.setNegativeButton("确定", new StopBtnLc());
		bld.setCancelable(false);
		this.stopLog = bld.create();

		// 参数获取
		this.rfid = MainActivity.rfid;
		Intent intent = getIntent();
		HashMap<String, Object> tag = (HashMap<String, Object>) intent.getSerializableExtra("tag");
		flush(tag);

		// 绑定事件
		Button btn;
		btn = (Button)findViewById(R.id.epcRead);
		btn.setOnClickListener(new BtnLc());
		btn = (Button)findViewById(R.id.tidRead);
		btn.setOnClickListener(new BtnLc());
		btn = (Button)findViewById(R.id.usrRead);
		btn.setOnClickListener(new BtnLc());
		btn = (Button)findViewById(R.id.bckRead);
		btn.setOnClickListener(new BtnLc());

		this.rfid.setTagEventListener(new ReadLc());
	}

	@Override
	protected void onDestroy() {
		// 关闭读写器
		this.rfid.setTagEvt(false);
		super.onDestroy();
	}

	// 设置按钮可用性
	private void setBtnEnable (boolean enable) {
		if (enable) {
			// 开启所有按钮功能
			this.stopLog.dismiss();
		} else {
			// 关闭所有按钮功能
			this.stopLog.show();
		}
	}

	// 刷新界面
	private void flush (HashMap<String, Object> tag) {
		TextView v = null;
		Object s = tag.get("epc");
			v = (TextView)findViewById(R.id.epcNum);
			if (s == null) s = "";
			v.setText(s.toString());
			s = tag.get("epcNam");
			v = (TextView)findViewById(R.id.epcNam);
			if (s == null) s = "";
			v.setText(s.toString());

		s = tag.get("tid");
			v = (TextView)findViewById(R.id.tidNum);
			if (s == null) s = "";
			v.setText(s.toString());
//			s = tag.get("tidNam");
//			v = (TextView)findViewById(R.id.tidNam);
//			if (s == null) s = "";
//			v.setText(s.toString());

		s = tag.get("usr");
			v = (TextView)findViewById(R.id.usrNum);
			if (s == null) s = "";
			v.setText(s.toString());
			s = tag.get("usrNam");
			v = (TextView)findViewById(R.id.usrNam);
			if (s == null) s = "";
			v.setText(s.toString());

		s = tag.get("bck");
			v = (TextView)findViewById(R.id.bckNum);
			if (s == null) s = "";
			v.setText(s.toString());
			s = tag.get("bckNam");
			v = (TextView)findViewById(R.id.bckNam);
			if (s == null) s = "";
			v.setText(s.toString());
	}

	// 按钮事件
	private class BtnLc implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.epcRead:
					setBtnEnable(false);
					rfid.read(BankType.EPC, 2, 6);
					break;
				case R.id.tidRead:
					setBtnEnable(false);
					rfid.read(BankType.TID, 0, 12);
					break;
				case R.id.usrRead:
					setBtnEnable(false);
					rfid.read(BankType.User, 0, 32);
					break;
				case R.id.bckRead:
					setBtnEnable(false);
					rfid.read(BankType.Reserved, 0, 4);
					break;
			}
		}
	}

	// 停止对话框事件
	private class StopBtnLc implements DialogInterface.OnClickListener {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			rfid.stop();
			dialog.dismiss();
		}
	}

	// 读取成功的事件回调
	private class ReadLc implements Rfid.OnTagChgListener {
		@Override
		public void onTagChg(HashMap<String, Object> tag) {
			rfid.stop();
			setBtnEnable(true);
			flush(tag);
		}
	}

}
