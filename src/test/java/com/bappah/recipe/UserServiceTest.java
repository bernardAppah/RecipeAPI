package com.bappah.recipe;

import com.bappah.recipe.entity.Recipe;
import com.bappah.recipe.entity.RecipeUser;
import com.bappah.recipe.entity.RecipeValueObject;
import com.bappah.recipe.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class UserServiceTest {

    private final static String USER_NAME = "APIuSER" + new Random().nextLong();;
    private final static String USER_PASSWORD = "password" + String.valueOf(new Random().nextLong());

    private final static String EBA = "Eba", SUYA ="Suya", LASAGNE = "Lasagne", EGUSI = "Egusi";

    @Autowired
    private UserService userService;

    private Long userId;

    List<RecipeValueObject> valueObjects;

    @BeforeEach
    void init() {
        valueObjects = List.of(
            new RecipeValueObject(EBA, "Fufu made from garri and hot water",
                "Boil water, then pour garri and stir", false, 2, "Garri", "Hot water"),
            new RecipeValueObject(SUYA, "Roasted meat",
                "Smoked seasoned beef", false, 3, "paprika", "onions", "goat meat", "suya pepper"),
            // Tomato is a duplicate ingredient
            new RecipeValueObject(LASAGNE, "A type of pasta, made of very wide, flat sheets ",
                "Roast and season raw beef", false, 12, "Pasta", "Tomato", "Ground beef", "Garlic", "Cheese"),
            new RecipeValueObject(EGUSI, "A Nigerian vegetable soup",
                "", true, 6, "Egusi", "Palm oil", "Onions", "Garlic", "Uwgu", "Water leaf")
        );

        userId = userService.registerUser(USER_NAME, USER_PASSWORD);

        assertNotNull(userId);
    }


    @Test
    void testRegisterUser() {

        Long testUserId = userService.registerUser("abtest", "abtest");

        assertNotNull(testUserId);
    }

    @Test
    public void testFindUserByName() {
        RecipeUser testRecipeUser = userService.findUserByUsername(USER_NAME);

        assertNotNull(testRecipeUser.getId());
        assertThat(testRecipeUser.getUsername()).isEqualTo(USER_NAME);
    }


    @Test
    public void testFindUserById() {
        RecipeUser testRecipeUser = userService.findUserById(userId);

        assertNotNull(testRecipeUser.getId());
        assertThat(testRecipeUser.getUsername()).isEqualTo(USER_NAME);
    }

    @Test
    public void testAddToMyRecipes() {
        // Save the recipes
        userService.addToMyRecipes(userId, valueObjects.subList(0,2));

        // Search for recipes given a list of names
        List<Recipe> searchResult = userService.findRecipesByUserId(userId);

        // Assert expected result
        assertThat(searchResult.size()).isEqualTo(2);
    }

    @Test
    public void testRemoveFromMyRecipes() {

        // Save the recipes
        userService.addToMyRecipes(userId, valueObjects.subList(0,2));

        // Search for recipes by the user id
        int saveCount = userService.findRecipesByUserId(userId).size();

        // Assert expected result
        assertThat(saveCount).isEqualTo(2);

        // Remove 2 recipes
        userService.removeFromMyRecipes(userId, List.of(SUYA));

        // Search for recipes by the user id
        int rmRecipesCount = userService.findRecipesByUserId(userId).size();

        assertThat(rmRecipesCount).isEqualTo(1);
    }
}
