package ch.seiberte.tournamentParser.proxys;

import ch.seiberte.tournamentParser.IKalenderReader;
import ch.seiberte.tournamentParser.ITournamentReader;
import ch.seiberte.tournamentParser.OetsvTournamentDataParser;
import ch.seiberte.tournamentParser.data.LongTournament;
import ch.seiberte.tournamentParser.data.ShortTournament;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TournamentProxy implements ITournamentReader {

    Map<Long, LongTournament> currentTournaments;
    Map<Long,LongTournament> tournamentsArchive;
    ITournamentReader baseService;

    public TournamentProxy() {
        this.currentTournaments=new HashMap<>();
        this.tournamentsArchive=new HashMap<>();
        this.baseService=new OetsvTournamentDataParser();//TODO
    }

    @Override
    public LongTournament readTournament(Long id) {
        if(currentTournaments.containsKey(id))
            return currentTournaments.get(id);
        if(tournamentsArchive.containsKey(id))
            return tournamentsArchive.get(id);
        return baseService.readTournament(id);
    }

    public void updateCurrentTournaments(Collection<Long> currentIds){
       if(currentTournaments.keySet().equals(currentIds))
           return;

       Set<Long> oldIds = currentTournaments.keySet();
       for(Long id : currentIds){
           if(!currentTournaments.containsKey(id)){
               LongTournament newTournament = baseService.readTournament(id);
               currentTournaments.put(newTournament.getId(), newTournament);
           }
        }
    }

    public void reset(){
        currentTournaments=new HashMap<>();
        tournamentsArchive=new HashMap<>();
    }

    public void resetAndUpdate(Collection<Long> currentIds){
        reset();
        updateCurrentTournaments(currentIds);
    }
}
