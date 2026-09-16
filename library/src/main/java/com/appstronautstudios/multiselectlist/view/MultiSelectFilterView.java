package com.appstronautstudios.multiselectlist.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.AnyRes;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appstronautstudios.multiselectlist.R;
import com.appstronautstudios.multiselectlist.adapter.MultiSelectFilterAdapter;
import com.appstronautstudios.multiselectlist.model.MultiSelectFilterConfig;
import com.appstronautstudios.multiselectlist.model.SelectableItem;
import com.appstronautstudios.multiselectlist.utils.MultiSelectUtils;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class MultiSelectFilterView<T> extends LinearLayout {

    private SearchView searchView;
    private RecyclerView recyclerView;
    private MultiSelectFilterAdapter<T> adapter;

    private final MultiSelectFilterConfig config = new MultiSelectFilterConfig();

    public MultiSelectFilterView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public MultiSelectFilterView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        setOrientation(VERTICAL);

        searchView = new SearchView(context);
        MultiSelectUtils.styleSearchView(context, searchView, config);

        float density = context.getResources().getDisplayMetrics().density;
        LinearLayout.LayoutParams searchParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        searchParams.setMargins((int) (8 * density), (int) (8 * density), (int) (8 * density), (int) (8 * density));

        recyclerView = new RecyclerView(context);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new ConfigurableDividerItemDecoration(config));

        addView(searchView, searchParams);
        addView(recyclerView, new LayoutParams(LayoutParams.MATCH_PARENT, 0, 1.0f));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (adapter != null) adapter.getFilter().filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (adapter != null) adapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    public void setItems(List<SelectableItem<T>> items, MultiSelectFilterAdapter.OnSelectionChangedListener<T> listener) {
        adapter = new MultiSelectFilterAdapter<>(items, listener, config);
        recyclerView.setAdapter(adapter);
    }

    public MultiSelectFilterConfig getConfig() {
        return config;
    }

    public void setHeaderView(View headerView) {
        this.addView(headerView, 1, new LinearLayout.LayoutParams(-1, -2));
    }

    public void setFooterView(View footerView) {
        this.addView(footerView, new LinearLayout.LayoutParams(-1, -2));
    }

    public void setCheckOnIcon(@AnyRes int resId) {
        config.checkOnResId = resId;
        if (adapter != null) adapter.notifyDataSetChanged();
    }

    public void setCheckOffIcon(@AnyRes int resId) {
        config.checkOffResId = resId;
        if (adapter != null) adapter.notifyDataSetChanged();
    }

    public void setIconTint(@ColorInt int color) {
        config.iconTint = MultiSelectUtils.resolveColor(getContext(), color);
        if (adapter != null) adapter.notifyDataSetChanged();
    }

    public void setIconTintRes(@ColorRes int colorResId) {
        if (colorResId != 0) {
            config.iconTint = ContextCompat.getColor(getContext(), colorResId);
            if (adapter != null) adapter.notifyDataSetChanged();
        } else {
            config.iconTint = null;
        }
    }

    public void setHighlightColor(@ColorInt int color) {
        config.highlightColour = MultiSelectUtils.resolveColor(getContext(), color);
        if (adapter != null) adapter.notifyDataSetChanged();
    }

    public void setHighlightColorRes(@ColorRes int colorResId) {
        if (colorResId != 0) {
            config.highlightColour = ContextCompat.getColor(getContext(), colorResId);
            if (adapter != null) adapter.notifyDataSetChanged();
        } else {
            config.highlightColour = null;
        }
    }

    public void setDividerColour(@ColorInt int color) {
        config.dividerColour = MultiSelectUtils.resolveColor(getContext(), color);
        recyclerView.invalidateItemDecorations();
    }

    public void setDividerColourRes(@ColorRes int colorResId) {
        if (colorResId != 0) {
            config.dividerColour = ContextCompat.getColor(getContext(), colorResId);
            recyclerView.invalidateItemDecorations();
        } else {
            config.dividerColour = null;
        }
    }

    public void setDividerHeight(float dp) {
        this.config.dividerHeight = dp;
        this.recyclerView.invalidateItemDecorations();
    }

    public void setDividerHeightRes(@DimenRes int resId) {
        setDividerHeight(MultiSelectUtils.resolveDimenToDp(getContext(), resId));
    }

    public void setPaddingHorizontal(float dp) {
        this.config.paddingHorizontal = dp;
        this.recyclerView.invalidateItemDecorations();
    }

    public void setPaddingHorizontalRes(@DimenRes int resId) {
        setPaddingHorizontal(MultiSelectUtils.resolveDimenToDp(getContext(), resId));
    }

    public void setPaddingVertical(float dp) {
        this.config.paddingVertical = dp;
        this.recyclerView.invalidateItemDecorations();
    }

    public void setPaddingVerticalRes(@DimenRes int resId) {
        setPaddingVertical(MultiSelectUtils.resolveDimenToDp(getContext(), resId));
    }

    public void setTextSize(float sp) {
        this.config.textSizeSp = sp;
        if (this.adapter != null) {
            this.adapter.notifyDataSetChanged();
        }
    }

    public void setTextSizeRes(@DimenRes int resId) {
        float px = getContext().getResources().getDimension(resId);
        setTextSize(MultiSelectUtils.pxToSp(getContext(), px));
    }

    public void setTypeface(Typeface typeface) {
        config.typeface = typeface;
        if (adapter != null) adapter.notifyDataSetChanged();
    }

    public void setSearchVisible(boolean visible) {
        searchView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public void setSortSelectedToTop(boolean enable) {
        config.sortSelectedToTop = enable;
        if (adapter != null) adapter.notifyDataSetChanged();
    }

    public Set<SelectableItem<T>> getSelectedItems() {
        return (adapter != null) ? adapter.getSelectedItems() : null;
    }

    public void clearSelections() {
        if (adapter != null) adapter.clearSelections();
    }

    public View createStyledDivider() {
        Context context = getContext();

        int heightPx = MultiSelectUtils.getDividerHeightPx(context, config);
        if (heightPx <= 0) {
            heightPx = Math.max(1, MultiSelectUtils.dpToPx(context, MultiSelectUtils.DEFAULT_DIVIDER_HEIGHT_DP));
        }

        int color = MultiSelectUtils.getDividerColor(config);
        int marginPx = MultiSelectUtils.getHorizontalPaddingPx(context, config);

        View dividerView = new View(context);
        dividerView.setBackgroundColor(color);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                heightPx
        );
        params.setMargins(marginPx, 0, marginPx, 0);
        dividerView.setLayoutParams(params);

        return dividerView;
    }

    public View createStyledCell(SelectableItem<T> item, @AnyRes int iconResId, OnClickListener onClickListener) {
        return createStyledCellInternal(item, iconResId, null, onClickListener);
    }

    public View createStyledCell(SelectableItem<T> item, @Nullable View customIconView, OnClickListener onClickListener) {
        return createStyledCellInternal(item, 0, customIconView, onClickListener);
    }

    private View createStyledCellInternal(SelectableItem<T> item, @AnyRes int resId, @Nullable View customView, OnClickListener onClickListener) {
        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_selectable, this, false);
        MultiSelectFilterAdapter.ViewHolder holder = new MultiSelectFilterAdapter.ViewHolder(itemView);

        if (adapter == null) {
            adapter = new MultiSelectFilterAdapter<>(Collections.emptyList(), null, config);
        }

        adapter.bindViewHolder(holder, item);

        MultiSelectUtils.populateContainerContent(getContext(), holder.iconContainer, resId, customView, config.iconTint);

        itemView.setOnClickListener(v -> {
            if (onClickListener != null) {
                onClickListener.onClick(v);
            }
        });

        return itemView;
    }
}