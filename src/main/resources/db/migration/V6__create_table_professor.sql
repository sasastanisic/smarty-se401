CREATE TABLE IF NOT EXISTS `professor` (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    surname VARCHAR(100) NOT NULL,
    years_of_experience INT NOT NULL,
    account_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_account2 FOREIGN KEY (account_id) REFERENCES `account`(id)
);