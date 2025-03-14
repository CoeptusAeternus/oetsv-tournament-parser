package ch.seiberte.tournamentParser.data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LongTournament extends BaseTournament{

    private String adresse;
    private LocalDateTime start;
    private String nenngeld;
    private List<String> klassen;

    public LongTournament(String adresse, String bezeichnung, LocalDateTime start, Long id, String nenngeld, List<String> klassen) {
        super(id,bezeichnung);
        this.adresse = adresse;
        this.start = start;
        this.nenngeld=nenngeld;
        this.klassen = klassen;
    }

    public LongTournament() {
        super();
        this.adresse = "";
        this.start = LocalDateTime.MIN;
        this.nenngeld="";
        this.klassen = new ArrayList<>();
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public List<String> getKlassen() {
        return klassen;
    }

    public void setKlassen(List<String> klassen) {
        this.klassen = klassen;
    }

    public String getNenngeld() {
        return nenngeld;
    }

    public void setNenngeld(String nenngeld) {
        this.nenngeld = nenngeld;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof LongTournament)
            return super.equals(obj)&&
                    adresse.equals(((LongTournament) obj).getAdresse())&&
                    start.equals(((LongTournament) obj).getStart())&&
                    nenngeld.equals(((LongTournament) obj).getNenngeld())&&
                    klassen.equals(((LongTournament) obj).getKlassen());
        return false;
    }

    @Override
    public String toString(){
        return "id: "+getId()+
                ";bez: "+getBezeichnung()+
                ";adr: "+getAdresse()+
                ";start: "+getStart().toString()+
                ";nenngeld: "+getNenngeld()+
                ";klassen: "+getKlassen();
    }
}