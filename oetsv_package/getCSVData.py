import requests
import pandas as pd

pd.set_option('display.max_colwidth', None)

def getCSVData(id):

    if requests.get("https://www.tanzsportverband.at/modules/ext-data/turniere/oetsv_turniere_"+id+".csv").status_code == 200:
        df=pd.read_csv("https://www.tanzsportverband.at/modules/ext-data/turniere/oetsv_turniere_"+id+".csv",sep=';',encoding='iso-8859-1')

        bezeichnung=df.get('nr').where(df.get('nr').str.find('TURNIERBEZEICHNUNG')==0).dropna().to_string().split('=')[1]
        datum=df.get('nr').where(df.get('nr').str.find('DATUM')==0).dropna().to_string().split('=')[1]
        kl=df.get('bezeichnung').where(df.get('dsart').str.find('TKL')==0).dropna().values.tolist()
        klassen=[]
        for k in kl:
            klassen.append(k[1:])

        ret_dict = {
            'id':id,
            'bezeichnung': bezeichnung,
            'datum':datum,
            'klassen':klassen,
        }

        return ret_dict
    
    return 404
