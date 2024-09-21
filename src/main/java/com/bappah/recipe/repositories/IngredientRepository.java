package com.bappah.recipe.repositories;

import com.bappah.recipe.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

    Optional<Ingredient> findByIngredientName(String name);
    boolean existsByIngredientName(String name);
    long countByIngredientName(String name);
}
