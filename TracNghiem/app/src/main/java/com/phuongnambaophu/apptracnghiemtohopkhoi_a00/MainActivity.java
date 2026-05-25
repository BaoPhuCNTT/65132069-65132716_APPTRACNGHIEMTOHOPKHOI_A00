package com.phuongnambaophu.apptracnghiemtohopkhoi_a00;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private String mssv = "65132069";
    private View btnToan, btnLy, btnHoa;
    private View btnChatAI;
    private View btnBackMain;
    private View btnLichSu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        btnBackMain = findViewById(R.id.btnBackMain);
        if (btnBackMain == null) {
            btnBackMain = findViewByIdDynamic("btnBackMain");
        }
        if (btnBackMain != null) {
            btnBackMain.setOnClickListener(v -> finish());
        }

        btnToan = findViewByIdDynamic("btnToan");
        if (btnToan == null) btnToan = findViewByIdDynamic("btnToanHoc");

        btnLy = findViewByIdDynamic("btnLy");
        if (btnLy == null) btnLy = findViewByIdDynamic("btnVatLy");

        btnHoa = findViewByIdDynamic("btnHoa");
        if (btnHoa == null) btnHoa = findViewByIdDynamic("btnHoaHoc");

        btnLichSu = findViewByIdDynamic("btnLichSu");

        btnChatAI = findViewByIdDynamic("cardAI");
        if (btnChatAI == null) btnChatAI = findViewByIdDynamic("btnChatAI");
        if (btnChatAI == null) btnChatAI = findViewByIdDynamic("fabChat");
        if (btnChatAI == null) btnChatAI = findViewByIdDynamic("btnAI");

        if (btnToan != null) {
            btnToan.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, DanhSachDeActivity.class);
                intent.putExtra("MON_HOC", "Toan");
                startActivity(intent);
            });
        }

        if (btnLy != null) {
            btnLy.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, DanhSachDeActivity.class);
                intent.putExtra("MON_HOC", "Ly");
                startActivity(intent);
            });
        }

        if (btnHoa != null) {
            btnHoa.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, DanhSachDeActivity.class);
                intent.putExtra("MON_HOC", "Hoa");
                startActivity(intent);
            });
        }

        if (btnLichSu != null) {
            btnLichSu.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, LichSuLamBaiActivity.class);
                startActivity(intent);
            });
        }

        if (btnChatAI != null) {
            btnChatAI.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, ChatAIActivity.class);
                startActivity(intent);
            });
        }
    }

    private <T extends View> T findViewByIdDynamic(String idName) {
        int id = getResources().getIdentifier(idName, "id", getPackageName());
        return id != 0 ? findViewById(id) : null;
    }
}