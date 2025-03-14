package ch.seiberte.tournamentParser.mailers;

import ch.seiberte.tournamentParser.data.ShortTournament;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class NennschlussReminderService implements ITournamentMailer{

    private static final Logger logger = LoggerFactory.getLogger(NennschlussReminderService.class);

    private final IMailerService mailerService;

    public NennschlussReminderService() {
        this.mailerService = new MailerService();
    }

    @Override
    public void sendMail(ShortTournament st, String email) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d.MMMM.yyyy",new Locale("de"));

        LocalDateTime ldt = st.getStart();
        String subject  = "Nennschluss für Turnier "+ st.getBezeichnung()+" vorbei";
        String text = "Der Nennschluss für das Turnier " + st.getBezeichnung()+" am "+ ldt.format(formatter) +" ist vorbei.\n https://nennungen.schwarzgold.at/";

        logger.debug("Sending mail with Subject: "+ subject+"\nText: "+text+"\nto: "+email);

        String senderName = "Nennschluss Erinnerung";
        mailerService.sendMail(senderName, email,text,subject);
    }
}
