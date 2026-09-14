package com.appstronautstudios.multiselectlist.adapter;

import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appstronautstudios.multiselectlist.R;
import com.appstronautstudios.multiselectlist.model.MultiSelectFilterConfig;
import com.appstronautstudios.multiselectlist.model.SelectableItem;

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
        applySort(); // Ensures initial sorting respects sortSelectedToTop
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

        // adjust padding based on user input
        float density = holder.itemView.getContext().getResources().getDisplayMetrics().density;
        int horizontalPaddingPx = (int) (config.paddingHorizontal * density); // 16 dp left and right
        int verticalPaddingPx = (int) (config.paddingVertical * density);   // 12 dp top and bottom
        holder.itemView.setPadding(
                horizontalPaddingPx,
                verticalPaddingPx,
                horizontalPaddingPx,
                verticalPaddingPx);

        // adjust typeface and text size based on user input
        if (config.typeface != null) {
            holder.tvName.setTypeface(config.typeface);
        }
        if (config.textSizeSp != null) {
            // TypedValue.COMPLEX_UNIT_SP ensures correct scaling with system accessibility settings
            holder.tvName.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, config.textSizeSp);
        }

        // Highlight matching query string
        if (!currentQuery.isEmpty() && config.highlightColour != null) {
            String lowerName = name.toLowerCase(Locale.getDefault());
            String lowerQuery = currentQuery.toLowerCase(Locale.getDefault());

            int startPos = lowerName.indexOf(lowerQuery);
            if (startPos != -1) {
                Spannable spannable = new SpannableString(name);
                int endPos = startPos + currentQuery.length();

                // 1. Use ForegroundColorSpan instead of TextAppearanceSpan for reliable coloring
                spannable.setSpan(new ForegroundColorSpan(config.highlightColour),
                        startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                // 2. Bold style set separately if desired
                spannable.setSpan(new StyleSpan(Typeface.BOLD),
                        startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                holder.tvName.setText(spannable);
            } else {
                holder.tvName.setText(name);
            }
        } else {
            holder.tvName.setText(name);
        }

        // Toggle Checkmark Visibility and Icon
        if (holder.ivCheck != null) {
            if (item.isSelected()) {
                holder.ivCheck.setVisibility(View.VISIBLE);
                if (config.checkOnResId != null) {
                    holder.ivCheck.setImageResource(config.checkOnResId);
                }
            } else {
                // If has an off icon show that. Otherwise, hide icon entirely
                if (config.checkOffResId != null) {
                    holder.ivCheck.setVisibility(View.VISIBLE);
                    holder.ivCheck.setImageResource(config.checkOffResId);
                } else {
                    holder.ivCheck.setVisibility(View.INVISIBLE);
                }
            }
        }

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
                applySort();
            }
        };
    }
}