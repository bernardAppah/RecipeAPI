package com.bappah.recipe.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class RecipeUser {

    @SequenceGenerator(name= "USER_SEQUENCE", sequenceName = "USER_SEQUENCE_ID", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="USER_SEQUENCE")
    @Id
    private long id;

    @Column(nullable = false, unique=true)
    private String username;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    private LocalDate createdAt;


    @JsonIgnore
    @JoinTable(name = "my_recipes",
        joinColumns = {@JoinColumn(name = "user_id")},
        inverseJoinColumns = {@JoinColumn(name = "recipe_id")}
    )
    @OneToMany(targetEntity = Recipe.class, fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.REMOVE)
    private List<Recipe> myRecipes = new ArrayList<>();;

    public RecipeUser(String name, String password) {
        this.username = name;
        this.password = password;
        this.createdAt = LocalDate.now();
    }
}
