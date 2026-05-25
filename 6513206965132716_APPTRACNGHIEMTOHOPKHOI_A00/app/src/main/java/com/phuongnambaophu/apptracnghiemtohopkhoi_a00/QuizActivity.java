package com.phuongnambaophu.apptracnghiemtohopkhoi_a00;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class QuizActivity extends AppCompatActivity {

    private String mssv = "65132069";
    private TextView tvCauHoiNum, tvCauHoiNoiDung, txtSubjectTitle;
    private MaterialCardView btnMenuGrid, cardQuizHeader;
    private Button btnDapAnA, btnDapAnB, btnDapAnC, btnDapAnD, btnBack, btnNext, btnGiaiChiTiet;
    private ImageView btnThoat;
    private DatabaseReference mDatabase;
    private ArrayList<CauHoi> danhSachCauHoi;
    private int[] trangThaiCauHoi;
    private String[] cauTraLoiCuaToi;
    private int indexHienTai = 0;
    private int diemSo = 0;
    private String monHoc = "Toan";
    private String chuDe = "chu_de_01";
    private String tenChuDe = "";
    private String maDe = "de_01";
    private int themeColor;
    private int thoiGianGiay = 0;
    private Handler timerHandler = new Handler();
    private Runnable timerRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        tvCauHoiNum = findViewById(R.id.txtQuestionNumber);
        tvCauHoiNoiDung = findViewById(R.id.txtQuestionContent);
        txtSubjectTitle = findViewById(R.id.txtSubjectTitle);
        btnDapAnA = findViewById(R.id.btnAnswerA);
        btnDapAnB = findViewById(R.id.btnAnswerB);
        btnDapAnC = findViewById(R.id.btnAnswerC);
        btnDapAnD = findViewById(R.id.btnAnswerD);
        btnBack = findViewById(R.id.btnBack);
        btnNext = findViewById(R.id.btnNext);
        btnThoat = findViewById(R.id.btnThoat);
        btnMenuGrid = findViewById(R.id.btnMenuGrid);
        cardQuizHeader = findViewById(R.id.cardQuizHeader);
        btnGiaiChiTiet = findViewById(R.id.btnGiaiChiTiet);

        tvCauHoiNum.setTypeface(null, Typeface.BOLD);
        tvCauHoiNum.setTextColor(Color.RED);

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra("MON_HOC")) monHoc = intent.getStringExtra("MON_HOC");
            if (intent.hasExtra("CHU_DE")) chuDe = intent.getStringExtra("CHU_DE");
            if (intent.hasExtra("MA_DE")) maDe = intent.getStringExtra("MA_DE");
            if (intent.hasExtra("TEN_CHU_DE")) {
                tenChuDe = intent.getStringExtra("TEN_CHU_DE");
            } else {
                tenChuDe = chuDe;
            }
        }

        String tenMon = "TOÁN";
        themeColor = Color.parseColor("#1976D2");
        if (monHoc.equalsIgnoreCase("Ly")) {
            tenMon = "VẬT LÝ";
            themeColor = Color.parseColor("#D32F2F");
        } else if (monHoc.equalsIgnoreCase("Hoa")) {
            tenMon = "HÓA HỌC";
            themeColor = Color.parseColor("#00695C");
        }

        txtSubjectTitle.setText(String.format("MÔN %s - %s", tenMon, tenChuDe.toUpperCase()));

        cardQuizHeader.setCardBackgroundColor(themeColor);
        btnMenuGrid.setCardBackgroundColor(themeColor);
        btnNext.setBackgroundColor(themeColor);

        danhSachCauHoi = new ArrayList<>();

        mDatabase = FirebaseDatabase.getInstance("https://apptracnghiemtohopkhoi-a00-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .getReference(monHoc).child(chuDe).child("danh_sach_de").child(maDe);

        timerRunnable = new Runnable() {
            @Override
            public void run() {
                thoiGianGiay++;
                if (danhSachCauHoi != null && !danhSachCauHoi.isEmpty()) {
                    int p = thoiGianGiay / 60;
                    int g = thoiGianGiay % 60;
                    tvCauHoiNum.setText(String.format(Locale.getDefault(), "Câu hỏi %d/%d - %02d:%02d", (indexHienTai + 1), danhSachCauHoi.size(), p, g));
                }
                timerHandler.postDelayed(this, 1000);
            }
        };

        docDuLieuFirebase();

        btnMenuGrid.setOnClickListener(v -> hienThiMenuGrid());
        btnThoat.setOnClickListener(v -> finish());

        btnBack.setOnClickListener(v -> {
            if (indexHienTai > 0) {
                indexHienTai--;
                hienThiCauHoi();
            }
        });

        btnNext.setOnClickListener(v -> {
            kiemTraChuyenCau();
        });

        AIManager.attachAIButton(this);
    }

    private void docDuLieuFirebase() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                danhSachCauHoi.clear();
                if (snapshot.exists()) {
                    try {
                        for (DataSnapshot data : snapshot.getChildren()) {
                            String cauHoiText = data.child("cau_hoi").getValue(String.class);
                            String daA = data.child("dap_an_a").getValue(String.class);
                            String daB = data.child("dap_an_b").getValue(String.class);
                            String daC = data.child("dap_an_c").getValue(String.class);
                            String daD = data.child("dap_an_d").getValue(String.class);
                            String daDung = data.child("dap_an_dung").getValue(String.class);

                            if (cauHoiText != null) {
                                CauHoi ch = new CauHoi(cauHoiText, daA, daB, daC, daD, daDung);
                                danhSachCauHoi.add(ch);
                            }
                        }
                        if (!danhSachCauHoi.isEmpty()) {
                            trangThaiCauHoi = new int[danhSachCauHoi.size()];
                            cauTraLoiCuaToi = new String[danhSachCauHoi.size()];
                            hienThiCauHoi();
                            timerHandler.removeCallbacks(timerRunnable);
                            timerHandler.postDelayed(timerRunnable, 1000);
                        } else {
                            tvCauHoiNoiDung.setText("Chủ đề này hiện chưa có câu hỏi nào!");
                        }
                    } catch (Exception e) {
                        tvCauHoiNoiDung.setText("Lỗi cấu trúc dữ liệu! Kiểm tra lại Firebase");
                    }
                } else {
                    tvCauHoiNoiDung.setText("Không tìm thấy chủ đề hoặc dữ liệu trống trên Firebase!");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                tvCauHoiNoiDung.setText("Lỗi mạng: " + error.getMessage());
            }
        });
    }

    private void hienThiCauHoi() {
        if (danhSachCauHoi == null || danhSachCauHoi.isEmpty() || indexHienTai >= danhSachCauHoi.size()) return;

        CauHoi current = danhSachCauHoi.get(indexHienTai);
        int p = thoiGianGiay / 60;
        int g = thoiGianGiay % 60;
        tvCauHoiNum.setText(String.format(Locale.getDefault(), "Câu hỏi %d/%d - %02d:%02d", (indexHienTai + 1), danhSachCauHoi.size(), p, g));
        tvCauHoiNum.setTextColor(Color.RED);
        tvCauHoiNum.setTypeface(null, Typeface.BOLD);

        String noiDungCauHoi = "";
        if (current.getCau_hoi() != null) {
            noiDungCauHoi = current.getCau_hoi().replaceFirst("^\\(Câu\\s*\\d+\\)\\s*\\[.*?\\]\\s*", "");
        }
        tvCauHoiNoiDung.setText(noiDungCauHoi);

        List<String> options = new ArrayList<>();
        options.add(current.getDap_an_a() != null ? current.getDap_an_a() : "A");
        options.add(current.getDap_an_b() != null ? current.getDap_an_b() : "B");
        options.add(current.getDap_an_c() != null ? current.getDap_an_c() : "C");
        options.add(current.getDap_an_d() != null ? current.getDap_an_d() : "D");

        if (indexHienTai == danhSachCauHoi.size() - 1) {
            btnNext.setText("NỘP BÀI");
        } else {
            btnNext.setText("NEXT");
        }

        if(trangThaiCauHoi[indexHienTai] != 0) {
            btnGiaiChiTiet.setVisibility(View.VISIBLE);
        } else {
            btnGiaiChiTiet.setVisibility(View.GONE);
        }

        String correct = current.getDap_an_dung();
        String banChon = cauTraLoiCuaToi[indexHienTai] != null ? cauTraLoiCuaToi[indexHienTai] : "Chưa trả lời";

        btnGiaiChiTiet.setOnClickListener(v -> moGiaiThichAI(current, banChon, correct));

        Button[] btns = {btnDapAnA, btnDapAnB, btnDapAnC, btnDapAnD};
        for(int i = 0; i < 4; i++) {
            btns[i].setText(String.format("%c. %s", "ABCD".charAt(i), options.get(i)));
            btns[i].setBackgroundColor(Color.WHITE);
            btns[i].setEnabled(true);

            if(trangThaiCauHoi[indexHienTai] != 0) {
                if(options.get(i).equals(correct)) {
                    btns[i].setBackgroundColor(Color.parseColor("#4CAF50"));
                } else if(trangThaiCauHoi[indexHienTai] == 2 && options.get(i).equals(cauTraLoiCuaToi[indexHienTai])) {
                    btns[i].setBackgroundColor(Color.parseColor("#F44336"));
                }
                btns[i].setOnClickListener(null);
            } else {
                btns[i].setOnClickListener(v -> kiemTraDapAn((Button) v));
            }
        }
    }

    private void kiemTraDapAn(Button btn) {
        if (danhSachCauHoi == null || danhSachCauHoi.isEmpty()) return;

        CauHoi current = danhSachCauHoi.get(indexHienTai);
        String correct = current.getDap_an_dung();

        String selected = btn.getText().toString();
        if (selected.length() > 3) {
            selected = selected.substring(3);
        }

        cauTraLoiCuaToi[indexHienTai] = selected;

        if (selected.equals(correct)) {
            btn.setBackgroundColor(Color.parseColor("#4CAF50"));
            trangThaiCauHoi[indexHienTai] = 1;
            diemSo++;
        } else {
            btn.setBackgroundColor(Color.parseColor("#F44336"));
            trangThaiCauHoi[indexHienTai] = 2;
            highlightCorrect(correct);
        }

        for(Button b : new Button[]{btnDapAnA, btnDapAnB, btnDapAnC, btnDapAnD}) {
            b.setOnClickListener(null);
        }

        btnGiaiChiTiet.setVisibility(View.VISIBLE);
        btnGiaiChiTiet.setOnClickListener(v -> moGiaiThichAI(current, cauTraLoiCuaToi[indexHienTai], correct));
    }

    private void moGiaiThichAI(CauHoi current, String banChon, String dapAnDung) {
        String trangThai = (banChon != null && banChon.equals(dapAnDung)) ? "(Đúng)" : "(Sai)";
        String dataSend = current.getCau_hoi() + "\n-> Đáp án bạn chọn: " + banChon + " " + trangThai + "\n-> Đáp án đúng: " + dapAnDung + "\n---------------------\n";

        Intent intent = new Intent(QuizActivity.this, ChatAIActivity.class);
        intent.putExtra("CAU_HOI_SAI", dataSend);
        startActivity(intent);
    }

    private void kiemTraChuyenCau() {
        if (indexHienTai < danhSachCauHoi.size() - 1) {
            indexHienTai++;
            hienThiCauHoi();
        } else {
            boolean allAnswered = true;
            int firstUnanswered = -1;

            for (int i = 0; i < trangThaiCauHoi.length; i++) {
                if (trangThaiCauHoi[i] == 0) {
                    allAnswered = false;
                    if (firstUnanswered == -1) {
                        firstUnanswered = i;
                    }
                }
            }

            if (allAnswered) {
                xacNhanNopBai();
            } else {
                indexHienTai = firstUnanswered;
                hienThiCauHoi();
                Toast.makeText(QuizActivity.this, "Hệ thống tự động quay lại câu chưa làm!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void highlightCorrect(String correct) {
        for(Button b : new Button[]{btnDapAnA, btnDapAnB, btnDapAnC, btnDapAnD}) {
            String btnText = b.getText().toString();
            if(btnText.length() > 3 && btnText.substring(3).equals(correct)) {
                b.setBackgroundColor(Color.parseColor("#4CAF50"));
            }
        }
    }

    private void xacNhanNopBai() {
        Dialog dialog = new Dialog(this);
        dialog.setCancelable(false);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        MaterialCardView cardView = new MaterialCardView(this);
        cardView.setRadius(dpToPx(20));
        cardView.setCardElevation(dpToPx(8));
        cardView.setCardBackgroundColor(Color.WHITE);
        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        int margin = dpToPx(20);
        params.setMargins(margin, margin, margin, margin);
        cardView.setLayoutParams(params);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(dpToPx(24), dpToPx(32), dpToPx(24), dpToPx(32));
        layout.setGravity(Gravity.CENTER);

        TextView title = new TextView(this);
        title.setText("THÔNG BÁO");
        title.setTextSize(22f);
        title.setTypeface(null, Typeface.BOLD);
        title.setTextColor(Color.parseColor("#202C39"));
        layout.addView(title);

        TextView msg = new TextView(this);
        msg.setText("Bạn đã hoàn thành tất cả câu hỏi.\nBạn muốn nộp bài ngay hay xem lại?");
        msg.setTextSize(16f);
        msg.setGravity(Gravity.CENTER);
        msg.setTextColor(Color.parseColor("#757575"));
        msg.setPadding(0, dpToPx(16), 0, dpToPx(32));
        layout.addView(msg);

        LinearLayout btnLayout = new LinearLayout(this);
        btnLayout.setOrientation(LinearLayout.HORIZONTAL);
        btnLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        Button btnReview = new Button(this);
        btnReview.setText("XEM LẠI");
        btnReview.setTextColor(Color.WHITE);
        btnReview.setBackgroundColor(Color.parseColor("#FF9800"));
        LinearLayout.LayoutParams btnParams1 = new LinearLayout.LayoutParams(0, dpToPx(50), 1f);
        btnParams1.setMargins(0, 0, dpToPx(8), 0);
        btnReview.setLayoutParams(btnParams1);

        Button btnSubmit = new Button(this);
        btnSubmit.setText("NỘP BÀI");
        btnSubmit.setTextColor(Color.WHITE);
        btnSubmit.setBackgroundColor(Color.parseColor("#4CAF50"));
        LinearLayout.LayoutParams btnParams2 = new LinearLayout.LayoutParams(0, dpToPx(50), 1f);
        btnParams2.setMargins(dpToPx(8), 0, 0, 0);
        btnSubmit.setLayoutParams(btnParams2);

        btnLayout.addView(btnReview);
        btnLayout.addView(btnSubmit);
        layout.addView(btnLayout);

        cardView.addView(layout);
        dialog.setContentView(cardView);

        btnReview.setOnClickListener(v -> {
            dialog.dismiss();
            hienThiMenuGrid();
        });

        btnSubmit.setOnClickListener(v -> {
            dialog.dismiss();
            hienThiKetQua();
        });

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

    private void hienThiKetQua() {
        if (timerHandler != null) {
            timerHandler.removeCallbacks(timerRunnable);
        }

        String tenMonLog = "TOÁN";
        String ngayGioHienTai = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());
        float diem10 = (diemSo * 10.0f) / danhSachCauHoi.size();
        String stringDiemSo = String.format(Locale.getDefault(), "Điểm: %.1f (%d/%d câu)", diem10, diemSo, danhSachCauHoi.size());
        int p = thoiGianGiay / 60;
        int g = thoiGianGiay % 60;
        String formatTimeStr = String.format(Locale.getDefault(), "%02d:%02d", p, g);

        try {
            if (monHoc.equalsIgnoreCase("Ly")) tenMonLog = "VẬT LÝ";
            else if (monHoc.equalsIgnoreCase("Hoa")) tenMonLog = "HÓA HỌC";

            StringBuilder chiTietBuilder = new StringBuilder();
            chiTietBuilder.append("CHI TIẾT BÀI THI MÔN ").append(tenMonLog).append(" - ").append(tenChuDe.toUpperCase()).append("\n");
            chiTietBuilder.append("Thời gian làm bài: ").append(formatTimeStr).append("\n\n");

            for (int i = 0; i < danhSachCauHoi.size(); i++) {
                CauHoi current = danhSachCauHoi.get(i);
                chiTietBuilder.append("Câu ").append(i + 1).append(": ").append(current.getCau_hoi()).append("\n");
                String choice = cauTraLoiCuaToi[i] != null ? cauTraLoiCuaToi[i] : "Chưa trả lời";
                chiTietBuilder.append("-> Đáp án bạn chọn: ").append(choice).append("\n");
                chiTietBuilder.append("-> Đáp án đúng: ").append(current.getDap_an_dung()).append("\n\n");
            }

            DatabaseHelper dbHelper = new DatabaseHelper(QuizActivity.this);
            dbHelper.addHistory(tenMonLog, ngayGioHienTai, stringDiemSo, chiTietBuilder.toString());
        } catch (Exception e) {
        }

        Dialog dialog = new Dialog(this);
        dialog.setCancelable(false);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        MaterialCardView cardView = new MaterialCardView(this);
        cardView.setRadius(dpToPx(20));
        cardView.setCardElevation(dpToPx(8));
        cardView.setCardBackgroundColor(Color.WHITE);
        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        int margin = dpToPx(20);
        params.setMargins(margin, margin, margin, margin);
        cardView.setLayoutParams(params);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(dpToPx(24), dpToPx(32), dpToPx(24), dpToPx(32));
        layout.setGravity(Gravity.CENTER);

        TextView title = new TextView(this);
        title.setText("KẾT QUẢ BÀI THI");
        title.setTextSize(24f);
        title.setTypeface(null, Typeface.BOLD);
        title.setTextColor(Color.parseColor("#202C39"));
        layout.addView(title);

        TextView score = new TextView(this);
        score.setText(String.format(Locale.getDefault(), "%.1f", diem10));
        score.setTextSize(64f);
        score.setTypeface(null, Typeface.BOLD);
        score.setTextColor(Color.parseColor("#4CAF50"));
        score.setPadding(0, dpToPx(16), 0, 0);
        layout.addView(score);

        TextView scoreLabel = new TextView(this);
        scoreLabel.setText("Điểm");
        scoreLabel.setTextSize(18f);
        scoreLabel.setTextColor(Color.parseColor("#757575"));
        layout.addView(scoreLabel);

        TextView correctCount = new TextView(this);
        correctCount.setText(String.format(Locale.getDefault(), "Số câu đúng: %d / %d", diemSo, danhSachCauHoi.size()));
        correctCount.setTextSize(16f);
        correctCount.setTypeface(null, Typeface.BOLD);
        correctCount.setTextColor(Color.parseColor("#202C39"));
        correctCount.setPadding(0, dpToPx(16), 0, 0);
        layout.addView(correctCount);

        LinearLayout timeLayout = new LinearLayout(this);
        timeLayout.setOrientation(LinearLayout.HORIZONTAL);
        timeLayout.setGravity(Gravity.CENTER);
        timeLayout.setPadding(0, dpToPx(16), 0, dpToPx(32));

        TextView timeLabel = new TextView(this);
        timeLabel.setText("Thời gian hoàn thành: ");
        timeLabel.setTextSize(16f);
        timeLabel.setTextColor(Color.parseColor("#202C39"));
        timeLayout.addView(timeLabel);

        TextView timeValue = new TextView(this);
        timeValue.setText(String.format(Locale.getDefault(), "%02d:%02d", p, g));
        timeValue.setTextSize(16f);
        timeValue.setTypeface(null, Typeface.BOLD);
        timeValue.setTextColor(themeColor);
        timeLayout.addView(timeValue);

        layout.addView(timeLayout);

        LinearLayout btnLayoutTop = new LinearLayout(this);
        btnLayoutTop.setOrientation(LinearLayout.HORIZONTAL);
        btnLayoutTop.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        Button btnReview = new Button(this);
        btnReview.setText("XEM LẠI");
        btnReview.setTextColor(Color.WHITE);
        btnReview.setBackgroundColor(Color.parseColor("#FF9800"));
        LinearLayout.LayoutParams btnParams1 = new LinearLayout.LayoutParams(0, dpToPx(50), 1f);
        btnParams1.setMargins(0, 0, dpToPx(8), 0);
        btnReview.setLayoutParams(btnParams1);

        Button btnExit = new Button(this);
        btnExit.setText("THOÁT");
        btnExit.setTextColor(Color.WHITE);
        btnExit.setBackgroundColor(Color.parseColor("#F44336"));
        LinearLayout.LayoutParams btnParams2 = new LinearLayout.LayoutParams(0, dpToPx(50), 1f);
        btnParams2.setMargins(dpToPx(8), 0, 0, 0);
        btnExit.setLayoutParams(btnParams2);

        btnLayoutTop.addView(btnReview);
        btnLayoutTop.addView(btnExit);
        layout.addView(btnLayoutTop);

        Button btnBackToList = new Button(this);
        btnBackToList.setText("DANH SÁCH ĐỀ THI");
        btnBackToList.setTextColor(Color.WHITE);
        btnBackToList.setBackgroundColor(Color.parseColor("#1976D2"));
        LinearLayout.LayoutParams btnParamsBack = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(50));
        btnParamsBack.setMargins(0, dpToPx(16), 0, 0);
        btnBackToList.setLayoutParams(btnParamsBack);
        layout.addView(btnBackToList);

        Button btnHistory = new Button(this);
        btnHistory.setText("LỊCH SỬ LÀM BÀI");
        btnHistory.setTextColor(Color.WHITE);
        btnHistory.setBackgroundColor(Color.parseColor("#455A64"));
        LinearLayout.LayoutParams btnParamsHistory = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(50));
        btnParamsHistory.setMargins(0, dpToPx(16), 0, 0);
        btnHistory.setLayoutParams(btnParamsHistory);
        layout.addView(btnHistory);

        Button btnShare = new Button(this);
        btnShare.setText("CHIA SẺ KẾT QUẢ");
        btnShare.setTextColor(Color.WHITE);
        btnShare.setBackgroundColor(Color.parseColor("#2196F3"));
        LinearLayout.LayoutParams btnParamsShare = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(50));
        btnParamsShare.setMargins(0, dpToPx(16), 0, 0);
        btnShare.setLayoutParams(btnParamsShare);
        layout.addView(btnShare);

        String finalTenMonLog = tenMonLog;
        btnShare.setOnClickListener(v -> {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            String shareMessage = "Kết quả bài thi môn " + finalTenMonLog + " - " + tenChuDe.toUpperCase() + "\n" + stringDiemSo + "\nThời gian: " + formatTimeStr;
            sendIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, "Chia sẻ kết quả"));
        });

        btnBackToList.setOnClickListener(v -> {
            dialog.dismiss();
            finish();
        });

        btnHistory.setOnClickListener(v -> {
            dialog.dismiss();
            try {
                Intent intentHistory = new Intent(QuizActivity.this, LichSuLamBaiActivity.class);
                startActivity(intentHistory);
                finish();
            } catch (Exception e) {
            }
        });

        cardView.addView(layout);
        dialog.setContentView(cardView);

        btnReview.setOnClickListener(v -> {
            dialog.dismiss();
            hienThiMenuGrid();
        });

        btnExit.setOnClickListener(v -> {
            dialog.dismiss();
            finish();
        });

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

    private void hienThiMenuGrid() {
        Dialog dialog = new Dialog(this);

        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setBackgroundColor(Color.WHITE);
        mainLayout.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        TextView txtBack = new TextView(this);
        txtBack.setText("QUAY LẠI DANH SÁCH BÀI LÀM");
        txtBack.setTextColor(Color.WHITE);
        txtBack.setBackgroundColor(themeColor);
        txtBack.setPadding(30, 40, 30, 40);
        txtBack.setTextSize(16f);
        txtBack.setTypeface(null, Typeface.BOLD);
        txtBack.setGravity(Gravity.CENTER);
        txtBack.setOnClickListener(v -> dialog.dismiss());
        mainLayout.addView(txtBack);

        ScrollView sv = new ScrollView(this);
        GridLayout gl = new GridLayout(this);
        gl.setColumnCount(5);
        gl.setPadding(16, 16, 16, 16);

        for (int i = 0; i < danhSachCauHoi.size(); i++) {
            Button btn = new Button(this);
            btn.setText(String.valueOf(i + 1));
            btn.setTextSize(16f);
            btn.setTypeface(null, Typeface.BOLD);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.setMargins(12, 12, 12, 12);
            btn.setLayoutParams(params);

            GradientDrawable shape = new GradientDrawable();
            shape.setShape(GradientDrawable.RECTANGLE);
            shape.setCornerRadius(dpToPx(10));
            shape.setStroke(dpToPx(1), Color.parseColor("#B0BEC5"));

            if (trangThaiCauHoi[i] == 1) {
                shape.setColor(Color.parseColor("#4CAF50"));
                shape.setStroke(0, Color.TRANSPARENT);
                btn.setTextColor(Color.WHITE);
            } else if (trangThaiCauHoi[i] == 2) {
                shape.setColor(Color.parseColor("#F44336"));
                shape.setStroke(0, Color.TRANSPARENT);
                btn.setTextColor(Color.WHITE);
            } else {
                shape.setColor(Color.parseColor("#F8F9FA"));
                btn.setTextColor(Color.parseColor("#202C39"));
            }

            btn.setBackground(shape);

            final int pos = i;
            btn.setOnClickListener(v -> {
                indexHienTai = pos;
                hienThiCauHoi();
                dialog.dismiss();
            });
            gl.addView(btn);
        }
        sv.addView(gl);
        mainLayout.addView(sv);

        dialog.setContentView(mainLayout);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        dialog.show();
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timerHandler != null) {
            timerHandler.removeCallbacks(timerRunnable);
        }
    }
}