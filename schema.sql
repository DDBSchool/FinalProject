-- USERS TABLE: Integer ID (primary key), unique username, password hash, and creation timestamp
CREATE TABLE users (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- TYPING RESULTS TABLE: Use user_id to reference users, with constraints and indexes
CREATE TABLE typing_results (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    user_id INT UNSIGNED NOT NULL,
    level INT NOT NULL,
    score INT NOT NULL CHECK (score >= 0),
    accuracy DECIMAL(5,2) NOT NULL CHECK (accuracy >= 0 AND accuracy <= 100),
    words_per_second DECIMAL(6,3) NOT NULL CHECK (words_per_second >= 0),
    type ENUM('ranked', 'unranked') NOT NULL,
    test_time DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX(user_id),
    INDEX(type),
    INDEX(test_time)
);
