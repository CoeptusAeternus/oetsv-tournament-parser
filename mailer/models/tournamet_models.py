import datetime
from pydantic import BaseModel

class ShortTournament(BaseModel):
    id: int
    bezeichnung: str
    start: datetime.datetime
    