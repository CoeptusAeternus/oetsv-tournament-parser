package ch.seiberte.tournamentParser;

import ch.seiberte.tournamentParser.data.ShortTournament;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;


import java.util.Properties;

public class ShortMailerService implements ITournamentMailer{

    private String sender;
    private Properties props;

    public ShortMailerService() {
        this.sender = "noreply@seiberte.ch";;
        this.props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "mail.seiberte.ch");
        props.put("mail.smtp.port", "587");

    }

    @Override
    public void sendMail(ShortTournament st, String email) {

        String subject = "Neues Turnier im ÖTSV-Kalender gefunden: " + st.getBezeichnung();
        String text = "Informationen:\nBezeichnung: "+st.getBezeichnung()+"\n Auschreibgung unter:\nhttps://www.tanzsportverband.at/portal/ausschreibung/ausschreibung_drucken.php?TKNr="+st.getId()+"&art=IN&conf_html=1";
        Authenticator authenticator = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(sender, "***REMOVED***");
            }
        };
        Session session = Session.getInstance(props, authenticator);

        try {
            Message m = new MimeMessage(session);

            m.setFrom(new InternetAddress(sender));
            m.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            m.setSubject(subject);
            m.setText(text);

            Transport.send(m);


        }catch (MessagingException e){
            throw new RuntimeException(e);
        }

    }
}
