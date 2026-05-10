package ch.seiberte.tournamentParser.data;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Short tournament representation with basic metadata and start date")
public class ShortTournament extends BaseTournament {
    @JsonProperty("start")
    @Schema(description = "Tournament start date and time", example = "2026-06-12T00:00:00")
    private LocalDateTime start; // start not in DateTime due to changes after rollout

    public ShortTournament(Long id, String name, LocalDate start) {
        super(id, name);
        this.start = start.atTime(0, 0);
    }

    public ShortTournament() {
        super();
        this.start = LocalDateTime.MIN;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    @Override
    public int hashCode() {
        return this.getId().hashCode() * start.hashCode() * this.getName().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ShortTournament)
            return super.equals(obj) && start.equals(((ShortTournament) obj).getStart());
        return false;
    }

    @Override
    public String toString() {
        return "id: " + getId().toString() + ", name: " + getName() + ", start: " + getStart().toString();
    }
}
