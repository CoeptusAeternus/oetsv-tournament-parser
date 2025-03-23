package ch.seiberte.tournamentParser.data;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ShortTournament extends BaseTournament{
    private LocalDateTime start; //start not in DateTime due to changes after rollout

    public ShortTournament(Long id, String bezeichnung, LocalDate start) {
        super(id, bezeichnung);
        this.start = start.atTime(0,0);
    }

    public ShortTournament() {
        super();
        this.start=LocalDateTime.MIN;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    @Override
    public int hashCode() {
        return this.getId().hashCode() * start.hashCode() * this.getBezeichnung().hashCode();
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
