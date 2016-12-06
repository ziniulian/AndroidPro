package test.ziniulian.invengo.com.test001;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import test.ziniulian.invengo.com.util.Ajax;

public class MainActivity extends AppCompatActivity {
    private TextView tv;
    private AjaxCallBack cb;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        setContentView(R.layout.amain01);
        setContentView(R.layout.amain02);

        tv = (TextView)findViewById(R.id.tv);
        cb = new AjaxCallBack();
        context = getApplicationContext();

        Button btn;
        btn = (Button)findViewById(R.id.btn);
        btn.setOnClickListener(new BtnClick("http://hq.sinajs.cn/list=s_sz002161"));

        // 添加滚动条
        tv.setMovementMethod(ScrollingMovementMethod.getInstance());

        // 天气查询按钮
        btn = (Button)findViewById(R.id.btnWt);
        btn.setOnClickListener(new BtnClick("http://www.weather.com.cn/data/sk/101160101.html"));

        // 清空按钮
        btn = (Button)findViewById(R.id.btnClr);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv.setText("");
            }
        });
    }

    // 按钮点击
    private class BtnClick implements View.OnClickListener {
        private String url;

        public BtnClick (String u) {
            url = u;
        }

        @Override
        public void onClick(View arg0) {
            new Thread(new Ajax(url, cb)).start();
        }
    }

    // Ajax 回调处理
    public class AjaxCallBack extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String r = msg.getData().getString("value");

//            输出测试
//            Log.i("mylog", "请求结果为-->" + r);
            if (!r.equals("ERR!")) {
                tv.append(r);

                // 写入文件（内存中）
                OutputStream out=null;
                try {
                    SimpleDateFormat df = new SimpleDateFormat("------- yyyy/MM/dd HH:mm:ss -----\n");
                    out = context.openFileOutput("TestLog.txt", Context.MODE_APPEND);
                    out.write(df.format(new Date()).getBytes());
                    out.write(r.getBytes());
                    out.write("\n------- END ---------- \n\n".getBytes());
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (out != null) {
                        try {
                            out.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                tv.append("...");
            }

//            线程安全测试
//            Integer i;
//            for (i=0; i<100; i++) {
//                tv.append(i.toString());
//            }

            tv.append("\n\n");

            // 滚动条自动定位到最底部
            int offset = tv.getLineCount()*tv.getLineHeight();
            if (offset > tv.getHeight()) {
                tv.scrollTo(0, offset - tv.getHeight());
            }
        }
    }
}
