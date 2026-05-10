package ch.seiberte.tournamentParser.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Common tournament properties shared by short and long tournament representations")
public abstract class BaseTournament {
    private final Long id;
    private String name;

    public BaseTournament(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public BaseTournament() {
        this.id = 0L;
        this.name = "";
    }

    @Schema(description = "Unique tournament identifier", example = "1500")
    public Long getId() {
        return id;
    }

    @JsonProperty("name")
    @Schema(name = "name", description = "Tournament name", example = "Staatsmeisterschaften")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BaseTournament)
            return id.equals(((BaseTournament) obj).getId())
                    && name.equals(((BaseTournament) obj).getName());
        return false;
    }
}
