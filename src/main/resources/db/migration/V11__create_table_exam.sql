CREATE TABLE IF NOT EXISTS `exam` (
    id BIGINT NOT NULL AUTO_INCREMENT,
    examination_period VARCHAR(50) NOT NULL,
    grade INT NOT NULL,
    points DOUBLE NOT NULL,
    date_of_examination DATE NOT NULL,
    comment VARCHAR(500) DEFAULT NULL,
    total_points DOUBLE NOT NULL,
    student_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_student2 FOREIGN KEY (student_id) REFERENCES `student`(id),
    CONSTRAINT fk_course3 FOREIGN KEY (course_id) REFERENCES `course`(id)
);