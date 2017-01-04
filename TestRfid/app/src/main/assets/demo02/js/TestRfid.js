(function (root, factory) {
	if (typeof exports === "object") {
		module.exports = factory();
	} else if (typeof define === "function" && define.amd) {
		define([], factory);
	} else {
		root.HelloWorld = factory;
	}
}(this, function () {
var LZR = { load: function(){}, loadAnnex: function(){} };
// 单件对象集合
LZR.singletons = {
	nodejsTools:{}
};	/*as:Object*/

// 获得一个ajax对象
LZR.getAjax = function ()/*as:Object*/ {
	var xmlHttp = null;
	try{
		xmlHttp = new XMLHttpRequest();
	} catch (MSIEx) {
		var activeX = [ "MSXML2.XMLHTTP.3.0", "MSXML2.XMLHTTP", "Microsoft.XMLHTTP" ];
		for (var i=0; i < activeX.length; i++) {
			try {
				xmlHttp = new ActiveXObject( activeX[i] );
			} catch (e) {}
		}
	}
	return xmlHttp;
};
LZR.getAjax.lzrClass_ = LZR;

// 父类构造器
LZR.initSuper = function (self/*as:Object*/, obj/*as:Object*/) {
	var s;
	if (obj && obj.lzrGeneralization_) {
		s = obj.lzrGeneralization_.prototype.super_;
	} else {
		s = self.super_;
	}

	for (var i=0; i<s.length; i++) {
		s[i].call(self, {lzrGeneralization_: s[i]});
	}
};
LZR.initSuper.lzrClass_ = LZR;

// 构造属性
LZR.setObj = function (obj/*as:Object*/, pro/*as:Object*/) {
	for (var s in pro) {
		var t = obj[s];
		if (t !== undefined) {
			var value = pro[s];
			switch (this.getClassName (t)) {
				case "LZR.Base.Val":
				case "LZR.Base.Val.Ctrl":
					switch (this.getClassName(value)) {
						case "LZR.Base.Val":
						case "LZR.Base.Val.Ctrl":
							obj[s] = value;
							break;
						default:
							// 调用值控制器赋值
							t.set (value, false);
							break;
					}
					break;
				default:
					// 普通赋值
					obj[s] = value;
					break;
			}
		}
	}
};
LZR.setObj.lzrClass_ = LZR;

// 获取一个单件对象
LZR.getSingleton = function (cls/*as:fun*/, obj/*as:Object*/, nodejsClassName/*as:string*/)/*as:Object*/ {
	var o;
	if (nodejsClassName) {
		o = LZR.singletons.nodejsTools[nodejsClassName];
		if (!o) {
			o = require(nodejsClassName);
			LZR.singletons.nodejsTools[nodejsClassName] = o;
		}
	} else {
		o = LZR.singletons[cls.prototype.className_];
		if (!o) {
			o = new cls(obj);
			LZR.singletons[o.className_] = o;
		}
	}
	return o;
};
LZR.getSingleton.lzrClass_ = LZR;

// 复制一个对象
LZR.clone = function (src/*as:Object*/, tag/*as:Object*/, objDep/*as:boolean*/, aryDep/*as:boolean*/)/*as:Object*/ {
	var s = this.getClassName(src);
	switch ( s ) {
		case "number":
		case "string":
		case "boolean":
		case "function":
		case "null":
		case "undefined":
			tag = src;
			break;
		case "Date":
			tag = new Date(src.valueOf());
			break;
		case "Array":
			// 普通数组克隆
			if (tag === null || tag === undefined) {
				tag = [];
			}
			for (s in src) {
				if (aryDep) {
					tag[s] = this.clone (src[s], tag[s], objDep, aryDep);
				} else {
					tag[s] = src[s];
				}
			}
			break;
		default:
			if (s.indexOf("LZR.") === 0 && src !== src.constructor.prototype) {
				// new 对象克隆
				if (src.clone) {
					// 有特殊克隆方法的对象克隆
					tag = src.clone(tag);
				} else {
					var p = src.constructor.prototype;
					var obj = {};
					for (s in src) {
						if (p[s] === undefined) {
							if (tag) {
								// 深度克隆
								if (tag[s]) {
									// 不需要深度克隆的特定属性
									obj[s] = tag[s];
								} else {
									obj[s] = this.clone(src[s], true);
								}
							} else {
								obj[s] = src[s];
							}
						}
					}
					tag = new src.constructor (obj);
				}
			} else {
				// 普通对象克隆
				if (tag === null || tag === undefined) {
					tag = {};
				}
				for (s in src) {
					if (objDep) {
						tag[s] = this.clone (src[s], tag[s], objDep, aryDep);
					} else {
						tag[s] = src[s];
					}
				}
			}
			break;
	}
	return tag;
};
LZR.clone.lzrClass_ = LZR;

// 获取类名
LZR.getClassName = function (obj/*as:Object*/)/*as:string*/ {
	if (null === obj)  return "null";

	var type = typeof obj;
	if (type != "object")  return type;

	// 自定义类属性
	type = obj.className_;
	if (type) {
		// if (type.indexOf("LZR.") === 0) {
			return type;
		// }
	}

	// Dom类型
	var c = Object.prototype.toString.apply ( obj );
	c = c.substring( 8, c.length - 1 );

	// 其它类型
	if ( c == "Object" ) {
		var con = obj.constructor;
		if (con == Object) {
			return c;
		}

		if (obj.prototype && "classname" in obj.prototype.constructor && typeof obj.prototype.constructor.classname == "string") {
			return con.prototype.classname;
		}
	}

	return c;
};
LZR.getClassName.lzrClass_ = LZR;

// 删除一个对象的属性
LZR.del = function (obj/*as:Object*/, proName/*as:string*/) {
	delete obj[proName];
};
LZR.del.lzrClass_ = LZR;

// 获取nodejs的模块路径
LZR.getNodejsModelPath = function (path/*as:string*/, fileNam/*as:string*/)/*as:string*/ {
	var p = require.resolve(path + fileNam);
	var i = p.indexOf(fileNam);
	if (i > 0) {
		i--;
		return (p.substr(0, i));
	} else if (i === 0) {
		return p;
	} else {
		return undefined;
	}
};
LZR.getNodejsModelPath.lzrClass_ = LZR;


/*************************************************
作者：子牛连
类名：Pro
说明：项目
创建日期：27-七月-2016 12:30:03
版本号：1.0
*************************************************/

LZR.load([
	"LZR"
], "LZR.Pro");
LZR.Pro = function (obj) {
	if (obj && obj.lzrGeneralization_) {
		obj.lzrGeneralization_.prototype.init_.call(this);
	} else {
		this.init_(obj);
	}
};
LZR.Pro.prototype.className_ = "LZR.Pro";
LZR.Pro.prototype.version_ = "1.0";

LZR.load(null, "LZR.Pro");

// 构造器
LZR.Pro.prototype.init_ = function (obj/*as:Object*/) {
	if (obj) {
		LZR.setObj (this, obj);
		this.hdObj_(obj);
	}
};
LZR.Pro.prototype.init_.lzrClass_ = LZR.Pro;

// 对构造参数的特殊处理
LZR.Pro.prototype.hdObj_ = function (obj/*as:Object*/) {
	
};
LZR.Pro.prototype.hdObj_.lzrClass_ = LZR.Pro;

/*************************************************
作者：子牛连
类名：Rfid
说明：RFID
创建日期：21-12月-2016 11:45:44
版本号：1.0
*************************************************/

LZR.load([
	"LZR.Pro"
], "LZR.Pro.Rfid");
LZR.Pro.Rfid = function (obj) {
	if (obj && obj.lzrGeneralization_) {
		obj.lzrGeneralization_.prototype.init_.call(this);
	} else {
		this.init_(obj);
	}
};
LZR.Pro.Rfid.prototype.className_ = "LZR.Pro.Rfid";
LZR.Pro.Rfid.prototype.version_ = "1.0";

LZR.load(null, "LZR.Pro.Rfid");

// 对构造参数的特殊处理
LZR.Pro.Rfid.prototype.hdObj_ = function (obj/*as:Object*/) {
	
};
LZR.Pro.Rfid.prototype.hdObj_.lzrClass_ = LZR.Pro.Rfid;

// 构造器
LZR.Pro.Rfid.prototype.init_ = function (obj/*as:Object*/) {
	if (obj) {
		LZR.setObj (this, obj);
		this.hdObj_(obj);
	}
};
LZR.Pro.Rfid.prototype.init_.lzrClass_ = LZR.Pro.Rfid;

/*************************************************
作者：子牛连
类名：Base
说明：基础
创建日期：27-七月-2016 12:30:02
版本号：1.0
*************************************************/

LZR.load([
	"LZR"
], "LZR.Base");
LZR.Base = function (obj) {
	if (obj && obj.lzrGeneralization_) {
		obj.lzrGeneralization_.prototype.init_.call(this);
	} else {
		this.init_(obj);
	}
};
LZR.Base.prototype.className_ = "LZR.Base";
LZR.Base.prototype.version_ = "1.0";

LZR.load(null, "LZR.Base");

// 构造器
LZR.Base.prototype.init_ = function (obj/*as:Object*/) {
	if (obj) {
		LZR.setObj (this, obj);
		this.hdObj_(obj);
	}
};
LZR.Base.prototype.init_.lzrClass_ = LZR.Base;

// 对构造参数的特殊处理
LZR.Base.prototype.hdObj_ = function (obj/*as:Object*/) {
	
};
LZR.Base.prototype.hdObj_.lzrClass_ = LZR.Base;

/*************************************************
作者：子牛连
类名：InfEvt
说明：事件集接口
创建日期：27-七月-2016 12:30:03
版本号：1.1
*************************************************/

LZR.load([
	"LZR.Base"
], "LZR.Base.InfEvt");
LZR.Base.InfEvt = function (obj) {
	// 事件集
	this.evt = {};	/*as:Object*/
};
LZR.Base.InfEvt.prototype.className_ = "LZR.Base.InfEvt";
LZR.Base.InfEvt.prototype.version_ = "1.1";

LZR.load(null, "LZR.Base.InfEvt");

// 设置事件调用对象
LZR.Base.InfEvt.prototype.setEventObj = function (obj/*as:Object*/) {
	this.setEventObjRecursion(this.evt, obj);
};
LZR.Base.InfEvt.prototype.setEventObj.lzrClass_ = LZR.Base.InfEvt;

// 递归设置调用对象
LZR.Base.InfEvt.prototype.setEventObjRecursion = function (o/*as:Object*/, obj/*as:Object*/) {
	for (var s in o) {
		if (o[s].className_ === "LZR.Base.CallBacks") {
			o[s].obj = obj;
		} else if (typeof(o[s]) === "object") {
			this.setEventObjRecursion(o[s], obj);
		}
	}
};
LZR.Base.InfEvt.prototype.setEventObjRecursion.lzrClass_ = LZR.Base.InfEvt;

/*************************************************
作者：子牛连
类名：Str
说明：字符串
创建日期：27-七月-2016 12:30:04
版本号：1.0
*************************************************/

LZR.load([
	"LZR.Base"
], "LZR.Base.Str");
LZR.Base.Str = function (obj) {
	if (obj && obj.lzrGeneralization_) {
		obj.lzrGeneralization_.prototype.init_.call(this);
	} else {
		this.init_(obj);
	}
};
LZR.Base.Str.prototype.className_ = "LZR.Base.Str";
LZR.Base.Str.prototype.version_ = "1.0";

LZR.load(null, "LZR.Base.Str");

// 构造器
LZR.Base.Str.prototype.init_ = function (obj/*as:Object*/) {
	if (obj) {
		LZR.setObj (this, obj);
		this.hdObj_(obj);
	}
};
LZR.Base.Str.prototype.init_.lzrClass_ = LZR.Base.Str;

// 对构造参数的特殊处理
LZR.Base.Str.prototype.hdObj_ = function (obj/*as:Object*/) {
	
};
LZR.Base.Str.prototype.hdObj_.lzrClass_ = LZR.Base.Str;

// 转固定宽度的字符
LZR.Base.Str.prototype.format = function (s/*as:string*/, width/*as:int*/, subs/*as:string*/)/*as:string*/ {
	var r = s;
	var n = s.length;
	if (width > n) {
		n = width - n;
		for (var i = 0; i < n; i++) {
			r = subs + r;
		}
	}
	return r;
};
LZR.Base.Str.prototype.format.lzrClass_ = LZR.Base.Str;

// 判断字符串是否以start字串开头
LZR.Base.Str.prototype.startWith = function (s/*as:string*/, start/*as:string*/)/*as:boolean*/ {
	var reg = new RegExp("^"+start);
	return reg.test(s);
};
LZR.Base.Str.prototype.startWith.lzrClass_ = LZR.Base.Str;

// 判断字符串是否以end字串结束
LZR.Base.Str.prototype.endWith = function (s/*as:string*/, end/*as:string*/)/*as:boolean*/ {
	var reg = new RegExp(end+"$");
	return reg.test(s);
};
LZR.Base.Str.prototype.endWith.lzrClass_ = LZR.Base.Str;

// 将一个 Unicode 码转换为 UTF-8 码
LZR.Base.Str.prototype.unicode2Utf8 = function (ch/*as:int*/)/*as:Array*/ {
	var h=0;	// 占用的字节数
	var v=0;	// 临时变量
	var mak = 0x3F;	// 最高位可用值域
	var r;

	if (ch < 0x80) {
		r = ch;
	} else {
		r = 0x80;
		while (ch > mak) {
			r = r >> 1 | 0x80;
			mak >>= 1;
			if (h) {
				v = (ch & 0x3F | 0x80) << 8 | v;
			} else {
				v = ch & 0x3F | 0x80;
			}
			ch >>= 6;
			h ++;
		}
		r = (r | ch) << (8 * h) | v;
	}
	// return r;
	h++;
	return [r, h];
};
LZR.Base.Str.prototype.unicode2Utf8.lzrClass_ = LZR.Base.Str;

// 将一个 UTF-8 码转换为 Unicode 码
LZR.Base.Str.prototype.utf82Unicode = function (ch/*as:int*/)/*as:int*/ {
	var r, h, v, mak;

	if (ch < 0x80) {
		r = ch;
	} else {
		h = 0;
		r = 0;
		mak = 0x3F;
		v = ch & 0xFF;
		while (ch > 0 && v < 0xC0) {
			if (h) {
				r = (v & 0x3F) << 6 | r;
			} else {
				r = v & 0x3F;
			}
			h++;
			ch >>= 8;
			mak >>= 1;
			v = ch & 0xFF;
		}
		r = (ch & mak) << (6 * h) | r;
	}

	return r;
};
LZR.Base.Str.prototype.utf82Unicode.lzrClass_ = LZR.Base.Str;

// 通过 UTF-8 的第一个字节判断其所占用的位数
LZR.Base.Str.prototype.getSizeByUtf8Head = function (head/*as:int*/)/*as:int*/ {
	if (head < 0x80) {
		return 1;
	} else if ((head & 0xE0) === 0xC0 ) {
		return 2;
	} else if ((head & 0xF0) === 0xE0) {
		return 3;
	} else if ((head & 0xF8) === 0xF0) {
		return 4;
	} else if ((head & 0xFC) === 0xF8) {
		return 5;
	} else if ((head & 0xFE) === 0xFC) {
		return 6;
	} else if (head === 0xFE) {
		return 7;
	} else {
		return 0;
	}
};
LZR.Base.Str.prototype.getSizeByUtf8Head.lzrClass_ = LZR.Base.Str;

// 将文字 转换为 UTF-8 16进制字串
LZR.Base.Str.prototype.toUtf8Str = function (str/*as:string*/, max/*as:int*/)/*as:string*/ {
	var i, v;
	var h = 0;
	var r = "";
	var len = str.length;
	for (i = 0; i < len; i++) {
		v = this.unicode2Utf8(str.charCodeAt(i));
		if (max) {
			h += v[1];
			if (max < h) {
				// 截断字符串
				h -= v[1];
				break;
			}
		}
		r += v[0].toString(16);
	}
	if (max) {
		// 补位
		for (i = h; i < max; i++) {
			r += "00";
		}
	}
	return r;
};
LZR.Base.Str.prototype.toUtf8Str.lzrClass_ = LZR.Base.Str;

// 将UTF-8 16进制字串 转换为 文字
LZR.Base.Str.prototype.passUtf8Str = function (u8str/*as:string*/)/*as:string*/ {
	var v, s, h, j;
	var i = 0;
	var r = [];
	var len = u8str.length;
	while (i < len) {
		s = u8str[i] + u8str[i+1];
		i += 2;

		h = this.getSizeByUtf8Head (parseInt(s, 16));
		if (h) {
			for (j = 1; j < h; j++) {
				if (u8str[i]) {
					s += u8str[i];
				} else {
					s += "00";
				}

				if (u8str[i+1]) {
					s += u8str[i+1];
				} else {
					s += "00";
				}

				i += 2;
			}
			s = this.utf82Unicode(parseInt(s, 16));
		}

		r.push(s);
	}

	return String.fromCharCode.apply(String, r);
};
LZR.Base.Str.prototype.passUtf8Str.lzrClass_ = LZR.Base.Str;


/*************************************************
作者：子牛连
类名：Util
说明：工具包
创建日期：27-七月-2016 12:30:04
版本号：1.0
*************************************************/

LZR.load([
	"LZR"
], "LZR.Util");
LZR.Util = function (obj) {
	if (obj && obj.lzrGeneralization_) {
		obj.lzrGeneralization_.prototype.init_.call(this);
	} else {
		this.init_(obj);
	}
};
LZR.Util.prototype.className_ = "LZR.Util";
LZR.Util.prototype.version_ = "1.0";

LZR.load(null, "LZR.Util");

// 构造器
LZR.Util.prototype.init_ = function (obj/*as:Object*/) {
	if (obj) {
		LZR.setObj (this, obj);
		this.hdObj_(obj);
	}
};
LZR.Util.prototype.init_.lzrClass_ = LZR.Util;

// 对构造参数的特殊处理
LZR.Util.prototype.hdObj_ = function (obj/*as:Object*/) {
	
};
LZR.Util.prototype.hdObj_.lzrClass_ = LZR.Util;

// 闭包调用
LZR.Util.prototype.bind = function (self/*as:Object*/, fun/*as:fun*/, args/*as:___*/)/*as:fun*/ {
	var arg = Array.prototype.slice.call(arguments, 2);
	return function () {
		var i, args = [];
		for ( i=0; i<arg.length; i++ ) {
			args.push ( arg[i] );
		}
		for ( i=0; i<arguments.length; i++ ) {
			args.push ( arguments[i] );
		}
		return fun.apply ( self, args );
	};
};
LZR.Util.prototype.bind.lzrClass_ = LZR.Util;

// 判断一个对象的属性是否存在
LZR.Util.prototype.exist = function (obj/*as:Object*/, pro/*as:string*/)/*as:Object*/ {
	var ps = pro.split(".");
	for (var i = 0; i<ps.length; i++) {
		if (undefined === obj || null === obj) {
			return undefined;
		}
		obj = obj[ps[i]];
	}
	return obj;
};
LZR.Util.prototype.exist.lzrClass_ = LZR.Util;

// 休眠函数
LZR.Util.prototype.sleep = function (numberMillis/*as:int*/) {
	var now = new Date();
	var exitTime = now.getTime() + numberMillis;
	while (true) {
		now = new Date();
		if (now.getTime() > exitTime) {
			return;
		}
	}
};
LZR.Util.prototype.sleep.lzrClass_ = LZR.Util;

// 调用父类方法
LZR.Util.prototype.supCall = function (self/*as:Object*/, idx/*as:int*/, funam/*as:string*/, args/*as:___*/)/*as:Object*/ {
	// 参数
	var arg = Array.prototype.slice.call(arguments, 3);

	// 确认父类
	var s = arguments.callee.caller.lzrClass_.prototype.super_;
// console.log (self.className_ + " ---> " + s[idx].prototype.className_);

	// 父类函数
	var f = s[idx].prototype[funam];

	// 执行父类函数
	return f.apply ( self, arg );
};
LZR.Util.prototype.supCall.lzrClass_ = LZR.Util;


/*************************************************
作者：子牛连
类名：CallBacks
说明：回调函数集合
创建日期：27-七月-2016 12:30:02
版本号：1.0
*************************************************/

LZR.load([
	"LZR.Base",
	"LZR.Base.Str",
	"LZR.Base.CallBacks.CallBack",
	"LZR.Util"
], "LZR.Base.CallBacks");
LZR.Base.CallBacks = function (obj) {
	// 是否触发事件
	this.enableEvent = true;	/*as:boolean*/

	// 事件自动恢复
	this.autoEvent = true;	/*as:boolean*/

	// 调用对象
	this.obj = this;	/*as:Object*/

	// 回调函数个数
	this.count = 0;	/*as:int*/

	// 自身回调
	this.exe = null;	/*as:fun*/

	// 唯一ID
	this.id = 0;	/*as:int*/

	// 回调函数集合
	this.funs/*m*/ = {};	/*as:LZR.Base.CallBacks.CallBack*/

	// 通用工具
	this.utLzr/*m*/ = LZR.getSingleton(LZR.Util);	/*as:LZR.Util*/

	if (obj && obj.lzrGeneralization_) {
		obj.lzrGeneralization_.prototype.init_.call(this);
	} else {
		this.init_(obj);
	}
};
LZR.Base.CallBacks.prototype.className_ = "LZR.Base.CallBacks";
LZR.Base.CallBacks.prototype.version_ = "1.0";

LZR.load(null, "LZR.Base.CallBacks");

// 添加回调函数
LZR.Base.CallBacks.prototype.add = function (fun/*as:fun*/, name/*as:LZR.Base.Str*/, self/*as:boolean*/)/*as:string*/ {
	if (name === undefined || name === null) {
		name = this.id;
	}
	this.id ++;
	if (this.funs[name] === undefined) {
		this.count ++;
	}
	var o = {
		name: name,
		fun: fun
	};
	if (self === true) {
		o.selfInfo = true;
	}
	this.funs[name] = new LZR.Base.CallBacks.CallBack (o);
	return name;
};
LZR.Base.CallBacks.prototype.add.lzrClass_ = LZR.Base.CallBacks;

// 删除回调函数
LZR.Base.CallBacks.prototype.del = function (name/*as:LZR.Base.Str*/)/*as:LZR.Base.CallBacks.CallBack*/ {
	var r = this.funs[name];
	if (r !== undefined) {
		LZR.del(this.funs, name);
		this.count --;
	}
	return r;
};
LZR.Base.CallBacks.prototype.del.lzrClass_ = LZR.Base.CallBacks;

// 执行回调函数
LZR.Base.CallBacks.prototype.execute = function ()/*as:boolean*/ {
	var b = true;	// 回调函数正常执行则返回 true，否则返回 false
	if (this.enableEvent) {
		for (var s in this.funs) {
			switch (s) {
				case "length":
					break;
				default:
					if (this.funs[s].enableEvent) {
						var arg;
						if (this.funs[s].selfInfo) {
							arg = Array.prototype.slice.call ( arguments );
							arg.push({
								id: "selfInfo",
								root: this,
								parent: this.funs,
								self: this.funs[s],
								nam: this.funs[s].name
							});
						} else {
							arg = arguments;
						}
						if ( (this.funs[s].fun.apply ( this.obj, arg )) === false ) {
							b = false;
						}
					} else {
						this.funs[s].enableEvent = this.funs[s].autoEvent;
					}
					break;
			}
		}
	} else {
		this.enableEvent = this.autoEvent;
	}
	return b;
};
LZR.Base.CallBacks.prototype.execute.lzrClass_ = LZR.Base.CallBacks;

// 构造器
LZR.Base.CallBacks.prototype.init_ = function (obj/*as:Object*/) {
	this.exe = this.utLzr.bind (this, this.execute);
	if (obj) {
		LZR.setObj (this, obj);
		this.hdObj_(obj);
	}
};
LZR.Base.CallBacks.prototype.init_.lzrClass_ = LZR.Base.CallBacks;

// 对构造参数的特殊处理
LZR.Base.CallBacks.prototype.hdObj_ = function (obj/*as:Object*/) {
	
};
LZR.Base.CallBacks.prototype.hdObj_.lzrClass_ = LZR.Base.CallBacks;

/*************************************************
作者：子牛连
类名：CallBack
说明：回调函数
创建日期：27-七月-2016 12:30:02
版本号：1.0
*************************************************/

LZR.load([
	"LZR.Base.CallBacks"
], "LZR.Base.CallBacks.CallBack");
LZR.Base.CallBacks.CallBack = function (obj) {
	// 名字
	this.name = "";	/*as:string*/

	// 是否触发事件
	this.enableEvent = true;	/*as:boolean*/

	// 事件自动恢复
	this.autoEvent = true;	/*as:boolean*/

	// 回调参数中是否添加自我的相关信息
	this.selfInfo = false;	/*as:boolean*/

	// 函数
	this.fun = null;	/*as:fun*/

	if (obj && obj.lzrGeneralization_) {
		obj.lzrGeneralization_.prototype.init_.call(this);
	} else {
		this.init_(obj);
	}
};
LZR.Base.CallBacks.CallBack.prototype.className_ = "LZR.Base.CallBacks.CallBack";
LZR.Base.CallBacks.CallBack.prototype.version_ = "1.0";

LZR.load(null, "LZR.Base.CallBacks.CallBack");

// 构造器
LZR.Base.CallBacks.CallBack.prototype.init_ = function (obj/*as:Object*/) {
	if (obj) {
		LZR.setObj (this, obj);
		this.hdObj_(obj);
	}
};
LZR.Base.CallBacks.CallBack.prototype.init_.lzrClass_ = LZR.Base.CallBacks.CallBack;

// 对构造参数的特殊处理
LZR.Base.CallBacks.CallBack.prototype.hdObj_ = function (obj/*as:Object*/) {
	
};
LZR.Base.CallBacks.CallBack.prototype.hdObj_.lzrClass_ = LZR.Base.CallBacks.CallBack;

/*************************************************
作者：子牛连
类名：At911n
说明：手持机设备
创建日期：21-12月-2016 17:24:04
版本号：1.0
*************************************************/

LZR.load([
	"LZR.Pro.Rfid",
	"LZR.Base.InfEvt",
	"LZR.Base.CallBacks"
], "LZR.Pro.Rfid.At911n");
LZR.Pro.Rfid.At911n = function (obj) /*interfaces:LZR.Base.InfEvt*/ {
	LZR.Base.InfEvt.call(this);

	// 安卓对象
	this.adrObj = null;	/*as:Object*/

	// 状态
	this.stat = 0;	/*as:int*/

	// 正在操作的存储块名
	this.bankNam = "";	/*as:string*/

	// 准备写入的存储信息
	this.wrMsg = "";	/*as:string*/

	// 通用工具
	this.utLzr/*m*/ = LZR.getSingleton(LZR.Util);	/*as:LZR.Util*/

	// 扫描事件
	this.evt.scan/*m*/ = new LZR.Base.CallBacks();	/*as:LZR.Base.CallBacks*/

	// 读码事件
	this.evt.read/*m*/ = new LZR.Base.CallBacks();	/*as:LZR.Base.CallBacks*/

	// 写码事件
	this.evt.write/*m*/ = new LZR.Base.CallBacks();	/*as:LZR.Base.CallBacks*/

	if (obj && obj.lzrGeneralization_) {
		obj.lzrGeneralization_.prototype.init_.call(this);
	} else {
		this.init_(obj);
	}
};
LZR.Pro.Rfid.At911n.prototype = LZR.clone (LZR.Base.InfEvt.prototype, LZR.Pro.Rfid.At911n.prototype);
LZR.Pro.Rfid.At911n.prototype.className_ = "LZR.Pro.Rfid.At911n";
LZR.Pro.Rfid.At911n.prototype.version_ = "1.0";

LZR.load(null, "LZR.Pro.Rfid.At911n");

// 构造器
LZR.Pro.Rfid.At911n.prototype.init_ = function (obj/*as:Object*/) {
	if (obj) {
		LZR.setObj (this, obj);
		this.hdObj_(obj);
	}
};
LZR.Pro.Rfid.At911n.prototype.init_.lzrClass_ = LZR.Pro.Rfid.At911n;

// 对构造参数的特殊处理
LZR.Pro.Rfid.At911n.prototype.hdObj_ = function (obj/*as:Object*/) {
	
};
LZR.Pro.Rfid.At911n.prototype.hdObj_.lzrClass_ = LZR.Pro.Rfid.At911n;

// 扫描
LZR.Pro.Rfid.At911n.prototype.scanning = function () {
	if (this.adrObj) {
		this.adrObj.scanning();

		// 循环读数
		if (!this.exeScanning) {
			this.exeScanning = this.utLzr.bind(this, this.getScanning);
		}
		setTimeout(this.exeScanning, 100);
	} else if (this.stat === 1) {
		// 模拟循环扫描操作
		if (!this.exeScanning) {
			this.exeScanning = this.utLzr.bind(this, this.scanning);
		}
		this.onScanning ("48656c6c6f20576f726c6421");
		setTimeout(this.exeScanning, 200);
	}
};
LZR.Pro.Rfid.At911n.prototype.scanning.lzrClass_ = LZR.Pro.Rfid.At911n;

// 获取扫描信息
LZR.Pro.Rfid.At911n.prototype.getScanning = function () {
	var s, i, n;
	if (this.adrObj) {
		s = this.adrObj.getScanning().split(",");
		n = s.length;
		for (i=1; i<n; i++) {
			this.hdScanning(s[i]);
		}
	}
	if (this.stat === 1) {
		setTimeout(this.exeScanning, 200);
	}
};
LZR.Pro.Rfid.At911n.prototype.scanning.lzrClass_ = LZR.Pro.Rfid.At911n;

// 停止
LZR.Pro.Rfid.At911n.prototype.stop = function () {
	if (this.adrObj) {
		this.adrObj.stop();
	}
};
LZR.Pro.Rfid.At911n.prototype.stop.lzrClass_ = LZR.Pro.Rfid.At911n;

// 写码
LZR.Pro.Rfid.At911n.prototype.write = function (bankNam/*as:string*/, offset/*as:int*/, msg/*as:string*/) {
	this.bankNam = bankNam;
	this.wrMsg = msg;

	if (this.adrObj) {
		this.adrObj.write(bankNam, offset, msg);
	} else if (this.stat === 2) {
		// 模拟写操作
		if (bankNam !== "ecp") {
			this.onWrite("48656c6c6f20576f726c6421", bankNam, msg);
		}
	}
};
LZR.Pro.Rfid.At911n.prototype.write.lzrClass_ = LZR.Pro.Rfid.At911n;

// 读码
LZR.Pro.Rfid.At911n.prototype.read = function (bankNam/*as:string*/, offset/*as:int*/, len/*as:int*/) {
	this.bankNam = bankNam;

	if (this.adrObj) {
		this.adrObj.read(bankNam, offset, len);
	} else if (this.stat === 3) {
		// 模拟读操作
		var msg;
		switch (bankNam) {
			case "ecp":
				msg = "48656c6c6f20576f726c6421";
				break;
			case "tid":
				msg = "5449447878782e2e2e000000000000000000000000000000";
				break;
			case "usr":
				msg = "54657374000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
				break;
			case "tid":
				msg = "5907752830020000";
				break;
			default:
				msg = "00000000";
				break;
		}
		this.onRead("48656c6c6f20576f726c6421", bankNam, msg);
	}
};
LZR.Pro.Rfid.At911n.prototype.read.lzrClass_ = LZR.Pro.Rfid.At911n;

// 处理扫描回调
LZR.Pro.Rfid.At911n.prototype.hdScanning = function (ecpId/*as:string*/) {
	this.onScanning (ecpId);
};
LZR.Pro.Rfid.At911n.prototype.hdScanning.lzrClass_ = LZR.Pro.Rfid.At911n;

// 处理读写码回调
LZR.Pro.Rfid.At911n.prototype.hdWr = function (ecpId/*as:string*/, msg/*as:string*/) {
	switch (this.stat) {
		case 2:
			this.onWrite(ecpId, this.bankNam, this.wrMsg);
			break;
		case 3:
			this.onRead(ecpId, this.bankNam, msg);
			break;
	}
	this.stat = 0;
};
LZR.Pro.Rfid.At911n.prototype.hdWr.lzrClass_ = LZR.Pro.Rfid.At911n;

// 触发扫描事件
LZR.Pro.Rfid.At911n.prototype.onScanning = function (ecpId/*as:string*/) {
	this.evt.scan.execute(ecpId);
};
LZR.Pro.Rfid.At911n.prototype.onScanning.lzrClass_ = LZR.Pro.Rfid.At911n;

// 触发写码事件
LZR.Pro.Rfid.At911n.prototype.onWrite = function (ecpId/*as:string*/, bankNam/*as:string*/, msg/*as:string*/) {
	this.evt.write.execute(ecpId, bankNam, msg);
};
LZR.Pro.Rfid.At911n.prototype.onWrite.lzrClass_ = LZR.Pro.Rfid.At911n;

// 触发读码事件
LZR.Pro.Rfid.At911n.prototype.onRead = function (ecpId/*as:string*/, bankNam/*as:string*/, msg/*as:string*/) {
	this.evt.read.execute(ecpId, bankNam, msg);
};
LZR.Pro.Rfid.At911n.prototype.onRead.lzrClass_ = LZR.Pro.Rfid.At911n;


/*************************************************
作者：子牛连
类名：BankNam
说明：存储块名称
创建日期：21-12月-2016 10:10:45
版本号：1.0
*************************************************/

LZR.load([
	"LZR.Pro.Rfid"
], "LZR.Pro.Rfid.BankNam");
LZR.Pro.Rfid.BankNam = function (obj) {
	// 名称
	this.nam = "";	/*as:string*/

	if (obj && obj.lzrGeneralization_) {
		obj.lzrGeneralization_.prototype.init_.call(this);
	} else {
		this.init_(obj);
	}
};
LZR.Pro.Rfid.BankNam.prototype.className_ = "LZR.Pro.Rfid.BankNam";
LZR.Pro.Rfid.BankNam.prototype.version_ = "1.0";

LZR.load(null, "LZR.Pro.Rfid.BankNam");

// 构造器
LZR.Pro.Rfid.BankNam.prototype.init_ = function (obj/*as:Object*/) {
	if (obj) {
		LZR.setObj (this, obj);
		this.hdObj_(obj);
	}
};
LZR.Pro.Rfid.BankNam.prototype.init_.lzrClass_ = LZR.Pro.Rfid.BankNam;

// 对构造参数的特殊处理
LZR.Pro.Rfid.BankNam.prototype.hdObj_ = function (obj/*as:Object*/) {
	
};
LZR.Pro.Rfid.BankNam.prototype.hdObj_.lzrClass_ = LZR.Pro.Rfid.BankNam;

/*************************************************
作者：子牛连
类名：Val
说明：值
创建日期：27-七月-2016 12:30:04
版本号：1.0
*************************************************/

LZR.load([
	"LZR.Base"
], "LZR.Base.Val");
LZR.Base.Val = function (obj) {
	// 值
	this.val = null;	/*as:Object*/

	if (obj && obj.lzrGeneralization_) {
		obj.lzrGeneralization_.prototype.init_.call(this);
	} else {
		this.init_(obj);
	}
};
LZR.Base.Val.prototype.className_ = "LZR.Base.Val";
LZR.Base.Val.prototype.version_ = "1.0";

LZR.load(null, "LZR.Base.Val");

// 构造器
LZR.Base.Val.prototype.init_ = function (obj/*as:Object*/) {
	if (obj) {
		this.val = obj;
		this.hdObj_(obj);
	}
};
LZR.Base.Val.prototype.init_.lzrClass_ = LZR.Base.Val;

// 获取值
LZR.Base.Val.prototype.get = function ()/*as:Object*/ {
	return this.val;
};
LZR.Base.Val.prototype.get.lzrClass_ = LZR.Base.Val;

// 设置值
LZR.Base.Val.prototype.set = function (value/*as:Object*/)/*as:boolean*/ {
	this.val = value;
	return true;
};
LZR.Base.Val.prototype.set.lzrClass_ = LZR.Base.Val;

// 克隆
LZR.Base.Val.prototype.clone = function (dep/*as:boolean*/)/*as:Object*/ {
	var r = this.get();
	if (dep) {
		r = LZR.clone(r);
	}
	return new this.constructor (r);
};
LZR.Base.Val.prototype.clone.lzrClass_ = LZR.Base.Val;

// 对构造参数的特殊处理
LZR.Base.Val.prototype.hdObj_ = function (obj/*as:Object*/) {
	
};
LZR.Base.Val.prototype.hdObj_.lzrClass_ = LZR.Base.Val;

/*************************************************
作者：子牛连
类名：Enum
说明：通用枚举
创建日期：27-七月-2016 12:30:03
版本号：1.0
*************************************************/

LZR.load([
	"LZR.Base.Val",
	"LZR.Util"
], "LZR.Base.Val.Enum");
LZR.Base.Val.Enum = function (obj) /*bases:LZR.Base.Val*/ {
	LZR.initSuper(this, obj);

	// 枚举名
	this.key = "";	/*as:string*/

	// 通用工具
	this.utLzr/*m*/ = LZR.getSingleton(LZR.Util);	/*as:LZR.Util*/

	if (obj && obj.lzrGeneralization_) {
		obj.lzrGeneralization_.prototype.init_.call(this);
	} else {
		this.init_(obj);
	}
};
LZR.Base.Val.Enum.prototype = LZR.clone (LZR.Base.Val.prototype, LZR.Base.Val.Enum.prototype);
LZR.Base.Val.Enum.prototype.super_ = [LZR.Base.Val];
LZR.Base.Val.Enum.prototype.className_ = "LZR.Base.Val.Enum";
LZR.Base.Val.Enum.prototype.version_ = "1.0";

LZR.load(null, "LZR.Base.Val.Enum");

// 构造器
LZR.Base.Val.Enum.prototype.init_ = function (obj/*as:Object*/) {
	this.set(obj);
	if (obj) {
		// this.hdObj_(obj);
	}
};
LZR.Base.Val.Enum.prototype.init_.lzrClass_ = LZR.Base.Val.Enum;

// 对构造参数的特殊处理
LZR.Base.Val.Enum.prototype.hdObj_ = function (obj/*as:Object*/) {
	
};
LZR.Base.Val.Enum.prototype.hdObj_.lzrClass_ = LZR.Base.Val.Enum;

// 获取枚举名
LZR.Base.Val.Enum.prototype.getKey = function ()/*as:string*/ {
	return this.key;
};
LZR.Base.Val.Enum.prototype.getKey.lzrClass_ = LZR.Base.Val.Enum;

// 通过枚举值获取枚举名
LZR.Base.Val.Enum.prototype.getKeyByVal = function (value/*as:Object*/)/*as:string*/ {
	for (var s in this.constructor) {
		if (value === this.constructor[s]) {
			return s;
		}
	}
	return undefined;
};
LZR.Base.Val.Enum.prototype.getKeyByVal.lzrClass_ = LZR.Base.Val.Enum;

// 获取所有枚举集合
LZR.Base.Val.Enum.prototype.enums = function ()/*as:Object*/ {
	var ems = {};
	for (var s in this.constructor) {
		switch (s) {
			// case "getKeyByVal":
			// case "enums":
				// break;
			default:
				ems[s] = this.constructor[s];
				break;
		}
	}
	return ems;
};
LZR.Base.Val.Enum.prototype.enums.lzrClass_ = LZR.Base.Val.Enum;

// 通过枚举值获取枚举
LZR.Base.Val.Enum.prototype.getByVal = function (value/*as:Object*/)/*as:Object*/ {
	for (var s in this.constructor) {
		if (value === this.constructor[s]) {
			return this.constructor[s];
		}
	}

	if (this.constructor.emnull) {
		return this.constructor.emnull;
	} else {
		return undefined;
	}
};
LZR.Base.Val.Enum.prototype.getByVal.lzrClass_ = LZR.Base.Val.Enum;

// ---- 设置值
LZR.Base.Val.Enum.prototype.set = function (key/*as:string*/)/*as:boolean*/ {
	if (key && this.constructor[key]) {
		this.key = key;
		this.val = this.constructor[key];
		return true;
	} else if (!key && this.constructor.emnull) {
		this.key = "emnull";
		this.val = this.constructor.emnull;
		return true;
	} else {
		return false;
	}
};
LZR.Base.Val.Enum.prototype.set.lzrClass_ = LZR.Base.Val.Enum;

// ---- 克隆
LZR.Base.Val.Enum.prototype.clone = function (dep/*as:boolean*/)/*as:Object*/ {
	return new this.constructor (this.key);
};
LZR.Base.Val.Enum.prototype.clone.lzrClass_ = LZR.Base.Val.Enum;


/*************************************************
作者：子牛连
类名：EmBankNam
说明：存储块名枚举
创建日期：21-12月-2016 17:12:57
版本号：1.0
*************************************************/

LZR.load([
	"LZR.Pro.Rfid",
	"LZR.Pro.Rfid.BankNam",
	"LZR.Base.Val.Enum"
], "LZR.Pro.Rfid.EmBankNam");
LZR.Pro.Rfid.EmBankNam = function (obj) /*bases:LZR.Base.Val.Enum*/ {
	LZR.initSuper(this, obj);

	if (obj && obj.lzrGeneralization_) {
		obj.lzrGeneralization_.prototype.init_.call(this);
	} else {
		this.init_(obj);
	}
};
LZR.Pro.Rfid.EmBankNam.prototype = LZR.clone (LZR.Base.Val.Enum.prototype, LZR.Pro.Rfid.EmBankNam.prototype);
LZR.Pro.Rfid.EmBankNam.prototype.super_ = [LZR.Base.Val.Enum];
LZR.Pro.Rfid.EmBankNam.prototype.className_ = "LZR.Pro.Rfid.EmBankNam";
LZR.Pro.Rfid.EmBankNam.prototype.version_ = "1.0";

LZR.load(null, "LZR.Pro.Rfid.EmBankNam");

// 可用
LZR.Pro.Rfid.EmBankNam.usr/*m*/ = new LZR.Pro.Rfid.BankNam({nam:"User"});	/*as:LZR.Pro.Rfid.BankNam*/

// 备用
LZR.Pro.Rfid.EmBankNam.bck/*m*/ = new LZR.Pro.Rfid.BankNam({nam:"Reserved"});	/*as:LZR.Pro.Rfid.BankNam*/

// EPC
LZR.Pro.Rfid.EmBankNam.ecp/*m*/ = new LZR.Pro.Rfid.BankNam({nam:"EPC"});	/*as:LZR.Pro.Rfid.BankNam*/

// TID
LZR.Pro.Rfid.EmBankNam.tid/*m*/ = new LZR.Pro.Rfid.BankNam({nam:"TID"});	/*as:LZR.Pro.Rfid.BankNam*/

// 构造器
LZR.Pro.Rfid.EmBankNam.prototype.init_ = function (obj/*as:Object*/) {
	if (obj) {
		LZR.setObj (this, obj);
		this.hdObj_(obj);
	}
};
LZR.Pro.Rfid.EmBankNam.prototype.init_.lzrClass_ = LZR.Pro.Rfid.EmBankNam;

// 对构造参数的特殊处理
LZR.Pro.Rfid.EmBankNam.prototype.hdObj_ = function (obj/*as:Object*/) {
	
};
LZR.Pro.Rfid.EmBankNam.prototype.hdObj_.lzrClass_ = LZR.Pro.Rfid.EmBankNam;

/*************************************************
作者：子牛连
类名：Bank
说明：存储块
创建日期：22-12月-2016 8:37:13
版本号：1.0
*************************************************/

LZR.load([
	"LZR.Pro.Rfid",
	"LZR.Pro.Rfid.EmBankNam"
], "LZR.Pro.Rfid.Bank");
LZR.Pro.Rfid.Bank = function (obj) {
	// 可存储的字节总数
	this.size = 0;	/*as:int*/

	// 写入开始的字节位置
	this.ws = 0;	/*as:int*/

	// 写入结束的字节位置
	this.we = 0;	/*as:int*/

	// 读取开始的字节位置
	this.rs = 0;	/*as:int*/

	// 读取结束的字节位置
	this.re = 0;	/*as:int*/

	// 存储的内容
	this.msg = "";	/*as:string*/

	// 名称
	this.emNam/*m*/ = new LZR.Pro.Rfid.EmBankNam();	/*as:LZR.Pro.Rfid.EmBankNam*/

	if (obj && obj.lzrGeneralization_) {
		obj.lzrGeneralization_.prototype.init_.call(this);
	} else {
		this.init_(obj);
	}
};
LZR.Pro.Rfid.Bank.prototype.className_ = "LZR.Pro.Rfid.Bank";
LZR.Pro.Rfid.Bank.prototype.version_ = "1.0";

LZR.load(null, "LZR.Pro.Rfid.Bank");

// 构造器
LZR.Pro.Rfid.Bank.prototype.init_ = function (obj/*as:Object*/) {
	if (obj) {
		LZR.setObj (this, obj);
		this.hdObj_(obj);
	}
};
LZR.Pro.Rfid.Bank.prototype.init_.lzrClass_ = LZR.Pro.Rfid.Bank;

// 对构造参数的特殊处理
LZR.Pro.Rfid.Bank.prototype.hdObj_ = function (obj/*as:Object*/) {
	if (obj.hd_emNam) {
		this.emNam.set(obj.hd_emNam);
	}
};
LZR.Pro.Rfid.Bank.prototype.hdObj_.lzrClass_ = LZR.Pro.Rfid.Bank;

// 获取可写入的字节长度
LZR.Pro.Rfid.Bank.prototype.getLength = function (isWrite/*as:boolean*/)/*as:int*/ {
	if (isWrite) {
		return this.we - this.ws;
	} else {
		return this.re - this.rs;
	}
};
LZR.Pro.Rfid.Bank.prototype.getLength.lzrClass_ = LZR.Pro.Rfid.Bank;


/*************************************************
作者：子牛连
类名：TagTyp
说明：标签类型
创建日期：21-12月-2016 14:33:35
版本号：1.0
*************************************************/

LZR.load([
	"LZR.Pro.Rfid"
], "LZR.Pro.Rfid.TagTyp");
LZR.Pro.Rfid.TagTyp = function (obj) {
	// 存储区配置信息
	this.conf = null;	/*as:Object*/

	if (obj && obj.lzrGeneralization_) {
		obj.lzrGeneralization_.prototype.init_.call(this);
	} else {
		this.init_(obj);
	}
};
LZR.Pro.Rfid.TagTyp.prototype.className_ = "LZR.Pro.Rfid.TagTyp";
LZR.Pro.Rfid.TagTyp.prototype.version_ = "1.0";

LZR.load(null, "LZR.Pro.Rfid.TagTyp");

// 构造器
LZR.Pro.Rfid.TagTyp.prototype.init_ = function (obj/*as:Object*/) {
	if (obj) {
		LZR.setObj (this, obj);
		this.hdObj_(obj);
	}
};
LZR.Pro.Rfid.TagTyp.prototype.init_.lzrClass_ = LZR.Pro.Rfid.TagTyp;

// 对构造参数的特殊处理
LZR.Pro.Rfid.TagTyp.prototype.hdObj_ = function (obj/*as:Object*/) {
	
};
LZR.Pro.Rfid.TagTyp.prototype.hdObj_.lzrClass_ = LZR.Pro.Rfid.TagTyp;


/*************************************************
作者：子牛连
类名：EmTagTyp
说明：标签类型枚举
创建日期：22-12月-2016 8:39:59
版本号：1.0
*************************************************/

LZR.load([
	"LZR.Pro.Rfid",
	"LZR.Base.Val.Enum",
	"LZR.Pro.Rfid.TagTyp"
], "LZR.Pro.Rfid.EmTagTyp");
LZR.Pro.Rfid.EmTagTyp = function (obj) /*bases:LZR.Base.Val.Enum*/ {
	LZR.initSuper(this, obj);

	if (obj && obj.lzrGeneralization_) {
		obj.lzrGeneralization_.prototype.init_.call(this);
	} else {
		this.init_(obj);
	}
};
LZR.Pro.Rfid.EmTagTyp.prototype = LZR.clone (LZR.Base.Val.Enum.prototype, LZR.Pro.Rfid.EmTagTyp.prototype);
LZR.Pro.Rfid.EmTagTyp.prototype.super_ = [LZR.Base.Val.Enum];
LZR.Pro.Rfid.EmTagTyp.prototype.className_ = "LZR.Pro.Rfid.EmTagTyp";
LZR.Pro.Rfid.EmTagTyp.prototype.version_ = "1.0";

LZR.load(null, "LZR.Pro.Rfid.EmTagTyp");

// 6C
LZR.Pro.Rfid.EmTagTyp.t6c/*m*/ = new LZR.Pro.Rfid.TagTyp({
	conf: {
		ecp:[16,4,16,4,16],
		tid:[24,24,24,0,24],
		usr:[64,0,64,0,64],
		bck:[8,0,8,0,8]
	}
});	/*as:LZR.Pro.Rfid.TagTyp*/

// 构造器
LZR.Pro.Rfid.EmTagTyp.prototype.init_ = function (obj/*as:Object*/) {
	if (obj) {
		LZR.setObj (this, obj);
		this.hdObj_(obj);
	}
};
LZR.Pro.Rfid.EmTagTyp.prototype.init_.lzrClass_ = LZR.Pro.Rfid.EmTagTyp;

// 对构造参数的特殊处理
LZR.Pro.Rfid.EmTagTyp.prototype.hdObj_ = function (obj/*as:Object*/) {
	
};
LZR.Pro.Rfid.EmTagTyp.prototype.hdObj_.lzrClass_ = LZR.Pro.Rfid.EmTagTyp;

/*************************************************
作者：子牛连
类名：Tag
说明：标签
创建日期：21-12月-2016 14:35:28
版本号：1.0
*************************************************/

LZR.load([
	"LZR.Pro.Rfid",
	"LZR.Pro.Rfid.Bank",
	"LZR.Pro.Rfid.EmTagTyp"
], "LZR.Pro.Rfid.Tag");
LZR.Pro.Rfid.Tag = function (obj) {
	// 存储区
	this.banks/*m*/ = {};	/*as:LZR.Pro.Rfid.Bank*/

	// 存储类
	this.clsBank/*m*/ = (LZR.Pro.Rfid.Bank);	/*as:fun*/

	// 标签类型
	this.emTyp/*m*/ = new LZR.Pro.Rfid.EmTagTyp();	/*as:LZR.Pro.Rfid.EmTagTyp*/

	if (obj && obj.lzrGeneralization_) {
		obj.lzrGeneralization_.prototype.init_.call(this);
	} else {
		this.init_(obj);
	}
};
LZR.Pro.Rfid.Tag.prototype.className_ = "LZR.Pro.Rfid.Tag";
LZR.Pro.Rfid.Tag.prototype.version_ = "1.0";

LZR.load(null, "LZR.Pro.Rfid.Tag");

// 构造器
LZR.Pro.Rfid.Tag.prototype.init_ = function (obj/*as:Object*/) {
	if (obj) {
		LZR.setObj (this, obj);
		this.hdObj_(obj);
	}
};
LZR.Pro.Rfid.Tag.prototype.init_.lzrClass_ = LZR.Pro.Rfid.Tag;

// 对构造参数的特殊处理
LZR.Pro.Rfid.Tag.prototype.hdObj_ = function (obj/*as:Object*/) {
	if (obj.hd_emTyp) {
		this.setTyp(obj.hd_emTyp);
	}
	if (obj.hd_msg) {
		this.hdMsg(obj.hd_msg);
	}
};
LZR.Pro.Rfid.Tag.prototype.hdObj_.lzrClass_ = LZR.Pro.Rfid.Tag;

// 处理信息构造参数
LZR.Pro.Rfid.Tag.prototype.hdMsg = function (msg/*as:Object*/) {
	var s;
	for (s in msg) {
		if (this.banks[s]) {
			this.banks[s].msg = msg[s];
		}
	}
};
LZR.Pro.Rfid.Tag.prototype.hdMsg.lzrClass_ = LZR.Pro.Rfid.Tag;

// 设置标签类型
LZR.Pro.Rfid.Tag.prototype.setTyp = function (typNam/*as:string*/) {
	if (typNam !== this.emTyp.getKey() && this.emTyp.set(typNam)) {
		LZR.del(this, "banks");
		this.banks = this.crtBanks();
	}
};
LZR.Pro.Rfid.Tag.prototype.setTyp.lzrClass_ = LZR.Pro.Rfid.Tag;

// 创建存储区
LZR.Pro.Rfid.Tag.prototype.crtBanks = function ()/*as:Object*/ {
	var s, r = {};
	var o = this.emTyp.get().conf;
	for (s in o) {
		r[s] = new this.clsBank({
			hd_emNam: s,
			size: o[s][0],
			ws: o[s][1],
			we: o[s][2],
			rs: o[s][3],
			re: o[s][4]
		});
	}
	return r;
};
LZR.Pro.Rfid.Tag.prototype.crtBanks.lzrClass_ = LZR.Pro.Rfid.Tag;


/*************************************************
作者：子牛连
类名：ScanTag
说明：扫码标签
创建日期：21-12月-2016 16:41:17
版本号：1.0
*************************************************/

LZR.load([
	"LZR.Pro.Rfid.At911n",
	"LZR.Pro.Rfid.Tag"
], "LZR.Pro.Rfid.At911n.ScanTag");
LZR.Pro.Rfid.At911n.ScanTag = function (obj) /*bases:LZR.Pro.Rfid.Tag*/ {
	LZR.initSuper(this, obj);

	// ECP序号
	this.id = "";	/*as:string*/

	// 被扫描到的次数
	this.scanNum = 0;	/*as:int*/

	// 元素
	this.doeo = null;	/*as:LZR.HTML.Base.Doe*/

	// 通用工具
	this.utLzr/*m*/ = LZR.getSingleton(LZR.Util);	/*as:LZR.Util*/

	if (obj && obj.lzrGeneralization_) {
		obj.lzrGeneralization_.prototype.init_.call(this);
	} else {
		this.init_(obj);
	}
};
LZR.Pro.Rfid.At911n.ScanTag.prototype = LZR.clone (LZR.Pro.Rfid.Tag.prototype, LZR.Pro.Rfid.At911n.ScanTag.prototype);
LZR.Pro.Rfid.At911n.ScanTag.prototype.super_ = [LZR.Pro.Rfid.Tag];
LZR.Pro.Rfid.At911n.ScanTag.prototype.className_ = "LZR.Pro.Rfid.At911n.ScanTag";
LZR.Pro.Rfid.At911n.ScanTag.prototype.version_ = "1.0";

LZR.load(null, "LZR.Pro.Rfid.At911n.ScanTag");

// 构造器
LZR.Pro.Rfid.At911n.ScanTag.prototype.init_ = function (obj/*as:Object*/) {
	if (obj) {
		LZR.setObj (this, obj);
		this.hdObj_(obj);
	}
};
LZR.Pro.Rfid.At911n.ScanTag.prototype.init_.lzrClass_ = LZR.Pro.Rfid.At911n.ScanTag;

// 对构造参数的特殊处理
LZR.Pro.Rfid.At911n.ScanTag.prototype.hdObj_ = function (obj/*as:Object*/) {
	this.utLzr.supCall(this, 0, "hdObj_", obj);
};
LZR.Pro.Rfid.At911n.ScanTag.prototype.hdObj_.lzrClass_ = LZR.Pro.Rfid.At911n.ScanTag;


/*************************************************
作者：子牛连
类名：HTML
说明：
创建日期：27-七月-2016 12:30:03
版本号：1.0
*************************************************/

LZR.load([
	"LZR"
], "LZR.HTML");
LZR.HTML = function (obj) {
	if (obj && obj.lzrGeneralization_) {
		obj.lzrGeneralization_.prototype.init_.call(this);
	} else {
		this.init_(obj);
	}
};
LZR.HTML.prototype.className_ = "LZR.HTML";
LZR.HTML.prototype.version_ = "1.0";

LZR.load(null, "LZR.HTML");

// LOG容器
LZR.HTML.logger = null;	/*as:Object*/

// 构造器
LZR.HTML.prototype.init_ = function (obj/*as:Object*/) {
	if (obj) {
		LZR.setObj (this, obj);
		this.hdObj_(obj);
	}
};
LZR.HTML.prototype.init_.lzrClass_ = LZR.HTML;

// 创建LOG
LZR.HTML.createLog = function () {
	if (!this.logger) {
		this.logger = document.getElementById("LZR_LOG");
		if (!this.logger) {
			this.logger = document.createElement( "pre" );
			this.logger.id = "LZR_LOG";
			if (document.body.children.length) {
				document.body.insertBefore(this.logger, document.body.children[0]);
			} else {
				document.body.appendChild(this.logger);
			}
		}
	}
};
LZR.HTML.createLog.lzrClass_ = LZR.HTML;

// 覆盖LOG
LZR.HTML.log = function (memo/*as:string*/) {
	this.createLog();
	this.logger.innerHTML = memo;
};
LZR.HTML.log.lzrClass_ = LZR.HTML;

// 追加LOG
LZR.HTML.alog = function (memo/*as:string*/) {
	this.createLog();
	this.logger.innerHTML += memo;
	this.logger.innerHTML += "<br>";
};
LZR.HTML.alog.lzrClass_ = LZR.HTML;

// 对构造参数的特殊处理
LZR.HTML.prototype.hdObj_ = function (obj/*as:Object*/) {
	
};
LZR.HTML.prototype.hdObj_.lzrClass_ = LZR.HTML;

/*************************************************
作者：子牛连
类名：Base
说明：基础
创建日期：27-七月-2016 12:30:02
版本号：1.0
*************************************************/

LZR.load([
	"LZR.HTML"
], "LZR.HTML.Base");
LZR.HTML.Base = function (obj) {
	if (obj && obj.lzrGeneralization_) {
		obj.lzrGeneralization_.prototype.init_.call(this);
	} else {
		this.init_(obj);
	}
};
LZR.HTML.Base.prototype.className_ = "LZR.HTML.Base";
LZR.HTML.Base.prototype.version_ = "1.0";

LZR.load(null, "LZR.HTML.Base");

// 构造器
LZR.HTML.Base.prototype.init_ = function (obj/*as:Object*/) {
	if (obj) {
		LZR.setObj (this, obj);
		this.hdObj_(obj);
	}
};
LZR.HTML.Base.prototype.init_.lzrClass_ = LZR.HTML.Base;

// 对构造参数的特殊处理
LZR.HTML.Base.prototype.hdObj_ = function (obj/*as:Object*/) {
	
};
LZR.HTML.Base.prototype.hdObj_.lzrClass_ = LZR.HTML.Base;

/*************************************************
作者：子牛连
类名：Ctrl
说明：值控制器
创建日期：27-七月-2016 12:30:02
版本号：1.1
*************************************************/

LZR.load([
	"LZR.Base.Val",
	"LZR.Base.CallBacks",
	"LZR.Base.InfEvt",
	"LZR.Util"
], "LZR.Base.Val.Ctrl");
LZR.Base.Val.Ctrl = function (obj) /*bases:LZR.Base.Val*/ /*interfaces:LZR.Base.InfEvt*/ {
	LZR.initSuper(this, obj);

	LZR.Base.InfEvt.call(this);

	// 是否触发事件
	this.enableEvent = true;	/*as:boolean*/

	// 事件自动恢复
	this.autoEvent = true;	/*as:boolean*/

	// 设置值之前触发的事件
	this.evt.before/*m*/ = new LZR.Base.CallBacks();	/*as:LZR.Base.CallBacks*/

	// 值变动后触发的事件
	this.evt.change/*m*/ = new LZR.Base.CallBacks();	/*as:LZR.Base.CallBacks*/

	// 设置值后触发的事件
	this.evt.set/*m*/ = new LZR.Base.CallBacks();	/*as:LZR.Base.CallBacks*/

	// 通用工具
	this.utLzr/*m*/ = LZR.getSingleton(LZR.Util);	/*as:LZR.Util*/

	if (obj && obj.lzrGeneralization_) {
		obj.lzrGeneralization_.prototype.init_.call(this);
	} else {
		this.init_(obj);
	}
};
LZR.Base.Val.Ctrl.prototype = LZR.clone (LZR.Base.InfEvt.prototype, LZR.Base.Val.Ctrl.prototype);
LZR.Base.Val.Ctrl.prototype = LZR.clone (LZR.Base.Val.prototype, LZR.Base.Val.Ctrl.prototype);
LZR.Base.Val.Ctrl.prototype.super_ = [LZR.Base.Val];
LZR.Base.Val.Ctrl.prototype.className_ = "LZR.Base.Val.Ctrl";
LZR.Base.Val.Ctrl.prototype.version_ = "1.1";

LZR.load(null, "LZR.Base.Val.Ctrl");

// 构造器
LZR.Base.Val.Ctrl.prototype.init_ = function (obj/*as:Object*/) {
	if (obj !== undefined) {
		this.val = obj;
		this.hdObj_(obj);
	}
};
LZR.Base.Val.Ctrl.prototype.init_.lzrClass_ = LZR.Base.Val.Ctrl;

// ---- 设置值
LZR.Base.Val.Ctrl.prototype.set = function (obj/*as:Object*/, doEvent/*as:boolean*/)/*as:boolean*/ {
	var r = true;
	if (doEvent === false) {
		this.val = obj;
	} else {
		if (this.enableEvent) {
			var old = this.val;
			var tmp = {
				tmpVal: obj
			};
			if (this.beforeSet (obj, this, old, tmp)) {
				if (tmp.tmpVal !== old) {
					this.val = tmp.tmpVal;
					r = this.onChange (this.val, this, old, tmp);
					if (!r) {
						this.val = tmp.tmpVal;
					}
				}
				if (!(this.onSet (this.val, this, old, tmp))) {
					this.val = tmp.tmpVal;
					r = false;
				}
			}
		} else {
			this.enableEvent = this.autoEvent;
			this.val = obj;
		}
	}
	return r;
};
LZR.Base.Val.Ctrl.prototype.set.lzrClass_ = LZR.Base.Val.Ctrl;

// 设置值之前触发的事件
LZR.Base.Val.Ctrl.prototype.beforeSet = function (val/*as:Object*/, self/*as:Object*/, old/*as:Object*/, tmpVal/*as:Object*/)/*as:boolean*/ {
	return this.evt.before.execute (val, self, old, tmpVal);
};
LZR.Base.Val.Ctrl.prototype.beforeSet.lzrClass_ = LZR.Base.Val.Ctrl;

// 值变动后触发的事件
LZR.Base.Val.Ctrl.prototype.onChange = function (val/*as:Object*/, self/*as:Object*/, old/*as:Object*/, tmpVal/*as:Object*/)/*as:boolean*/ {
	return this.evt.change.execute (val, self, old, tmpVal);
};
LZR.Base.Val.Ctrl.prototype.onChange.lzrClass_ = LZR.Base.Val.Ctrl;

// 设置值后触发的事件
LZR.Base.Val.Ctrl.prototype.onSet = function (val/*as:Object*/, self/*as:Object*/, old/*as:Object*/, tmpVal/*as:Object*/)/*as:boolean*/ {
	return this.evt.set.execute (val, self, old, tmpVal);
};
LZR.Base.Val.Ctrl.prototype.onSet.lzrClass_ = LZR.Base.Val.Ctrl;

// 对构造参数的特殊处理
LZR.Base.Val.Ctrl.prototype.hdObj_ = function (obj/*as:Object*/) {
	
};
LZR.Base.Val.Ctrl.prototype.hdObj_.lzrClass_ = LZR.Base.Val.Ctrl;

// ---- 克隆
LZR.Base.Val.Ctrl.prototype.clone = function (dep/*as:boolean*/)/*as:Object*/ {
	var r = this.utLzr.supCall(this, 0, "clone", dep);
	r.enableEvent = this.enableEvent;
	r.autoEvent = this.autoEvent;

	// 事件克隆
	// LZR.clone(this.evt[s], true);
	for (var s in r.evt) {
		LZR.setObj (r.evt[s], this.evt[s]);
		if (dep) {
			// 深度克隆
			r.evt[s].funs = LZR.clone(this.evt[s].funs, true);
		}
	}
	r.setEventObj(r);
};
LZR.Base.Val.Ctrl.prototype.clone.lzrClass_ = LZR.Base.Val.Ctrl;

/*************************************************
作者：子牛连
类名：Data
说明：数据
创建日期：27-七月-2016 12:30:02
版本号：1.0
*************************************************/

LZR.load([
	"LZR.Base",
	"LZR.Base.Data",
	"LZR.Base.Val.Ctrl"
], "LZR.Base.Data");
LZR.Base.Data = function (obj) {
	// 视图元素
	this.view = null;	/*as:Object*/

	// 控制元素
	this.ctrl = null;	/*as:Object*/

	// 子元素个数
	this.count = 0;	/*as:int*/

	// 前一个同级元素
	this.prev = null;	/*as:Object*/

	// 后一个同级元素
	this.next = null;	/*as:Object*/

	// 第一个子元素
	this.first = null;	/*as:Object*/

	// 最后一个子元素
	this.last = null;	/*as:Object*/

	// 数据之名
	this.id/*m*/ = new LZR.Base.Val.Ctrl();	/*as:LZR.Base.Val.Ctrl*/

	// 数据之根
	this.root/*m*/ = new LZR.Base.Val.Ctrl();	/*as:LZR.Base.Val.Ctrl*/

	// 数据之父
	this.parent/*m*/ = new LZR.Base.Val.Ctrl();	/*as:LZR.Base.Val.Ctrl*/

	// 数据之子
	this.subs/*m*/ = {};	/*as:LZR.Base.Data*/

	if (obj && obj.lzrGeneralization_) {
		obj.lzrGeneralization_.prototype.init_.call(this);
	} else {
		this.init_(obj);
	}
};
LZR.Base.Data.prototype.className_ = "LZR.Base.Data";
LZR.Base.Data.prototype.version_ = "1.0";

LZR.load(null, "LZR.Base.Data");

// 添加子数据
LZR.Base.Data.prototype.add = function (sub/*as:Data*/, id/*as:string*/)/*as:boolean*/ {
	if (!id) {
		id = sub.id.get();

		// id 不正常，则不能添加
		if (!id) {
			return false;
		}
	}

	// 已存在相同 id 的子元素 且 与sub不相同时，不能添加
	var p = this.subs[id];
	if (p === undefined) {
		this.count ++;
	} else if (p !== sub) {
		return false;
	} else {
		return true;
	}

	// 删除子元素在原父类中的关系
	p = sub.parent.get();
	if (p) {
		p.del (sub.id.get());
	}

	// 维护链表关系
	if (this.last === null) {
		this.first = sub;
		this.last = sub;
	} else {
		this.last.next = sub;
		sub.prev = this.last;
		this.last = sub;
	}

	// 添加子元素
	this.subs[id] = sub;
	sub.root.set (this.root.get());	// 会触发root的change事件
	sub.id.set(id, false);
	sub.parent.set(this, false);
	return true;
};
LZR.Base.Data.prototype.add.lzrClass_ = LZR.Base.Data;

// 构造器
LZR.Base.Data.prototype.init_ = function (obj/*as:Object*/) {
	this.root.set (this, false);
	this.root.setEventObj (this);
	this.root.evt.change.add(this.changeRoot);

	this.id.setEventObj (this);
	this.id.evt.change.add(this.changeId);

	this.parent.setEventObj (this);
	this.parent.evt.change.add(this.changeParent);

	if (obj) {
		LZR.setObj (this, obj);
		this.hdObj_(obj);
	}
};
LZR.Base.Data.prototype.init_.lzrClass_ = LZR.Base.Data;

// 父类变化时触发的事件
LZR.Base.Data.prototype.changeParent = function (obj/*as:Object*/, self/*as:LZR.Base.Val.Ctrl*/, old/*as:Object*/) {
	if (obj) {
		self.set(old, false);
		return obj.add (this);
	} else if (old) {
		// 新父类不正确时，将其从原父类中移除。
		old.del(this);
	}
};
LZR.Base.Data.prototype.changeParent.lzrClass_ = LZR.Base.Data;

// 名称变化时触发的事件
LZR.Base.Data.prototype.changeId = function (obj/*as:Object*/, self/*as:LZR.Base.Val.Ctrl*/, old/*as:Object*/) {
	var p = this.parent.get();
	if (p) {
		if (!obj) {
			// id 不正确时，将其从父类中移除。
			p.del(old);
		} else {
			self.set(old, false);
			return p.add (this, obj);
		}
	}
};
LZR.Base.Data.prototype.changeId.lzrClass_ = LZR.Base.Data;

// 递归查询匹配ID的数据
LZR.Base.Data.prototype.getById = function (id/*as:string*/)/*as:Object*/ {
	if (id === this.id.get()) {
		return this;
	} else if (this.subs[id]) {
		return this.subs[id];
	} else {
		for (var s in this.subs) {
			var v = this.subs[s].getById(id);
			if (v) {
				return v;
			}
		}
	}
	return undefined;
};
LZR.Base.Data.prototype.getById.lzrClass_ = LZR.Base.Data;

// 删除子数据
LZR.Base.Data.prototype.del = function (id/*as:string*/)/*as:Object*/ {
	if (id) {	// 检查 id 是否正常

		// id 可以是数据对象
		if (id.className_) {
			id = id.id.get();
		}

		// 如果存在对应 id 的子元素，则删除它
		var sub = this.subs[id];
		if (sub !== undefined) {

			// 维护链表关系
			if (sub.prev === null) {
				if (sub.next === null) {
					this.last = null;
					this.first = null;
				} else {
					this.first = sub.next;
					sub.next.prev = null;
				}
			} else {
				if (sub.next === null) {
					sub.prev.next = null;
					this.last = sub.prev;
				} else {
					sub.prev.next = sub.next;
					sub.next.prev = sub.prev;
				}
			}
			sub.prev = null;
			sub.next = null;

			LZR.del(this.subs, id);
			this.count --;
			sub.root.set (sub);	// 会触发root的change事件
			sub.parent.set(null, false);
		}
		return sub;
	} else {
		return undefined;
	}
};
LZR.Base.Data.prototype.del.lzrClass_ = LZR.Base.Data;

// 递归创建子数组
LZR.Base.Data.prototype.initSubs = function (config/*as:Object*/) {
	for (var s in config) {
		var o = config[s];
		var c = o.cls_;
		if (!c) {
			// 获取对象的构造函数
			c = this.constructor;
		}
		this.add( new c(o), s );
	}
};
LZR.Base.Data.prototype.initSubs.lzrClass_ = LZR.Base.Data;

// 根变化时触发的事件
LZR.Base.Data.prototype.changeRoot = function (obj/*as:Object*/, self/*as:LZR.Base.Val.Ctrl*/, old/*as:Object*/) {
	for (var s in this.subs) {
		this.subs[s].root.set (obj);
	}
};
LZR.Base.Data.prototype.changeRoot.lzrClass_ = LZR.Base.Data;

// 结构输出
LZR.Base.Data.prototype.print = function (indent/*as:string*/)/*as:string*/ {
	if (!indent) {
		indent = "";
	}
	var r = indent;
	r += this.id.get();
	r += "\n";
	indent += "\t";
	for (var s in this.subs) {
		r += this.subs[s].print(indent);
	}
	return r;
};
LZR.Base.Data.prototype.print.lzrClass_ = LZR.Base.Data;

// 对构造参数的特殊处理
LZR.Base.Data.prototype.hdObj_ = function (obj/*as:Object*/) {
	var note;	/*
				参数说明： obj 里有两个不能属于该类属性的特殊字段 chd_ 和 cls_ 
				chd_: {		// 该字段用于递归创建子数据
					a: {
						id: "a",
						cls_: LZR.Base.Data		// 用于明确数据类型，若为空，则使用对象自己的构造函数。
					}
				}
			*/
	if (obj.chd_) {
		// 子数据的递归创建
		this.initSubs(obj.chd_);
	}
};
LZR.Base.Data.prototype.hdObj_.lzrClass_ = LZR.Base.Data;

// 克隆
LZR.Base.Data.prototype.clone = function (dep/*as:boolean*/)/*as:Object*/ {
	var s;
	var r = {};
	var p = this.constructor.prototype;
	for (s in this) {
		if (p[s] === undefined) {
			switch (s) {
				case "root":
				case "parent":
				case "count":
				case "subs":
				case "prev":
				case "next":
				case "first":
				case "last":
					break;
				case "id":
					r.id = this.id.get();
					break;
				default:
					this.hdClonePro (s, r, dep);
			}
		}
	}
// console.log (r);
	r = new this.constructor(r);

	for (s in this.subs) {
		r.add (this.subs[s].clone(dep));
	}
	return r;
};
LZR.Base.Data.prototype.clone.lzrClass_ = LZR.Base.Data;

// 处理克隆参数
LZR.Base.Data.prototype.hdClonePro = function (name/*as:string*/, rt/*as:Object*/, dep/*as:boolean*/)/*as:Object*/ {
	if (dep) {
		rt[name] = LZR.clone(this[name], dep);
	} else {
		rt[name] = this[name];
	}
	return rt;
};
LZR.Base.Data.prototype.hdClonePro.lzrClass_ = LZR.Base.Data;

// 删除所有子元素
LZR.Base.Data.prototype.delAll = function ()/*as:Array*/ {
	var r = [];
	for (var s in this.subs) {
		r.push(this.del(s));
	}
	return r;
};
LZR.Base.Data.prototype.delAll.lzrClass_ = LZR.Base.Data;

/*************************************************
作者：子牛连
类名：Ary
说明：数组
创建日期：27-七月-2016 12:30:02
版本号：1.0
*************************************************/

LZR.load([
	"LZR.Base"
], "LZR.Base.Ary");
LZR.Base.Ary = function (obj) {
	if (obj && obj.lzrGeneralization_) {
		obj.lzrGeneralization_.prototype.init_.call(this);
	} else {
		this.init_(obj);
	}
};
LZR.Base.Ary.prototype.className_ = "LZR.Base.Ary";
LZR.Base.Ary.prototype.version_ = "1.0";

LZR.load(null, "LZR.Base.Ary");

// 构造器
LZR.Base.Ary.prototype.init_ = function (obj/*as:Object*/) {
	if (obj) {
		LZR.setObj (this, obj);
		this.hdObj_(obj);
	}
};
LZR.Base.Ary.prototype.init_.lzrClass_ = LZR.Base.Ary;

// 对构造参数的特殊处理
LZR.Base.Ary.prototype.hdObj_ = function (obj/*as:Object*/) {
	
};
LZR.Base.Ary.prototype.hdObj_.lzrClass_ = LZR.Base.Ary;

// 获取元素在数组中的位置
LZR.Base.Ary.prototype.getId = function (ary/*as:Array*/, val/*as:Object*/)/*as:int*/ {
	for (i = 0; i < ary.length; i++)
	{
		if (val === ary[i])
		return i;
	}
	return undefined;
};
LZR.Base.Ary.prototype.getId.lzrClass_ = LZR.Base.Ary;

// 删除数组中某个位置的元素
LZR.Base.Ary.prototype.delById = function (ary/*as:Array*/, id/*as:int*/) {
	if (!isNaN(id)) {
		ary.splice(id, 1);
	}
};
LZR.Base.Ary.prototype.delById.lzrClass_ = LZR.Base.Ary;

// 删除数组中的元素
LZR.Base.Ary.prototype.delByVal = function (ary/*as:Array*/, val/*as:Object*/) {
	this.delById(ary, this.getId(ary, val));
};
LZR.Base.Ary.prototype.delByVal.lzrClass_ = LZR.Base.Ary;

/*************************************************
作者：子牛连
类名：Util
说明：工具包
创建日期：27-七月-2016 12:30:04
版本号：1.0
*************************************************/

LZR.load([
	"LZR.HTML"
], "LZR.HTML.Util");
LZR.HTML.Util = function (obj) {
	if (obj && obj.lzrGeneralization_) {
		obj.lzrGeneralization_.prototype.init_.call(this);
	} else {
		this.init_(obj);
	}
};
LZR.HTML.Util.prototype.className_ = "LZR.HTML.Util";
LZR.HTML.Util.prototype.version_ = "1.0";

LZR.load(null, "LZR.HTML.Util");

// 构造器
LZR.HTML.Util.prototype.init_ = function (obj/*as:Object*/) {
	if (obj) {
		LZR.setObj (this, obj);
		this.hdObj_(obj);
	}
};
LZR.HTML.Util.prototype.init_.lzrClass_ = LZR.HTML.Util;

// 对构造参数的特殊处理
LZR.HTML.Util.prototype.hdObj_ = function (obj/*as:Object*/) {
	
};
LZR.HTML.Util.prototype.hdObj_.lzrClass_ = LZR.HTML.Util;

/*************************************************
作者：子牛连
类名：DomPosition
说明：元素位置
创建日期：27-七月-2016 12:30:03
版本号：1.0
*************************************************/

LZR.load([
	"LZR.HTML.Util"
], "LZR.HTML.Util.DomPosition");
LZR.HTML.Util.DomPosition = function (obj) {
	// 上
	this.top = 0;	/*as:double*/

	// 左
	this.left = 0;	/*as:double*/

	// 宽
	this.width = 0;	/*as:double*/

	// 高
	this.height = 0;	/*as:double*/

	if (obj && obj.lzrGeneralization_) {
		obj.lzrGeneralization_.prototype.init_.call(this);
	} else {
		this.init_(obj);
	}
};
LZR.HTML.Util.DomPosition.prototype.className_ = "LZR.HTML.Util.DomPosition";
LZR.HTML.Util.DomPosition.prototype.version_ = "1.0";

LZR.load(null, "LZR.HTML.Util.DomPosition");

// 构造器
LZR.HTML.Util.DomPosition.prototype.init_ = function (obj/*as:Object*/) {
	if (obj) {
		LZR.setObj (this, obj);
		this.hdObj_(obj);
	}
};
LZR.HTML.Util.DomPosition.prototype.init_.lzrClass_ = LZR.HTML.Util.DomPosition;

// 对构造参数的特殊处理
LZR.HTML.Util.DomPosition.prototype.hdObj_ = function (obj/*as:Object*/) {
	
};
LZR.HTML.Util.DomPosition.prototype.hdObj_.lzrClass_ = LZR.HTML.Util.DomPosition;

/*************************************************
作者：子牛连
类名：DomTool
说明：元素工具
创建日期：27-七月-2016 12:30:03
版本号：1.0
*************************************************/

LZR.load([
	"LZR.HTML.Util",
	"LZR.HTML.Util.DomPosition"
], "LZR.HTML.Util.DomTool");
LZR.HTML.Util.DomTool = function (obj) {
	// 元素位置类
	this.clsDoePosition/*m*/ = (LZR.HTML.Util.DomPosition);	/*as:fun*/

	if (obj && obj.lzrGeneralization_) {
		obj.lzrGeneralization_.prototype.init_.call(this);
	} else {
		this.init_(obj);
	}
};
LZR.HTML.Util.DomTool.prototype.className_ = "LZR.HTML.Util.DomTool";
LZR.HTML.Util.DomTool.prototype.version_ = "1.0";

LZR.load(null, "LZR.HTML.Util.DomTool");

// 构造器
LZR.HTML.Util.DomTool.prototype.init_ = function (obj/*as:Object*/) {
	if (obj) {
		LZR.setObj (this, obj);
		this.hdObj_(obj);
	}
};
LZR.HTML.Util.DomTool.prototype.init_.lzrClass_ = LZR.HTML.Util.DomTool;

// 对构造参数的特殊处理
LZR.HTML.Util.DomTool.prototype.hdObj_ = function (obj/*as:Object*/) {
	
};
LZR.HTML.Util.DomTool.prototype.hdObj_.lzrClass_ = LZR.HTML.Util.DomTool;

// 设置DOM元素属性
LZR.HTML.Util.DomTool.prototype.setAtt = function (key/*as:string*/, val/*as:string*/, doe/*as:Object*/) {
	doe.setAttribute(key, val);
};
LZR.HTML.Util.DomTool.prototype.setAtt.lzrClass_ = LZR.HTML.Util.DomTool;

// 获取DOM元素属性
LZR.HTML.Util.DomTool.prototype.getAtt = function (key/*as:string*/, doe/*as:Object*/)/*as:string*/ {
	return doe.getAttribute(key);
};
LZR.HTML.Util.DomTool.prototype.getAtt.lzrClass_ = LZR.HTML.Util.DomTool;

// 删除DOM元素属性
LZR.HTML.Util.DomTool.prototype.delAtt = function (key/*as:string*/, doe/*as:Object*/) {
	doe.removeAttribute(key);
};
LZR.HTML.Util.DomTool.prototype.delAtt.lzrClass_ = LZR.HTML.Util.DomTool;

// 处理样式名
LZR.HTML.Util.DomTool.prototype.calcStyleNam = function (key/*as:int*/, lower/*as:boolean*/)/*as:string*/ {
	if (lower) {
		return key.replace(/[A-Z]/g, function(all, letter){
			// console.log ("all : " + all);
			// console.log ("leter : " + letter);
			return "-" + all.toLowerCase();
		});
	} else {
		return key.replace(/\-(\w)/g, function(all, letter){
			// console.log ("all : " + all);
			// console.log ("leter : " + letter);
			return letter.toUpperCase();
		});
	}
};
LZR.HTML.Util.DomTool.prototype.calcStyleNam.lzrClass_ = LZR.HTML.Util.DomTool;

// 设置DOM元素的Styley样式
LZR.HTML.Util.DomTool.prototype.setStyle = function (key/*as:string*/, val/*as:string*/, doe/*as:Object*/) {
	doe.style[this.calcStyleNam(key)] = val;
};
LZR.HTML.Util.DomTool.prototype.setStyle.lzrClass_ = LZR.HTML.Util.DomTool;

// 获取DOM元素的Styley样式
LZR.HTML.Util.DomTool.prototype.getStyle = function (key/*as:string*/, doe/*as:Object*/)/*as:string*/ {
	if ("\v" == "v") {
		//简单判断ie6~8
		key = this.calcStyleNam(key);
		if(key === "backgroundPosition"){
			//IE6~8不兼容backgroundPosition写法，识别backgroundPositionX/Y
			return doe.currentStyle.backgroundPositionX + " " + doe.currentStyle.backgroundPositionY;
		}
		return doe.currentStyle[key];
	}else{
		return this.getDocument(doe).defaultView.getComputedStyle(doe, null).getPropertyValue(this.calcStyleNam(key, true));
	}
};
LZR.HTML.Util.DomTool.prototype.getStyle.lzrClass_ = LZR.HTML.Util.DomTool;

// 计算位置
LZR.HTML.Util.DomTool.prototype.calcPosition = function (doe/*as:Object*/)/*as:Object*/ {
	var d, dm, box;
	var r = new this.clsDoePosition();

	if (doe === document) {
		r.left = 0;
		r.top = 0;
		r.width = window.innerWidth;
		r.height = window.innerHeight;
	} else {
		dm = this.getDocument(doe);
		box = doe.getBoundingClientRect();
		r.width = box.right - box.left;
		r.height = box.bottom - box.top;
		if (window.pageXOffset !== undefined) {
			d = dm.documentElement;
			r.left = box.left - d.clientLeft + window.pageXOffset;
			r.top = box.top - d.clientTop + window.pageYOffset;
		} else {
			// IE 浏览器
			d = dm.body;
			r.left = box.left - d.clientLeft + (d.scrollLeft || dm.documentElement.scrollLeft);
			r.top = box.top - d.clientTop + (d.scrollTop || dm.documentElement.scrollTop);
		}
	}

	return r;
};
LZR.HTML.Util.DomTool.prototype.calcPosition.lzrClass_ = LZR.HTML.Util.DomTool;

// 在父元素中滚动条定位
LZR.HTML.Util.DomTool.prototype.matchParent = function (vertical/*as:string*/, horizontal/*as:string*/, padding/*as:int*/, doe/*as:Object*/) {
	var p, t, c, d, s;
	if (!padding) {
		padding = 0;
	}
	p = doe.parentNode;	// 父元素

	if (p) {
		t = this.getStyle("position", p);		// 定位类型

		// 垂直调整
		if (vertical) {
			c = p.clientHeight;		// 父元素当前长度
			d = doe.offsetHeight;		// 子元素当前长度
			s = doe.offsetTop;		// 子元素相对于父元素的位置
			if (t === "static") {
				s -= p.offsetTop + p.clientTop;
			}
			switch (vertical) {
				case "T":	// 靠上
				case "t":
					p.scrollTop = s - padding;
					break;
				case "C":	// 居中
				case "c":
					p.scrollTop = s + d/2 - c/2;
					break;
				case "B":	// 靠下
				case "b":
					p.scrollTop = s + d + padding - c;
					break;
			}
		}

		// 横向调整
		if (horizontal) {
			c = p.clientWidth;		// 父元素当前长度
			d = doe.offsetWidth;		// 子元素当前长度
			s = doe.offsetLeft;		// 子元素相对于父元素的位置
			if (t === "static") {
				s -= p.offsetLeft + p.clientLeft;
			}
			switch (horizontal) {
				case "L":	// 靠左
				case "l":
					p.scrollLeft = s - padding;
					break;
				case "C":	// 居中
				case "c":
					p.scrollLeft = s + d/2 - c/2;
					break;
				case "R":	// 靠右
				case "r":
					p.scrollLeft = s + d + padding - c;
					break;
			}
		}
	}
};
LZR.HTML.Util.DomTool.prototype.matchParent.lzrClass_ = LZR.HTML.Util.DomTool;

// 获取文档
LZR.HTML.Util.DomTool.prototype.getDocument = function (doe/*as:Object*/)/*as:Object*/ {
	return doe.ownerDocument || document;
};
LZR.HTML.Util.DomTool.prototype.getDocument.lzrClass_ = LZR.HTML.Util.DomTool;

/*************************************************
作者：子牛连
类名：Evt
说明：事件
创建日期：27-七月-2016 12:30:03
版本号：1.0
*************************************************/

LZR.load([
	"LZR.HTML.Util",
	"LZR.HTML.Util.DomTool"
], "LZR.HTML.Util.Evt");
LZR.HTML.Util.Evt = function (obj) {
	// 元素工具
	this.utDt/*m*/ = LZR.getSingleton(LZR.HTML.Util.DomTool);	/*as:LZR.HTML.Util.DomTool*/

	if (obj && obj.lzrGeneralization_) {
		obj.lzrGeneralization_.prototype.init_.call(this);
	} else {
		this.init_(obj);
	}
};
LZR.HTML.Util.Evt.prototype.className_ = "LZR.HTML.Util.Evt";
LZR.HTML.Util.Evt.prototype.version_ = "1.0";

LZR.load(null, "LZR.HTML.Util.Evt");

// 鼠标按键状态常量
LZR.HTML.Util.Evt.prototype.MouseStat = {nan: 0, lk: 1, rk: 2, mid: 4, touch:128};	/*as:Object*/

// 构造器
LZR.HTML.Util.Evt.prototype.init_ = function (obj/*as:Object*/) {
	if (obj) {
		LZR.setObj (this, obj);
		this.hdObj_(obj);
	}
};
LZR.HTML.Util.Evt.prototype.init_.lzrClass_ = LZR.HTML.Util.Evt;

// 对构造参数的特殊处理
LZR.HTML.Util.Evt.prototype.hdObj_ = function (obj/*as:Object*/) {
	
};
LZR.HTML.Util.Evt.prototype.hdObj_.lzrClass_ = LZR.HTML.Util.Evt;

// 获取事件
LZR.HTML.Util.Evt.prototype.getEvent = function (e/*as:Object*/)/*as:Object*/ {
	return window.event || e;
};
LZR.HTML.Util.Evt.prototype.getEvent.lzrClass_ = LZR.HTML.Util.Evt;

// 获取触发事件的DOM对象
LZR.HTML.Util.Evt.prototype.getEventTarg = function (e/*as:Object*/)/*as:Object*/ {
	e = this.getEvent(e);
	return e.srcElement || e.target;
};
LZR.HTML.Util.Evt.prototype.getEventTarg.lzrClass_ = LZR.HTML.Util.Evt;

// 阻止默认事件的执行
LZR.HTML.Util.Evt.prototype.stopDefault = function (e/*as:Object*/) {
	if ( e && e.preventDefault ) {
		e.preventDefault();
	} else {
		// IE 浏览器
		window.event.returnValue = false;
	}
};
LZR.HTML.Util.Evt.prototype.stopDefault.lzrClass_ = LZR.HTML.Util.Evt;

// 阻止事件冒泡
LZR.HTML.Util.Evt.prototype.stopBubble = function (e/*as:Object*/) {
	if (e && e.stopPropagation)  {
		e.stopPropagation();
	} else {
		// IE 浏览器
		window.event.cancelBubble=true;
	}
};
LZR.HTML.Util.Evt.prototype.stopBubble.lzrClass_ = LZR.HTML.Util.Evt;

// 获取鼠标位置
LZR.HTML.Util.Evt.prototype.getMousePosition = function (e/*as:Object*/)/*as:Object*/ {
	if (!isNaN(e.pageX) || !isNaN(e.pageY)) {
		return {x: e.pageX, y: e.pageY};
	} else {
		// IE 浏览器
		var dm = this.utDt.getDocument(this.getEventTarg(e));
		return {
			x: window.event.clientX + dm.documentElement.scrollLeft - dm.documentElement.clientLeft,
			y: window.event.clientY + dm.documentElement.scrollTop - dm.documentElement.clientTop
		};
	}
};
LZR.HTML.Util.Evt.prototype.getMousePosition.lzrClass_ = LZR.HTML.Util.Evt;

// 解析鼠标按键状态
LZR.HTML.Util.Evt.prototype.parseMouseKey = function (evt/*as:Object*/)/*as:string*/ {
	var e = this.getEvent(evt);
	var en = LZR.getClassName(e);
	var k;
	switch (en) {
		case "TouchEvent":
			return "touch";
			break;
		case "MouseEvent":
			k = this.getEvent(evt).button;
			if ("\v" != "v") {
				// 非 IE 6、7、8 版 rotate
				switch (k) {
					case 0:
						k = this.MouseStat.lk;
						break;
					case 1:
						k = this.MouseStat.mid;
						break;
				}
			}
			switch (k) {
				case this.MouseStat.lk:
					return "lk";
				case this.MouseStat.rk:
					return "rk";
				case this.MouseStat.mid:
					return "mid";
				default:
					return "nan";
			}
			break;
		default:
			return "nan";
			break;
	}
};
LZR.HTML.Util.Evt.prototype.parseMouseKey.lzrClass_ = LZR.HTML.Util.Evt;

// 添加一个DOM事件
LZR.HTML.Util.Evt.prototype.addEvt = function (obj/*as:Object*/, eventName/*as:string*/, callback/*as:fun*/, useCapture/*as:boolean*/)/*as:fun*/ {
	if(obj.dispatchEvent){
		obj.addEventListener(eventName, callback, useCapture  );
	} else {
		// IE 浏览器
		obj.attachEvent( "on"+eventName, callback);
	}
	return callback;
};
LZR.HTML.Util.Evt.prototype.addEvt.lzrClass_ = LZR.HTML.Util.Evt;

// 移除一个DOM事件
LZR.HTML.Util.Evt.prototype.delEvt = function (obj/*as:Object*/, eventName/*as:string*/, callback/*as:fun*/, useCapture/*as:boolean*/) {
	if(obj.dispatchEvent){
		obj.removeEventListener(eventName, callback, useCapture  );
	} else {
		// IE 浏览器
		obj.detachEvent( "on"+eventName, callback);
	}
};
LZR.HTML.Util.Evt.prototype.delEvt.lzrClass_ = LZR.HTML.Util.Evt;

// 添加一个滚轮事件
LZR.HTML.Util.Evt.prototype.addWheel = function (obj/*as:Object*/, callback/*as:fun*/, useCapture/*as:boolean*/)/*as:fun*/ {
	var wheelType = "mousewheel";
	var dm = this.utDt.getDocument(obj);
	try {
		dm.createEvent("MouseScrollEvents");
		wheelType = "DOMMouseScroll";			// 火狐浏览器私有类型
	} catch(e) {}

	var self = this;
	return this.addEvt(obj, wheelType, function(e){
		var event = self.getEvent(e);
		if ("wheelDelta" in event){

			//统一为±120，其中正数表示为向上滚动，负数表示向下滚动
			var delta = event.wheelDelta;

			//opera 9x系列的滚动方向与IE保持一致，10后修正
			if( window.opera && opera.version() < 10 ) delta = -delta;

			//由于事件对象的原有属性是只读，我们只能通过添加一个私有属性delta来解决兼容问题
			event.delta = Math.round(delta) /120; //修正safari的浮点 bug

		} else if ("detail" in event ) {
			//为火狐添加更大众化的wheelDelta
			event.wheelDelta = -event.detail * 40;

			//添加私有的delta
			event.delta = event.wheelDelta /120;
		}

		// callback.call(obj,event);	// 修正this指向
		callback(event);
	}, useCapture);
};
LZR.HTML.Util.Evt.prototype.addWheel.lzrClass_ = LZR.HTML.Util.Evt;

// 移除一个滚轮事件
LZR.HTML.Util.Evt.prototype.delWheel = function (obj/*as:Object*/, callback/*as:fun*/, useCapture/*as:boolean*/) {
	var wheelType = "mousewheel";
	var dm = this.utDt.getDocument(obj);
	try {
		dm.createEvent("MouseScrollEvents");
		wheelType = "DOMMouseScroll";		// 火狐浏览器私有类型
	} catch(e) {}

	this.delEvt(obj, wheelType, callback, useCapture);
};
LZR.HTML.Util.Evt.prototype.delWheel.lzrClass_ = LZR.HTML.Util.Evt;

// 触发事件
LZR.HTML.Util.Evt.prototype.trigger = function (doe/*as:Object*/, evtNam/*as:string*/) {
	var evt;
	var dm = this.utDt.getDocument(doe);
	if (dm.createEventObject){
		// dispatch for IE
		evt = dm.createEventObject();
		doe.fireEvent("on"+evtNam,evt);
	} else {
		// dispatch for firefox + others
		evt = dm.createEvent("HTMLEvents");
		evt.initEvent(evtNam, true, true );	// event name, bubbling, cancelable
		doe.dispatchEvent(evt);
	}
};
LZR.HTML.Util.Evt.prototype.trigger.lzrClass_ = LZR.HTML.Util.Evt;

/*************************************************
作者：子牛连
类名：Ctrl
说明：Doe控制器
创建日期：27-七月-2016 12:30:02
版本号：1.0
*************************************************/

LZR.load([
	"LZR.HTML.Base",
	"LZR.HTML.Base.Doe",
	"LZR.Base.CallBacks.CallBack",
	"LZR.Base.InfEvt",
	"LZR.Base.Ary",
	"LZR.HTML.Util.Evt",
	"LZR.Base.Str",
	"LZR.Util"
], "LZR.HTML.Base.Ctrl");
LZR.HTML.Base.Ctrl = function (obj) /*interfaces:LZR.Base.InfEvt*/ {
	LZR.Base.InfEvt.call(this);

	// 删除方式
	this.delMode = 1;	/*as:int*/

	// 数组工具
	this.utAry/*m*/ = LZR.getSingleton(LZR.Base.Ary);	/*as:LZR.Base.Ary*/

	// 事件工具
	this.utEvt/*m*/ = LZR.getSingleton(LZR.HTML.Util.Evt);	/*as:LZR.HTML.Util.Evt*/

	// 元素类
	this.clsDoe/*m*/ = (LZR.HTML.Base.Doe);	/*as:fun*/

	// 元素集合
	this.subs/*m*/ = [];	/*as:LZR.HTML.Base.Doe*/

	// 字串工具
	this.utStr/*m*/ = LZR.getSingleton(LZR.Base.Str);	/*as:LZR.Base.Str*/

	// 通用工具
	this.utLzr/*m*/ = LZR.getSingleton(LZR.Util);	/*as:LZR.Util*/

	if (obj && obj.lzrGeneralization_) {
		obj.lzrGeneralization_.prototype.init_.call(this);
	} else {
		this.init_(obj);
	}
};
LZR.HTML.Base.Ctrl.prototype = LZR.clone (LZR.Base.InfEvt.prototype, LZR.HTML.Base.Ctrl.prototype);
LZR.HTML.Base.Ctrl.prototype.className_ = "LZR.HTML.Base.Ctrl";
LZR.HTML.Base.Ctrl.prototype.version_ = "1.0";

LZR.load(null, "LZR.HTML.Base.Ctrl");

// 构造器
LZR.HTML.Base.Ctrl.prototype.init_ = function (obj/*as:Object*/) {
	if (obj) {
		LZR.setObj (this, obj);
		this.hdObj_(obj);
	}
};
LZR.HTML.Base.Ctrl.prototype.init_.lzrClass_ = LZR.HTML.Base.Ctrl;

// 对构造参数的特殊处理
LZR.HTML.Base.Ctrl.prototype.hdObj_ = function (obj/*as:Object*/) {
	
};
LZR.HTML.Base.Ctrl.prototype.hdObj_.lzrClass_ = LZR.HTML.Base.Ctrl;

// 添加一个Doe元素
LZR.HTML.Base.Ctrl.prototype.add = function (doeo/*as:LZR.HTML.Base.Doe*/, pro/*as:Object*/, obj/*as:Object*/) {
	if (!doeo.ctrl) {
		doeo.ctrl = {};
	}
	var dc = doeo.ctrl[this.className_];
	if (dc) {
		dc.del(doeo);
	}
	doeo.ctrl[this.className_] = this;
	this.subs.push(doeo);
	this.addEvt(doeo, pro, obj);
};
LZR.HTML.Base.Ctrl.prototype.add.lzrClass_ = LZR.HTML.Base.Ctrl;

// 删除一个Doe元素
LZR.HTML.Base.Ctrl.prototype.del = function (doeo/*as:LZR.HTML.Base.Doe*/)/*as:boolean*/ {
	if (doeo.ctrl && doeo.ctrl[this.className_] === this) {
		this.delEvt(doeo);
		LZR.del (doeo.ctrl, this.className_);
		this.utAry.delByVal(this.subs, doeo);
		return true;
	}
	return false;
};
LZR.HTML.Base.Ctrl.prototype.del.lzrClass_ = LZR.HTML.Base.Ctrl;

// 给元素添加事件集
LZR.HTML.Base.Ctrl.prototype.addEvt = function (doeo/*as:LZR.HTML.Base.Doe*/, pro/*as:Object*/, obj/*as:Object*/) {
	
};
LZR.HTML.Base.Ctrl.prototype.addEvt.lzrClass_ = LZR.HTML.Base.Ctrl;

// 移除元素的事件集
LZR.HTML.Base.Ctrl.prototype.delEvt = function (doeo/*as:LZR.HTML.Base.Doe*/) {
	
};
LZR.HTML.Base.Ctrl.prototype.delEvt.lzrClass_ = LZR.HTML.Base.Ctrl;

// 在元素内创建数据
LZR.HTML.Base.Ctrl.prototype.crtDat = function (doeo/*as:LZR.HTML.Base.Doe*/, key/*as:string*/, obj/*as:Object*/)/*as:Object*/ {
	key = this.checkPrefix(key);

	// var cls = this.clsNum;
	if (!doeo.dat) {
		doeo.dat = {};
	}
	if (!doeo.dat[key]) {
		doeo.dat[key] = obj;
	}
	return doeo.dat[key];
};
LZR.HTML.Base.Ctrl.prototype.crtDat.lzrClass_ = LZR.HTML.Base.Ctrl;

// 在元素内创建子元素
LZR.HTML.Base.Ctrl.prototype.crtDoe = function (doeo/*as:LZR.HTML.Base.Doe*/, id/*as:string*/, typ/*as:string*/, css/*as:string*/)/*as:LZR.HTML.Base.Doe*/ {
	id = this.checkPrefix(id);
	if (css === undefined) {
		css = "Lc_" + id;
	}

	var d = doeo.getById(id);
	if (!d) {
		d = new this.clsDoe ({
			id: id,
			hd_typ: typ,
			hd_css: css
		});
		doeo.add(d);
	}
	return d;
};
LZR.HTML.Base.Ctrl.prototype.crtDoe.lzrClass_ = LZR.HTML.Base.Ctrl;

// 为元素的数据创建回调
LZR.HTML.Base.Ctrl.prototype.crtCb2Dat = function (doeo/*as:LZR.HTML.Base.Doe*/, evt/*as:Object*/, cbNam/*as:string*/, f/*as:fun*/) {
	var enam = this.className_ + "_" + cbNam;
	if (!doeo.ctrlCbs) {
		doeo.ctrlCbs = {};	// 控制器相关的回调函数集合
	}
	doeo.ctrlCbs[enam] =  enam + "_" + evt.id;
	if (f) {
		evt.add( f, doeo.ctrlCbs[enam] );
	} else {
		evt.add( this.utLzr.bind(this, this[cbNam], doeo), doeo.ctrlCbs[enam] );
	}
};
LZR.HTML.Base.Ctrl.prototype.crtCb2Dat.lzrClass_ = LZR.HTML.Base.Ctrl;

// 删除元素数据的相关回调
LZR.HTML.Base.Ctrl.prototype.delCb2Dat = function (doeo/*as:LZR.HTML.Base.Doe*/, evt/*as:Object*/, cbNam/*as:string*/)/*as:LZR.Base.CallBacks.CallBack*/ {
	var enam = this.className_ + "_" + cbNam;
	var r = evt.del(doeo.ctrlCbs[enam]);
	LZR.del (doeo.ctrlCbs, enam);
	return r;
};
LZR.HTML.Base.Ctrl.prototype.delCb2Dat.lzrClass_ = LZR.HTML.Base.Ctrl;

// 获取元素数据的相关回调
LZR.HTML.Base.Ctrl.prototype.getCb2Dat = function (doeo/*as:LZR.HTML.Base.Doe*/, evt/*as:Object*/, cbNam/*as:string*/)/*as:LZR.Base.CallBacks.CallBack*/ {
	return evt.funs[ doeo.ctrlCbs[(this.className_ + "_" + cbNam)] ];
};
LZR.HTML.Base.Ctrl.prototype.getCb2Dat.lzrClass_ = LZR.HTML.Base.Ctrl;

// 删除元素内数据
LZR.HTML.Base.Ctrl.prototype.delDat = function (doeo/*as:LZR.HTML.Base.Doe*/, key/*as:string*/)/*as:boolean*/ {
	key = this.checkPrefix(key);

	var s;
	var n = 0;
	var e = doeo.dat[key];
	switch (this.delMode) {
		case 0:
			return false;
		case 1:
			if (e && e.evt) {
				e = e.evt;
				for (s in e) {
					n += e[s].count;
				}
			}
			if (n === 0) {
				LZR.del (doeo.dat, key);
				return true;
			} else {
				return false;
			}
			break;
		case 2:
			LZR.del (doeo.dat, key);
			return true;
	}
};
LZR.HTML.Base.Ctrl.prototype.delDat.lzrClass_ = LZR.HTML.Base.Ctrl;

// 前缀检查
LZR.HTML.Base.Ctrl.prototype.checkPrefix = function (nam/*as:string*/)/*as:string*/ {
	if (!this.utStr.startWith (nam, "hct_")) {
		nam = "hct_" + nam;
	}
	return nam;
};
LZR.HTML.Base.Ctrl.prototype.checkPrefix.lzrClass_ = LZR.HTML.Base.Ctrl;

/*************************************************
作者：子牛连
类名：Doe
说明：元素
创建日期：27-七月-2016 12:30:02
版本号：1.0
*************************************************/

LZR.load([
	"LZR.HTML.Base",
	"LZR.Base.Data",
	"LZR.HTML.Base.Ctrl",
	"LZR.HTML.Util.Evt",
	"LZR.Base.InfEvt",
	"LZR.Base.CallBacks",
	"LZR.Util",
	"LZR.HTML.Base.Doe.Css",
	"LZR.HTML.Util.DomPosition",
	"LZR.HTML.Util.DomTool"
], "LZR.HTML.Base.Doe");
LZR.HTML.Base.Doe = function (obj) /*bases:LZR.Base.Data*/ /*interfaces:LZR.Base.InfEvt*/ {
	LZR.initSuper(this, obj);

	LZR.Base.InfEvt.call(this);

	// DOM元素
	this.doe = null;	/*as:Object*/

	// DOM元素的标记名称
	this.typ = "";	/*as:string*/

	// 控制器相关的回调函数集合
	this.ctrlCbs = null;	/*as:Object*/

	// 事件工具
	this.utEvt/*m*/ = LZR.getSingleton(LZR.HTML.Util.Evt);	/*as:LZR.HTML.Util.Evt*/

	// 数据
	this.dat/*m*/ = null;	/*as:LZR.Base.Data*/

	// 回调函数类
	this.clsCb/*m*/ = (LZR.Base.CallBacks);	/*as:fun*/

	// 通用工具
	this.utLzr/*m*/ = LZR.getSingleton(LZR.Util);	/*as:LZR.Util*/

	// 样式
	this.css/*m*/ = new LZR.HTML.Base.Doe.Css();	/*as:LZR.HTML.Base.Doe.Css*/

	// 元素在文档中的位置
	this.position/*m*/ = new LZR.HTML.Util.DomPosition();	/*as:LZR.HTML.Util.DomPosition*/

	// 元素工具
	this.utDt/*m*/ = LZR.getSingleton(LZR.HTML.Util.DomTool);	/*as:LZR.HTML.Util.DomTool*/

	if (obj && obj.lzrGeneralization_) {
		obj.lzrGeneralization_.prototype.init_.call(this);
	} else {
		this.init_(obj);
	}
};
LZR.HTML.Base.Doe.prototype = LZR.clone (LZR.Base.InfEvt.prototype, LZR.HTML.Base.Doe.prototype);
LZR.HTML.Base.Doe.prototype = LZR.clone (LZR.Base.Data.prototype, LZR.HTML.Base.Doe.prototype);
LZR.HTML.Base.Doe.prototype.super_ = [LZR.Base.Data];
LZR.HTML.Base.Doe.prototype.className_ = "LZR.HTML.Base.Doe";
LZR.HTML.Base.Doe.prototype.version_ = "1.0";

LZR.load(null, "LZR.HTML.Base.Doe");

// 适配参数
LZR.HTML.Base.Doe.prototype.adaptation = "";	/*as:string*/

// 构造器
LZR.HTML.Base.Doe.prototype.init_ = function (obj/*as:Object*/) {
	if (obj) {
		LZR.setObj (this, obj);
		this.hdObj_(obj);
	}
};
LZR.HTML.Base.Doe.prototype.init_.lzrClass_ = LZR.HTML.Base.Doe;

// 对构造参数的特殊处理
LZR.HTML.Base.Doe.prototype.hdObj_ = function (obj/*as:Object*/) {
	if (obj.hd_typ) {
		this.hdCss(obj.hd_css, false);
		this.hdTyp (obj.hd_typ);
	} else if (obj.hd_doe) {
		this.hdDoe(obj.hd_doe);
	}

	// 调用父类的参数处理（子数据的递归创建）
	this.utLzr.supCall (this, 0, "hdObj_", obj);
};
LZR.HTML.Base.Doe.prototype.hdObj_.lzrClass_ = LZR.HTML.Base.Doe;

// 处理标记
LZR.HTML.Base.Doe.prototype.hdTyp = function (hd_typ/*as:string*/, flushCss/*as:boolean*/) {
	this.typ = hd_typ;
	this.doe = document.createElement(hd_typ);
	if (flushCss !== false) {
		this.css.flush(this.doe);
	}
};
LZR.HTML.Base.Doe.prototype.hdTyp.lzrClass_ = LZR.HTML.Base.Doe;

// 处理样式
LZR.HTML.Base.Doe.prototype.hdCss = function (hd_css/*as:string*/, flushCss/*as:boolean*/) {
	if (typeof(hd_css) === "string") {
		this.css = this.css.constructor.parse(hd_css);
		if (flushCss !== false) {
			this.css.flush(this.doe);
		}
	}
};
LZR.HTML.Base.Doe.prototype.hdCss.lzrClass_ = LZR.HTML.Base.Doe;

// 处理DOM元素
LZR.HTML.Base.Doe.prototype.hdDoe = function (hd_doe/*as:Object*/) {
	if (hd_doe.tagName) {
		this.doe = hd_doe;
		this.typ = hd_doe.tagName;
		this.css = this.css.constructor.parse(hd_doe);
		this.initSubsByDom ();
	}
};
LZR.HTML.Base.Doe.prototype.hdDoe.lzrClass_ = LZR.HTML.Base.Doe;

// 用元素初始化时包含递归的子元素
LZR.HTML.Base.Doe.prototype.initSubsByDom = function () {
	var i, d, idd;
	var ns = this.doe.childNodes;
	for (i = 0; i<ns.length; i++) {
		if (ns[i].tagName) {
			idd = ns[i].getAttribute("idLzr");
			if (!idd) {
				idd = ns[i].id ? ns[i].id : ("doe_" + this.count.toString());
			}
			d = new this.constructor ({
				id: idd,
				hd_doe: ns[i]
			});
			this.utLzr.supCall (this, 0, "add", d);	// 不能用 this.add (d); 方法
		}
	}
};
LZR.HTML.Base.Doe.prototype.initSubsByDom.lzrClass_ = LZR.HTML.Base.Doe;

// 添加CSS样式
LZR.HTML.Base.Doe.prototype.addCss = function (name/*as:string*/)/*as:boolean*/ {
	if (this.css.add(name)) {
		this.css.flush(this.doe);
		return true;
	} else {
		return false;
	}
};
LZR.HTML.Base.Doe.prototype.addCss.lzrClass_ = LZR.HTML.Base.Doe;

// 删除CSS样式
LZR.HTML.Base.Doe.prototype.delCss = function (name/*as:string*/)/*as:boolean*/ {
	if (this.css.del(name)) {
		this.css.flush(this.doe);
		return true;
	} else {
		return false;
	}
};
LZR.HTML.Base.Doe.prototype.delCss.lzrClass_ = LZR.HTML.Base.Doe;

// 替换CSS样式
LZR.HTML.Base.Doe.prototype.chgCss = function (name/*as:string*/)/*as:boolean*/ {
	this.css.delAll();
	if (name) {
		return this.addCss(name);
	} else {
		this.css.flush(this.doe);
		return true;
	}
};
LZR.HTML.Base.Doe.prototype.chgCss.lzrClass_ = LZR.HTML.Base.Doe;

// 添加控制器
LZR.HTML.Base.Doe.prototype.addCtrl = function (ctl/*as:LZR.HTML.Base.Ctrl*/) {
	ctl.add(this);
};
LZR.HTML.Base.Doe.prototype.addCtrl.lzrClass_ = LZR.HTML.Base.Doe;

// 删除控制器
LZR.HTML.Base.Doe.prototype.delCtrl = function (ctl/*as:LZR.HTML.Base.Ctrl*/)/*as:boolean*/ {
	return ctl.del(this);
};
LZR.HTML.Base.Doe.prototype.delCtrl.lzrClass_ = LZR.HTML.Base.Doe;

// 创建事件
LZR.HTML.Base.Doe.prototype.crtEvt = function (name/*as:string*/)/*as:Object*/ {
	var e = this.evt[name];
	if (!e) {
		// 创建事件
		e = new this.clsCb({
			obj: this
		});
		this.evt[name] = e;
		switch (name) {
			case "wheel":
				this.utEvt.addWheel (this.doe, e.exe, false);
				break;
			case "resize":
				this.utEvt.addEvt (window, name, e.exe, false);
				break;
			default:
				this.utEvt.addEvt (this.doe, name, e.exe, false);
				break;
		}
	}
	return e;
};
LZR.HTML.Base.Doe.prototype.crtEvt.lzrClass_ = LZR.HTML.Base.Doe;

// 添加事件
LZR.HTML.Base.Doe.prototype.addEvt = function (name/*as:string*/, fun/*as:fun*/, funam/*as:string*/) {
	this.crtEvt(name).add(fun, funam);
};
LZR.HTML.Base.Doe.prototype.addEvt.lzrClass_ = LZR.HTML.Base.Doe;

// 删除事件
LZR.HTML.Base.Doe.prototype.delEvt = function (name/*as:string*/, funam/*as:string*/) {
	var e = this.evt[name];
	if (e) {
		if (funam) {
			e.del(funam);
			if (e.count) {
				return;
			}
		}

		switch (name) {
			case "wheel":
				this.utEvt.delWheel (this.doe, e.exe, false);
				break;
			case "resize":
				this.utEvt.delEvt (window, name, e.exe, false);
				break;
			default:
				this.utEvt.delEvt (this.doe, name, e.exe, false);
				break;
		}
		LZR.del(this.evt, name);
	}
};
LZR.HTML.Base.Doe.prototype.delEvt.lzrClass_ = LZR.HTML.Base.Doe;

// 触发事件
LZR.HTML.Base.Doe.prototype.trigger = function (evtNam/*as:string*/) {
	this.utEvt.trigger(this.doe, evtNam);
};
LZR.HTML.Base.Doe.prototype.trigger.lzrClass_ = LZR.HTML.Base.Doe;

// 放入一个DOM元素中
LZR.HTML.Base.Doe.prototype.place = function (doe/*as:Object*/) {
	this.parent.set(null);
	doe.appendChild(this.doe);
};
LZR.HTML.Base.Doe.prototype.place.lzrClass_ = LZR.HTML.Base.Doe;

// 从DOM中移出
LZR.HTML.Base.Doe.prototype.remove = function () {
	this.parent.set(null);
	var p = this.doe.parentNode;
	if (p) {
		p.removeChild(this.doe);
	}
};
LZR.HTML.Base.Doe.prototype.remove.lzrClass_ = LZR.HTML.Base.Doe;

// 设置DOM元素属性
LZR.HTML.Base.Doe.prototype.setAtt = function (key/*as:string*/, val/*as:string*/) {
	this.utDt.setAtt(key, val, this.doe);
};
LZR.HTML.Base.Doe.prototype.setAtt.lzrClass_ = LZR.HTML.Base.Doe;

// 获取DOM元素属性
LZR.HTML.Base.Doe.prototype.getAtt = function (key/*as:string*/)/*as:string*/ {
	return this.utDt.getAtt(key, this.doe);
};
LZR.HTML.Base.Doe.prototype.getAtt.lzrClass_ = LZR.HTML.Base.Doe;

// 删除DOM元素属性
LZR.HTML.Base.Doe.prototype.delAtt = function (key/*as:string*/) {
	this.utDt.delAtt(key, this.doe);
};
LZR.HTML.Base.Doe.prototype.delAtt.lzrClass_ = LZR.HTML.Base.Doe;

// 设置DOM元素的Styley样式
LZR.HTML.Base.Doe.prototype.setStyle = function (key/*as:string*/, val/*as:string*/) {
	if (typeof (val) === "number") {
		val = val + "px";
	}
	this.utDt.setStyle(key, val, this.doe);
};
LZR.HTML.Base.Doe.prototype.setStyle.lzrClass_ = LZR.HTML.Base.Doe;

// 获取DOM元素的Styley样式
LZR.HTML.Base.Doe.prototype.getStyle = function (key/*as:string*/)/*as:string*/ {
	return this.utDt.getStyle(key, this.doe);
};
LZR.HTML.Base.Doe.prototype.getStyle.lzrClass_ = LZR.HTML.Base.Doe;

// 计算位置
LZR.HTML.Base.Doe.prototype.calcPosition = function () {
	this.position = this.utDt.calcPosition(this.doe);
};
LZR.HTML.Base.Doe.prototype.calcPosition.lzrClass_ = LZR.HTML.Base.Doe;

// 在父元素中滚动条定位
LZR.HTML.Base.Doe.prototype.matchParent = function (vertical/*as:string*/, horizontal/*as:string*/, padding/*as:int*/) {
	this.utDt.matchParent(vertical, horizontal, padding, this.doe);
};
LZR.HTML.Base.Doe.prototype.matchParent.lzrClass_ = LZR.HTML.Base.Doe;

// 获取文档
LZR.HTML.Base.Doe.prototype.getDocument = function ()/*as:Object*/ {
	return this.utDt.getDocument(this.doe);
};
LZR.HTML.Base.Doe.prototype.getDocument.lzrClass_ = LZR.HTML.Base.Doe;

// ---- 添加
LZR.HTML.Base.Doe.prototype.add = function (sub/*as:LZR.Base.Data*/, id/*as:string*/)/*as:boolean*/ {
	if (this.utLzr.supCall (this, 0, "add", sub, id)) {
		this.doe.appendChild(sub.doe);
		return true;
	} else {
		return false;
	}
};
LZR.HTML.Base.Doe.prototype.add.lzrClass_ = LZR.HTML.Base.Doe;

// ---- 删除
LZR.HTML.Base.Doe.prototype.del = function (id/*as:string*/)/*as:Object*/ {
	var r = this.utLzr.supCall (this, 0, "del", id);
	if (r) {
		this.doe.removeChild(r.doe);
	}
	return r;
};
LZR.HTML.Base.Doe.prototype.del.lzrClass_ = LZR.HTML.Base.Doe;

// ---- 处理克隆参数
LZR.HTML.Base.Doe.prototype.hdClonePro = function (name/*as:string*/, rt/*as:Object*/, dep/*as:boolean*/)/*as:Object*/ {
	switch (name) {
		case "doe":
		case "evt":
		case "ctrl":
		case "ctrlCbs":
		case "clsCb":
		case "utEvt":
		case "utLzr":
			break;
		case "typ":
			rt.hd_typ = this[name];
			break;
		case "dat":	// 数据不克隆
			rt.dat = this[name];
			break;
		case "css":
			rt.hd_css = this.css.print();
			break;
		default:
			this.utLzr.supCall (this, 0, "hdClonePro", name, rt, dep);
			break;
	}
	return rt;
};
LZR.HTML.Base.Doe.prototype.hdClonePro.lzrClass_ = LZR.HTML.Base.Doe;

// ---- 克隆
LZR.HTML.Base.Doe.prototype.clone = function (dep/*as:boolean*/)/*as:Object*/ {
	var r = this.utLzr.supCall (this, 0, "clone", dep);

	// 追加控制器
	if (this.ctrl) {
		for (var s in this.ctrl) {
			this.ctrl[s].add(r);
		}
	}
	return r;
};
LZR.HTML.Base.Doe.prototype.clone.lzrClass_ = LZR.HTML.Base.Doe;


/*************************************************
作者：子牛连
类名：Css
说明：样式
创建日期：27-七月-2016 12:30:02
版本号：1.0
*************************************************/

LZR.load([
	"LZR.HTML.Base.Doe",
	"LZR.Base.Data",
	"LZR.Util"
], "LZR.HTML.Base.Doe.Css");
LZR.HTML.Base.Doe.Css = function (obj) /*bases:LZR.Base.Data*/ {
	LZR.initSuper(this, obj);

	// CSS样式名
	this.name = "";	/*as:string*/

	// 通用工具
	this.utLzr/*m*/ = LZR.getSingleton(LZR.Util);	/*as:LZR.Util*/

	if (obj && obj.lzrGeneralization_) {
		obj.lzrGeneralization_.prototype.init_.call(this);
	} else {
		this.init_(obj);
	}
};
LZR.HTML.Base.Doe.Css.prototype = LZR.clone (LZR.Base.Data.prototype, LZR.HTML.Base.Doe.Css.prototype);
LZR.HTML.Base.Doe.Css.prototype.super_ = [LZR.Base.Data];
LZR.HTML.Base.Doe.Css.prototype.className_ = "LZR.HTML.Base.Doe.Css";
LZR.HTML.Base.Doe.Css.prototype.version_ = "1.0";

LZR.load(null, "LZR.HTML.Base.Doe.Css");

// 构造器
LZR.HTML.Base.Doe.Css.prototype.init_ = function (obj/*as:Object*/) {
	if (obj) {
		LZR.setObj (this, obj);
		this.hdObj_(obj);
		// 不对 chd_ 做递归创建
	}
};
LZR.HTML.Base.Doe.Css.prototype.init_.lzrClass_ = LZR.HTML.Base.Doe.Css;

// 对构造参数的特殊处理
LZR.HTML.Base.Doe.Css.prototype.hdObj_ = function (obj/*as:Object*/) {
	
};
LZR.HTML.Base.Doe.Css.prototype.hdObj_.lzrClass_ = LZR.HTML.Base.Doe.Css;

// 刷新样式
LZR.HTML.Base.Doe.Css.prototype.flush = function (doe/*as:Object*/) {
	var s = this.print();
	if (s || s === "") {
		doe.className = s;
	}
};
LZR.HTML.Base.Doe.Css.prototype.flush.lzrClass_ = LZR.HTML.Base.Doe.Css;

// ---- 添加
LZR.HTML.Base.Doe.Css.prototype.add = function (name/*as:string*/)/*as:boolean*/ {
	var css;
	switch (LZR.getClassName(name)) {
		case "string":
			if (name.indexOf(" ") === -1) {
				css = new this.constructor({
					id: name,
					name: name
				});
			} else {
				// 字符串包含多个样式名时的处理
				var s = name.split(" ");
				var b = false;
				for (var i=0; i<s.length; i++) {
					b = this.add (s[i]) || b;
				}
				return b;
			}
			break;
		case this.className_:
			css = name;
			break;
		default:
			css = new this.constructor(name);
			break;
	}
	return this.utLzr.supCall (this, 0, "add", css);
};
LZR.HTML.Base.Doe.Css.prototype.add.lzrClass_ = LZR.HTML.Base.Doe.Css;

// ---- 输出
LZR.HTML.Base.Doe.Css.prototype.print = function ()/*as:string*/ {
	var r = this.name;
	for (var s in this.subs) {
		if (r) {
			r += " ";
		}
		r += this.subs[s].print();
	}
	return r;
};
LZR.HTML.Base.Doe.Css.prototype.print.lzrClass_ = LZR.HTML.Base.Doe.Css;

// 解析样式
LZR.HTML.Base.Doe.Css.parse = function (doe/*as:Object*/)/*as:Css*/ {
	var r = new this();
	var s = doe.className;
	if (!s && (typeof(doe) === "string")) {
		s = doe;
	}
	if (s) {
		s = s.split(" ");
		for (var i=0; i<s.length; i++) {
			if (s[i]) {
				r.add(s[i]);
			}
		}
	}
	return r;
};
LZR.HTML.Base.Doe.Css.parse.lzrClass_ = LZR.HTML.Base.Doe.Css;

// 添加样式
LZR.HTML.Base.Doe.Css.addCss = function (doe/*as:Object*/, name/*as:string*/) {
	var r = this.parse(doe);
	r.add(name);
	r.flush(doe);
};
LZR.HTML.Base.Doe.Css.addCss.lzrClass_ = LZR.HTML.Base.Doe.Css;

// 删除样式
LZR.HTML.Base.Doe.Css.delCss = function (doe/*as:Object*/, name/*as:string*/) {
	var r = this.parse(doe);
	r.del(name);
	r.flush(doe);
};
LZR.HTML.Base.Doe.Css.delCss.lzrClass_ = LZR.HTML.Base.Doe.Css;

/*************************************************
作者：子牛连
类名：Scd
说明：选择器
创建日期：27-七月-2016 12:30:04
版本号：1.2
*************************************************/

LZR.load([
	"LZR.HTML.Base.Ctrl",
	"LZR.HTML.Base.Doe",
	"LZR.Base.Val.Ctrl"
], "LZR.HTML.Base.Ctrl.Scd");
LZR.HTML.Base.Ctrl.Scd = function (obj) /*bases:LZR.HTML.Base.Ctrl*/ {
	LZR.initSuper(this, obj);

	// 被选中时的样式
	this.css = "";	/*as:string*/

	// 鼠标可用
	this.mouseAble = true;	/*as:boolean*/

	// 触控可用
	this.touchAble = true;	/*as:boolean*/

	// 值控制器类
	this.clsVc/*m*/ = (LZR.Base.Val.Ctrl);	/*as:fun*/

	if (obj && obj.lzrGeneralization_) {
		obj.lzrGeneralization_.prototype.init_.call(this);
	} else {
		this.init_(obj);
	}
};
LZR.HTML.Base.Ctrl.Scd.prototype = LZR.clone (LZR.HTML.Base.Ctrl.prototype, LZR.HTML.Base.Ctrl.Scd.prototype);
LZR.HTML.Base.Ctrl.Scd.prototype.super_ = [LZR.HTML.Base.Ctrl];
LZR.HTML.Base.Ctrl.Scd.prototype.className_ = "LZR.HTML.Base.Ctrl.Scd";
LZR.HTML.Base.Ctrl.Scd.prototype.version_ = "1.1";

LZR.load(null, "LZR.HTML.Base.Ctrl.Scd");

// 构造器
LZR.HTML.Base.Ctrl.Scd.prototype.init_ = function (obj/*as:Object*/) {
	if (obj) {
		LZR.setObj (this, obj);
		this.hdObj_(obj);
	}
};
LZR.HTML.Base.Ctrl.Scd.prototype.init_.lzrClass_ = LZR.HTML.Base.Ctrl.Scd;

// 对构造参数的特殊处理
LZR.HTML.Base.Ctrl.Scd.prototype.hdObj_ = function (obj/*as:Object*/) {
	
};
LZR.HTML.Base.Ctrl.Scd.prototype.hdObj_.lzrClass_ = LZR.HTML.Base.Ctrl.Scd;

// 处理触摸按下事件
LZR.HTML.Base.Ctrl.Scd.prototype.hdTouchDown = function (doeo/*as:LZR.HTML.Base.Doe*/, evt/*as:Object*/) {
	this.utEvt.stopDefault(evt);
	this.hdDown(doeo, true, evt);
};
LZR.HTML.Base.Ctrl.Scd.prototype.hdTouchDown.lzrClass_ = LZR.HTML.Base.Ctrl.Scd;

// 处理按下事件
LZR.HTML.Base.Ctrl.Scd.prototype.hdDown = function (doeo/*as:LZR.HTML.Base.Doe*/, isTouch/*as:boolean*/, evt/*as:Object*/) {
	if (isTouch || this.utEvt.parseMouseKey(evt) === "lk") {	// 判断是左键被按下
		var b = doeo.dat.hct_scd.get();
		doeo.dat.hct_scd.set (!b);
	}
};
LZR.HTML.Base.Ctrl.Scd.prototype.hdDown.lzrClass_ = LZR.HTML.Base.Ctrl.Scd;

// 设置css样式
LZR.HTML.Base.Ctrl.Scd.prototype.setCss = function (doeo/*as:LZR.HTML.Base.Doe*/, val/*as:boolean*/) {
	if (val) {
		doeo.addCss(this.css);
	} else {
		doeo.delCss(this.css);
	}
};
LZR.HTML.Base.Ctrl.Scd.prototype.setCss.lzrClass_ = LZR.HTML.Base.Ctrl.Scd;

// ---- 给元素添加事件集
LZR.HTML.Base.Ctrl.Scd.prototype.addEvt = function (doeo/*as:LZR.HTML.Base.Doe*/, pro/*as:Object*/, obj/*as:Object*/) {
	var v;
	// 创建数据
	if (obj) {
		v = this.crtDat(doeo, "hct_scd", obj);
	} else {
		if (pro !== true) {
			pro = false;
		}
		v = this.crtDat(doeo, "hct_scd", new this.clsVc(pro));
	}

	// 事件添加
	this.crtCb2Dat(doeo, doeo.dat.hct_scd.evt.set, "setCss");
	if (this.mouseAble) {
		doeo.addEvt ("mousedown", this.utLzr.bind(this, this.hdDown, doeo, false), this.className_);
	}
	if (this.touchAble) {
		doeo.addEvt ("touchstart", this.utLzr.bind(this, this.hdTouchDown, doeo, false), this.className_);
	}
};
LZR.HTML.Base.Ctrl.Scd.prototype.addEvt.lzrClass_ = LZR.HTML.Base.Ctrl.Scd;

// ---- 移除元素的事件集
LZR.HTML.Base.Ctrl.Scd.prototype.delEvt = function (doeo/*as:LZR.HTML.Base.Doe*/) {
	this.delCb2Dat(doeo, doeo.dat.hct_scd.evt.set, "setCss");
	if (this.mouseAble) {
		doeo.delEvt ("mousedown", this.className_);
	}
	if (this.touchAble) {
		doeo.delEvt ("touchstart", this.className_);
	}

	// 删除数据
	this.delDat(doeo, "hct_scd");
};
LZR.HTML.Base.Ctrl.Scd.prototype.delEvt.lzrClass_ = LZR.HTML.Base.Ctrl.Scd;


/*************************************************
作者：子牛连
类名：SglScd
说明：单选器
创建日期：27-七月-2016 12:30:04
版本号：1.1
*************************************************/

LZR.load([
	"LZR.HTML.Base.Ctrl",
	"LZR.HTML.Base.Doe",
	"LZR.Base.Val.Ctrl",
	"LZR.HTML.Base.Ctrl.Scd"
], "LZR.HTML.Base.Ctrl.SglScd");
LZR.HTML.Base.Ctrl.SglScd = function (obj) /*bases:LZR.HTML.Base.Ctrl.Scd*/ {
	LZR.initSuper(this, obj);

	// 至少有一个选项被选中
	this.only = true;	/*as:boolean*/

	// 当前被选中的元素
	this.vcCur/*m*/ = new LZR.Base.Val.Ctrl();	/*as:LZR.Base.Val.Ctrl*/

	if (obj && obj.lzrGeneralization_) {
		obj.lzrGeneralization_.prototype.init_.call(this);
	} else {
		this.init_(obj);
	}
};
LZR.HTML.Base.Ctrl.SglScd.prototype = LZR.clone (LZR.HTML.Base.Ctrl.Scd.prototype, LZR.HTML.Base.Ctrl.SglScd.prototype);
LZR.HTML.Base.Ctrl.SglScd.prototype.super_ = [LZR.HTML.Base.Ctrl.Scd];
LZR.HTML.Base.Ctrl.SglScd.prototype.className_ = "LZR.HTML.Base.Ctrl.SglScd";
LZR.HTML.Base.Ctrl.SglScd.prototype.version_ = "1.1";

LZR.load(null, "LZR.HTML.Base.Ctrl.SglScd");

// 构造器
LZR.HTML.Base.Ctrl.SglScd.prototype.init_ = function (obj/*as:Object*/) {
	if (obj) {
		LZR.setObj (this, obj);
		this.hdObj_(obj);
	}
};
LZR.HTML.Base.Ctrl.SglScd.prototype.init_.lzrClass_ = LZR.HTML.Base.Ctrl.SglScd;

// ---- 给元素添加事件集
LZR.HTML.Base.Ctrl.SglScd.prototype.addEvt = function (doeo/*as:LZR.HTML.Base.Doe*/, pro/*as:Object*/, obj/*as:Object*/) {
	this.utLzr.supCall(this, 0, "addEvt", doeo);

	// 添加事件
	this.crtCb2Dat(doeo, doeo.dat.hct_scd.evt.before, "hdBefore");

	// 与 Scd 的设置Css样式不同，无论关联多少个元素，只能触发一次
	var evtName = this.className_ + "_hdBefore_";
	var cbs = doeo.dat.hct_scd.evt.before.funs;
	var b = true;
	for (var s in cbs) {
		if (this.utStr.startWith(s, evtName)) {
			if (cbs[s].enableEvent && cbs[s].autoEvent) {
				if (b) {
					b = false;
				} else {
// console.log (s);
					cbs[s].enableEvent = false;
					cbs[s].autoEvent = false;
				}
			}
		}
	}
};
LZR.HTML.Base.Ctrl.SglScd.prototype.addEvt.lzrClass_ = LZR.HTML.Base.Ctrl.SglScd;

// ---- 移除元素的事件集
LZR.HTML.Base.Ctrl.SglScd.prototype.delEvt = function (doeo/*as:LZR.HTML.Base.Doe*/) {
	var cb = this.delCb2Dat(doeo, doeo.dat.hct_scd.evt.before, "hdBefore");

	// 只能触发一次的事件被关闭时，须寻找有无其它可开启的事件。
	if (cb.enableEvent && cb.autoEvent) {
		var evtName = this.className_ + "_hdBefore_";
		var cbs = doeo.dat.hct_scd.evt.before.funs;
		for (var s in cbs) {
			if (this.utStr.startWith(s, evtName)) {
// console.log (s);
				cbs[s].enableEvent = true;
				cbs[s].autoEvent = true;
				if (this.vcCur.get() === doeo) {
					// 当前被选中的与要删除的元素相同，且 元素的数据有与之匹配的其它元素时：须将当前被选中元素改为可用的其它元素。
					// （目前暂未实现此部分功能）
				}
				break;
			}
		}
	}

	// 调用父类方法
	this.utLzr.supCall(this, 0, "delEvt", doeo);
};
LZR.HTML.Base.Ctrl.SglScd.prototype.delEvt.lzrClass_ = LZR.HTML.Base.Ctrl.SglScd;

// 对构造参数的特殊处理
LZR.HTML.Base.Ctrl.SglScd.prototype.hdObj_ = function (obj/*as:Object*/) {
	
};
LZR.HTML.Base.Ctrl.SglScd.prototype.hdObj_.lzrClass_ = LZR.HTML.Base.Ctrl.SglScd;

// 选择前处理
LZR.HTML.Base.Ctrl.SglScd.prototype.hdBefore = function (doeo/*as:LZR.HTML.Base.Doe*/, val/*as:boolean*/)/*as:boolean*/ {
	if (this.vcCur.get()) {
// alert(0);
		if (this.vcCur.get() === doeo) {
// alert(4);
			// 当，当前被选中的选项 与 正在选择的选项一致，时：
			if (!val && !this.only) {
// alert(5);
				// 当，预设值为 false，且 不需要 至少有一个选项被选中，时：则可进行设值处理。
				this.vcCur.set(null);
				return true;
			} else {
// alert(6);
				// 否则，不做设置处理
				return false;
			}
		} else {
// alert(7);
			// 当，当前被选中的选项 与 正在选择的选项 不一致，时：
			if (val) {
// alert(8);
				// 当，预设值为 true 时：
				var old = this.vcCur.get();
				this.vcCur.set(doeo);
				old.dat.hct_scd.set(false);
				return true;
			} else {
// alert(9);
				// 否则，也可进行设值处理。
				return true;
			}
		}
	} else {
// alert(1);
		// 当，没有任何被选中选项，时：
		if (val) {
// alert(2);
			// 当，预设值为 true 时，可进行设值处理。
			this.vcCur.set(doeo);
			return true;
		} else {
// alert(3);
			// 否则，也可进行设值处理。
			return true;
		}
	}
};
LZR.HTML.Base.Ctrl.SglScd.prototype.hdBefore.lzrClass_ = LZR.HTML.Base.Ctrl.SglScd;

/*************************************************
作者：子牛连
类名：Time
说明：时间工具
创建日期：27-七月-2016 12:30:04
版本号：1.0
*************************************************/

LZR.load([
	"LZR.Base",
	"LZR.Base.Str"
], "LZR.Base.Time");
LZR.Base.Time = function (obj) {
	// 字符串工具
	this.utStr/*m*/ = LZR.getSingleton(LZR.Base.Str);	/*as:LZR.Base.Str*/

	if (obj && obj.lzrGeneralization_) {
		obj.lzrGeneralization_.prototype.init_.call(this);
	} else {
		this.init_(obj);
	}
};
LZR.Base.Time.prototype.className_ = "LZR.Base.Time";
LZR.Base.Time.prototype.version_ = "1.0";

LZR.load(null, "LZR.Base.Time");

// 构造器
LZR.Base.Time.prototype.init_ = function (obj/*as:Object*/) {
	if (obj) {
		LZR.setObj (this, obj);
		this.hdObj_(obj);
	}
};
LZR.Base.Time.prototype.init_.lzrClass_ = LZR.Base.Time;

// 字符串转时间
LZR.Base.Time.prototype.stringToDate = function (strDate/*as:string*/)/*as:Date*/ {
	var r = strDate.match(/\d+/g);
	r[1]--;
	// return eval( "new Date(" + strDate.replace( /\d+(?=-[^-]+$)/, function (a) { console.log (a); return parseInt(a, 10) - 1; } ).match(/\d+/g) + ")" );
	return eval( "new Date(" + r + ")" );
};
LZR.Base.Time.prototype.stringToDate.lzrClass_ = LZR.Base.Time;

// 时间转换为字符串
LZR.Base.Time.prototype.format = function (date/*as:Date*/, format/*as:string*/)/*as:string*/ {
	var s;
	switch (format) {
		case "date":
			s = date.getFullYear();
			s += "-";
			s += date.getMonth() + 1;
			s += "-";
			s += date.getDate();
			break;
		case "dateChn":
			s = date.getFullYear();
			s += "年";
			s += date.getMonth() + 1;
			s += "月";
			s += date.getDate();
			s += "日";
			break;
		case "hourChn":
			s = date.getFullYear();
			s += "年";
			s += date.getMonth() + 1;
			s += "月";
			s += date.getDate();
			s += "日";
			s += this.utStr.format(date.getHours(), 2, "0");
			s += "时";
			break;
		case "mdChn":
			s = date.getMonth() + 1;
			s += "月";
			s += date.getDate();
			s += "日";
			break;
		case "weekChn":
			switch (date.getDay()) {
				case 0:
					s = "周日";
					break;
				case 1:
					s = "周一";
					break;
				case 2:
					s = "周二";
					break;
				case 3:
					s = "周三";
					break;
				case 4:
					s = "周四";
					break;
				case 5:
					s = "周五";
					break;
				case 6:
					s = "周六";
					break;
			}
			break;
		default:
			s = date.getFullYear();
			s += "-";
			s += date.getMonth() + 1;
			s += "-";
			s += date.getDate();
			s += " ";
			s += this.utStr.format(date.getHours(), 2, "0");
			s += ":";
			s += this.utStr.format(date.getMinutes(), 2, "0");
			s += ":";
			s += this.utStr.format(date.getSeconds(), 2, "0");
			break;
	}
	return s;
};
LZR.Base.Time.prototype.format.lzrClass_ = LZR.Base.Time;

// 时间圆整
LZR.Base.Time.prototype.normalize = function (date/*as:Date*/, hour/*as:int*/, clone/*as:boolean*/)/*as:Date*/ {
	if (!date) {
		date = this.getDate();
	} else if (clone) {
		date = new Date(date.valueOf());
	}
	if (isNaN(hour)) {
		hour = date.getHours();
	}
	date.setMinutes(0);
	date.setSeconds(0);
	date.setMilliseconds(0);
	date.setHours(hour);
	return date;
};
LZR.Base.Time.prototype.normalize.lzrClass_ = LZR.Base.Time;

// 时间加N个小时的时间
LZR.Base.Time.prototype.addHour = function (n/*as:int*/, date/*as:Date*/, clone/*as:boolean*/)/*as:Date*/ {
	if (!date) {
		date = this.getDate();
	} else if (clone) {
		date = new Date(date.valueOf());
	}
	date.setTime(date.valueOf() + n * 3600 * 1000);
	return date;
};
LZR.Base.Time.prototype.addHour.lzrClass_ = LZR.Base.Time;

// 复制一个时间
LZR.Base.Time.prototype.clone = function (date/*as:Date*/)/*as:Date*/ {
	if (!date) {
		return this.getDate();
	}
	return new Date(date.valueOf());
};
LZR.Base.Time.prototype.clone.lzrClass_ = LZR.Base.Time;

// 对构造参数的特殊处理
LZR.Base.Time.prototype.hdObj_ = function (obj/*as:Object*/) {
	
};
LZR.Base.Time.prototype.hdObj_.lzrClass_ = LZR.Base.Time;

// 获取当前时间
LZR.Base.Time.prototype.getDate = function ()/*as:Date*/ {
	return new Date();
};
LZR.Base.Time.prototype.getDate.lzrClass_ = LZR.Base.Time;

// 获取当前时间值
LZR.Base.Time.prototype.getTim = function ()/*as:int*/ {
	return this.getDate().getTime();
};
LZR.Base.Time.prototype.getTim.lzrClass_ = LZR.Base.Time;

/*************************************************
作者：子牛连
类名：Btn
说明：按钮
创建日期：27-七月-2016 12:30:02
版本号：1.1
*************************************************/

LZR.load([
	"LZR.HTML.Base.Ctrl",
	"LZR.HTML.Base.Doe",
	"LZR.Base.CallBacks",
	"LZR.Base.Time"
], "LZR.HTML.Base.Ctrl.Btn");
LZR.HTML.Base.Ctrl.Btn = function (obj) /*bases:LZR.HTML.Base.Ctrl*/ {
	LZR.initSuper(this, obj);

	// 点击时间
	this.tim = 0;	/*as:int*/

	// 双击状态
	this.dbStat = 0;	/*as:int*/

	// 延时函数
	this.timout = null;	/*as:fun*/

	// 双击间隔
	this.dbTim = 120;	/*as:int*/

	// 长按间隔
	this.longTim = 500;	/*as:int*/

	// 按钮按下时的样式
	this.css = "";	/*as:string*/

	// 触控可用
	this.touchAble = true;	/*as:boolean*/

	// 鼠标可用
	this.mouseAble = true;	/*as:boolean*/

	// 单击
	this.evt.click/*m*/ = new LZR.Base.CallBacks();	/*as:LZR.Base.CallBacks*/

	// 长按
	this.evt.lclick/*m*/ = new LZR.Base.CallBacks();	/*as:LZR.Base.CallBacks*/

	// 时间工具
	this.utTim/*m*/ = LZR.getSingleton(LZR.Base.Time);	/*as:LZR.Base.Time*/

	// 按下
	this.evt.down/*m*/ = new LZR.Base.CallBacks();	/*as:LZR.Base.CallBacks*/

	// 抬起
	this.evt.up/*m*/ = new LZR.Base.CallBacks();	/*as:LZR.Base.CallBacks*/

	// 双击
	this.evt.dbclick/*m*/ = new LZR.Base.CallBacks();	/*as:LZR.Base.CallBacks*/

	if (obj && obj.lzrGeneralization_) {
		obj.lzrGeneralization_.prototype.init_.call(this);
	} else {
		this.init_(obj);
	}
};
LZR.HTML.Base.Ctrl.Btn.prototype = LZR.clone (LZR.HTML.Base.Ctrl.prototype, LZR.HTML.Base.Ctrl.Btn.prototype);
LZR.HTML.Base.Ctrl.Btn.prototype.super_ = [LZR.HTML.Base.Ctrl];
LZR.HTML.Base.Ctrl.Btn.prototype.className_ = "LZR.HTML.Base.Ctrl.Btn";
LZR.HTML.Base.Ctrl.Btn.prototype.version_ = "1.0";

LZR.load(null, "LZR.HTML.Base.Ctrl.Btn");

// 构造器
LZR.HTML.Base.Ctrl.Btn.prototype.init_ = function (obj/*as:Object*/) {
	if (obj) {
		LZR.setObj (this, obj);
		this.hdObj_(obj);
	}

	this.setEventObj (this);
};
LZR.HTML.Base.Ctrl.Btn.prototype.init_.lzrClass_ = LZR.HTML.Base.Ctrl.Btn;

// 对构造参数的特殊处理
LZR.HTML.Base.Ctrl.Btn.prototype.hdObj_ = function (obj/*as:Object*/) {
	
};
LZR.HTML.Base.Ctrl.Btn.prototype.hdObj_.lzrClass_ = LZR.HTML.Base.Ctrl.Btn;

// 处理触摸按下事件
LZR.HTML.Base.Ctrl.Btn.prototype.hdTouchDown = function (doeo/*as:LZR.HTML.Base.Doe*/, evt/*as:Object*/) {
	this.utEvt.stopDefault(evt);
	// if (this.touchAble === true && evt.touches.length === 1) {
	if (this.touchAble === true) {
		this.touchAble = doeo.doe;
		this.hdDown(doeo, true, evt);
	}
};
LZR.HTML.Base.Ctrl.Btn.prototype.hdTouchDown.lzrClass_ = LZR.HTML.Base.Ctrl.Btn;

// 处理触摸抬起事件
LZR.HTML.Base.Ctrl.Btn.prototype.hdTouchUp = function (doeo/*as:LZR.HTML.Base.Doe*/, evt/*as:Object*/) {
	this.utEvt.stopDefault(evt);
	if (this.touchAble === doeo.doe) {
		this.hdUp(doeo);
		this.touchAble = true;
	}
};
LZR.HTML.Base.Ctrl.Btn.prototype.hdTouchUp.lzrClass_ = LZR.HTML.Base.Ctrl.Btn;

// 处理按下事件
LZR.HTML.Base.Ctrl.Btn.prototype.hdDown = function (doeo/*as:LZR.HTML.Base.Doe*/, isTouch/*as:boolean*/, evt/*as:Object*/) {
	if (isTouch || this.utEvt.parseMouseKey(evt) === "lk") {	// 判断是左键被按下
		doeo.addCss(this.css);

		// 触发按下事件
		if (this.onDown(doeo)) {
			if ((this.dbStat === 2) && ((this.utTim.getTim() - this.tim) < 2*this.dbTim)) {
				this.dbStat = 3;

				// 删除延时单击
				clearTimeout(this.timeout);

				// 触发双击事件
				this.onDbclick(doeo);
				return;
			} else {
				this.tim = this.utTim.getTim();
			}
		}
		this.dbStat = 1;
	}
};
LZR.HTML.Base.Ctrl.Btn.prototype.hdDown.lzrClass_ = LZR.HTML.Base.Ctrl.Btn;

// 处理抬起事件
LZR.HTML.Base.Ctrl.Btn.prototype.hdUp = function (doeo/*as:LZR.HTML.Base.Doe*/, evt/*as:Object*/) {
	switch (this.dbStat) {
		case 3:
			doeo.delCss(this.css);
			break;
		case 1:
			doeo.delCss(this.css);

			// 触发抬起事件
			if (this.onUp(doeo)) {
				var t = this.utTim.getTim() - this.tim;
				if (this.dbTim && t < this.dbTim) {
					this.dbStat = 2;

					// 创建延时单击
					this.timeout = setTimeout(this.utLzr.bind(this, this.onClick, doeo), this.dbTim);
					return;
				} else if (t < this.longTim || !this.longTim) {
					// 触发单击事件
					this.onClick(doeo);
				} else {
					// 触发长按事件
					this.onLclick(doeo);
				}
			}
			break;
	}
	this.dbStat = 0;
};
LZR.HTML.Base.Ctrl.Btn.prototype.hdUp.lzrClass_ = LZR.HTML.Base.Ctrl.Btn;

// 处理移出事件
LZR.HTML.Base.Ctrl.Btn.prototype.hdOut = function (doeo/*as:LZR.HTML.Base.Doe*/, evt/*as:Object*/) {
	doeo.delCss(this.css);
	this.dbStat = 0;
};
LZR.HTML.Base.Ctrl.Btn.prototype.hdOut.lzrClass_ = LZR.HTML.Base.Ctrl.Btn;

// 单击事件
LZR.HTML.Base.Ctrl.Btn.prototype.onClick = function (doeo/*as:LZR.HTML.Base.Doe*/)/*as:boolean*/ {
	return this.evt.click.execute (doeo);
};
LZR.HTML.Base.Ctrl.Btn.prototype.onClick.lzrClass_ = LZR.HTML.Base.Ctrl.Btn;

// 长按事件
LZR.HTML.Base.Ctrl.Btn.prototype.onLclick = function (doeo/*as:LZR.HTML.Base.Doe*/)/*as:boolean*/ {
	return this.evt.lclick.execute (doeo);
};
LZR.HTML.Base.Ctrl.Btn.prototype.onLclick.lzrClass_ = LZR.HTML.Base.Ctrl.Btn;

// 双击事件
LZR.HTML.Base.Ctrl.Btn.prototype.onDbclick = function (doeo/*as:LZR.HTML.Base.Doe*/)/*as:boolean*/ {
	return this.evt.dbclick.execute (doeo);
};
LZR.HTML.Base.Ctrl.Btn.prototype.onDbclick.lzrClass_ = LZR.HTML.Base.Ctrl.Btn;

// 按下事件
LZR.HTML.Base.Ctrl.Btn.prototype.onDown = function (doeo/*as:LZR.HTML.Base.Doe*/)/*as:boolean*/ {
	return this.evt.down.execute (doeo);
};
LZR.HTML.Base.Ctrl.Btn.prototype.onDown.lzrClass_ = LZR.HTML.Base.Ctrl.Btn;

// 抬起事件
LZR.HTML.Base.Ctrl.Btn.prototype.onUp = function (doeo/*as:LZR.HTML.Base.Doe*/)/*as:boolean*/ {
	return this.evt.up.execute (doeo);
};
LZR.HTML.Base.Ctrl.Btn.prototype.onUp.lzrClass_ = LZR.HTML.Base.Ctrl.Btn;

// ---- 给元素添加事件集
LZR.HTML.Base.Ctrl.Btn.prototype.addEvt = function (doeo/*as:LZR.HTML.Base.Doe*/) {
	var out = this.utLzr.bind(this, this.hdOut, doeo);
	if (this.mouseAble) {
		doeo.addEvt ("mousedown", this.utLzr.bind(this, this.hdDown, doeo, false), this.className_);
		doeo.addEvt ("mouseup",  this.utLzr.bind(this, this.hdUp, doeo), this.className_);
		doeo.addEvt ("mouseout",  out, this.className_);
	}
	if (this.touchAble) {
		doeo.addEvt ("touchstart", this.utLzr.bind(this, this.hdTouchDown, doeo), this.className_);
		doeo.addEvt ("touchend", this.utLzr.bind(this, this.hdTouchUp, doeo), this.className_);
		doeo.addEvt ("touchcancel",  out, this.className_);
		doeo.addEvt ("toucheleave",  out, this.className_);
	}
};
LZR.HTML.Base.Ctrl.Btn.prototype.addEvt.lzrClass_ = LZR.HTML.Base.Ctrl.Btn;

// ---- 移除元素的事件集
LZR.HTML.Base.Ctrl.Btn.prototype.delEvt = function (doeo/*as:LZR.HTML.Base.Doe*/) {
	if (this.mouseAble) {
		doeo.delEvt ("mousedown", this.className_);
		doeo.delEvt ("mouseup", this.className_);
		doeo.delEvt ("mouseout", this.className_);
	}
	if (this.touchAble) {
		doeo.delEvt ("touchstart", this.className_);
		doeo.delEvt ("touchend", this.className_);
		doeo.delEvt ("touchcancel", this.className_);
		doeo.delEvt ("toucheleave", this.className_);
	}
};
LZR.HTML.Base.Ctrl.Btn.prototype.delEvt.lzrClass_ = LZR.HTML.Base.Ctrl.Btn;


/*************************************************
作者：子牛连
类名：At911nView
说明：视图
创建日期：26-12月-2016 16:05:07
版本号：1.0
*************************************************/

LZR.load([
	"LZR.Base.Str",
	"LZR.Pro.Rfid.At911n",
	"LZR.HTML.Base.Doe",
	"LZR.HTML.Base.Ctrl.SglScd",
	"LZR.HTML.Base.Ctrl.Btn"
], "LZR.Pro.Rfid.At911n.At911nView");
LZR.Pro.Rfid.At911n.At911nView = function (obj) {
	// 按钮名
	this.btnNams = null;	/*as:Object*/

	// 列表框
	this.listDoeo/*m*/ = null;	/*as:LZR.HTML.Base.Doe*/

	// 标签列表切换控制器
	this.chgCtrl/*m*/ = new LZR.HTML.Base.Ctrl.SglScd({
		mouseAble: false,
		only: false
	});	/*as:LZR.HTML.Base.Ctrl.SglScd*/

	// 按钮控制器
	this.btnCtrl/*m*/ = new LZR.HTML.Base.Ctrl.Btn({
		mouseAble: false,
		dbTim: 0,
		longTim: 0
	});	/*as:LZR.HTML.Base.Ctrl.Btn*/

	// 元素类
	this.clsDoe/*m*/ = (LZR.HTML.Base.Doe);	/*as:fun*/

	// 列表模型
	this.listMod/*m*/ = null;	/*as:LZR.HTML.Base.Doe*/

	// 列表控制栏
	this.listCtrlBar/*m*/ = null;	/*as:LZR.HTML.Base.Doe*/

	// 列表停止栏
	this.listStopBar/*m*/ = null;	/*as:LZR.HTML.Base.Doe*/

	// ECP栏
	this.ecpDoeo/*m*/ = null;	/*as:LZR.HTML.Base.Doe*/

	// TID栏
	this.tidDoeo/*m*/ = null;	/*as:LZR.HTML.Base.Doe*/

	// USR栏
	this.usrDoeo/*m*/ = null;	/*as:LZR.HTML.Base.Doe*/

	// BCK栏
	this.bckDoeo/*m*/ = null;	/*as:LZR.HTML.Base.Doe*/

	// 浮动停止窗
	this.stopDoeo/*m*/ = null;	/*as:LZR.HTML.Base.Doe*/

	// 列表页
	this.listOut/*m*/ = null;	/*as:LZR.HTML.Base.Doe*/

	// 标签页
	this.tagOut/*m*/ = null;	/*as:LZR.HTML.Base.Doe*/

	// 切换按钮
	this.chgScd/*m*/ = null;	/*as:LZR.HTML.Base.Doe*/

	// 写入窗
	this.writeDoeo/*m*/ = null;	/*as:LZR.HTML.Base.Doe*/

	// 字符串工具
	this.utStr/*m*/ = LZR.getSingleton(LZR.Base.Str);	/*as:LZR.Base.Str*/

	if (obj && obj.lzrGeneralization_) {
		obj.lzrGeneralization_.prototype.init_.call(this);
	} else {
		this.init_(obj);
	}
};
LZR.Pro.Rfid.At911n.At911nView.prototype.className_ = "LZR.Pro.Rfid.At911n.At911nView";
LZR.Pro.Rfid.At911n.At911nView.prototype.version_ = "1.0";

LZR.load(null, "LZR.Pro.Rfid.At911n.At911nView");

// 构造器
LZR.Pro.Rfid.At911n.At911nView.prototype.init_ = function (obj/*as:Object*/) {
	if (obj) {
		LZR.setObj (this, obj);
		this.hdObj_(obj);
	}
};
LZR.Pro.Rfid.At911n.At911nView.prototype.init_.lzrClass_ = LZR.Pro.Rfid.At911n.At911nView;

// 对构造参数的特殊处理
LZR.Pro.Rfid.At911n.At911nView.prototype.hdObj_ = function (obj/*as:Object*/) {
	
};
LZR.Pro.Rfid.At911n.At911nView.prototype.hdObj_.lzrClass_ = LZR.Pro.Rfid.At911n.At911nView;

// 初始化视图
LZR.Pro.Rfid.At911n.At911nView.prototype.initView = function (doe/*as:Object*/, domNams/*as:Object*/, btnNams/*as:Object*/) {
	var s;
	var doeo = new this.clsDoe({
		hd_doe: doe
	});

	if (!this.listOut) {
		if (domNams.listOut) {
			this.listOut = doeo.getById(domNams.listOut);
		} else {
			return;
		}
	}

	if (!this.tagOut) {
		if (domNams.tagOut) {
			this.tagOut = doeo.getById(domNams.tagOut);
		} else {
			return;
		}
	}

	if (btnNams) {
		this.btnNams = btnNams;
	}

	for (s in domNams) {
		switch (s) {
			case "chgScd":
				this.chgScd = doeo.getById(domNams.chgScd);
				break;
			case "listDoeo":
				this.listDoeo = this.listOut.getById(domNams.listDoeo);
				break;
			case "listMod":
				this.listMod = this.listDoeo.getById(domNams.listMod);
				this.listMod.remove();
				break;
			case "listCtrlBar":
				this.listCtrlBar = this.listOut.getById(domNams.listCtrlBar);
				break;
			case "listStopBar":
				this.listStopBar = this.listOut.getById(domNams.listStopBar);
				break;
			case "btnCtrlCss":
				this.btnCtrl.css = domNams.btnCtrlCss;
				break;
			case "chgCtrlCss":
				this.chgCtrl.css = domNams.chgCtrlCss;
				break;
			case "ecp":
				this.ecpDoeo = this.tagOut.getById(domNams.ecp);
				break;
			case "tid":
				this.tidDoeo = this.tagOut.getById(domNams.tid);
				break;
			case "usr":
				this.usrDoeo = this.tagOut.getById(domNams.usr);
				break;
			case "bck":
				this.bckDoeo = this.tagOut.getById(domNams.bck);
				break;
			case "stopDoeo":
				this.stopDoeo = doeo.getById(domNams.stopDoeo);
			case "writeDoeo":
				this.writeDoeo = doeo.getById(domNams.writeDoeo);
		}
	}

	this.btnCtrl.add(this.listCtrlBar.getById(this.btnNams.scan));
	this.btnCtrl.add(this.listCtrlBar.getById(this.btnNams.clean));
	this.btnCtrl.add(this.listStopBar);
	this.btnCtrl.add(this.ecpDoeo.getById(this.btnNams.ecpReadBtn));
	this.btnCtrl.add(this.ecpDoeo.getById(this.btnNams.ecpWriteBtn));
	this.btnCtrl.add(this.tidDoeo.getById(this.btnNams.tidReadBtn));
	this.btnCtrl.add(this.usrDoeo.getById(this.btnNams.usrReadBtn));
	this.btnCtrl.add(this.usrDoeo.getById(this.btnNams.usrWriteBtn));
	this.btnCtrl.add(this.bckDoeo.getById(this.btnNams.bckReadBtn));
	this.btnCtrl.add(this.bckDoeo.getById(this.btnNams.bckWriteBtn));
	this.btnCtrl.add(this.stopDoeo.getById(this.btnNams.stopBtn));
	this.btnCtrl.add(this.writeDoeo.getById(this.btnNams.writeOkBtn));
	this.btnCtrl.add(this.writeDoeo.getById(this.btnNams.writeCancleBtn));
	this.chgCtrl.add(this.chgScd);
	this.chgScd.dat.hct_scd.setEventObj(this);
	this.chgScd.dat.hct_scd.evt.change.add(this.showTag);
};
LZR.Pro.Rfid.At911n.At911nView.prototype.initView.lzrClass_ = LZR.Pro.Rfid.At911n.At911nView;

// 刷新列表页
LZR.Pro.Rfid.At911n.At911nView.prototype.flushList = function (tags/*as:Object*/) {
	var s, d, t;
	this.listDoeo.delAll();
	for (s in tags) {
		this.listDoeo.add(tags[s].doeo);
	}
};
LZR.Pro.Rfid.At911n.At911nView.prototype.flushList.lzrClass_ = LZR.Pro.Rfid.At911n.At911nView;

// 设置列表控制栏可见性
LZR.Pro.Rfid.At911n.At911nView.prototype.setListCtrl = function (visiable/*as:boolean*/) {
	if (visiable) {
		this.listCtrlBar.delCss("Lc_nosee");
		this.listStopBar.addCss("Lc_nosee");
	} else {
		this.listCtrlBar.addCss("Lc_nosee");
		this.listStopBar.delCss("Lc_nosee");
	}
};
LZR.Pro.Rfid.At911n.At911nView.prototype.setListCtrl.lzrClass_ = LZR.Pro.Rfid.At911n.At911nView;

// 刷新标签页
LZR.Pro.Rfid.At911n.At911nView.prototype.flushTag = function (tag/*as:Object*/) {
	var v;
	v = this.ecpDoeo.getById(this.btnNams.hx).doe;
	v.innerHTML = tag.banks.ecp.msg;
	v = this.ecpDoeo.getById(this.btnNams.txt).doe;
	v.innerHTML = this.utStr.passUtf8Str(tag.banks.ecp.msg);
	v = this.usrDoeo.getById(this.btnNams.hx).doe;
	v.innerHTML = tag.banks.usr.msg;
	v = this.usrDoeo.getById(this.btnNams.txt).doe;
	v.innerHTML = this.utStr.passUtf8Str(tag.banks.usr.msg);
	v = this.bckDoeo.getById(this.btnNams.hx).doe;
	v.innerHTML = tag.banks.bck.msg;
	v = this.bckDoeo.getById(this.btnNams.txt).doe;
	v.innerHTML = this.utStr.passUtf8Str(tag.banks.bck.msg);
	v = this.tidDoeo.getById(this.btnNams.hx).doe;
	v.innerHTML = tag.banks.tid.msg;
};
LZR.Pro.Rfid.At911n.At911nView.prototype.flushTag.lzrClass_ = LZR.Pro.Rfid.At911n.At911nView;

// 设置悬浮停止窗可见性
LZR.Pro.Rfid.At911n.At911nView.prototype.setStopDoeo = function (visiable/*as:boolean*/) {
	if (visiable) {
		this.stopDoeo.delCss("Lc_nosee");
	} else {
		this.stopDoeo.addCss("Lc_nosee");
	}
};
LZR.Pro.Rfid.At911n.At911nView.prototype.setStopDoeo.lzrClass_ = LZR.Pro.Rfid.At911n.At911nView;

// 设置写入窗可见性
LZR.Pro.Rfid.At911n.At911nView.prototype.setWriteDoeo = function (visiable/*as:boolean*/, title/*as:string*/, msg/*as:string*/) {
	if (visiable) {
		this.writeDoeo.getById(this.btnNams.writeTitle).doe.innerHTML = title;
		this.writeDoeo.getById(this.btnNams.txt).doe.value = msg;
		this.writeDoeo.delCss("Lc_nosee");
	} else {
		this.writeDoeo.addCss("Lc_nosee");
	}
};
LZR.Pro.Rfid.At911n.At911nView.prototype.setStopDoeo.lzrClass_ = LZR.Pro.Rfid.At911n.At911nView;

// 获取写入信息
LZR.Pro.Rfid.At911n.At911nView.prototype.getWriteTxt = function ()/*as:string*/ {
	return this.writeDoeo.getById(this.btnNams.txt).doe.value;
};
LZR.Pro.Rfid.At911n.At911nView.prototype.getWriteTxt.lzrClass_ = LZR.Pro.Rfid.At911n.At911nView;

// 设置标签可见性
LZR.Pro.Rfid.At911n.At911nView.prototype.showTag = function (v/*as:boolean*/, self/*as:Object*/, old/*as:Object*/, tmpVal/*as:Object*/) {
	if (v) {
		if (this.listStopBar.doe.className.indexOf("Lc_nosee") >= 0) {
			this.listOut.addCss("Lc_nosee");
			this.tagOut.delCss("Lc_nosee");
		} else {
			tmpVal.tmpVal = false;
			this.chgCtrl.vcCur.set(null);
			return false;
		};
	} else {
		this.tagOut.addCss("Lc_nosee");
		this.listOut.delCss("Lc_nosee");
	}
};
LZR.Pro.Rfid.At911n.At911nView.prototype.showTag.lzrClass_ = LZR.Pro.Rfid.At911n.At911nView;


/*************************************************
作者：子牛连
类名：At911nCtrl
说明：控制器
创建日期：22-12月-2016 9:10:31
版本号：1.0
*************************************************/

LZR.load([
	"LZR.Base.Str",
	"LZR.Pro.Rfid.At911n",
	"LZR.Pro.Rfid.At911n.ScanTag",
	"LZR.Pro.Rfid.At911n.At911nView"
], "LZR.Pro.Rfid.At911n.At911nCtrl");
LZR.Pro.Rfid.At911n.At911nCtrl = function (obj) {
	// 一个字的单位大小
	this.wordUnit = 2;	/*as:int*/

	// 标签类型
	this.tagTyp = "t6c";	/*as:string*/

	// 标签集合的数量
	this.tagsCount = 0;	/*as:int*/

	// 写入区块
	this.writeBank = null;	/*as:Object*/

	// 标签类
	this.clsTag/*m*/ = (LZR.Pro.Rfid.At911n.ScanTag);	/*as:fun*/

	// 安卓对象
	this.adr/*m*/ = new LZR.Pro.Rfid.At911n();	/*as:LZR.Pro.Rfid.At911n*/

	// 标签集合
	this.tags/*m*/ = {};	/*as:LZR.Pro.Rfid.At911n.ScanTag*/

	// 字符串工具
	this.utStr/*m*/ = LZR.getSingleton(LZR.Base.Str);	/*as:LZR.Base.Str*/

	// 视图
	this.view/*m*/ = new LZR.Pro.Rfid.At911n.At911nView();	/*as:LZR.Pro.Rfid.At911n.At911nView*/

	if (obj && obj.lzrGeneralization_) {
		obj.lzrGeneralization_.prototype.init_.call(this);
	} else {
		this.init_(obj);
	}
};
LZR.Pro.Rfid.At911n.At911nCtrl.prototype.className_ = "LZR.Pro.Rfid.At911n.At911nCtrl";
LZR.Pro.Rfid.At911n.At911nCtrl.prototype.version_ = "1.0";

LZR.load(null, "LZR.Pro.Rfid.At911n.At911nCtrl");

// 构造器
LZR.Pro.Rfid.At911n.At911nCtrl.prototype.init_ = function (obj/*as:Object*/) {
	this.bindView();

	if (obj) {
		LZR.setObj (this, obj);
		this.hdObj_(obj);
	}
};
LZR.Pro.Rfid.At911n.At911nCtrl.prototype.init_.lzrClass_ = LZR.Pro.Rfid.At911n.At911nCtrl;

// 对构造参数的特殊处理
LZR.Pro.Rfid.At911n.At911nCtrl.prototype.hdObj_ = function (obj/*as:Object*/) {
	if (obj.hd_adr) {
		this.adr.adrObj = obj.hd_adr;
	}
};
LZR.Pro.Rfid.At911n.At911nCtrl.prototype.hdObj_.lzrClass_ = LZR.Pro.Rfid.At911n.At911nCtrl;

// 字节与字的转换
LZR.Pro.Rfid.At911n.At911nCtrl.prototype.byte2Word = function (num/*as:int*/)/*as:int*/ {
	return Math.floor(num/this.wordUnit);
};
LZR.Pro.Rfid.At911n.At911nCtrl.prototype.byte2Word.lzrClass_ = LZR.Pro.Rfid.At911n.At911nCtrl;

// 创建一个标签
LZR.Pro.Rfid.At911n.At911nCtrl.prototype.crtTag = function (msg/*as:Object*/)/*as:Object*/ {
	var t;
	var pro = {
		id: this.tagsCount,
		hd_emTyp: this.tagTyp
	}
	if (msg) {
		pro.hd_msg = msg;
		if (msg.id) {
			pro.id = msg.id;
		}
	}

	if (this.tags[pro.id]) {
		// 处理 id 重名问题
		if (msg.ecp && msg.ecp !== pro.id) {
			// 处理 ecp 更名
			t = this.tags[pro.id];
			LZR.del(this.tags, pro.id);
			pro.id = msg.ecp;
			t.id = pro.id;
			this.tags[pro.id] = t;

			// 修改列表元素
			t.doeo.id.set(t.id);
			t.doeo.getById(this.view.btnNams.hx).doe.innerHTML = t.id;
			t.doeo.getById(this.view.btnNams.txt).doe.innerHTML = this.utStr.passUtf8Str(t.id);
		}

		t = this.tags[pro.id];
		t.hdMsg(msg);

		if (msg.scanNum) {
			t.scanNum += msg.scanNum;
			t.doeo.getById(this.view.btnNams.tim).doe.innerHTML = t.scanNum;
		}
	} else {
		if (msg.scanNum) {
			pro.scanNum = msg.scanNum;
		}
		t = new this.clsTag(pro);
		this.tags[pro.id] = t;
		this.tagsCount ++;

		// 创建列表元素
		t.doeo = this.view.listMod.clone();
		t.doeo.getById(this.view.btnNams.tim).doe.innerHTML = t.scanNum;
		t.doeo.getById(this.view.btnNams.hx).doe.innerHTML = t.id;
		t.doeo.getById(this.view.btnNams.txt).doe.innerHTML = this.utStr.passUtf8Str(t.id);
		this.view.listDoeo.add(t.doeo, t.id);
		t.doeo.matchParent("b", "l");
	}

	return this.tags[pro.id];
};
LZR.Pro.Rfid.At911n.At911nCtrl.prototype.crtTag.lzrClass_ = LZR.Pro.Rfid.At911n.At911nCtrl;

// 清空标签
LZR.Pro.Rfid.At911n.At911nCtrl.prototype.clean = function () {
	this.tags = {};
	this.view.listDoeo.delAll();
};
LZR.Pro.Rfid.At911n.At911nCtrl.prototype.clean.lzrClass_ = LZR.Pro.Rfid.At911n.At911nCtrl;

// 扫描
LZR.Pro.Rfid.At911n.At911nCtrl.prototype.scanning = function () {
	this.adr.stat = 1;

	this.view.setListCtrl(false);
	this.adr.scanning();
};
LZR.Pro.Rfid.At911n.At911nCtrl.prototype.scanning.lzrClass_ = LZR.Pro.Rfid.At911n.At911nCtrl;

// 停止
LZR.Pro.Rfid.At911n.At911nCtrl.prototype.stop = function (isTagStop/*as:boolean*/) {
	this.adr.stat = 0;

	this.adr.stop();

	if (isTagStop) {
		this.view.setStopDoeo(false);
	} else {
		this.view.setListCtrl(true);
	}
};
LZR.Pro.Rfid.At911n.At911nCtrl.prototype.stop.lzrClass_ = LZR.Pro.Rfid.At911n.At911nCtrl;

// 读码
LZR.Pro.Rfid.At911n.At911nCtrl.prototype.read = function (bank/*as:Object*/, msg/*as:string*/) {
	this.adr.stat = 3;

	this.view.setStopDoeo(true);

	var l = bank.getLength();
	this.adr.read(bank.emNam.getKey(), this.byte2Word(bank.rs), this.byte2Word(l));
};
LZR.Pro.Rfid.At911n.At911nCtrl.prototype.read.lzrClass_ = LZR.Pro.Rfid.At911n.At911nCtrl;

// 写码准备
LZR.Pro.Rfid.At911n.At911nCtrl.prototype.writeBefore = function (bank/*as:Object*/) {
	this.writeBank = bank;
	this.view.setWriteDoeo(true, bank.emNam.get().nam, this.view[bank.emNam.getKey() + "Doeo"].getById(this.view.btnNams.txt).doe.innerHTML);
};
LZR.Pro.Rfid.At911n.At911nCtrl.prototype.writeBefore.lzrClass_ = LZR.Pro.Rfid.At911n.At911nCtrl;

// 写码
LZR.Pro.Rfid.At911n.At911nCtrl.prototype.write = function (bank/*as:Object*/, msg/*as:string*/) {
	this.adr.stat = 2;

	this.view.setWriteDoeo(false);
	this.view.setStopDoeo(true);

	var l = bank.getLength(true);
	var s = this.utStr.toUtf8Str(msg, l).toUpperCase();
	this.adr.write(bank.emNam.getKey(), this.byte2Word(bank.ws), s);
};
LZR.Pro.Rfid.At911n.At911nCtrl.prototype.write.lzrClass_ = LZR.Pro.Rfid.At911n.At911nCtrl;

// 处理扫码
LZR.Pro.Rfid.At911n.At911nCtrl.prototype.hdScan = function (ecpId/*as:string*/) {
	var t = this.crtTag({
		id: ecpId,
		ecp: ecpId,
		scanNum: 1
	});
};
LZR.Pro.Rfid.At911n.At911nCtrl.prototype.hdScan.lzrClass_ = LZR.Pro.Rfid.At911n.At911nCtrl;

// 处理写码
LZR.Pro.Rfid.At911n.At911nCtrl.prototype.hdWrite = function (ecpId/*as:string*/, bankNam/*as:string*/, msg/*as:string*/) {
	var pro = {
		id: ecpId
	};
	pro[bankNam] = msg;

	var t = this.crtTag(pro);

	this.view.flushTag(t);
	this.view.setStopDoeo(false);
};
LZR.Pro.Rfid.At911n.At911nCtrl.prototype.hdWrite.lzrClass_ = LZR.Pro.Rfid.At911n.At911nCtrl;

// 处理读码
LZR.Pro.Rfid.At911n.At911nCtrl.prototype.hdRead = function (ecpId/*as:string*/, bankNam/*as:string*/, msg/*as:string*/) {
	this.hdWrite(ecpId, bankNam, msg);
};
LZR.Pro.Rfid.At911n.At911nCtrl.prototype.hdRead.lzrClass_ = LZR.Pro.Rfid.At911n.At911nCtrl;

// 绑定视图
LZR.Pro.Rfid.At911n.At911nCtrl.prototype.bindView = function (view/*as:Object*/) {
	this.adr.setEventObj(this);
	this.adr.evt.scan.add(this.hdScan);
	this.adr.evt.write.add(this.hdWrite);
	this.adr.evt.read.add(this.hdRead);

	this.view.btnCtrl.evt.click.add(this.adr.utLzr.bind(this, this.hdClick));
};
LZR.Pro.Rfid.At911n.At911nCtrl.prototype.bindView.lzrClass_ = LZR.Pro.Rfid.At911n.At911nCtrl;

// 处理按钮点击
LZR.Pro.Rfid.At911n.At911nCtrl.prototype.hdClick = function (doeo/*as:LZR.HTML.Base.Doe*/) {
	var t = this.tags[0];
	if (!t) {
		t = new this.clsTag({hd_emTyp: this.tagTyp});
	}
	switch (doeo.id.get()) {
		case this.view.btnNams.scan:
			this.scanning();
			break;
		case this.view.btnNams.clean:
			this.clean();
			break;
		case this.view.btnNams.listStop:
			this.stop(false);
			break;
		case this.view.btnNams.ecpReadBtn:
			this.read(t.banks.ecp);
			break;
		case this.view.btnNams.tidReadBtn:
			this.read(t.banks.tid);
			break;
		case this.view.btnNams.usrReadBtn:
			this.read(t.banks.usr);
			break;
		case this.view.btnNams.bckReadBtn:
			this.read(t.banks.bck);
			break;
		case this.view.btnNams.stopBtn:
			this.stop(true);
			break;
		case this.view.btnNams.ecpWriteBtn:
			this.writeBefore(t.banks.ecp);
			break;
		case this.view.btnNams.usrWriteBtn:
			this.writeBefore(t.banks.usr);
			break;
		case this.view.btnNams.bckWriteBtn:
			this.writeBefore(t.banks.bck);
			break;
		case this.view.btnNams.writeCancleBtn:
			this.view.setWriteDoeo(false);
			break;
		case this.view.btnNams.writeOkBtn:
			this.write(this.writeBank, this.view.getWriteTxt());
			break;
	}
};
LZR.Pro.Rfid.At911n.At911nCtrl.prototype.hdClick.lzrClass_ = LZR.Pro.Rfid.At911n.At911nCtrl;


var r = {root:LZR,obj:{},At911n:LZR.Pro.Rfid.At911n.At911nCtrl};r.obj.tools=r;return r;
}));