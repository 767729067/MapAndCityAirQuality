package com.example.mapandweather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.mapandweather.adapter.MyAdapter;
import com.example.mapandweather.bean.AirQuelityBean;
import com.example.mapandweather.helper.MySqliteHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class CityInfoAndAirQuality extends AppCompatActivity implements View.OnClickListener {
    private final String key="7c1fa4434715352337718a2e1768dc66";
    private ImageView back;//点击返回
    private TextView city_name;//显示该城市名称
    private Button save;//点击保存按钮
    private TextView city_introduce;//有关于城市的介绍
    private ListView air_quality_lv;//显示该城市近几天的空气状况
    private String cityName;
    private RequestQueue requestQueue;
    private List<AirQuelityBean> airQuelityBeanList;
    private AirQuelityBean airQuelityBean;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city_info_and_air_quality);
        initView();//初始化控件
        getIntentData();//获得上一个界面传递过来的数据
        city_name.setText(cityName);//设置城市名称
        requestQueue= Volley.newRequestQueue(CityInfoAndAirQuality.this);//初始化requestQueue
        String url="http://web.juhe.cn:8080/environment/air/cityair?city="+cityName+"&key="+key;
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if(response.getString("resultcode").equals("200")){
                        airQuelityBeanList=new ArrayList<>();
                        JSONArray jsonArray=response.getJSONArray("result");
                        JSONObject value=jsonArray.getJSONObject(0);
                        JSONObject quality=value.getJSONObject("citynow");
                        JSONObject lastTwoWeeks=value.getJSONObject("lastTwoWeeks");
                        airQuelityBean=new AirQuelityBean();
                        airQuelityBean.setAQI(quality.getString("AQI"));
                        airQuelityBean.setQuality(quality.getString("quality"));
                        airQuelityBean.setDate(quality.getString("date"));
                        airQuelityBeanList.add(airQuelityBean);
                        for(int i=1;i<=14;i++){
                            JSONObject jsonObject=lastTwoWeeks.getJSONObject(String.valueOf(i));
                            airQuelityBean=new AirQuelityBean();
                            airQuelityBean.setAQI(jsonObject.getString("AQI"));
                            airQuelityBean.setQuality(jsonObject.getString("quality"));
                            airQuelityBean.setDate(jsonObject.getString("date"));
                            airQuelityBeanList.add(airQuelityBean);
                        }
                        Log.i("size",String.valueOf(airQuelityBeanList.size()));
                        MyAdapter myAdapter=new MyAdapter(CityInfoAndAirQuality.this,airQuelityBeanList);
                        air_quality_lv.setAdapter(myAdapter);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    private void getIntentData() {
        Intent intent=getIntent();
        cityName=intent.getStringExtra("cityName");
    }
    private void initView() {
        back=findViewById(R.id.back);
        back.setOnClickListener(this);
        city_name=findViewById(R.id.city_name);
        save=findViewById(R.id.save);
        save.setOnClickListener(this);
        city_introduce=findViewById(R.id.city_introduce);
        air_quality_lv=findViewById(R.id.air_quality_lv);
    }
    private MySqliteHelper mySqliteHelper;
    private SQLiteDatabase sqLiteDatabase;
    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.back){
            finish();
        }
        if(view.getId()==R.id.save){
            //将数据保存到Sqlite
            if(airQuelityBeanList.size()!=0){
                mySqliteHelper=new MySqliteHelper(CityInfoAndAirQuality.this,"cityAirQuality",null,1);
                sqLiteDatabase=mySqliteHelper.getWritableDatabase();
                ContentValues values=new ContentValues();
                for(int i=0;i<airQuelityBeanList.size();i++) {
                    AirQuelityBean airQuelityBean=airQuelityBeanList.get(i);
                    values.put("cityName", cityName);
                    values.put("AQI",airQuelityBean.getAQI());
                    values.put("airQuality",airQuelityBean.getQuality());
                    values.put("data",airQuelityBean.getDate());
                    sqLiteDatabase.insert("cityAirQuality",null,values);
                }
                sqLiteDatabase.close();
                Toast.makeText(CityInfoAndAirQuality.this,"已保存",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
