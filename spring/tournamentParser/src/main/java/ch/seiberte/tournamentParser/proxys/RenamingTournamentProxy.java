package ch.seiberte.tournamentParser.proxys;

import ch.seiberte.tournamentParser.ITournamentReader;
import ch.seiberte.tournamentParser.data.LongTournament;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RenamingTournamentProxy extends TournamentProxy implements ITournamentReader {

    private Logger logger = LoggerFactory.getLogger(RenamingTournamentProxy.class);

    private boolean isLateinStandardOrKombi(String str){
        return str.matches("Latein.*")||str.matches("Standard.*")||str.matches("Kombi.*");
    }

    private String reformatLM(String bezeichnung){
        List<String> teile = new ArrayList<>(Arrays.asList(bezeichnung.split(" ",3)));

        teile.remove("Landesmeisterschaft");

        if(isLateinStandardOrKombi(teile.get(0)))
            Collections.swap(teile,0,1);

        if(teile.get(0).matches("[Ww]ien.*"))
            teile.set(0,"Wien");
        if(teile.get(0).matches("[Bb]urg.*"))
            teile.set(0,"Burgenland");
        if(teile.get(0).matches("[Kk]ärnt.*"))
            teile.set(0,"Kärnten");
        if(teile.get(0).matches("[N]ieder.*"))
            teile.set(0,"Niederösterreich");
        if(teile.get(0).matches("[Oo]ber.*"))
            teile.set(0,"Oberösterreich");
        if(teile.get(0).matches("[Ss]alz.*"))
            teile.set(0,"Salzburg");
        if(teile.get(0).matches("[Ss]tei.*"))
            teile.set(0,"Steiermark");
        if(teile.get(0).matches("[Tt]irol.*"))
            teile.set(0,"Tirol");
        if(teile.get(0).matches("[Vv]ora.*"))
            teile.set(0,"Vorarlberg");

        if(teile.get(1).matches("Kombi.*"))
            teile.set(1,"Kombi");

        teile.add(0,"LM");

        logger.debug(String.join(" ",teile));

        return String.join(" ",teile);
    }

    private String reformatOeM(String bezeichnung){
        List<String> teile = new ArrayList<>(Arrays.asList(bezeichnung.split(" ",3)));
        String neueBezeichnung = "ÖM" + teile.get(teile.size()-1);
        return neueBezeichnung;
    }

    private String reformatStaats(String bezeichnung){
        if(bezeichnung.matches(".*Staatsmeisterschaft(en)? (Latein|Standard|Kombi.*)")){
            String neuebezeichnung = "Staats ";
            if(bezeichnung.matches(".*Latein.*"))
                neuebezeichnung=neuebezeichnung+"Latein";
            if(bezeichnung.matches(".*Standard.*"))
                neuebezeichnung=neuebezeichnung+"Standard";
            if(bezeichnung.matches(".*Kombi.*"))
                neuebezeichnung=neuebezeichnung+"Kombi";

            return neuebezeichnung;
        }

        return bezeichnung;
    }

    @Override
    protected void addTournamentToCache(Long id){
        LongTournament newTournament = baseService.readTournament(id);

        if(newTournament.getBezeichnung().matches(".*Landesmeisterschaft.*")){
            logger.info("Changing Tournament Name for LT with id:" + id +" (LM)");
            String neueBezeichnung = reformatLM(newTournament.getBezeichnung());
            newTournament.setBezeichnung(neueBezeichnung);
        }

        if(newTournament.getBezeichnung().matches(".*Österreichische Meisterschaft.*")){
            logger.info("Changing Tournament Name for LT with id:" + id +" (ÖM)");
            String neueBezeichnung = reformatOeM(newTournament.getBezeichnung());
            newTournament.setBezeichnung(neueBezeichnung);
        }

        if(newTournament.getBezeichnung().matches(".*Staatsmeisterschaft.*")){
            logger.info("Changing Tournament Name for LT with id:" + id +" (Staats)");
            String neueBezeichnung = reformatStaats(newTournament.getBezeichnung());
            newTournament.setBezeichnung(neueBezeichnung);
        }

        tournamentCache.put(newTournament.getId(), newTournament);
    }
}
