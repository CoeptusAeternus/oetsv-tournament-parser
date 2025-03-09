from pydantic import BaseModel

from .tournamet_models import ShortTournament


class Mail(BaseModel):
    
    tournament: ShortTournament
    
    def __init__(self, tournament: ShortTournament):
        self.tournament = tournament
    
    def format_datetime(self, dt) -> str:
        return dt.strftime("%d.%m.%Y")
    
    def format_for_sending(self, recipients):
        if type(self) is Mail:
            raise NotImplementedError("Mail is an abstract class")

        subject = self.subject.format(self.tournament.bezeichnung)
        body = self.body.format(self.tournament.bezeichnung, self.format_datetime(self.tournament.start))

        return subject, body, self.sender

class NewTournamentMail(Mail):
    
    subject = "Neues Turnier im ÖTSV-Kalender gefunden: {}"
    body = \
    """
    Informationen:
    Bezeichnung: {}
    Datum: {}
    Kalender: https://www.tanzsportverband.at/kalender/
    """
    sender = "Neues Turnier Mitteilung"
    
    def __init__(self, tournament: ShortTournament):
        super().__init__tournament

class NennschlussMail(Mail):
    
    subject = "Nennschluss für Turnier {}"
    body = \
    """
    Der Nennschluss für das Turnier {} am {} ist vorbei.
    https://nennungen.schwarzgold.at
    """
    sender = "Nennschluss Erinnerung"
    
    def __init__(self, tournament: ShortTournament):
        super().__init__(tournament)