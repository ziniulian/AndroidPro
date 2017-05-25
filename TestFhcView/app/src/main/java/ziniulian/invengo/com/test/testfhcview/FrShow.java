package ziniulian.invengo.com.test.testfhcview;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
		ArrayList<HashMap<String, Object>> lvdat = new ArrayList<>();
		SimpleAdapter lvadp;
		Button b;

		b = (Button)(getView().findViewById(R.id.back));
		b.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getFragmentManager().beginTransaction().remove(ma.frShow).commit();
				getFragmentManager().beginTransaction().replace(R.id.frdiv, ma.frMain).commit();
			}
		});

		frFile(lvdat);

		// 关联列表数据
		lvadp = new SimpleAdapter(ma, lvdat, R.layout.lvitem,
				new String[] {"tim", "pro", "typ", "num", "fac", "mdt", "xiu"},
				new int[] {R.id.tim, R.id.pro, R.id.typ, R.id.num, R.id.fac, R.id.mdt, R.id.xiu});
		lv.setAdapter(lvadp);
	}

	// 读文件
	private void frFile (ArrayList<HashMap<String, Object>> a) {
		BufferedReader f = null;
		try {
			f = new BufferedReader(new InputStreamReader(ma.openFileInput(ma.path)));
			String s = f.readLine();
			while (s != null) {
				HashMap<String, Object> r = new HashMap<>();
				r.put("tim", s);
				r.put("pro", f.readLine());
				r.put("typ", f.readLine());
				r.put("num", f.readLine());
				r.put("fac", f.readLine());
				r.put("mdt", f.readLine());
				r.put("xiu", f.readLine());
				a.add(r);
				s = f.readLine();
			}
		} catch (Exception e) {
//			e.printStackTrace();
		} finally {
			if (f != null) {
				try {
					f.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
