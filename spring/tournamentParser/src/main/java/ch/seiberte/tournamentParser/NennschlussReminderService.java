package ch.seiberte.tournamentParser;

import ch.seiberte.tournamentParser.data.ShortTournament;

import java.time.LocalDateTime;

public class NennschlussReminderService implements ITournamentMailer{

    private IMailerService mailerService;

    public NennschlussReminderService() {
        this.mailerService = new MailerService();
    }

    @Override
    public void sendMail(ShortTournament st, String email) {

        LocalDateTime ldt = st.getStart();
        String tournamentDate = ldt.getDayOfMonth() +"."+ldt.getMonth().toString()+"."+ldt.getYear();
        String subject  = "Nennschluss für Turnier "+ st.getBezeichnung()+"vorbei";
        String text = "Der Nennschluss für das Turnier " + st.getBezeichnung()+" am "+ tournamentDate +" ist vorbei.\n https://nennungen.schwarzgold.at/";

        mailerService.sendMail(email,text,subject);
    }
}
