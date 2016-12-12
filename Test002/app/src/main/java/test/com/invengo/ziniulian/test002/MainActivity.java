package test.com.invengo.ziniulian.test002;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

	private Context context = null;
	private WebView wv = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		wv = (WebView)findViewById(R.id.wv);
		context = getApplicationContext();

		WebSettings settings = wv.getSettings();

		// 支持JS
		settings.setJavaScriptEnabled(true);

		// 设置默认字体
		settings.setDefaultTextEncodingName("UTF-8");

		// 设置滚动条风格
		wv.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

		// 传入安卓对象
		wv.addJavascriptInterface(new AdrObj(context), "myAdrObj");

		// 加载页面
		wv.loadUrl("file:///android_asset/demo02/index.html");
	}

	// 传给JS的安卓对象
	private class AdrObj {
		private Context context = null;

		public AdrObj (Context c) {
			this.context = c;
		}

		// 文件写入
		@JavascriptInterface
		public boolean appendFile (String fileNam, String msg) {
			boolean r = false;

			// 写入文件（内存中）
			OutputStream out=null;
			try {
				SimpleDateFormat df = new SimpleDateFormat("------- yyyy/MM/dd HH:mm:ss -----\n");
				out = context.openFileOutput(fileNam, Context.MODE_APPEND);
				out.write(df.format(new Date()).getBytes());
				out.write(msg.getBytes());
				out.write("\n------- END ---------- \n\n".getBytes());
				r = true;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (out != null) {
					try {
						out.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

			return r;
		}
	}
}
