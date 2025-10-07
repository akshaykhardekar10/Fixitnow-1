package com.fixitnow.config;

import com.fixitnow.model.Role;
import com.fixitnow.model.ServiceCategory;
import com.fixitnow.model.User;
import com.fixitnow.repository.ServiceCategoryRepository;
import com.fixitnow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ServiceCategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Create default admin user if no admin exists
        if (!userRepository.existsByRole(Role.ADMIN)) {
            User adminUser = User.builder()
                    .name("System Administrator")
                    .email("admin@fixitnow.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .location("System")
                    .enabled(true)
                    .build();
            
            userRepository.save(adminUser);
            log.info("✅ Default admin user created:");
            log.info("   Email: admin@fixitnow.com");
            log.info("   Password: admin123");
            log.info("   Role: ADMIN");
        }

        // Create default service categories if none exist
        if (categoryRepository.count() == 0) {
            List<ServiceCategory> defaultCategories = Arrays.asList(
                ServiceCategory.builder()
                    .name("Home Cleaning")
                    .description("Professional home cleaning services")
                    .iconUrl("🏠")
                    .build(),
                ServiceCategory.builder()
                    .name("Plumbing")
                    .description("Plumbing repair and installation services")
                    .iconUrl("🔧")
                    .build(),
                ServiceCategory.builder()
                    .name("Electrical")
                    .description("Electrical repair and installation services")
                    .iconUrl("⚡")
                    .build(),
                ServiceCategory.builder()
                    .name("Gardening")
                    .description("Garden maintenance and landscaping services")
                    .iconUrl("🌱")
                    .build(),
                ServiceCategory.builder()
                    .name("Handyman")
                    .description("General repair and maintenance services")
                    .iconUrl("🔨")
                    .build(),
                ServiceCategory.builder()
                    .name("Painting")
                    .description("Interior and exterior painting services")
                    .iconUrl("🎨")
                    .build()
            );
            
            categoryRepository.saveAll(defaultCategories);
            log.info("✅ Default service categories created: {}", defaultCategories.size());
        }

        // Log current statistics
        long totalUsers = userRepository.count();
        long adminCount = userRepository.countByRole(Role.ADMIN);
        long providerCount = userRepository.countByRole(Role.PROVIDER);
        long customerCount = userRepository.countByRole(Role.CUSTOMER);
        long totalCategories = categoryRepository.count();

        log.info("📊 Current Statistics:");
        log.info("   Total Users: {}", totalUsers);
        log.info("   Admins: {}", adminCount);
        log.info("   Providers: {}", providerCount);
        log.info("   Customers: {}", customerCount);
        log.info("   Service Categories: {}", totalCategories);
    }
}
