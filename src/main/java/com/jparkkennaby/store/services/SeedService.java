package com.jparkkennaby.store.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.github.javafaker.Faker;
import com.jparkkennaby.store.exceptions.DatabaseSeedingException;
import com.jparkkennaby.store.products.Category;
import com.jparkkennaby.store.products.CategoryRepository;
import com.jparkkennaby.store.products.Product;
import com.jparkkennaby.store.products.ProductRepository;
import com.jparkkennaby.store.repositories.CartRepository;
import com.jparkkennaby.store.repositories.OrderRepository;
import com.jparkkennaby.store.users.Role;
import com.jparkkennaby.store.users.User;
import com.jparkkennaby.store.users.UserRepository;

import lombok.AllArgsConstructor;

/**
 * SeedService:
 * 
 * This code is hacky and should never really be used in production.
 * It could be worth while moving some of this functionality to the relevent
 * service, but for now it has been put here to keep the concerning code in one
 * place.
 */

@AllArgsConstructor
@Service
public class SeedService {
    private Faker faker;
    private CategoryRepository categoryRepository;
    private ProductRepository productRepository;
    private UserRepository userRepository;
    private CartRepository cartRepository;
    private OrderRepository orderRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private List<Category> categories;

    public void seedDatabase() {
        clearTables();

        // seed categories/products
        categories = seedCategories();
        seedProducts();

        // seed users
        seedDefaultUser();
        seedUsers();
    }

    public void resetAutoIncrement(String tableName) {
        Set<String> allowedTables = Set.of(
                "orders",
                "carts",
                "users",
                "products",
                "categories");

        if (!allowedTables.contains(tableName)) {
            throw new IllegalArgumentException("Invalid table name");
        }

        String sql = String.format("ALTER TABLE %s AUTO_INCREMENT = %d", tableName, 0);
        try {
            jdbcTemplate.execute(sql);
        } catch (Exception e) {
            throw new DatabaseSeedingException("Failed to reset " + tableName + " table auto-increment value");
        }

    }

    public void clearTables() {
        orderRepository.deleteAll();
        resetAutoIncrement("orders");

        cartRepository.deleteAll();
        resetAutoIncrement("carts");

        userRepository.deleteAll();
        resetAutoIncrement("users");

        productRepository.deleteAll();
        resetAutoIncrement("products");

        categoryRepository.deleteAll();
        resetAutoIncrement("categories");
    }

    public List<Category> seedCategories() {
        try {
            return IntStream.range(0, 10)
                    .mapToObj(i -> {
                        Category category = new Category();
                        category.setName(faker.commerce().department());
                        return categoryRepository.save(category);
                    }).toList();
        } catch (Exception e) {
            throw new DatabaseSeedingException("Failed to seed categories");
        }
    }

    public Product generateFakeProduct() {
        String material = faker.commerce().material(); // e.g., "Rubber"
        String department = faker.commerce().department(); // e.g., "Home"
        String description = "A high-quality " + material + " item perfect for " + department + " lovers.";

        var product = new Product();
        product.setName(faker.commerce().productName());
        product.setDescription(description);
        product.setPrice(new BigDecimal(faker.commerce().price()));
        product.setCategory(categories.get(faker.random().nextInt(categories.size())));

        return product;
    }

    public void seedProducts() {
        try {
            var products = IntStream.range(0, 25);
            products.forEach(i -> {
                var product = generateFakeProduct();
                productRepository.save(product);
            });
        } catch (Exception e) {
            throw new DatabaseSeedingException("Failed to seed products");
        }
    }

    public void seedUsers() {
        try {
            var users = IntStream.range(0, 4);
            users.forEach(i -> {
                var user = new User();
                user.setName(faker.name().fullName());
                user.setEmail(faker.internet().emailAddress());
                user.setPassword(passwordEncoder.encode(faker.internet().password()));
                user.setRole(Role.USER); // TODO: make this apply USER by default
                userRepository.save(user);
            });
        } catch (Exception e) {
            throw new DatabaseSeedingException("Failed to seed users");
        }

    }

    public void seedDefaultUser() {
        try {
            var user = new User();
            user.setName("John Smith");
            user.setEmail("johnsmith@gmail.com");
            user.setPassword(passwordEncoder.encode("123456"));
            user.setRole(Role.USER); // TODO: make this apply USER by default
            userRepository.save(user);
        } catch (Exception e) {
            throw new DatabaseSeedingException("Failed to seed the default user");
        }

    }
}
