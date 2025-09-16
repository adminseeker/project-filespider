CREATE TABLE t_users (
    user_id BINARY(16) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255),
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    phone VARCHAR(10) NOT NULL,
    country VARCHAR(100),
    created_date DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    modified_date DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (user_id)
);
