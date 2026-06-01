package com.phuongnambaophu.apptracnghiemtohopkhoi_a00;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ChatAIActivity extends AppCompatActivity {

    private String mssv_Nam = "65132069";
    private String mssv_Phu = "65132716";
    private ListView lvChat;
    private EditText edtTinNhan;
    private Button btnGui;
    private ImageView btnBackCustom;
    private ArrayList<TinNhan> dataChat;
    private ArrayList<TinNhan> tempMessages;
    private ChatAdapter adapter;
    private final OkHttpClient client = new OkHttpClient();

    private final String GROQ_API_KEY = "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";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        setContentView(R.layout.activity_chat_aiactivity);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        lvChat = findViewById(R.id.lvChat);
        edtTinNhan = findViewById(R.id.edtTinNhan);
        btnGui = findViewById(R.id.btnGui);
        btnBackCustom = findViewById(R.id.btnBackCustom);

        lvChat.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        lvChat.setStackFromBottom(true);

        dataChat = new ArrayList<>();
        tempMessages = new ArrayList<>();

        taiLichSuChat();

        adapter = new ChatAdapter(dataChat);
        lvChat.setAdapter(adapter);

        if (dataChat.isEmpty()) {
            TinNhan welcomeMsg = new TinNhan("Xin chào! Mình là SmartA. Bạn cần mình giảng giải chi tiết bài tập nào nè?", false);
            dataChat.add(welcomeMsg);
            adapter.notifyDataSetChanged();
            luuLichSuChat();
        }

        btnBackCustom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnGui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cauHoi = edtTinNhan.getText().toString().trim();
                if (!cauHoi.isEmpty()) {
                    btnGui.setEnabled(false);

                    TinNhan userMsg = new TinNhan(cauHoi, true);
                    dataChat.add(userMsg);
                    adapter.notifyDataSetChanged();
                    luuLichSuChat();

                    edtTinNhan.setText("");
                    cuonDanhSach();
                    goiGroqAI(cauHoi, false);
                }
            }
        });

        if (getIntent() != null && getIntent().hasExtra("CAU_HOI_SAI") && savedInstanceState == null) {
            String cauHoiSai = getIntent().getStringExtra("CAU_HOI_SAI");
            String tenDe = getIntent().hasExtra("TEN_DE_THI") ? getIntent().getStringExtra("TEN_DE_THI") : "Chưa xác định";
            String ngayThi = getIntent().hasExtra("NGAY_THI") ? getIntent().getStringExtra("NGAY_THI") : "Chưa xác định";

            if (cauHoiSai != null && !cauHoiSai.isEmpty()) {
                btnGui.setEnabled(false);

                String cauYeuCau = "Yêu cầu SmartA giải thích chi tiết các câu làm sai.\nĐề thi: " + tenDe + "\nThời gian làm bài: " + ngayThi;
                TinNhan reqMsg = new TinNhan(cauYeuCau, true);
                dataChat.add(reqMsg);
                tempMessages.add(reqMsg);
                adapter.notifyDataSetChanged();
                luuLichSuChat();

                cuonDanhSach();
                goiGroqAI(cauHoiSai, true);
            }
        }
    }

    private void goiGroqAI(String text, final boolean isGiaiThichCauSai) {
        if (GROQ_API_KEY.isEmpty() || GROQ_API_KEY.startsWith("THAY_")) {
            xuLyLoiGiaoDien("Lỗi: BẠN CHƯA THAY API KEY MỚI!", isGiaiThichCauSai);
            return;
        }

        String url = "https://api.groq.com/openai/v1/chat/completions";

        try {
            String systemPrompt = "Bạn là SmartA, gia sư tận tâm. Trình bày văn bản bằng định dạng Markdown thật đẹp mắt. BẮT BUỘC cách nhau 1 dòng trống (nhấn Enter 2 lần) giữa các đoạn văn, các gạch đầu dòng và các ý lớn để văn bản không bị dính liền.\n\n";

            if (isGiaiThichCauSai) {
                systemPrompt += "Nhiệm vụ: CHỈ giải thích chi tiết các câu học sinh làm SAI. Tuân thủ tuyệt đối cấu trúc sau, chia rõ từng bước giải:\n\n"
                        + "### Câu hỏi [Số thứ tự]\n\n"
                        + "💡 **1. Kiến thức trọng tâm:**\n\n"
                        + "Nêu công thức hoặc định lý cần dùng.\n\n"
                        + "🔍 **2. Tóm tắt đề bài:**\n\n"
                        + "Liệt kê các dữ kiện đã cho và yêu cầu của đề.\n\n"
                        + "📝 **3. Các bước giải chi tiết:**\n\n"
                        + "- **Bước 1:** Trình bày chi tiết phép tính đầu tiên.\n\n"
                        + "- **Bước 2:** Trình bày chi tiết phép tính tiếp theo.\n\n"
                        + "- **Kết luận:** Đưa ra đáp án cuối cùng.\n\n"
                        + "🚨 **4. Phân tích lỗi sai:**\n\n"
                        + "Chỉ ra điểm đánh lừa của đề bài hoặc lý do tính toán sai.\n\n"
                        + "---\n\n";
            } else {
                systemPrompt += "Nhiệm vụ: Giải đáp học tập thật chi tiết, từng bước một, xuống dòng rõ ràng giữa các đoạn, sử dụng gạch đầu dòng cho các bước giải tách biệt dễ nhìn.";
            }

            JSONObject jsonBody = new JSONObject();
            jsonBody.put("model", "llama-3.1-8b-instant");
            jsonBody.put("temperature", 0.3);

            JSONArray messages = new JSONArray();

            JSONObject systemMessage = new JSONObject();
            systemMessage.put("role", "system");
            systemMessage.put("content", systemPrompt);
            messages.put(systemMessage);

            int startIndex = Math.max(0, dataChat.size() - 4);
            for (int i = startIndex; i < dataChat.size(); i++) {
                TinNhan tn = dataChat.get(i);
                if (tempMessages.contains(tn)) continue;
                JSONObject msg = new JSONObject();
                msg.put("role", tn.isNguoiDung() ? "user" : "assistant");
                msg.put("content", tn.getNoiDung());
                messages.put(msg);
            }

            JSONObject userMessage = new JSONObject();
            userMessage.put("role", "user");
            userMessage.put("content", text);
            messages.put(userMessage);

            jsonBody.put("messages", messages);

            RequestBody body = RequestBody.create(
                    jsonBody.toString(),
                    MediaType.parse("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Authorization", "Bearer " + GROQ_API_KEY)
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    xuLyLoiGiaoDien("Lỗi kết nối internet!", isGiaiThichCauSai);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            String responseBody = response.body().string();
                            JSONObject jsonResponse = new JSONObject(responseBody);
                            JSONArray choices = jsonResponse.getJSONArray("choices");

                            final String traLoi = choices.getJSONObject(0).getJSONObject("message").getString("content");

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    TinNhan aiMsg = new TinNhan(traLoi, false);
                                    dataChat.add(aiMsg);
                                    if (isGiaiThichCauSai) {
                                        tempMessages.add(aiMsg);
                                    }
                                    adapter.notifyDataSetChanged();
                                    luuLichSuChat();
                                    cuonDanhSach();
                                    btnGui.setEnabled(true);
                                }
                            });
                        } catch (Exception e) {
                            xuLyLoiGiaoDien("Hệ thống gặp lỗi xử lý dữ liệu chữ!", isGiaiThichCauSai);
                        }
                    } else {
                        String detailError = "Mã lỗi: " + response.code();
                        if (response.body() != null) {
                            try {
                                String errorBody = response.body().string();
                                JSONObject errJson = new JSONObject(errorBody);
                                if (errJson.has("error")) {
                                    detailError += " - " + errJson.getJSONObject("error").getString("message");
                                }
                            } catch (Exception ex) {
                            }
                        }

                        if (response.code() == 429) {
                            xuLyLoiGiaoDien("Mã API Key đã hết hạn mức (Lỗi 429).", isGiaiThichCauSai);
                        } else {
                            xuLyLoiGiaoDien("SmartA báo lỗi hệ thống. " + detailError, isGiaiThichCauSai);
                        }
                    }
                }
            });

        } catch (Exception e) {
            xuLyLoiGiaoDien("Lỗi xử lý dữ liệu JSON!", isGiaiThichCauSai);
        }
    }

    private void xuLyLoiGiaoDien(String thongBao, boolean isTemp) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TinNhan errMsg = new TinNhan(thongBao, false);
                dataChat.add(errMsg);
                if (isTemp) {
                    tempMessages.add(errMsg);
                }
                adapter.notifyDataSetChanged();
                luuLichSuChat();
                cuonDanhSach();
                btnGui.setEnabled(true);
            }
        });
    }

    private void cuonDanhSach() {
        lvChat.post(new Runnable() {
            @Override
            public void run() {
                lvChat.setSelection(adapter.getCount() - 1);
            }
        });
    }

    private void luuLichSuChat() {
        SharedPreferences sharedPreferences = getSharedPreferences("SmartAChatPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        JSONArray jsonArray = new JSONArray();

        for (TinNhan tn : dataChat) {
            if (tempMessages.contains(tn)) {
                continue;
            }
            try {
                JSONObject obj = new JSONObject();
                obj.put("noidung", tn.getNoiDung());
                obj.put("isnguoidung", tn.isNguoiDung());
                jsonArray.put(obj);
            } catch (Exception e) {
            }
        }
        editor.putString("history_data", jsonArray.toString());
        editor.apply();
    }

    private void taiLichSuChat() {
        SharedPreferences sharedPreferences = getSharedPreferences("SmartAChatPrefs", Context.MODE_PRIVATE);
        String history = sharedPreferences.getString("history_data", "[]");

        try {
            JSONArray jsonArray = new JSONArray(history);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                String noiDung = obj.getString("noidung");
                boolean isNguoiDung = obj.getBoolean("isnguoidung");
                dataChat.add(new TinNhan(noiDung, isNguoiDung));
            }
        } catch (Exception e) {
        }
    }
}