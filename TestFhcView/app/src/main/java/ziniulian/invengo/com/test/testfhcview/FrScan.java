package ziniulian.invengo.com.test.testfhcview;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.Date;

/**
 * Created by LZR on 2017/4/27.
 */

public class FrScan extends Fragment {
	private MainActivity ma;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.scan, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		ma = (MainActivity)getActivity();

		Button b;
		b = (Button)(getView().findViewById(R.id.back));
		b.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getFragmentManager().beginTransaction().remove(ma.frScan).commit();
				getFragmentManager().beginTransaction().replace(R.id.frdiv, ma.frMain).commit();
			}
		});

		b = (Button)(getView().findViewById(R.id.scan));
		b.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ma.demo.qryTag();
			}
		});

		b = (Button)(getView().findViewById(R.id.save));
		b.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				TextView tv;
				tv = (TextView)(getView().findViewById(R.id.tim));
				if (!(tv.getText().equals(""))) {
					StringBuilder s = new StringBuilder();
					s.append(tv.getText());
					s.append("\n");
					tv.setText("");

					tv = (TextView)(getView().findViewById(R.id.pro));
//					s.append("属性:");
					s.append(tv.getText());
					s.append("\n");
					tv.setText("");

					tv = (TextView)(getView().findViewById(R.id.typ));
//					s.append("车种车型:");
					s.append(tv.getText());
					s.append("\n");
					tv.setText("");

					tv = (TextView)(getView().findViewById(R.id.num));
//					s.append("车号:");
					s.append(tv.getText());
					s.append("\n");
					tv.setText("");

					tv = (TextView)(getView().findViewById(R.id.fac));
//					s.append("制造厂:");
					s.append(tv.getText());
					s.append("\n");
					tv.setText("");

					tv = (TextView)(getView().findViewById(R.id.mdt));
//					s.append("制造年月:");
					s.append(tv.getText());
					s.append("\n");
					tv.setText("");

//					s.append("修程:");
					s.append(((Spinner)(getView().findViewById(R.id.xiu))).getSelectedItem().toString());
					s.append("\n");

					toFile(s.toString());
				}
			}
		});
	}

	// 写入文件
	private void toFile (String s) {
		OutputStream out=null;
		try {
			out = ma.openFileOutput(ma.path, Context.MODE_APPEND);
			out.write(s.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// 清空界面
	public void clearUi () {
		TextView tv;
		tv = (TextView)(getView().findViewById(R.id.pro));
		tv.setText("");
		tv = (TextView)(getView().findViewById(R.id.typ));
		tv.setText("");
		tv = (TextView)(getView().findViewById(R.id.num));
		tv.setText("");
		tv = (TextView)(getView().findViewById(R.id.fac));
		tv.setText("");
		tv = (TextView)(getView().findViewById(R.id.mdt));
		tv.setText("");
		tv = (TextView)(getView().findViewById(R.id.tim));
		tv.setText("");
	}

	// 更新界面
	public void updateUi (String pro, String typ, String num, String fac, String mdt) {
		TextView tv;
		tv = (TextView)(getView().findViewById(R.id.pro));
		tv.setText(pro);
		tv = (TextView)(getView().findViewById(R.id.typ));
		tv.setText(typ);
		tv = (TextView)(getView().findViewById(R.id.num));
		tv.setText(num);
		tv = (TextView)(getView().findViewById(R.id.fac));
		tv.setText(fac);
		tv = (TextView)(getView().findViewById(R.id.mdt));
		tv.setText(mdt);
		tv = (TextView)(getView().findViewById(R.id.tim));
		tv.setText(DateFormat.getDateTimeInstance().format(new Date()));
	}
}
