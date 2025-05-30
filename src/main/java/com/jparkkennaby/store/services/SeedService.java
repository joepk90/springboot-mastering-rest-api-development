package com.jparkkennaby.store.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.github.javafaker.Faker;

import com.jparkkennaby.store.entities.Category;
import com.jparkkennaby.store.entities.Product;
import com.jparkkennaby.store.entities.User;
import com.jparkkennaby.store.exceptions.DatabaseSeedingException;
import com.jparkkennaby.store.repositories.CartRepository;
import com.jparkkennaby.store.repositories.CategoryRepository;
import com.jparkkennaby.store.repositories.OrderRepository;
import com.jparkkennaby.store.repositories.ProductRepository;
import com.jparkkennaby.store.repositories.UserRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class SeedService {
    private CategoryRepository categoryRepository;
    private ProductRepository productRepository;
    private UserRepository userRepository;
    private CartRepository cartRepository;
    private OrderRepository orderRepository;
    private PasswordEncoder passwordEncoder;

    private Faker faker = new Faker(new Locale("en"));
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

    public void clearTables() {
        orderRepository.deleteAll();
        cartRepository.deleteAll();
        userRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();
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

    public void seedProducts() {
        try {
            var products = IntStream.range(0, 25);
            products.forEach(i -> {
                var product = new Product();
                product.setName(faker.commerce().productName());
                product.setPrice(new BigDecimal(faker.commerce().price()));
                product.setCategory(categories.get(faker.random().nextInt(categories.size())));
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
                user.setEmail(passwordEncoder.encode(faker.internet().password()));
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
            user.setPassword("123456");
        } catch (Exception e) {
            throw new DatabaseSeedingException("Failed to seed the default user");
        }

    }
}
