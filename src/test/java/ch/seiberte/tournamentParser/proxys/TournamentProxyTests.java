package ch.seiberte.tournamentParser.proxys;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import ch.seiberte.tournamentParser.ITournamentReader;
import ch.seiberte.tournamentParser.data.LongTournament;

class TournamentProxyTests {

    @Test
    void cachesTournamentLookups() {
        CountingTournamentReader baseService = new CountingTournamentReader(Map.of(
                1L, new LongTournament("addr", "One", LocalDateTime.of(2026, 1, 1, 12, 0), 1L, "10", List.of("A"))));

        TournamentProxy proxy = new TournamentProxy(baseService);

        LongTournament first = proxy.readTournament(1L);
        LongTournament second = proxy.readTournament(1L);

        assertSame(first, second);
        assertThat(baseService.readCount).isEqualTo(1);
    }

    @Test
    void renamesAndCachesTournamentLookups() {
        CountingTournamentReader baseService = new CountingTournamentReader(Map.of(
                10L,
                new LongTournament("addr", "Landesmeisterschaft Standard Steiermark",
                        LocalDateTime.of(2026, 5, 16, 16, 0), 10L, "30,-", List.of()),
                11L,
                new LongTournament("addr", "Landesmeisterschaft Kombination Niederösterreich",
                        LocalDateTime.of(2026, 5, 17, 16, 0), 11L, "30,-", List.of()),
                12L,
                new LongTournament("addr", "Österreichische Meisterschaft Senioren",
                        LocalDateTime.of(2026, 5, 18, 16, 0), 12L, "30,-", List.of()),
                13L,
                new LongTournament("addr", "Landesmeisterschaft Senioren Tirol", LocalDateTime.of(2026, 5, 19, 16, 0),
                        13L, "30,-", List.of()),
                14L, new LongTournament("addr", "Staatsmeisterschaft Standard", LocalDateTime.of(2026, 5, 20, 16, 0),
                        14L, "30,-", List.of())));

        RenamingTournamentProxy proxy = new RenamingTournamentProxy(baseService);

        assertThat(proxy.readTournament(10L).getName()).isEqualTo("LM Steiermark Standard");
        assertThat(proxy.readTournament(11L).getName()).isEqualTo("LM Niederösterreich Kombi");
        assertThat(proxy.readTournament(12L).getName()).isEqualTo("ÖMSenioren");
        assertThat(proxy.readTournament(13L).getName()).isEqualTo("LM Senioren Tirol");
        assertThat(proxy.readTournament(14L).getName()).isEqualTo("Staats Standard");

        assertThat(baseService.readCount).isEqualTo(5);
        assertThat(proxy.readTournament(10L).getName()).isEqualTo("LM Steiermark Standard");
        assertThat(baseService.readCount).isEqualTo(5);
    }

    private static final class CountingTournamentReader implements ITournamentReader {

        private final Map<Long, LongTournament> tournaments;
        private int readCount;

        private CountingTournamentReader(Map<Long, LongTournament> tournaments) {
            this.tournaments = tournaments;
        }

        @Override
        public LongTournament readTournament(Long id) {
            readCount++;

            LongTournament tournament = tournaments.get(id);
            if (tournament == null) {
                throw new IllegalArgumentException("Missing tournament " + id);
            }

            return new LongTournament(
                    tournament.getAddress(),
                    tournament.getName(),
                    tournament.getStart(),
                    tournament.getId(),
                    tournament.getFee(),
                    tournament.getClasses());
        }
    }
}