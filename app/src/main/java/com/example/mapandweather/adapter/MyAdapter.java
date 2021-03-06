package com.example.mapandweather.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.mapandweather.R;
import com.example.mapandweather.bean.AirQuelityBean;

import java.util.List;

public class MyAdapter extends BaseAdapter {
    private Context context;
    private List<AirQuelityBean> airQuelityBeanList;
    private AirQuelityBean airQuelityBean;
    public MyAdapter(Context context,List<AirQuelityBean> airQuelityBeanList){
        this.context=context;
        this.airQuelityBeanList=airQuelityBeanList;
    }
    @Override
    public int getCount() {
        return airQuelityBeanList.size();
    }

    @Override
    public Object getItem(int i) {
        return airQuelityBeanList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        airQuelityBean=airQuelityBeanList.get(i);
        if(view==null){
            view= LayoutInflater.from(context).inflate(R.layout.item_air_quality,viewGroup,false);
        }
        TextView date=view.findViewById(R.id.date);
        TextView AQI_num=view.findViewById(R.id.AQI_num);
        TextView quality_value=view.findViewById(R.id.quality_value);
        date.setText(airQuelityBean.getDate());
        AQI_num.setText(airQuelityBean.getAQI());
        quality_value.setText(airQuelityBean.getQuality());
        return view;
    }
}
