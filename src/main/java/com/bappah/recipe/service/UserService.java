package com.bappah.recipe.service;

import com.bappah.recipe.entity.*;
import com.bappah.recipe.exception.Unauthorised;
import com.bappah.recipe.repositories.RecipeRepository;
import com.bappah.recipe.repositories.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Data
@AllArgsConstructor
public class UserService {

    private static Logger log = LoggerFactory.getLogger(UserService.class);

    private final static String USER_ID_NOT_FOUND_MSG = "User with id %s not found";
    private final static String USER_NOT_FOUND_MSG = "User with name %s not found";
//    private final static BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    private UserRepository userRepository;
    private RecipeRepository recipeRepository;

    private RecipeService recipeService;

    public Long registerUser(String name, String password) {
        // Check if the user already exists
        Optional<RecipeUser> userOptional = userRepository.findRecipeUserByUsername(name);

        if (userOptional.isPresent()) {
            log.info("Name already taken");
            throw new IllegalStateException("Name already taken");
        }

//        String encodedPassword = bCryptPasswordEncoder.encode(password);
        String encodedPassword = password;
        RecipeUser newRecipeUser = RecipeUser.builder()
            .username(name).password(encodedPassword)
            .createdAt(LocalDate.now())
            .build();

        return userRepository.saveAndFlush(newRecipeUser).getId();
    }

    public RecipeUser findUserById(Long id) {
        return userRepository.findRecipeUserById(id).orElseThrow(
            ()-> new Unauthorised(String.format(USER_ID_NOT_FOUND_MSG, id)));
    }

    public RecipeUser findUserByUsername(String username) {
        return userRepository.findRecipeUserByUsername(username).orElseThrow(() ->
            new Unauthorised(String.format(USER_NOT_FOUND_MSG, username)));
    }

    public void addToMyRecipes(Long id, List<RecipeValueObject> myRecipes) {
        RecipeUser recipeUser = findUserById(id);
        // Save the recipes
        List<Recipe> recipes = myRecipes
            .stream()
            .map(recipeValueObject -> recipeService.saveRecipe(recipeUser,recipeValueObject))
            .collect(Collectors.toList());

        if (recipeUser.getMyRecipes() == null) recipeUser.setMyRecipes(new ArrayList<>());
        recipeUser.getMyRecipes().addAll(recipes);

        userRepository.save(recipeUser);
    }

    public void removeFromMyRecipes(Long id, List<String> recipeNames) {
        RecipeUser recipeUser = findUserById(id);
        List<String> recipeNamesToRemove =recipeNames.stream().map(StringUtils::lowerCase).collect(Collectors.toList());
        List<Recipe> recipesToRemove = recipeService.findRecipesByNameIn(recipeNamesToRemove);
        recipeUser.getMyRecipes().removeAll(recipesToRemove);

        recipeRepository.deleteAll(recipesToRemove);
        userRepository.save(recipeUser);
    }

    public List<Recipe> findRecipesByUserId(Long id) {
        return recipeRepository.findByRecipeUserId(id);
    }

    public Set<Recipe> findAllVegetarianRecipe(Long id) {
        return recipeRepository.findByIsVegetarianAndRecipeUserId(true, id);
    }

    /**
     * Filter the recipes of the given user by the filter criteria in the filter value object
     * @param userId
     * @param filterValueObject
     * @return the filtered recipes
     */
    public Set<Recipe> findRecipesFilteredBy(Long userId, FilterValueObject filterValueObject) {
        // Check that user exist, will throw exception if user does not exist
        findUserById(userId);

        // Query the database per criteria
        Set<Recipe> vegetarianFiltered =
            recipeRepository.findByIsVegetarianAndRecipeUserId(filterValueObject.isVegetarian,
            userId);
        Set<Recipe> servingsFiltered =
            recipeRepository.findByServingsGreaterThanEqual(filterValueObject.minimumServing);
        Set<Recipe> instructionsFiltered =
            recipeRepository.findByInstructionsContainingIgnoreCase(filterValueObject.instructionKeyWord);
        Set<Recipe> ingredientFiltered =
            recipeRepository.filterByIngredientNameIgnoreCase(filterValueObject.includeIngredient, filterValueObject.excludeIngredient);

        // Perform a union of the 4 criteria
        vegetarianFiltered.retainAll(instructionsFiltered);
        vegetarianFiltered.retainAll(servingsFiltered);
        vegetarianFiltered.retainAll(ingredientFiltered);

        return vegetarianFiltered;
    }

    public RecipeValueObject mapToValueObject(Recipe recipe) {
        RecipeValueObject recipeValueObject= new RecipeValueObject();
        recipeValueObject.name = recipe.getName();
        recipeValueObject.description = recipe.getDescription();
        recipeValueObject.instructions = recipe.getInstructions();
        recipeValueObject.isVegetarian = recipe.isVegetarian();
        recipeValueObject.ingredients.addAll(recipe.getIngredients().stream().map(Ingredient::getIngredientName).collect(Collectors.toList()));
        return recipeValueObject;
    }
}
