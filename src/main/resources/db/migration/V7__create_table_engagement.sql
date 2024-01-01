CREATE TABLE IF NOT EXISTS `engagement` (
    id BIGINT NOT NULL AUTO_INCREMENT,
    professor_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_professor FOREIGN KEY (professor_id) REFERENCES `professor`(id),
    CONSTRAINT fk_course FOREIGN KEY (course_id) REFERENCES `course`(id)
);