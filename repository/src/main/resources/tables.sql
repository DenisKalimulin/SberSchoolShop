CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       login VARCHAR(255) NOT NULL UNIQUE,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL
);

CREATE TABLE roles (
                       id SERIAL PRIMARY KEY,
                       role_name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE users_roles (
                             user_id BIGINT NOT NULL,
                             role_id BIGINT NOT NULL,
                             PRIMARY KEY (user_id, role_id),
                             FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                             FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

CREATE TABLE addresses (
                           id SERIAL PRIMARY KEY,
                           country VARCHAR(255) NOT NULL,
                           region VARCHAR(255) NOT NULL,
                           city VARCHAR(255) NOT NULL,
                           street VARCHAR(255) NOT NULL,
                           zip_code INTEGER,
                           user_id BIGINT,
                           FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE wallets (
                         id SERIAL PRIMARY KEY,
                         balance DECIMAL(15, 2) NOT NULL DEFAULT 0,
                         pin VARCHAR(255) NOT NULL,
                         wallet_number VARCHAR(12) NOT NULL UNIQUE,
                         user_id BIGINT NOT NULL UNIQUE,
                         FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE carts (
                       id SERIAL PRIMARY KEY,
                       user_id BIGINT NOT NULL UNIQUE,
                       total_price DECIMAL(15,2) NOT NULL DEFAULT 0,
                       FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE categories (
                            id SERIAL PRIMARY KEY,
                            category_name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE products (
                          id SERIAL PRIMARY KEY,
                          title VARCHAR(255) NOT NULL,
                          description TEXT NOT NULL,
                          stocks INT NOT NULL CHECK (stocks >= 0),
                          price DECIMAL(10,2) NOT NULL CHECK (price > 0),
                          status VARCHAR(50) NOT NULL DEFAULT 'AVAILABLE',
                          category_id BIGINT NOT NULL,
                          user_id BIGINT NOT NULL,
                          created_at TIMESTAMP DEFAULT now(),
                          updated_at TIMESTAMP DEFAULT now(),
                          FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE,
                          FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE images (
                        id SERIAL PRIMARY KEY,
                        image_url VARCHAR(255) NOT NULL,
                        product_id BIGINT NOT NULL,
                        FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

CREATE TABLE cart_items (
                            id SERIAL PRIMARY KEY,
                            cart_id BIGINT NOT NULL,
                            product_id BIGINT NOT NULL,
                            quantity INT NOT NULL CHECK (quantity > 0),
                            FOREIGN KEY (cart_id) REFERENCES carts(id) ON DELETE CASCADE,
                            FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

CREATE TABLE favorites (
                           id SERIAL PRIMARY KEY,
                           user_id BIGINT NOT NULL,
                           FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE favorite_product (
                                  favorite_id BIGINT NOT NULL,
                                  product_id BIGINT NOT NULL,
                                  PRIMARY KEY (favorite_id, product_id),
                                  FOREIGN KEY (favorite_id) REFERENCES favorites(id) ON DELETE CASCADE,
                                  FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

CREATE TABLE reviews (
                         id SERIAL PRIMARY KEY,
                         seller_id BIGINT NOT NULL,
                         buyer_id BIGINT NOT NULL,
                         rating INT NOT NULL CHECK (rating BETWEEN 1 AND 5),
                         comment TEXT,
                         created_at TIMESTAMP DEFAULT now(),
                         FOREIGN KEY (seller_id) REFERENCES users(id) ON DELETE CASCADE,
                         FOREIGN KEY (buyer_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE orders (
                        id SERIAL PRIMARY KEY,
                        user_id BIGINT NOT NULL,
                        total_price DECIMAL(15, 2) NOT NULL,
                        status VARCHAR(50) NOT NULL,  -- Добавьте enum или статусы по необходимости
                        created_at TIMESTAMP DEFAULT now(),
                        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
CREATE TABLE order_items (
                             id SERIAL PRIMARY KEY,
                             order_id BIGINT NOT NULL,
                             product_id BIGINT NOT NULL,
                             quantity INT NOT NULL CHECK (quantity > 0),
                             price DECIMAL(10, 2) NOT NULL,  -- Цена товара в заказе
                             FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
                             FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);



-- Индексы для ускорения поиска пользователей по login и email
CREATE INDEX idx_users_login ON users(login);
CREATE INDEX idx_users_email ON users(email);

-- Индекс для ускорения получения кошелька пользователя
CREATE INDEX idx_wallets_user_id ON wallets(user_id);

-- Индекс для ускорения получения корзины пользователя
CREATE INDEX idx_carts_user_id ON carts(user_id);

-- Индексы для ускорения фильтрации товаров по категории, продавцу, статусу, цене
CREATE INDEX idx_products_category_id ON products(category_id);
CREATE INDEX idx_products_user_id ON products(user_id);
CREATE INDEX idx_products_status ON products(status);
CREATE INDEX idx_products_price ON products(price);

-- Индексы для ускорения поиска избранных товаров
CREATE INDEX idx_favorites_user_id ON favorites(user_id);
CREATE INDEX idx_favorite_product_favorite_id ON favorite_product(favorite_id);
CREATE INDEX idx_favorite_product_product_id ON favorite_product(product_id);
