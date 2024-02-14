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

}
