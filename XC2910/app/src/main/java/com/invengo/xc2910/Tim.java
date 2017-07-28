package com.invengo.xc2910;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.Calendar;

/**
 * Created by LZR on 2017/7/26.
 */

public class Tim implements Runnable {
	private Calendar t = Calendar.getInstance();	// 时间
	private long offset = 0;	// 时间差
	private MainActivity ma;
	private Thread tr = null;
	private String path = "tim.txt";

	public Tim (MainActivity m) {
		this.ma = m;
		try {
			// 读取时间差
			BufferedReader br = new BufferedReader(new InputStreamReader(m.openFileInput(this.path)));
			this.offset = Long.parseLong(br.readLine());
			br.close();
		} catch (Exception e) {
//			e.printStackTrace();
		}
		this.setOffset(this.offset);
		this.start();
	}

	public void setOffset(long offset) {
		this.offset = offset;
		this.setT();
	}

	public void setT(int y, int m, int d, int h, int f, int s) {
		this.stop();
		this.t.set(y, m, d, h, f, s);
		this.offset = this.t.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
		this.start();
		try {
			// 保存时间差
			FileOutputStream fs = this.ma.openFileOutput(this.path, Context.MODE_PRIVATE);
			fs.write((this.offset + "\n").getBytes());
			fs.close();
		} catch (Exception e) {
//			e.printStackTrace();
		}
	}

	public void setT() {
		this.t.setTimeInMillis(Calendar.getInstance().getTimeInMillis() + this.offset);
	}

	public Calendar getT() {
		return this.t;
	}

	public Thread start() {
		if (this.tr == null) {
			this.tr = new Thread(this);
			this.tr.start();
		}
		return this.tr;
	}

	public void stop() {
		if (this.tr != null) {
			this.tr.interrupt();
			this.tr = null;
		}
	}

	@Override
	public void run() {
		try {
//			// 每秒更新一次
//			while (!this.tr.isInterrupted()) {
//				ma.flushTim();
//				Thread.sleep(1000);
//				this.t.add(Calendar.SECOND, 1);
//			}

			// 每分钟更新一次
			ma.flushTim();
			Thread.sleep(1000 * (60 - this.t.get(Calendar.SECOND)));
			while (!this.tr.isInterrupted()) {
				this.setT();
				ma.flushTim();
				Thread.sleep(60000);
			}
		} catch (Exception e) {
//			e.printStackTrace();
		}
	}
}
