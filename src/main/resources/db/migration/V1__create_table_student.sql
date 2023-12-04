CREATE TABLE IF NOT EXISTS `student` (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    surname VARCHAR(100) NOT NULL,
    `index` INT NOT NULL,
    year INT NOT NULL,
    semester INT NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_index (`index`)
);