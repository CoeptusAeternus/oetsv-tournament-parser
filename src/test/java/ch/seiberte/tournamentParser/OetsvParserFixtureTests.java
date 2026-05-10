package ch.seiberte.tournamentParser;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;

import ch.seiberte.tournamentParser.data.LongTournament;
import ch.seiberte.tournamentParser.data.ShortTournament;
import ch.seiberte.tournamentParser.exceptions.UnableToReadDataException;

class OetsvParserFixtureTests {

    @Test
    void parsesTournamentDetailFixture() throws IOException, UnableToReadDataException {
        Document document = Jsoup.parse(Files.readString(
                Path.of("src", "test", "resources", "fixtures", "sample_detail_pages", "tournament_1767.html")));

        OetsvTournamentDataParser parser = new OetsvTournamentDataParser();
        LongTournament tournament = parser.parseTournamentFromDoc(document, 1767L);

        assertThat(tournament.getId()).isEqualTo(1767L);
        assertThat(tournament.getName()).isEqualTo("Steirische Landesmeisterschaft Standard 2026");
        assertThat(tournament.getAddress()).isEqualTo("Strassengler Halle, 8111 Judendorf-Straßengel, Hauptplatz 2");
        assertThat(tournament.getStart()).hasToString("2026-05-16T16:00");
        assertThat(tournament.getFee()).isEqualTo("30,-");
        assertThat(tournament.getClasses()).containsExactly(
                "Allg.Kl. Sta D",
                "Allg.Kl. Sta C",
                "Allg.Kl. Sta B",
                "Allg.Kl. Sta A",
                "Allg.Kl. Sta S",
                "Allg.Kl. Solo Sta SE",
                "Allg.Kl. Solo Sta SF");
    }

    @Test
    void parsesCalendarFixtureIntoShortTournaments() throws IOException, UnableToReadDataException {
        Document document = Jsoup
                .parse(Files.readString(Path.of("src", "test", "resources", "fixtures", "calendar.html")));

        OetsvCalendarDataParser parser = new OetsvCalendarDataParser();
        List<ShortTournament> tournaments = List.copyOf(parser.parseTournamentsFromDoc(document));

        ShortTournament steiermarkLm = tournaments.stream()
                .filter(tournament -> tournament.getId().equals(1767L))
                .findFirst()
                .orElseThrow();
        ShortTournament steiermarkKombi = tournaments.stream()
                .filter(tournament -> tournament.getId().equals(1765L))
                .findFirst()
                .orElseThrow();

        assertThat(tournaments).hasSizeGreaterThan(8);
        assertThat(steiermarkLm.getName()).isEqualTo("Landesmeisterschaft Standard Steiermark");
        assertThat(steiermarkLm.getStart()).hasToString("2026-05-16T00:00");
        assertThat(steiermarkKombi.getName()).isEqualTo("Ö-Cup - Landesmeisterschaft Kombination Steiermark");
        assertThat(steiermarkKombi.getStart()).hasToString("2026-05-17T00:00");
    }
}