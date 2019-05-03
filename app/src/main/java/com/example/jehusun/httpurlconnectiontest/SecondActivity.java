package com.example.jehusun.httpurlconnectiontest;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.UUID;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SecondActivity extends BaseActivity {
    TextView responseText1;
    TextView responseText2;
    String money1="0";
    private static final String TAG ="SecondActivity";
    BluetoothAdapter mBluetoothAdapter;
    BluetoothDevice mBluetoothDevice; //连接远程设备
    BluetoothSocket bluetoothSocket; //客户端
//    private String blueAddress = "34:80:B3:D5:30:F6";//蓝牙模块的MAC地址
//    private String blueAddress = "AC:92:32:24:CA:E4";//蓝牙模块的MAC地址
private String blueAddress = "00:00:00:00:00:00";//蓝牙模块的MAC地址
    private static final UUID MY_UUID_SECURE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");//蓝牙串口服务相关UUID

    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mBrocastReceiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (action.equals(mBluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,mBluetoothAdapter.ERROR);
                switch (state){
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "onReceive: 蓝牙处于关闭状态");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "mBroadcastReceiver1:蓝牙正在关闭");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "mBroadcastReceiver1:蓝牙已经打开");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "mBroadcastReceiver1:蓝牙正在打开");
                        break;
                }
            }
        }
    };
    @Override
    protected void onDestroy(){
        Log.d(TAG, "onDestroy: called.");
        super.onDestroy();
        unregisterReceiver(mBrocastReceiver1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        final Intent intent=getIntent();
        final String name=intent.getStringExtra("param1");
        final String money=intent.getStringExtra("param2");
        /**
         * 注册按钮和文本框
         */
        Button tuichu=(Button)findViewById(R.id.tuichu);
        Button chaxun=(Button)findViewById(R.id.chaxun );
        Button btnONOFF=(Button)findViewById(R.id.btnONOFF);
        Button lianjielanya=(Button)findViewById(R.id.lianjielanya);
        Button chonzhi=(Button)findViewById(R.id.chonzhi);

        mBluetoothAdapter=BluetoothAdapter.getDefaultAdapter();

        responseText1=(TextView)findViewById(R.id.response1) ;
        responseText2=(TextView)findViewById(R.id.response2) ;
        responseText1.setText("用户："+name);
        responseText2.setText("钱包:"+"￥￥"+"元");
        //responseText2.setText(pass);
       // responseText2.setText("钱包："+money);
        /**
         * 监听按钮
         */
        tuichu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //退出整个程序
                ActivityCollector.finishAll();
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });

        chaxun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequestWithOKHttp();
            }

            private void sendRequestWithOKHttp() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            OkHttpClient client=new OkHttpClient();
                            RequestBody requestBody=new FormBody.Builder()
                                    .add("name",intent.getStringExtra("param1"))
                                    .build();
                            Request request=new Request.Builder()
                                    .url("http://47.103.4.106/QUERY.php")
                                    .post(requestBody)
                                    .build();
                            Response response=client.newCall(request).execute();
                            String responseData=response.body().string();
                            parseJSONWithJSONObject(responseData);
                            if(!responseData.equals("no data")){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        responseText2.setText("钱包:"+money1+"元");

                                    }
                                });
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                   private void parseJSONWithJSONObject(String jsonData) {
                       String money="";
                        try {

                            JSONArray jsonArray=new JSONArray(jsonData);
                            for (int i=0;i<jsonArray.length();i++){
                                JSONObject jsonObject=jsonArray.getJSONObject(i);
                                String id=jsonObject.getString("id");
                                String name=jsonObject.getString("name");
                                String pass=jsonObject.getString("pass");
                                money=jsonObject.getString("money");
                                money1=money;

                            }


                        }catch (Exception e){
                            e.printStackTrace();
                            Log.d("----Exception",e.getMessage());
                        }
                    }
                }).start();

            }
        });
        //打开关闭蓝牙
        btnONOFF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: 打开/关闭蓝牙");
                enableDisableBT();
            }
        });

    lianjielanya.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try{
                mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(blueAddress);
                bluetoothSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID_SECURE);
                Log.d("true","开始连接");
                bluetoothSocket.connect();
                Log.d("true","完成连接");
            }catch (IOException e){
                e.printStackTrace();
                Log.d(TAG, " 连接失败");
            }
        }
    });
    chonzhi.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                        OkHttpClient client=new OkHttpClient();
                        RequestBody requestBody=new FormBody.Builder()
                                .add("name",intent.getStringExtra("param1"))
                                .add("money", Integer.toString(Integer.parseInt(money1)+10))
                                .build();
                        Request request=new Request.Builder()
                                .url("http://47.103.4.106/CHARGE.php")
                                .post(requestBody)
                                .build();
                        Response response=client.newCall(request).execute();
                        Log.d(TAG, "充值成功");
                    }catch (Exception e){
                        e.printStackTrace();
                        Log.d(TAG, "充值失败");
                    }
                }
            }).start();
        }
    });
    }
    public void enableDisableBT(){
        if (mBluetoothAdapter==null){
            Log.d(TAG, "enableDisableBT: 没有蓝牙功能");
        }
        if (!mBluetoothAdapter.isEnabled()){
            //Log.d(TAG, "enableDisableBT: 正在打开蓝牙");
            Toast.makeText(SecondActivity.this, "正在打开蓝牙！",Toast.LENGTH_LONG).show();
            Intent enableBTIntent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBTIntent);
            //添加蓝牙设备
            IntentFilter BTIntent =new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBrocastReceiver1,BTIntent);
        }
        if (mBluetoothAdapter.isEnabled()){
            //Log.d(TAG, "enableDisableBT: 正在关闭蓝牙");
            Toast.makeText(SecondActivity.this, "正在关闭蓝牙！",Toast.LENGTH_LONG).show();
            mBluetoothAdapter.disable();

            IntentFilter BTIntent =new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBrocastReceiver1,BTIntent);
        }
    }
    


}
