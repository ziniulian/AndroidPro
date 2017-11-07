package lzr.test.airhub.enums;

/**
 * 数据库查询语句
 * Created by LZR on 2017/8/16.
 */

public enum EmWebSql {
	// sqlite 建表
	// CrtUser("create table user(id integer primary key autoincrement, nam text, pwd text)"),
	// CrtTabl("create table t_inventory_table(id integer primary key autoincrement, deviceid text, devicename text, brand text, model text, unit text, price text, level text, serialnum text, arrivalT numeric, intime numeric, isout text, coucount text, location text, fegularcycle text, scrapped text, remark text, tid text)"),
	// CrtPan("create table t_inventory_plan(id integer primary key autoincrement, recordid text, planP text, planT numeric, complete text, remark text)"),
	// CrtRecord("create table t_inventory_record(id integer primary key autoincrement, t_inventory_plan_id text, deviceid text, recordt numeric, recordp text, isexist text)"),
	// CrtOutm("create table t_out_warehouse_master(id integer primary key autoincrement, recordid text, out_reason text, reasonYN text, reasonP text, reasonT numeric, reasonC text, maintainYN text, maintainP text, maintainT numeric, maintainC text, exteriorP text, exteriorT text, exteriorC text, outtestP text, outtestT numeric, outtestC text, exteriorYN text, AexteriorC text, packageYN text, packageC text, annexYN text, tid text, annexC text, materialYN text, materialC text, outstatesP text, outstatesT numeric, receiveU text, receiveP text, receiveT numeric, receiveC text, remark text)"),
	// CrtBorrow("create table t_warehouse_borrow(id integer primary key autoincrement, recordid text, deviceid text, receiveT numeric, receiveU text, receiveP text, out_reason text, completeT numeric)"),
	// CrtInm("create table t_in_warehouse_master(id integer primary key autoincrement, recordid text, supplier text, purchase text, pur_reason text, pur_other text, classification text, storage text, status text, featuresYN text, featuresP text, featuresT numeric, featuresC text, exteriorYN text, exteriorP text, exteriorT numeric, exteriorC text, materialYN text, materialP text, materialT numeric, materialC text, manager text, arrivalT numeric, registerP text, intime numeric, remark text, inmark text, report text, tid text)"),
	// CrtIns("create table t_in_warehouse_slave(id integer primary key autoincrement, t_master_recordid text, deviceid text, masterid text, intime numeric, devicename text, brand text, model text, specification text, unit text, price text, location text, serialnum text, arrivalT numeric, fegularcycle text, remarks text, rsystem text)"),

	// insert into t_inventory_table(deviceid, devicename, brand, model, unit, price, level, serialnum, arrivalT, intime, isout, coucount, location, fegularcycle) values('test0001', '某物品', '某品牌', '某型号', '个', 50.0, '设备', 'xsxxxxxxxxxxxx555555111111', 1508725414638, 1508725414638, 0, 0, 'A区', 3)
	// insert into t_warehouse_borrow(recordid, deviceid, receiveT, receiveU, receiveP, out_reason, completeT) values('out002', 'DH0001', 1508701414638, '某单位', '某人', '借用', 1508712414638)
	

	// 登录
	Signin("select * from user where nam = ? and pwd = ?"),	// 简单登录
	GetPwd("select password from sys_user where login_name = ?"),	// 获取密码

	// 基本查询
	GetDevInfo("select * from t_inventory_table where deviceid = ?"),	// 获取设备的详细信息
	GetBorrowByDev("select * from t_warehouse_borrow where deviceid = ? order by receiveT desc"),	// 获取设备的领用记录


	// 盘点
	GetDevsByPlan("select r.deviceid, r.isexist, t.devicename, t.model, t.serialnum from t_inventory_record as r, t_inventory_table as t where r.t_inventory_plan_id = ? and t.deviceid = r.deviceid"),	// 获取盘点计划相关的设备信息
	MarkRecord("update t_inventory_record set isexist = 1, recordP = ?, recordT = strftime('%s000','now') where t_inventory_plan_id = ? and deviceid = ? and isexist = 3"),	// 标记一个未盘点的记录
	CrtPlan("insert into t_inventory_plan (recordid, planP, planT, complete) values(?, ?, strftime('%s000','now'), 0)"),	// 新增盘点计划 ，保存多条数据
	GetAllUdPlan("select recordid from t_inventory_plan where complete = 0"),	// 获取所有未完成的盘点计划
	PlanOk("update t_inventory_plan set complete = 1 where recordid = ?"),	// 盘点完成
	GetPlanInfo("select * from t_inventory_plan where recordid = ?"),	// 获取盘点计划的详细信息
	DelPlan("delete from t_inventory_plan where recordid = ?"),	// 删除一个盘点计划
	DelOneRecord("delete from t_inventory_record where t_inventory_plan_id = ? and deviceid = ?"),	// 删除一笔盘点记录
	CrtRecord("insert into t_inventory_record (t_inventory_plan_id, deviceid, recordT, recordP, isexist) values(?, ?, strftime('%s000','now'), ?, ?)"),	// 新增盘点记录		？：预计盘点项目中没有的(2)；X：库存表中都找不到的未知标签(0)；V：与预计盘点项目对应上的(1)。另：已保存的盘点记录不能再次保存。
	DelRecordInPlan("delete from t_inventory_record where t_inventory_plan_id = ?"),	// 删除一个盘点计划下的所有盘点记录
	ClearRecord("update t_inventory_record set isexist = 3 where t_inventory_plan_id = ? and deviceid = ? and isexist = 1"),	// 清除一个已盘点的记录


	// 入库
	GetDevsBySlave("select deviceid, devicename, model, serialnum from t_in_warehouse_slave where t_master_recordid = ?"),//获取入库表相关设备信息
	CrtSlave("insert into t_in_warehouse_slave (t_master_recordid,deviceid,intime)values(?,?,strftime('%s000','now'))"),		//新增申请表从表
	DelOneSlave("delete from t_in_warehouse_slave where t_master_recordid = ? and deviceid = ?"),	// 删除一笔入库记录
	GetAllUdMaster("select recordid from t_in_warehouse_master where  inmark = 0 or inmark ='' or inmark is null"),	//获取所有未确认的入库表
	MasterOk("update t_in_warehouse_master set manager= ?, inmark =1, intime = strftime('%s000','now') where recordid = ? "), //入库完成

	CrtMaster("insert into t_in_warehouse_master(recordid) values(?)"), 		//新增入库表
	DelMaster("delete from t_in_warehouse_master where recordid= ?"),		//删除入库表信息
	DelSlave("delete from t_in_warehouse_slave where t_master_recordid = ?"),	//删除入库表下所有设备信息


	// 出库
	GetDevsByOut("select b.deviceid, t.devicename, t.model, t.serialnum from t_warehouse_borrow as b, t_inventory_table as t where b.recordid = ? and t.deviceid = b.deviceid"),	// 获取出库表相关的设备信息
	GetAllUdOut("select recordid from t_out_warehouse_master where outstatesP = '' or outstatesP is null"),	// 获取所有未完成的出库表
	OutOk("update t_out_warehouse_master set outstatesP = ?, outstatesT = strftime('%s000','now') where recordid = ?"),	// 出库完成
	CrtBorrow("insert into t_warehouse_borrow (recordid, deviceid, receiveT) values(?, ?, strftime('%s000','now'))"),	// 新增领用记录  单条数据，保存多条数据
	DelOneBorrow("delete from t_warehouse_borrow where recordid = ? and deviceid = ?"),	// 删除一笔领用记录

	CrtOut("insert into t_out_warehouse_master (recordid) values(?)"),	// 新增一张出库表
	DelOut("delete from t_out_warehouse_master where recordid = ?"),	// 删除一张出库表
	DelBorrowInOut("delete from t_warehouse_borrow where recordid = ?"),	// 删除一张出库表下的所有领用记录
	GetOutInfo("select * from t_out_warehouse_master where recordid = ?"),	// 获取出库表的详细信息


	// 同步
	GetXzTable("select * from t_inventory_table where arrivalT > ? and arrivalT <= ?"),
	GetXgTable("select * from t_inventory_table where arrivalT <= ? and intime > ? and intime <= ?"),
	GetXzBorrow("select * from t_warehouse_borrow where receiveT  > ? and receiveT  <= ?"),
	GetXgBorrow("select * from t_warehouse_borrow where receiveT <= ? and completeT > ? and completeT  <= ?"),








/* ------------------- 以上SQL已测试可用（sqlite语法与MySQL略有差异），以下SQL未测试 ---------------------------------- */




	// 同步
//	GetXzTable("select * from t_inventory_table where arrivalT >FROM_UNIXTIME (?)  && arrivalT <=FROM_UNIXTIME (?)"),

	// 出库
	GetAllOut("select * from t_out_warehouse_master"),	// 获取所有出库表
	GetAllBorrow("select * from t_warehouse_borrow"),	// 获取所有领用记录
//	CrtBorrow("insert into t_out_warehouse_slave (recordid, deviceid) values(?, ?"),
//	CrtBorrow("insert into t_out_warehouse_master (recordid, deviceid, receiveT) values(?, ?, NOW())"),

	// 盘点
	GetAllPlan("select * from t_inventory_plan"),	// 获取所有盘点计划
	GetAllRecord("select * from t_inventory_record"),	// 获取所有盘点记录

	//库存
	GetAllTable("select * from t_inventory_table where intime is not NULL"),//获取库存
	//时间比较
	
	//入库
	GetAllMaster("select * from t_in_warehouse_master"),	 	//获取所有入库表
	GetAllSlave("select * from t_in_warehouse_slave"), 	//获取入库从表
	CrtDSlave("insert into t_in_warehouse_slave (t_master_recordid,deviceid,intime)values(?,?,NOW())"),//多条记录
	GetAllUnMaster("select recordid from t_in_warehouse_master where  inmark = 1 "),//获取所有已确认的入库表
	
	//设备编码
	GetAllFacility("select * from t_warehouse_facility");//获取所有编码信息

	
	
	private final String sql;
	EmWebSql(String s) {
		sql = s;
	}
	@Override
	public String toString() {
		return sql;
	}
}
