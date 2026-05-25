package com.phuongnambaophu.apptracnghiemtohopkhoi_a00;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

public class HistoryAdapter extends ArrayAdapter<HistoryItem> {
    public HistoryAdapter(Context context, ArrayList<HistoryItem> historyList) {
        super(context, 0, historyList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HistoryItem item = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_lich_su, parent, false);
        }

        View sideIndicator = convertView.findViewById(R.id.sideIndicator);
        TextView tvMonHoc = convertView.findViewById(R.id.tvMonHoc);
        TextView tvThoiGian = convertView.findViewById(R.id.tvThoiGian);
        TextView tvDiemSo = convertView.findViewById(R.id.tvDiemSo);
        ImageView btnHoiAI = convertView.findViewById(R.id.btnHoiAI);

        if (item != null) {
            tvMonHoc.setText(item.getMonHoc());
            tvThoiGian.setText(item.getNgayGio());
            tvDiemSo.setText(item.getDiemSo());

            String mon = item.getMonHoc().toUpperCase();
            if (mon.contains("TOÁN")) {
                sideIndicator.setBackgroundColor(Color.parseColor("#1976D2"));
            } else if (mon.contains("LÝ") || mon.contains("VẬT LÝ")) {
                sideIndicator.setBackgroundColor(Color.parseColor("#D32F2F"));
            } else if (mon.contains("HÓA")) {
                sideIndicator.setBackgroundColor(Color.parseColor("#00695C"));
            } else {
                sideIndicator.setBackgroundColor(Color.parseColor("#475569"));
            }

            btnHoiAI.setOnClickListener(v -> {
                Intent intentAI = new Intent(getContext(), ChatAIActivity.class);
                intentAI.putExtra("CAU_HOI_SAI", "Hãy đọc danh sách kết quả bài thi môn " + item.getMonHoc() + " sau đây. Hãy tìm các câu có chữ (Sai) hoặc ghi nhận làm sai để giải thích chi tiết phương pháp giải và đáp án đúng cho tôi:\n\n" + item.getChiTietBaiThi());
                intentAI.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(intentAI);
            });

            convertView.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), ChiTietLichSuActivity.class);
                intent.putExtra("title", item.getNgayGio());
                intent.putExtra("mon_hoc", item.getMonHoc());
                intent.putExtra("detail_content", item.getChiTietBaiThi());

                String rawContent = item.getChiTietBaiThi();
                if (rawContent != null) {
                    for (String line : rawContent.split("\n")) {
                        String lower = line.toLowerCase().trim();
                        if ((lower.contains("thời gian") || lower.contains("time")) && line.contains(":")) {
                            String timeValue = line.substring(line.indexOf(":") + 1).trim();
                            intent.putExtra("thoi_gian", timeValue);
                            break;
                        }
                    }
                }
                getContext().startActivity(intent);
            });
        }

        return convertView;
    }
}