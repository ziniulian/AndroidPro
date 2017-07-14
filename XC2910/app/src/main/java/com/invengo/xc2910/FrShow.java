package com.invengo.xc2910;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by LZR on 2017/4/27.
 */

public class FrShow extends Fragment {
	private MainActivity ma;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.show, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		ma = (MainActivity)getActivity();
		ListView lv = (ListView)(getView().findViewById(R.id.lv));
		ArrayList<HashMap<String, String>> lvdat = new ArrayList<>();
		SimpleAdapter lvadp;

		ma.db.find(lvdat);

		// 关联列表数据
		lvadp = new SimpleAdapter(ma, lvdat, R.layout.lvitem,
				new String[] {"tim", "cod", "xiu"},
				new int[] {R.id.tim, R.id.cod, R.id.xiu});
		lv.setAdapter(lvadp);
	}

}
