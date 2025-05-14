package com.example.YummyDaily.repository;

import com.example.YummyDaily.entity.User;
import com.example.YummyDaily.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository  // Đảm bảo @Repository có mặt
public interface UserRepository extends JpaRepository<User, Long> {
    // Kiểm tra username đã tồn tại chưa
    boolean existsByUsername(String username);

    // Tìm kiếm người dùng theo username
    Optional<User> findByUsername(String username);

    // Tìm kiếm người dùng theo fullName (hỗ trợ tìm kiếm không phân biệt chữ hoa/thường)
    List<User> findByFullNameContainingIgnoreCase(String fullName);
    List<User> findByRolesContaining(Role role);
    // Thêm phương thức tìm tất cả admin users
    List<User> findAllByRolesContaining(Role role);
}
