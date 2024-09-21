package com.bappah.recipe;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.bappah.recipe.entity.RecipeUser;
import com.bappah.recipe.entity.RecipeValueObject;
import com.bappah.recipe.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class RecipeApplicationIntegrationTest {

	private final static String USER_NAME = "IntegrAPIuSER" + new Random().nextInt();
	private final static String USER_PASSWORD = "password";
	private final static String EBA = "Eba", SUYA ="Suya", LASAGNE = "Lasagne", EGUSI = "Egusi";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserService userService;
	@MockBean
	private RecipeUser recipeUser;

	@Autowired
	private ObjectMapper mapper;
	private List<RecipeValueObject> valueObjects;

	@BeforeEach
	void init() throws Exception {
		valueObjects = List.of(
			new RecipeValueObject("Suya", "Roasted meat",
				"Smoked seasoned beef", false, 3, "Tomato", "onions", "goat meat", "suya pepper"),
			new RecipeValueObject("Lasagne", "A type of pasta, made of very wide, flat sheets ",
				"Roast and season raw beef", false, 12, "Pasta", "Tomato", "Ground beef", "Garlic", "Cheese"),
			new RecipeValueObject("Egusi soup", "A Nigerian vegetable soup",
				"", true, 6, "Egusi", "Palm oil", "Onions", "Garlic", "Uwgu", "Water leaf"),
			new RecipeValueObject("Eba", "Fufu made from garri and hot water",
				"Boil water, then pour garri and stir", false, 2, "Garri", "Hot water")
		);

		mockMvc.perform(
				post("/api/recipe/register-user")
					.param("name", USER_NAME)
					.param("password", USER_PASSWORD))
			.andExpect(status().isCreated()).andReturn();

		recipeUser = userService.findUserByUsername(USER_NAME);
		recipeUser.setMyRecipes(new ArrayList<>());
	}

	@Test
	public void contextLoads() throws Exception {
		assertThat(mockMvc).isNotNull();
		assertThat(recipeUser).isNotNull();
	}


	@Test
	public void addRecipe() throws Exception {
		String json = mapper.writeValueAsString(valueObjects.get(0));

		MvcResult mvcResult = mockMvc.perform(
				post("/api/recipe/add-recipe")
					.param("userId", String.valueOf(recipeUser.getId()))
					.contentType(MediaType.APPLICATION_JSON_VALUE)
					.content(json))
			.andExpect(status().isCreated()).andReturn();
	}

	@Test
	public void findMyRecipes() throws Exception {

		String json = mapper.writeValueAsString(valueObjects);

		mockMvc.perform(post("/api/recipe/add-recipes")
				.param("userId", String.valueOf(recipeUser.getId()))
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content(json))
			.andExpect(status().isCreated());

		mockMvc.perform(get("/api/recipe/find-my-recipes")
				.param("userId", String.valueOf(recipeUser.getId()))
			)
			.andExpect(status().isOk());
	}

	@Test
	public void testUpdateRecipeName() throws Exception {

		RecipeValueObject ebaVO = valueObjects.get(0);
		final String newerName = "newRandomRecipeName";

		String json = mapper.writeValueAsString(ebaVO);

		MvcResult mvcResult = mockMvc.perform(
				post("/api/recipe/add-recipe")
					.param("userId", String.valueOf(recipeUser.getId()))
					.contentType(MediaType.APPLICATION_JSON_VALUE)
					.content(json))
			.andExpect(status().isCreated()).andReturn();

		mockMvc.perform(
				put("/api/recipe/update-recipe-name")
					.param("userId", String.valueOf(recipeUser.getId()))
					.param("name", ebaVO.name)
					.param("newName", newerName)
					.contentType(MediaType.APPLICATION_JSON_VALUE))
			.andExpect(status().isOk());
	}

	@Test
	public void shouldUpdateRecipeDetails() throws Exception {

		RecipeValueObject VO1 = valueObjects.get(0);
		RecipeValueObject VO2 = valueObjects.get(0);

		String json = mapper.writeValueAsString(VO1);
		String updateJson = mapper.writeValueAsString(VO2);

		MvcResult mvcResult = mockMvc.perform(
				post("/api/recipe/add-recipe")
					.param("userId", String.valueOf(recipeUser.getId()))
					.contentType(MediaType.APPLICATION_JSON_VALUE)
					.content(json))
			.andExpect(status().isCreated()).andReturn();

		mockMvc.perform(
				patch("/api/recipe/update-recipe-details")
					.param("userId", String.valueOf(recipeUser.getId()))
					.param("name", VO1.name)
					.contentType(MediaType.APPLICATION_JSON_VALUE)
					.content(updateJson))
			.andExpect(status().isOk());
	}
}