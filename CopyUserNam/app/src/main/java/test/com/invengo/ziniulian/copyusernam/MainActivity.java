package test.com.invengo.ziniulian.copyusernam;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
	private EditText inV;	// 输入框
	private TextView outV;	// 输出框
	private ClipboardManager cm;	// 剪贴板
	private InputMethodManager imm;	// 软键盘
	private Context ct;

	// 难点：输入框的焦点处理问题。

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// 初始化
		ct = getApplicationContext();
		inV = (EditText)findViewById(R.id.namTxt);
		outV = (TextView)findViewById(R.id.resultTxt);
		cm = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
		imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
		outV.setMovementMethod(ScrollingMovementMethod.getInstance());

		// 添加按钮事件
		BtnClk bc = new BtnClk();
		findViewById(R.id.parseBtn).setOnClickListener(bc);
		findViewById(R.id.copyBtn).setOnClickListener(bc);

		// 输入框事件
		inV.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					outV.setText("");
					outV.scrollTo(0, 0);
				}
			}
		});

		inV.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				String instr = inV.getText().toString();
				if (!instr.equals("")) {
					outV.setText(parseNam(instr));
				}

				// 清除焦点
				inV.clearFocus();
				imm.hideSoftInputFromWindow(inV.getWindowToken(), 0);
				return false;
			}
		});
	}

	// 处理按钮事件
	private class BtnClk implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			String instr = inV.getText().toString();
			if (!instr.equals("")) {
				switch (v.getId()) {
					case R.id.parseBtn:
						outV.setText(parseNam(instr));
						break;
					case R.id.copyBtn:
						String txt = hdCopyTxt(instr, outV.getText().toString());
						cm.setPrimaryClip(ClipData.newPlainText(null, txt));
						Toast.makeText(ct, "复制成功！", Toast.LENGTH_LONG).show();
						break;
				}
			}
		}
	}




	// ------------------- 接口 -----------------

	// 解析用户名
	private String parseNam (String nam) {
		String r = nam + "__ is parsed!";
		return r;
	}

	// 处理要复制的内容
	private String hdCopyTxt (String nam, String parsedContent) {
		String r = "{\n\tnam: " + nam + ",\n\tresult: " + parsedContent + "\n}\n";
		return r;
	}
}
