LZR 的 安卓项目：
===================================================================

计划：
-------------------------------------------------------------------

读写小工具
web端RFID模拟器

*******************************************************************


开发明细：
-------------------------------------------------------------------

##### 2018-8-20 （ 添加GPL许可 ）：

##### 2018-3-8（ 注明位标解析修改方法 ）：
	XC2002 : 注明位标解析版本的修改方法

##### 2018-3-1（ 更新文档 ）：
	XC2002 : 更新说明书内容

##### 2018-1-26（ 数据存储限制 ）：
	XC2002 : （V0.2.10）
		加换肤页面
		数据库加存储限制
		写测试日志

##### 2018-1-26（ 铁路手持机设计说明书 ）：
	XC2002 : 设计说明书 （V0.2.9）

##### 2018-1-18（ 手动校时 ）：
	XC2002 : 手动校时 （V0.2.8）

##### 2018-1-10（ 标签容错 ）：
	XC2002 : 标签容错 （V0.2.6）

##### 2018-1-3（ 仓库首页加LOGO ）：
	Airhub : 仓库首页加LOGO （V0.2.3）

##### 2017-12-26（ 铁路标签更新用户手册 ）：
	XC2002 : 更新用户手册 （V0.2.6）

##### 2017-12-26（ 铁路标签更新用户手册 ）：
	XC2002 : 更新用户手册 （V0.2.5）

##### 2017-12-21（ 仓库出库改用分类统计数目 ）：
	Airhub : 仓库出库改用分类统计数目 （V0.2.2）

##### 2017-12-21（ 铁路标签位标解析修改 ）：
	XC2002 : 修正位标解析算法（V0.2.4）
	Airhub : 仓库加刷新按钮（V0.2.1）

##### 2017-12-9（ 铁路标签位标测试 ）：
	XC2002 :（V0.2.4）
		位标标签解析测试
		新增清空数据页
		数据按不同型号分别存储

##### 2017-12-5（ 铁路标签禁用旋屏 ）：
	XC2002 :（V0.2.3）
		禁用旋屏
		生成三个型号的APK

##### 2017-12-4（ 铁路标签用户手册 ）：
	XC2002 : 铁路标签用户手册

##### 2017-11-24（ 仓库更换界面 ）：
	Airhub : 仓库更换界面	（V0.2.0）

##### 2017-11-24（ 铁路标签加声音 ）：
	XC2002 : 加声音、修正序号	（V0.2.2）

##### 2017-11-16（ 铁路标签新UI ）：
	XC2002 : 根据新UI改界面	（V0.2.1）

##### 2017-11-13（ 仓库出库SQL修改 ）：
	Airhub : 仓库出库改为关联从表信息	（V0.1.6）

##### 2017-11-7（ 仓库添加测试服务 ）：
	Airhub : 添加测试服务
	Airhub : 发布新版	（V0.1.5）
	BedMg : 标签增加洗涤次数信息，明细表分日期保存	（V0.1.10）
	XC2002 : 新版重做，替换原 XC2910	（V0.2.0）

##### 2017-9-19（ 仓库BUG修正 ）：
	Airhub : 修改SQL语句BUG	（V0.1.4）

##### 2017-9-19（ 仓库脱机同步 ）：
	Airhub : 实现了数据库同步	（V0.1.3）
	Airhub : 脱机扫码

##### 2017-9-19（ 卧具盘点优化 ）：
	BedMg : 卧具盘点加入日期查询	（V0.1.8）

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
