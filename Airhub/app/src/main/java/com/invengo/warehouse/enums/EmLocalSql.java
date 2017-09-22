package com.invengo.warehouse.enums;

/**
 * 本地数据库查询
 * Created by LZR on 2017/8/14.
 */

public enum EmLocalSql {
	// 建表
	CrtTable(	// 创建库存表
			"create table t_inventory_table(" +	// 表名
			"deviceid varchar(255) primary key not null," +	// 设备编码
			"devicename varchar(255) not null," +	// 名称
			"brand varchar(255) not null," +	// 品牌
			"model varchar(255) not null," +	// 型号
			"unit varchar(255)," +	// 单位
			"price varchar(255)," +	// 采购价值
			"level varchar(255)," +	// 零备件级别。设备；模块(板卡)；元件
			"serialnum varchar(255) not null," +	// 设备序列号
			"arrivalT datetime," +	// 到货时间。第一次入库时生成，再不做修秘诀
			"intime datetime," +	// 最后一次和库的时间
			"isout varchar(255)," +	// 是否出库。0：未出库，默认值；1：已出库
			"coucount varchar(255)," +	// 出库次数。出一次库累加一次
			"location varchar(255)," +	// 存储库位
			"fegularcycle integer," +	// 定检周期。整数，以月为单位的周期值
			"scrapped Char(255)," +	// 报废标志。1-已报废
			"remark varchar(255)," +	// 备注
			"tid varchar(255))"),	// TID
	CrtPlan(	// 创建盘点计划
			"create table t_ inventory_plan(" +	// 表名
			"recordid varchar(40) primary key not null," +	// 申请编号
			"planP varchar(60)," +	// 计划人
			"planT datetime," +	// 计划时间
			"complete char(1)," +	// 完成标志。0：未完成；1：已完成
			"remark varchar(600))"),	// 备注
	CrtRecord(	// 创建盘点记录
			"create table t_ inventory_record(" +	// 表名
			"recordid varchar(40) not null, " +	// 盘点计划编号
			"deviceid varchar(40) not null, " +	// 设备编码
			"recordT datetime, " +	// 盘点时间
			"recordP varchar(60), " +	// 盘点人
			"isexist char(1)"),	// 盘点状态。0：默认值；1：实物和数据库一致；2：数据库中没有而有实物；3：数据库中有而无实物
	CrtBorrow(	// 创建领用记录
			"create table t_warehouse_borrow(" +	// 表名
			"recordid varchar(64) not null, " +	// 出库表单号
			"deviceid varchar(255) not null, " +	// 设备编码
			"receiveT datetime, " +	// 领用时间
			"receiveU varchar(400), " +	// 单位名称
			"receiveP varchar(255), " +	// 领用人
			"out_reason varchar(255), " +	// 领用原因
			"completeT datetime)"),	// 结束日期
	CrtCatch(	// 创建缓存表
			"create table catch(" +	// 表名
					"k varchar(40) primary key not null, " +	// 键
					"v varchar(100))"),	// 值
//	CrtMaintain(""),	// 创建维修记录

	// 查询
	GetDevInfo("select * from t_inventory_table where deviceid = '<0>'"),	// 获取设备的详细信息
	GetBorrowByDev("select * from t_warehouse_borrow where deviceid = '<0>'"),	// 获取设备的领用记录
	GetV("select v from catch where k = '<0>'"),	// 获取缓存键值对
//	GetRecord(),	// 获取盘点记录
//	GetPlanNoCom(),	// 获取未完成的盘点计划
//	GetPlanInfo(),	// 获取详细的盘点信息

	// 操作
	AddV("insert into catch(k,v) values('<0>', '<1>')"),	// 添加键值对
	SetV("update catch set v = '<1>' where k = '<0>'"),	// 修改键值对
	AddDevs("insert into t_inventory_table(deviceid,devicename,brand,model,unit,price,level,serialnum,arrivalT,intime,isout,coucount,location,fegularcycle,scrapped,remark,tid) values(<0>)"),	// 添加库存记录
	AddBorrows("insert into t_warehouse_borrow(recordid,deviceid,receiveT,receiveU,receiveP,out_reason,completeT) values(<0>)"),	// 添加领用记录
	SetDevs("update t_inventory_table set <1> where deviceid = '<0>'"),	// 修改库存记录
	SetBorrows("update t_inventory_table set <2> where deviceid = '<0>' and recordid = '<1>'"),	// 修改领用记录
//	DelDevs(""),	// 清空库存记录
//	DelBorrows(""),	// 清空领用记录

	// 数据库名
	sdDir("Invengo/WareHouse/"),
	DbNam("warehouse");

	private final String sql;
	EmLocalSql(String s) {
		sql = s;
	}
	@Override
	public String toString() {
		return sql;
	}
}
