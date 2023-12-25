CREATE TABLE IF NOT EXISTS `course` (
    id BIGINT NOT NULL AUTO_INCREMENT,
    code VARCHAR(20) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    points DOUBLE NOT NULL,
    year INT NOT NULL,
    semester INT NOT NULL,
    description VARCHAR(2000) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_code (code)
);