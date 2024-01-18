CREATE TABLE IF NOT EXISTS `post` (
    id BIGINT NOT NULL AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL,
    description VARCHAR(2000) NOT NULL,
    created_at DATETIME NOT NULL,
    PRIMARY KEY (id)
);