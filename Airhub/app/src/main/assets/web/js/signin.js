function init() {
}

sig = {
    busy: "登录中，请稍候 ...",
    ajx: null,
    // url: "http://127.0.0.1/testAjax",
    // url: "http://192.169.0.12:8080/jeesite/a/login?__ajax=true",
    url: "http://192.169.0.12:8080/jeesite/mobile/mobileLogin",

    signin: function () {
        if (note.innerHTML !== this.busy) {
            if (!user.value) {
                note.innerHTML = "用户名不能为空";
                return;
            } else if (!pwd.value) {
                note.innerHTML = "密码不能为空";
                return;
            } else {
                if (this.getAjax()) {
                    note.innerHTML = this.busy;
                    this.ajx.open("POST", this.url, true);
                    this.ajx.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
                    var msg = "mobileLogin=true&username=";
                    msg += user.value;
                    msg += "&password=";
                    msg += pwd.value;
                    this.ajx.send(msg);
                } else {
                    sig.msg("");
                }
            }
        }
    },

    getAjax: function ()/*as:Object*/ {
        if (!this.ajx) {
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

            if (xmlHttp) {
                this.ajx = xmlHttp;
                this.ajx.onreadystatechange = this.onRsp;
            } else {
                return false;
            }
        }
        return true;
    },

    onRsp: function (evt) {
        a = evt.target;
        if ( a.readyState == 4 ) {
            if (a.status === 200) {
                if (a.responseText === "ok") {
                    sig.msg("登录成功");
                } else {
                    sig.msg("用户名或密码错误");
                }
            } else {
                sig.msg("连接失败");
            }
        }
    },

    msg: function (msg) {
        note.innerHTML = msg;
        user.value = "";
        pwd.value = "";
    }
};
