package ch.seiberte.tournamentParser.data;

import java.time.LocalDate;

public class ShortTournament extends BaseTournament{
    private LocalDate start;

    public ShortTournament(Long id, String bezeichnung, LocalDate start) {
        super(id, bezeichnung);
        this.start = start;
    }

    public ShortTournament() {
        super();
        this.start=LocalDate.MIN;
    }

    public LocalDate getStart() {
        return start;
    }

    public void setStart(LocalDate start) {
        this.start = start;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof ShortTournament)
            return super.equals(obj) && start.equals(((ShortTournament) obj).getStart());
        return false;
    }

    @Override
    public String toString(){
        return "id: " + getId().toString()+", bez: "+getBezeichnung()+", start: "+getStart().toString();
    }
}
