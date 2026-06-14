package sk.adamkatrenic.bankingapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Async
    public void sendTransactionNotification(String to, String type,
                                            BigDecimal amount, BigDecimal newBalance) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Banking App — Transaction Notification");
        message.setText(buildMessage(type, amount, newBalance));
        mailSender.send(message);
    }

    private String buildMessage(String type, BigDecimal amount, BigDecimal newBalance) {
        return String.format("""
                Hello,
                
                A transaction has been processed on your account:
                
                Type: %s
                Amount: €%.2f
                New Balance: €%.2f
                
                If you did not authorize this transaction, please contact us immediately.
                
                Banking App Team
                """, type, amount, newBalance);
    }
}