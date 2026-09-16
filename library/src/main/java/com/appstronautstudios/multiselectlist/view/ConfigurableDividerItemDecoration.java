package com.appstronautstudios.multiselectlist.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appstronautstudios.multiselectlist.model.MultiSelectFilterConfig;
import com.appstronautstudios.multiselectlist.utils.MultiSelectUtils;

public class ConfigurableDividerItemDecoration extends RecyclerView.ItemDecoration {

    private final Paint paint = new Paint();
    private final MultiSelectFilterConfig config;

    public ConfigurableDividerItemDecoration(MultiSelectFilterConfig config) {
        this.config = config;
        paint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        int itemCount = state.getItemCount();
        int heightPx = MultiSelectUtils.getDividerHeightPx(parent.getContext(), config);

        if (position == RecyclerView.NO_POSITION || position == itemCount - 1 || heightPx <= 0 || config.dividerColour == null) {
            outRect.set(0, 0, 0, 0);
            return;
        }

        outRect.bottom = heightPx;
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int heightPx = MultiSelectUtils.getDividerHeightPx(parent.getContext(), config);
        if (heightPx <= 0 || config.dividerColour == null) {
            return;
        }

        Context context = parent.getContext();
        paint.setColor(MultiSelectUtils.getDividerColor(config));

        int marginHorizontalPx = MultiSelectUtils.getHorizontalPaddingPx(context, config);
        int left = marginHorizontalPx;
        int right = parent.getWidth() - marginHorizontalPx;
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