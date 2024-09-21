package com.bappah.recipe.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
@ToString
@Builder
@Table(name = "recipe", indexes = @Index(name = "user_index", columnList = "user_id"),
    uniqueConstraints = { @UniqueConstraint(name = "UniqueRecipe", columnNames = { "name", "isVegetarian"})})
@Entity
public class Recipe {

    @JsonIgnore
    @SequenceGenerator(name= "RECIPE_SEQUENCE", sequenceName = "RECIPE_SEQUENCE_ID", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.IDENTITY, generator="RECIPE_SEQUENCE")
    @Id
    private long id;

    @Column(unique=true)
    private String name;

    private String description;

    private String instructions;

    private boolean isVegetarian;

    private int servings;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private RecipeUser recipeUser;

    @ToString.Exclude
    @JoinTable(name="recipe_ingredients",
            joinColumns = {@JoinColumn(name = "recipe_id")},
            inverseJoinColumns = {@JoinColumn(name = "ingredient_id")}
    )
    @OneToMany(targetEntity = Ingredient.class, cascade = CascadeType.PERSIST)
    private Set<Ingredient> ingredients;

    public Recipe(String name, String description, String instructions, boolean isVegetarian, int servings) {
        this.name = name;
        this.description = description;
        this.instructions = instructions;
        this.isVegetarian = isVegetarian;
        this.servings = servings;
        ingredients = new HashSet<>();
    }
}
