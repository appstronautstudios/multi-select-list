package com.appstronautstudios.multiselectlist.model;

import android.graphics.Typeface;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;

public class MultiSelectFilterConfig {
    @DrawableRes
    public Integer checkOnResId = null;
    @DrawableRes
    public Integer checkOffResId = null;
    @ColorInt
    public Integer highlightColour = null;
    public Float dividerHeight = 0f;
    @ColorInt
    public Integer dividerColour = null;
    public Float paddingHorizontal = 0f;
    public Float paddingVertical = 0f;
    public boolean sortSelectedToTop = false;
    // Typography configuration
    public Float textSizeSp = null; // Text size in SP
    public Typeface typeface = null; // Custom Typeface / Font
}