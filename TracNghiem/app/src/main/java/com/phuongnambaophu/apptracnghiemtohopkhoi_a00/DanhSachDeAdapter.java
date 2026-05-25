package com.phuongnambaophu.apptracnghiemtohopkhoi_a00;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;

public class DanhSachDeAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> listDe;

    public DanhSachDeAdapter(Context context, ArrayList<String> listDe) {
        this.context = context;
        this.listDe = listDe;
    }

    @Override
    public int getCount() {
        return listDe != null ? listDe.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return listDe.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        TextView tv = convertView.findViewById(android.R.id.text1);
        if (tv != null) {
            tv.setText(listDe.get(position));
            tv.setTextSize(18f);
            tv.setTypeface(null, Typeface.BOLD);
            tv.setTextColor(Color.parseColor("#202C39"));
            tv.setPadding(60, 45, 60, 45);
        }

        return convertView;
    }
}