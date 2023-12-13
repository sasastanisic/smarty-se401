CREATE TABLE IF NOT EXISTS `account` (
    id BIGINT NOT NULL AUTO_INCREMENT,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(50) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_email (email)
);

ALTER TABLE `student`
ADD COLUMN account_id BIGINT NOT NULL;

ALTER TABLE `student`
ADD CONSTRAINT fk_account FOREIGN KEY (account_id) REFERENCES `account`(id);