package com.example.mapandweather;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.mapandweather.adapter.SaveListViewAdapter;
import com.example.mapandweather.bean.AirQuelityBean;
import com.example.mapandweather.helper.MySqliteHelper;

import java.util.ArrayList;
import java.util.List;

public class SaveInfoShow extends AppCompatActivity implements View.OnClickListener {
    private ImageView back;
    private ListView lv_save;
    private AirQuelityBean airQuelityBean;
    private List<AirQuelityBean> airQuelityBeanList;
    private MySqliteHelper mySqliteHelper;
    private SQLiteDatabase sqLiteDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_info_show);
        initView();
        initData();
        initListView();
    }

    private void initListView() {
        SaveListViewAdapter saveListViewAdapter=new SaveListViewAdapter(SaveInfoShow.this,airQuelityBeanList);
        lv_save.setAdapter(saveListViewAdapter);
    }

    private void initData() {
        mySqliteHelper=new MySqliteHelper(SaveInfoShow.this,"cityAirQuality",null,1);
        sqLiteDatabase=mySqliteHelper.getReadableDatabase();
        Cursor cursor=sqLiteDatabase.query("cityAirQuality",null,null,null,null,null,null);
        if(cursor.getCount()!=0){
            airQuelityBeanList=new ArrayList<>();
            while(cursor.moveToNext()){
                airQuelityBean=new AirQuelityBean();
                airQuelityBean.setCityName(cursor.getString(cursor.getColumnIndex("cityName")));
                airQuelityBean.setAQI(cursor.getString(cursor.getColumnIndex("AQI")));
                airQuelityBean.setDate(cursor.getString(cursor.getColumnIndex("data")));
                airQuelityBean.setQuality(cursor.getString(cursor.getColumnIndex("airQuality")));
                airQuelityBeanList.add(airQuelityBean);
           }
            Log.i("size",String.valueOf(airQuelityBeanList.size()));
        }
    }

    private void initView() {
        back=findViewById(R.id.back);
        lv_save=findViewById(R.id.lv_save);
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view==back){
            finish();
        }
    }
}
