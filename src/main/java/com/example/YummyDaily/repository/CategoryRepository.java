package com.example.YummyDaily.repository;

import com.example.YummyDaily.entity.Category;
import com.example.YummyDaily.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByCategoryName(String categoryName);
    List<Category> findByCategoryNameContainingIgnoreCase(String keyword);

    Optional<Category> findByCategoryName(String name);
    List<Category> findByCategoryImageContainingIgnoreCase(String imagePath);

    // 🔥 Thêm mới:
    Page<Category> findAll(Pageable pageable);
    Page<Category> findByCategoryNameContainingIgnoreCase(String keyword, Pageable pageable);
}

