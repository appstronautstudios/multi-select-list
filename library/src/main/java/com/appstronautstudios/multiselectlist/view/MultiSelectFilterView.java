package com.appstronautstudios.multiselectlist.view;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.AnyRes;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appstronautstudios.multiselectlist.R;
import com.appstronautstudios.multiselectlist.adapter.MultiSelectFilterAdapter;
import com.appstronautstudios.multiselectlist.model.MultiSelectFilterConfig;
import com.appstronautstudios.multiselectlist.model.SelectableItem;

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
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint(getContext().getString(androidx.appcompat.R.string.abc_search_hint));

        // 1. Remove legacy underline & background artifacts inside SearchView
        View searchPlate = searchView.findViewById(androidx.appcompat.R.id.search_plate);
        if (searchPlate != null) {
            searchPlate.setBackground(null);
        }

        // 2. Remove default inset margins so content aligns flush to edges
        View searchEditFrame = searchView.findViewById(androidx.appcompat.R.id.search_edit_frame);
        if (searchEditFrame != null && searchEditFrame.getLayoutParams() instanceof MarginLayoutParams) {
            MarginLayoutParams params = (MarginLayoutParams) searchEditFrame.getLayoutParams();
            params.leftMargin = 0;
            params.rightMargin = 0;
            searchEditFrame.setLayoutParams(params);
        }

        // 3. Match horizontal padding to list items (16dp default)
        float density = context.getResources().getDisplayMetrics().density;
        int padH = (int) (config.paddingHorizontal * density); // Default 16dp
        int padV = (int) (6 * density);
        searchView.setPadding(padH, padV, padH, padV);

        // 4. Modern rounded background for the search field
        android.graphics.drawable.GradientDrawable bg = new android.graphics.drawable.GradientDrawable();
        bg.setShape(android.graphics.drawable.GradientDrawable.RECTANGLE);
        bg.setCornerRadius(8 * density);
        bg.setColor(android.graphics.Color.parseColor("#F1F3F4")); // Subtle light gray background
        searchView.setBackground(bg);

        // 5. Wrap searchView inside a container with margins to separate it from list
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
        config.iconTint = resolveColor(color);
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
        config.highlightColour = resolveColor(color);
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
        config.dividerColour = resolveColor(color);
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
        setDividerHeight(resolveDimenToDp(resId));
    }

    public void setPaddingHorizontal(float dp) {
        this.config.paddingHorizontal = dp;
        this.recyclerView.invalidateItemDecorations();
    }

    public void setPaddingHorizontalRes(@DimenRes int resId) {
        setPaddingHorizontal(resolveDimenToDp(resId));
    }

    public void setPaddingVertical(float dp) {
        this.config.paddingVertical = dp;
        this.recyclerView.invalidateItemDecorations();
    }

    public void setPaddingVerticalRes(@DimenRes int resId) {
        setPaddingVertical(resolveDimenToDp(resId));
    }

    public void setTextSize(float sp) {
        this.config.textSizeSp = sp;
        if (this.adapter != null) {
            this.adapter.notifyDataSetChanged();
        }
    }

    public void setTextSizeRes(@DimenRes int resId) {
        float px = getContext().getResources().getDimension(resId);
        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        float sp = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
                ? TypedValue.deriveDimension(TypedValue.COMPLEX_UNIT_SP, px, metrics)
                : px / metrics.scaledDensity;
        setTextSize(sp);
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

    // 1. Unified Resource ID Overload (Handles both @DrawableRes and @LayoutRes)
    public View createStyledCell(SelectableItem<T> item, @AnyRes int iconResId, OnClickListener onClickListener) {
        return createStyledCellInternal(item, iconResId, null, onClickListener);
    }

    // 2. Pre-Inflated Custom View Overload
    public View createStyledCell(SelectableItem<T> item, @Nullable View customIconView, OnClickListener onClickListener) {
        return createStyledCellInternal(item, 0, customIconView, onClickListener);
    }

    // Master Private Builder
    private View createStyledCellInternal(SelectableItem<T> item, @AnyRes int resId, @Nullable View customView, OnClickListener onClickListener) {
        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_selectable, this, false);
        MultiSelectFilterAdapter.ViewHolder holder = new MultiSelectFilterAdapter.ViewHolder(itemView);

        if (adapter == null) {
            adapter = new MultiSelectFilterAdapter<>(Collections.emptyList(), null, config);
        }

        // 1. Bind text styling, padding, and typeface
        adapter.bindViewHolder(holder, item);

        // 2. Override container slot with custom header icon/view
        setContainerContent(getContext(), holder.iconContainer, resId, customView);

        itemView.setOnClickListener(v -> {
            if (onClickListener != null) onClickListener.onClick(v);

            // Re-bind text and re-apply custom container content to retain header state
            adapter.bindViewHolder(holder, item);
            setContainerContent(getContext(), holder.iconContainer, resId, customView);
        });

        return itemView;
    }

    private void setContainerContent(Context context, FrameLayout container, @AnyRes int resId, @Nullable View customView) {
        if (container == null) return;
        container.removeAllViews();

        if (customView != null) {
            // Option A: Pre-inflated View (Ensure it has no existing parent)
            if (customView.getParent() != null) {
                ((ViewGroup) customView.getParent()).removeView(customView);
            }
            container.addView(customView, new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
            ));
            container.setVisibility(View.VISIBLE);
        } else if (resId != 0) {
            String resourceType = context.getResources().getResourceTypeName(resId);

            if ("layout".equalsIgnoreCase(resourceType)) {
                // Option B: Custom Layout XML ID (R.layout.my_custom_view)
                LayoutInflater.from(context).inflate(resId, container, true);
                container.setVisibility(View.VISIBLE);
            } else {
                // Option C: Drawable/Vector Resource ID (R.drawable.ic_header)
                View iconLayout = LayoutInflater.from(context)
                        .inflate(R.layout.icon_container, container, false);
                ImageView ivIcon = iconLayout.findViewById(R.id.lib_iv_icon);
                ivIcon.setImageResource(resId);

                if (config.iconTint != null) {
                    ImageViewCompat.setImageTintList(
                            ivIcon,
                            android.content.res.ColorStateList.valueOf(config.iconTint)
                    );
                }

                container.addView(iconLayout);
                container.setVisibility(View.VISIBLE);
            }
        } else {
            container.setVisibility(View.GONE);
        }
    }

    @Nullable
    private Integer resolveColor(@ColorInt int colorOrResId) {
        if (colorOrResId == 0) {
            return null;
        }
        try {
            String resourceType = getContext().getResources().getResourceTypeName(colorOrResId);
            if ("color".equalsIgnoreCase(resourceType)) {
                return ContextCompat.getColor(getContext(), colorOrResId);
            }
        } catch (android.content.res.Resources.NotFoundException ignored) {
            // Fall through to treat as raw @ColorInt
        }
        return colorOrResId;
    }

    private float resolveDimenToDp(@DimenRes int resId) {
        if (resId == 0) return 0f;
        try {
            String type = getContext().getResources().getResourceTypeName(resId);
            if ("dimen".equalsIgnoreCase(type)) {
                float px = getContext().getResources().getDimension(resId);
                return px / getContext().getResources().getDisplayMetrics().density;
            }
        } catch (android.content.res.Resources.NotFoundException ignored) {
            // Not a valid resource ID
        }
        return 0f;
    }
}