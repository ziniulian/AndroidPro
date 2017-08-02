package com.invengo.xc2910;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.media.AudioManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

/**
 * Created by LZR on 2017/4/27.	3070
 */

public class FrMain extends Fragment {
	private MainActivity ma;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.main, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		ma = (MainActivity)getActivity();

		// 亮度调节功能
		SeekBar sb = (SeekBar)(getView().findViewById(R.id.ctrlLit));
		checkScreenBrightness (sb);
		sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				setScreenBritness(progress);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}
		});

		//音量控制,初始化定义
		sb = (SeekBar)(getView().findViewById(R.id.ctrlVol));
		checkVol(sb);
		sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				setVol(progress);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}
		});

		Button b;
		// 扫描标签
		b = (Button)(getView().findViewById(R.id.scan));
		b.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hidCtrl ();
				ma.setF("s");
			}
		});

		// 浏览标签
		b = (Button)(getView().findViewById(R.id.show));
		b.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hidCtrl ();
				ma.setF("w");
			}
		});

		// 上传
		b = (Button)(getView().findViewById(R.id.update));
		b.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hidCtrl ();
				if (ma.ds.tsend(ma.db.getPath()) == null) {
					Toast.makeText(ma.getApplicationContext(), "发送异常...", Toast.LENGTH_SHORT).show();
				} else {
					// TODO: 2017/6/30
				}
			}
		});

		// 清空数据
		b = (Button)(getView().findViewById(R.id.clear));
		b.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hidCtrl ();
				clearDat();
			}
		});

		// 校时
		b = (Button)(getView().findViewById(R.id.tim));
		b.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hidCtrl ();
				setAutoTim();
			}
		});

		// 音量
		b = (Button)(getView().findViewById(R.id.vol));
		b.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hidCtrl ();
				getView().findViewById(R.id.ctrlVolOut).setVisibility(View.VISIBLE);
			}
		});
		b = (Button)(getView().findViewById(R.id.ctrlVolHid));
		b.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hidCtrl ();
			}
		});

		// 亮度
		b = (Button)(getView().findViewById(R.id.lit));
		b.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hidCtrl ();
				getView().findViewById(R.id.ctrlLitOut).setVisibility(View.VISIBLE);
			}
		});
		b = (Button)(getView().findViewById(R.id.ctrlLitHid));
		b.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hidCtrl ();
			}
		});

		// 关于
		b = (Button)(getView().findViewById(R.id.about));
		b.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO: 2017/5/5  
				hidCtrl ();
			}
		});
	}

	// 检查系统亮度并设置seekBar
	private void checkScreenBrightness(SeekBar sb) {
		//先关闭系统的亮度自动调节
		try {
			if(android.provider.Settings.System.getInt(ma.getContentResolver(),android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE) == android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC) {
				android.provider.Settings.System.putInt(ma.getContentResolver(),
						android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE,
						android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
			}
		} catch (Settings.SettingNotFoundException e) {
			e.printStackTrace();
		}

		//获取当前亮度,获取失败则返回255
		int intScreenBrightness=(int)(android.provider.Settings.System.getInt(ma.getContentResolver(),
				android.provider.Settings.System.SCREEN_BRIGHTNESS,
				255));

		//进度条显示
		sb.setProgress(intScreenBrightness);
	}

	//屏幕亮度
	private void setScreenBritness(int brightness) {
		//不让屏幕全暗
		if(brightness<=1) {
			brightness=1;
		}

		//设置当前activity的屏幕亮度
		WindowManager.LayoutParams lp = ma.getWindow().getAttributes();

		//0到1,调整亮度暗到全亮
		lp.screenBrightness = Float.valueOf(brightness/255f);
		ma.getWindow().setAttributes(lp);

		//保存为系统亮度方法1
		android.provider.Settings.System.putInt(ma.getContentResolver(),
				android.provider.Settings.System.SCREEN_BRIGHTNESS,
				brightness);

		//保存为系统亮度方法2
//        Uri uri = android.provider.Settings.System.getUriFor("screen_brightness");
//        android.provider.Settings.System.putInt(getContentResolver(), "screen_brightness", brightness);
//        // resolver.registerContentObserver(uri, true, myContentObserver);
//        getContentResolver().notifyChange(uri, null);
	}

	// 检查系统音量并设置seekBar
	private void checkVol (SeekBar sb) {
		//最大音量
		int maxVolume = ma.am.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);

		//当前音量
		int currentVolume = ma.am.getStreamVolume(AudioManager.STREAM_SYSTEM);

		sb.setMax(maxVolume);
		sb.setProgress(currentVolume);
	}

	// 设置音量
	private void setVol (int v) {
		// 系统音量
		ma.am.setStreamVolume(AudioManager.STREAM_SYSTEM, v, 0);

		// 媒体音量
		// mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, v, 0);

		// 播放一个提示音
		ma.mkNtf("30");
	}

	// 隐藏显示
	private void hidCtrl () {
		LinearLayout layout;
		layout = (LinearLayout)(getView().findViewById(R.id.ctrlLitOut));
		layout.setVisibility(View.GONE);
		layout = (LinearLayout)(getView().findViewById(R.id.ctrlVolOut));
		layout.setVisibility(View.GONE);
	}

	// 设置为自动校准时间
	@SuppressLint("NewApi")
	private void setAutoTim() {
//		// 需要root，还要将apk文件push到system app目录下。<uses-permission android:name="android.permission.WRITE_SECURE_SETTINGS" />
//		try {
//			if (Settings.System.getInt(ma.getContentResolver(),Settings.Global.AUTO_TIME_ZONE) == 0) {
//				Settings.Global.putInt(ma.getContentResolver(),Settings.Global.AUTO_TIME_ZONE,1);
//			}
//			if (Settings.System.getInt(ma.getContentResolver(),Settings.Global.AUTO_TIME) == 0) {
//				Settings.Global.putInt(ma.getContentResolver(),Settings.Global.AUTO_TIME,1);
//			}
//		} catch (Settings.SettingNotFoundException e) {
//			e.printStackTrace();
//		}
//
//		Toast toast = Toast.makeText(ma.getApplicationContext(), "时间已校准！", Toast.LENGTH_SHORT);
//		toast.show();
	}

	// 清空数据
	private void clearDat() {
		ma.db.del();
		Toast toast = Toast.makeText(ma.getApplicationContext(), "数据已清空！", Toast.LENGTH_SHORT);
		toast.show();
	}
}
