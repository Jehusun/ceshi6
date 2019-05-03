//版本控制2
package com.example.jehusun.httpurlconnectiontest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends BaseActivity {
    private EditText name;
    private EditText pass;

    TextView responseText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button sendRequest= (Button) findViewById(R.id.send_request);
        Button zhuce=(Button)findViewById(R.id.zhuce);
        responseText = (TextView) findViewById(R.id.response_text);
        name = (EditText) findViewById(R.id.name);
        pass = (EditText) findViewById(R.id.pass);

 //       sendRequest.setOnClickListener(this);
 //       zhuce.setOnClickListener(this);
        zhuce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userString=name.getText().toString().trim();
                String passString=pass.getText().toString().trim();
                if (userString.equals("")||passString.equals("")){
                    Toast.makeText(MainActivity.this,
                            "用户名或密码不能为空！",Toast.LENGTH_LONG).show();
                }
                else
                    zhuceRequestWithOKHttp();
            }
        });
        sendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userString=name.getText().toString().trim();
                String passString=pass.getText().toString().trim();
                if (userString.equals("")||passString.equals("")){
                    Toast.makeText(MainActivity.this,
                            "用户名或密码不能为空！",Toast.LENGTH_LONG).show();
                }
                else
                    sendRequestWithOKHttp();
            }
        });
    }

    private void zhuceRequestWithOKHttp() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    //post用法
                    RequestBody requestBody = new FormBody.Builder()
                            .add("name", name.getText().toString())
                            .add("pass", pass.getText().toString())
                            .build();
                    Request request = new Request.Builder()
                            .url("http://47.103.4.106/REGISTER.php") //这是正式时使用
                            // .url("http://47.103.4.106/zhuce.php")//这是测试时使用
                            .post(requestBody)
                            .build();
                    final Response response=client.newCall(request).execute();
                    String responseData=response.body().string();
                    if(response.code()==200){   //不知道啥意思
                        //Todo 进不去
                        if(responseData.equals("用户名已存在")){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this,"用户名已存在",Toast.LENGTH_SHORT).show();
                                   /* Intent intent=new Intent(MainActivity.this,SecondActivity.class);
                                    startActivity(intent);*/
                                }
                            });
                        }
                    }
//                   parseJSONWithJSONObject(responseData);
                    if (responseData.equals("ok")){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this,"注册成功，请登陆",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void sendRequestWithOKHttp(){
        new Thread(new Runnable() {
            @Override
            public void run() {
              //  HttpURLConnection connection = null;
               // BufferedReader reader = null;
                try {
                    OkHttpClient client=new OkHttpClient();
                    //post用法
                    RequestBody requestBody=new FormBody.Builder()
                            .add("name",name.getText().toString())
                            .add("pass",pass.getText().toString())
                            .build();
                    Request request=new Request.Builder()
                            .url("http://47.103.4.106/LOGIN.php")
                            .post(requestBody)
                            .build();
                    final Response response=client.newCall(request).execute();
                    final String responseData=response.body().string();
                    parseJSONWithJSONObject(responseData);
                    if(response.code()==200){   //不知道啥意思
                        if(responseData.equals("fail")){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                   Toast.makeText(MainActivity.this,"用户名或密码错误",Toast.LENGTH_SHORT).show();
                                   /* Intent intent=new Intent(MainActivity.this,SecondActivity.class);
                                    startActivity(intent);*/
                                }
                            });
                    }
                    }
//                   parseJSONWithJSONObject(responseData);
                    if (responseData.equals("ok")){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent=new Intent(MainActivity.this,SecondActivity.class);
                                //传值用户名name与pass
                                intent.putExtra("param1",name.getText().toString().trim());
                                //intent.putExtra("param2",pass.getText().toString().trim());
                                //ToDo  余额如何显示出来


                                startActivity(intent);
                            }
                        });
                    }
                    //showResponse(responseData);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        }).start();

            }
            //json解析
    private void parseJSONWithJSONObject(String jsonData){
        try{
            JSONArray jsonArray=new JSONArray(jsonData);
            for (int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                String id=jsonObject.getString("id");
                String name=jsonObject.getString("name");
                String pass=jsonObject.getString("pass");
                String money=jsonObject.getString("money");
                Log.d("MainActivity","id is"+id);
                Log.d("MainActivity","name is"+name);
                Log.d("MainActivity","pass is"+pass);
                Log.d("MainActivity","money is"+money);
            }
        }catch (Exception e){
        e.printStackTrace();
        }
    }

}


