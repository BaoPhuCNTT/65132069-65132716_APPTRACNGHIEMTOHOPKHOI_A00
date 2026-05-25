package com.phuongnambaophu.apptracnghiemtohopkhoi_a00;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class DanhSachDeActivity extends AppCompatActivity {

    private View headerDanhSach;
    private TextView txtTitleDanhSach;
    private ListView lvDanhSachDe;
    private ImageView btnBackDanhSach;
    private ArrayList<String> listDe;
    private ArrayList<String> listKeys;
    private ArrayList<String> listTopicNames;
    private ArrayAdapter<String> adapter;
    private String monHoc = "Toan";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_danh_sach_de);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        headerDanhSach = findViewById(R.id.headerDanhSach);
        txtTitleDanhSach = findViewById(R.id.txtTitleDanhSach);
        lvDanhSachDe = findViewById(R.id.lvDanhSachDe);
        btnBackDanhSach = findViewById(R.id.btnBackDanhSach);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("MON_HOC")) {
            String passedMonHoc = intent.getStringExtra("MON_HOC");
            if (passedMonHoc != null && !passedMonHoc.isEmpty()) {
                monHoc = passedMonHoc;
            }
        }

        String[] toanTopics = {"Cấp số", "Hàm số và Đồ thị", "Hình học Oxyz", "Khối tròn xoay", "Mũ và Logarit", "Nguyên hàm - Tích phân", "Phương trình - Bất phương trình", "Số phức", "Thể tích Đa diện", "Tổ hợp - Xác suất", "Đạo hàm", "Giới hạn", "Đại số tổ hợp", "Hình học phẳng", "Lượng giác", "Véc tơ", "Phép dời hình", "Nhị thức Niu-tơn", "Xác suất thực tế", "Toán thực tế"};
        String[] lyTopics = {"Dao động cơ", "Sóng cơ và Sóng âm", "Dòng điện xoay chiều", "Điện từ 11", "Dòng điện không đổi", "Động học 10", "Hạt nhân nguyên tử", "Lượng tử ánh sáng", "Mạch dao động", "Sóng ánh sáng", "Tĩnh điện", "Quang hình học", "Cơ học vật rắn", "Từ trường", "Cảm ứng điện từ", "Động lực học", "Tĩnh học", "Chất khí", "Nhiệt động lực học", "Vật lý hạt"};
        String[] hoaTopics = {"Este - Lipit", "Cacbohidrat", "Amin, Amino Axit và Protein", "Polime và Vật liệu polime", "Đại cương kim loại", "Hóa phân tích", "Kim loại Kiềm, Kiềm thổ, Nhôm", "Nitơ - Photpho", "Phi kim - Halogen", "Sắt và Crom", "Tổng hợp vô cơ", "Tổng hợp hữu cơ", "Sự điện li", "Ancol - Phenol", "Anđehit - Axit cacboxylic", "Đại cương hữu cơ", "Hiđrocacbon", "Oxi - Lưu huỳnh", "Cacbon - Silic", "Tốc độ phản ứng"};
        String[] currentTopics = null;

        if (monHoc.equalsIgnoreCase("Toan")) {
            txtTitleDanhSach.setText("Danh mục chủ đề môn Toán học");
            headerDanhSach.setBackgroundColor(Color.parseColor("#1976D2"));
            currentTopics = toanTopics;
        } else if (monHoc.equalsIgnoreCase("Ly")) {
            txtTitleDanhSach.setText("Danh mục chủ đề môn Vật lý");
            headerDanhSach.setBackgroundColor(Color.parseColor("#C62828"));
            currentTopics = lyTopics;
        } else if (monHoc.equalsIgnoreCase("Hoa")) {
            txtTitleDanhSach.setText("Danh mục chủ đề môn Hóa học");
            headerDanhSach.setBackgroundColor(Color.parseColor("#00695C"));
            currentTopics = hoaTopics;
        } else {
            txtTitleDanhSach.setText("Danh mục chủ đề môn " + monHoc);
            headerDanhSach.setBackgroundColor(Color.parseColor("#202C39"));
        }

        listDe = new ArrayList<>();
        listKeys = new ArrayList<>();
        listTopicNames = new ArrayList<>();

        if (currentTopics != null) {
            for (int i = 0; i < currentTopics.length; i++) {
                String indexStr = String.format("%02d", i + 1);
                listDe.add("Chủ đề " + indexStr + " - " + currentTopics[i]);
                listKeys.add("chu_de_" + indexStr);
                listTopicNames.add(currentTopics[i]);
            }
        }

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listDe) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView tv = view.findViewById(android.R.id.text1);
                if (tv != null) {
                    tv.setTextSize(18);
                    tv.setTypeface(null, Typeface.BOLD);
                    tv.setTextColor(Color.parseColor("#202C39"));
                    tv.setPadding(50, 45, 50, 45);
                }
                return view;
            }
        };

        lvDanhSachDe.setAdapter(adapter);

        lvDanhSachDe.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intentChonDe = new Intent(DanhSachDeActivity.this, ChonDeActivity.class);
                intentChonDe.putExtra("MON_HOC", monHoc);
                intentChonDe.putExtra("CHU_DE", listKeys.get(position));
                intentChonDe.putExtra("TEN_CHU_DE", listTopicNames.get(position));
                startActivity(intentChonDe);
            }
        });

        btnBackDanhSach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        AIManager.attachAIButton(this);
    }
}