package com.appstronautstudios.multiselectlist.model;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;

public class MultiSelectFilterConfig {
    @DrawableRes
    public Integer checkOnResId = null;
    @DrawableRes
    public Integer checkOffResId = null;
    @ColorInt
    public Integer highlightColour = null;
    public Integer dividerHeight = null;
    @ColorInt
    public Integer dividerColour = null;
    public Integer dividerPaddingLeft = null;
    public Integer dividerPaddingRight = null;
    public boolean sortSelectedToTop = false;

    // Fluent builder / chained setters for clean API usage
    public MultiSelectFilterConfig setCheckOnIcon(@DrawableRes int resId) {
        this.checkOnResId = resId;
        return this;
    }

    public MultiSelectFilterConfig setCheckOffIcon(@DrawableRes int resId) {
        this.checkOffResId = resId;
        return this;
    }

    public MultiSelectFilterConfig setHighlightColour(@ColorInt int colour) {
        this.highlightColour = colour;
        return this;
    }

    public MultiSelectFilterConfig setDividerHeight(int height) {
        this.dividerHeight = height;
        return this;
    }

    public MultiSelectFilterConfig setDividerColour(@ColorInt int colour) {
        this.dividerColour = colour;
        return this;
    }

    public MultiSelectFilterConfig setDividerPaddingLeft(int paddingLeft) {
        this.dividerPaddingLeft = paddingLeft;
        return this;
    }

    public MultiSelectFilterConfig setDividerPaddingRight(int paddingRight) {
        this.dividerPaddingRight = paddingRight;
        return this;
    }

    public MultiSelectFilterConfig setSortSelectedToTop(boolean enable) {
        this.sortSelectedToTop = enable;
        return this;
    }
}