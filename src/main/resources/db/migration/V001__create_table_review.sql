create table review(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT,
    user_id BIGINT,
    text VARCHAR(255),
    rating INT
);