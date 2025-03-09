from pydantic import BaseModel

from .tournamet_models import ShortTournament

class PikaMessage(BaseModel):
    type: str
    tournament: ShortTournament
    
    def __init__(self, type: str, tournament: ShortTournament):
        self.type = type
        self.tournament = tournament