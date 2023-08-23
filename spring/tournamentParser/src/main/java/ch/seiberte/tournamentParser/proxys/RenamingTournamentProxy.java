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
        if(teile.get(0).matches("[Bb]urge.*"))
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

    private String reformatSeniorenLM(String bezeichnung){

        if(bezeichnung.matches(".*[Ww]ien.*"))
            return "LM Wien Senioren";
        if(bezeichnung.matches(".*[Bb]urge.*"))
            return "LM Burgenland Senioren";
        if(bezeichnung.matches(".*[Kk]ärnt.*"))
            return "LM Kärnten Senioren";
        if(bezeichnung.matches(".*[N]ieder.*"))
            return "LM Niederösterreich Senioren";
        if(bezeichnung.matches(".*[Oo]ber.*"))
            return "LM Oberösterreich Senioren";
        if(bezeichnung.matches(".*[Ss]alz.*"))
            return "LM Salzburg Senioren";
        if(bezeichnung.matches(".*[Ss]tei.*"))
            return "LM Steiermark Senioren";
        if(bezeichnung.matches(".*[Tt]irol.*"))
            return "LM Tirol Senioren";
        if(bezeichnung.matches(".*[Vv]ora.*"))
            return "LM Vorarlberg Senioren";


        return bezeichnung;
    }

    private String reformatOeM(String bezeichnung){
        List<String> teile = new ArrayList<>(Arrays.asList(bezeichnung.split(" ",3)));
        return "ÖM" + teile.get(teile.size()-1);
    }

    private String reformatStaats(String bezeichnung){
        if(bezeichnung.matches(".*Staatsmeisterschaft(en)? (Latein|Standard|Kombi.*)")){
            String neueBezeichnung = "Staats ";
            if(bezeichnung.matches(".*Latein.*"))
                neueBezeichnung=neueBezeichnung+"Latein";
            if(bezeichnung.matches(".*Standard.*"))
                neueBezeichnung=neueBezeichnung+"Standard";
            if(bezeichnung.matches(".*Kombi.*"))
                neueBezeichnung=neueBezeichnung+"Kombi";

            return neueBezeichnung;
        }

        return bezeichnung;
    }

    @Override
    protected void addTournamentToCache(Long id){
        LongTournament newTournament = baseService.readTournament(id);

        if(newTournament.getBezeichnung().matches("(.*Landesmeisterschaft.*|.*Meisterschaft Senioren.*)")){
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

        if(newTournament.getBezeichnung().matches(".*Meisterschaft Senioren.*")){
            logger.info("Changing Tournament Name for LT with id:" + id +" (LM Senioren)");
            String neueBezeichnung = reformatSeniorenLM(newTournament.getBezeichnung());
            newTournament.setBezeichnung(neueBezeichnung);
        }

        tournamentCache.put(newTournament.getId(), newTournament);
    }
}
