package com.phuongnambaophu.apptracnghiemtohopkhoi_a00;

import android.app.Activity;
import android.content.Intent;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class AIManager {

    public static void attachAIButton(Activity activity) {
        FrameLayout rootLayout = activity.findViewById(android.R.id.content);
        if (rootLayout == null) return;

        ImageView btnAI = new ImageView(activity);
        btnAI.setImageResource(R.drawable.ic_logo_ai);

        int sizeInDp = 120;
        int sizeInPx = (int) (sizeInDp * activity.getResources().getDisplayMetrics().density);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(sizeInPx, sizeInPx);

        params.gravity = Gravity.BOTTOM | Gravity.END;
        int rightMarginInPx = (int) (16 * activity.getResources().getDisplayMetrics().density);
        int bottomMarginInPx = (int) (110 * activity.getResources().getDisplayMetrics().density);
        params.setMargins(0, 0, rightMarginInPx, bottomMarginInPx);
        btnAI.setLayoutParams(params);

        btnAI.setOnClickListener(v -> {
            Intent intent = new Intent(activity, ChatAIActivity.class);
            activity.startActivity(intent);
        });

        rootLayout.addView(btnAI);
    }
}