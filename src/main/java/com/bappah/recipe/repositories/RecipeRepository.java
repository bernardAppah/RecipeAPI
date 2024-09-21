package com.bappah.recipe.repositories;

import com.bappah.recipe.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;


public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    @Query("SELECT r FROM Recipe r WHERE r.name = :name")
    Optional<Recipe> findByName(String name);

    @Query("SELECT r FROM Recipe r WHERE r.name IN :names")
    List<Recipe> findByNameIn(Collection<String> names);


    List<Recipe> findByRecipeUserId(Long id);

    Set<Recipe> findByInstructionsContainingIgnoreCase(String token);

    Set<Recipe> findByIsVegetarianAndRecipeUserId(boolean isVegetarian, long id);

    Set<Recipe> findByServingsGreaterThanEqual(int servings);

    @Transactional
    @Modifying
    @Query("UPDATE Recipe r SET r.name = :newName WHERE r.name = :oldname")
    void updateTheNameOfAGivenRecipeIgnoreCase(String oldname, String newName);

    @Query("SELECT r FROM Recipe r JOIN r.ingredients i " +
        "WHERE i.ingredientName = :include AND NOT i.ingredientName = :exclude")
    Set<Recipe> filterByIngredientNameIgnoreCase(String include, String exclude);
}
