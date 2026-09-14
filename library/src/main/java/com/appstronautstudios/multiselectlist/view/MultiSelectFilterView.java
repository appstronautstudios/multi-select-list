package com.appstronautstudios.multiselectlist.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appstronautstudios.multiselectlist.adapter.MultiSelectFilterAdapter;
import com.appstronautstudios.multiselectlist.model.MultiSelectFilterConfig;
import com.appstronautstudios.multiselectlist.model.SelectableItem;

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
        searchView.setIconifiedByDefault(false);

        recyclerView = new RecyclerView(context);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        // Attach the custom ItemDecoration once
        recyclerView.addItemDecoration(new ConfigurableDividerItemDecoration(config));

        addView(searchView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
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
        // Pass sortSelectedToTop directly to constructor so initial sorting honors the setting
        adapter = new MultiSelectFilterAdapter<>(items, listener, config);
        recyclerView.setAdapter(adapter);
    }

    public MultiSelectFilterConfig getConfig() {
        return config;
    }

    public void setCheckOnIcon(@DrawableRes int resId) {
        config.checkOnResId = resId;
        if (adapter != null) adapter.notifyDataSetChanged();
    }

    public void setCheckOffIcon(@DrawableRes int resId) {
        config.checkOffResId = resId;
        if (adapter != null) adapter.notifyDataSetChanged();
    }

    public void setHighlightColor(int color) {
        config.highlightColour = color;
        if (adapter != null) adapter.notifyDataSetChanged();
    }

    public void setDividerColour(int colour) {
        config.dividerColour = colour;
        recyclerView.invalidateItemDecorations(); // Refreshes item offsets & redraws
    }

    public void setDividerHeightDp(float height) {
        config.dividerHeight = height;
        recyclerView.invalidateItemDecorations(); // Refreshes item offsets & redraws
    }

    public void setPaddingHorizontal(float paddingHorizontal) {
        config.paddingHorizontal = paddingHorizontal;
        recyclerView.invalidateItemDecorations();
    }

    public void setPaddingVertical(float paddingVertical) {
        config.paddingVertical = paddingVertical;
        recyclerView.invalidateItemDecorations();
    }

    public void setTextSizeSp(float textSizeSp) {
        config.textSizeSp = textSizeSp;
        if (adapter != null) adapter.notifyDataSetChanged();
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
}