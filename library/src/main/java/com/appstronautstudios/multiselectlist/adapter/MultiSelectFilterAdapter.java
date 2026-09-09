package com.appstronautstudios.multiselectlist.adapter;

import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appstronautstudios.multiselectlist.R;
import com.appstronautstudios.multiselectlist.model.SelectableItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;


public class MultiSelectFilterAdapter<T>
        extends RecyclerView.Adapter<MultiSelectFilterAdapter.ViewHolder>
        implements Filterable {

    public interface OnSelectionChangedListener<T> {
        void onSelectionChanged(Set<SelectableItem<T>> selectedItems);
    }

    private final List<SelectableItem<T>> originalList;
    private List<SelectableItem<T>> displayList;
    private final OnSelectionChangedListener<T> listener;
    private String currentQuery = "";
    private @ColorInt Integer highlightColor = null;

    public MultiSelectFilterAdapter(List<SelectableItem<T>> items, OnSelectionChangedListener<T> listener) {
        this.originalList = new ArrayList<>(items);
        this.displayList = new ArrayList<>(items);
        this.listener = listener;
        sortSelectedToTop();
    }

    public void setHighlightColor(@ColorInt int color) {
        this.highlightColor = color;
    }

    public void updateData(List<SelectableItem<T>> newItems) {
        this.originalList.clear();
        this.originalList.addAll(newItems);
        this.displayList = new ArrayList<>(newItems);
        sortSelectedToTop();
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
        sortSelectedToTop();
        if (listener != null) {
            listener.onSelectionChanged(getSelectedItems());
        }
    }

    public void sortSelectedToTop() {
        Collections.sort(displayList, (o1, o2) -> {
            // If selection states differ, put selected item higher (-1)
            if (o1.isSelected() != o2.isSelected()) {
                return o1.isSelected() ? -1 : 1;
            }
            // If selection states are identical, sort alphabetically by name
            return o1.getName().compareToIgnoreCase(o2.getName());
        });
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvName;
        final ImageView ivCheck;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.lib_tv_name);
            ivCheck = itemView.findViewById(R.id.lib_iv_check);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_selectable, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SelectableItem<T> item = displayList.get(position);
        String name = item.getName();

        // Highlight matching query string
        if (!currentQuery.isEmpty() && highlightColor != null) {
            int startPos = name.toLowerCase(Locale.getDefault()).indexOf(currentQuery.toLowerCase(Locale.getDefault()));
            if (startPos != -1) {
                Spannable spannable = new SpannableString(name);
                ColorStateList colorStateList = ColorStateList.valueOf(highlightColor);
                spannable.setSpan(new TextAppearanceSpan(null, Typeface.BOLD, -1, colorStateList, null),
                        startPos, startPos + currentQuery.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.tvName.setText(spannable);
            } else {
                holder.tvName.setText(name);
            }
        } else {
            holder.tvName.setText(name);
        }

        // Toggle Checkmark Visibility
        if (holder.ivCheck != null) {
            holder.ivCheck.setVisibility(item.isSelected() ? View.VISIBLE : View.INVISIBLE);
        }

        holder.itemView.setOnClickListener(v -> {
            // 1. Toggle selection state
            item.setSelected(!item.isSelected());

            // 2. Re-sort entire list (Selected at top, unselected alphabetical below)
            sortSelectedToTop();

            // 3. Notify external listener
            if (listener != null) {
                listener.onSelectionChanged(getSelectedItems());
            }
        });
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
                currentQuery = (constraint == null) ? "" : constraint.toString().trim();
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
                sortSelectedToTop();
            }
        };
    }
}