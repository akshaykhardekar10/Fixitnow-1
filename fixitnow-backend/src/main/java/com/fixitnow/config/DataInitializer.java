package com.fixitnow.config;

import com.fixitnow.model.Role;
import com.fixitnow.model.ServiceCategory;
import com.fixitnow.model.ServiceSubcategory;
import com.fixitnow.model.User;
import com.fixitnow.repository.ServiceCategoryRepository;
import com.fixitnow.repository.ServiceSubcategoryRepository;
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
    private final ServiceSubcategoryRepository subcategoryRepository;
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

        // Create default subcategories if none exist
        if (subcategoryRepository.count() == 0) {
            List<ServiceCategory> categories = categoryRepository.findAll();
            
            for (ServiceCategory category : categories) {
                List<ServiceSubcategory> subcategories = null;
                
                switch (category.getName()) {
                    case "Home Cleaning":
                        subcategories = Arrays.asList(
                            createSubcategory("Deep Cleaning", "Thorough cleaning of entire home", category),
                            createSubcategory("Regular Cleaning", "Routine maintenance cleaning", category),
                            createSubcategory("Move-in/Move-out Cleaning", "Cleaning for moving purposes", category),
                            createSubcategory("Carpet Cleaning", "Professional carpet and upholstery cleaning", category)
                        );
                        break;
                    case "Plumbing":
                        subcategories = Arrays.asList(
                            createSubcategory("Leak Repair", "Fix leaking pipes and faucets", category),
                            createSubcategory("Drain Cleaning", "Clear clogged drains", category),
                            createSubcategory("Installation", "Install new fixtures and appliances", category),
                            createSubcategory("Emergency Plumbing", "24/7 emergency plumbing services", category)
                        );
                        break;
                    case "Electrical":
                        subcategories = Arrays.asList(
                            createSubcategory("Wiring & Rewiring", "Electrical wiring services", category),
                            createSubcategory("Lighting Installation", "Install lights and fixtures", category),
                            createSubcategory("Electrical Repair", "Fix electrical issues", category),
                            createSubcategory("Panel Upgrades", "Upgrade electrical panels", category)
                        );
                        break;
                    case "Gardening":
                        subcategories = Arrays.asList(
                            createSubcategory("Lawn Maintenance", "Mowing and lawn care", category),
                            createSubcategory("Landscaping", "Garden design and landscaping", category),
                            createSubcategory("Tree Trimming", "Tree and shrub pruning", category),
                            createSubcategory("Garden Design", "Plan and design gardens", category)
                        );
                        break;
                    case "Handyman":
                        subcategories = Arrays.asList(
                            createSubcategory("Furniture Assembly", "Assemble furniture and fixtures", category),
                            createSubcategory("Home Repairs", "General home repair services", category),
                            createSubcategory("Door & Window Repair", "Fix doors and windows", category),
                            createSubcategory("Drywall Repair", "Patch and repair drywall", category)
                        );
                        break;
                    case "Painting":
                        subcategories = Arrays.asList(
                            createSubcategory("Interior Painting", "Paint interior walls and rooms", category),
                            createSubcategory("Exterior Painting", "Paint exterior surfaces", category),
                            createSubcategory("Cabinet Painting", "Refinish and paint cabinets", category),
                            createSubcategory("Wallpaper Installation", "Install and remove wallpaper", category)
                        );
                        break;
                }
                
                if (subcategories != null) {
                    subcategoryRepository.saveAll(subcategories);
                }
            }
            
            log.info("✅ Default service subcategories created: {}", subcategoryRepository.count());
        }

        // Log current statistics
        long totalUsers = userRepository.count();
        long adminCount = userRepository.countByRole(Role.ADMIN);
        long providerCount = userRepository.countByRole(Role.PROVIDER);
        long customerCount = userRepository.countByRole(Role.CUSTOMER);
        long totalCategories = categoryRepository.count();
        long totalSubcategories = subcategoryRepository.count();

        log.info("📊 Current Statistics:");
        log.info("   Total Users: {}", totalUsers);
        log.info("   Admins: {}", adminCount);
        log.info("   Providers: {}", providerCount);
        log.info("   Customers: {}", customerCount);
        log.info("   Service Categories: {}", totalCategories);
        log.info("   Service Subcategories: {}", totalSubcategories);
    }
    
    private ServiceSubcategory createSubcategory(String name, String description, ServiceCategory category) {
        return ServiceSubcategory.builder()
                .name(name)
                .description(description)
                .category(category)
                .active(true)
                .build();
    }
}
