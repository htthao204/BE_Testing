package com.example.YummyDaily.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name="cookingstep")
public class CookingStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long cookingId;
    Integer stepNumber;
    @Column(length = 1000)  // Cột này sẽ có kiểu VARCHAR(1000)
    String description;


    String cookingImg;

    @ManyToOne
    @JoinColumn(name = "recipe_id", nullable = false)
    Recipe recipe;  // Quan hệ ManyToOne với Recipe
}
