package com.appstronautstudios.multiselectlist.adapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appstronautstudios.multiselectlist.R;
import com.appstronautstudios.multiselectlist.model.MultiSelectFilterConfig;
import com.appstronautstudios.multiselectlist.model.SelectableItem;
import com.appstronautstudios.multiselectlist.utils.MultiSelectUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class MultiSelectFilterAdapter<T> extends RecyclerView.Adapter<MultiSelectFilterAdapter.ViewHolder> implements Filterable {

    private final List<SelectableItem<T>> originalList;
    private List<SelectableItem<T>> displayList;
    private final OnSelectionChangedListener<T> listener;
    private String currentQuery = "";

    private MultiSelectFilterConfig config;

    public interface OnSelectionChangedListener<T> {
        void onSelectionChanged(Set<SelectableItem<T>> selectedItems);
    }

    public MultiSelectFilterAdapter(List<SelectableItem<T>> items, OnSelectionChangedListener<T> listener, MultiSelectFilterConfig config) {
        this.originalList = new ArrayList<>(items);
        this.displayList = new ArrayList<>(items);
        this.listener = listener;
        this.config = config;
        applySort();
    }

    public void updateData(List<SelectableItem<T>> newItems) {
        this.originalList.clear();
        this.originalList.addAll(newItems);
        this.displayList = new ArrayList<>(newItems);
        applySort();
    }

    public Set<SelectableItem<T>> getSelectedItems() {
        Set<SelectableItem<T>> selected = new HashSet<>();
        for (SelectableItem<T> item : originalList) {
            if (item.isSelected()) {
                selected.add(item);
            }
        }
        return selected;
    }

    public void clearSelections() {
        for (SelectableItem<T> item : originalList) {
            item.setSelected(false);
        }
        applySort();
        if (listener != null) {
            listener.onSelectionChanged(getSelectedItems());
        }
    }

    public void applySort() {
        if (config.sortSelectedToTop) {
            Collections.sort(displayList, (o1, o2) -> {
                // If selection states differ, put selected item higher (-1)
                if (o1.isSelected() != o2.isSelected()) {
                    return o1.isSelected() ? -1 : 1;
                }
                // If selection states are identical, sort alphabetically by name
                return o1.getName().compareToIgnoreCase(o2.getName());
            });
        }
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName;
        public FrameLayout iconContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.lib_tv_name);
            iconContainer = itemView.findViewById(R.id.lib_fl_icon_container);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_selectable, parent, false);
        return new ViewHolder(view);
    }

    public void bindViewHolder(@NonNull ViewHolder holder, SelectableItem<T> item) {
        Context context = holder.itemView.getContext();

        // 1. Adjust view padding via MultiSelectUtils
        MultiSelectUtils.applyConfigPadding(holder.itemView, config);

        // 2. Adjust typeface and text size
        if (config.typeface != null) {
            holder.tvName.setTypeface(config.typeface);
        }
        if (config.textSizeSp != null) {
            holder.tvName.setTextSize(TypedValue.COMPLEX_UNIT_SP, config.textSizeSp);
        }

        // 3. Highlight text via MultiSelectUtils (handles spaces & special characters)
        holder.tvName.setText(MultiSelectUtils.highlightText(
                item.getName(),
                currentQuery,
                config.highlightColour
        ));

        // 4. Container & Checkmark slot population via MultiSelectUtils
        if (holder.iconContainer != null) {
            boolean isSelected = item.isSelected();
            int resId = isSelected ? config.checkOnResId : config.checkOffResId;

            if (resId == 0 && isSelected) {
                // Fallback default checkmark icon when selected and no custom resource set
                resId = R.drawable.check_24px;
            }

            if (resId != 0) {
                MultiSelectUtils.populateContainerContent(
                        context,
                        holder.iconContainer,
                        resId,
                        null,
                        config.iconTint
                );
            } else {
                holder.iconContainer.removeAllViews();
                holder.iconContainer.setVisibility(View.INVISIBLE);
            }
        }

        // 5. OnClick selection behavior
        holder.itemView.setOnClickListener(v -> {
            item.setSelected(!item.isSelected());

            if (config.sortSelectedToTop) {
                applySort();
            } else {
                notifyItemChanged(holder.getBindingAdapterPosition());
            }

            if (listener != null) {
                listener.onSelectionChanged(getSelectedItems());
            }
        });
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SelectableItem<T> item = displayList.get(position);
        bindViewHolder(holder, item);
    }

    @Override
    public int getItemCount() {
        return displayList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                currentQuery = (constraint == null) ? "" : constraint.toString();
                String cleanQuery = currentQuery.toLowerCase(Locale.getDefault()).replaceAll("[\\s+\\+\\.,\\-'\\|]+", "");

                List<SelectableItem<T>> filteredList = new ArrayList<>();

                if (cleanQuery.isEmpty()) {
                    filteredList.addAll(originalList);
                } else {
                    for (SelectableItem<T> item : originalList) {
                        String cleanItem = item.getName().toLowerCase(Locale.getDefault()).replaceAll("[\\s+\\+\\.,\\-'\\|]+", "");
                        if (cleanItem.contains(cleanQuery)) {
                            filteredList.add(item);
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                displayList = (List<SelectableItem<T>>) results.values;
                applySort();
            }
        };
    }
}