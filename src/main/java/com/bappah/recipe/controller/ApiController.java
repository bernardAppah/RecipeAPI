package com.bappah.recipe.controller;

import com.bappah.recipe.entity.FilterValueObject;
import com.bappah.recipe.entity.Ingredient;
import com.bappah.recipe.entity.Recipe;
import com.bappah.recipe.entity.RecipeValueObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.bappah.recipe.entity.*;
import com.bappah.recipe.service.RecipeService;
import com.bappah.recipe.service.UserService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@CrossOrigin("*")
@AllArgsConstructor
@RestController
@RequestMapping("/api/recipe")
public class ApiController {

    private static Logger log = LoggerFactory.getLogger(ApiController.class);

    private UserService userService;
    private RecipeService recipeService;
    private ObjectMapper mapper;


    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/register-user")
    public String register(@RequestParam String name, @RequestParam String password) {
        Long userId = userService.registerUser(name, password);

        return "{\n        \"userId\": \""+ userId +",\n        \"comment\": \"Registered new user with name: \" "+ name +"\"     }" ;
    }

    @ResponseBody()
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/add-recipe")
    public void addNewRecipes(@RequestParam Long userId,
                              @RequestBody RecipeValueObject myRecipes) {
        userService.addToMyRecipes(userId, List.of(myRecipes));
    }


    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/find-my-recipes")
    public List<Recipe> findRecipes(@RequestParam Long userId) {
        log.info("Searching for recipes for user with id {}", userId);
        return userService.findRecipesByUserId(userId);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/update-recipe-name")
    public String updateRecipeName(@RequestParam Long userId, @RequestParam String name, @RequestParam String newName) {

        log.info("Updating the name of recipe {} to {}", name, newName);
        recipeService.updateRecipeName(name, newName);
        return "Successfully updated the name of the recipe";
    }

    @ResponseStatus(HttpStatus.OK)
    @PatchMapping("/update-recipe-details")
    public String updateRecipe(@RequestParam Long userId, @RequestParam String name,
                               @RequestBody RecipeValueObject recipeValueObject) {

        log.info("Updating the recipe {} ", recipeValueObject.name);
        recipeService.updateRecipe(name, recipeValueObject);
        return "Successfully updated the recipe details";
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/delete")
    public void deleteRecipes(@RequestParam Long userId, @RequestBody  List<String> recipesToDelete) {
        userService.removeFromMyRecipes(userId, recipesToDelete);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/filter-recipes-by")
    public Set<Recipe> filterRecipesBy(@RequestParam Long userId, @RequestBody FilterValueObject filterValueObject) {
        return userService.findRecipesFilteredBy(userId, filterValueObject);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/get-recipe")
    public Recipe getRecipeDetail(@RequestParam Long userId, @RequestParam String recipeName) {

        log.info("Retrieve data for recipe {}", recipeName);
        return recipeService.findRecipeByName(recipeName);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/find-ingredients")
    public Set<Ingredient> retrieveIngredientsForRecipes(@RequestParam Long userId, @RequestParam String recipeName) {
        log.info("Calling get request to retrieve all ingredients for recipe {}", recipeName);
        return recipeService.findRecipeByName(recipeName).getIngredients();
    }

}
