CREATE TABLE IF NOT EXISTS user_info (
    id VARCHAR(255) PRIMARY KEY,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    full_name VARCHAR(255),
    user_name VARCHAR(255),
    paassword VARCHAR(255),
    email_id VARCHAR(255),
    mobile_num VARCHAR(255),
    house_num VARCHAR(255),
    landmark VARCHAR(255),
    city VARCHAR(255),
    pincode VARCHAR(255),
    user_role VARCHAR(255),
    created_at DATE,
    updated_at DATE
);

CREATE TABLE IF NOT EXISTS product_info (
    product_id VARCHAR(255) PRIMARY KEY,
    product_name VARCHAR(255),
    category_id VARCHAR(255),
    product_image_url VARCHAR(255),
    product_details VARCHAR(255),
    product_actual_price DOUBLE,
    offer_percentage INT,
    usage_reward_points INT
);

CREATE TABLE IF NOT EXISTS wallet_info (
    id VARCHAR(255) PRIMARY KEY,
    user_id VARCHAR(255),
    avl_bal DOUBLE,
    last_transaction_details DOUBLE,
    usage_reward_points INT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS kart_info (
    kart_id VARCHAR(255) PRIMARY KEY,
    user_id VARCHAR(255),
    products VARCHAR(255),
    product_count INT DEFAULT 0,
    total_price DOUBLE,
    discount_price DOUBLE,
    pay DOUBLE,
    get_reward_points INT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS order_info (
    order_id VARCHAR(255) PRIMARY KEY,
    user_id VARCHAR(255),
    products VARCHAR(255),
    product_count INT DEFAULT 0,
    total_price DOUBLE,
    discount_price DOUBLE,
    pay DOUBLE,
    order_date DATE,
    user_fullname VARCHAR(255),
    user_address VARCHAR(255),
    user_mobileNum VARCHAR(255),
    payment_status VARCHAR(255),
    paid_amount DOUBLE,
    payment_id VARCHAR(255),
    transaction_id VARCHAR(255),
    order_status VARCHAR(255),
    delivery_date VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS payment_info (
    payment_id VARCHAR(255) PRIMARY KEY,
    transaction_id VARCHAR(255),
    order_id VARCHAR(255),
    user_id VARCHAR(255),
    user_fullname VARCHAR(255),
    payment_status VARCHAR(255),
    total_price DOUBLE,
    paid_amount DOUBLE,
    discount_price DOUBLE,
    products VARCHAR(255),
    total_products INT,
    payment_mode VARCHAR(255),
    payment_date TIMESTAMP
);
