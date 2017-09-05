package com.invengo.bedmg.action;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.invengo.bedmg.entity.Web;
import com.invengo.bedmg.enums.EmUh;
import com.invengo.bedmg.enums.EmUrl;

public class MainActivity extends AppCompatActivity {
	private Web w = new Web();	// 读写器
	private WebView wv;
	private Handler uh = new UiHandler();
	private boolean reading = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD, WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD); // 全屏、不锁屏
		setContentView(R.layout.activity_main);

		// 读写器设置
		w.init(this);

		// 页面设置
		wv = (WebView)findViewById(R.id.wv);
		WebSettings ws = wv.getSettings();
		ws.setDefaultTextEncodingName("UTF-8");
		ws.setJavaScriptEnabled(true);
		wv.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
		wv.addJavascriptInterface(w, "rfdo");

		sendUrl(EmUrl.ScanDemo);
	}

	@Override
	protected void onResume() {
		w.open();
		super.onResume();
	}

	@Override
	protected void onPause() {
		w.close();
		super.onPause();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_SOFT_RIGHT:
				if (event.getRepeatCount() == 0) {
					EmUrl e = getCurUi();
					if (e != null) {
						switch (getCurUi()) {
							case ScanDemo:
								reading = true;
								w.scan();
								break;
						}
					}
				}
				return true;
			case KeyEvent.KEYCODE_BACK:
				EmUrl e = getCurUi();
				if (e != null) {
					switch (e) {
						case ScanDemo:
						case Err:
							return super.onKeyDown(keyCode, event);
					}
				} else {
					wv.goBack();
				}
				return true;
			default:
				return super.onKeyDown(keyCode, event);
		}
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_SOFT_RIGHT:
				if (reading) {
					w.stop();
					reading = false;
				}
				return true;
			default:
				return super.onKeyUp(keyCode, event);
		}
	}

	// 获取当前页面信息
	private EmUrl getCurUi () {
		try {
			return EmUrl.valueOf(wv.getTitle());
		} catch (Exception e) {
			return null;
		}
	}

	// 页面跳转
	public void sendUrl (EmUrl e) {
		uh.sendMessage(uh.obtainMessage(EmUh.Url.ordinal(), e.ordinal(), 0));
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
				case Url:
					wv.loadUrl(EmUrl.values()[msg.arg1].url());
					break;
				case Connected:
					if (getCurUi() == EmUrl.Err) {
						wv.goBack();
					}
				default:
					break;
			}
		}
	}
}
