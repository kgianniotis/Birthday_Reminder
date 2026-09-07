import java.util.Properties;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class EmailService {

    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";

    private static final String USERNAME = "username@gmail.com";
    // this is created and stored through Windows Powershell
    private static final String APP_PASSWORD =
        System.getenv("BIRTHDAY_EMAIL_APP_PASSWORD");
    

    private static final String RECIPIENT = "username@gmail.com";

    public static void sendBirthdayEmail(String name) {

        Properties props = new Properties();

        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);

        Session session = Session.getInstance(
                props,
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(
                                USERNAME,
                                APP_PASSWORD
                        );
                    }
                }
        );

        try {

            Message message = new MimeMessage(session);

            message.setFrom(new InternetAddress(USERNAME));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(RECIPIENT)
            );

            message.setSubject(
                    "Birthday Reminder: " + name
            );

            message.setText(
                    "Today is " + name + "'s birthday!"
            );

            Transport.send(message);

            System.out.println(
                    "Email sent successfully for " + name
            );

        } catch (MessagingException e) {
            System.err.println(
                    "Failed to send email for " + name
            );

            e.printStackTrace();
        }
    }
}