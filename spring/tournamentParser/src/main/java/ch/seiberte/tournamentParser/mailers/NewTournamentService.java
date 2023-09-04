package ch.seiberte.tournamentParser.mailers;

import ch.seiberte.tournamentParser.data.ShortTournament;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class NewTournamentService implements ITournamentMailer{

    private static final Logger logger = LoggerFactory.getLogger(NewTournamentService.class);

    final IMailerService mailerService;
    public NewTournamentService() {
        this.mailerService = new MailerService();
    }

    @Override
    public void sendMail(ShortTournament st, String email) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d.MM.yyyy",new Locale("de"));

        String subject = "Neues Turnier im ÖTSV-Kalender gefunden: " + st.getBezeichnung();
        String text = "Informationen:\nBezeichnung: "+st.getBezeichnung()+"\nDatum: "+st.getStart().format(formatter)+"\nKalender: https://www.tanzsportverband.at/kalender/";

        logger.debug("Sending mail with Subject: "+ subject+"\nText: "+text+"\nto: "+email);

        String senderName = "Neues Turnier Mitteilung <noreply@seiberte.ch>";
        mailerService.sendMail(senderName, email,text,subject);

    }
}
