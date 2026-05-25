package com.phuongnambaophu.apptracnghiemtohopkhoi_a00;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import io.noties.markwon.Markwon;
import java.util.ArrayList;

public class ChatAdapter extends BaseAdapter {

    private String mssv_Nam = "65132069";
    private String mssv_Phu = "65132716";
    private ArrayList<TinNhan> danhSachChat;

    public ChatAdapter(ArrayList<TinNhan> danhSachChat) {
        this.danhSachChat = danhSachChat;
    }

    @Override
    public int getCount() {
        return danhSachChat.size();
    }

    @Override
    public Object getItem(int position) {
        return danhSachChat.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TinNhan tinNhan = danhSachChat.get(position);
        Markwon markwon = Markwon.create(parent.getContext());

        convertView = View.inflate(parent.getContext(), android.R.layout.simple_list_item_1, null);
        TextView tv = convertView.findViewById(android.R.id.text1);
        tv.setPadding(30, 20, 30, 20);

        if (tinNhan.isNguoiDung()) {
            String noiDungText = "Bạn: " + tinNhan.getNoiDung();
            markwon.setMarkdown(tv, noiDungText);

            String textUpper = tinNhan.getNoiDung().toUpperCase();
            if (textUpper.contains("YÊU CẦU SMARTA GIẢI THÍCH")) {
                if (textUpper.contains("TOÁN")) {
                    convertView.setBackgroundColor(Color.parseColor("#1976D2"));
                    tv.setTextColor(Color.WHITE);
                } else if (textUpper.contains("LÝ") || textUpper.contains("VẬT LÝ")) {
                    convertView.setBackgroundColor(Color.parseColor("#D32F2F"));
                    tv.setTextColor(Color.WHITE);
                } else if (textUpper.contains("HÓA")) {
                    convertView.setBackgroundColor(Color.parseColor("#00695C"));
                    tv.setTextColor(Color.WHITE);
                } else {
                    convertView.setBackgroundColor(Color.TRANSPARENT);
                    tv.setTextColor(Color.BLACK);
                }
            } else {
                convertView.setBackgroundColor(Color.TRANSPARENT);
                tv.setTextColor(Color.BLACK);
            }
        } else {
            String noiDungText = "SmartA: " + tinNhan.getNoiDung();
            markwon.setMarkdown(tv, noiDungText);
            convertView.setBackgroundColor(Color.TRANSPARENT);
            tv.setTextColor(Color.BLACK);
        }

        return convertView;
    }
}