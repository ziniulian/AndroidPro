package com.invengo.xc2910;

import android.app.Fragment;
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
import com.invengo.train.tag.xc2910.Nulltag;

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
	private Spinner sn = null;
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
		lv = (ListView)(getView().findViewById(R.id.lv));
		sn = (Spinner)(getView().findViewById(R.id.xiu));
		ma = (MainActivity)getActivity();

		//创建简单适配器SimpleAdapter
		lvadp = new SimpleAdapter(ma, lvdat, R.layout.lvonetag,
				new String[] {"k","v"},
				new int[] {R.id.lvK, R.id.lvV});

		//加载SimpleAdapter到ListView中
		lv.setAdapter(lvadp);

		Button b = (Button)(getView().findViewById(R.id.save));
		b.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				sav();
			}
		});
	}

	@Override
	public void onPause() {
		super.onPause();
		clearUi();
	}

	public void setTag(InfViewTag tag) {
		this.tag = tag;
	}

	// 保存数据
	protected void sav() {
		if (tag != null && !(tag.getClass().equals(Nulltag.class))) {
			ma.db.add(
					getTim(),
					tag.getCod(),
					getXiu()
			);
		}
		clearUi();
	}

	// 切换修程选项
	protected void chgXiu(int i) {
		int n = sn.getCount();
		int p = sn.getSelectedItemPosition() + i;
		if (p < 0) {
			p += n;
		} else if (p >= n) {
			p -= n;
		}
		sn.setSelection(p);
	}

	// 清空界面
	private void clearUi () {
		tag = null;
		updateUi(false);
	}

	// 更新界面
	public void updateUi (boolean msg) {
		TextView tv = (TextView)(getView().findViewById(R.id.tim));
		lvdat.clear();
		if (tag == null) {
			tv.setText("");
			if (msg) {
				Toast.makeText(ma, "未搜索到标签，请重试 ...", Toast.LENGTH_SHORT).show();
				ma.mkNtf("32");
			}
		} else {
			ma.mkNtf("19");
			lvdat.addAll(tag.getViewDat());
			tv.setText(ma.timFmt.format(new Date()));
		}
		lvadp.notifyDataSetChanged();
	}

	private String getTim () {
		CharSequence c = ((TextView)(getView().findViewById(R.id.tim))).getText();
		String r = c.toString();
		return r;
	}

	private String getXiu () {
		String r = null;
		switch (sn.getSelectedItem().toString()) {
			case "厂修":
				r = "C";
				break;
			case "段修":
				r = "D";
				break;
			case "辅修":
				r = "F";
				break;
			case "轴修":
				r = "Z";
				break;
			case "临修":
				r = "L";
				break;
			case "报废":
				r = "B";
				break;
		}
		return r;
	}
}
