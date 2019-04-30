package com.example.jehusun.httpurlconnectiontest;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SecondActivity extends BaseActivity {
    TextView responseText1;
    TextView responseText2;
    String money1="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        final Intent intent=getIntent();
        final String name=intent.getStringExtra("param1");
       final String money=intent.getStringExtra("param2");
        Button tuichu=(Button)findViewById(R.id.tuichu);
        Button chaxun=(Button)findViewById(R.id.chaxun );

        responseText1=(TextView)findViewById(R.id.response1) ;
        responseText2=(TextView)findViewById(R.id.response2) ;
        responseText1.setText("用户："+name);
        //responseText2.setText(pass);
       // responseText2.setText("钱包："+money);
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
                                        //Todo 如何调用方法中的局部变量
                                        responseText2.setText("钱包:"+money1);

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
        //Todo 蓝牙开锁


    }
}
