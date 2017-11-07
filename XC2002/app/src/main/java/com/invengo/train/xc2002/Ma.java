package com.invengo.train.xc2002;

import android.content.IntentFilter;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.invengo.train.xc2002.entity.FlLight;
import com.invengo.train.xc2002.entity.Tim;
import com.invengo.train.xc2002.entity.Web;
import com.invengo.train.xc2002.enums.EmUh;
import com.invengo.train.xc2002.enums.EmUrl;

import java.text.SimpleDateFormat;

import static com.invengo.train.xc2002.enums.UtStrMegEm.meg;

public class Ma extends AppCompatActivity {
	public static final String sdDir = "Invengo/Train/XC2002/";	// SD卡路径

	private Web w = new Web();	// 读写器
	private WebView wv;
	private Handler uh = new UiHandler();

	// 时间
	private Tim tim;	// 时间对象
	private TextView timv;	// 显示界面
	private SimpleDateFormat timFmtSql = new SimpleDateFormat("yyyyMMddHHmmss");	// 数据库时间格式
	private SimpleDateFormat timFmtView = new SimpleDateFormat("yyyy-M-d HH:mm");	// 界面时间格式

	// 手电筒
	public FlLight fl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ma);

		// 时间
		timv = (TextView) findViewById(R.id.tim);
		tim = new Tim(this);
		IntentFilter f = new IntentFilter();
		f.addAction("com.invengo.train.xc2002.timrcv");
		registerReceiver(tim.getRcv(), f);
		tim.start();

		// 手电筒
		fl = new FlLight(this);

		// 浏览器
		wv = (WebView)findViewById(R.id.wv);
		WebSettings ws = wv.getSettings();
		ws.setDefaultTextEncodingName("UTF-8");
		ws.setJavaScriptEnabled(true);
		wv.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
		wv.addJavascriptInterface(w, "rfdo");
		sendUrl(EmUrl.Home);

		// 读写器
		w.init(this);

	}

	public String getTim () {
		return timFmtSql.format(tim.getT().getTime());
	}

	@Override
	protected void onResume() {
		w.open();
		super.onResume();
	}

	@Override
	protected void onPause() {
		w.close();
		fl.flashlight(true);
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		tim.stop();
		unregisterReceiver(tim.getRcv());
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
//Log.i("---------------", "" + keyCode);
		switch (keyCode) {
			case 84:
				if (event.getRepeatCount() == 0) {
					fl.flashlight(false);
				}
				return true;
			case KeyEvent.KEYCODE_SOFT_RIGHT:
				if (event.getRepeatCount() == 0) {
					EmUrl e = getCurUi();
					switch (getCurUi()) {
						case Read:
							w.read();
							break;
						case Info:
							sendUrl(EmUrl.ReRead);
							break;
					}
				}
				return true;
			case KeyEvent.KEYCODE_BACK:
				EmUrl e = getCurUi();
				switch (e) {
					case Home:
					case Read:
					case Info:
					case Check:
					case Chg:
						sendUrl(EmUrl.Back);
						break;
					case Exit:
					case Err:
						return super.onKeyDown(keyCode, event);
				}
				return true;
			default:
				return super.onKeyDown(keyCode, event);
		}
	}

	// 获取当前页面信息
	@Nullable
	private EmUrl getCurUi () {
		try {
			return EmUrl.valueOf(wv.getTitle());
		} catch (Exception e) {
			return null;
		}
	}

	// 页面跳转
	public void sendUrl (EmUrl e, String... args) {
		uh.sendMessage(uh.obtainMessage(EmUh.Url.ordinal(), meg(e.toString(), args)));
	}

	// 发送页面处理消息
	public void sendUh (EmUh e) {
		uh.sendMessage(uh.obtainMessage(e.ordinal()));
	}

	// 页面处理器
	private class UiHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			EmUh e = EmUh.values()[msg.what];
			switch (e) {
				case Tim:
//					Calendar c = tim.getT();
//					timv.setText(c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DATE) + " " + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE));
					timv.setText(timFmtView.format(tim.getT().getTime()));
					break;
				case Url:
					wv.loadUrl((String)msg.obj);
					break;
			}
		}
	}

}
