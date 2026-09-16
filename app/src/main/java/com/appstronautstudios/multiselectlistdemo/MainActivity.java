package com.appstronautstudios.multiselectlistdemo;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.appstronautstudios.multiselectlist.model.SelectableItem;
import com.appstronautstudios.multiselectlist.view.MultiSelectFilterView;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private TextView selectedItemsTV;
    private final ArrayList<Food> selectedFoods = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        selectedItemsTV = findViewById(R.id.selected_items);

        Button defaultButton = findViewById(R.id.selected_items_btn);
        Button customIconButton = findViewById(R.id.selected_items_custom_icon_btn);
        Button customHighlightButton = findViewById(R.id.selected_items_custom_highlight_btn);
        Button noSearchButton = findViewById(R.id.no_search_btn);
        Button sortToTopButton = findViewById(R.id.sort_to_top_btn);
        Button customDividerButton = findViewById(R.id.divider_btn);
        Button customPaddingButton = findViewById(R.id.custom_padding);
        Button customHeaderFooterButton = findViewById(R.id.custom_header);
        Button everythingButton = findViewById(R.id.everything_btn);

        defaultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MultiSelectFilterView<Food> view = createSelectionView();
                showDialog(view);
            }
        });

        customIconButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MultiSelectFilterView<Food> view = createSelectionView();
                view.setCheckOnIcon(R.drawable.check_box_24px);
                view.setCheckOffIcon(R.drawable.check_box_outline_blank_24px);
                view.setIconTintRes(R.color.colorAccent);
                showDialog(view);
            }
        });

        customHighlightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MultiSelectFilterView<Food> view = createSelectionView();
                view.setHighlightColor(ContextCompat.getColor(MainActivity.this, android.R.color.holo_orange_dark));
                showDialog(view);
            }
        });

        noSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MultiSelectFilterView<Food> view = createSelectionView();
                view.setSearchVisible(false);
                showDialog(view);
            }
        });

        sortToTopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MultiSelectFilterView<Food> view = createSelectionView();
                view.setSortSelectedToTop(true);
                showDialog(view);
            }
        });

        customDividerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MultiSelectFilterView<Food> view = createSelectionView();
                view.setDividerColour(Color.BLACK);
                view.setDividerHeight(2);
                showDialog(view);
            }
        });

        customPaddingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MultiSelectFilterView<Food> view = createSelectionView();
                view.setPaddingHorizontal(8);
                view.setPaddingVertical(8);
                showDialog(view);
            }
        });

        customHeaderFooterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MultiSelectFilterView<Food> view = createSelectionView();
                view.setHeaderView(createButtonHeader());
                view.setFooterView(createStyleFooter());
                showDialog(view);
            }
        });

        everythingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MultiSelectFilterView<Food> view = createSelectionView();
                view.setCheckOnIcon(R.drawable.check_box_24px);
                view.setCheckOffIcon(R.drawable.check_box_outline_blank_24px);
                view.setIconTintRes(R.color.colorAccent);
                view.setHighlightColor(ContextCompat.getColor(MainActivity.this, android.R.color.holo_orange_dark));
                view.setSortSelectedToTop(true);
                view.setDividerColour(Color.BLACK);
                view.setDividerHeight(2);
                view.setPaddingHorizontal(8);
                view.setPaddingVertical(8);
                view.setTextSize(18);
                view.setTypeface(Typeface.SERIF);

                // create custom cell that matches list config
                LinearLayout headerContainer = new LinearLayout(MainActivity.this);
                headerContainer.setOrientation(LinearLayout.VERTICAL);
                View customCell = view.createStyledCell(new SelectableItem<>(null, "Styled Cell with Layout Button"), R.layout.view_option_add, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(MainActivity.this, "header 1 clicked", Toast.LENGTH_SHORT).show();
                    }
                });
                View divider = view.createStyledDivider();
                View customCell2 = view.createStyledCell(new SelectableItem<>(null, "Styled Cell with Drawable Button"), R.drawable.edit_24px, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(MainActivity.this, "header 2 clicked", Toast.LENGTH_SHORT).show();
                    }
                });
                headerContainer.addView(customCell);
                headerContainer.addView(divider);
                headerContainer.addView(customCell2);
                view.setHeaderView(headerContainer);
                view.setFooterView(createStyleFooter());
                showDialog(view);
            }
        });
    }

    private void configureSelectedFoods() {
        ArrayList<String> foodNames = new ArrayList<>();
        for (Food selectedFood : selectedFoods) {
            foodNames.add(selectedFood.name);
        }
        selectedItemsTV.setText(String.join(", ", foodNames));
    }

    private boolean isFoodSelected(Food food) {
        for (Food selectedFood : selectedFoods) {
            if (selectedFood.id.equals(food.id)) {
                return true;
            }
        }
        return false;
    }

    private MultiSelectFilterView<Food> createSelectionView() {
        ArrayList<Food> allFoods = getFoodsFake();
        ArrayList<SelectableItem<Food>> items = new ArrayList<>();

        for (Food food : allFoods) {
            items.add(new SelectableItem<>(food, food.name, isFoodSelected(food)));
        }

        MultiSelectFilterView<Food> view = new MultiSelectFilterView<>(MainActivity.this);
        view.setItems(items, null);
        return view;
    }

    private View createButtonHeader() {
        // 1. Root vertical container to hold divider line + content
        LinearLayout footerLayout = new LinearLayout(MainActivity.this);
        footerLayout.setOrientation(LinearLayout.VERTICAL);

        float density = getResources().getDisplayMetrics().density;

        // 3. Action Container (Left/Right layout for action buttons)
        LinearLayout actionContainer = new LinearLayout(MainActivity.this);
        actionContainer.setOrientation(LinearLayout.HORIZONTAL);
        actionContainer.setGravity(Gravity.CENTER_VERTICAL);

        // Set horizontal margin/padding to line up neatly with dialog padding
        int paddingHorizontal = (int) (16 * density);
        int paddingVertical = (int) (4 * density);
        actionContainer.setPadding(paddingHorizontal, paddingVertical, paddingHorizontal, paddingVertical);

        // 4. Primary Action Button (e.g., + Add Custom Item)
        TextView btnAddItem = new TextView(MainActivity.this);
        btnAddItem.setText("+ Add Custom Item");
        btnAddItem.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.colorAccent));
        btnAddItem.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        btnAddItem.setTypeface(Typeface.DEFAULT_BOLD);
        btnAddItem.setClickable(true);
        btnAddItem.setFocusable(true);

        // Ripple effect on click
        TypedValue outValue = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
        btnAddItem.setBackgroundResource(outValue.resourceId);

        btnAddItem.setOnClickListener(vClick -> {
            // Perform Add action...
        });

        actionContainer.addView(btnAddItem);
        footerLayout.addView(actionContainer);

        return footerLayout;
    }

    private View createStyleFooter() {
        // 1. Root container with expanded padding to increase height and width footprint
        LinearLayout footerLayout = new LinearLayout(MainActivity.this);
        footerLayout.setOrientation(LinearLayout.VERTICAL);

        float density = getResources().getDisplayMetrics().density;
        int padHorizontal = (int) (24 * density);
        int padVertical = (int) (20 * density); // Increased top/bottom padding to make it taller
        footerLayout.setPadding(padHorizontal, padVertical, padHorizontal, padVertical);

        // 2. Horizontal layout holding the larger shapes
        LinearLayout shapeRow = new LinearLayout(MainActivity.this);
        shapeRow.setOrientation(LinearLayout.HORIZONTAL);
        shapeRow.setGravity(Gravity.CENTER);

        // Create 3 larger, wider pill-shaped indicators
        for (int i = 0; i < 3; i++) {
            View pill = new View(MainActivity.this);

            // Increased width and height values
            int width = (i == 1) ? (int) (48 * density) : (int) (16 * density); // Taller & wider spans
            int height = (int) (8 * density); // Doubled shape height (thickness)

            LinearLayout.LayoutParams pillParams = new LinearLayout.LayoutParams(width, height);
            if (i > 0) {
                pillParams.setMargins((int) (10 * density), 0, 0, 0); // Wider gap between shapes
            }

            // Shape styling with larger corner radius
            android.graphics.drawable.GradientDrawable shape = new android.graphics.drawable.GradientDrawable();
            shape.setShape(android.graphics.drawable.GradientDrawable.RECTANGLE);
            shape.setCornerRadius(4 * density);
            shape.setColor(i == 1 ? Color.parseColor("#B0B0B0") : Color.parseColor("#E0E0E0"));

            pill.setBackground(shape);
            shapeRow.addView(pill, pillParams);
        }

        footerLayout.addView(shapeRow);
        return footerLayout;
    }

    private void showDialog(MultiSelectFilterView<Food> view) {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Select all that apply")
                .setView(view)
                .setPositiveButton("Done", (dialog, which) -> {
                    selectedFoods.clear();
                    Set<SelectableItem<Food>> selected = view.getSelectedItems();
                    if (selected != null) {
                        for (SelectableItem<Food> item : selected) {
                            selectedFoods.add(item.getData());
                        }
                    }
                    configureSelectedFoods();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private ArrayList<Food> getFoodsFake() {
        ArrayList<Food> myFoods = new ArrayList<>();
        myFoods.add(new Food("1", "Apple"));
        myFoods.add(new Food("2", "Orange"));
        myFoods.add(new Food("3", "Banana"));
        myFoods.add(new Food("4", "Milk"));
        myFoods.add(new Food("5", "Cheese"));
        myFoods.add(new Food("6", "Bread"));
        myFoods.add(new Food("7", "Chicken"));
        myFoods.add(new Food("8", "Steak"));
        myFoods.add(new Food("9", "Olive Oil"));
        myFoods.add(new Food("10", "Canola Oil"));
        myFoods.add(new Food("11", "Tuna"));
        myFoods.add(new Food("12", "Salmon"));
        myFoods.add(new Food("13", "Crackers"));
        myFoods.add(new Food("14", "Chips"));
        myFoods.add(new Food("15", "Chocolate"));
        myFoods.add(new Food("16j", "Cake"));
        return myFoods;
    }

    static class Food {
        private String id;
        private String name;

        Food(String id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}
