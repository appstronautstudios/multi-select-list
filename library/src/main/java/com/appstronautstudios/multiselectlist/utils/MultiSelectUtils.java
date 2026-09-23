package com.appstronautstudios.multiselectlist.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.AnyRes;
import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.annotation.DimenRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;

import com.appstronautstudios.multiselectlist.R;
import com.appstronautstudios.multiselectlist.model.MultiSelectFilterConfig;

import java.util.Locale;

public class MultiSelectUtils {

    public static final int DEFAULT_DIVIDER_COLOR = Color.parseColor("#E0E0E0");
    public static final float DEFAULT_DIVIDER_HEIGHT_DP = 1.0f;

    public static int dpToPx(@NonNull Context context, float dp) {
        return Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                context.getResources().getDisplayMetrics()
        ));
    }

    public static float pxToDp(@NonNull Context context, float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    public static float pxToSp(@NonNull Context context, float px) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            return TypedValue.deriveDimension(TypedValue.COMPLEX_UNIT_SP, px, metrics);
        }
        return px / metrics.scaledDensity;
    }

    @Nullable
    public static Integer resolveColor(@NonNull Context context, @ColorInt int colorOrResId) {
        if (colorOrResId == 0) return null;
        try {
            String resourceType = context.getResources().getResourceTypeName(colorOrResId);
            if ("color".equalsIgnoreCase(resourceType)) {
                return ContextCompat.getColor(context, colorOrResId);
            }
        } catch (android.content.res.Resources.NotFoundException ignored) {
            // Fall through to return raw color int
        }
        return colorOrResId;
    }

    public static float resolveDimenToDp(@NonNull Context context, @DimenRes int resId) {
        if (resId == 0) return 0f;
        try {
            String type = context.getResources().getResourceTypeName(resId);
            if ("dimen".equalsIgnoreCase(type)) {
                float px = context.getResources().getDimension(resId);
                return pxToDp(context, px);
            }
        } catch (android.content.res.Resources.NotFoundException ignored) {
            // Invalid resource ID
        }
        return 0f;
    }

    public static int getDividerHeightPx(@NonNull Context context, @Nullable MultiSelectFilterConfig config) {
        if (config == null || config.dividerHeight == null || config.dividerHeight <= 0) {
            return 0;
        }
        return Math.max(1, dpToPx(context, config.dividerHeight));
    }

    public static int getDividerColor(@Nullable MultiSelectFilterConfig config) {
        if (config != null && config.dividerColour != null) {
            return config.dividerColour;
        }
        return DEFAULT_DIVIDER_COLOR;
    }

    public static int getHorizontalPaddingPx(@NonNull Context context, @Nullable MultiSelectFilterConfig config) {
        if (config == null || config.paddingHorizontal == null) {
            return 0;
        }
        return dpToPx(context, config.paddingHorizontal);
    }

    /**
     * Cleans up SearchView inner margins, underlines, and sets a modern rounded background.
     */
    public static void styleSearchView(@NonNull Context context, @NonNull SearchView searchView, @NonNull MultiSelectFilterConfig config) {
        searchView.setIconifiedByDefault(false);

        // query text
        int searchHintResId = context.getResources().getIdentifier("abc_search_hint", "string", "androidx.appcompat");
        if (searchHintResId != 0) {
            searchView.setQueryHint(context.getString(searchHintResId));
        } else {
            searchView.setQueryHint("Search...");
        }

        // 1. Remove legacy underline & background artifacts
        View searchPlate = searchView.findViewById(androidx.appcompat.R.id.search_plate);
        if (searchPlate != null) {
            searchPlate.setBackground(null);
        }

        // 2. Flush search edit frame to edges
        View searchEditFrame = searchView.findViewById(androidx.appcompat.R.id.search_edit_frame);
        if (searchEditFrame != null && searchEditFrame.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) searchEditFrame.getLayoutParams();
            params.leftMargin = 0;
            params.rightMargin = 0;
            searchEditFrame.setLayoutParams(params);
        }

        // 3. Match horizontal padding to config
        float density = context.getResources().getDisplayMetrics().density;
        int padH = (int) (config.paddingHorizontal * density);
        int padV = (int) (6 * density);
        searchView.setPadding(padH, padV, padH, padV);

        // 4. Rounded background
        GradientDrawable bg = new GradientDrawable();
        bg.setShape(GradientDrawable.RECTANGLE);
        bg.setCornerRadius(8 * density);
        bg.setColor(getSurfaceColor(context));
        searchView.setBackground(bg);
    }

    /**
     * Populates dynamic slots (Header/Footer/Item icons) with layouts, custom views, or tinted vectors.
     */
    public static void populateContainerContent(
            @NonNull Context context,
            @Nullable FrameLayout container,
            @AnyRes int resId,
            @Nullable View customView,
            @Nullable Integer tintColor
    ) {
        if (container == null) return;
        container.removeAllViews();

        if (customView != null) {
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
                LayoutInflater.from(context).inflate(resId, container, true);
                container.setVisibility(View.VISIBLE);
            } else {
                View iconLayout = LayoutInflater.from(context).inflate(R.layout.icon_container, container, false);
                ImageView ivIcon = iconLayout.findViewById(R.id.lib_iv_icon);
                ivIcon.setImageResource(resId);

                if (tintColor != null) {
                    ImageViewCompat.setImageTintList(ivIcon, ColorStateList.valueOf(tintColor));
                }

                container.addView(iconLayout);
                container.setVisibility(View.VISIBLE);
            }
        } else {
            container.setVisibility(View.GONE);
        }
    }

    public static void applyConfigPadding(@NonNull View view, @NonNull MultiSelectFilterConfig config) {
        Context context = view.getContext();
        int padH = config.paddingHorizontal != null ? dpToPx(context, config.paddingHorizontal) : 0;
        int padV = config.paddingVertical != null ? dpToPx(context, config.paddingVertical) : 0;
        view.setPadding(padH, padV, padH, padV);
    }

    public static CharSequence highlightText(@NonNull String fullText, @Nullable String query, @Nullable Integer highlightColor) {
        if (query == null || highlightColor == null) {
            return fullText;
        }

        // 1. Strip query to match the clean filter logic
        String cleanQuery = query.toLowerCase(Locale.getDefault()).replaceAll("[\\s+\\+\\.,\\-'\\|]+", "");
        if (cleanQuery.isEmpty()) {
            return fullText;
        }

        String lowerText = fullText.toLowerCase(Locale.getDefault());

        // 2. Build index map: cleanTextIndex -> originalTextIndex
        StringBuilder cleanTextBuilder = new StringBuilder();
        int[] indexMap = new int[fullText.length() + 1];
        int cleanLength = 0;

        for (int i = 0; i < fullText.length(); i++) {
            char c = lowerText.charAt(i);
            // Ignore spaces and special characters matching filter regex
            if (!Character.isWhitespace(c) && c != '+' && c != '.' && c != ',' && c != '-' && c != '\'' && c != '|') {
                indexMap[cleanLength] = i;
                cleanTextBuilder.append(c);
                cleanLength++;
            }
        }
        indexMap[cleanLength] = fullText.length();

        String cleanText = cleanTextBuilder.toString();
        int matchIndex = cleanText.indexOf(cleanQuery);

        if (matchIndex == -1) {
            return fullText;
        }

        SpannableString spannable = new SpannableString(fullText);

        // 3. Highlight all matching instances across clean boundaries
        while (matchIndex != -1) {
            int rawStart = indexMap[matchIndex];
            int rawEnd = indexMap[matchIndex + cleanQuery.length()];

            // If the match ends right before stripped characters, include trailing chars if needed or end at next mapped char
            spannable.setSpan(
                    new ForegroundColorSpan(highlightColor),
                    rawStart,
                    rawEnd,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );
            spannable.setSpan(
                    new StyleSpan(Typeface.BOLD),
                    rawStart,
                    rawEnd,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );

            matchIndex = cleanText.indexOf(cleanQuery, matchIndex + 1);
        }

        return spannable;
    }

    /**
     * Resolves the first available color attribute from the host theme, falling back to a default color.
     */
    @ColorInt
    public static int resolveThemeColor(@NonNull Context context, @AttrRes int attrRes, @ColorInt int fallbackColor) {
        TypedValue typedValue = new TypedValue();
        if (context.getTheme().resolveAttribute(attrRes, typedValue, true)) {
            return typedValue.data;
        }
        return fallbackColor;
    }

    /**
     * Resolves a suitable background surface color across Material 3, AppCompat, and standard Android themes.
     */
    @ColorInt
    public static int getSurfaceColor(@NonNull Context context) {
        TypedValue typedValue = new TypedValue();

        // 1. Material 3 Surface Container
        if (context.getTheme().resolveAttribute(com.google.android.material.R.attr.colorSurfaceContainerHigh, typedValue, true)) {
            return typedValue.data;
        }
        // 2. Control Highlight / Surface variant fallback
        if (context.getTheme().resolveAttribute(com.google.android.material.R.attr.colorControlHighlight, typedValue, true)) {
            return typedValue.data;
        }
        // 3. System Background fallback
        if (context.getTheme().resolveAttribute(android.R.attr.colorBackground, typedValue, true)) {
            return typedValue.data;
        }

        return 0xFFE0E0E0; // Safe default grey if theme resolution completely fails
    }
}