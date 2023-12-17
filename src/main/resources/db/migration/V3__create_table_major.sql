CREATE TABLE IF NOT EXISTS `major` (
    id BIGINT NOT NULL AUTO_INCREMENT,
    code VARCHAR(20) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    description VARCHAR(2000) NOT NULL,
    duration INT NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_code (code)
);

ALTER TABLE `student`
ADD COLUMN major_id BIGINT NOT NULL;

ALTER TABLE `student`
ADD CONSTRAINT fk_major FOREIGN KEY (major_id) REFERENCES `major`(id);