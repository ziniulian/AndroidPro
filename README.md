LZR 的 安卓项目：
===================================================================

计划：
-------------------------------------------------------------------

- TestRfidApp ： 写码功能未完成

*******************************************************************


开发明细：
-------------------------------------------------------------------

##### 2017-9-11（ 完善初级框架 ）：
	BedMg : 卧具盘点意见修改	（V0.1.5）
	BedMg : 初级框架，在扫描时要设置 Q 值
	BedMg : 初级框架，新增返回是否正在扫描的状态函数

##### 2017-9-7（ 完成卧具盘点初版 ）：
	BedMg : 初版完成	（V0.1.1）

##### 2017-9-5（ 完善初级框架 ）：
	BedMg : 初级框架新增写标签功能	（V0.0.2）

##### 2017-9-1（ RFID初级框架 ）：
	BedMg : 新增卧具盘点项目	（V0.0.1）

##### 2017-8-31（ 入库 ）：
	Airhub : 完成入库功能	（V0.1.1）
	Airhub : 完成简易的物品详情页

##### 2017-8-25（ 仓库初版完成 ）：
	Airhub : 完成盘点功能	（V0.1.0）
	Airhub : 取消测试页

##### 2017-8-24（ 出库 ）：
	Airhub : 完成最简单的出库扫码功能	（V0.0.2）

##### 2017-8-21（ 取消服务接口 ）：
	Airhub : 取消服务接口

##### 2017-8-21（ 服务接口 ）：
	Airhub : 添加测试用服务接口

##### 2017-8-12（ 支持WEB端 ）：
	Airhub : 改用网页形式的界面 （V0.0.1）
	Airhub.读写器基类 ：
		新增 获取推送方式 方法
		新增 清空标签集 方法
		修改 获取标签集 方法
	Airhub.字符转换 :
		新增 将二进制字节数组转换成ASCII字符数据 方法
		新增 修剪乱码 方法
	Airhub.X型标签读写器 :
		修改 断开设备 方法 : 保证断开前停止扫描
		修改 扫描 方法 : 保证扫描前清空标签集

##### 2017-8-11（ 仓库改包名 ）：
	Airhub ：
		改包名
		读写器基类 添加 二进制选项
		X型读写器 添加 bank选项

##### 2017-8-10（ 仓库项目初测试 ）：
	Airhub ：X新机型的 RFID API 测试完成

##### 2017-8-4（ 开发文档 ）：
	XC2910 ： （V11）
		补充开发文档
		添加截屏功能

##### 2017-8-3（ 重定义配置文件 ）：
	XC2910 ： （V10）
		配置文件可设置软件名称
		锁屏后不清除扫码数据
		取消校时功能

##### 2017-8-2（ 屏蔽HOME键 ）：
	XC2910 ： 屏蔽HOME键 （V9）

##### 2017-7-28（ 手持机重定义XML ）：
	XC2910 ： 记录时间差，重读XML配置（V8）

##### 2017-7-20（ 手持机GPS测试 ）：
	XC2910 ： 手持机GPS测试，很失败。（V7）

##### 2017-7-14（ 手持机数据存入内存卡 ）：
	XC2910 ： 数据存入内存卡、自启动、全屏、不能退出（V7）
	XC2910Srv ： 增大缓存区以读取大文件信息（V1）

##### 2017-7-4（ 改用SQLite存储数据 ）：
	XC2910 ： 改用SQLite存储数据（V6）

##### 2017-6-30（ 手持机多种标签的处理 ）：
	XC2910 ： 多类型标签的处理（V5）

##### 2017-6-28（ 手持机数据上传功能 ）：
	XC2910 ： 网络连接测试成功（V4）
	JavaLib ： UDP、TCP 的连接测试
	XC2910Srv ： 手持机测试数据上传用的简单服务（V0）

##### 2017-6-23（ 手持机读机车标签正式版 ）：
	TestFhcView ： 删除该项目
	XC2910 ： 原 TestFhcView 项目的正式版本，包名已全更改，未完成（V3）

##### 2017-4-13（ 手持机读机车标签 ）：
	TestFhcView ： 读取机车标签的小应用。（XC2002-FHC型便携式标签读出器）

##### 2017-4-13（ JAVA 小项目库 ）：
	JavaLib ： Java 小项目暂存处。

##### 2017-3-27（ 蓝牙串口通信测试 ）：
	TestBluetooth ： 蓝牙串口通信测试

##### 2016-12-12（ 界面改用网页 ）：
	Test002：访问网络服务的小练习

##### 2016-12-7（ 访问网络服务的小练习 ）：
	Test001：访问网络服务的小练习

*******************************************************************
