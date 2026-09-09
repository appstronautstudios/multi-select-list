package com.appstronautstudios.multiselectlist.model;

public class SelectableItem<T> {
    private final T data;
    private final String name;
    private boolean isSelected;

    public SelectableItem(T data, String name) {
        this.data = data;
        this.name = name;
        this.isSelected = false;
    }

    public SelectableItem(T data, String name, boolean isSelected) {
        this.data = data;
        this.name = name;
        this.isSelected = isSelected;
    }

    public T getData() {
        return data;
    }

    public String getName() {
        return name;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}