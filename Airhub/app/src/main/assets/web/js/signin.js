function init() {
}

sig = {
    busy: "登录中，请稍候 ...",
    ajx: null,

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
                    this.ajx.open("POST", srvurl, true);
                    this.ajx.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
                    var msg = "srv=signin&username=";
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
                var o = JSON.parse(a.responseText);
                if (o.ok) {
                    sig.msg("登录成功");
                    rfdo.signin(o.user);
                    location.href = "user.html";
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
