package com.appstronautstudios.multiselectlistdemo;

import android.content.DialogInterface;
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
    private ArrayList<Food> selectedFoods = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        selectedItemsTV = findViewById(R.id.selected_items);

        Button editButton = findViewById(R.id.selected_items_btn);
        Button editCustomIconButton = findViewById(R.id.selected_items_custom_icon_btn);
        Button editCustomHighlightButton = findViewById(R.id.selected_items_custom_highlight_btn);
        Button noSearchButton = findViewById(R.id.no_search_btn);
        Button sortToTopButton = findViewById(R.id.sort_to_top_btn);

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectionPrompt(false, false, false, true, false);
            }
        });
        editCustomIconButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectionPrompt(true, true, false, true, false);
            }
        });
        editCustomHighlightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectionPrompt(false, false, true, true, false);
            }
        });
        noSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectionPrompt(false, false, false, false, false);
            }
        });
        sortToTopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectionPrompt(false, false, false, true, true);
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

    private void showSelectionPrompt(boolean customOnIcon, boolean customOffIcon, boolean customHighlight, boolean searchOn, boolean sortToTop) {
        ArrayList<Food> allFoods = getFoodsFake();

        ArrayList<SelectableItem<Food>> items = new ArrayList<>();
        for (Food food : allFoods) {
            items.add(new SelectableItem<>(food, food.name, isFoodSelected(food)));
        }

        MultiSelectFilterView<Food> view = new MultiSelectFilterView<>(MainActivity.this);
        if (customOnIcon) {
            view.setCheckOnIcon(R.drawable.check_box_24px);
        }
        if (customOffIcon) {
            view.setCheckOffIcon(R.drawable.check_box_outline_blank_24px);
        }
        if (customHighlight) {
            view.setHighlightColor(ContextCompat.getColor(MainActivity.this, android.R.color.holo_orange_dark));
        }
        view.setSearchVisible(searchOn);
        view.setSortSelectedToTop(sortToTop);

        // Pass null for listener so clicks only toggle internal UI state without mutating activity data immediately
        view.setItems(items, null);

        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Select all that apply")
                .setView(view)
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Update main selected foods ONLY when user confirms
                        selectedFoods = new ArrayList<>();
                        Set<SelectableItem<Food>> selected = view.getSelectedItems();
                        if (selected != null) {
                            for (SelectableItem<Food> item : selected) {
                                selectedFoods.add(item.getData());
                            }
                        }
                        configureSelectedFoods();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create()
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
