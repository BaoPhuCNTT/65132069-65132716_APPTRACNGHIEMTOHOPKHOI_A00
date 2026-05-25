package com.phuongnambaophu.apptracnghiemtohopkhoi_a00;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class LichSuLamBaiActivity extends AppCompatActivity {

    private ListView lvLichSu;
    private ImageView btnBack;
    private DatabaseHelper dbHelper;
    private ArrayList<HistoryItem> listHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lich_su_lam_bai);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        lvLichSu = findViewById(R.id.lvLichSu);
        btnBack = findViewById(R.id.btnBack);
        dbHelper = new DatabaseHelper(this);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        listHistory = dbHelper.getAllHistory();

        HistoryAdapter adapter = new HistoryAdapter(this, listHistory);
        lvLichSu.setAdapter(adapter);

        lvLichSu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HistoryItem selectedItem = listHistory.get(position);
                Intent intent = new Intent(LichSuLamBaiActivity.this, ChiTietLichSuActivity.class);

                intent.putExtra("title", selectedItem.getMonHoc() + " - " + selectedItem.getNgayGio());
                intent.putExtra("detail_content", selectedItem.getChiTietBaiThi());
                intent.putExtra("mon_hoc", selectedItem.getMonHoc());

                startActivity(intent);
            }
        });
    }
}