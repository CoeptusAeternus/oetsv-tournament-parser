package ch.seiberte.tournamentParser;

import ch.seiberte.tournamentParser.data.ShortTournament;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

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
        mailer.sendMail(st, "sportwart@schwarzgold.at");
    }

    @Test
    public void nennschlussTestMitDaten(){
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String jsonString = "[{\"id\":1557,\"bezeichnung\":\"Landesmeisterschaft Standard Tirol\",\"start\":\"2023-09-03T00:00:00\"},{\"id\":1555,\"bezeichnung\":\"Landesmeisterschaft Latein Tirol\",\"start\":\"2023-09-02T00:00:00\"},{\"id\":1556,\"bezeichnung\":\"Tiroler Meisterschaft Schüler, Junioren Jugend\",\"start\":\"2023-09-04T00:00:00\"}]";
        List<ShortTournament> stList = null;
        try {
            stList = mapper.readValue(jsonString, new TypeReference<List<ShortTournament>>(){});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        System.out.println(stList);

        ITournamentMailer nennschlussReminder = new NennschlussReminderService();



        LocalDateTime now = LocalDateTime.now();
        now = now.withSecond(0).withMinute(0).withHour(0).truncatedTo(ChronoUnit.SECONDS);
        for(ShortTournament st : stList) {
            LocalDateTime nennschlussReminderDate = st.getStart();
            nennschlussReminderDate = nennschlussReminderDate.minusDays(13);
            System.out.println(nennschlussReminderDate.equals(now));
            if (nennschlussReminderDate.equals(now)) {
                nennschlussReminder.sendMail(st, "jaksei.lol@gmail.com");
            }
        }
    }
}
