package ch.seiberte.tournamentParser;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;
public class MailerService implements IMailerService{

    private Properties props;
    private String sender;

    public MailerService() {
        this.sender = "noreply@seiberte.ch";
        this.props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "mail.seiberte.ch");
        props.put("mail.smtp.port", "587");
    }

    @Override
    public void sendMail(String to, String text, String subject) {
        Authenticator authenticator = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(sender, "***REMOVED***");
            }
        };
        Session session = Session.getInstance(props, authenticator);

        try {
            Message m = new MimeMessage(session);

            m.setFrom(new InternetAddress(sender));
            m.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            m.setSubject(subject);
            m.setText(text);

            Transport.send(m);


        }catch (MessagingException e){
            throw new RuntimeException(e);
        }
    }
}
