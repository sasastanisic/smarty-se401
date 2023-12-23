CREATE TABLE IF NOT EXISTS `study_status` (
    id BIGINT NOT NULL AUTO_INCREMENT,
    type VARCHAR(50) NOT NULL,
    PRIMARY KEY (id)
);

ALTER TABLE `student`
ADD COLUMN study_status_id BIGINT NOT NULL;

ALTER TABLE `student`
ADD CONSTRAINT fk_study_status FOREIGN KEY (study_status_id) REFERENCES `study_status`(id);