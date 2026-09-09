package com.appstronautstudios.multiselectlist.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

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

        // Dynamically program layout elements
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
        adapter = new MultiSelectFilterAdapter<>(items, listener);
        recyclerView.setAdapter(adapter);
    }

    public void setHighlightColor(int color) {
        if (adapter != null) adapter.setHighlightColor(color);
    }

    public Set<SelectableItem<T>> getSelectedItems() {
        return (adapter != null) ? adapter.getSelectedItems() : null;
    }

    public void clearSelections() {
        if (adapter != null) adapter.clearSelections();
    }
}