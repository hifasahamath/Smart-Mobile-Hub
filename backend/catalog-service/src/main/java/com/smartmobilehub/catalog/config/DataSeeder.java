package com.smartmobilehub.catalog.config;

import com.smartmobilehub.catalog.entity.*;
import com.smartmobilehub.catalog.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;
import java.util.*;

/**
 * Seeds the database with realistic mobile phone product data for development.
 * Only runs when the "seed" profile is active OR when the database is empty.
 */
@Configuration
public class DataSeeder {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    @Bean
    CommandLineRunner seedData(CategoryRepository categoryRepo, BrandRepository brandRepo,
                               ProductRepository productRepo, SkuRepository skuRepo) {
        return args -> {
            // Only seed if database is empty
            if (categoryRepo.count() > 0) {
                log.info("Database already seeded, skipping.");
                return;
            }

            log.info("Seeding catalog database...");

            // --- Categories ---
            Category smartphones = createCategory(categoryRepo, "Smartphones", "smartphones",
                    "Latest smartphones from top brands", null);
            Category tablets = createCategory(categoryRepo, "Tablets", "tablets",
                    "Tablets for work and entertainment", null);
            Category accessories = createCategory(categoryRepo, "Accessories", "accessories",
                    "Phone and tablet accessories", null);

            // Sub-categories
            Category flagships = createCategory(categoryRepo, "Flagship Phones", "flagship-phones",
                    "Premium flagship smartphones", smartphones);
            Category midRange = createCategory(categoryRepo, "Mid-Range Phones", "mid-range-phones",
                    "Great value mid-range smartphones", smartphones);
            Category cases = createCategory(categoryRepo, "Cases & Covers", "cases-covers",
                    "Protective cases and covers", accessories);
            Category chargers = createCategory(categoryRepo, "Chargers & Cables", "chargers-cables",
                    "Fast chargers and USB cables", accessories);

            // --- Brands ---
            Brand apple = createBrand(brandRepo, "Apple", "apple", "Premium consumer electronics");
            Brand samsung = createBrand(brandRepo, "Samsung", "samsung", "Global electronics leader");
            Brand google = createBrand(brandRepo, "Google", "google", "Pixel phones with pure Android");
            Brand oneplus = createBrand(brandRepo, "OnePlus", "oneplus", "Never Settle — flagship killers");
            Brand xiaomi = createBrand(brandRepo, "Xiaomi", "xiaomi", "Innovation for everyone");

            // --- Products ---

            // iPhone 15 Pro Max
            Product iphone15pm = createProduct(productRepo, "iPhone 15 Pro Max", "iphone-15-pro-max",
                    apple, flagships,
                    "The most powerful iPhone ever. Featuring the A17 Pro chip, a titanium design, " +
                    "a 48MP main camera with 5x optical zoom, and Action button. " +
                    "Available in Natural Titanium, Blue Titanium, White Titanium, and Black Titanium.",
                    "A17 Pro chip, titanium design, 48MP camera, 5x optical zoom",
                    new BigDecimal("1199.00"), new BigDecimal("1299.00"),
                    "{\"display\":\"6.7-inch Super Retina XDR OLED\",\"processor\":\"A17 Pro\",\"camera\":\"48MP + 12MP + 12MP\",\"battery\":\"4441 mAh\",\"os\":\"iOS 17\"}",
                    true, true);

            // Add variant groups
            VariantGroup iphoneStorage = addVariantGroup(iphone15pm, "Storage", 0,
                    List.of("256GB", "512GB", "1TB"));
            VariantGroup iphoneColor = addVariantGroup(iphone15pm, "Color", 1,
                    List.of("Natural Titanium", "Blue Titanium", "White Titanium", "Black Titanium"));
            productRepo.save(iphone15pm);

            // Add SKUs
            Map<String, VariantOption> storageOpts = mapOptions(iphoneStorage);
            Map<String, VariantOption> colorOpts = mapOptions(iphoneColor);

            addSku(skuRepo, iphone15pm, "IPH15PM-256-NAT", new BigDecimal("1199.00"), null,
                    Set.of(storageOpts.get("256GB"), colorOpts.get("Natural Titanium")));
            addSku(skuRepo, iphone15pm, "IPH15PM-256-BLU", new BigDecimal("1199.00"), null,
                    Set.of(storageOpts.get("256GB"), colorOpts.get("Blue Titanium")));
            addSku(skuRepo, iphone15pm, "IPH15PM-512-NAT", new BigDecimal("1399.00"), null,
                    Set.of(storageOpts.get("512GB"), colorOpts.get("Natural Titanium")));
            addSku(skuRepo, iphone15pm, "IPH15PM-1TB-BLK", new BigDecimal("1599.00"), null,
                    Set.of(storageOpts.get("1TB"), colorOpts.get("Black Titanium")));

            // Samsung Galaxy S24 Ultra
            Product galaxyS24 = createProduct(productRepo, "Samsung Galaxy S24 Ultra", "samsung-galaxy-s24-ultra",
                    samsung, flagships,
                    "Galaxy AI is here. The Galaxy S24 Ultra features a Snapdragon 8 Gen 3 processor, " +
                    "a 200MP camera, built-in S Pen, and a titanium frame. " +
                    "Circle to Search, Live Translate, and more AI-powered features.",
                    "Snapdragon 8 Gen 3, 200MP camera, S Pen, Galaxy AI",
                    new BigDecimal("1299.99"), null,
                    "{\"display\":\"6.8-inch Dynamic AMOLED 2X\",\"processor\":\"Snapdragon 8 Gen 3\",\"camera\":\"200MP + 12MP + 50MP + 10MP\",\"battery\":\"5000 mAh\",\"os\":\"Android 14, One UI 6.1\"}",
                    true, true);

            VariantGroup s24Storage = addVariantGroup(galaxyS24, "Storage", 0,
                    List.of("256GB", "512GB", "1TB"));
            VariantGroup s24Color = addVariantGroup(galaxyS24, "Color", 1,
                    List.of("Titanium Gray", "Titanium Black", "Titanium Violet", "Titanium Yellow"));
            productRepo.save(galaxyS24);

            Map<String, VariantOption> s24StorageOpts = mapOptions(s24Storage);
            Map<String, VariantOption> s24ColorOpts = mapOptions(s24Color);
            addSku(skuRepo, galaxyS24, "SGS24U-256-GRY", new BigDecimal("1299.99"), null,
                    Set.of(s24StorageOpts.get("256GB"), s24ColorOpts.get("Titanium Gray")));
            addSku(skuRepo, galaxyS24, "SGS24U-512-BLK", new BigDecimal("1419.99"), null,
                    Set.of(s24StorageOpts.get("512GB"), s24ColorOpts.get("Titanium Black")));

            // Google Pixel 8 Pro
            Product pixel8 = createProduct(productRepo, "Google Pixel 8 Pro", "google-pixel-8-pro",
                    google, flagships,
                    "The best of Google AI in a phone. Tensor G3 chip delivers the best photo and video " +
                    "experience on Pixel yet, plus new AI features like Best Take, Magic Editor, " +
                    "and call screening. 7 years of OS and security updates.",
                    "Tensor G3, 50MP camera, 7 years of updates, AI powered",
                    new BigDecimal("999.00"), new BigDecimal("1099.00"),
                    "{\"display\":\"6.7-inch LTPO OLED, 120Hz\",\"processor\":\"Google Tensor G3\",\"camera\":\"50MP + 48MP + 48MP\",\"battery\":\"5050 mAh\",\"os\":\"Android 14\"}",
                    true, false);

            VariantGroup p8Storage = addVariantGroup(pixel8, "Storage", 0, List.of("128GB", "256GB", "512GB"));
            VariantGroup p8Color = addVariantGroup(pixel8, "Color", 1, List.of("Obsidian", "Porcelain", "Bay"));
            productRepo.save(pixel8);

            Map<String, VariantOption> p8StorageOpts = mapOptions(p8Storage);
            Map<String, VariantOption> p8ColorOpts = mapOptions(p8Color);
            addSku(skuRepo, pixel8, "PX8P-128-OBS", new BigDecimal("999.00"), new BigDecimal("1099.00"),
                    Set.of(p8StorageOpts.get("128GB"), p8ColorOpts.get("Obsidian")));
            addSku(skuRepo, pixel8, "PX8P-256-POR", new BigDecimal("1059.00"), null,
                    Set.of(p8StorageOpts.get("256GB"), p8ColorOpts.get("Porcelain")));

            // OnePlus 12 (mid-range feel, flagship specs)
            Product oneplus12 = createProduct(productRepo, "OnePlus 12", "oneplus-12",
                    oneplus, flagships,
                    "The OnePlus 12 features a Snapdragon 8 Gen 3 processor, Hasselblad camera system, " +
                    "100W SUPERVOOC charging, and a 2K 120Hz ProXDR display. " +
                    "Comes with 16GB RAM and up to 512GB storage.",
                    "Snapdragon 8 Gen 3, Hasselblad camera, 100W charging",
                    new BigDecimal("799.99"), new BigDecimal("899.99"),
                    "{\"display\":\"6.82-inch 2K LTPO AMOLED, 120Hz\",\"processor\":\"Snapdragon 8 Gen 3\",\"camera\":\"50MP + 48MP + 64MP\",\"battery\":\"5400 mAh\",\"os\":\"Android 14, OxygenOS 14\"}",
                    false, true);

            VariantGroup op12Storage = addVariantGroup(oneplus12, "Storage", 0, List.of("256GB", "512GB"));
            VariantGroup op12Color = addVariantGroup(oneplus12, "Color", 1, List.of("Flowy Emerald", "Silky Black"));
            productRepo.save(oneplus12);

            Map<String, VariantOption> op12StorageOpts = mapOptions(op12Storage);
            Map<String, VariantOption> op12ColorOpts = mapOptions(op12Color);
            addSku(skuRepo, oneplus12, "OP12-256-EMR", new BigDecimal("799.99"), new BigDecimal("899.99"),
                    Set.of(op12StorageOpts.get("256GB"), op12ColorOpts.get("Flowy Emerald")));
            addSku(skuRepo, oneplus12, "OP12-512-BLK", new BigDecimal("899.99"), null,
                    Set.of(op12StorageOpts.get("512GB"), op12ColorOpts.get("Silky Black")));

            // Xiaomi Redmi Note 13 Pro (mid-range)
            Product redmiNote13 = createProduct(productRepo, "Xiaomi Redmi Note 13 Pro", "xiaomi-redmi-note-13-pro",
                    xiaomi, midRange,
                    "The Redmi Note 13 Pro delivers a 200MP camera, AMOLED display, and 67W turbo charging " +
                    "at an incredible price. MediaTek Dimensity 7200 Ultra processor for smooth performance.",
                    "200MP camera, 120Hz AMOLED, 67W charging, great value",
                    new BigDecimal("299.99"), new BigDecimal("349.99"),
                    "{\"display\":\"6.67-inch AMOLED, 120Hz\",\"processor\":\"MediaTek Dimensity 7200 Ultra\",\"camera\":\"200MP + 8MP + 2MP\",\"battery\":\"5100 mAh\",\"os\":\"Android 13, MIUI 14\"}",
                    false, true);

            VariantGroup rn13Storage = addVariantGroup(redmiNote13, "Storage", 0, List.of("128GB", "256GB"));
            VariantGroup rn13Color = addVariantGroup(redmiNote13, "Color", 1,
                    List.of("Midnight Black", "Lavender Purple", "Ocean Teal"));
            productRepo.save(redmiNote13);

            Map<String, VariantOption> rn13StorageOpts = mapOptions(rn13Storage);
            Map<String, VariantOption> rn13ColorOpts = mapOptions(rn13Color);
            addSku(skuRepo, redmiNote13, "RN13P-128-BLK", new BigDecimal("299.99"), new BigDecimal("349.99"),
                    Set.of(rn13StorageOpts.get("128GB"), rn13ColorOpts.get("Midnight Black")));
            addSku(skuRepo, redmiNote13, "RN13P-256-LAV", new BigDecimal("349.99"), null,
                    Set.of(rn13StorageOpts.get("256GB"), rn13ColorOpts.get("Lavender Purple")));

            // iPad Pro (tablet)
            createProduct(productRepo, "Apple iPad Pro 12.9\" M2", "apple-ipad-pro-12-9-m2",
                    apple, tablets,
                    "The ultimate iPad experience. M2 chip delivers next-level performance. " +
                    "Liquid Retina XDR display, ProRes video recording, and works with Apple Pencil 2nd generation.",
                    "M2 chip, 12.9\" Liquid Retina XDR, Apple Pencil 2 support",
                    new BigDecimal("1099.00"), new BigDecimal("1199.00"),
                    "{\"display\":\"12.9-inch Liquid Retina XDR\",\"processor\":\"Apple M2\",\"camera\":\"12MP + 10MP\",\"battery\":\"10758 mAh\",\"os\":\"iPadOS 17\"}",
                    true, false);

            log.info("Catalog database seeded with {} categories, {} brands, {} products",
                    categoryRepo.count(), brandRepo.count(), productRepo.count());
        };
    }

    private Category createCategory(CategoryRepository repo, String name, String slug,
                                     String description, Category parent) {
        Category cat = new Category();
        cat.setName(name);
        cat.setSlug(slug);
        cat.setDescription(description);
        cat.setParent(parent);
        return repo.save(cat);
    }

    private Brand createBrand(BrandRepository repo, String name, String slug, String description) {
        Brand brand = new Brand();
        brand.setName(name);
        brand.setSlug(slug);
        brand.setDescription(description);
        return repo.save(brand);
    }

    private Product createProduct(ProductRepository repo, String name, String slug,
                                   Brand brand, Category category, String description,
                                   String shortDescription, BigDecimal basePrice,
                                   BigDecimal compareAtPrice, String specifications,
                                   boolean featured, boolean trending) {
        Product p = new Product();
        p.setName(name);
        p.setSlug(slug);
        p.setBrand(brand);
        p.setCategory(category);
        p.setDescription(description);
        p.setShortDescription(shortDescription);
        p.setBasePrice(basePrice);
        p.setCompareAtPrice(compareAtPrice);
        p.setSpecifications(specifications);
        p.setFeatured(featured);
        p.setTrending(trending);
        return repo.save(p);
    }

    private VariantGroup addVariantGroup(Product product, String name, int sortOrder, List<String> optionValues) {
        VariantGroup group = new VariantGroup();
        group.setProduct(product);
        group.setName(name);
        group.setSortOrder(sortOrder);

        int optSort = 0;
        for (String val : optionValues) {
            VariantOption opt = new VariantOption();
            opt.setVariantGroup(group);
            opt.setValue(val);
            opt.setSortOrder(optSort++);
            group.getOptions().add(opt);
        }

        product.getVariantGroups().add(group);
        return group;
    }

    private Map<String, VariantOption> mapOptions(VariantGroup group) {
        Map<String, VariantOption> map = new HashMap<>();
        for (VariantOption opt : group.getOptions()) {
            map.put(opt.getValue(), opt);
        }
        return map;
    }

    private void addSku(SkuRepository repo, Product product, String skuCode,
                        BigDecimal price, BigDecimal compareAtPrice, Set<VariantOption> options) {
        Sku sku = new Sku();
        sku.setProduct(product);
        sku.setSkuCode(skuCode);
        sku.setPrice(price);
        sku.setCompareAtPrice(compareAtPrice);
        sku.setOptionValues(options);
        repo.save(sku);
    }
}
