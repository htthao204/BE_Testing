package com.example.YummyDaily.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name="rating")
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long ratingId;

    float ratingScore;
    String ratingImage;
    @Column(length = 1000)  // Cột này sẽ có kiểu VARCHAR(1000)
    String description;
    LocalDate date;


    @ManyToOne
    @JoinColumn(name = "recipe_id", nullable = false)
    Recipe recipe;  // Quan hệ ManyToOne với Recipe

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    User user;  // Quan hệ ManyToOne với User

    @OneToMany(mappedBy = "rating", cascade = CascadeType.ALL)
    @JsonIgnore
    List<Notification> notifications;


}
