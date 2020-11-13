package com.example.mapandweather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final String key="7c1fa4434715352337718a2e1768dc66";
    private MapView mapView;
    private BaiduMap baiduMap;
    private String cityName;
    private TextView city_name;
    private TextView AQI_num;
    private TextView quality_value;
    private RequestQueue requestQueue;
    private Button save_info;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        initView();
        requestQueue= Volley.newRequestQueue(this);
        mapView=findViewById(R.id.bmapView);
        baiduMap=mapView.getMap();
        baiduMap.setMapType(baiduMap.MAP_TYPE_NORMAL);
        baiduMap.setMaxAndMinZoomLevel(6,4);
        baiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Log.i("纬度",String.valueOf(latLng.latitude));
                Log.i("经度",String.valueOf(latLng.longitude));
            }
            @Override
            public void onMapPoiClick(MapPoi mapPoi) {
                cityName=mapPoi.getName();
                city_name.setText(cityName);
                Log.i("name",mapPoi.getName());
                String url="http://web.juhe.cn:8080/environment/air/cityair?city="+cityName+"&key="+key;
                JsonObjectRequest airQuality=new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("resultcode").equals("200")){
                                JSONArray jsonArray=response.getJSONArray("result");
                                JSONObject value=jsonArray.getJSONObject(0);
                                JSONObject quality=value.getJSONObject("citynow");
                                AQI_num.setText(quality.getString("AQI"));
                                quality_value.setText(quality.getString("quality"));
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
                requestQueue.add(airQuality);
            }
        });
    }

    private void initView() {
        city_name=findViewById(R.id.city_name);
        AQI_num=findViewById(R.id.AQI_num);
        quality_value=findViewById(R.id.quality_value);
        city_name.setOnClickListener(this);
        save_info=findViewById(R.id.save_info);
        save_info.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.city_name){
            Intent intent=new Intent(MainActivity.this,CityInfoAndAirQuality.class);
            intent.putExtra("cityName",cityName);
            startActivity(intent);
        }
        if(view.getId()==R.id.save_info){
            Intent intent=new Intent(MainActivity.this,SaveInfoShow.class);
            startActivity(intent);
        }
    }
}
