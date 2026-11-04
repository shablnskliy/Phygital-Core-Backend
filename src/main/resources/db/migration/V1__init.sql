CREATE TABLE users (
                       id UUID PRIMARY KEY,
                       first_name VARCHAR(255) NOT NULL,
                       last_name VARCHAR(255) NOT NULL,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       phone VARCHAR(50),
                       telegram VARCHAR(100),
                       birth_date DATE,
                       bio TEXT,
                       avatar TEXT,
                       role VARCHAR(50) NOT NULL,
                       created_at TIMESTAMP NOT NULL
);

