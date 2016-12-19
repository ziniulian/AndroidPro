package test.com.invengo.ziniulian.testrfid;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.util.Log;

import com.atid.lib.dev.ATRfidManager;
import com.atid.lib.dev.ATRfidReader;
import com.atid.lib.dev.event.RfidReaderEventListener;
import com.atid.lib.dev.rfid.type.ActionState;
import com.atid.lib.dev.rfid.type.BankType;
import com.atid.lib.dev.rfid.type.ConnectionState;
import com.atid.lib.dev.rfid.type.ResultCode;
import com.jiangzi.ziniulian.android.rfid.InfRfidJsObj;

import com.atid.lib.dev.rfid.param.LockParam;
import com.atid.lib.dev.rfid.type.LockType;

public class MainActivity extends AppCompatActivity {

	private Context context = null;
	private WebView wv = null;

	private ATRfidReader reader = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		wv = (WebView) findViewById(R.id.wv);
		context = getApplicationContext();

		// 浏览器设置
		WebSettings settings = wv.getSettings();

		// 设置
		settings.setDefaultTextEncodingName("UTF-8");

		// 设置滚动条风格，隐藏滚动条
		wv.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);

		// 创建读写器
		this.reader = ATRfidManager.getInstance();

		// 测试读写器是否创建成功
		if (this.reader == null) {
			// 加载错误页面
			wv.loadUrl("file:///android_asset/demo01/err.html");
		} else {
			// 添加事件
			this.reader.setEventListener(new RfidLc());

			// 浏览器支持JS
			settings.setJavaScriptEnabled(true);

			// 传入安卓对象
			wv.addJavascriptInterface(new RfidJsObj(), "rfidObj");

			// 加载页面
			wv.loadUrl("file:///android_asset/demo01/index.html");
		}

	}

	@Override
	protected void onResume() {
		super.onResume();

		// 唤醒读写器
		if (this.reader != null) {
			ATRfidManager.wakeUp();
			Log.i("----------", "wakeup");
		}
	}

	@Override
	protected void onStop() {

		// 暂停读写器
		if (this.reader != null) {
			ATRfidManager.sleep();
			Log.i("----------", "sleep");
		}

		super.onStop();
	}

	@Override
	protected void onDestroy() {

		// 关闭读写器
		if (this.reader != null) {
			ATRfidManager.onDestroy();
			Log.i("----------", "destroy");
		}

		super.onDestroy();
	}

	// 事件
	private class RfidLc implements RfidReaderEventListener {

		@Override
		public void onReaderStateChanged(ATRfidReader atRfidReader, ConnectionState connectionState) {
			Log.i("----------", "state changed");
		}

		@Override
		public void onReaderActionChanged(ATRfidReader atRfidReader, ActionState actionState) {
			Log.i("----------", "action changed");
		}

		@Override
		public void onReaderReadTag(ATRfidReader atRfidReader, String s, float v, float v1) {
			Log.i("----------", "read tag ：" + s + " , " + Float.toString(v) + " , " + Float.toString(v1));
		}

		@Override
		public void onReaderResult(ATRfidReader atRfidReader, ResultCode resultCode, ActionState actionState, String s, String s1, float v, float v1) {
			Log.i("----------", "result ：" + s + " , " + s1 + " , " + Float.toString(v) + " , " + Float.toString(v1));
//			Log.i("---------- result: ", s1);
			wv.loadUrl("javascript: hwo.testAlert('" + s1 + "')");
		}
	}

	// JS对象
	private class RfidJsObj implements InfRfidJsObj {
		@Override
		@JavascriptInterface
		public void readTag() {
			reader.readMemory6c(BankType.EPC, 2, 6);	// 3000E2001021411101280440E384;
//			reader.readMemory6c(BankType.TID, 0, 12);	// E20034120137F9000097E3842A1D013670055FFBFFFFDC50
//			reader.readMemory6c(BankType.User, 0, 32);
//			reader.readMemory6c(BankType.Reserved, 0, 4);
		}
//
		@Override
		@JavascriptInterface
		public void writeTag(String msg) {
			reader.writeMemory6c(BankType.User, 0, "0");
//			reader.writeMemory6c(BankType.EPC, 2, "1234567890ABCDEF00000000");
//			reader.writeMemory6c(BankType.Reserved, 0, "1234567890ABCDEF");
//			reader.writeMemory6c(BankType.Reserved, 0, "0000000000000000");
		}

		@Override
		@JavascriptInterface
		public void stop() {
			reader.stop();
		}
	}

}
