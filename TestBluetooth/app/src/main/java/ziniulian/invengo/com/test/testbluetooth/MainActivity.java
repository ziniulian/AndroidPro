package ziniulian.invengo.com.test.testbluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    // 回调状态
    private static final int REQUEST_ENABLE_BT = 2;

    // 蓝牙服务器名
    private static final String NAME_INSECURE = "SPP";

    // 连接号
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // 蓝牙适配器
    private BluetoothAdapter ba = BluetoothAdapter.getDefaultAdapter();

    // 已配对的蓝牙驱动
    private Set<BluetoothDevice> ds = null;

    // 搜索设备用的广播
    private BroadcastReceiver br = null;

    // 服务端监听线程
    private AcceptThread at = null;

    // 客户端套接字
    private BluetoothSocket cs = null;

    // 客户端数据线程
    private DatCltThread ct = null;

    private TextView t = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        t = (TextView) findViewById(R.id.tt);

        if (ba == null) {
            Log.i("-- 1 --", "没有蓝牙");
        } else {
            Log.i("-- 2 --", "有蓝牙");
            Log.i("-- 3 --", String.valueOf(ba.isEnabled()));
            if (!ba.isEnabled()) {
                Log.i("-- 4 --", "蓝牙未开启，准备开启蓝牙 ...");
                Intent ei = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(ei, REQUEST_ENABLE_BT);
            } else {
                init();
            }
        }

        // 开启服务端
        // 可被扫描到
        // 关闭服务端
        // 扫描服务端
        // 开启客户端
        // 关闭客户端

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                Log.i("-- 5 --", "蓝牙已启动");
                init();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(br);
        closeClient();
        closeSrv();
        super.onDestroy();
    }

    // 初始化蓝牙
    private void init () {
        Log.i("-- 6 --", "蓝牙初始化");

        // 广播处理
        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                Log.i("-- 13 --", action);
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice d = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    Log.i("-- 14 --", d.getName());
                    if (d.getBondState() != BluetoothDevice.BOND_BONDED) {
                        Log.i("-- 15 --", d.getName() + " , " + d.getAddress());
                    }
                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                    Log.i("-- 16 --", "搜索结束");
                }
            }
        };

        // 当一个设备被发现时，调用广播
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(br, filter);

        // 当搜索结束后调用广播
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(br, filter);

        getOldDs();
//        openSrv();
//        ensureDiscoverable(300);
//        search();



/*
        try {
            openSrv();  // 开启服务端
            Thread.sleep(2000);
            closeSrv(); // 关闭服务端
        } catch (InterruptedException e) {
            Log.e("-- test --", "sleep() Err!", e);
        }
*/

/*
        try {
            search(); // 搜索蓝牙设备
            Thread.sleep(1000);
            nosearch(); // 停止搜索蓝牙设备
        } catch (InterruptedException e) {
            Log.e("-- test --", "sleep() Err!", e);
        }
*/

//        ensureDiscoverable(30); // 本机蓝牙在30秒内可被搜索到
//        getOldDs(); // 查找已配对的蓝牙驱动
    }

    // 查找已配对的蓝牙驱动
    private void getOldDs () {
        ds = ba.getBondedDevices();
        if (ds.size() > 0) {
            for (BluetoothDevice d : ds) {
                Log.i("-- 7 --", d.getName() + " , " + d.getAddress());
                openClient(d);
            }
        } else {
            Log.i("-- 8 --", "无匹配设备");
        }
    }

    // 使本机蓝牙在 s 秒内可被搜索
    private void ensureDiscoverable (int s) {
        if (ba.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Log.i("-- 9 --", "正在开启蓝牙配对");
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, s);
            startActivity(discoverableIntent);
        } else {
            Log.i("-- 10 --", "目前蓝牙可配对");
        }
    }

    // 搜索蓝牙设备
    private void search () {
        Log.i("-- 11 --", String.valueOf(ba.isDiscovering()));
        if (!ba.isDiscovering()) {
            ba.startDiscovery();
        }
    }

    // 停止搜索蓝牙设备
    private void nosearch () {
        Log.i("-- 12 --", String.valueOf(ba.isDiscovering()));
        if (ba.isDiscovering()) {
            ba.cancelDiscovery();
        }
    }

    // 服务端监听线程
    private class AcceptThread extends Thread {
        private BluetoothServerSocket bss;
        private Boolean isTrue;

        public AcceptThread (boolean secure) {
            BluetoothServerSocket tmp = null;
            try {
                Log.i("-- 17 --", "启动服务端监听");
                tmp = ba.listenUsingRfcommWithServiceRecord(NAME_INSECURE, MY_UUID);
            } catch (IOException e) {
                Log.e("-- 18 --", "listen() failed!", e);
            }
            this.bss = tmp;
            this.isTrue = secure;
        }

        @Override
        public void run() {
            BluetoothSocket socket = null;
            while (isTrue) {
                try {
                    Log.i("+-+-+", "run");
                    socket = bss.accept();
                    Log.i("+++++", "ok");
                } catch (IOException e) {
                    Log.e("-- 21 --", "accept() failed!", e);
                    break;
                }

                if (socket != null) {
                    Log.i("-- 22 --", "已监听到一个 socket 连接");
                    new DatSrvThread(socket).start();
//                    break;
                }
            }
        }

        public void cancel() {
            try {
                Log.i("-- 19 --", "关闭服务端监听");
                isTrue = false;
                bss.close();
            } catch (IOException e) {
                Log.e("-- 20 --", "close() of server failed!", e);
            }
        }
    }

    // 开启服务端
    private void openSrv () {
        if (at == null) {
            Log.i("-- 23 --", "开启服务端");
            at = new AcceptThread(true);
            at.start();
            Log.i("+++++", "start");
        }
    }

    // 关闭服务端
    private void closeSrv () {
        if (at != null) {
            Log.i("-- 24 --", "关闭服务端");
            at.cancel();
            at.interrupt();
            at = null;
        }
    }

    // 服务端数据处理
    private class DatSrvThread extends Thread {
        private BluetoothSocket socket = null;
        private InputStream is = null;
        private OutputStream os = null;

        public DatSrvThread (BluetoothSocket s) {
            this.socket = s;
            try {
                is = socket.getInputStream();
                os = socket.getOutputStream();
            } catch (IOException e) {
                Log.e("-- 25 --", "I/O Err!", e);
            }
        }

        @Override
        public void run () {
            try {
                Log.i("-- 28 --", "开始发送倒计时数据 ...");
                os.write("5".getBytes());
                Log.i("-- 28 --", "5");
                sleep(1000);
                os.write("4".getBytes());
                Log.i("-- 28 --", "4");
                sleep(1000);
                os.write("3".getBytes());
                Log.i("-- 28 --", "3");
                sleep(1000);
                os.write("2".getBytes());
                Log.i("-- 28 --", "2");
                sleep(1000);
                os.write("1".getBytes());
                Log.i("-- 28 --", "1");
                sleep(1000);
                os.write("Bye".getBytes());
                Log.i("-- 28 --", "Over");
                this.socket.close();
                Log.i("-- 29 --", "数据发送完毕，并断开。");
            } catch (IOException e) {
                Log.e("-- 26 --", "write Err!", e);
            } catch (InterruptedException e) {
                Log.e("-- 27 --", "sleep Err!", e);
            }
        }
    }

    // 开启客户端
    private void openClient (BluetoothDevice d) {
        if (cs == null) {
            try {
                cs = d.createRfcommSocketToServiceRecord(MY_UUID);
                Log.i("-- 30 --", "获取套接字成功");

                if (cs != null) {
                    try {
                        cs.connect();
                        Log.i("-- 31 --", "连接成功");
                        if (ct == null) {
                            ct = new DatCltThread(cs);
                            ct.start();
                        }
                    } catch (IOException e) {
                        Log.e("-- 32 --", "连接失败", e);
                        closeClient();
                    }
                }
            } catch (IOException e) {
                Log.e("-- 33 --", "获取套接字失败", e);
            }
        }
    }

    // 关闭客户端
    private void closeClient () {
        if (cs != null) {
            try {
                if (ct != null) {
                    ct.cancel();
                    ct.interrupt();
                    ct = null;
                }
                cs.close();
                Log.i("-- 34 --", "关闭连接成功");
            } catch (IOException e) {
                Log.e("-- 35 --", "关闭连接失败", e);
            }
            cs = null;
        }
    }

    // 客户端数据处理
    private class DatCltThread extends Thread {
        private BluetoothSocket socket = null;
        private InputStream is = null;
        private OutputStream os = null;
        private boolean isTrue = true;

        public DatCltThread (BluetoothSocket s) {
            this.socket = s;
            try {
                is = socket.getInputStream();
                os = socket.getOutputStream();
            } catch (IOException e) {
                Log.e("-- 36 --", "I/O Err!", e);
            }
        }

        @Override
        public void run () {
            int len = 0;
            byte[] buf = new byte[8];

            while (isTrue) {
                try {
                    len = is.read(buf);
                    Log.i("-- 39 --", "串口读取成功");
                    Log.i("-- 40 --", String.valueOf(len));
                    Log.i("-- 41 --", new String(buf));
                    if (len == 3) {
                        closeClient();
                        break;
                    }
                } catch (IOException e) {
                    Log.e("-- 38 --", "串口读取失败", e);
                    break;
                }
            }
        }

        public void cancel () {
            Log.i("-- 37 --", "关闭客户端数据线程");
            isTrue = false;
        }
    }

}
