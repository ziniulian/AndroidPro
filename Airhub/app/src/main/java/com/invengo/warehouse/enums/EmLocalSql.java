package com.invengo.warehouse.enums;

/**
 * 本地数据库查询
 * Created by LZR on 2017/8/14.
 */

public enum EmLocalSql {
	// 建表
	CrtTable(	// 创建库存表
			"create table t_inventory_table(" +	// 表名
			"deviceid varchar(40) primary key not null," +	// 设备编码
			"devicename varchar(400) not null," +	// 名称
			"brand varchar(400) not null," +	// 品牌
			"model varchar(400) not null," +	// 型号
			"unit varchar(40)," +	// 单位
			"price decimal(15,4)," +	// 采购价值
			"level varchar(400)," +	// 零备件级别。设备；模块(板卡)；元件
			"serialnum varchar(400) not null," +	// 设备序列号
			"arrivalT datetime," +	// 到货时间。第一次入库时生成，再不做修秘诀
			"intime datetime not null," +	// 最后一次和库的时间
			"isout char(1)," +	// 是否出库。0：未出库，默认值；1：已出库
			"Coucount integer," +	// 出库次数。出一次库累加一次
			"location varchar(400)," +	// 存储库位
			"fegularcycle integer," +	// 定检周期。整数，以月为单位的周期值
			"Scrapped Char(1)," +	// 报废标志。1-已报废
			"remark varchar(600))"),	// 备注
	CrtPlan(	// 创建盘点计划
			"create table t_ inventory_plan(" +	// 表名
			"recordid varchar(40) primary key not null," +	// 申请编号
			"planP varchar(60)," +	// 计划人
			"planT datetime," +	// 计划时间
			"complete char(1)," +	// 完成标志。0：未完成；1：已完成
			"remark varchar(600))"),	// 备注
	CrtRecord(	// 创建盘点记录
			"create table t_ inventory_record(" +	// 表名
			"recordid varchar(40) primary key not null, " +	// 盘点计划编号
			"deviceid varchar(40) primary key not null, " +	// 设备编码
			"recordT datetime, " +	// 盘点时间
			"recordP varchar(60), " +	// 盘点人
			"isexist char(1)"),	// 盘点状态。0：默认值；1：实物和数据库一致；2：数据库中没有而有实物；3：数据库中有而无实物
	CrtBorrow(	// 创建领用记录
			"create table t_warehouse_ borrow(" +	// 表名
			"deviceid varchar(40) primary key not null, " +	// 设备编码
			"receiveT datetime, " +	// 领用时间
			"receiveU varchar(400), " +	// 单位名称
			"receiveP varchar(60), " +	// 领用人
			"out_reason varchar(20), " +	// 领用原因
			"completeT datetime)"),	// 结束日期
//	CrtMaintain(""),	// 创建维修记录

	// 查询
//	GetInfo(),	// 获取设备基本信息
//	GetBorrow(),	// 获取领用记录
//	GetRecord(),	// 获取盘点记录
//	GetPlanNoCom(),	// 获取未完成的盘点计划
//	GetPlanInfo(),	// 获取详细的盘点信息

	// 数据库名
	DbNam("warehouseDB");

	private final String sql;
	EmLocalSql(String s) {
		sql = s;
	}
	@Override
	public String toString() {
		return sql;
	}
}
