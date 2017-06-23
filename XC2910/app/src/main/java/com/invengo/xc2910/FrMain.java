package com.invengo.xc2910;

import android.app.Fragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
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
	private AudioManager mAudioManager;
	private NotificationManager mNotificationManager;
	private Notification n = new Notification();

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
		mAudioManager = (AudioManager) ma.getSystemService(Context.AUDIO_SERVICE);
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

		//通知控制,初始化定义
		mNotificationManager = (NotificationManager)ma.getSystemService(Context.NOTIFICATION_SERVICE);
		n.defaults = Notification.DEFAULT_SOUND;

		Button b;
		// 扫描标签
		b = (Button)(getView().findViewById(R.id.scan));
		b.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hidCtrl ();
				getFragmentManager().beginTransaction().remove(ma.frMain).commit();
				getFragmentManager().beginTransaction().replace(R.id.frdiv, ma.frScan).commit();
			}
		});

		// 浏览标签
		b = (Button)(getView().findViewById(R.id.show));
		b.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hidCtrl ();
				getFragmentManager().beginTransaction().remove(ma.frMain).commit();
				getFragmentManager().beginTransaction().replace(R.id.frdiv, ma.frShow).commit();
			}
		});

		// 上传
		b = (Button)(getView().findViewById(R.id.update));
		b.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO
				hidCtrl ();
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
		int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);

		//当前音量
		int currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);

		sb.setMax(maxVolume);
		sb.setProgress(currentVolume);
	}

	// 设置音量
	private void setVol (int v) {
		// 系统音量
		mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, v, 0);

		// 媒体音量
		// mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, v, 0);

		// 播放一个提示音
		if (mNotificationManager != null) {
			mNotificationManager.notify(1, n);
		}
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
	private void setAutoTim() {
		try {
			if (Settings.System.getInt(ma.getContentResolver(),Settings.System.AUTO_TIME_ZONE) == 0) {
				Settings.System.putInt(ma.getContentResolver(),Settings.System.AUTO_TIME_ZONE,1);
			}
			if (Settings.System.getInt(ma.getContentResolver(),Settings.System.AUTO_TIME) == 0) {
				Settings.System.putInt(ma.getContentResolver(),Settings.System.AUTO_TIME,1);
			}
		} catch (Settings.SettingNotFoundException e) {
			e.printStackTrace();
		}

		Toast toast = Toast.makeText(ma.getApplicationContext(), "时间已校准！", Toast.LENGTH_SHORT);
		toast.show();
	}

	// 清空数据
	private void clearDat() {
		ma.deleteFile(ma.path);
		Toast toast = Toast.makeText(ma.getApplicationContext(), "数据已清空！", Toast.LENGTH_SHORT);
		toast.show();
	}
}
