package com.appstronautstudios.multiselectlist.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appstronautstudios.multiselectlist.model.MultiSelectFilterConfig;

public class ConfigurableDividerItemDecoration extends RecyclerView.ItemDecoration {

    private final Paint paint = new Paint();
    private final MultiSelectFilterConfig config;

    public ConfigurableDividerItemDecoration(MultiSelectFilterConfig config) {
        this.config = config;
        paint.setStyle(Paint.Style.FILL);
    }

    private int dpToPx(Context context, int dp) {
        return Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                context.getResources().getDisplayMetrics()
        ));
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        int itemCount = state.getItemCount();

        if (position == RecyclerView.NO_POSITION || position == itemCount - 1
                || config.dividerHeight == null || config.dividerHeight <= 0) {
            outRect.set(0, 0, 0, 0);
            return;
        }

        // Convert DP height to pixels
        outRect.bottom = dpToPx(parent.getContext(), config.dividerHeight);
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        if (config.dividerHeight == null || config.dividerHeight <= 0 || config.dividerColour == null) {
            return;
        }

        Context context = parent.getContext();
        paint.setColor(config.dividerColour);

        // Convert DP heights and paddings to pixels
        int heightPx = dpToPx(context, config.dividerHeight);
        int paddingLeftPx = (config.dividerPaddingLeft != null) ? dpToPx(context, config.dividerPaddingLeft) : 0;
        int paddingRightPx = (config.dividerPaddingRight != null) ? dpToPx(context, config.dividerPaddingRight) : 0;

        int left = parent.getPaddingLeft() + paddingLeftPx;
        int right = parent.getWidth() - parent.getPaddingRight() - paddingRightPx;
        int childCount = parent.getChildCount();

        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            int adapterPosition = parent.getChildAdapterPosition(child);

            // Skip drawing for the last item in the list
            if (adapterPosition == RecyclerView.NO_POSITION || adapterPosition == state.getItemCount() - 1) {
                continue;
            }

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + heightPx;

            c.drawRect(left, top, right, bottom, paint);
        }
    }
}