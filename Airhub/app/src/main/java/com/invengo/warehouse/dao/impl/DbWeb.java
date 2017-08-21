package com.invengo.warehouse.dao.impl;

import com.invengo.warehouse.enums.EmWebSql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

/**
 * 服务器数据库
 * Created by LZR on 2017/8/16.
 */

public class DbWeb {
	private Connection con;
	private String driver = "com.mysql.jdbc.Driver";
	private String url = "jdbc:mysql://192.169.0.12:3306/jeesite";
	private String user = "root";
	private String pwd = "123456";

	// 连接数据库
	public boolean open () {
		boolean r = false;
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url,user,pwd);
			r = !con.isClosed();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return r;
	}

	// 关闭数据库
	public void close () {
		try {
			con.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getStr (ResultSet rs, String field) {
		String s = null;
		try {
			s = rs.getString(field);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (s != null) {
			return '\"' + s + '\"';
		} else {
			return null;
		}
	}

	private Long getTim (ResultSet rs, String field) {
		Timestamp t = null;
		try {
			t = rs.getTimestamp(field);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (t != null) {
			return t.getTime();
		} else {
			return null;
		}
	}

	// 密码检查
	private boolean checkPwd (String pwd, String dbPwd) {
		// TODO: 2017/8/17
		return pwd.equals(dbPwd);
	}

	// 登录
	public StringBuilder signin (String userNam, String pwd) {
		StringBuilder r = new StringBuilder();
		boolean b = false;
		try {
			PreparedStatement psql = con.prepareStatement(EmWebSql.GetPwd.toString());
			psql.setString(1, userNam);
			ResultSet rs = psql.executeQuery();
			if (rs.next()) {
				b = checkPwd(pwd, rs.getString(1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		r.append(b);
		r.append(",\"user\":\"");
		r.append(userNam);
		r.append('\"');
		return r;
	}

	// 获取设备的详细信息
	public StringBuilder getDevInfo (String id) {
		StringBuilder r = new StringBuilder();
		boolean b = false;
		try {
			PreparedStatement psql = con.prepareStatement(EmWebSql.GetDevInfo.toString());
			psql.setString(1, id);
			ResultSet rs = psql.executeQuery();
			if (rs.next()) {
				b = true;
				r.append(b);

				// TODO: 2017/8/17
				r.append(",\"devid\":");
				r.append(getStr(rs, "deviceid"));
				r.append(",\"nam\":");
				r.append(getStr(rs, "devicename"));
				r.append(",\"brand\":");
				r.append(getStr(rs, "brand"));
				r.append(",\"mod\":");
				r.append(getStr(rs, "model"));
				r.append(",\"unit\":");
				r.append(getStr(rs, "unit"));
				r.append(",\"price\":");
				r.append(rs.getBigDecimal("price"));
				r.append(",\"level\":");
				r.append(getStr(rs, "level"));
				r.append(",\"sn\":");
				r.append(getStr(rs, "serialnum"));
				r.append(",\"tim\":");
				r.append(getTim(rs, "arrivalT"));
				r.append(",\"intim\":");
				r.append(getTim(rs, "intime"));
				r.append(",\"isout\":");
				r.append(rs.getString("isout"));
				r.append(",\"cont\":");
				r.append(rs.getInt("Coucount"));
				r.append(",\"location\":");
				r.append(getStr(rs, "location"));
				r.append(",\"fegularcycle\":");
				r.append(rs.getInt("fegularcycle"));
				r.append(",\"iscrap\":");
				r.append(rs.getString("Scrapped"));
				r.append(",\"remark\":");
				r.append(getStr(rs, "remark"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!b) {
			r.append(b);
		}
		return r;
	}

	// 获取设备的领用记录
	public StringBuilder getBorrowByDev (String id) {
		StringBuilder r = new StringBuilder();
		boolean b = false;
		try {
			PreparedStatement psql = con.prepareStatement(EmWebSql.GetBorrowByDev.toString());
			psql.setString(1, id);
			ResultSet rs = psql.executeQuery();
			while (rs.next()) {
				if (!b) {
					b = true;
					r.append(b);
					r.append(",\"dat\":[");
				}

				// TODO: 2017/8/17
				r.append("{\"devid\":");
				r.append(getStr(rs, "deviceid"));
				r.append(",\"rid\":");
				r.append(getStr(rs, "recordid"));
				r.append(",\"tim\":");
				r.append(getTim(rs, "receiveT"));
				r.append(",\"dep\":");
				r.append(getStr(rs, "receiveU"));
				r.append(",\"person\":");
				r.append(getStr(rs, "receiveP"));
				r.append(",\"reason\":");
				r.append(getStr(rs, "out_reason"));
				r.append(",\"comtim\":");
				r.append(getTim(rs, "completeT"));
				r.append('}');
				r.append(',');
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!b) {
			r.append(b);
		} else {
			r.deleteCharAt(r.length() - 1);
			r.append(']');
		}
		return r;
	}

	// 获取所有出库表
	public StringBuilder getAllOut () {
		StringBuilder r = new StringBuilder();
		boolean b = false;
		try {
			PreparedStatement psql = con.prepareStatement(EmWebSql.GetAllOut.toString());
			ResultSet rs = psql.executeQuery();
			while (rs.next()) {
				if (!b) {
					b = true;
					r.append(b);
					r.append(",\"dat\":[");
				}

				// TODO: 2017/8/16
				r.append("{\"rid\":");
				r.append(getStr(rs, "recordid"));
				r.append(",\"osp\":");
				r.append(getStr(rs, "outstatesP"));
				r.append(",\"ost\":");
				r.append(getTim(rs, "outstatesT"));
				r.append('}');
				r.append(',');
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!b) {
			r.append(b);
		} else {
			r.deleteCharAt(r.length() - 1);
			r.append(']');
		}
		return r;
	}

	// 获取所有领用记录
	public StringBuilder getAllBorrow () {
		StringBuilder r = new StringBuilder();
		boolean b = false;
		try {
			PreparedStatement psql = con.prepareStatement(EmWebSql.GetAllBorrow.toString());
			ResultSet rs = psql.executeQuery();
			while (rs.next()) {
				if (!b) {
					b = true;
					r.append(b);
					r.append(",\"dat\":[");
				}

				// TODO: 2017/8/16
				r.append("{\"devid\":");
				r.append(getStr(rs, "deviceid"));
				r.append(",\"rid\":");
				r.append(getStr(rs, "recordid"));
				r.append(",\"tim\":");
				r.append(getTim(rs, "receiveT"));
				r.append(",\"dep\":");
				r.append(getStr(rs, "receiveU"));
				r.append(",\"person\":");
				r.append(getStr(rs, "receiveP"));
				r.append(",\"reason\":");
				r.append(getStr(rs, "out_reason"));
				r.append(",\"comtim\":");
				r.append(getTim(rs, "completeT"));
				r.append('}');
				r.append(',');
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!b) {
			r.append(b);
		} else {
			r.deleteCharAt(r.length() - 1);
			r.append(']');
		}
		return r;
	}

	// 新增一张出库表
	public boolean crtOut (String id) {
		boolean b = false;
		try {
			PreparedStatement psql = con.prepareStatement(EmWebSql.CrtOut.toString());
			psql.setString(1, id);
			b = psql.executeUpdate() != 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return b;
	}

	// 新增领用记录
	public boolean crtBorrow (String id, String devid) {
		boolean b = false;
		try {
			PreparedStatement psql = con.prepareStatement(EmWebSql.CrtBorrow.toString());
			psql.setString(1, id);
			psql.setString(2, devid);
			b = psql.executeUpdate() != 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return b;
	}

	// 获取所有未完成的出库表
	public StringBuilder getAllUdOut () {
		StringBuilder r = new StringBuilder();
		boolean b = false;
		try {
			PreparedStatement psql = con.prepareStatement(EmWebSql.GetAllUdOut.toString());
			ResultSet rs = psql.executeQuery();
			while (rs.next()) {
				if (!b) {
					b = true;
					r.append(b);
					r.append(",\"dat\":[");
				}

				// TODO: 2017/8/16
				r.append("{\"rid\":");
				r.append(getStr(rs, "recordid"));
				r.append('}');
				r.append(',');
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!b) {
			r.append(b);
		} else {
			r.deleteCharAt(r.length() - 1);
			r.append(']');
		}
		return r;
	}

	// 获取出库表的详细信息
	public StringBuilder getOutInfo (String id) {
		StringBuilder r = new StringBuilder();
		boolean b = false;
		try {
			PreparedStatement psql = con.prepareStatement(EmWebSql.GetOutInfo.toString());
			psql.setString(1, id);
			ResultSet rs = psql.executeQuery();
			if (rs.next()) {
				b = true;
				r.append(b);

				// TODO: 2017/8/16
				r.append(",\"rid\":");
				r.append(getStr(rs, "recordid"));
				r.append(",\"osp\":");
				r.append(getStr(rs, "outstatesP"));
				r.append(",\"ost\":");
				r.append(getTim(rs, "outstatesT"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!b) {
			r.append(b);
		}
		return r;
	}

	// 获取出库表相关的设备信息
	public StringBuilder getDevsByOut (String id) {
		StringBuilder r = new StringBuilder();
		boolean b = false;
		try {
			PreparedStatement psql = con.prepareStatement(EmWebSql.GetDevsByOut.toString());
			psql.setString(1, id);
			ResultSet rs = psql.executeQuery();
			while (rs.next()) {
				if (!b) {
					b = true;
					r.append(b);
					r.append(",\"dat\":[");
				}

				// TODO: 2017/8/16
				r.append("{\"devid\":");
				r.append(getStr(rs, "deviceid"));
				r.append('}');
				r.append(',');
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!b) {
			r.append(b);
		} else {
			r.deleteCharAt(r.length() - 1);
			r.append(']');
		}
		return r;
	}

	// 删除一笔领用记录
	public boolean delOneBorrow (String id, String devid) {
		boolean b = false;
		try {
			PreparedStatement psql = con.prepareStatement(EmWebSql.DelOneBorrow.toString());
			psql.setString(1, id);
			psql.setString(2, devid);
			b = psql.executeUpdate() != 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return b;
	}

	// 删除一张出库表
	public boolean delOut (String id) {
		boolean b = false;
		try {
			PreparedStatement psql = con.prepareStatement(EmWebSql.DelBorrowInOut.toString());
			psql.setString(1, id);
			psql.executeUpdate();
			psql = con.prepareStatement(EmWebSql.DelOut.toString());
			psql.setString(1, id);
			b = psql.executeUpdate() != 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return b;
	}

	// 出库完成
	public boolean outOk (String id, String user) {
		boolean b = false;
		try {
			PreparedStatement psql = con.prepareStatement(EmWebSql.OutOk.toString());
			psql.setString(1, user);
			psql.setString(2, id);
			b = psql.executeUpdate() != 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return b;
	}

	// 获取所有盘点计划
	public StringBuilder getAllPlan () {
		StringBuilder r = new StringBuilder();
		boolean b = false;
		try {
			PreparedStatement psql = con.prepareStatement(EmWebSql.GetAllPlan.toString());
			ResultSet rs = psql.executeQuery();
			while (rs.next()) {
				if (!b) {
					b = true;
					r.append(b);
					r.append(",\"dat\":[");
				}

				// TODO: 2017/8/16
				r.append("{\"rid\":");
				r.append(getStr(rs, "recordid"));
				r.append(",\"person\":");
				r.append(getStr(rs, "planP"));
				r.append(",\"tim\":");
				r.append(getTim(rs, "planT"));
				r.append(",\"iscom\":");
				r.append(rs.getString("complete"));
				r.append(",\"remark\":");
				r.append(getStr(rs, "remark"));
				r.append('}');
				r.append(',');
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!b) {
			r.append(b);
		} else {
			r.deleteCharAt(r.length() - 1);
			r.append(']');
		}
		return r;
	}

	// 获取所有盘点记录
	public StringBuilder getAllRecord () {
		StringBuilder r = new StringBuilder();
		boolean b = false;
		try {
			PreparedStatement psql = con.prepareStatement(EmWebSql.GetAllRecord.toString());
			ResultSet rs = psql.executeQuery();
			while (rs.next()) {
				if (!b) {
					b = true;
					r.append(b);
					r.append(",\"dat\":[");
				}

				// TODO: 2017/8/16
				r.append("{\"devid\":");
				r.append(getStr(rs, "deviceid"));
				r.append(",\"rid\":");
				r.append(getStr(rs, "t_inventory_plan_id"));
				r.append(",\"tim\":");
				r.append(getTim(rs, "recordT"));
				r.append(",\"person\":");
				r.append(getStr(rs, "recordP"));
				r.append(",\"status\":");
				r.append(rs.getString("isexist"));
				r.append('}');
				r.append(',');
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!b) {
			r.append(b);
		} else {
			r.deleteCharAt(r.length() - 1);
			r.append(']');
		}
		return r;
	}

	// 新增盘点计划
	public boolean crtPlan (String id, String user) {
		boolean b = false;
		try {
			PreparedStatement psql = con.prepareStatement(EmWebSql.CrtPlan.toString());
			psql.setString(1, id);
			psql.setString(2, user);
			b = psql.executeUpdate() != 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return b;
	}

	// 新增盘点记录
	public boolean crtRecord (String id, String devid, String user, String status) {
		boolean b = false;
		try {
			PreparedStatement psql = con.prepareStatement(EmWebSql.CrtRecord.toString());
			psql.setString(1, id);
			psql.setString(2, devid);
			psql.setString(3, user);
			psql.setString(4, status);
			b = psql.executeUpdate() != 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return b;
	}

	// 获取所有未完成的盘点计划
	public StringBuilder getAllUdPlan () {
		StringBuilder r = new StringBuilder();
		boolean b = false;
		try {
			PreparedStatement psql = con.prepareStatement(EmWebSql.GetAllUdPlan.toString());
			ResultSet rs = psql.executeQuery();
			while (rs.next()) {
				if (!b) {
					b = true;
					r.append(b);
					r.append(",\"dat\":[");
				}

				// TODO: 2017/8/16
				r.append("{\"rid\":");
				r.append(getStr(rs, "recordid"));
				r.append('}');
				r.append(',');
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!b) {
			r.append(b);
		} else {
			r.deleteCharAt(r.length() - 1);
			r.append(']');
		}
		return r;
	}

	// 获取盘点计划的详细信息
	public StringBuilder getPlanInfo (String id) {
		StringBuilder r = new StringBuilder();
		boolean b = false;
		try {
			PreparedStatement psql = con.prepareStatement(EmWebSql.GetPlanInfo.toString());
			psql.setString(1, id);
			ResultSet rs = psql.executeQuery();
			if (rs.next()) {
				b = true;
				r.append(b);

				// TODO: 2017/8/16
				r.append("{\"rid\":");
				r.append(getStr(rs, "recordid"));
				r.append(",\"person\":");
				r.append(getStr(rs, "planP"));
				r.append(",\"tim\":");
				r.append(getTim(rs, "planT"));
				r.append(",\"iscom\":");
				r.append(rs.getString("complete"));
				r.append(",\"remark\":");
				r.append(getStr(rs, "remark"));
				r.append('}');
				r.append(',');
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!b) {
			r.append(b);
		}
		return r;
	}

	// 获取盘点计划相关的设备信息
	public StringBuilder getDevsByPlan (String id) {
		StringBuilder r = new StringBuilder();
		boolean b = false;
		try {
			PreparedStatement psql = con.prepareStatement(EmWebSql.GetDevsByPlan.toString());
			psql.setString(1, id);
			ResultSet rs = psql.executeQuery();
			while (rs.next()) {
				if (!b) {
					b = true;
					r.append(b);
					r.append(",\"dat\":[");
				}

				// TODO: 2017/8/16
				r.append("{\"devid\":");
				r.append(getStr(rs, "deviceid"));
				r.append(",\"status\":");
				r.append(rs.getString("isexist"));
				r.append('}');
				r.append(',');
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!b) {
			r.append(b);
		} else {
			r.deleteCharAt(r.length() - 1);
			r.append(']');
		}
		return r;
	}

	// 删除一笔盘点记录
	public boolean delOneRecord (String id, String devid) {
		boolean b = false;
		try {
			PreparedStatement psql = con.prepareStatement(EmWebSql.DelOneRecord.toString());
			psql.setString(1, id);
			psql.setString(2, devid);
			b = psql.executeUpdate() != 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return b;
	}

	// 删除一个盘点计划
	public boolean delPlan (String id) {
		boolean b = false;
		try {
			PreparedStatement psql = con.prepareStatement(EmWebSql.DelRecordInPlan.toString());
			psql.setString(1, id);
			psql.executeUpdate();
			psql = con.prepareStatement(EmWebSql.DelPlan.toString());
			psql.setString(1, id);
			b = psql.executeUpdate() != 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return b;
	}

	// 清除一个已盘点的记录
	public boolean clearRecord (String id, String devid) {
		boolean b = false;
		try {
			PreparedStatement psql = con.prepareStatement(EmWebSql.ClearRecord.toString());
			psql.setString(1, id);
			psql.setString(2, devid);
			b = psql.executeUpdate() != 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return b;
	}

	// 标记一个未盘点的记录
	public boolean markRecord (String id, String devid, String user) {
		boolean b = false;
		try {
			PreparedStatement psql = con.prepareStatement(EmWebSql.MarkRecord.toString());
			psql.setString(1, user);
			psql.setString(2, id);
			psql.setString(3, devid);
			b = psql.executeUpdate() != 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return b;
	}

	// 盘点完成
	public boolean planOk (String id) {
		boolean b = false;
		try {
			PreparedStatement psql = con.prepareStatement(EmWebSql.PlanOk.toString());
			psql.setString(1, id);
			b = psql.executeUpdate() != 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return b;
	}

}
