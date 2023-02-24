package ch.seiberte.tournamentParser.data;

public abstract class BaseTournament {
    private final Long id;
    private String bezeichnung;

    public BaseTournament(Long id, String bezeichnung) {
        this.id = id;
        this.bezeichnung = bezeichnung;
    }

    public BaseTournament() {
        this.id = 0L;
        this.bezeichnung="";
    }

    public Long getId() {
        return id;
    }

    public String getBezeichnung() {
        return bezeichnung;
    }

    public void setBezeichnung(String bezeichnung) {
        this.bezeichnung = bezeichnung;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof BaseTournament)
            return id.equals(((BaseTournament) obj).getId()) && bezeichnung.equals(((BaseTournament) obj).getBezeichnung());
        return false;
    }
}
