package com.example.YummyDaily.config;

import com.example.YummyDaily.entity.User;
import com.example.YummyDaily.enums.Role;
import com.example.YummyDaily.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {
    final PasswordEncoder passwordEncoder;

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository) {
        return args -> {
            userRepository.findByUsername("admin").ifPresentOrElse(
                    user -> log.info("Admin user already exists."),
                    () -> {
                        User adminUser = User.builder()
                                .username("admin")
                                .password(passwordEncoder.encode("admin"))
                                .roles(Set.of(Role.ADMIN))  // Dùng Set<Role>
                                .build();

                        userRepository.save(adminUser);
                        log.warn("Admin user has been created. Please change the default password.");
                    }
            );
        };
    }
}
