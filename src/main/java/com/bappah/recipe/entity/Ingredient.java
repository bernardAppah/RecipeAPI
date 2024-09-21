package com.bappah.recipe.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "ingredient")
public class Ingredient {


    @SequenceGenerator(name= "INGREDIENT_SEQUENCE", sequenceName = "INGREDIENT_SEQUENCE_ID", allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.IDENTITY, generator="INGREDIENT_SEQUENCE")
    @Id
    private long id;

    private String ingredientName;

    public Ingredient(String ingredientName) {
        this.ingredientName = StringUtils.lowerCase(ingredientName);
    }
}
