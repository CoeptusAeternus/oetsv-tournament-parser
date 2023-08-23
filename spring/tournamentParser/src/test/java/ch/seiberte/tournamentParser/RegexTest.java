package ch.seiberte.tournamentParser;

import org.junit.jupiter.api.Test;

public class RegexTest {


    @Test
    public void LMSenionrenTest(){
        String test = "NÖ Meisterschaft für Senioren";

        System.out.println(test.matches("([Nn]ieder.*|.*N[Öö].*)"));

    }

}
