CREATE TABLE IF NOT EXISTS `task` (
    id BIGINT NOT NULL AUTO_INCREMENT,
    type VARCHAR(50) NOT NULL,
    max_points DOUBLE NOT NULL,
    number_of_tasks INT NOT NULL,
    course_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_course2 FOREIGN KEY (course_id) REFERENCES `course`(id)
);