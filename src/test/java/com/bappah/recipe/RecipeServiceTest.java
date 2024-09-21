package com.bappah.recipe;

import com.bappah.recipe.entity.Recipe;
import com.bappah.recipe.entity.RecipeUser;
import com.bappah.recipe.entity.RecipeValueObject;
import com.bappah.recipe.exception.RecipeNotFound;
import com.bappah.recipe.repositories.IngredientRepository;
import com.bappah.recipe.service.RecipeService;
import com.bappah.recipe.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class RecipeServiceTest {

    private final static String USER_NAME = "IntegrAPIuSER" + new Random().nextLong();
    private final static String USER_PASSWORD = "password" + new Random().nextLong();

    private final static String EBA = "Eba", SUYA ="Suya", LASAGNE = "Lasagne";

    @Autowired
    RecipeService recipeService;

    @Autowired
    UserService userService;
    @Autowired
    private IngredientRepository ingredientRepository;

    @MockBean
    RecipeUser recipeUser;

    private Long userId;

    List<RecipeValueObject> valueObjects;

    @BeforeEach
    void init() {
        valueObjects = List.of(
            new RecipeValueObject(EBA, "Fufu made from garri and hot water",
                "Boil water, then pour garri and stir", false, 2, "Garri", "Hot water"),
            new RecipeValueObject(SUYA, "Roasted meat",
                "Smoked seasoned beef", false, 3, "tomato", "onions", "goat meat", "suya pepper")
        );

        userId = userService.registerUser(USER_NAME, USER_PASSWORD);
        recipeUser = userService.findUserById(userId);
        recipeUser.setMyRecipes(new ArrayList<>());

        assertNotNull(userId);
        assertNotNull(recipeUser);
    }


    @Test
    void testSaveRecipe() {

        RecipeValueObject recipeVO = valueObjects.get(0);
        Recipe testRecipe = recipeService.saveRecipe(recipeUser, recipeVO);

        assertNotNull(testRecipe.getId());
        assertThat(testRecipe.getName()).isEqualTo(StringUtils.lowerCase(recipeVO.name));
        assertThat(testRecipe.getDescription()).isEqualTo(recipeVO.description);
    }

    @Test
    public void testFindByName() {

        RecipeValueObject recipeVO = valueObjects.get(1);
        recipeService.saveRecipe(recipeUser, recipeVO);

        Recipe testRecipe = recipeService.findRecipeByName(recipeVO.name);

        assertNotNull(testRecipe.getId());
        assertThat(testRecipe.getName()).isEqualTo(StringUtils.lowerCase(recipeVO.name));
        assertThat(testRecipe.getDescription()).isEqualTo(recipeVO.description);
    }

    @Test
    public void testUpdateRecipeName() {

        for (RecipeValueObject valueObject : valueObjects) {
            recipeService.saveRecipe(recipeUser, valueObject);
        }

        recipeService.updateRecipeName(SUYA, "newName");

        assertThrows(RecipeNotFound.class, ()->recipeService.findRecipeByName(SUYA));
    }

    @Test
    public void testUpdateRecipe() {
        // Given 2 value object
        RecipeValueObject recipeVO = valueObjects.get(0);
        RecipeValueObject recipeVO2 = valueObjects.get(1);

        // One is saved
        Recipe recipeBeforeUpdate = recipeService.saveRecipe(recipeUser, recipeVO);
        String description = new String(recipeBeforeUpdate.getDescription());

        // And the second is used to update the saved recipe
        Recipe recipeAfterUpdate = recipeService.updateRecipe(recipeBeforeUpdate.getName(), recipeVO2);

        // Assert that the update is successful
        assertThat(recipeBeforeUpdate.getId()).isEqualTo(recipeAfterUpdate.getId());
        assertThat(description).isNotEqualTo(recipeAfterUpdate.getDescription());
    }

    @Test
    public void testDeleteRecipe() {
        RecipeValueObject recipeVO = valueObjects.get(1);
        Recipe savedRecipe = recipeService.saveRecipe(recipeUser, recipeVO);
        recipeService.deleteRecipe(savedRecipe.getName());

        assertThrows(RecipeNotFound.class, ()->recipeService.findRecipeByName(savedRecipe.getName()));
    }
    
}
