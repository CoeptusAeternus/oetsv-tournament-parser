package ch.seiberte.tournamentParser.data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Detailed tournament representation including address, start time, fee and classes")
public class LongTournament extends BaseTournament {

    @Schema(description = "Address of the tournament venue", example = "Mainstreet 1, 8000 Zurich")
    private String address;

    @Schema(description = "Tournament start date and time", example = "2026-06-12T09:30:00")
    private LocalDateTime start;

    @Schema(description = "Entry fee (human readable, no defined schema)", example = "30,-; 30,00; 30")
    private String fee;

    @Schema(description = "List of competition classes or categories", example = "[\"U10\", \"U12\"]")
    private List<String> classes;

    public LongTournament(String address, String name, LocalDateTime start, Long id, String fee,
            List<String> classes) {
        super(id, name);
        this.address = address;
        this.start = start;
        this.fee = fee;
        this.classes = classes;
    }

    public LongTournament() {
        super();
        this.address = "";
        this.start = LocalDateTime.MIN;
        this.fee = "";
        this.classes = new ArrayList<>();
    }

    @JsonProperty("address")
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @JsonProperty("start")
    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    @JsonProperty("classes")
    public List<String> getClasses() {
        return classes;
    }

    public void setClasses(List<String> classes) {
        this.classes = classes;
    }

    @JsonProperty("fee")
    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    @Override
    public int hashCode() {
        return this.getId().hashCode() * start.hashCode() * this.getName().hashCode()
                * this.getAddress().hashCode() * this.getFee().hashCode() * this.getClasses().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof LongTournament)
            return super.equals(obj) &&
                    address.equals(((LongTournament) obj).getAddress()) &&
                    start.equals(((LongTournament) obj).getStart()) &&
                    fee.equals(((LongTournament) obj).getFee()) &&
                    classes.equals(((LongTournament) obj).getClasses());
        return false;
    }

    @Override
    public String toString() {
        return "id: " + getId() +
                ";name: " + getName() +
                ";address: " + getAddress() +
                ";start: " + getStart().toString() +
                ";fee: " + getFee() +
                ";classes: " + getClasses();
    }
}