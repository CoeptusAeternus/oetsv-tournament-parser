package ch.seiberte.tournamentParser;

import ch.seiberte.tournamentParser.data.ShortTournament;
import org.junit.jupiter.api.Test;

public class MailerTest {

    @Test
    public void nennschlussTest(){
        IKalenderReader kr = new OetsvCalendarDataParser();
        ShortTournament st = kr.getTournaments().get(0);
        ITournamentMailer mailer = new NennschlussReminderService();
        mailer.sendMail(st,"jaksei.lol@gmail.com");
    }
    @Test
    public void neuesTurnierTest(){
        IKalenderReader kr = new OetsvCalendarDataParser();
        ShortTournament st = kr.getTournaments().get(0);
        ITournamentMailer mailer = new NewTournamentService();
        mailer.sendMail(st,"jaksei.lol@gmail.com");
    }
}
