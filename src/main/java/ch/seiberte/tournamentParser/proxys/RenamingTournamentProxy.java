package ch.seiberte.tournamentParser.proxys;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.seiberte.tournamentParser.ITournamentReader;
import ch.seiberte.tournamentParser.data.LongTournament;

public class RenamingTournamentProxy extends TournamentProxy implements ITournamentReader {

    private static final Logger logger = LoggerFactory.getLogger(RenamingTournamentProxy.class);

    private boolean isLateinStandardOrKombi(String str){
        return str.matches("Latein.*")||str.matches("Standard.*")||str.matches("Kombi.*");
    }

    private String reformatLM(String name){
        List<String> teile = new ArrayList<>(Arrays.asList(name.split(" ",3)));

        teile.remove("Landesmeisterschaft");

        if(teile.size()<2)
            return name;
        
        if(isLateinStandardOrKombi(teile.getFirst()))
            Collections.swap(teile,0,1);

        if(teile.getFirst().matches("[Ww]ien.*"))
            teile.set(0,"Wien");
        if(teile.getFirst().matches("[Bb]urgen.*"))
            teile.set(0,"Burgenland");
        if(teile.getFirst().matches("[Kk]ärnt.*"))
            teile.set(0,"Kärnten");
        if(teile.getFirst().matches("([Nn]ieder.*|N[Öö])"))
            teile.set(0,"Niederösterreich");
        if(teile.getFirst().matches("([Oo]ber.*|O[Öö])"))
            teile.set(0,"Oberösterreich");
        if(teile.getFirst().matches("[Ss]alz.*"))
            teile.set(0,"Salzburg");
        if(teile.getFirst().matches("[Ss]tei.*"))
            teile.set(0,"Steiermark");
        if(teile.getFirst().matches("[Tt]irol.*"))
            teile.set(0,"Tirol");
        if(teile.getFirst().matches("[Vv]ora.*"))
            teile.set(0,"Vorarlberg");

        if(teile.get(1).matches("Kombi.*"))
            teile.set(1,"Kombi");

        teile.addFirst("LM");

        logger.debug(String.join(" ",teile));

        return String.join(" ",teile);
    }

    private String reformatSeniorenLM(String name){

        if(name.matches(".*[Ww]ien.*"))
            return "LM Wien Senioren";
        if(name.matches(".*[Bb]urgen.*"))
            return "LM Burgenland Senioren";
        if(name.matches(".*[Kk]ärnt.*"))
            return "LM Kärnten Senioren";
        if(name.matches("([Nn]ieder.*|.*N[Öö].*)"))
            return "LM Niederösterreich Senioren";
        if(name.matches("([Oo]ber.*|.*O[Öö].*)"))
            return "LM Oberösterreich Senioren";
        if(name.matches(".*[Ss]alz.*"))
            return "LM Salzburg Senioren";
        if(name.matches(".*[Ss]tei.*"))
            return "LM Steiermark Senioren";
        if(name.matches(".*[Tt]irol.*"))
            return "LM Tirol Senioren";
        if(name.matches(".*[Vv]ora.*"))
            return "LM Vorarlberg Senioren";


        return name;
    }

    private String reformatOeM(String name){
        List<String> teile = new ArrayList<>(Arrays.asList(name.split(" ",3)));
        return "ÖM" + teile.getLast();
    }

    private String reformatStaats(String name){
        if(name.matches(".*Staatsmeisterschaft(en)? (Latein|Standard|Kombi.*)")){
            String newName = "Staats ";
            if(name.matches(".*Latein.*"))
                newName=newName+"Latein";
            if(name.matches(".*Standard.*"))
                newName=newName+"Standard";
            if(name.matches(".*Kombi.*"))
                newName=newName+"Kombi";

            return newName;
        }

        return name;
    }

    @Override
    protected void addTournamentToCache(Long id){
        LongTournament newTournament = baseService.readTournament(id);

        if(newTournament.getName().matches(".*Landesmeisterschaft.*")){
            logger.info("Changing Tournament Name for LT with id:{} (LM)", id);
            String newName = reformatLM(newTournament.getName());
            newTournament.setName(newName);
        }

        if(newTournament.getName().matches(".*Österreichische Meisterschaft.*")){
            logger.info("Changing Tournament Name for LT with id:{} (ÖM)", id);
            String newName = reformatOeM(newTournament.getName());
            newTournament.setName(newName);
        }

        if(newTournament.getName().matches(".*Staatsmeisterschaft.*")){
            logger.info("Changing Tournament Name for LT with id:{} (Staats)", id);
            String newName = reformatStaats(newTournament.getName());
            newTournament.setName(newName);
        }

        if(newTournament.getName().matches(".*Meisterschaft.*Senioren.*")){
            logger.info("Changing Tournament Name for LT with id:{} (LM Senioren)", id);
            String newName = reformatSeniorenLM(newTournament.getName());
            newTournament.setName(newName);
        }

        tournamentCache.put(newTournament.getId(), newTournament);
    }
}
