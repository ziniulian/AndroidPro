package com.jiangzi.ziniulian.android.util;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import ziniulian.invengo.com.test.testfhcview.R;

/**
 * Created by LZR on 2017/4/27.
 */

public abstract class SingleFragmentActivity extends FragmentActivity {
	protected abstract Fragment createFragment();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		FragmentManager fm = getSupportFragmentManager();
		Fragment fragment = fm.findFragmentById(R.id.id_fragment_container);
		if(fragment == null) {
			fragment = createFragment();
			fm.beginTransaction().add(R.id.id_fragment_container,fragment).commit();
		}
	}
}
