package com.example.Phygital_Core.config;

import com.example.Phygital_Core.entity.User;
import com.example.Phygital_Core.entity.UserRole;
import com.example.Phygital_Core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Если админа нет, создаем его
        if (userRepository.findByEmailIgnoreCase("admin@phygital.local").isEmpty()) {
            User admin = User.builder()
                    .firstName("Главный")
                    .lastName("Админ")
                    .email("admin@phygital.local")
                    .password(passwordEncoder.encode("password")) // Java сам сделает правильный хэш!
                    .role(UserRole.ADMIN)
                    .createdAt(LocalDateTime.now())
                    .build();
            userRepository.save(admin);
            System.out.println("=====================================================");
            System.out.println("✅ АДМИН УСПЕШНО СОЗДАН: admin@phygital.local / password");
            System.out.println("=====================================================");
        }

        // Если обычного игрока нет, создаем его
        if (userRepository.findByEmailIgnoreCase("user@phygital.local").isEmpty()) {
            User user = User.builder()
                    .firstName("Иван")
                    .lastName("Петров")
                    .email("user@phygital.local")
                    .password(passwordEncoder.encode("password"))
                    .role(UserRole.USER)
                    .createdAt(LocalDateTime.now())
                    .build();
            userRepository.save(user);
            System.out.println("✅ ИГРОК УСПЕШНО СОЗДАН: user@phygital.local / password");
            System.out.println("=====================================================");
        }
    }
}
