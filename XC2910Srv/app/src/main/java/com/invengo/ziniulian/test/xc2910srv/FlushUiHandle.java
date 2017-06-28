package com.invengo.ziniulian.test.xc2910srv;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

/**
 * Created by LZR on 2017/6/28.
 */

public class FlushUiHandle extends Handler {
	private MainActivity ma;

	public void setMa (MainActivity a) {
		this.ma = a;
	}

	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);
		Bundle dat = msg.getData();
		Toast toast;
		switch (dat.getString("stat")) {
			case "prt":
				ma.print(dat.getString("cont"));
				break;
			case "msg":
				toast = Toast.makeText(ma.getApplicationContext(), dat.getString("cont"), Toast.LENGTH_SHORT);
				toast.show();
				break;
		}
	}
}
