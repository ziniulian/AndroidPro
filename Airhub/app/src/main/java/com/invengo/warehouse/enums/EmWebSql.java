package com.invengo.warehouse.enums;

/**
 * 服务器数据库查询
 * Created by LZR on 2017/8/16.
 */

public enum EmWebSql {
	// 登录
	GetPwd("select password from sys_user where login_name = ?"),	// 获取密码

	// 基本查询
	GetDevInfo("select * from t_inventory_table where deviceid = ?"),	// 获取设备的详细信息
	GetBorrowByDev("select * from t_warehouse_borrow where deviceid = ?"),	// 获取设备的领用记录

	// 出库
	GetAllOut("select * from t_out_warehouse_master"),	// 获取所有出库表
	GetAllBorrow("select * from t_warehouse_borrow"),	// 获取所有领用记录
	CrtOut("insert into t_out_warehouse_master (recordid) values(?)"),	// 新增一张出库表
	CrtBorrow("insert into t_warehouse_borrow (recordid, deviceid, receiveT) values(?, ?, NOW())"),	// 新增领用记录
	GetAllUdOut("select recordid from t_out_warehouse_master where isNull(outstatesP)"),	// 获取所有未完成的出库表
	GetOutInfo("select * from t_out_warehouse_master where recordid = ?"),	// 获取出库表的详细信息
	GetDevsByOut("select deviceid from t_warehouse_borrow where recordid = ?"),	// 获取出库表相关的设备信息
	DelOneBorrow("delete from t_warehouse_borrow where recordid = ? and deviceid = ?"),	// 删除一笔领用记录
	DelOut("delete from t_out_warehouse_master where recordid = ?"),	// 删除一张出库表
	DelBorrowInOut("delete from t_warehouse_borrow where recordid = ?"),	// 删除一张出库表下的所有领用记录
	OutOk("update t_out_warehouse_master set outstatesP = ?, outstatesT = NOW() where recordid = ?"),	// 出库完成

	// 盘点
	GetAllPlan("select * from t_inventory_plan"),	// 获取所有盘点计划
	GetAllRecord("select * from t_inventory_record"),	// 获取所有盘点记录
	CrtPlan("insert into t_inventory_plan (recordid, planP, planT, complete) values(?, ?, NOW(), 0)"),	// 新增盘点计划
	CrtRecord("insert into t_inventory_record (t_inventory_plan_id, deviceid, recordT, recordP, isexist) values(?, ?, NOW(), ?, ?)"),	// 新增盘点记录		？：预计盘点项目中没有的(2)；X：库存表中都找不到的未知标签(0)；V：与预计盘点项目对应上的(1)。另：已保存的盘点记录不能再次保存。
	GetAllUdPlan("select recordid from t_inventory_plan where complete = 0"),	// 获取所有未完成的盘点计划
	GetPlanInfo("select * from t_inventory_plan where recordid = ?"),	// 获取盘点计划的详细信息
	GetDevsByPlan("select deviceid, isexist from t_inventory_record where t_inventory_plan_id = ?"),	// 获取盘点计划相关的设备信息
	DelOneRecord("delete from t_inventory_record where t_inventory_plan_id = ? and deviceid = ?"),	// 删除一笔盘点记录
	DelPlan("delete from t_inventory_plan where recordid = ?"),	// 删除一个盘点计划
	DelRecordInPlan("delete from t_inventory_record where t_inventory_plan_id = ?"),	// 删除一个盘点计划下的所有盘点记录
	ClearRecord("update t_inventory_record set isexist = 3 where t_inventory_plan_id = ? and deviceid = ? and isexist = 1"),	// 清除一个已盘点的记录
	MarkRecord("update t_inventory_record set isexist = 1, recordP = ?, recordT = NOW() where t_inventory_plan_id = ? and deviceid = ? and isexist = 3"),	// 标记一个未盘点的记录
	PlanOk("update t_inventory_plan set complete = 1 where recordid = ?");	// 盘点完成

	private final String sql;
	EmWebSql(String s) {
		sql = s;
	}
	@Override
	public String toString() {
		return sql;
	}
}
