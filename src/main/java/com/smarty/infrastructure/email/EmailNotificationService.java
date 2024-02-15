package com.smarty.infrastructure.email;

import org.springframework.stereotype.Service;

@Service
public class EmailNotificationService {

    private final EmailSender emailSender;

    public EmailNotificationService(EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void sendActivityNotification(String studentEmail, String activity, String student, double points, String course) {
        String message = """
                Respected %s,
                     
                %.2f points is stored into the system for task %s from course %s.
                                        
                This message is automatic, so please don't reply to it.
                Smarty, ITM""".formatted(student, points, activity, course);

        emailSender.sendEmail(studentEmail, activity, message);
    }

    public void sendExamNotification(String studentEmail, String courseCode, String course, String student, double points, int grade) {
        String subjectMessage = "Grade entered for course %s - %s".formatted(courseCode, course);

        String message = """
                Respected %s,
                                
                You scored %.2f points on the exam from course %s - %s and got a grade of %d.
                                
                This message is automatic, so please don't reply to it.
                Smarty, ITM""".formatted(student, points, courseCode, course, grade);

        emailSender.sendEmail(studentEmail, subjectMessage, message);
    }

    public void sendConfirmation(String email, String name) {
        String subjectMessage = "Account created successfully";

        String message = """
                Respected %s,
                                
                Your account has been created successfully.
                                
                This message is automatic, so please don't reply to it.
                Smarty, ITM""".formatted(name);

        emailSender.sendEmail(email, subjectMessage, message);
    }

}
