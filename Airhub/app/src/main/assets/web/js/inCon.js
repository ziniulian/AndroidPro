ol.constant = {
	url: {
		list: "inList.html",
		inf: "inInfo.html?rid=",
		detail: "inDetail.html?rid=",
		scan: "inScan.html?rid=",
		dev: "devInfo.html?devid="
	},
	srv: {
		devs: "srv=getDevsBySlave&rid=",
		mark: "srv=markSlave&rid=",		// ...
		clr: "srv=clearSlave&rid=",		// ...
		crt: "srv=crtSlave&rid=",
		del: "srv=delOneSlave&rid=",
		// 清单
		crtR: "srv=crtMaster&rid=",
		delR: "srv=delMaster&rid=",
		allR: "srv=getAllUdMaster",
		// 信息
		infR: "srv=getMasterInfo&rid=",	// ...
		okR: "srv=masterOk&rid=",
		dev: "srv=getDevInfo&devid="
	},
	prefixR: "入库_"
};
