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
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class ChonDeActivity extends AppCompatActivity {

    private TextView txtTitleChonDe;
    private ListView lvChonDe;
    private ImageView btnBackChonDe;
    private View headerChonDe;
    private ArrayList<String> listTenDe;
    private ArrayList<String> listMaDe;
    private ArrayAdapter<String> adapter;
    private String monHoc = "Toan";
    private String chuDe = "chu_de_01";
    private String tenChuDe = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chon_de);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        txtTitleChonDe = findViewById(R.id.txtTitleChonDe);
        lvChonDe = findViewById(R.id.lvChonDe);
        btnBackChonDe = findViewById(R.id.btnBackChonDe);
        headerChonDe = findViewById(R.id.headerChonDe);

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra("MON_HOC")) monHoc = intent.getStringExtra("MON_HOC");
            if (intent.hasExtra("CHU_DE")) chuDe = intent.getStringExtra("CHU_DE");
            if (intent.hasExtra("TEN_CHU_DE")) tenChuDe = intent.getStringExtra("TEN_CHU_DE");
        }

        txtTitleChonDe.setText("CHỌN ĐỀ THI - " + tenChuDe.toUpperCase());

        if (monHoc.equalsIgnoreCase("Toan")) {
            headerChonDe.setBackgroundColor(Color.parseColor("#1976D2"));
        } else if (monHoc.equalsIgnoreCase("Ly")) {
            headerChonDe.setBackgroundColor(Color.parseColor("#C62828"));
        } else if (monHoc.equalsIgnoreCase("Hoa")) {
            headerChonDe.setBackgroundColor(Color.parseColor("#00695C"));
        }

        btnBackChonDe.setOnClickListener(v -> finish());

        listTenDe = new ArrayList<>();
        listMaDe = new ArrayList<>();

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listTenDe) {
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

        lvChonDe.setAdapter(adapter);

        loadDanhSachDe();

        lvChonDe.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intentQuiz = new Intent(ChonDeActivity.this, QuizActivity.class);
                intentQuiz.putExtra("MON_HOC", monHoc);
                intentQuiz.putExtra("CHU_DE", chuDe);
                intentQuiz.putExtra("TEN_CHU_DE", tenChuDe);
                intentQuiz.putExtra("MA_DE", listMaDe.get(position));
                startActivity(intentQuiz);
            }
        });

        AIManager.attachAIButton(this);
    }

    private void loadDanhSachDe() {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance("https://apptracnghiemtohopkhoi-a00-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference(monHoc).child(chuDe).child("danh_sach_de");

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listTenDe.clear();
                listMaDe.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        String key = data.getKey();
                        if (key != null) {
                            listMaDe.add(key);
                            listTenDe.add(key.replace("de_", "Đề số "));
                        }
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(ChonDeActivity.this, "Chủ đề này hiện chưa có đề thi!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChonDeActivity.this, "Lỗi kết nối Firebase", Toast.LENGTH_SHORT).show();
            }
        });
    }
}