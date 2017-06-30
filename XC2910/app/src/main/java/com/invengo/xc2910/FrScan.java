package com.invengo.xc2910;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.invengo.train.tag.xc2910.InfViewTag;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by LZR on 2017/4/27.
 */

public class FrScan extends Fragment {
	private MainActivity ma;
	private InfViewTag tag;	// 标签

	private ListView lv = null;
	private SimpleAdapter lvadp = null;
	private List<Map<String, String>> lvdat = new ArrayList<>();

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.scan, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		ma = (MainActivity)getActivity();
		lv = (ListView)(getView().findViewById(R.id.lv));

		//创建简单适配器SimpleAdapter
		lvadp = new SimpleAdapter(ma, lvdat, R.layout.lvonetag,
				new String[] {"k","v"},
				new int[] {R.id.lvK, R.id.lvV});

		//加载SimpleAdapter到ListView中
		lv.setAdapter(lvadp);

		Button b;
		b = (Button)(getView().findViewById(R.id.back));
		b.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// 界面清空
				tag = null;
				updateUi(false);
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
				if (tag != null) {
					StringBuilder s = new StringBuilder();
					s.append(((TextView)(getView().findViewById(R.id.tim))).getText());
					s.append("\n");
					s.append(tag.getCod());
					s.append("\n");
					s.append(((Spinner)(getView().findViewById(R.id.xiu))).getSelectedItem().toString());
					s.append("\n");
					toFile(s.toString());
				}

				// 界面清空
				tag = null;
				updateUi(false);
			}
		});
	}

	public void setTag(InfViewTag tag) {
		this.tag = tag;
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

	// 更新界面
	public void updateUi (boolean msg) {
		TextView tv = (TextView)(getView().findViewById(R.id.tim));
		lvdat.clear();
		if (tag == null) {
			tv.setText("");
			if (msg) {
				Toast.makeText(ma, "未搜索到标签，请重试 ...", Toast.LENGTH_SHORT).show();
			}
		} else {
			lvdat.addAll(tag.getViewDat());
			tv.setText(DateFormat.getDateTimeInstance().format(new Date()));
		}
		lvadp.notifyDataSetChanged();
	}
}
