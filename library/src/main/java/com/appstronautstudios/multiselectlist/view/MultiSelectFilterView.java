package com.appstronautstudios.multiselectlist.view;

import android.content.Context;
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
import com.appstronautstudios.multiselectlist.model.SelectableItem;

import java.util.List;
import java.util.Set;

public class MultiSelectFilterView<T> extends LinearLayout {

    private SearchView searchView;
    private RecyclerView recyclerView;
    private MultiSelectFilterAdapter<T> adapter;
    private Integer customCheckOnResId = null;
    private Integer customCheckOffResId = null;
    private Integer customHighlightColour = null;
    private boolean sortSelectedToTop = false;

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
        adapter = new MultiSelectFilterAdapter<>(items, listener, sortSelectedToTop);

        // Apply stored configs
        if (customCheckOnResId != null) {
            adapter.setCheckOnIcon(customCheckOnResId);
        }
        if (customCheckOffResId != null) {
            adapter.setCheckOffIcon(customCheckOffResId);
        }
        if (customHighlightColour != null) {
            adapter.setHighlightColor(customHighlightColour);
        }

        recyclerView.setAdapter(adapter);
    }

    public void setCheckOnIcon(@DrawableRes int resId) {
        this.customCheckOnResId = resId;
        if (adapter != null) adapter.setCheckOnIcon(resId);
    }

    public void setCheckOffIcon(@DrawableRes int resId) {
        this.customCheckOffResId = resId;
        if (adapter != null) adapter.setCheckOffIcon(resId);
    }

    public void setHighlightColor(int color) {
        this.customHighlightColour = color;
        if (adapter != null) adapter.setHighlightColor(color);
    }

    public void setSearchVisible(boolean visible) {
        searchView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public void setSortSelectedToTop(boolean enable) {
        this.sortSelectedToTop = enable;
        if (adapter != null) {
            adapter.setSortSelectedToTop(enable);
        }
    }

    public Set<SelectableItem<T>> getSelectedItems() {
        return (adapter != null) ? adapter.getSelectedItems() : null;
    }

    public void clearSelections() {
        if (adapter != null) adapter.clearSelections();
    }
}