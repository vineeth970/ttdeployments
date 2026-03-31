package com.scrms.config;

import com.scrms.entity.SlaPolicy;
import com.scrms.entity.User;
import com.scrms.enums.Category;
import com.scrms.enums.Priority;
import com.scrms.enums.Role;
import com.scrms.repository.SlaPolicyRepository;
import com.scrms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final SlaPolicyRepository slaPolicyRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedUsers();
        seedSlaPolicies();
        log.info("========== Data Seeding Complete ==========");
    }

    private void seedUsers() {
        if (userRepository.count() > 0) return;

        userRepository.save(User.builder()
                .fullName("System Admin")
                .email("admin@scrms.com")
                .password(passwordEncoder.encode("admin123"))
                .role(Role.ROLE_ADMIN)
                .department("IT")
                .phone("9000000001")
                .active(true)
                .build());

        userRepository.save(User.builder()
                .fullName("Operations Manager")
                .email("manager@scrms.com")
                .password(passwordEncoder.encode("manager123"))
                .role(Role.ROLE_MANAGER)
                .department("Operations")
                .phone("9000000002")
                .active(true)
                .build());

        userRepository.save(User.builder()
                .fullName("Support Agent One")
                .email("agent1@scrms.com")
                .password(passwordEncoder.encode("agent123"))
                .role(Role.ROLE_AGENT)
                .department("Support")
                .phone("9000000003")
                .active(true)
                .build());

        userRepository.save(User.builder()
                .fullName("Support Agent Two")
                .email("agent2@scrms.com")
                .password(passwordEncoder.encode("agent123"))
                .role(Role.ROLE_AGENT)
                .department("Support")
                .phone("9000000004")
                .active(true)
                .build());

        userRepository.save(User.builder()
                .fullName("John Customer")
                .email("customer@scrms.com")
                .password(passwordEncoder.encode("customer123"))
                .role(Role.ROLE_CUSTOMER)
                .department("N/A")
                .phone("9000000005")
                .active(true)
                .build());

        log.info("Seeded 5 default users.");
    }

    private void seedSlaPolicies() {
        if (slaPolicyRepository.count() > 0) return;

        // CRITICAL - 4h response, 8h resolution
        for (Category cat : Category.values()) {
            slaPolicyRepository.save(SlaPolicy.builder()
                    .category(cat).priority(Priority.CRITICAL)
                    .responseHours(4).resolutionHours(8).build());
            slaPolicyRepository.save(SlaPolicy.builder()
                    .category(cat).priority(Priority.HIGH)
                    .responseHours(8).resolutionHours(24).build());
            slaPolicyRepository.save(SlaPolicy.builder()
                    .category(cat).priority(Priority.MEDIUM)
                    .responseHours(24).resolutionHours(72).build());
            slaPolicyRepository.save(SlaPolicy.builder()
                    .category(cat).priority(Priority.LOW)
                    .responseHours(48).resolutionHours(120).build());
        }

        log.info("Seeded SLA policies for all categories and priorities.");
    }
}
