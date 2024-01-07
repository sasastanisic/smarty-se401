ALTER TABLE `account`
ADD CONSTRAINT chk_role CHECK (role IN ('STUDENT', 'ASSISTANT', 'PROFESSOR', 'ADMIN'));

ALTER TABLE `task`
ADD CONSTRAINT chk_type CHECK (type IN ('HOMEWORK', 'PROJECT', 'TEST', 'ENGAGEMENT'));