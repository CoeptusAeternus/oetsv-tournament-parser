package ch.seiberte.tournamentParser;

import org.junit.jupiter.api.Test;

public class ListParserTest {

    @Test
    public void test(){
        IKalenderReader kr = new OetsvCalendarDataParser();

        System.out.println(kr.getTournaments());
    }

    @Test
    public void test2(){
        ITournamentReader tr = new OetsvTournamentDataParser();

        System.out.println(tr.readTournament(1531L));
    }
}
