ol.constant = {
	url: {
		list: "outList.html",
		inf: "outInfo.html?rid=",
		detail: "outDetail.html?rid=",
		scan: "outScan.html?rid=",
		dev: "devInfo.html?devid="
	},
	srv: {
		goods: "srv=getAllGoods&rid=",
		devs: "srv=getDevsByOut&rid=",
		mark: "srv=markBorrow&rid=",		// ...
		clr: "srv=clearBorrow&rid=",		// ...
		crt: "srv=crtOutSlaveFid&rid=",
		del: "srv=delOneSlaveOut&rid=",
		// 清单
		crtR: "srv=crtOut&rid=",	// ...
		delR: "srv=delOut&rid=",	// ...
		allR: "srv=getAllUdOut",
		// 信息
		infR: "srv=getOutInfo&rid=",		// ...
		okR: "srv=outOk&rid=",
		dev: "srv=getDevInfo&devid="
	},
	prefixR: "出库_"
};
