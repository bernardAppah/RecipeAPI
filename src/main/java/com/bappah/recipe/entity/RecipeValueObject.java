package com.bappah.recipe.entity;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Data
public class RecipeValueObject {
    public String name;
    public String description;
    public String instructions;
    public boolean isVegetarian;
    public int servings;
    public List<String> ingredients;

    public RecipeValueObject(String name, String description, String instructions, boolean isVegetarian,
                             int servings
            , String... ingredients) {
        this.name = name;
        this.description = description;
        this.instructions = instructions;
        this.isVegetarian = isVegetarian;
        this.servings = servings;
        this.ingredients = List.of(ingredients);
    }

    public RecipeValueObject() {
        this.ingredients = new ArrayList<>();
    }
}
