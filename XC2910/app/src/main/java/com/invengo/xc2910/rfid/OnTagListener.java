package com.invengo.xc2910.rfid;

import java.util.Map;

/**
 * Created by LZR on 2017/5/23.
 */

public interface OnTagListener {
	public void onTag (Map<String, String> r);
}
