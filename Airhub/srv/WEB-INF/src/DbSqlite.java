package lzr.test.airhub.dao;


// import java.security.MessageDigest;
// import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
// import java.util.StringTokenizer;

// import javax.servlet.http.HttpServletRequest;
// import javax.servlet.http.HttpServletResponse;

// import org.apache.shiro.web.util.WebUtils;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.RequestMapping;

// import com.thinkgem.jeesite.common.security.Digests;
// import com.thinkgem.jeesite.common.utils.Encodes;
// import com.thinkgem.jeesite.common.utils.StringUtils;
// import com.thinkgem.jeesite.modules.sys.security.FormAuthenticationFilter;
import lzr.test.airhub.enums.EmWebSql;

/**
 * 服务器数据库
 * Created by LZR on 2017/8/16.
 */

public class DbSqlite {
	
	public static final int HASH_INTERATIONS=1024;
	
	private Connection con;
	private String driver = "org.sqlite.JDBC";
	// private String url = "jdbc:sqlite://C:/ProG/Green/X64/apache-tomcat-7.0.47/webapps/Airhub/a.db";
	private String url = "jdbc:sqlite:../../a.db";
	private String user = "";
	private String pwd = "";

	// 连接数据库
	public boolean open () {
		boolean r = false;
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, user, pwd);
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
	private String getStr1 (ResultSet rs, String field) {
		String s = null;
		try {
			s = rs.getString(field);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (s != null) {
			return '\'' + s + '\'';
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

	// public boolean crt () {
	// 	boolean b = false;
	// 	try {
	// 		PreparedStatement psql = con.prepareStatement(EmWebSql.CrtUser.toString());
	// 		b = psql.executeUpdate() != 0;
	// 	} catch (Exception e) {
	// 		e.printStackTrace();
	// 	}
	// 	return b;
	// }



// 	// 密码检查
// //	public static boolean checkPwd(String plainPassword, String password) {
// //		String plain = Encodes.unescapeHtml(plainPassword);
// //		byte[] salt = Encodes.decodeHex(password.substring(0,16));
// //		byte[] hashPassword = Digests.sha1(plain.getBytes(), salt, HASH_INTERATIONS);
// //		return password.equals(Encodes.encodeHex(salt)+Encodes.encodeHex(hashPassword));
// //	}
	
// 	public static boolean checkPwd(String plainPassword, String password) {
// 	//	System.out.println("===================="+plainPassword+"====="+password);
// 		return SHA1(plainPassword).equalsIgnoreCase(password);
// 	}
// 	public static String SHA1(String decript) {
// 		try {
// 			MessageDigest digest = java.security.MessageDigest
// 					.getInstance("SHA-1");
// 			digest.update(decript.getBytes());
// 			byte messageDigest[] = digest.digest();
// 			// Create Hex String
// 			StringBuffer hexString = new StringBuffer();
// 			// 字节数组转换为 十六进制 数
// 			for (int i = 0; i < messageDigest.length; i++) {
// 				String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
// 				if (shaHex.length() < 2) {
// 					hexString.append(0);
// 				}
// 				hexString.append(shaHex);
// 			}
// 			return hexString.toString();

// 		} catch (NoSuchAlgorithmException e) {
// 			e.printStackTrace();
// 		}
// 		return "";
// 	}

	// 登录
	public StringBuilder signin (String userNam, String pwd) {
		StringBuilder r = new StringBuilder();
		boolean b = false;
		try {
			PreparedStatement psql = con.prepareStatement(EmWebSql.Signin.toString());
			psql.setString(1, userNam);
			psql.setString(2, pwd);
			ResultSet rs = psql.executeQuery();
			b = rs.next();
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
				// r.append(rs.getBigDecimal("price"));
				r.append(rs.getDouble("price"));
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
				r.append(rs.getString("scrapped"));
				r.append(",\"remark\":");
				r.append(getStr(rs, "remark"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (!b) {
			r.append(b);
			r.append(",\"devid\":");
			r.append("\"" + id +"\"");
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




	// 新增领用记录
	public boolean crtBorrow (String id, String devid) {
		boolean b = false;
		String[] devids = devid.split(",");
		try {
			for(int i = 0; i<devids.length;i++){
				PreparedStatement psql = con.prepareStatement(EmWebSql.CrtBorrow.toString());
				psql.setString(1, id);
				psql.setString(2, devids[i]);
				b = psql.executeUpdate() != 0;
			}
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

				r.append(",\"nam\":");
				r.append(getStr(rs, "devicename"));
				r.append(",\"mod\":");
				r.append(getStr(rs, "model"));
				r.append(",\"sn\":");
				r.append(getStr(rs, "serialnum"));

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
		String [] devids = devid.split(",");
		try {
			for(int i = 0; i < devids.length; i++){
				PreparedStatement psql = con.prepareStatement(EmWebSql.CrtRecord.toString());
				psql.setString(1, id);
				psql.setString(2, devids[i]);
				psql.setString(3, user);
				psql.setString(4, status);
				b = psql.executeUpdate() != 0;
			}
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
				r.append(",\"rid\":");
				r.append(getStr(rs, "recordid"));
				r.append(",\"person\":");
				r.append(getStr(rs, "planP"));
				r.append(",\"tim\":");
				r.append(getTim(rs, "planT"));
				r.append(",\"iscom\":");
				r.append(rs.getString("complete"));
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

				r.append(",\"nam\":");
				r.append(getStr(rs, "devicename"));
				r.append(",\"mod\":");
				r.append(getStr(rs, "model"));
				r.append(",\"sn\":");
				r.append(getStr(rs, "serialnum"));

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
// System.out.println("---- mark ----- : " + devid);
		String[] devids = devid.split(",");
		try {
			for(int i=0;i<devids.length;i++){
				PreparedStatement psql = con.prepareStatement(EmWebSql.MarkRecord.toString());
				psql.setString(1, user);
				psql.setString(2, id);
				psql.setString(3, devids[i]);
				b = psql.executeUpdate() != 0;
			}
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
	
	
	public StringBuilder getAllSlave(){
		StringBuilder r = new StringBuilder();
		boolean b = false;
		try {
			PreparedStatement psql = con.prepareStatement(EmWebSql.GetAllSlave.toString());
			ResultSet rs = psql.executeQuery();
			while (rs.next()) {
				if(!b){
					b=true;
					r.append(b);
					r.append(",\"dat\":[");
				}
				r.append("{\"rid\":");
				r.append(getStr(rs, "t_in_warehouse_master_recordid"));
				r.append(",\"devid\":");
				r.append(getStr(rs, "deviceid"));
				r.append(",\"master\":");
				r.append(getStr(rs, "masterid"));
				r.append(",\"intim\":");
				r.append(getTim(rs, "intime"));
				r.append(",\"nam\":");
				r.append(getStr(rs, "devicename"));
				r.append(",\"brand\":");
				r.append(getStr(rs, "brand"));
				r.append(",\"mod\":");
				r.append(getStr(rs, "model"));
				r.append(",\"speci\":");
				r.append(getStr(rs, "specification"));
				r.append(",\"unit\":");
				r.append(getStr(rs, "unit"));
				r.append(",\"price\":");
				r.append(rs.getBigDecimal("price"));
				r.append(",\"location\":");
				r.append(getStr(rs, "location"));
				r.append(",\"sn\":");
				r.append(getStr(rs, "serialnum"));
				r.append(",\"fegularcycle\":");
				r.append(rs.getInt("fegularcycle"));
				r.append('}');
				r.append(',');
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
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
	//添加入库表
	public boolean crtMaster(String id){
		boolean b = false;
		try{
			PreparedStatement psql = con.prepareStatement(EmWebSql.CrtMaster.toString());
			psql.setString(1, id);
			b = psql.executeUpdate()!=0;
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return b;
	}

//	public boolean crtDSlave(String id,String devid){
//		boolean b = false;
//		try{
//			PreparedStatement psql =con.prepareStatement(EmWebSql.CrtDSlave.toString());
//			psql.setString(1, id);
//			psql.setString(2, devid);
//			b = psql.executeUpdate()!=0;
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		return b;
//	}


	//新增入库设备
	public boolean crtSlave (String id, String devid) {
		boolean b = false;
		String[] devids = devid.split(",");
		try {
			for(int i=0;i<devids.length;i++){
				PreparedStatement psql = con.prepareStatement(EmWebSql.CrtSlave.toString());
				psql.setString(1, id);
				psql.setString(2, devids[i]);
				b = psql.executeUpdate() != 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return b;
	}
	//获取所有未确认的入库表
	public StringBuilder getAllUdMaster () {
		StringBuilder r = new StringBuilder();
		boolean b = false;
		try {
			PreparedStatement psql = con.prepareStatement(EmWebSql.GetAllUdMaster.toString());
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
	//获取所有已确认的入库表
	public StringBuilder getAllUnMaster () {
		StringBuilder r = new StringBuilder();
		boolean b = false;
		try {
			PreparedStatement psql = con.prepareStatement(EmWebSql.GetAllUnMaster.toString());
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
	public StringBuilder getDevsBySlave (String id) {
		StringBuilder r = new StringBuilder();
		boolean b = false;
		try {
			PreparedStatement psql = con.prepareStatement(EmWebSql.GetDevsBySlave.toString());
			psql.setString(1, id);
			ResultSet rs = psql.executeQuery();
			while (rs.next()) {
				if (!b) {
					b = true;
					r.append(b);
					r.append(",\"dat\":[");
				}

				r.append("{\"devid\":");
				r.append(getStr(rs, "deviceid"));

				r.append(",\"nam\":");
				r.append(getStr(rs, "devicename"));
				r.append(",\"mod\":");
				r.append(getStr(rs, "model"));
				r.append(",\"sn\":");
				r.append(getStr(rs, "serialnum"));

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


	// 删除一笔入库记录
//	public boolean delOneSlave (String id, String devid) {
//		boolean b = false;
//		try {
//			PreparedStatement psql = con.prepareStatement(EmWebSql.DelOneSlave.toString());
//			psql.setString(1, id);
//			b = psql.executeUpdate() != 0;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return b;
//	}

	public boolean delOneSlave(String id, String devid) {
		boolean b = false;
		try {
			PreparedStatement psql = con.prepareStatement(EmWebSql.DelOneSlave.toString());
			psql.setString(1, id);
			psql.setString(2, devid);
			b = psql.executeUpdate() != 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return b;
	}
	
	
	
	public boolean delMaster (String id) {
		boolean b = false;
		// System.out.println(id);
		try {
			PreparedStatement psql = con.prepareStatement(EmWebSql.DelSlave.toString());
			psql.setString(1, id);
			psql.executeUpdate();
			psql = con.prepareStatement(EmWebSql.DelMaster.toString());
			psql.setString(1, id);
			b = psql.executeUpdate() != 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return b;
	}
	

	public boolean masterOk (String id, String user) {
		boolean b = false;
		try {
			PreparedStatement psql = con.prepareStatement(EmWebSql.MasterOk.toString());
			psql.setString(1, user);
			psql.setString(2, id);
			b = psql.executeUpdate() != 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return b;
	}
	
	
	public StringBuilder getAllMaster(){
		StringBuilder r= new StringBuilder();
		boolean b = false;
		try{
			PreparedStatement psql = con.prepareStatement(EmWebSql.GetAllMaster.toString());
			ResultSet rs = psql.executeQuery();
			while(rs.next()){
				if(!b){
					b= true;
					r.append(b);
					r.append(",\"dat\":[");
				}
				//值
				r.append("{\"rid\":");
				r.append(getStr(rs, "recordid"));
				r.append(",\"sup\":");
				r.append(getStr(rs, "supplier"));
				r.append(",\"chase\":");
				r.append(getStr(rs, "purchase"));
				r.append(",\"reason\":");
				r.append(getStr(rs, "pur_reason"));
				r.append(",\"other\":");
				r.append(getStr(rs, "pur_other"));
				r.append(",\"fication\":");
				r.append(getStr(rs, "classification"));
				r.append(",\"statu\":");
				r.append(getStr(rs, "status"));
				r.append(",\"yn\":");
				r.append(getStr(rs, "featuresYN"));
				r.append(",\"p\":");
				r.append(getStr(rs, "featuresP"));
				r.append(",\"t\":");
				r.append(getStr(rs, "featuresT"));
				r.append(",\"c\":");
				r.append(getStr(rs, "featuresC"));
				r.append(",\"eyn\":");
				r.append(getStr(rs, "exteriorYN"));
				r.append(",\"ep\":");
				r.append(getStr(rs, "exteriorP"));
				r.append(",\"et\":");
				r.append(getStr(rs, "exteriorT"));
				r.append(",\"ec\":");
				r.append(getStr(rs, "exteriorC"));
				r.append(",\"myn\":");
				r.append(getStr(rs, "materialYN"));
				r.append(",\"mp\":");
				r.append(getStr(rs, "materialP"));
				r.append(",\"mt\":");
				r.append(getStr(rs, "materialT"));
				r.append(",\"mc\":");
				r.append(getStr(rs, "materialC"));
				r.append(",\"manage\":");
				r.append(getStr(rs, "manager"));
				r.append(",\"at\":");
				r.append(getStr(rs, "arrivalT"));
				r.append(",\"rp\":");
				r.append(getStr(rs, "registerP"));
				r.append(",\"intim\":");
				r.append(getTim(rs, "intime"));
				r.append(",\"rk\":");
				r.append(getStr(rs, "remark"));
				r.append(",\"mark\":");
				r.append(getStr(rs, "inmark"));
				r.append(",\"port\":");
				r.append(getStr(rs, "report"));
				r.append('}');
				r.append(',');
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		if(!b){
			r.append(b);
		}else {
			r.deleteCharAt(r.length() - 1);
			r.append(']');
		}
		return r;
	}
	
	//设备编码
	public StringBuilder getAllFacility () {
		StringBuilder r = new StringBuilder();
		boolean b = false;
		try {
			PreparedStatement psql = con.prepareStatement(EmWebSql.GetAllFacility.toString());
			ResultSet rs = psql.executeQuery();
			while (rs.next()) {
				if (!b) {
					b = true;
					r.append(b);
					r.append(",\"dat\":[");
				}
				r.append("{\"devid\":");
				r.append(getStr(rs, "deviceid"));
				r.append(",\"names\":");
				r.append(getTim(rs, "name"));
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
	
	// 获取所有库存表
	public StringBuilder getAllTable () {
		StringBuilder r = new StringBuilder();
		boolean b = false;
		try {
			PreparedStatement psql = con.prepareStatement(EmWebSql.GetAllTable.toString());
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

	public StringBuilder getXzTable (String min, String max) {
		StringBuilder r = new StringBuilder();
		boolean b = false;
		try {
			PreparedStatement psql = con.prepareStatement(EmWebSql.GetXzTable.toString());
			psql.setLong(1, Long.parseLong(min));
			psql.setLong(2, Long.parseLong(max));
			ResultSet rs = psql.executeQuery();
			while (rs.next()) {
				if (!b) {
					b = true;
					r.append(b);
					r.append(",\"dat\":[");
				}	
				// TODO: 2017/8/16
		//		r.append(""");
				r.append('"');
				r.append(getStr1(rs, "deviceid"));
				r.append(",");
				r.append(getStr1(rs, "devicename"));
				r.append(",");
				r.append(getStr1(rs, "brand"));
				r.append(",");
				r.append(getStr1(rs, "model"));
				r.append(",");
				r.append(getStr1(rs, "unit"));
				r.append(",");
				// r.append(rs.getBigDecimal("price"));
				r.append(rs.getDouble("price"));
				r.append(",");
				r.append(getStr1(rs, "level"));
				r.append(",");
				r.append(getStr1(rs, "serialnum"));
				r.append(",");
				r.append(getTim(rs, "arrivalT"));
				r.append(",");
				r.append(getTim(rs, "intime"));
				r.append(",");
				r.append(getStr1(rs,"isout"));
				r.append(",");
				r.append(getStr1(rs,"coucount"));
				r.append(",");
				r.append(getStr1(rs,"location"));
				r.append(",");
				r.append(rs.getInt("fegularcycle"));
				r.append(",");
				r.append(getStr1(rs,"scrapped"));
				r.append(",");
				r.append(getStr1(rs,"remark"));
				r.append(",");
				r.append(getStr1(rs, "tid"));
				r.append('"');
				
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
	
	public StringBuilder getXzBorrow (String min, String max) {
		StringBuilder r = new StringBuilder();
		boolean b = false;
		try {
			PreparedStatement psql = con.prepareStatement(EmWebSql.GetXzBorrow.toString());
			psql.setLong(1, Long.parseLong(min));
			psql.setLong(2, Long.parseLong(max));
			ResultSet rs = psql.executeQuery();
			while (rs.next()) {
				if (!b) {
					b = true;
					r.append(b);
					r.append(",\"dat\":[");
				}	
				// TODO: 2017/8/16
		//		r.append(""");
				r.append('"');
				r.append(getStr1(rs, "recordid"));
				r.append(",");
				r.append(getStr1(rs, "deviceid"));
				r.append(",");
				r.append(getTim(rs, "receiveT"));
				r.append(",");
				r.append(getStr1(rs,"receiveU"));
				r.append(",");
				r.append(getStr1(rs,"receiveP"));
				r.append(",");
				r.append(getStr1(rs,"out_reason"));
				r.append(",");
				r.append(getTim(rs,"completeT"));

				r.append('"');
				
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

	public StringBuilder getXgBorrow (String min, String max) {
		StringBuilder r = new StringBuilder();
		boolean b = false;
		try {
			PreparedStatement psql = con.prepareStatement(EmWebSql.GetXgBorrow.toString());
			psql.setLong(1, Long.parseLong(min));
			psql.setLong(2, Long.parseLong(min));
			psql.setLong(3, Long.parseLong(max));
			ResultSet rs = psql.executeQuery();
			while (rs.next()) {
				if (!b) {
					b = true;
					r.append(b);
					r.append(",\"dat\":[");
				}	
				// TODO: 2017/8/16
		//		r.append(""");
				r.append('"');
				r.append(getStr1(rs, "deviceid"));
				r.append(":");
				r.append(getStr1(rs,"recordid"));
				r.append(":");
				r.append("completeT=");	
				r.append(getTim(rs, "completeT"));
				r.append('"');
				
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

	
	public StringBuilder getXgTable (String min, String max) {
		StringBuilder r = new StringBuilder();
		boolean b = false;
		try {
			PreparedStatement psql = con.prepareStatement(EmWebSql.GetXgTable.toString());
			psql.setLong(1, Long.parseLong(min));
			psql.setLong(2, Long.parseLong(min));
			psql.setLong(3, Long.parseLong(max));
			ResultSet rs = psql.executeQuery();
			while (rs.next()) {
				if (!b) {
					b = true;
					r.append(b);
					r.append(",\"dat\":[");
				}	
				// TODO: 2017/8/16
		//		r.append(""");
				r.append('"');

				r.append(getStr1(rs,"deviceid"));
				r.append(":");
				r.append("intime=");	
				r.append(getTim(rs, "intime"));
				r.append(",");
				r.append("isout=");
				r.append(getStr1(rs, "isout"));
				r.append(",");
				r.append("coucount=");
				r.append(getStr1(rs,"coucount"));
				r.append(",");
				r.append("scrapped=");
				r.append(getStr1(rs,"scrapped"));
				r.append('"');
				
				
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
}
