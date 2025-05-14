package com.example.YummyDaily.entity;

import com.example.YummyDaily.enums.Difficultylevel;
import com.example.YummyDaily.enums.StateRecipe;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "recipe")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long recipeId;
    String recipeName;
    @Column(length = 1000)  // Cột này sẽ có kiểu VARCHAR(1000)
    String description;
    String recipeImage;
    int cookingTime;
    Difficultylevel difficultyLevel;
    int view;
    int numberOfPeople;
    LocalDate generatedDate;
    StateRecipe state;
    String introductionVideo;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    Category category;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    User user;  // Quan hệ ManyToOne với User

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    Set<CookingStep> cookingSteps;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, fetch = FetchType.LAZY)

    @JsonIgnore
    Set<Rating> ratings;
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, fetch = FetchType.LAZY)

    @JsonIgnore
    Set<IngredientRecipe> ingredientRecipes;
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    List<Notification> notifications;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    List <UserFavorite> userFavorites;

}
