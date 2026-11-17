-- =============================================================
--  DATABASE SETUP - shopnow_ecommerce
--  Creates the schema and safely seeds/reseeds the sample catalog
-- =============================================================

CREATE DATABASE IF NOT EXISTS shopnow_ecommerce
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE shopnow_ecommerce;

-- -------------------------------------------------------------
-- 1. users
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    id              INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    first_name      VARCHAR(100)        NOT NULL,
    last_name       VARCHAR(100)        NOT NULL,
    email           VARCHAR(255)        NOT NULL UNIQUE,
    password_hash   VARCHAR(255)        NOT NULL,
    phone           VARCHAR(20),
    address_line1   VARCHAR(255),
    address_line2   VARCHAR(255),
    city            VARCHAR(100),
    state           VARCHAR(100),
    pincode         VARCHAR(20),
    country         VARCHAR(100)        DEFAULT 'India',
    created_at      DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- -------------------------------------------------------------
-- 2. password_reset_tokens
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS password_reset_tokens (
    id          INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id     INT UNSIGNED        NOT NULL,
    token       VARCHAR(255)        NOT NULL UNIQUE,
    expiry_time DATETIME            NOT NULL,
    used        TINYINT(1)          NOT NULL DEFAULT 0,
    created_at  DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_prt_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- -------------------------------------------------------------
-- 3. categories
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS categories (
    id          INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(150)        NOT NULL UNIQUE,
    description TEXT,
    image_url   VARCHAR(500),
    is_active   TINYINT(1)          NOT NULL DEFAULT 1,
    created_at  DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- -------------------------------------------------------------
-- 4. products
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS products (
    id               INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    category_id      INT UNSIGNED        NOT NULL,
    name             VARCHAR(255)        NOT NULL,
    description      TEXT,
    brand            VARCHAR(150),
    price            DECIMAL(10,2)       NOT NULL,
    discount_percent DECIMAL(5,2)        NOT NULL DEFAULT 0.00,
    color            VARCHAR(100),
    is_size_available TINYINT(1)         NOT NULL DEFAULT 0,
    stock_quantity   INT                 NOT NULL DEFAULT 0,
    main_image_url   VARCHAR(500),
    is_active        TINYINT(1)          NOT NULL DEFAULT 1,
    created_at       DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_prod_cat FOREIGN KEY (category_id) REFERENCES categories (id) ON DELETE RESTRICT
) ENGINE=InnoDB;

-- -------------------------------------------------------------
-- 5. product_sizes
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS product_sizes (
    id             INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    product_id     INT UNSIGNED    NOT NULL,
    size_label     VARCHAR(20)     NOT NULL,
    stock_quantity INT             NOT NULL DEFAULT 0,
    CONSTRAINT fk_ps_product FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- -------------------------------------------------------------
-- 6. product_images
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS product_images (
    id            INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    product_id    INT UNSIGNED    NOT NULL,
    image_url     VARCHAR(500)    NOT NULL,
    display_order INT             NOT NULL DEFAULT 0,
    CONSTRAINT fk_pi_product FOREIGN KEY (product_id) REFERENCES products (id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- -------------------------------------------------------------
-- 7. carts
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS carts (
    id         INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id    INT UNSIGNED    NOT NULL UNIQUE,
    created_at DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_cart_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- -------------------------------------------------------------
-- 8. cart_items
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS cart_items (
    id         INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    cart_id    INT UNSIGNED    NOT NULL,
    product_id INT UNSIGNED    NOT NULL,
    size_id    INT UNSIGNED,
    quantity   INT             NOT NULL DEFAULT 1,
    added_at   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ci_cart    FOREIGN KEY (cart_id)    REFERENCES carts         (id) ON DELETE CASCADE,
    CONSTRAINT fk_ci_product FOREIGN KEY (product_id) REFERENCES products      (id) ON DELETE CASCADE,
    CONSTRAINT fk_ci_size    FOREIGN KEY (size_id)    REFERENCES product_sizes (id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- -------------------------------------------------------------
-- 9. coupons
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS coupons (
    id              INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    code            VARCHAR(50)                     NOT NULL UNIQUE,
    discount_type   ENUM('PERCENT','FLAT')          NOT NULL,
    discount_value  DECIMAL(10,2)                   NOT NULL,
    min_order_value DECIMAL(10,2)                   NOT NULL DEFAULT 0.00,
    max_uses        INT                             NOT NULL DEFAULT 1,
    used_count      INT                             NOT NULL DEFAULT 0,
    valid_from      DATETIME                        NOT NULL,
    valid_until     DATETIME                        NOT NULL,
    is_active       TINYINT(1)                      NOT NULL DEFAULT 1
) ENGINE=InnoDB;

-- -------------------------------------------------------------
-- 10. orders
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS orders (
    id                   INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id              INT UNSIGNED        NOT NULL,
    coupon_id            INT UNSIGNED,
    subtotal             DECIMAL(10,2)       NOT NULL,
    discount_amount      DECIMAL(10,2)       NOT NULL DEFAULT 0.00,
    shipping_charge      DECIMAL(10,2)       NOT NULL DEFAULT 0.00,
    total_amount         DECIMAL(10,2)       NOT NULL,
    shipping_name        VARCHAR(200)        NOT NULL,
    shipping_phone       VARCHAR(20)         NOT NULL,
    shipping_address_line1 VARCHAR(255)      NOT NULL,
    shipping_address_line2 VARCHAR(255),
    shipping_city        VARCHAR(100)        NOT NULL,
    shipping_state       VARCHAR(100)        NOT NULL,
    shipping_pincode     VARCHAR(20)         NOT NULL,
    shipping_country     VARCHAR(100)        NOT NULL DEFAULT 'India',
    status               ENUM('PENDING','CONFIRMED','SHIPPED','DELIVERED','CANCELLED')
                                             NOT NULL DEFAULT 'PENDING',
    payment_status       ENUM('PENDING','PAID','FAILED','REFUNDED')
                                             NOT NULL DEFAULT 'PENDING',
    created_at           DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_ord_user   FOREIGN KEY (user_id)   REFERENCES users    (id) ON DELETE RESTRICT,
    CONSTRAINT fk_ord_coupon FOREIGN KEY (coupon_id) REFERENCES coupons  (id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- -------------------------------------------------------------
-- 11. order_items
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS order_items (
    id            INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    order_id      INT UNSIGNED        NOT NULL,
    product_id    INT UNSIGNED        NOT NULL,
    size_id       INT UNSIGNED,
    product_name  VARCHAR(255)        NOT NULL,
    product_color VARCHAR(100),
    size_label    VARCHAR(20),
    unit_price    DECIMAL(10,2)       NOT NULL,
    quantity      INT                 NOT NULL,
    total_price   DECIMAL(10,2)       NOT NULL,
    CONSTRAINT fk_oi_order   FOREIGN KEY (order_id)   REFERENCES orders        (id) ON DELETE CASCADE,
    CONSTRAINT fk_oi_product FOREIGN KEY (product_id) REFERENCES products      (id) ON DELETE RESTRICT,
    CONSTRAINT fk_oi_size    FOREIGN KEY (size_id)    REFERENCES product_sizes (id) ON DELETE SET NULL
) ENGINE=InnoDB;

-- -------------------------------------------------------------
-- 12. payments
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS payments (
    id             INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    order_id       INT UNSIGNED                                              NOT NULL UNIQUE,
    payment_method ENUM('COD','CARD','UPI','NET_BANKING')                   NOT NULL,
    transaction_id VARCHAR(255),
    amount         DECIMAL(10,2)                                            NOT NULL,
    status         ENUM('PENDING','SUCCESS','FAILED','REFUNDED')            NOT NULL DEFAULT 'PENDING',
    paid_at        DATETIME,
    created_at     DATETIME                                                 NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_pay_order FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- -------------------------------------------------------------
-- 13. order_status_history
-- -------------------------------------------------------------
CREATE TABLE IF NOT EXISTS order_status_history (
    id         INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    order_id   INT UNSIGNED        NOT NULL,
    status     VARCHAR(50)         NOT NULL,
    remarks    TEXT,
    changed_at DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_osh_order FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE
) ENGINE=InnoDB;


-- =============================================================
--  SAMPLE CATALOG DATA
--  Catalog inserts are keyed by category/product names so this
--  section can safely be run again without assuming auto IDs.
-- =============================================================

INSERT INTO categories (name, description, image_url, is_active)
SELECT 'Electronics', 'Latest gadgets, mobiles, laptops and accessories',
       'https://images.unsplash.com/photo-1498049794561-7780e7231661?auto=format&fit=crop&w=900&q=80', 1
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Electronics');
INSERT INTO categories (name, description, image_url, is_active)
SELECT 'Clothing', 'Men, women and kids fashion wear',
       'https://images.unsplash.com/photo-1483985988355-763728e1935b?auto=format&fit=crop&w=900&q=80', 1
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Clothing');
INSERT INTO categories (name, description, image_url, is_active)
SELECT 'Footwear', 'Shoes, sandals, sneakers and sports footwear',
       'https://images.unsplash.com/photo-1542291026-7eec264c27ff?auto=format&fit=crop&w=900&q=80', 1
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Footwear');
INSERT INTO categories (name, description, image_url, is_active)
SELECT 'Books', 'Bestsellers, textbooks and books for curious minds',
       'https://images.unsplash.com/photo-1507842217343-583bb7270b66?auto=format&fit=crop&w=900&q=80', 1
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Books');
INSERT INTO categories (name, description, image_url, is_active)
SELECT 'Home & Kitchen', 'Useful appliances and kitchen essentials',
       'https://images.unsplash.com/photo-1556911220-bff31c812dba?auto=format&fit=crop&w=900&q=80', 1
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Home & Kitchen');
INSERT INTO categories (name, description, image_url, is_active)
SELECT 'Sports', 'Fitness equipment and outdoor essentials',
       'https://images.unsplash.com/photo-1518611012118-696072aa579a?auto=format&fit=crop&w=900&q=80', 1
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Sports');

UPDATE categories
SET image_url = CASE name
    WHEN 'Electronics' THEN 'https://images.unsplash.com/photo-1498049794561-7780e7231661?auto=format&fit=crop&w=900&q=80'
    WHEN 'Clothing' THEN 'https://images.unsplash.com/photo-1483985988355-763728e1935b?auto=format&fit=crop&w=900&q=80'
    WHEN 'Footwear' THEN 'https://images.unsplash.com/photo-1542291026-7eec264c27ff?auto=format&fit=crop&w=900&q=80'
    WHEN 'Books' THEN 'https://images.unsplash.com/photo-1507842217343-583bb7270b66?auto=format&fit=crop&w=900&q=80'
    WHEN 'Home & Kitchen' THEN 'https://images.unsplash.com/photo-1556911220-bff31c812dba?auto=format&fit=crop&w=900&q=80'
    WHEN 'Sports' THEN 'https://images.unsplash.com/photo-1518611012118-696072aa579a?auto=format&fit=crop&w=900&q=80'
END
WHERE name IN ('Electronics', 'Clothing', 'Footwear', 'Books', 'Home & Kitchen', 'Sports')
  AND (image_url IS NULL OR image_url LIKE '/images/%');

-- Twelve example products with descriptions, brands, prices, stock and online images.
INSERT INTO products
  (category_id, name, description, brand, price, discount_percent, color, is_size_available, stock_quantity, main_image_url, is_active)
SELECT c.id, 'Wireless Bluetooth Headphones',
       'Over-ear wireless headphones with rich sound and up to 30 hours of battery life.',
       'SoundMax', 2999.00, 10.00, 'Black', 0, 50,
       'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?auto=format&fit=crop&w=900&q=80', 1
FROM categories c WHERE c.name = 'Electronics'
AND NOT EXISTS (SELECT 1 FROM products p WHERE p.category_id = c.id AND p.name = 'Wireless Bluetooth Headphones');
INSERT INTO products
  (category_id, name, description, brand, price, discount_percent, color, is_size_available, stock_quantity, main_image_url, is_active)
SELECT c.id, 'Smartwatch Pro X',
       'AMOLED smartwatch with everyday activity tracking, heart-rate monitoring and GPS.',
       'TechWear', 7499.00, 15.00, 'Silver', 0, 30,
       'https://images.unsplash.com/photo-1523275335684-37898b6baf30?auto=format&fit=crop&w=900&q=80', 1
FROM categories c WHERE c.name = 'Electronics'
AND NOT EXISTS (SELECT 1 FROM products p WHERE p.category_id = c.id AND p.name = 'Smartwatch Pro X');
INSERT INTO products
  (category_id, name, description, brand, price, discount_percent, color, is_size_available, stock_quantity, main_image_url, is_active)
SELECT c.id, 'Men''s Slim Fit Chinos',
       'Stretch cotton slim-fit chinos designed for comfortable casual and semi-formal wear.',
       'UrbanStyle', 999.00, 20.00, 'Khaki', 1, 100,
       'https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?auto=format&fit=crop&w=900&q=80', 1
FROM categories c WHERE c.name = 'Clothing'
AND NOT EXISTS (SELECT 1 FROM products p WHERE p.category_id = c.id AND p.name = 'Men''s Slim Fit Chinos');
INSERT INTO products
  (category_id, name, description, brand, price, discount_percent, color, is_size_available, stock_quantity, main_image_url, is_active)
SELECT c.id, 'Women''s Floral Kurti',
       'Lightweight printed rayon kurti with a comfortable everyday fit.',
       'EthnicWave', 749.00, 25.00, 'Multicolor', 1, 80,
       'https://images.unsplash.com/photo-1585487000160-6ebcfceb0d03?auto=format&fit=crop&w=900&q=80', 1
FROM categories c WHERE c.name = 'Clothing'
AND NOT EXISTS (SELECT 1 FROM products p WHERE p.category_id = c.id AND p.name = 'Women''s Floral Kurti');
INSERT INTO products
  (category_id, name, description, brand, price, discount_percent, color, is_size_available, stock_quantity, main_image_url, is_active)
SELECT c.id, 'Running Sneakers',
       'Lightweight mesh running shoes with cushioned soles for daily training and walks.',
       'StrideFast', 1899.00, 10.00, 'White/Blue', 1, 60,
       'https://images.unsplash.com/photo-1542291026-7eec264c27ff?auto=format&fit=crop&w=900&q=80', 1
FROM categories c WHERE c.name = 'Footwear'
AND NOT EXISTS (SELECT 1 FROM products p WHERE p.category_id = c.id AND p.name = 'Running Sneakers');
INSERT INTO products
  (category_id, name, description, brand, price, discount_percent, color, is_size_available, stock_quantity, main_image_url, is_active)
SELECT c.id, 'Leather Formal Shoes',
       'Classic lace-up formal shoes with a cushioned insole for work and special occasions.',
       'ClassicStep', 3499.00, 5.00, 'Brown', 1, 40,
       'https://images.unsplash.com/photo-1614252235316-8c857d38b5f4?auto=format&fit=crop&w=900&q=80', 1
FROM categories c WHERE c.name = 'Footwear'
AND NOT EXISTS (SELECT 1 FROM products p WHERE p.category_id = c.id AND p.name = 'Leather Formal Shoes');
INSERT INTO products
  (category_id, name, description, brand, price, discount_percent, color, is_size_available, stock_quantity, main_image_url, is_active)
SELECT c.id, 'Clean Code by Robert C. Martin',
       'A practical guide to writing clear, maintainable software and improving code quality.',
       'Prentice Hall', 599.00, 0.00, 'N/A', 0, 200,
       'https://images.unsplash.com/photo-1544947950-fa07a98d237f?auto=format&fit=crop&w=900&q=80', 1
FROM categories c WHERE c.name = 'Books'
AND NOT EXISTS (SELECT 1 FROM products p WHERE p.category_id = c.id AND p.name = 'Clean Code by Robert C. Martin');
INSERT INTO products
  (category_id, name, description, brand, price, discount_percent, color, is_size_available, stock_quantity, main_image_url, is_active)
SELECT c.id, 'Atomic Habits by James Clear',
       'A practical guide to building good habits, breaking unhelpful routines and making progress.',
       'Avery', 449.00, 5.00, 'N/A', 0, 150,
       'https://images.unsplash.com/photo-1512820790803-83ca734da794?auto=format&fit=crop&w=900&q=80', 1
FROM categories c WHERE c.name = 'Books'
AND NOT EXISTS (SELECT 1 FROM products p WHERE p.category_id = c.id AND p.name = 'Atomic Habits by James Clear');
INSERT INTO products
  (category_id, name, description, brand, price, discount_percent, color, is_size_available, stock_quantity, main_image_url, is_active)
SELECT c.id, 'Stainless Steel Pressure Cooker 5L',
       'Five-litre stainless steel pressure cooker with a secure lid and safety valve.',
       'Prestige', 1299.00, 12.00, 'Silver', 0, 70,
       'https://images.unsplash.com/photo-1556911220-e15b29be8c8f?auto=format&fit=crop&w=900&q=80', 1
FROM categories c WHERE c.name = 'Home & Kitchen'
AND NOT EXISTS (SELECT 1 FROM products p WHERE p.category_id = c.id AND p.name = 'Stainless Steel Pressure Cooker 5L');
INSERT INTO products
  (category_id, name, description, brand, price, discount_percent, color, is_size_available, stock_quantity, main_image_url, is_active)
SELECT c.id, 'Non-Stick Cookware Set (3-piece)',
       'Three-piece non-stick cookware set for everyday frying, sauteing and cooking.',
       'Hawkins', 2199.00, 18.00, 'Black', 0, 45,
       'https://images.unsplash.com/photo-1556911220-bff31c812dba?auto=format&fit=crop&w=900&q=80', 1
FROM categories c WHERE c.name = 'Home & Kitchen'
AND NOT EXISTS (SELECT 1 FROM products p WHERE p.category_id = c.id AND p.name = 'Non-Stick Cookware Set (3-piece)');
INSERT INTO products
  (category_id, name, description, brand, price, discount_percent, color, is_size_available, stock_quantity, main_image_url, is_active)
SELECT c.id, 'Yoga Mat (6mm)',
       'Textured, anti-slip exercise mat with carrying strap for yoga and floor workouts.',
       'FitZone', 849.00, 8.00, 'Purple', 0, 90,
       'https://images.unsplash.com/photo-1518611012118-696072aa579a?auto=format&fit=crop&w=900&q=80', 1
FROM categories c WHERE c.name = 'Sports'
AND NOT EXISTS (SELECT 1 FROM products p WHERE p.category_id = c.id AND p.name = 'Yoga Mat (6mm)');
INSERT INTO products
  (category_id, name, description, brand, price, discount_percent, color, is_size_available, stock_quantity, main_image_url, is_active)
SELECT c.id, 'Adjustable Dumbbell Set 10kg',
       'Adjustable dumbbell pair with textured grips for strength training at home.',
       'PowerFit', 1799.00, 10.00, 'Chrome', 0, 35,
       'https://images.unsplash.com/photo-1583454110551-21f2fa2afe61?auto=format&fit=crop&w=900&q=80', 1
FROM categories c WHERE c.name = 'Sports'
AND NOT EXISTS (SELECT 1 FROM products p WHERE p.category_id = c.id AND p.name = 'Adjustable Dumbbell Set 10kg');

-- Size-level stock for apparel and footwear. Product IDs are looked up by name.
INSERT INTO product_sizes (product_id, size_label, stock_quantity)
SELECT p.id, sizes.size_label, sizes.stock_quantity
FROM products p
JOIN categories c ON c.id = p.category_id
JOIN (
  SELECT 'Men''s Slim Fit Chinos' AS product_name, 'S' AS size_label, 20 AS stock_quantity
  UNION ALL SELECT 'Men''s Slim Fit Chinos', 'M', 30
  UNION ALL SELECT 'Men''s Slim Fit Chinos', 'L', 30
  UNION ALL SELECT 'Men''s Slim Fit Chinos', 'XL', 20
  UNION ALL SELECT 'Women''s Floral Kurti', 'S', 20
  UNION ALL SELECT 'Women''s Floral Kurti', 'M', 25
  UNION ALL SELECT 'Women''s Floral Kurti', 'L', 20
  UNION ALL SELECT 'Women''s Floral Kurti', 'XL', 15
  UNION ALL SELECT 'Running Sneakers', '6', 10
  UNION ALL SELECT 'Running Sneakers', '7', 15
  UNION ALL SELECT 'Running Sneakers', '8', 15
  UNION ALL SELECT 'Running Sneakers', '9', 12
  UNION ALL SELECT 'Running Sneakers', '10', 8
  UNION ALL SELECT 'Leather Formal Shoes', '6', 8
  UNION ALL SELECT 'Leather Formal Shoes', '7', 10
  UNION ALL SELECT 'Leather Formal Shoes', '8', 12
  UNION ALL SELECT 'Leather Formal Shoes', '9', 7
  UNION ALL SELECT 'Leather Formal Shoes', '10', 3
) sizes ON sizes.product_name = p.name
WHERE c.name IN ('Clothing', 'Footwear')
AND NOT EXISTS (
  SELECT 1 FROM product_sizes existing
  WHERE existing.product_id = p.id AND existing.size_label = sizes.size_label
);

-- Two gallery images per product, hosted by Unsplash. Main images remain
-- available as a fallback if a remote gallery image cannot be loaded.
INSERT INTO product_images (product_id, image_url, display_order)
SELECT p.id, p.main_image_url, 1 FROM products p
WHERE p.is_active = 1
AND NOT EXISTS (
  SELECT 1 FROM product_images i WHERE i.product_id = p.id AND i.display_order = 1
);
INSERT INTO product_images (product_id, image_url, display_order)
SELECT p.id, gallery.image_url, 2
FROM products p
JOIN (
  SELECT 'Wireless Bluetooth Headphones' AS product_name,
         'https://images.unsplash.com/photo-1484704849700-f032a568e944?auto=format&fit=crop&w=900&q=80' AS image_url
  UNION ALL SELECT 'Smartwatch Pro X', 'https://images.unsplash.com/photo-1434493789847-2f02dc6ca35d?auto=format&fit=crop&w=900&q=80'
  UNION ALL SELECT 'Men''s Slim Fit Chinos', 'https://images.unsplash.com/photo-1473966968600-fa801b869a1a?auto=format&fit=crop&w=900&q=80'
  UNION ALL SELECT 'Women''s Floral Kurti', 'https://images.unsplash.com/photo-1490481651871-ab68de25d43d?auto=format&fit=crop&w=900&q=80'
  UNION ALL SELECT 'Running Sneakers', 'https://images.unsplash.com/photo-1460353581641-37baddab0fa2?auto=format&fit=crop&w=900&q=80'
  UNION ALL SELECT 'Leather Formal Shoes', 'https://images.unsplash.com/photo-1460353581641-37baddab0fa2?auto=format&fit=crop&w=900&q=80'
  UNION ALL SELECT 'Clean Code by Robert C. Martin', 'https://images.unsplash.com/photo-1519682337058-a94d519337bc?auto=format&fit=crop&w=900&q=80'
  UNION ALL SELECT 'Atomic Habits by James Clear', 'https://images.unsplash.com/photo-1516979187457-637abb4f9353?auto=format&fit=crop&w=900&q=80'
  UNION ALL SELECT 'Stainless Steel Pressure Cooker 5L', 'https://images.unsplash.com/photo-1556911220-bff31c812dba?auto=format&fit=crop&w=900&q=80'
  UNION ALL SELECT 'Non-Stick Cookware Set (3-piece)', 'https://images.unsplash.com/photo-1556911220-bff31c812dba?auto=format&fit=crop&w=900&q=80'
  UNION ALL SELECT 'Yoga Mat (6mm)', 'https://images.unsplash.com/photo-1599447421416-3414500d18a5?auto=format&fit=crop&w=900&q=80'
  UNION ALL SELECT 'Adjustable Dumbbell Set 10kg', 'https://images.unsplash.com/photo-1583454110551-21f2fa2afe61?auto=format&fit=crop&w=900&q=80'
) gallery ON gallery.product_name = p.name
WHERE NOT EXISTS (
  SELECT 1 FROM product_images i WHERE i.product_id = p.id AND i.display_order = 2
);

-- Demo coupons are also safe to seed more than once.
INSERT INTO coupons
  (code, discount_type, discount_value, min_order_value, max_uses, used_count, valid_from, valid_until, is_active)
SELECT 'SAVE10', 'PERCENT', 10.00, 500.00, 500, 0, '2024-01-01 00:00:00', '2099-12-31 23:59:59', 1
WHERE NOT EXISTS (SELECT 1 FROM coupons WHERE code = 'SAVE10');
INSERT INTO coupons
  (code, discount_type, discount_value, min_order_value, max_uses, used_count, valid_from, valid_until, is_active)
SELECT 'FLAT50', 'FLAT', 50.00, 300.00, 1000, 0, '2024-01-01 00:00:00', '2099-12-31 23:59:59', 1
WHERE NOT EXISTS (SELECT 1 FROM coupons WHERE code = 'FLAT50');
