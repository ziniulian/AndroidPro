package test.ziniulian.invengo.com.util;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by ziniulian on 2016/11/30.
 */

public class Ajax implements Runnable {
    private String url;
    private Handler handler;

    public Ajax (String u, Handler h) {
        url = u;
        handler = h;
    }

    // 子线程
    @Override
    public void run() {
        // TODO
        // 在这里进行 http request.网络请求相关操作
        Message msg = new Message();
        Bundle data = new Bundle();
//        data.putString("value", getHuc(url));
        data.putString("value", getHc(url));
        msg.setData(data);
        handler.sendMessage(msg);
    }
/*
* 
*/
    // HttpClient get方法调用
    public String getHc (String u) {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet httpget = new HttpGet(u);
            HttpResponse response = client.execute(httpget);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                // 解析字符编码
                String charNam = response.getHeaders("Content-Type")[0].getValue();
//                Log.i("charNam", charNam);
                int ii = charNam.indexOf("charset");
                if (ii<0) {
                    charNam = "UTF-8";
                } else {
                    charNam = charNam.substring(ii+7).replaceFirst("=", "").trim();
                    ii = charNam.indexOf(";");
                    if (ii>=0) {
                        charNam = charNam.substring(0, ii).trim();
                    }
                }
//                Log.i("charNam", charNam);

                // 循环读取流
                BufferedReader br = new BufferedReader(new InputStreamReader(new BufferedHttpEntity(entity).getContent(), charNam));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                // 关闭流
                br.close();

                // 输出结果
                return sb.toString();
            } else {
                return "ERR!";
            }

//            CloseableHttpClient client = HttpClients.createDefault();
//            HttpGet get = new HttpGet(u);
//            CloseableHttpResponse res = client.execute(get);
//            HttpEntity ety = res.getEntity();
//            Log.i("req", EntityUtils.toString(ety));
        } catch (Exception e) {
            e.printStackTrace();
            return "ERR!";
        }
    }

    // HttpURLConnection get方法调用
    public String getHuc (String u) {
        try {
            // 创建URL对象
            URL urlobj = new URL(u);

            // 打开连接
            HttpURLConnection connection = (HttpURLConnection) urlobj.openConnection();

//            // post 连接设置
//            connection.setDoOutput(true);
//            connection.setDoInput(true);
//            connection.setRequestMethod("POST");
//            connection.setUseCaches(false);
//            // 设定传送的内容类型是可序列化的java对象    (如果不设此项,在传送序列化对象时,当WEB服务默认的不是这种类型时可能抛java.io.EOFException)
//            // application/x-javascript text/xml->xml数据 application/x-javascript->json对象 application/x-www-form-urlencoded->表单数据
//            connection.setRequestProperty("Content-type", "application/x-java-serialized-object");

            // 连接
            connection.connect();

//            // post 发送：
//            // 创建输入输出流,用于往连接里面输出携带的参数,(输出内容为?后面的内容)
//            DataOutputStream dataout = new DataOutputStream(connection.getOutputStream());
//
//            //URLEncoder.encode()方法  为字符串进行编码
//            String parm = "storeId=" + URLEncoder.encode("32", "utf-8");
//
//            // 将参数输出到连接
//            dataout.writeBytes(parm);
//
//            // 输出完成后刷新并关闭流
//            dataout.flush();
//            dataout.close(); // 重要且易忽略步骤 (关闭流,切记!) 

//            // 检查返回状态
//            Log.i("responseCode", Integer.toString(connection.getResponseCode()));

            // 获取输入流
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "GBK"));

            // 循环读取流
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            // 关闭流
            br.close();

            // 断开连接
            connection.disconnect();

            // 输出结果
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "ERR!";
        }
    }

}
