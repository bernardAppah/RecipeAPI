package com.bappah.recipe.service;

import com.bappah.recipe.entity.Ingredient;
import com.bappah.recipe.entity.Recipe;
import com.bappah.recipe.entity.RecipeUser;
import com.bappah.recipe.entity.RecipeValueObject;
import com.bappah.recipe.exception.RecipeNotFound;
import com.bappah.recipe.repositories.IngredientRepository;
import com.bappah.recipe.repositories.RecipeRepository;
import com.bappah.recipe.entity.*;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class RecipeService {
    private static Logger log = LoggerFactory.getLogger(RecipeService.class);

    private final static String RECIPE_NOT_FOUND_MSG = "Recipe name %s not found";
    private final static String RECIPE_ID_NOT_FOUND_MSG = "Recipe id %s not found";

    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;

    /**
     * Saves a new recipe to the database
     * @param recipeValueObject The value object hold the data for the recipe
     * @param recipeUser The user
     */
    public Recipe saveRecipe(RecipeUser recipeUser, RecipeValueObject recipeValueObject) {
        Recipe recipe = Recipe.builder()
                .name(StringUtils.lowerCase(recipeValueObject.name))
                .description(recipeValueObject.description)
                .instructions(recipeValueObject.instructions)
                .servings(recipeValueObject.servings)
                .isVegetarian(recipeValueObject.isVegetarian)
                .recipeUser(recipeUser)
                .ingredients(new HashSet<>())
                .build();

        Set<Ingredient> ingredientsForRecipe = convertIngredientStringToObjects(recipeValueObject);

        // Set the ingredients on the recipe so the join table is populated when saving the ingredients
        recipe.setIngredients(ingredientsForRecipe);

        handleSavingDuplicateIngredients(ingredientsForRecipe);

        return recipeRepository.save(recipe);
    }

    /**
     * Retrieve a recipe given its Id
     * @param id The id of the recipe
     * @return The recipe
     */
    public Recipe findRecipeById(Long id) {
        // Convert name variable to lowercase before searching since the name of the recipe is stored in lowercase.
        return recipeRepository.findById(id)
            .orElseThrow(()-> new RecipeNotFound(String.format(RECIPE_ID_NOT_FOUND_MSG, id)));
    }

    /**
     * Retrieve a recipe given its name
     * @param name The name of the recipe
     * @return The recipe
     */
    public Recipe findRecipeByName(String name) {
        // Convert name variable to lowercase before searching since the name of the recipe is stored in lowercase.
        String nameForSearch = name.toLowerCase();
        return recipeRepository.findByName(nameForSearch)
                .orElseThrow(()-> new RecipeNotFound(String.format(RECIPE_NOT_FOUND_MSG, name)));
    }

    public List<Recipe> findRecipesByNameIn(List<String> names) {
        // Convert name variable to lowercase before searching since the name of the recipe is stored in lowercase.
        names.forEach(StringUtils::lowerCase);
        return recipeRepository.findByNameIn(names);
    }

    /**
     * Update a given recipe
     * @param name Name of recipe to be updated
     * @param recipeValueObject Value Object holding the new data for the update
     * @return The updated recipe
     */
    public Recipe updateRecipe(String name, RecipeValueObject recipeValueObject) {
        // Retrieve the recipe and update it
        Recipe original = findRecipeByName(name);
        BeanUtils.copyProperties(recipeValueObject, original,  "name", "ingredients");

        // If there are additional ingredients, append them to the recipe
        Set<Ingredient> newIngredients = convertIngredientStringToObjects(recipeValueObject);
        original.getIngredients().addAll(newIngredients);

        handleSavingDuplicateIngredients(newIngredients);
        return recipeRepository.save(original);
    }


    /**
     * Update the name of a given recipe
     * @param oldName The old name
     * @param newName The new name
     */
    public void updateRecipeName(String oldName, String newName) {
        String oldNameForUpdate = StringUtils.lowerCase(oldName);
        String newNameForUpdate = StringUtils.lowerCase(newName);
        recipeRepository.updateTheNameOfAGivenRecipeIgnoreCase(oldNameForUpdate, newNameForUpdate);
    }

    /**
     * Delete recipe
     * @param name The name of the recipe to delete
     */
    public void deleteRecipe(String name) {
        recipeRepository.findByName(name)
            .ifPresentOrElse(recipeRepository::delete, () -> {
                    throw new RecipeNotFound(String.format(RECIPE_NOT_FOUND_MSG, name));
                });
    }

    /**
     * @return A list of all recipes in the database
     */
    public Page<Recipe> findAllRecipes() {
        return recipeRepository.findAll(Pageable.ofSize(50));
    }

    /**
     * Handles saving ingredients while preventing attempts to save duplicate ingredients.
     * @param ingredientsForRecipe The ingredients
     */
    private void handleSavingDuplicateIngredients(Set<Ingredient> ingredientsForRecipe) {
        // Filter out ingredients that already exist in the database.
        List<Ingredient> newIngredientsToSave = ingredientsForRecipe.stream()
            .filter(ing-> !ingredientRepository.existsByIngredientName(ing.getIngredientName()))
            .collect(Collectors.toList());

        // Save the ingredients before saving the recipe.
        ingredientRepository.saveAllAndFlush(newIngredientsToSave);
    }


    private Set<Ingredient> convertIngredientStringToObjects(RecipeValueObject recipeValueObject) {
        return recipeValueObject.ingredients
            .stream()
            // Map the strings into Ingredient objects
            .map(Ingredient::new)
            .collect(Collectors.toSet());
    }
}
