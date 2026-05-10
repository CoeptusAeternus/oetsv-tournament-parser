package ch.seiberte.tournamentParser.proxys;

import ch.seiberte.tournamentParser.ITournamentReader;
import ch.seiberte.tournamentParser.OetsvTournamentDataParser;
import ch.seiberte.tournamentParser.data.LongTournament;

import java.util.HashMap;
import java.util.Map;

public class TournamentProxy implements ITournamentReader {

    protected final Map<Long, LongTournament> tournamentCache;
    protected final ITournamentReader baseService;

    public TournamentProxy() {
        this(new HashMap<>(), new OetsvTournamentDataParser());
    }

    public TournamentProxy(ITournamentReader baseService) {
        this(new HashMap<>(), baseService);
    }

    protected TournamentProxy(Map<Long, LongTournament> tournamentCache, ITournamentReader baseService) {
        this.tournamentCache = tournamentCache;
        this.baseService = baseService;
    }

    protected void addTournamentToCache(Long id){
        LongTournament newTournament = baseService.readTournament(id);
        tournamentCache.put(newTournament.getId(), newTournament);
    }

    @Override
    public LongTournament readTournament(Long id) {
        if(!tournamentCache.containsKey(id))
            addTournamentToCache(id);
        return tournamentCache.get(id);
    }
}
