package ch.seiberte.tournamentParser;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class DateTimeTest {

    @Test
    public void test(){
        LocalDateTime now = LocalDateTime.now();
        now = now.withHour(0);
        now = now.withMinute(0);
        now = now.withSecond(0);
        now = now.truncatedTo(ChronoUnit.SECONDS);
        LocalDateTime dt = LocalDateTime.parse("2023-09-10T00:00:00");
        System.out.println(dt);
        System.out.println(dt.minusDays(13));
        System.out.println(now);
        LocalDateTime dt2 = LocalDateTime.parse("2023-09-03T00:00:00");
        System.out.println(dt2.minusDays(13));
        System.out.println(dt2.equals(now));
        System.out.println(dt2.minusDays(13).equals(now));
    }

}
