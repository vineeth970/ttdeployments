package com.scrms.config;

import com.scrms.entity.SlaPolicy;
import com.scrms.entity.User;
import com.scrms.enums.Category;
import com.scrms.enums.Priority;
import com.scrms.enums.Role;
import com.scrms.repository.SlaPolicyRepository;
import com.scrms.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final SlaPolicyRepository slaPolicyRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository,
            SlaPolicyRepository slaPolicyRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.slaPolicyRepository = slaPolicyRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        seedUsers();
        seedSlaPolicies();
        System.out.println("========== Data Seeding Complete ==========");
    }

    public void seedUsers() {
        if (userRepository.count() > 0)
            return;

        User admin = new User();
        admin.setFullName("System Admin");
        admin.setEmail("admin@scrms.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole(Role.ROLE_ADMIN);
        admin.setDepartment("IT");
        admin.setPhone("9000000001");
        admin.setActive(true);
        userRepository.save(admin);

        User manager = new User();
        manager.setFullName("Operations Manager");
        manager.setEmail("manager@scrms.com");
        manager.setPassword(passwordEncoder.encode("manager123"));
        manager.setRole(Role.ROLE_MANAGER);
        manager.setDepartment("Operations");
        manager.setPhone("9000000002");
        manager.setActive(true);
        userRepository.save(manager);

        User agent1 = new User();
        agent1.setFullName("Support Agent One");
        agent1.setEmail("agent1@scrms.com");
        agent1.setPassword(passwordEncoder.encode("agent123"));
        agent1.setRole(Role.ROLE_AGENT);
        agent1.setDepartment("Support");
        agent1.setPhone("9000000003");
        agent1.setActive(true);
        userRepository.save(agent1);

        User agent2 = new User();
        agent2.setFullName("Support Agent Two");
        agent2.setEmail("agent2@scrms.com");
        agent2.setPassword(passwordEncoder.encode("agent123"));
        agent2.setRole(Role.ROLE_AGENT);
        agent2.setDepartment("Support");
        agent2.setPhone("9000000004");
        agent2.setActive(true);
        userRepository.save(agent2);

        User customer = new User();
        customer.setFullName("John Customer");
        customer.setEmail("customer@scrms.com");
        customer.setPassword(passwordEncoder.encode("customer123"));
        customer.setRole(Role.ROLE_CUSTOMER);
        customer.setDepartment("N/A");
        customer.setPhone("9000000005");
        customer.setActive(true);
        userRepository.save(customer);

        System.out.println("Seeded 5 default users.");
    }

    public void seedSlaPolicies() {
        if (slaPolicyRepository.count() > 0)
            return;

        for (Category cat : Category.values()) {
            SlaPolicy critical = new SlaPolicy();
            critical.setCategory(cat);
            critical.setPriority(Priority.CRITICAL);
            critical.setResponseHours(4);
            critical.setResolutionHours(8);
            slaPolicyRepository.save(critical);

            SlaPolicy high = new SlaPolicy();
            high.setCategory(cat);
            high.setPriority(Priority.HIGH);
            high.setResponseHours(8);
            high.setResolutionHours(24);
            slaPolicyRepository.save(high);

            SlaPolicy medium = new SlaPolicy();
            medium.setCategory(cat);
            medium.setPriority(Priority.MEDIUM);
            medium.setResponseHours(24);
            medium.setResolutionHours(72);
            slaPolicyRepository.save(medium);

            SlaPolicy low = new SlaPolicy();
            low.setCategory(cat);
            low.setPriority(Priority.LOW);
            low.setResponseHours(48);
            low.setResolutionHours(120);
            slaPolicyRepository.save(low);
        }

        System.out.println("Seeded SLA policies.");
    }
}
