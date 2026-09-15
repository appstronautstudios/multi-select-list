package com.appstronautstudios.multiselectlist.model;

import android.graphics.Typeface;

import androidx.annotation.AnyRes;
import androidx.annotation.ColorInt;

public class MultiSelectFilterConfig {
    @AnyRes
    public int checkOnResId = 0;
    @AnyRes
    public int checkOffResId = 0;
    @ColorInt public Integer iconTint = null; // Tint color for drawable icons
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