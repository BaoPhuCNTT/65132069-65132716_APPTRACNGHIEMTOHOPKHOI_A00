package com.phuongnambaophu.apptracnghiemtohopkhoi_a00;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChiTietLichSuActivity extends AppCompatActivity {

    private LinearLayout layoutHeaderDetail;
    private ImageView btnBackDetail;
    private TextView tvTitleDetail, tvDateTaken, tvDuration, tvScoreDetail, tvContentDetail;
    private Button btnGiaiAI;

    private int totalQuestions = 0;
    private int correctAnswers = 0;
    private String extractedTopic = "";
    private String timeTaken = "--:--";
    private String rawDetailContent = "";
    private String headerStr = "";
    private String dateStr = "--/--/----";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chi_tiet_lich_su);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        layoutHeaderDetail = findViewById(R.id.layoutHeaderDetail);
        btnBackDetail = findViewById(R.id.btnBackDetail);
        tvTitleDetail = findViewById(R.id.tvTitleDetail);
        tvDateTaken = findViewById(R.id.tvDateTaken);
        tvDuration = findViewById(R.id.tvDuration);
        tvScoreDetail = findViewById(R.id.tvScoreDetail);
        tvContentDetail = findViewById(R.id.tvContentDetail);
        btnGiaiAI = findViewById(R.id.btnGiaiAI);

        btnBackDetail.setOnClickListener(v -> finish());

        String fullTitle = getIntent().getStringExtra("title");
        String detailContent = getIntent().getStringExtra("detail_content");
        String monHoc = getIntent().getStringExtra("mon_hoc");

        String timeExtra = getIntent().getStringExtra("thoi_gian");
        if (timeExtra == null) timeExtra = getIntent().getStringExtra("time");
        if (timeExtra != null && !timeExtra.trim().isEmpty()) {
            timeTaken = timeExtra;
        }

        if (fullTitle != null) {
            Matcher m = Pattern.compile("\\d{2}/\\d{2}/\\d{4} \\d{2}:\\d{2}").matcher(fullTitle);
            if (m.find()) {
                dateStr = m.group();
            } else if (fullTitle.contains("-")) {
                String[] parts = fullTitle.split("-");
                dateStr = parts[parts.length - 1].trim();
            } else {
                dateStr = fullTitle;
            }

            if (fullTitle.toLowerCase().contains("thời gian:")) {
                String[] parts = fullTitle.split("(?i)Thời gian:");
                if (parts.length > 1) {
                    String potentialTime = parts[1].split("-")[0].trim();
                    if (!potentialTime.isEmpty()) timeTaken = potentialTime;
                }
            }
        }
        tvDateTaken.setText("Ngày làm: " + dateStr);

        if (detailContent != null) {
            rawDetailContent = detailContent;
            String htmlContent = formatQuestionsToHtml(detailContent);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                tvContentDetail.setText(Html.fromHtml(htmlContent, Html.FROM_HTML_MODE_COMPACT));
            } else {
                tvContentDetail.setText(Html.fromHtml(htmlContent));
            }
        }

        tvDuration.setText("Thời gian làm bài: " + timeTaken);
        tvScoreDetail.setText("Số câu đúng: " + correctAnswers + " / " + totalQuestions);

        if (monHoc != null && !monHoc.isEmpty()) {
            headerStr = "MÔN " + monHoc.toUpperCase();
            if (!extractedTopic.isEmpty()) {
                headerStr += " - " + extractedTopic.toUpperCase();
            } else if (fullTitle != null && fullTitle.contains("[")) {
                String topic = fullTitle.substring(fullTitle.indexOf("[") + 1, fullTitle.indexOf("]"));
                headerStr += " - " + topic.toUpperCase();
            }
        } else {
            headerStr = extractedTopic.isEmpty() ? "CHI TIẾT BÀI LÀM" : extractedTopic.toUpperCase();
        }
        tvTitleDetail.setText(headerStr);

        String targetMon = (monHoc != null) ? monHoc : (fullTitle != null ? fullTitle : "");
        updateHeaderColor(targetMon.toUpperCase());

        btnGiaiAI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (correctAnswers == totalQuestions && totalQuestions > 0) {
                    Toast.makeText(ChiTietLichSuActivity.this, "Bài này bạn làm đúng hết 100% rồi, không có câu sai!", Toast.LENGTH_LONG).show();
                } else if (!rawDetailContent.isEmpty()) {
                    Intent intent = new Intent(ChiTietLichSuActivity.this, ChatAIActivity.class);
                    intent.putExtra("CAU_HOI_SAI", rawDetailContent);
                    intent.putExtra("TEN_DE_THI", headerStr);
                    intent.putExtra("NGAY_THI", dateStr);
                    startActivity(intent);
                } else {
                    Toast.makeText(ChiTietLichSuActivity.this, "Không có dữ liệu bài làm để giải thích.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateHeaderColor(String mon) {
        if (mon.contains("TOÁN")) {
            layoutHeaderDetail.setBackgroundColor(Color.parseColor("#1976D2"));
        } else if (mon.contains("LÝ") || mon.contains("VẬT LÝ")) {
            layoutHeaderDetail.setBackgroundColor(Color.parseColor("#D32F2F"));
        } else if (mon.contains("HÓA")) {
            layoutHeaderDetail.setBackgroundColor(Color.parseColor("#00695C"));
        } else {
            layoutHeaderDetail.setBackgroundColor(Color.parseColor("#1E293B"));
        }
    }

    private String formatQuestionsToHtml(String raw) {
        if (raw == null) return "";
        StringBuilder sb = new StringBuilder();
        String[] lines = raw.split("\n");
        String dapAnBanChon = "";
        String dapAnDung = "";

        for (int i = 0; i < lines.length; i++) {
            String l = lines[i].trim();
            if (l.isEmpty() || l.toUpperCase().startsWith("CHI TIẾT BÀI THI")) continue;

            String lowerL = l.toLowerCase();

            if (lowerL.startsWith("thời gian") || lowerL.startsWith("time")) {
                if (l.contains(":")) {
                    timeTaken = l.substring(l.indexOf(":") + 1).trim();
                } else {
                    timeTaken = l.replaceAll("(?i)thời gian làm bài", "").replaceAll("(?i)thời gian", "").trim();
                }
                continue;
            }
            if (lowerL.contains("số câu đúng") || lowerL.contains("điểm")) {
                continue;
            }

            l = l.replaceAll("\\(Câu\\s*\\d+\\)\\s*", "");

            if (l.startsWith("Câu")) {
                totalQuestions++;
                if (extractedTopic.isEmpty() && l.contains("[") && l.contains("]")) {
                    extractedTopic = l.substring(l.indexOf("[") + 1, l.indexOf("]"));
                }

                String cleanQuestion = l;
                Matcher m = Pattern.compile("Câu \\d+:").matcher(l);
                if (m.find()) {
                    String prefix = m.group();
                    String rest = l.substring(prefix.length()).trim();
                    rest = rest.replaceAll("^Câu \\d+:\\s*", "");
                    cleanQuestion = prefix + " " + rest;
                }

                sb.append("<br><b>").append(cleanQuestion).append("</b><br>");
            } else if (l.startsWith("-> Đáp án bạn chọn:")) {
                dapAnBanChon = l.replace("-> Đáp án bạn chọn:", "").trim();
                dapAnDung = "";

                if (i + 1 < lines.length && lines[i + 1].trim().startsWith("-> Đáp án đúng:")) {
                    dapAnDung = lines[i + 1].replace("-> Đáp án đúng:", "").trim();
                }

                if (!dapAnBanChon.isEmpty() && !dapAnBanChon.equals("null") && dapAnBanChon.equals(dapAnDung)) {
                    correctAnswers++;
                    sb.append("<font color='#4CAF50'><b>✓ Bạn chọn: ").append(dapAnBanChon).append(" (Đúng)</b></font><br>");
                } else {
                    sb.append("<font color='#F44336'><b>✘ Bạn chọn: ").append((dapAnBanChon.isEmpty() || dapAnBanChon.equals("null")) ? "Chưa chọn" : dapAnBanChon).append(" (Sai)</b></font><br>");
                }
            } else if (l.startsWith("-> Đáp án đúng:")) {
                if (!dapAnBanChon.equals(l.replace("-> Đáp án đúng:", "").trim())) {
                    sb.append("<font color='#4CAF50'><b>✓ Đáp án đúng: ").append(l.replace("-> Đáp án đúng:", "").trim()).append("</b></font><br>");
                }
                sb.append("<br><font color='#E2E8F0'>----------------------------------------</font><br>");
            } else {
                sb.append(l).append("<br>");
            }
        }
        return sb.toString();
    }
}