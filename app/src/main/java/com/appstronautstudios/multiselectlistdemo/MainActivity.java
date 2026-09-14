package com.appstronautstudios.multiselectlistdemo;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
                view.setDividerHeightDp(2);
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

        everythingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MultiSelectFilterView<Food> view = createSelectionView();
                view.setCheckOnIcon(R.drawable.check_box_24px);
                view.setCheckOffIcon(R.drawable.check_box_outline_blank_24px);
                view.setHighlightColor(ContextCompat.getColor(MainActivity.this, android.R.color.holo_orange_dark));
                view.setSortSelectedToTop(true);
                view.setDividerColour(Color.BLACK);
                view.setDividerHeightDp(2);
                view.setPaddingHorizontal(8);
                view.setPaddingVertical(8);
                view.setTextSizeSp(18);
                view.setTypeface(Typeface.SERIF);
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
