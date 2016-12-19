package com.jiangzi.ziniulian.android.util;

/**
 * Created by ziniulian on 2016/12/15.
 */

public interface InfBaseJsObj {
	// 输出信息
	public void log (String msg);

	// 向文件添加信息
	public boolean appendFile (String fileNam, String msg);

	// 读取文件信息
	public String readFile (String fileNam);

	// 向SD卡的文件添加信息
	public boolean appendSdFile (String fileNam, String msg);

	// 读取SD卡的文件信息
	public String readSdFile (String fileNam);
}
