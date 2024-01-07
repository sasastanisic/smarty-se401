CREATE TABLE IF NOT EXISTS `activity` (
    id BIGINT NOT NULL AUTO_INCREMENT,
    activity_name VARCHAR(100) NOT NULL,
    points DOUBLE NOT NULL,
    comment VARCHAR(500) DEFAULT NULL,
    task_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_task FOREIGN KEY (task_id) REFERENCES `task`(id),
    CONSTRAINT fk_student FOREIGN KEY (student_id) REFERENCES `student`(id)
);